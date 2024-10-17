package dev.riemer.lostandfound.dto;

import dev.riemer.lostandfound.model.LostItem;
import dev.riemer.lostandfound.model.LostItemClaim;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Special DTO variant for Admins. Extends LostItemDto but also includes all Claims and their user info
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LostItemAdminDto extends LostItemDto {
    private List<LostItemClaimDto> claims;

    /**
     * Constructs the DTO with an existing LostItem Entity.
     *
     * @param lostItem the existing LostItem Entity
     */
    public LostItemAdminDto(final LostItem lostItem) {
        super(lostItem);

        claims = new ArrayList<>();
        for (LostItemClaim lostItemClaim : lostItem.getClaims()) {
            claims.add(new LostItemClaimDto(lostItemClaim));
        }
    }

    /**
     * Subclass DTO for LostItemAdminDto, so we also return all available claims.
     */
    @Data
    public static class LostItemClaimDto {
        // We also store the Username to simplify the JSON
        private String username;
        private int quantity;

        /**
         * Constructs the DTO with an existing LostItemClaim Entity.
         *
         * @param claim the existing LostItemClaim Entity
         */
        public LostItemClaimDto(final LostItemClaim claim) {
            this.username = claim.getUser().getUsername();
            this.quantity = claim.getQuantity();
        }
    }
}
