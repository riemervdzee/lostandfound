package dev.riemer.lostandfound.fileprocessor;

import dev.riemer.lostandfound.model.LostItem;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Registers a simple text FileProcessor. Read the file as UTF-8 and forward it to LostItemParser for further processing
 */
@Component
public class TextFileProcessor implements FileProcessor {
    /**
     * This Processor supports plain text files.
     *
     * @param contentType MIME type of the file to parse
     * @return returns true if the processor can parse it
     */
    @Override
    public boolean supports(final String contentType) {
        return "text/plain".equals(contentType);
    }

    /**
     * Processes a plan text file, returning the LostItems in a List.
     *
     * @param file the MultipartFile to process
     * @return a List of LostItems
     * @throws IOException thrown when the file is invalid
     */
    @Override
    public List<LostItem> processFile(final MultipartFile file) throws IOException {
        String text = new String(file.getBytes(), StandardCharsets.UTF_8);
        return LostItemParser.parseLostItemsFromText(text);
    }
}
