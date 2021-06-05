package com.alc.moreminecarts.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;

public class FlagDisplayBlock extends Block {

    public static final IntegerProperty MAIN = IntegerProperty.create("main", 0, 17);
    public static final IntegerProperty NEXT = IntegerProperty.create("next", 0, 17);
    public static final IntegerProperty LAST = IntegerProperty.create("last", 0, 17);

    public FlagDisplayBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(MAIN, 0).setValue(NEXT, 0).setValue(LAST, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(MAIN, NEXT, LAST);
    }

}
