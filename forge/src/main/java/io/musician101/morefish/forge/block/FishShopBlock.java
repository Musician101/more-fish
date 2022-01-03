package io.musician101.morefish.forge.block;

import io.musician101.morefish.forge.tileentity.FishShopTileEntity;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class FishShopBlock extends ContainerBlock {

    public FishShopBlock() {
        super(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader worldIn) {
        return new FishShopTileEntity();
    }
}
