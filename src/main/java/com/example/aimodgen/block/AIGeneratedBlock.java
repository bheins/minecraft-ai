package com.example.aimodgen.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class AIGeneratedBlock extends Block {
    public AIGeneratedBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
            .strength(3.0f)
            .requiresCorrectToolForDrops());
    }
}
