package dev.riemer.lostandfound.fileprocessor;

import dev.riemer.lostandfound.model.LostItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to convert and validate String inputs to LostItems.
 */
public final class LostItemParser {
    /**
     * Processes a big piece of text into multiple LostItems. Using ItemName as a delimiter to start a new LostItem.
     * A LostItem should have at least the following keys "ItemName", "Quantity" and "Place" case-insensitive.
     *
     * @param text the string/text to parse for multiple LostItems
     * @return the parsed and validated LostItems
     */
    public static List<LostItem> parseLostItemsFromText(final String text) {
        List<LostItem> lostItems = new ArrayList<>();
        Map<String, String> currentItemData = new HashMap<>();

        // Split the input text on new-lines
        String[] lines = text.split("\\r?\\n");

        // Loop over all lines, trimming them and skipping empty ones
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            // If we start with a new LostItem key and if we have data for a previous item, then create it and add it
            if (line.toLowerCase().startsWith("itemname:") && !currentItemData.isEmpty()) {
                LostItem lostItem = createLostItemFromData(currentItemData);
                lostItems.add(lostItem);
                currentItemData.clear();
            }

            // Parse key-value pairs
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                String key = parts[0].trim().toLowerCase();
                String value = parts[1].trim();
                currentItemData.put(key, value);
            }
        }

        // Flush the last item if any
        if (!currentItemData.isEmpty()) {
            LostItem lostItem = createLostItemFromData(currentItemData);
            lostItems.add(lostItem);
        }

        return lostItems;
    }

    /**
     * Processes and validates a Map of found key-values to a single LostItem.
     *
     * @param data the map of key-values
     * @return The parsed LostItem
     */
    public static LostItem createLostItemFromData(final Map<String, String> data) {
        String name = data.get("itemname");
        String quantityStr = data.get("quantity");
        String place = data.get("place");

        // Validate
        if (name == null || quantityStr == null || place == null) {
            throw new IllegalArgumentException("Missing required fields in item data.");
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid quantity: " + quantityStr);
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        // Set and return
        LostItem lostItem = new LostItem();
        lostItem.setItemName(name);
        lostItem.setQuantity(quantity);
        lostItem.setPlace(place);
        return lostItem;
    }
}
