package com.alc.moreminecarts.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;

public class PistonDisplayBlock extends Block {

    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 4);

    public PistonDisplayBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(VARIANT);
    }

}
