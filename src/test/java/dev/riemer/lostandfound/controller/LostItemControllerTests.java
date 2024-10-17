package dev.riemer.lostandfound.controller;

import dev.riemer.lostandfound.dto.LostItemDto;
import dev.riemer.lostandfound.dto.NewLostItemClaim;
import dev.riemer.lostandfound.model.LostItem;
import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.service.LostItemClaimService;
import dev.riemer.lostandfound.service.LostItemService;
import dev.riemer.lostandfound.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LostItemControllerTests {

    private LostItemController lostItemController;
    private LostItemService lostItemService;
    private UserService userService;
    private LostItemClaimService lostItemClaimService;

    @BeforeEach
    public void setUp() {
        lostItemService = mock(LostItemService.class);
        userService = mock(UserService.class);
        lostItemClaimService = mock(LostItemClaimService.class);
        lostItemController = new LostItemController(lostItemService, userService, lostItemClaimService);
    }

    @Test
    public void testGetLostItems() {
        // Prepare mock data
        LostItem item1 = new LostItem();
        item1.setItemName("Wallet");
        item1.setPlace("Lobby");
        item1.setQuantity(1);

        LostItem item2 = new LostItem();
        item2.setItemName("Umbrella");
        item2.setPlace("Entrance");
        item2.setQuantity(2);

        List<LostItem> lostItems = Arrays.asList(item1, item2);

        when(lostItemService.getAllLostItems()).thenReturn(lostItems);

        // Call the method under test
        ResponseEntity<List<LostItemDto>> response = lostItemController.getLostItems();

        // Verify
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        List<LostItemDto> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());

        List<String> itemNames = body.stream().map(LostItemDto::getItemName).toList();
        assertTrue(itemNames.contains("Wallet"));
        assertTrue(itemNames.contains("Umbrella"));

        verify(lostItemService, times(1)).getAllLostItems();
    }

    @Test
    public void testClaimLostItem_Success() {
        // Prepare mock data
        Long lostItemId = 1L;
        int quantity = 1;

        NewLostItemClaim newLostItemClaim = new NewLostItemClaim();
        newLostItemClaim.setLostItemId(lostItemId);
        newLostItemClaim.setQuantity(quantity);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        User user = new User();
        user.setUsername("testuser");

        LostItem lostItem = new LostItem();
        lostItem.setId(lostItemId);
        lostItem.setQuantity(5);

        when(lostItemService.findById(lostItemId)).thenReturn(Optional.of(lostItem));
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Call the method under test
        ResponseEntity<String> response = lostItemController.claimLostItem(newLostItemClaim, userDetails);

        // Verify
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Claimed", response.getBody());

        verify(lostItemService, times(1)).findById(lostItemId);
        verify(userService, times(1)).findByUsername("testuser");
        verify(lostItemClaimService, times(1)).createLostItemClaim(user, lostItem, quantity);
    }

    @Test
    public void testClaimLostItem_LostItemNotFound() {
        // Prepare mock data
        Long lostItemId = 1L;
        int quantity = 1;

        NewLostItemClaim newLostItemClaim = new NewLostItemClaim();
        newLostItemClaim.setLostItemId(lostItemId);
        newLostItemClaim.setQuantity(quantity);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(lostItemService.findById(lostItemId)).thenReturn(Optional.empty());

        // Call the method under test and expect an exception
        assertThrows(NoSuchElementException.class, () -> {
            lostItemController.claimLostItem(newLostItemClaim, userDetails);
        });

        // Verify
        verify(lostItemService, times(1)).findById(lostItemId);
        verify(userService, never()).findByUsername(anyString());
        verify(lostItemClaimService, never()).createLostItemClaim(any(), any(), anyInt());
    }

    @Test
    public void testClaimLostItem_UserNotFound() {
        // Prepare mock data
        Long lostItemId = 1L;
        int quantity = 1;

        NewLostItemClaim newLostItemClaim = new NewLostItemClaim();
        newLostItemClaim.setLostItemId(lostItemId);
        newLostItemClaim.setQuantity(quantity);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        LostItem lostItem = new LostItem();
        lostItem.setId(lostItemId);
        lostItem.setQuantity(5);

        when(lostItemService.findById(lostItemId)).thenReturn(Optional.of(lostItem));
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        // Call the method under test and expect an exception
        assertThrows(NoSuchElementException.class, () -> {
            lostItemController.claimLostItem(newLostItemClaim, userDetails);
        });

        // Verify
        verify(lostItemService, times(1)).findById(lostItemId);
        verify(userService, times(1)).findByUsername("testuser");
        verify(lostItemClaimService, never()).createLostItemClaim(any(), any(), anyInt());
    }
}
