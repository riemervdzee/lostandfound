package dev.riemer.lostandfound.fileprocessor;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Contains the Registry for the available FileProcessors and selects the right Processor for the right contentType.
 */
@Component
public class FileProcessorFactory {
    private final List<FileProcessor> processors;

    /**
     * The Constructor of the FileProcessorFactory.
     *
     * @param processors all registered FileProcessor
     */
    public FileProcessorFactory(final List<FileProcessor> processors) {
        this.processors = processors;
    }

    /**
     * Loops through all available FileProcessors and picks the first available which can process the requested
     * contentType.
     *
     * @param contentType the MIME contentType of the file to parse
     * @return a FileProcessor suitable to process the requested contentType file
     */
    public FileProcessor getProcessor(final String contentType) {
        return processors.stream()
                .filter(processor -> processor.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file type: " + contentType));
    }
}
