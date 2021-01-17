package com.example.examplemod.entities;

import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;

public class CampfireCartEntity extends AbstractMinecartEntity {
    private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(FurnaceMinecartEntity.class, DataSerializers.BOOLEAN);

    public double pushX = 0;
    public double pushZ = 0;

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    // Taken from FurnaceMinecartEntity

    public CampfireCartEntity(EntityType<?> furnaceCart, World world) {
        super(furnaceCart, world);
    }

    public CampfireCartEntity(EntityType<?> furnaceCart, World worldIn, double x, double y, double z) {
        super(furnaceCart, worldIn, x, y, z);
    }

    public AbstractMinecartEntity.Type getMinecartType() {
        return AbstractMinecartEntity.Type.FURNACE;
    }


    public void tick() {
        super.tick();

        if (Math.abs(this.getMotion().getX()*3) > pushX) {
            pushX = this.getMotion().getX();
        }

        if (Math.abs(this.getMotion().getZ()*3) > pushZ) {
            pushZ = this.getMotion().getZ();
        }

        if (this.isMinecartPowered()){
            Vector3d pos = this.getPositionVec();
            if (rand.nextInt(10) == 0) {
                world.playSound((double)pos.getX(), (double)pos.getY() + 0.4D, (double)pos.getZ(), SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
            }

            if (rand.nextInt(10) == 0) {
                spawnSmokeParticles(world, pos, false, false);
            }
        }


    }

    // Taken from Campfire Block
    public static void spawnSmokeParticles(World worldIn, Vector3d pos, boolean isSignalFire, boolean spawnExtraSmoke) {
        Random random = worldIn.getRandom();
        BasicParticleType basicparticletype = isSignalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        worldIn.addOptionalParticle(basicparticletype, true, (double)pos.getX() + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4 + random.nextDouble(), (double)pos.getZ() + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
        if (spawnExtraSmoke) {
            worldIn.addParticle(ParticleTypes.SMOKE, (double)pos.getX(), (double)pos.getY() + 0.8D, (double)pos.getZ(), 0.0D, 0.005D, 0.0D);
        }

    }

    public void killMinecart(DamageSource source) {
        this.remove();
        if (!source.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.entityDropItem(Blocks.CAMPFIRE);
        }

    }

    protected void moveAlongTrack(BlockPos pos, BlockState state) {
        double d0 = 1.0E-4D;
        double d1 = 0.001D;
        super.moveAlongTrack(pos, state);
        Vector3d vector3d = this.getMotion();
        double d2 = horizontalMag(vector3d);
        double d3 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (isMinecartPowered() && d3 > 1.0E-4D && d2 > 0.001D) {
            double d4 = (double)MathHelper.sqrt(d2);
            double d5 = (double)MathHelper.sqrt(d3);
            this.pushX = vector3d.x / d4 * d5;
            this.pushZ = vector3d.z / d4 * d5;
        }

    }

    protected void applyDrag() {
        double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (isMinecartPowered() && d0 > 1.0E-7D && isMinecartPowered() && !isGoingUphill()) {
            d0 = (double)MathHelper.sqrt(d0);
            this.pushX /= d0;
            this.pushZ /= d0;
            // Four times slower than a furnace cart.
            Vector3d min_motion = this.getMotion().mul(0.8D, 0.0D, 0.8D);
            double speed_coeff = this.getSpeedCoeff();
            double new_x = (Math.abs(this.pushX/speed_coeff) > Math.abs(min_motion.x))? this.pushX/speed_coeff : min_motion.x;
            double new_z = (Math.abs(this.pushZ/speed_coeff) > Math.abs(min_motion.z))? this.pushZ/speed_coeff : min_motion.z;
            this.setMotion(new_x, min_motion.y, new_z);
        } else {
            this.setMotion(this.getMotion().mul(0.8D, 0.0D, 0.8D));
        }

        super.applyDrag();
    }

    public double getSpeedCoeff() {
        return 11;
    }

    public boolean isGoingUphill() {
        int i = MathHelper.floor(this.getPosX());
        int j = MathHelper.floor(this.getPosY());
        int k = MathHelper.floor(this.getPosZ());

        BlockPos pos = new BlockPos(i, j, k);
        BlockState state = this.world.getBlockState(pos);
        if (AbstractRailBlock.isRail(state)) {
            RailShape railshape = ((AbstractRailBlock) state.getBlock()).getRailDirection(state, this.world, pos, this);

            boolean is_uphill = (railshape == RailShape.ASCENDING_EAST || railshape == RailShape.ASCENDING_WEST
                    || railshape == RailShape.ASCENDING_NORTH || railshape == RailShape.ASCENDING_SOUTH);

            return is_uphill;
        }

        return false;
    }

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;

        if (this.pushX == 0 && this.pushZ == 0) {
            this.pushX = this.getPosX() - player.getPosX();
            this.pushZ = this.getPosZ() - player.getPosZ();
        }

        Vector3d pos = this.getPositionVec();
        if (isMinecartPowered()) {
            world.playSound((double)pos.getX(), (double)pos.getY() + 0.4D, (double)pos.getZ(), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
            spawnSmokeParticles(world, this.getPositionVec(), false, true);
        }
        else {
            world.playSound((double)pos.getX(), (double)pos.getY() + 0.4D, (double)pos.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 0.8F, world.getRandom().nextFloat() * 0.4F + 0.8F, false);
        }

        setMinecartPowered(!isMinecartPowered());

        return ActionResultType.func_233537_a_(this.world.isRemote);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(POWERED, false);
    }

    protected boolean isMinecartPowered() {
        return this.dataManager.get(POWERED);
    }

    protected void setMinecartPowered(boolean powered) {
        this.dataManager.set(POWERED, powered);
    }


    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putDouble("PushX", this.pushX);
        compound.putDouble("PushZ", this.pushZ);
    }

    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.pushX = compound.getDouble("PushX");
        this.pushZ = compound.getDouble("PushZ");
    }

    public BlockState getDefaultDisplayTile() {
        return Blocks.CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, Boolean.valueOf(isMinecartPowered()));
    }

}
