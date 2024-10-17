package dev.riemer.lostandfound.fileprocessor;

import dev.riemer.lostandfound.model.LostItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PdfFileProcessorTests {

    private PdfFileProcessor pdfFileProcessor;

    @BeforeEach
    public void setUp() {
        pdfFileProcessor = new PdfFileProcessor();
    }

    @Test
    public void testSupports_withPdfContentType_shouldReturnTrue() {
        String contentType = "application/pdf";
        assertTrue(pdfFileProcessor.supports(contentType), "Should support 'application/pdf' content type");
    }

    @Test
    public void testSupports_withNonPdfContentType_shouldReturnFalse() {
        String contentType = "text/plain";
        assertFalse(pdfFileProcessor.supports(contentType), "Should not support non-PDF content types");
    }

    @Test
    public void testProcessFile_withValidPdf_shouldReturnLostItems() throws IOException {
        // Create an in-memory PDF with known content
        String pdfContent = "ItemName: Wallet\nQuantity: 1\nPlace: Lobby";
        MultipartFile multipartFile = createPdfMultipartFile("test.pdf", pdfContent);

        List<LostItem> lostItems = pdfFileProcessor.processFile(multipartFile);

        assertNotNull(lostItems, "LostItems should not be null");
        assertEquals(1, lostItems.size(), "Should parse one lost item");

        LostItem item = lostItems.get(0);
        assertEquals("Wallet", item.getItemName());
        assertEquals(1, item.getQuantity());
        assertEquals("Lobby", item.getPlace());
    }

    @Test
    public void testProcessFile_withInvalidPdf_shouldThrowIOException() {
        // Create a mock MultipartFile with invalid PDF content
        byte[] invalidPdfContent = "Not a PDF content".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "invalid.pdf",
                "application/pdf",
                invalidPdfContent
        );

        assertThrows(IOException.class, () -> pdfFileProcessor.processFile(multipartFile),
                "Expected processFile to throw IOException for invalid PDF");
    }

    // Helper method to create an in-memory PDF and return it as a MultipartFile
    private MultipartFile createPdfMultipartFile(String fileName, String content) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Add text content to the PDF
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 700);
                // Split content into lines to handle newlines
                String[] lines = content.split("\n");
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15); // Move down by 15 units for the next line
                }
                contentStream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);

            return new MockMultipartFile(
                    "file",
                    fileName,
                    "application/pdf",
                    outputStream.toByteArray()
            );
        }
    }
}
