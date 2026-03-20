package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.BlockStatus;
import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.repositories.MaintenanceBlockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceBlocksService {

    private final MaintenanceBlockRepository maintenanceBlockRepository;

    public MaintenanceBlocksService(MaintenanceBlockRepository maintenanceBlockRepository) {
        this.maintenanceBlockRepository = maintenanceBlockRepository;
    }

    public MaintenanceBlock getBlock(Long id) {
        return maintenanceBlockRepository.findById(id).orElse(null);
    }

    public List<MaintenanceBlock> getBlocksForSpace(Space space) {
        return maintenanceBlockRepository.findBySpaceOrderByStartDateTimeAsc(space);
    }

    public void addBlock(MaintenanceBlock block) {
        block.setReason(normalizeReason(block.getReason()));
        block.setStatus(BlockStatus.ACTIVE);
        maintenanceBlockRepository.save(block);
    }

    public void cancelBlock(Long blockId) {
        MaintenanceBlock block = maintenanceBlockRepository.findById(blockId).orElse(null);
        if (block == null) {
            throw new IllegalArgumentException("El bloqueo no existe.");
        }
        if (block.getStatus() == BlockStatus.CANCELLED) {
            return;
        }
        block.setStatus(BlockStatus.CANCELLED);
        maintenanceBlockRepository.save(block);
    }

    public List<MaintenanceBlock> getActiveBlocksInRange(Space space, LocalDateTime from, LocalDateTime to) {
        return maintenanceBlockRepository.findBySpaceAndStatusAndEndDateTimeAfterAndStartDateTimeBefore(
                space,
                BlockStatus.ACTIVE,
                from,
                to
        );
    }

    private String normalizeReason(String reason) {
        if (reason == null) {
            return null;
        }
        String trimmed = reason.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
