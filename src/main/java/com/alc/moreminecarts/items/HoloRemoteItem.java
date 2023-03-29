package com.alc.moreminecarts.items;

import com.alc.moreminecarts.registry.MMBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class HoloRemoteItem extends Item {

    public enum HoloRemoteType {
        regular,
        backwards,
        simple,
        broken
    }

    public Supplier<Block> regular_block = MMBlocks.HOLO_SCAFFOLD;
    public Supplier<Block> chaotic_block = MMBlocks.CHAOTIC_HOLO_SCAFFOLD;
    public HoloRemoteType remote_type;

    public HoloRemoteItem(HoloRemoteType remote_type, Item.Properties properties) {
        super(properties);
        this.remote_type = remote_type;
    }

    // Taken from ScaffoldingItem

    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState blockstate = world.getBlockState(blockpos);

        //if (!HoloScaffold.isValidDistance(world, blockpos)) return null;

        if (!(blockstate.is(regular_block.get()) || blockstate.is(chaotic_block.get())) || remote_type == HoloRemoteType.simple || remote_type == HoloRemoteType.broken) {
            return context;
        } else {
            Direction direction;
            if (context.isSecondaryUseActive()) {
                direction = context.isInside() ? context.getClickedFace().getOpposite() : context.getClickedFace();
            } else {
                direction = (context.getClickedFace().getAxis() == Direction.Axis.Y)
                    ? context.getHorizontalDirection()
                    : remote_type == HoloRemoteType.regular
                    ? Direction.UP
                    : Direction.DOWN;
            }

            int i = 0;
            BlockPos.MutableBlockPos blockpos$mutable = blockpos.mutable().move(direction);

            while(i < 30) {
                if (!world.isClientSide && !context.getLevel().isInWorldBounds(blockpos$mutable)) {
                    Player player = context.getPlayer();
                    int j = world.getMaxBuildHeight();
                    if (player instanceof ServerPlayer && blockpos$mutable.getY() >= j) {
                        ((ServerPlayer)player).sendSystemMessage(Component.translatable("build.tooHigh", j - 1).withStyle(ChatFormatting.RED), true);
                    }
                    break;
                }

                blockstate = world.getBlockState(blockpos$mutable);
                if (!blockstate.is(this.getBlock())) {
                    if (blockstate.canBeReplaced(context)) {
                        return BlockPlaceContext.at(context, blockpos$mutable, direction);
                    }
                    break;
                }

                blockpos$mutable.move(direction);
                if (direction.getAxis().isHorizontal()) {
                    ++i;
                }
            }
        }
        return null;
    }


    // All copied from BlockItem, and slightly modified.

    public Block getBlock() {
        return this.getBlockRaw() == null ? null : ForgeRegistries.BLOCKS.getDelegateOrThrow(this.getBlockRaw()).get();
    }

    private Block getBlockRaw() {
        return remote_type == HoloRemoteType.broken? chaotic_block.get() : regular_block.get();
    }

    public InteractionResult useOn(UseOnContext p_195939_1_) {
        InteractionResult actionresulttype = this.place(new BlockPlaceContext(p_195939_1_));
        return !actionresulttype.consumesAction() && this.isEdible() ? this.use(p_195939_1_.getLevel(), p_195939_1_.getPlayer(), p_195939_1_.getHand()).getResult() : actionresulttype;
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext p_195945_1_) {
        BlockState blockstate = this.getBlock().getStateForPlacement(p_195945_1_);
        return blockstate != null && this.canPlace(p_195945_1_, blockstate) ? blockstate : null;
    }

    protected boolean canPlace(BlockPlaceContext p_195944_1_, BlockState p_195944_2_) {
        Player playerentity = p_195944_1_.getPlayer();
        CollisionContext iselectioncontext = playerentity == null ? CollisionContext.empty() : CollisionContext.of(playerentity);
        return (!this.mustSurvive() || p_195944_2_.canSurvive(p_195944_1_.getLevel(), p_195944_1_.getClickedPos())) && p_195944_1_.getLevel().isUnobstructed(p_195944_2_, p_195944_1_.getClickedPos(), iselectioncontext);
    }

    protected boolean mustSurvive() {
        return true;
    }

    protected boolean placeBlock(BlockPlaceContext p_195941_1_, BlockState p_195941_2_) {
        boolean ret = p_195941_1_.getLevel().setBlock(p_195941_1_.getClickedPos(), p_195941_2_, 11);
        if (remote_type == HoloRemoteType.broken)
            p_195941_1_.getLevel().scheduleTick(p_195941_1_.getClickedPos(), p_195941_2_.getBlock(), 2, TickPriority.VERY_LOW);
        return ret;
    }

    public InteractionResult place(BlockPlaceContext context) {
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockitemusecontext = this.updatePlacementContext(context);
            if (blockitemusecontext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getPlacementState(blockitemusecontext);
                if (blockstate == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(blockitemusecontext, blockstate)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockitemusecontext.getClickedPos();
                    Level world = blockitemusecontext.getLevel();
                    Player playerentity = blockitemusecontext.getPlayer();
                    ItemStack itemstack = blockitemusecontext.getItemInHand();
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    Block block = blockstate1.getBlock();
                    if (block == blockstate.getBlock()) {
                        blockstate1 = this.updateBlockStateFromTag(blockpos, world, itemstack, blockstate1);
                        this.updateCustomBlockEntityTag(blockpos, world, playerentity, itemstack, blockstate1);
                        block.setPlacedBy(world, blockpos, blockstate1, playerentity, itemstack);
                        if (playerentity instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)playerentity, blockpos, itemstack);
                        }
                    }

                    SoundType soundtype = blockstate1.getSoundType(world, blockpos, context.getPlayer());
                    world.playSound(playerentity, blockpos, this.getPlaceSound(blockstate1, world, blockpos, context.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    if (playerentity == null || !playerentity.isCreative()) {
                        // Remote blocks are free.
                        //itemstack.shrink(1);
                    }

                    return InteractionResult.sidedSuccess(world.isClientSide);
                }
            }
        }
    }

    protected boolean updateCustomBlockEntityTag(BlockPos p_195943_1_, Level p_195943_2_, @Nullable Player p_195943_3_, ItemStack p_195943_4_, BlockState p_195943_5_) {
        return updateCustomBlockEntityTag(p_195943_2_, p_195943_3_, p_195943_1_, p_195943_4_);
    }

    public static boolean updateCustomBlockEntityTag(Level p_179224_0_, @Nullable Player p_179224_1_, BlockPos p_179224_2_, ItemStack p_179224_3_) {
        MinecraftServer minecraftserver = p_179224_0_.getServer();
        if (minecraftserver == null) {
            return false;
        } else {
            CompoundTag compoundnbt = p_179224_3_.getTagElement("BlockEntityTag");
            if (compoundnbt != null) {
                BlockEntity tileentity = p_179224_0_.getBlockEntity(p_179224_2_);
                if (tileentity != null) {
                    if (!p_179224_0_.isClientSide && tileentity.onlyOpCanSetNbt() && (p_179224_1_ == null || !p_179224_1_.canUseGameMasterBlocks())) {
                        return false;
                    }

                    CompoundTag compoundnbt1 = tileentity.saveWithFullMetadata();
                    CompoundTag compoundnbt2 = compoundnbt1.copy();
                    compoundnbt1.merge(compoundnbt);
                    compoundnbt1.putInt("x", p_179224_2_.getX());
                    compoundnbt1.putInt("y", p_179224_2_.getY());
                    compoundnbt1.putInt("z", p_179224_2_.getZ());
                    if (!compoundnbt1.equals(compoundnbt2)) {
                        tileentity.load(compoundnbt1);
                        tileentity.setChanged();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private BlockState updateBlockStateFromTag(BlockPos p_219985_1_, Level p_219985_2_, ItemStack p_219985_3_, BlockState p_219985_4_) {
        BlockState blockstate = p_219985_4_;
        CompoundTag compoundnbt = p_219985_3_.getTag();
        if (compoundnbt != null) {
            CompoundTag compoundnbt1 = compoundnbt.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> statecontainer = p_219985_4_.getBlock().getStateDefinition();

            for(String s : compoundnbt1.getAllKeys()) {
                Property<?> property = statecontainer.getProperty(s);
                if (property != null) {
                    String s1 = compoundnbt1.get(s).getAsString();
                    blockstate = updateState(blockstate, property, s1);
                }
            }
        }

        if (blockstate != p_219985_4_) {
            p_219985_2_.setBlock(p_219985_1_, blockstate, 2);
        }

        return blockstate;
    }

    private static <T extends Comparable<T>> BlockState updateState(BlockState p_219988_0_, Property<T> p_219988_1_, String p_219988_2_) {
        return p_219988_1_.getValue(p_219988_2_).map((p_219986_2_) -> {
            return p_219988_0_.setValue(p_219988_1_, p_219986_2_);
        }).orElse(p_219988_0_);
    }

    //Forge: Sensitive version of BlockItem#getPlaceSound
    protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
        return state.getSoundType(world, pos, entity).getPlaceSound();
    }
}
