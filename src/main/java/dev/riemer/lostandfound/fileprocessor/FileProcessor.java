package dev.riemer.lostandfound.fileprocessor;

import dev.riemer.lostandfound.model.LostItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Interface for FileProcessors. By extending this interface the class will get registered automatically in the factory.
 */
public interface FileProcessor {
    /**
     * Implement this to tell the factory whether you support the requested content-type.
     *
     * @param contentType MIME type of the file to parse
     * @return return true if this Processor can parse this contentType file
     */
    boolean supports(String contentType);

    /**
     * Process the MultipartFile file and convert it into multiple LostItems.
     *
     * @param file the MultipartFile to process
     * @return a list of LostItems
     * @throws IOException error in processing
     */
    List<LostItem> processFile(MultipartFile file) throws IOException;
}
