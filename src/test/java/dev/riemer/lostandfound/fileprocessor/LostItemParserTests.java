package dev.riemer.lostandfound.fileprocessor;

import dev.riemer.lostandfound.model.LostItem;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LostItemParserTests {

    @Test
    public void testParseLostItemsFromText_SingleItem() {
        String text = "ItemName: Wallet\nQuantity: 1\nPlace: Lobby";

        List<LostItem> lostItems = LostItemParser.parseLostItemsFromText(text);

        assertEquals(1, lostItems.size(), "Should parse one lost item");

        LostItem item = lostItems.get(0);
        assertEquals("Wallet", item.getItemName());
        assertEquals(1, item.getQuantity());
        assertEquals("Lobby", item.getPlace());
    }

    @Test
    public void testParseLostItemsFromText_MultipleItems() {
        String text = "ItemName: Wallet\nQuantity: 1\nPlace: Lobby\n" +
                "ItemName: Umbrella\nQuantity: 2\nPlace: Entrance";

        List<LostItem> lostItems = LostItemParser.parseLostItemsFromText(text);

        assertEquals(2, lostItems.size(), "Should parse two lost items");

        LostItem item1 = lostItems.get(0);
        assertEquals("Wallet", item1.getItemName());
        assertEquals(1, item1.getQuantity());
        assertEquals("Lobby", item1.getPlace());

        LostItem item2 = lostItems.get(1);
        assertEquals("Umbrella", item2.getItemName());
        assertEquals(2, item2.getQuantity());
        assertEquals("Entrance", item2.getPlace());
    }

    @Test
    public void testParseLostItemsFromText_MissingRequiredFields() {
        String text = "ItemName: Wallet\nPlace: Lobby";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LostItemParser.parseLostItemsFromText(text),
                "Expected parseLostItemsFromText to throw, but it didn't"
        );

        assertEquals("Missing required fields in item data.", exception.getMessage());
    }

    @Test
    public void testParseLostItemsFromText_InvalidQuantity() {
        String text = "ItemName: Wallet\nQuantity: abc\nPlace: Lobby";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LostItemParser.parseLostItemsFromText(text),
                "Expected parseLostItemsFromText to throw, but it didn't"
        );

        assertEquals("Invalid quantity: abc", exception.getMessage());
    }

    @Test
    public void testParseLostItemsFromText_EmptyText() {
        String text = "";

        List<LostItem> lostItems = LostItemParser.parseLostItemsFromText(text);

        assertTrue(lostItems.isEmpty(), "Should return an empty list for empty input");
    }

    @Test
    public void testParseLostItemsFromText_QuantityLessThanOne() {
        String text = "ItemName: Wallet\nQuantity: 0\nPlace: Lobby";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LostItemParser.parseLostItemsFromText(text),
                "Expected parseLostItemsFromText to throw, but it didn't"
        );

        assertEquals("Quantity must be greater than zero.", exception.getMessage());
    }

    @Test
    public void testParseLostItemsFromText_IrregularSpacing() {
        String text = "   ItemName:   Wallet  \n  Quantity:   1\nPlace:   Lobby  ";

        List<LostItem> lostItems = LostItemParser.parseLostItemsFromText(text);

        assertEquals(1, lostItems.size(), "Should parse one lost item with irregular spacing");

        LostItem item = lostItems.get(0);
        assertEquals("Wallet", item.getItemName());
        assertEquals(1, item.getQuantity());
        assertEquals("Lobby", item.getPlace());
    }

    @Test
    public void testParseLostItemsFromText_CaseInsensitiveKeys() {
        String text = "itemname: Wallet\nquantity: 1\nplace: Lobby";

        List<LostItem> lostItems = LostItemParser.parseLostItemsFromText(text);

        assertEquals(1, lostItems.size(), "Should parse one lost item with case-insensitive keys");

        LostItem item = lostItems.get(0);
        assertEquals("Wallet", item.getItemName());
        assertEquals(1, item.getQuantity());
        assertEquals("Lobby", item.getPlace());
    }

    @Test
    public void testCreateLostItemFromData_Success() {
        Map<String, String> data = new HashMap<>();
        data.put("itemname", "Watch");
        data.put("quantity", "1");
        data.put("place", "Reception");

        LostItem item = LostItemParser.createLostItemFromData(data);

        assertEquals("Watch", item.getItemName());
        assertEquals(1, item.getQuantity());
        assertEquals("Reception", item.getPlace());
    }

    @Test
    public void testCreateLostItemFromData_MissingFields() {
        Map<String, String> data = new HashMap<>();
        data.put("itemname", "Watch");
        data.put("place", "Reception");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LostItemParser.createLostItemFromData(data),
                "Expected createLostItemFromData to throw, but it didn't"
        );

        assertEquals("Missing required fields in item data.", exception.getMessage());
    }

    @Test
    public void testCreateLostItemFromData_InvalidQuantity() {
        Map<String, String> data = new HashMap<>();
        data.put("itemname", "Watch");
        data.put("quantity", "abc");
        data.put("place", "Reception");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LostItemParser.createLostItemFromData(data),
                "Expected createLostItemFromData to throw, but it didn't"
        );

        assertEquals("Invalid quantity: abc", exception.getMessage());
    }

    @Test
    public void testCreateLostItemFromData_QuantityLessThanOne() {
        Map<String, String> data = new HashMap<>();
        data.put("itemname", "Watch");
        data.put("quantity", "0");
        data.put("place", "Reception");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LostItemParser.createLostItemFromData(data),
                "Expected createLostItemFromData to throw, but it didn't"
        );

        assertEquals("Quantity must be greater than zero.", exception.getMessage());
    }
}
