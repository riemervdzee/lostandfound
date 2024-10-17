package dev.riemer.lostandfound.service;

import dev.riemer.lostandfound.model.LostItem;
import dev.riemer.lostandfound.model.LostItemClaim;
import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.repository.LostItemClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LostItemClaimServiceTest {

    @Mock
    private LostItemClaimRepository lostItemClaimRepository;

    @InjectMocks
    private LostItemClaimService lostItemClaimService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLostItemClaimSuccessful() {
        User user = new User();
        LostItem lostItem = new LostItem();
        int quantity = 1;

        when(lostItemClaimRepository.existsByUserAndLostItem(user, lostItem)).thenReturn(false);

        LostItemClaim claim = new LostItemClaim();
        claim.setUser(user);
        claim.setLostItem(lostItem);
        claim.setQuantity(quantity);

        when(lostItemClaimRepository.save(any(LostItemClaim.class))).thenReturn(claim);

        LostItemClaim result = lostItemClaimService.createLostItemClaim(user, lostItem, quantity);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(lostItem, result.getLostItem());
        assertEquals(quantity, result.getQuantity());

        verify(lostItemClaimRepository, times(1)).existsByUserAndLostItem(user, lostItem);
        verify(lostItemClaimRepository, times(1)).save(any(LostItemClaim.class));
    }

    @Test
    void testCreateLostItemClaimAlreadyExists() {
        User user = new User();
        LostItem lostItem = new LostItem();

        when(lostItemClaimRepository.existsByUserAndLostItem(user, lostItem)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                lostItemClaimService.createLostItemClaim(user, lostItem, 1)
        );

        assertEquals("A claim for this item by this user already exists.", exception.getMessage());

        verify(lostItemClaimRepository, times(1)).existsByUserAndLostItem(user, lostItem);
        verify(lostItemClaimRepository, never()).save(any(LostItemClaim.class));
    }
}
