package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Random;

public class CampfireCartEntity extends AbstractMinecart {
    private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(CampfireCartEntity.class, EntityDataSerializers.BOOLEAN);
    private static final String PUSH_X_NAME = "PushX";
    private static final String PUSH_Z_NAME = "PushZ";
    private static final String POWERED_NAME = "powered";


    public double pushX = 0;
    public double pushZ = 0;

    // Taken from FurnaceMinecartEntity

    public CampfireCartEntity(EntityType<?> furnaceCart, Level world) {
        super(furnaceCart, world);
    }

    public CampfireCartEntity(EntityType<?> furnaceCart, Level worldIn, double x, double y, double z) {
        super(furnaceCart, worldIn, x, y, z);
    }

    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.FURNACE;
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
            Vec3 pos = this.position();
            if (random.nextInt(10) == 0) {
                level.playLocalSound(pos.x, pos.y + 0.4D, pos.z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.2F + random.nextFloat()/3, random.nextFloat() * 0.7F + 0.6F, false);
            }

            if (random.nextInt(10) == 0) {
                spawnSmokeParticles(level, pos, false, false);
            }
        }


    }

    // Taken from Campfire Block
    private static void spawnSmokeParticles(Level worldIn, Vec3 pos, boolean isSignalFire, boolean spawnExtraSmoke) {
        if (!worldIn.isClientSide()) return;
        Random random = worldIn.getRandom();
        SimpleParticleType basicparticletype = isSignalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        worldIn.addParticle(basicparticletype, true, pos.x + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.y + 0.4 + random.nextDouble(), (double)pos.z + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
        if (spawnExtraSmoke) {
            worldIn.addParticle(ParticleTypes.SMOKE, pos.x, pos.y + 0.8D, pos.z, 0.0D, 0.005D, 0.0D);
        }

    }

    public void destroy(DamageSource source) {
        this.remove(RemovalReason.KILLED);
        if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(MMItemReferences.campfire_cart);
        }

    }

    protected void moveAlongTrack(BlockPos pos, BlockState state) {
        double d0 = 1.0E-4D;
        double d1 = 0.001D;
        super.moveAlongTrack(pos, state);
        Vec3 vector3d = this.getDeltaMovement();
        double d2 = vector3d.horizontalDistanceSqr();
        double d3 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (isMinecartPowered() && d3 > 1.0E-4D && d2 > 0.001D) {
            double d4 = (double)Math.sqrt(d2);
            double d5 = (double)Math.sqrt(d3);
            this.pushX = vector3d.x / d4 * d5;
            this.pushZ = vector3d.z / d4 * d5;
        }

    }

    @Override
    protected void applyNaturalSlowdown() {
        double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (isMinecartPowered() && d0 > 1.0E-7D && isMinecartPowered() && !isGoingUphill()) {
            d0 = (double)Math.sqrt(d0);
            this.pushX /= d0;
            this.pushZ /= d0;
            // Four times slower than a furnace cart.
            Vec3 min_motion = this.getDeltaMovement().multiply(0.8D, 0.0D, 0.8D);
            double speed_coeff = this.getSpeedDiv();
            double new_x = (Math.abs(this.pushX/speed_coeff) > Math.abs(min_motion.x))? this.pushX/speed_coeff : min_motion.x;
            double new_z = (Math.abs(this.pushZ/speed_coeff) > Math.abs(min_motion.z))? this.pushZ/speed_coeff : min_motion.z;
            this.setDeltaMovement(new_x, min_motion.y, new_z);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.95D, 0.0D, 0.95D));
        }

        super.applyNaturalSlowdown();
    }

    public double getSpeedDiv() {
        return 11;
    }

    public boolean isGoingUphill() {
        int i = Mth.floor(this.position().x);
        int j = Mth.floor(this.position().y);
        int k = Mth.floor(this.position().z);

        BlockPos pos = new BlockPos(i, j, k);
        BlockState state = this.level.getBlockState(pos);
        if (RailBlock.isRail(state)) {
            RailShape railshape = ((BaseRailBlock) state.getBlock()).getRailDirection(state, this.level, pos, this);

            boolean is_uphill = (railshape == RailShape.ASCENDING_EAST || railshape == RailShape.ASCENDING_WEST
                    || railshape == RailShape.ASCENDING_NORTH || railshape == RailShape.ASCENDING_SOUTH);

            return is_uphill;
        }
        else {
            return true;
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;

        if (this.pushX == 0 && this.pushZ == 0) {
            this.pushX = this.position().x - player.position().x;
            this.pushZ = this.position().z - player.position().z;
        }

        Vec3 pos = this.position();
        if (level.isClientSide()) {
            if (isMinecartPowered()) {
                level.playLocalSound(pos.x, pos.y + 0.4D, pos.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.3F, 1.0F, false);
                spawnSmokeParticles(level, this.position(), false, true);
            } else {
                level.playLocalSound(pos.x, pos.y + 0.4D, pos.z, SoundEvents.FLINTANDSTEEL_USE, SoundSource.NEUTRAL, 0.3F, level.getRandom().nextFloat() * 0.4F + 0.8F, false);
            }
        }

        setMinecartPowered(!isMinecartPowered());

        return InteractionResult.sidedSuccess(this.level.isClientSide());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(POWERED, false);
    }

    public boolean isMinecartPowered() {
        return this.entityData.get(POWERED);
    }

    public void setMinecartPowered(boolean powered) {
        this.entityData.set(POWERED, powered);
    }


    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putDouble(PUSH_X_NAME, this.pushX);
        compound.putDouble(PUSH_Z_NAME, this.pushZ);
        compound.putBoolean(POWERED_NAME, this.isMinecartPowered());
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.pushX = compound.getDouble("PushX");
        this.pushZ = compound.getDouble("PushZ");
        setMinecartPowered(compound.getBoolean(POWERED_NAME));
    }

    public BlockState getDefaultDisplayBlockState() {
        return Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, Boolean.valueOf(isMinecartPowered()));
    }


    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.campfire_cart); }
}
