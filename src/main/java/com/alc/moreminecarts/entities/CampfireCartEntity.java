package com.alc.moreminecarts.entities;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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
import net.minecraftforge.registries.ObjectHolder;

import java.util.Random;

@ObjectHolder("moreminecarts")
public class CampfireCartEntity extends AbstractMinecartEntity {
    private static final DataParameter<Boolean> POWERED = EntityDataManager.defineId(FurnaceMinecartEntity.class, DataSerializers.BOOLEAN);

    public static final Item campfire_cart = null;

    public double pushX = 0;
    public double pushZ = 0;

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

        if (Math.abs(this.getDeltaMovement().x * 5) > pushX) {
            pushX = this.getDeltaMovement().x;
        }

        if (Math.abs(this.getDeltaMovement().z * 5) > pushZ) {
            pushZ = this.getDeltaMovement().z;
        }

        if (this.isMinecartPowered() && level.isClientSide()){
            Vector3d pos = this.position();
            if (random.nextInt(10) == 0) {
                level.playLocalSound(pos.x, pos.y + 0.4D, pos.z, SoundEvents.CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.2F + random.nextFloat()/3, random.nextFloat() * 0.7F + 0.6F, false);
            }

            if (random.nextInt(10) == 0) {
                spawnSmokeParticles(level, pos, false, false);
            }
        }


    }

    // Taken from Campfire Block
    private static void spawnSmokeParticles(World worldIn, Vector3d pos, boolean isSignalFire, boolean spawnExtraSmoke) {
        Random random = worldIn.getRandom();
        BasicParticleType basicparticletype = isSignalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        worldIn.addParticle(basicparticletype, true, pos.x + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.y + 0.4 + random.nextDouble(), (double)pos.z + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
        if (spawnExtraSmoke) {
            worldIn.addParticle(ParticleTypes.SMOKE, pos.x, pos.y + 0.8D, pos.z, 0.0D, 0.005D, 0.0D);
        }

    }

    public void destroy(DamageSource source) {
        this.remove();
        if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(campfire_cart);
        }

    }

    protected void moveAlongTrack(BlockPos pos, BlockState state) {
        double d0 = 1.0E-4D;
        double d1 = 0.001D;
        super.moveAlongTrack(pos, state);
        Vector3d vector3d = this.getDeltaMovement();
        double d2 = getHorizontalDistanceSqr(vector3d);
        double d3 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (isMinecartPowered() && d3 > 1.0E-4D && d2 > 0.001D) {
            double d4 = (double)MathHelper.sqrt(d2);
            double d5 = (double)MathHelper.sqrt(d3);
            this.pushX = vector3d.x / d4 * d5;
            this.pushZ = vector3d.z / d4 * d5;
        }

    }

    @Override
    protected void applyNaturalSlowdown() {
        double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (isMinecartPowered() && d0 > 1.0E-7D && isMinecartPowered() && !isGoingUphill()) {
            d0 = (double)MathHelper.sqrt(d0);
            this.pushX /= d0;
            this.pushZ /= d0;
            // Four times slower than a furnace cart.
            Vector3d min_motion = this.getDeltaMovement().multiply(0.8D, 0.0D, 0.8D);
            double speed_coeff = this.getSpeedCoeff();
            double new_x = (Math.abs(this.pushX/speed_coeff) > Math.abs(min_motion.x))? this.pushX/speed_coeff : min_motion.x;
            double new_z = (Math.abs(this.pushZ/speed_coeff) > Math.abs(min_motion.z))? this.pushZ/speed_coeff : min_motion.z;
            this.setDeltaMovement(new_x, min_motion.y, new_z);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.95D, 0.0D, 0.95D));
        }

        super.applyNaturalSlowdown();
    }

    public double getSpeedCoeff() {
        return 11;
    }

    public boolean isGoingUphill() {
        int i = MathHelper.floor(this.position().x);
        int j = MathHelper.floor(this.position().y);
        int k = MathHelper.floor(this.position().z);

        BlockPos pos = new BlockPos(i, j, k);
        BlockState state = this.level.getBlockState(pos);
        if (AbstractRailBlock.isRail(state)) {
            RailShape railshape = ((AbstractRailBlock) state.getBlock()).getRailDirection(state, this.level, pos, this);

            boolean is_uphill = (railshape == RailShape.ASCENDING_EAST || railshape == RailShape.ASCENDING_WEST
                    || railshape == RailShape.ASCENDING_NORTH || railshape == RailShape.ASCENDING_SOUTH);

            return is_uphill;
        }
        else {
            return true;
        }
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;

        if (this.pushX == 0 && this.pushZ == 0) {
            this.pushX = this.position().x - player.position().x;
            this.pushZ = this.position().z - player.position().z;
        }

        Vector3d pos = this.position();
        if (isMinecartPowered()) {
            level.playLocalSound(pos.x, pos.y + 0.4D, pos.z, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.3F, 1.0F, false);
            spawnSmokeParticles(level, this.position(), false, true);
        }
        else {
            level.playLocalSound(pos.x, pos.y + 0.4D, pos.z, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 0.3F, level.getRandom().nextFloat() * 0.4F + 0.8F, false);
        }

        setMinecartPowered(!isMinecartPowered());

        return ActionResultType.sidedSuccess(this.level.isClientSide());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(POWERED, false);
    }

    protected boolean isMinecartPowered() {
        return this.entityData.get(POWERED);
    }

    protected void setMinecartPowered(boolean powered) {
        this.entityData.set(POWERED, powered);
    }


    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putDouble("PushX", this.pushX);
        compound.putDouble("PushZ", this.pushZ);
    }

    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.pushX = compound.getDouble("PushX");
        this.pushZ = compound.getDouble("PushZ");
    }

    public BlockState getDefaultDisplayBlockState() {
        return Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, Boolean.valueOf(isMinecartPowered()));
    }


    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


}
