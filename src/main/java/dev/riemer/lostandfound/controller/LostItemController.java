package dev.riemer.lostandfound.controller;

import dev.riemer.lostandfound.dto.LostItemDto;
import dev.riemer.lostandfound.dto.NewLostItemClaim;
import dev.riemer.lostandfound.service.LostItemClaimService;
import dev.riemer.lostandfound.service.LostItemService;
import dev.riemer.lostandfound.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * LostItemController; allows normal users to list all current LostItems and claim them.
 */
@RequestMapping("/lost-items")
@RestController
public class LostItemController {
    private final LostItemService lostItemService;
    private final UserService userService;
    private final LostItemClaimService lostItemClaimService;

    /**
     * Constructor of LostItemController.
     *
     * @param lostItemService      injected LostItemService
     * @param userService          injected UserService
     * @param lostItemClaimService injected LostItemClaimService
     */
    public LostItemController(
            final LostItemService lostItemService,
            final UserService userService,
            final LostItemClaimService lostItemClaimService
    ) {
        this.lostItemService = lostItemService;
        this.userService = userService;
        this.lostItemClaimService = lostItemClaimService;
    }

    /**
     * Get endpoint which returns all available LostItems with their name, place and quantity.
     *
     * @return the available LostItems
     */
    @GetMapping
    public ResponseEntity<List<LostItemDto>> getLostItems() {
        var lostItems = lostItemService.getAllLostItems();
        var lostItemDTOs = lostItems.stream().map(LostItemDto::new).collect(Collectors.toList());

        return ResponseEntity.ok(lostItemDTOs);

    }

    /**
     * Allows the user to claim a single LostItem.
     *
     * @param newLostItemClaim Claim object with the LostItem id and quantity
     * @param userDetails      user details, used for linking
     * @return if the claim is successfully made, a simple string of "Claimed" is returned
     */
    @PutMapping("claim")
    public ResponseEntity<String> claimLostItem(
            final @Valid @RequestBody NewLostItemClaim newLostItemClaim,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        var lostItem = lostItemService.findById(newLostItemClaim.getLostItemId()).orElseThrow();
        var user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        lostItemClaimService.createLostItemClaim(user, lostItem, newLostItemClaim.getQuantity());

        return ResponseEntity.ok("Claimed");
    }
}
