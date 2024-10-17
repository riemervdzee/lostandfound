package dev.riemer.lostandfound.repository;

import dev.riemer.lostandfound.model.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for getting LostItem Entities.
 */
@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {
}
