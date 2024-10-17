package dev.riemer.lostandfound.fileprocessor;

import dev.riemer.lostandfound.model.LostItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Registers a PDF FileProcessor. Opens the PDF, gets all text and forward it to LostItemParser for further processing
 */
@Component
public class PdfFileProcessor implements FileProcessor {
    /**
     * This Processor supports PDFs.
     *
     * @param contentType MIME type of the file to parse
     * @return returns true if the processor can parse it
     */
    @Override
    public boolean supports(final String contentType) {
        return "application/pdf".equals(contentType);
    }

    /**
     * Processes a PDF file, returning the LostItems in a List.
     *
     * @param file the PDF to process
     * @return a List of LostItems
     * @throws IOException thrown when the file is invalid
     */
    @Override
    public List<LostItem> processFile(final MultipartFile file) throws IOException {
        String text = extractTextFromPdf(file);
        return LostItemParser.parseLostItemsFromText(text);
    }

    /**
     * Uses PDFBox to open the file and return all found text.
     *
     * @param file the PDF to process
     * @return all found text within the PDF
     * @throws IOException thrown when the file is invalid
     */
    private String extractTextFromPdf(final MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }
}
