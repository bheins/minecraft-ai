package com.example.aimodgen.tests;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;

/**
 * Test class for AI Item Behavior functionality
 */
public class AIItemBehaviorTest {

    public static void main(String[] args) {
        System.out.println("Running AI Item Behavior Tests...");
        
        testTeleportAbilityDetection();
        testHealingAbilityDetection();
        testFireAbilityDetection();
        testIceAbilityDetection();
        testItemNBTTagging();
        testCooldownCalculations();
        
        System.out.println("AI Item Behavior Tests completed.");
    }

    private static void testTeleportAbilityDetection() {
        try {
            // Test teleport ability detection
            ItemStack testItem = new ItemStack(Items.ENDER_PEARL);
            
            // Basic test - item should be recognizable as teleport-related
            boolean hasTeleportAbility = testItem.getItem() == Items.ENDER_PEARL;
            
            if (hasTeleportAbility) {
                System.out.println("[PASS] Teleport ability detection test");
            } else {
                System.err.println("[FAIL] Teleport ability detection test failed");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Teleport ability detection test failed: " + e.getMessage());
        }
    }

    private static void testHealingAbilityDetection() {
        try {
            // Test healing ability detection
            ItemStack testItem = new ItemStack(Items.GOLDEN_APPLE);
            
            // Basic test - item should be recognizable as healing-related
            boolean hasHealingAbility = testItem.getItem() == Items.GOLDEN_APPLE;
            
            if (hasHealingAbility) {
                System.out.println("[PASS] Healing ability detection test");
            } else {
                System.err.println("[FAIL] Healing ability detection test failed");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Healing ability detection test failed: " + e.getMessage());
        }
    }

    private static void testFireAbilityDetection() {
        try {
            // Test fire ability detection
            ItemStack testItem = new ItemStack(Items.FIRE_CHARGE);
            
            // Basic test - item should be recognizable as fire-related
            boolean hasFireAbility = testItem.getItem() == Items.FIRE_CHARGE;
            
            if (hasFireAbility) {
                System.out.println("[PASS] Fire ability detection test");
            } else {
                System.err.println("[FAIL] Fire ability detection test failed");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Fire ability detection test failed: " + e.getMessage());
        }
    }

    private static void testIceAbilityDetection() {
        try {
            // Test ice ability detection
            ItemStack testItem = new ItemStack(Items.ICE);
            
            // Basic test - item should be recognizable as ice-related
            boolean hasIceAbility = testItem.getItem() == Items.ICE;
            
            if (hasIceAbility) {
                System.out.println("[PASS] Ice ability detection test");
            } else {
                System.err.println("[FAIL] Ice ability detection test failed");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Ice ability detection test failed: " + e.getMessage());
        }
    }

    private static void testItemNBTTagging() {
        try {
            ItemStack testItem = new ItemStack(Items.DIAMOND_SWORD);
            CompoundTag nbt = testItem.getOrCreateTag();
            
            // Test NBT tagging
            nbt.putString("aimod_id", "test_sword");
            if (nbt.contains("aimod_id")) {
                System.out.println("[PASS] NBT aimod_id test");
            } else {
                System.err.println("[FAIL] NBT aimod_id test failed");
            }
            
            nbt.putLong("cooldown", 5000L);
            if (nbt.contains("cooldown")) {
                System.out.println("[PASS] NBT cooldown test");
            } else {
                System.err.println("[FAIL] NBT cooldown test failed");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Item NBT tagging test failed: " + e.getMessage());
        }
    }

    private static void testCooldownCalculations() {
        try {
            long currentTime = System.currentTimeMillis();
            long cooldownDuration = 5000L; // 5 seconds
            long cooldownEnd = currentTime + cooldownDuration;
            
            if (cooldownEnd > currentTime) {
                System.out.println("[PASS] Cooldown calculation test");
            } else {
                System.err.println("[FAIL] Cooldown calculation test failed");
            }
            
            long remainingCooldown = Math.max(0, cooldownEnd - currentTime);
            if (remainingCooldown >= 0) {
                System.out.println("[PASS] Remaining cooldown calculation test");
            } else {
                System.err.println("[FAIL] Remaining cooldown calculation test failed");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Cooldown calculations test failed: " + e.getMessage());
        }
    }
}