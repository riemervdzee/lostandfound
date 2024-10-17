package dev.riemer.lostandfound.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Input DTO for claiming a LostItem.
 */
@Data
public class NewLostItemClaim {
    @NotNull(message = "LostItem ID must not be null.")
    private Long lostItemId;

    @NotNull(message = "Quantity must not be null.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;
}
