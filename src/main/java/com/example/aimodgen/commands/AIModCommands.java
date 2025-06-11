package com.example.aimodgen.commands;

import com.example.aimodgen.generation.ContentGenerator;
import com.example.aimodgen.generation.GeneratedContent;
import com.example.aimodgen.generation.ContentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIModCommands {
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(\\w+)=(\\w+)");    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("aimod")
            .then(Commands.literal("generate")
                .then(Commands.literal("block")
                    .then(Commands.argument("description", StringArgumentType.greedyString())
                        .executes(context -> generateBlock(context.getSource(), 
                            StringArgumentType.getString(context, "description")))))
                .then(Commands.literal("item")
                    .then(Commands.argument("description", StringArgumentType.greedyString())
                        .executes(context -> generateItem(context.getSource(), 
                            StringArgumentType.getString(context, "description"))))))
            .then(Commands.literal("list")
                .executes(context -> listGeneratedContent(context.getSource())))
            .then(Commands.literal("give")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(context -> giveGeneratedItem(context.getSource(), 
                        StringArgumentType.getString(context, "name")))))
            .then(Commands.literal("delete")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(context -> deleteContent(context.getSource(), 
                        StringArgumentType.getString(context, "name"))))));
    }

    private static int generateBlock(CommandSourceStack source, String description) {
        try {
            source.sendSuccess(Component.literal("Starting block generation..."), true);
              Map<String, String> properties = extractProperties(description);
            String cleanDescription = description.replaceAll("\\s+\\w+=\\w+", "").trim();
            
            ContentGenerator.getInstance().generateBlock(cleanDescription, properties)
                .thenAccept(block -> {
                    source.sendSuccess(Component.literal("Successfully generated block!"), true);
                })
                .exceptionally(e -> {
                    source.sendFailure(Component.literal("Failed to generate block: " + e.getMessage()));
                    return null;
                });
            
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to generate block: " + e.getMessage()));
            return 0;
        }
    }

    private static int generateItem(CommandSourceStack source, String description) {
        try {
            source.sendSuccess(Component.literal("Starting item generation..."), true);
              Map<String, String> properties = extractProperties(description);
            String cleanDescription = description.replaceAll("\\s+\\w+=\\w+", "").trim();
            
            ContentGenerator.getInstance().generateItem(cleanDescription, properties)
                .thenAccept(item -> {
                    source.sendSuccess(Component.literal("Successfully generated item!"), true);
                })
                .exceptionally(e -> {
                    source.sendFailure(Component.literal("Failed to generate item: " + e.getMessage()));
                    return null;
                });
            
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to generate item: " + e.getMessage()));
            return 0;
        }
    }    private static int listGeneratedContent(CommandSourceStack source) {
        Map<String, GeneratedContent> content = ContentGenerator.getInstance().listContent();
        if (content.isEmpty()) {
            source.sendSuccess(Component.literal("No generated content found."), false);
        } else {
            source.sendSuccess(Component.literal("Generated content:"), false);
            content.forEach((id, item) -> {
                source.sendSuccess(Component.literal(
                    String.format("- %s (%s): %s", 
                        id, 
                        item.getType().toString().toLowerCase(),
                        item.getDescription())), 
                    false);
            });
        }
        return 1;
    }    private static int deleteContent(CommandSourceStack source, String name) {
        try {
            ContentGenerator.getInstance().deleteContent(name);
            source.sendSuccess(Component.literal("Successfully deleted: " + name), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to delete content: " + e.getMessage()));
            return 0;
        }
    }

    private static int giveGeneratedItem(CommandSourceStack source, String itemName) {
        try {
            Map<String, GeneratedContent> content = ContentGenerator.getInstance().listContent();
            GeneratedContent generatedContent = content.get(itemName);
            
            if (generatedContent == null) {
                source.sendFailure(Component.literal("Item '" + itemName + "' not found. Use /aimod list to see available items."));
                return 0;
            }
            
            if (generatedContent.getType() != ContentType.ITEM) {
                source.sendFailure(Component.literal("'" + itemName + "' is not an item."));
                return 0;
            }
            
            // Get the player
            if (!(source.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
                source.sendFailure(Component.literal("Only players can receive items."));
                return 0;
            }
            
            // Create a base item (stick for now) with custom NBT
            net.minecraft.world.item.ItemStack itemStack = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.STICK);
            
            // Add custom NBT data
            net.minecraft.nbt.CompoundTag nbtTag = itemStack.getOrCreateTag();
            nbtTag.putString("aimod_id", generatedContent.getId());
            nbtTag.putString("aimod_description", generatedContent.getDescription());
            nbtTag.putString("aimod_type", "generated_item");
            
            // Set custom display name and lore
            net.minecraft.nbt.CompoundTag displayTag = new net.minecraft.nbt.CompoundTag();
            String displayName = "Generated Item";
            if (generatedContent.getProperties().has("name")) {
                displayName = generatedContent.getProperties().get("name").getAsString();
            }
            displayTag.putString("Name", Component.Serializer.toJson(Component.literal(displayName).withStyle(net.minecraft.ChatFormatting.YELLOW)));
            
            // Add lore with description and properties
            net.minecraft.nbt.ListTag loreTag = new net.minecraft.nbt.ListTag();
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(Component.Serializer.toJson(Component.literal("AI Generated Item").withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC))));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(Component.Serializer.toJson(Component.literal(generatedContent.getDescription()).withStyle(net.minecraft.ChatFormatting.BLUE))));
            
            // Add some properties to lore
            if (generatedContent.getProperties().has("maxDurability")) {
                int durability = generatedContent.getProperties().get("maxDurability").getAsInt();
                loreTag.add(net.minecraft.nbt.StringTag.valueOf(Component.Serializer.toJson(Component.literal("Durability: " + durability).withStyle(net.minecraft.ChatFormatting.GREEN))));
            }
            
            displayTag.put("Lore", loreTag);
            nbtTag.put("display", displayTag);
            
            // Give item to player
            player.getInventory().add(itemStack);
            source.sendSuccess(Component.literal("Gave " + displayName + " to " + player.getName().getString()), true);
            
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to give item: " + e.getMessage()));
            return 0;
        }
    }

    private static Map<String, String> extractProperties(String description) {
        Map<String, String> properties = new HashMap<>();
        Matcher matcher = PROPERTY_PATTERN.matcher(description);
        while (matcher.find()) {
            properties.put(matcher.group(1), matcher.group(2));
        }
        return properties;
    }
}
