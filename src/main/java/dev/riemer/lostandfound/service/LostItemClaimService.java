package dev.riemer.lostandfound.service;

import dev.riemer.lostandfound.model.LostItem;
import dev.riemer.lostandfound.model.LostItemClaim;
import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.repository.LostItemClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for helping with Claiming a LostItem.
 */
@Service
public class LostItemClaimService {
    private final LostItemClaimRepository lostItemClaimRepository;

    /**
     * Constructor of LostItemClaimService.
     *
     * @param lostItemClaimRepository Injected LostItemClaimRepository
     */
    public LostItemClaimService(final LostItemClaimRepository lostItemClaimRepository) {
        this.lostItemClaimRepository = lostItemClaimRepository;
    }

    /**
     * Creates a new LostItemClaim Entity for the given user, lostItem and quantity. As a User can claim a LostItem only
     * once, we check for duplication and a SQL Transaction is used
     *
     * @param user     the user which is making the claim
     * @param lostItem the LostItem which is claimed
     * @param quantity the quantity claimed
     * @return The new LostItemClaim Entity
     */
    @Transactional
    public LostItemClaim createLostItemClaim(final User user, final LostItem lostItem, final int quantity) {
        boolean exists = lostItemClaimRepository.existsByUserAndLostItem(user, lostItem);
        if (exists) {
            throw new IllegalArgumentException("A claim for this item by this user already exists.");
        }

        // Create and save the new claim
        LostItemClaim claim = new LostItemClaim();
        claim.setUser(user);
        claim.setLostItem(lostItem);
        claim.setQuantity(quantity);

        return lostItemClaimRepository.save(claim);
    }
}
