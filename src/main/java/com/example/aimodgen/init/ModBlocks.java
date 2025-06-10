package com.example.aimodgen.init;

import com.example.aimodgen.AiModGenerator;
import com.example.aimodgen.block.AIGeneratedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AiModGenerator.MOD_ID);
    
    public static final RegistryObject<Block> AI_GENERATED_BLOCK = BLOCKS.register("ai_generated_block",
            AIGeneratedBlock::new);
}
