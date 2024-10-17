package dev.riemer.lostandfound.service;

import dev.riemer.lostandfound.fileprocessor.FileProcessor;
import dev.riemer.lostandfound.fileprocessor.FileProcessorFactory;
import dev.riemer.lostandfound.model.LostItem;
import dev.riemer.lostandfound.repository.LostItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LostItemServiceTests {

    @Mock
    private LostItemRepository lostItemRepository;

    @Mock
    private FileProcessorFactory fileProcessorFactory;

    @Mock
    private MultipartFile file;

    @Mock
    private FileProcessor fileProcessor;

    @InjectMocks
    private LostItemService lostItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        LostItem lostItem = new LostItem();
        when(lostItemRepository.findById(id)).thenReturn(Optional.of(lostItem));

        Optional<LostItem> result = lostItemService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(lostItem, result.get());
        verify(lostItemRepository, times(1)).findById(id);
    }

    @Test
    void testGetAllLostItems() {
        LostItem lostItem1 = new LostItem();
        LostItem lostItem2 = new LostItem();
        List<LostItem> lostItems = Arrays.asList(lostItem1, lostItem2);
        when(lostItemRepository.findAll()).thenReturn(lostItems);

        List<LostItem> result = lostItemService.getAllLostItems();

        assertEquals(2, result.size());
        assertEquals(lostItems, result);
        verify(lostItemRepository, times(1)).findAll();
    }

    @Test
    void testProcessLostItemsFile() throws IOException {
        // Use an unsupported ContentType, so we know for sure everything gets mocked properly
        String contentType = "text/csv";
        LostItem lostItem1 = new LostItem();
        LostItem lostItem2 = new LostItem();
        List<LostItem> lostItems = Arrays.asList(lostItem1, lostItem2);

        when(file.getContentType()).thenReturn(contentType);
        when(fileProcessorFactory.getProcessor(contentType)).thenReturn(fileProcessor);
        when(fileProcessor.processFile(file)).thenReturn(lostItems);

        lostItemService.processLostItemsFile(file);

        verify(fileProcessorFactory, times(1)).getProcessor(contentType);
        verify(fileProcessor, times(1)).processFile(file);
        verify(lostItemRepository, times(1)).saveAll(lostItems);
    }

    @Test
    void testProcessLostItemsFileThrowsIOException() throws IOException {
        // Use an unsupported ContentType, so we know for sure everything gets mocked properly
        String contentType = "text/csv";

        when(file.getContentType()).thenReturn(contentType);
        when(fileProcessorFactory.getProcessor(contentType)).thenReturn(fileProcessor);
        when(fileProcessor.processFile(file)).thenThrow(new IOException());

        assertThrows(IOException.class, () -> lostItemService.processLostItemsFile(file));

        verify(fileProcessorFactory, times(1)).getProcessor(contentType);
        verify(fileProcessor, times(1)).processFile(file);
        verify(lostItemRepository, never()).saveAll(anyList());
    }
}
