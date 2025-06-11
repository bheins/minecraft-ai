package com.example.aimodgen.block;

import com.example.aimodgen.generation.GeneratedContent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.List;

public class AIGeneratedBlock extends Block {
    private final GeneratedContent content;

    public AIGeneratedBlock(BlockBehaviour.Properties properties, GeneratedContent content) {
        super(properties);
        this.content = content;
    }

    public AIGeneratedBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
            .strength(3.0f)
            .requiresCorrectToolForDrops());
        this.content = null;
    }

    public GeneratedContent getContent() {
        return content;
    }
}
