package com.maisalae.shiningsapphires.blocks;

import net.minecraft.block.*;
import net.minecraft.block.trees.Tree;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;
import java.util.function.Supplier;

public class ModSaplingBlock extends BushBlock implements IGrowable {
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE_0_1;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D,0.0D,2.0D,14.0D,12.0D,14.0D);
    private final Supplier<Tree> tree;
    private Block growsOn;

    public ModSaplingBlock(Supplier<Tree> treeIn, Properties properties, Block growsOnIn) {
        super(properties);
        this.tree = treeIn;
        this.growsOn = growsOnIn;
    }

    public static VoxelShape getShape() {
        return SHAPE;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if(!worldIn.isAreaLoaded(pos,1)){
            return;
        }
        if(worldIn.getLight(pos.up())>=9 && rand.nextInt(7)==0){
            this.grow(worldIn,pos,state,rand);
        }
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return (double) worldIn.rand.nextFloat() < 0.45D;
    }
    public void grow(ServerWorld serverWorld, BlockPos pos, BlockState state, Random rand) {
        if (state.get(STAGE) == 0) {
            serverWorld.setBlockState(pos, state.cycle(STAGE), 4);
        } else {
            if (!ForgeEventFactory.saplingGrowTree(serverWorld, rand, pos))
                return;
            this.tree.get().place(serverWorld, serverWorld.getChunkProvider().getChunkGenerator(), pos, state,
                    rand);
        }
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        this.grow(worldIn,pos,state,rand);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        Block block = state.getBlock();
        return block == growsOn;
    }
}
