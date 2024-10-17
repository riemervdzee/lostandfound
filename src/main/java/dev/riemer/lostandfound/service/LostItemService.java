package dev.riemer.lostandfound.service;

import dev.riemer.lostandfound.fileprocessor.FileProcessor;
import dev.riemer.lostandfound.fileprocessor.FileProcessorFactory;
import dev.riemer.lostandfound.model.LostItem;
import dev.riemer.lostandfound.repository.LostItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service for LostItems; Providing a few getters and a way to bulk-import new LostItems.
 */
@Service
public class LostItemService {
    private final LostItemRepository repository;
    private final FileProcessorFactory fileProcessorFactory;

    /**
     * Constructor of LostItemService.
     *
     * @param lostItemRepository   Injected LostItemRepository
     * @param fileProcessorFactory Injected FileProcessorFactory
     */
    public LostItemService(
            final LostItemRepository lostItemRepository, final FileProcessorFactory fileProcessorFactory) {
        this.repository = lostItemRepository;
        this.fileProcessorFactory = fileProcessorFactory;
    }

    /**
     * Gets a LostItem by ID.
     *
     * @param id the ID of the LostItem requested
     * @return LostItem if found
     */
    public Optional<LostItem> findById(final Long id) {
        return repository.findById(id);
    }

    /**
     * Returns all current LostItems.
     *
     * @return the current LostItems
     */
    public List<LostItem> getAllLostItems() {
        return repository.findAll();
    }

    /**
     * Bulk-import new LostItem, requests the right FileProcessor to parse it and save all found LostItems.
     *
     * @param file the uploaded MultipartFile
     * @throws IOException exception when something went wrong
     */
    public void processLostItemsFile(final MultipartFile file) throws IOException {
        String contentType = file.getContentType();

        FileProcessor processor = fileProcessorFactory.getProcessor(contentType);
        List<LostItem> lostItems = processor.processFile(file);

        repository.saveAll(lostItems);
    }
}
