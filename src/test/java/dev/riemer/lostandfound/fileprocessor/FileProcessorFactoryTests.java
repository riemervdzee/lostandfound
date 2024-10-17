package dev.riemer.lostandfound.fileprocessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileProcessorFactoryTests {

    @Mock
    private FileProcessor fileProcessor1;

    @Mock
    private FileProcessor fileProcessor2;

    private List<FileProcessor> processors;

    @InjectMocks
    private FileProcessorFactory fileProcessorFactory;

    @BeforeEach
    public void setUp() {
        processors = Arrays.asList(fileProcessor1, fileProcessor2);
        fileProcessorFactory = new FileProcessorFactory(processors);
    }

    @Test
    public void testGetProcessor_SupportedContentType() {
        String contentType = "application/pdf";

        when(fileProcessor1.supports(contentType)).thenReturn(false);
        when(fileProcessor2.supports(contentType)).thenReturn(true);

        FileProcessor result = fileProcessorFactory.getProcessor(contentType);

        assertNotNull(result, "Processor should not be null");
        assertEquals(fileProcessor2, result, "Should return the processor that supports the content type");
    }

    @Test
    public void testGetProcessor_UnsupportedContentType() {
        String contentType = "application/unknown";

        when(fileProcessor1.supports(contentType)).thenReturn(false);
        when(fileProcessor2.supports(contentType)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fileProcessorFactory.getProcessor(contentType),
                "Expected getProcessor to throw, but it didn't"
        );

        String expectedMessage = "Unsupported file type: " + contentType;
        assertTrue(
                exception.getMessage().contains(expectedMessage),
                "Exception message should contain the unsupported content type"
        );
    }
}
