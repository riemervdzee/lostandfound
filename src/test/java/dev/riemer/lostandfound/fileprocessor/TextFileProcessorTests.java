package dev.riemer.lostandfound.fileprocessor;

import dev.riemer.lostandfound.model.LostItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TextFileProcessorTests {

    private TextFileProcessor textFileProcessor;

    @BeforeEach
    public void setUp() {
        textFileProcessor = new TextFileProcessor();
    }

    @Test
    public void testSupports_withTextPlain_shouldReturnTrue() {
        String contentType = "text/plain";
        assertTrue(textFileProcessor.supports(contentType), "Should support 'text/plain' content type");
    }

    @Test
    public void testSupports_withNonTextPlain_shouldReturnFalse() {
        String contentType = "application/pdf";
        assertFalse(textFileProcessor.supports(contentType), "Should not support non-'text/plain' content types");
    }

    @Test
    public void testProcessFile_withValidTextFile_shouldReturnLostItems() throws IOException {
        String textContent = "ItemName: Wallet\nQuantity: 1\nPlace: Lobby";
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                textContent.getBytes()
        );

        List<LostItem> lostItems = textFileProcessor.processFile(multipartFile);

        assertNotNull(lostItems, "LostItems should not be null");
        assertEquals(1, lostItems.size(), "Should parse one lost item");

        LostItem item = lostItems.get(0);
        assertEquals("Wallet", item.getItemName());
        assertEquals(1, item.getQuantity());
        assertEquals("Lobby", item.getPlace());
    }

    @Test
    public void testProcessFile_withEmptyFile_shouldReturnEmptyList() throws IOException {
        String textContent = "";
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                textContent.getBytes()
        );

        List<LostItem> lostItems = textFileProcessor.processFile(multipartFile);

        assertNotNull(lostItems, "LostItems should not be null");
        assertTrue(lostItems.isEmpty(), "Should return an empty list for empty file");
    }

    @Test
    public void testProcessFile_withInvalidContent_shouldThrowException() {
        String textContent = "ItemName: Wallet\nInvalid content without proper fields";
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "invalid.txt",
                "text/plain",
                textContent.getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> textFileProcessor.processFile(multipartFile),
                "Expected processFile to throw IllegalArgumentException for invalid content");
    }
}