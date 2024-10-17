package dev.riemer.lostandfound.repository;

import dev.riemer.lostandfound.model.LostItem;
import dev.riemer.lostandfound.model.LostItemClaim;
import dev.riemer.lostandfound.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for getting LostItemClaim Entities.
 */
@Repository
public interface LostItemClaimRepository extends JpaRepository<LostItemClaim, Long> {
    /**
     * Check if a claim already exists on the LostItem by User.
     *
     * @param user     which is making the claim
     * @param lostItem the LostItem which is claimed
     * @return true if there is a LostItemClaim already for this User+LostItem combination
     */
    boolean existsByUserAndLostItem(User user, LostItem lostItem);
}
