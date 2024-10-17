package dev.riemer.lostandfound.controller;

import dev.riemer.lostandfound.dto.LostItemAdminDto;
import dev.riemer.lostandfound.dto.UserInfoDto;
import dev.riemer.lostandfound.model.LostItem;
import dev.riemer.lostandfound.model.LostItemClaim;
import dev.riemer.lostandfound.model.Role;
import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.service.LostItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminControllerTests {

    private AdminController adminController;
    private LostItemService lostItemService;

    @BeforeEach
    public void setUp() {
        lostItemService = mock(LostItemService.class);
        adminController = new AdminController(lostItemService);
    }

    @Test
    public void testGetLostItemClaims() {
        // Prepare mock data
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);

        LostItemClaim lostItemClaim1 = new LostItemClaim();
        lostItemClaim1.setId(1L);
        lostItemClaim1.setUser(user);

        LostItem item1 = new LostItem();
        item1.setId(1L);
        item1.setItemName("Wallet");
        item1.setPlace("Lobby");
        item1.setQuantity(1);
        item1.setClaims(List.of(lostItemClaim1));

        LostItem item2 = new LostItem();
        item2.setId(2L);
        item2.setItemName("Umbrella");
        item2.setPlace("Entrance");
        item2.setQuantity(2);
        item2.setClaims(List.of(lostItemClaim1));

        List<LostItem> lostItems = Arrays.asList(item1, item2);

        when(lostItemService.getAllLostItems()).thenReturn(lostItems);

        // Call the method under test
        ResponseEntity<List<LostItemAdminDto>> response = adminController.getLostItemClaims();

        // Verify
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<LostItemAdminDto> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());

        List<Long> itemIds = body.stream().map(LostItemAdminDto::getId).toList();
        assertTrue(itemIds.contains(1L));
        assertTrue(itemIds.contains(2L));

        verify(lostItemService, times(1)).getAllLostItems();
    }

    @Test
    public void testUploadLostItemsFile_Success() throws IOException {
        // Prepare mock file
        String content = "ItemName: Wallet\nQuantity: 1\nPlace: Lobby";
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content.getBytes()
        );

        // Call the method under test
        ResponseEntity<?> response = adminController.uploadLostItemsFile(multipartFile);

        // Verify
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File processed successfully.", response.getBody());

        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(lostItemService, times(1)).processLostItemsFile(fileCaptor.capture());
        assertEquals(multipartFile, fileCaptor.getValue());
    }

    @Test
    public void testUploadLostItemsFile_IOException() throws IOException {
        // Prepare mock file
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[0]
        );

        // Mock behavior to throw IOException
        doThrow(new IOException("File read error")).when(lostItemService).processLostItemsFile(multipartFile);

        // Call the method under test and expect an exception
        IOException exception = assertThrows(IOException.class, () -> {
            adminController.uploadLostItemsFile(multipartFile);
        });

        assertEquals("File read error", exception.getMessage());

        verify(lostItemService, times(1)).processLostItemsFile(multipartFile);
    }

    @Test
    public void testGetUserInfo() {
        // Prepare test data
        String username = "johndoe";

        // Call the method under test
        ResponseEntity<UserInfoDto> response = adminController.getUserInfo(username);

        // Verify
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserInfoDto userInfo = response.getBody();
        assertNotNull(userInfo);
        assertEquals(username, userInfo.getUsername());
        assertEquals("John", userInfo.getFirstName());
        assertEquals("Doe", userInfo.getLastName());
        assertEquals("123 Main Street", userInfo.getAddress());
        assertEquals("Leeuwarden", userInfo.getCity());
        assertEquals("4422AD", userInfo.getPostalCode());
        assertEquals("NL", userInfo.getCountry());
        assertEquals("+316 22334455", userInfo.getTelephone());
    }
}
