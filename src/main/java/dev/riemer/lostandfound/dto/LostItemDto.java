package dev.riemer.lostandfound.dto;

import dev.riemer.lostandfound.model.LostItem;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple DTO for a single LostItem.
 */
@Data
@NoArgsConstructor
public class LostItemDto {
    private Long id;
    private String itemName;
    private int quantity;
    private String place;

    /**
     * Constructs the DTO with an existing LostItem Entity.
     *
     * @param lostItem the existing LostItem Entity
     */
    public LostItemDto(final LostItem lostItem) {
        this.id = lostItem.getId();
        this.itemName = lostItem.getItemName();
        this.quantity = lostItem.getQuantity();
        this.place = lostItem.getPlace();
    }
}
