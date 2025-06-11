package com.example.aimodgen.events;

import com.example.aimodgen.AiModGenerator;
import com.example.aimodgen.generation.ContentGenerator;
import com.example.aimodgen.generation.GeneratedContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod.EventBusSubscriber(modid = AiModGenerator.MOD_ID)
public class AIItemBehaviorHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    
    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        Level level = event.getLevel();
        
        // Check if this is an AI-generated item
        CompoundTag nbt = itemStack.getTag();
        if (nbt == null || !nbt.contains("aimod_id")) {
            return;
        }
        
        String aimodId = nbt.getString("aimod_id");
        
        // Get the generated content data
        Map<String, GeneratedContent> content = ContentGenerator.getInstance().listContent();
        GeneratedContent generatedContent = content.get(aimodId);
        
        if (generatedContent == null) {
            return;
        }
        
        // Handle different types of AI-generated item behaviors
        boolean handled = false;
        
        // Check for teleportation abilities
        if (hasCustomProperty(generatedContent, "teleportRange") || 
            aimodId.contains("teleport") || 
            generatedContent.getDescription().toLowerCase().contains("teleport")) {
            handled = handleTeleportation(player, itemStack, generatedContent, level);
        }
        
        // Check for healing abilities
        if (!handled && (hasCustomProperty(generatedContent, "healAmount") || 
            aimodId.contains("heal") || 
            generatedContent.getDescription().toLowerCase().contains("heal"))) {
            handled = handleHealing(player, itemStack, generatedContent, level);
        }
        
        // Check for fire abilities
        if (!handled && (hasCustomProperty(generatedContent, "fireRadius") || 
            aimodId.contains("fire") || 
            generatedContent.getDescription().toLowerCase().contains("fire"))) {
            handled = handleFireAbility(player, itemStack, generatedContent, level);
        }
        
        // Check for ice/freeze abilities
        if (!handled && (hasCustomProperty(generatedContent, "freezeRadius") || 
            aimodId.contains("ice") || aimodId.contains("freeze") ||
            generatedContent.getDescription().toLowerCase().contains("freeze") ||
            generatedContent.getDescription().toLowerCase().contains("ice"))) {
            handled = handleIceAbility(player, itemStack, generatedContent, level);
        }
        
        if (handled) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
    
    private static boolean hasCustomProperty(GeneratedContent content, String property) {
        return content.getProperties().has("customProperties") &&
               content.getProperties().getAsJsonObject("customProperties").has(property);
    }
    
    private static boolean handleTeleportation(Player player, ItemStack itemStack, GeneratedContent content, Level level) {
        if (level.isClientSide()) return false;
        
        // Check cooldown
        CompoundTag nbt = itemStack.getOrCreateTag();
        long lastUse = nbt.getLong("lastTeleportUse");
        long currentTime = level.getGameTime();
        
        // Get cooldown from AI properties (default 60 ticks = 3 seconds)
        int cooldownTicks = 60;
        if (hasCustomProperty(content, "teleportCooldown")) {
            cooldownTicks = content.getProperties().getAsJsonObject("customProperties")
                    .get("teleportCooldown").getAsInt();
        }
        
        if (currentTime - lastUse < cooldownTicks) {
            long remainingSeconds = (cooldownTicks - (currentTime - lastUse)) / 20;
            player.sendSystemMessage(Component.literal("Teleport on cooldown for " + remainingSeconds + " seconds"));
            return true;
        }
        
        // Get teleport range (default 10 blocks)
        int range = 10;
        if (hasCustomProperty(content, "teleportRange")) {
            range = content.getProperties().getAsJsonObject("customProperties")
                    .get("teleportRange").getAsInt();
        }
        
        // Calculate teleport destination (in the direction player is looking)
        Vec3 lookVec = player.getLookAngle();
        Vec3 currentPos = player.position();
        Vec3 targetPos = currentPos.add(lookVec.scale(range));
        
        // Find safe ground position
        BlockPos safePos = findSafePosition((ServerLevel) level, new BlockPos(targetPos));
        if (safePos == null) {
            player.sendSystemMessage(Component.literal("Cannot teleport - no safe location found!"));
            return true;
        }
        
        // Perform teleportation
        player.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
        
        // Effects
        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Update cooldown
        nbt.putLong("lastTeleportUse", currentTime);
        
        // Damage item slightly
        if (itemStack.isDamageableItem()) {
            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        
        player.sendSystemMessage(Component.literal("Teleported " + range + " blocks!"));
        LOGGER.info("Player {} used teleport wand to teleport to {}", player.getName().getString(), safePos);
        
        return true;
    }
    
    private static boolean handleHealing(Player player, ItemStack itemStack, GeneratedContent content, Level level) {
        if (level.isClientSide()) return false;
        
        // Check cooldown
        CompoundTag nbt = itemStack.getOrCreateTag();
        long lastUse = nbt.getLong("lastHealUse");
        long currentTime = level.getGameTime();
        
        int cooldownTicks = 100; // 5 seconds default
        if (currentTime - lastUse < cooldownTicks) {
            long remainingSeconds = (cooldownTicks - (currentTime - lastUse)) / 20;
            player.sendSystemMessage(Component.literal("Healing on cooldown for " + remainingSeconds + " seconds"));
            return true;
        }
        
        // Get heal amount (default 4 hearts = 8 health points)
        float healAmount = 8.0f;
        if (hasCustomProperty(content, "healAmount")) {
            healAmount = content.getProperties().getAsJsonObject("customProperties")
                    .get("healAmount").getAsFloat();
        }
        
        // Only heal if player is damaged
        if (player.getHealth() >= player.getMaxHealth()) {
            player.sendSystemMessage(Component.literal("Already at full health!"));
            return true;
        }
        
        // Perform healing
        float newHealth = Math.min(player.getMaxHealth(), player.getHealth() + healAmount);
        player.setHealth(newHealth);
        
        // Effects
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 2.0f);
        
        // Update cooldown
        nbt.putLong("lastHealUse", currentTime);
        
        // Damage item
        if (itemStack.isDamageableItem()) {
            itemStack.hurtAndBreak(2, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        
        player.sendSystemMessage(Component.literal("Healed " + (int)(healAmount/2) + " hearts!"));
        return true;
    }
    
    private static boolean handleFireAbility(Player player, ItemStack itemStack, GeneratedContent content, Level level) {
        if (level.isClientSide()) return false;
        
        // Check cooldown
        CompoundTag nbt = itemStack.getOrCreateTag();
        long lastUse = nbt.getLong("lastFireUse");
        long currentTime = level.getGameTime();
        
        int cooldownTicks = 40; // 2 seconds
        if (currentTime - lastUse < cooldownTicks) {
            return true;
        }
        
        // Create fire effect around player
        int radius = 3;
        if (hasCustomProperty(content, "fireRadius")) {
            radius = content.getProperties().getAsJsonObject("customProperties")
                    .get("fireRadius").getAsInt();
        }
        
        BlockPos playerPos = player.blockPosition();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x*x + z*z <= radius*radius) {
                    BlockPos firePos = playerPos.offset(x, 0, z);
                    if (level.getBlockState(firePos).isAir() && 
                        level.getBlockState(firePos.below()).isSolidRender(level, firePos.below())) {
                        level.setBlockAndUpdate(firePos, net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState());
                    }
                }
            }
        }
        
        // Effects
        level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Update cooldown
        nbt.putLong("lastFireUse", currentTime);
        
        // Damage item
        if (itemStack.isDamageableItem()) {
            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        
        player.sendSystemMessage(Component.literal("Created fire in " + radius + " block radius!"));
        return true;
    }
    
    private static boolean handleIceAbility(Player player, ItemStack itemStack, GeneratedContent content, Level level) {
        if (level.isClientSide()) return false;
        
        // Check cooldown
        CompoundTag nbt = itemStack.getOrCreateTag();
        long lastUse = nbt.getLong("lastIceUse");
        long currentTime = level.getGameTime();
        
        int cooldownTicks = 60; // 3 seconds
        if (currentTime - lastUse < cooldownTicks) {
            return true;
        }
        
        // Create ice effect around player
        int radius = 2;
        if (hasCustomProperty(content, "freezeRadius")) {
            radius = content.getProperties().getAsJsonObject("customProperties")
                    .get("freezeRadius").getAsInt();
        }
        
        BlockPos playerPos = player.blockPosition();
        int blocksChanged = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x*x + z*z <= radius*radius) {
                        BlockPos icePos = playerPos.offset(x, y, z);
                        if (level.getBlockState(icePos).getBlock() == net.minecraft.world.level.block.Blocks.WATER) {
                            level.setBlockAndUpdate(icePos, net.minecraft.world.level.block.Blocks.ICE.defaultBlockState());
                            blocksChanged++;
                        }
                    }
                }
            }
        }
        
        // Effects
        level.playSound(null, player.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0f, 0.5f);
        
        // Update cooldown
        nbt.putLong("lastIceUse", currentTime);
        
        // Damage item
        if (itemStack.isDamageableItem()) {
            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        
        if (blocksChanged > 0) {
            player.sendSystemMessage(Component.literal("Froze " + blocksChanged + " water blocks!"));
        } else {
            player.sendSystemMessage(Component.literal("No water nearby to freeze!"));
        }
        return true;
    }
    
    private static BlockPos findSafePosition(ServerLevel level, BlockPos target) {
        // Try to find a safe position near the target
        for (int y = Math.max(level.getMinBuildHeight(), target.getY() - 5); 
             y <= Math.min(level.getMaxBuildHeight() - 2, target.getY() + 5); y++) {
            BlockPos pos = new BlockPos(target.getX(), y, target.getZ());
            
            // Check if position is safe (solid ground, air above)
            if (level.getBlockState(pos.below()).isSolidRender(level, pos.below()) &&
                level.getBlockState(pos).isAir() &&
                level.getBlockState(pos.above()).isAir()) {
                return pos;
            }
        }
        return null; // No safe position found
    }
}
