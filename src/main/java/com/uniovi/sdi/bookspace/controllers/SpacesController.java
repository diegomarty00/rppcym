package com.uniovi.sdi.bookspace.controllers;

import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.entities.SpaceType;
import com.uniovi.sdi.bookspace.services.AvailabilityService;
import com.uniovi.sdi.bookspace.services.MaintenanceBlocksService;
import com.uniovi.sdi.bookspace.services.SpacesService;
import com.uniovi.sdi.bookspace.validators.AddMaintenanceBlockValidator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/space")
public class SpacesController {
    private final SpacesService spacesService;
    private final AvailabilityService availabilityService;
    private final MaintenanceBlocksService maintenanceBlocksService;
    private final AddMaintenanceBlockValidator addMaintenanceBlockValidator;

    public SpacesController(SpacesService spacesService, AvailabilityService availabilityService,
                            MaintenanceBlocksService maintenanceBlocksService,
                            AddMaintenanceBlockValidator addMaintenanceBlockValidator) {
        this.spacesService = spacesService;
        this.availabilityService = availabilityService;
        this.maintenanceBlocksService = maintenanceBlocksService;
        this.addMaintenanceBlockValidator = addMaintenanceBlockValidator;
    }


    @GetMapping
    public String listSpaces(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minCapacity,
            Model model
    ) {
        model.addAttribute("spaces", spacesService.getVisibleSpaces(type, minCapacity));
        model.addAttribute("type", type);
        model.addAttribute("minCapacity", minCapacity);
        return "space/list";

    }

    @GetMapping("/list")
    public String listSpacesAlias(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minCapacity,
            Model model
    ) {
        return listSpaces(type, minCapacity, model);

    }

    @GetMapping("/list/update")
    public String updateSpacesList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minCapacity,
            Model model
    ) {
        model.addAttribute("spaces", spacesService.getActiveSpaces(type, minCapacity));
        return "fragments/spacesTable :: spacesTable";
    }

    @GetMapping("/details/{id}")
    public String spaceDetails(@PathVariable Long id, Model model) {
        Space space = getActiveSpace(id);
        if (space == null) {
            return "redirect:/space";
        }
        model.addAttribute("space", space);
        return "space/details";
    }

    @GetMapping("/{id}/availability")
    public String spaceAvailability(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Model model
    ) {
        Space space = getActiveSpace(id);
        if (space == null) {
            return "redirect:/space";
        }
        model.addAttribute("space", space);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("allBlocks", maintenanceBlocksService.getBlocksForSpace(space));
        if (isValidRange(from, to)) {
            List<Reservation> reservations = availabilityService.getActiveReservationsInRange(space, from, to);
            List<MaintenanceBlock> blocks = availabilityService.getActiveBlocksInRange(space, from, to);
            model.addAttribute("reservations", reservations);
            model.addAttribute("blocks", blocks);
        }
        return "space/availability";
    }

    @GetMapping("/{id}/availability/reservations/update")
    public String updateAvailabilityReservations(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Model model
    ) {
        Space space = getActiveSpace(id);
        if (space == null) {
            return "fragments/availabilityReservationsTable :: availabilityReservationsTable";
        }
        if (isValidRange(from, to)) {
            model.addAttribute("reservations", availabilityService.getActiveReservationsInRange(space, from, to));
        }
        return "fragments/availabilityReservationsTable :: availabilityReservationsTable";
    }

    @GetMapping("/{id}/availability/blocks/update")
    public String updateAvailabilityBlocks(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Model model
    ) {
        Space space = getActiveSpace(id);
        if (space == null) {
            return "fragments/availabilityBlocksTable :: availabilityBlocksTable";
        }
        if (isValidRange(from, to)) {
            model.addAttribute("blocks", availabilityService.getActiveBlocksInRange(space, from, to));
        }
        return "fragments/availabilityBlocksTable :: availabilityBlocksTable";
    }

    @GetMapping("/{id}/blocks/add")
    public String addBlockForm(@PathVariable Long id, Model model) {
        Space space = getActiveSpace(id);
        if (space == null) {
            return "redirect:/space";
        }
        model.addAttribute("space", space);
        model.addAttribute("block", new MaintenanceBlock());
        return "space/addBlock";
    }

    @PostMapping("/{id}/blocks/add")
    public String addBlockSubmit(@PathVariable Long id,
                                 @ModelAttribute("block") MaintenanceBlock block,
                                 BindingResult result,
                                 Model model) {
        Space space = getActiveSpace(id);
        if (space == null) {
            return "redirect:/space";
        }

        block.setId(null);
        block.setSpace(space);
        addMaintenanceBlockValidator.validate(block, result);
        if (result.hasErrors()) {
            model.addAttribute("space", space);
            return "space/addBlock";
        }
        maintenanceBlocksService.addBlock(block);
        return "redirect:/space/" + id + "/availability";
    }

    @PostMapping("/blocks/{blockId}/cancel")
    public String cancelBlock(@PathVariable Long blockId) {
        MaintenanceBlock block = maintenanceBlocksService.getBlock(blockId);
        if (block == null) {
            return "redirect:/space";
        }
        Long spaceId = block.getSpace().getId();
        maintenanceBlocksService.cancelBlock(blockId);
        return "redirect:/space/" + spaceId + "/availability";
    }

    @GetMapping("/add")
    public String addSpace(Model model) {
        model.addAttribute("space", new Space());
        model.addAttribute("spaceTypes", SpaceType.values());
        return "space/add";
    }

    @PostMapping("/add")
    public String addSpaceSubmit(@Validated @ModelAttribute("space") Space space,
                                 BindingResult result,
                                 Model model) {

        if (result.hasErrors()) {
            model.addAttribute("spaceTypes", SpaceType.values());
            return "space/add";
        }

        try {
            spacesService.addSpace(space);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("spaceTypes", SpaceType.values());
            return "space/add";
        }
        return "redirect:/space";
    }

    @PostMapping("/delete/{id}")
    public String deleteSpace(@PathVariable Long id) {
        Space space = spacesService.getSpace(id);
        if (space != null) {
            space.setActive(false);
            spacesService.addSpace(space);
        }
        return "redirect:/space";
    }

    @GetMapping("/edit/{id}")
    public String editSpace(Model model, @PathVariable Long id) {
        model.addAttribute("space", spacesService.getSpace(id));
        model.addAttribute("spaceTypes", SpaceType.values());
        return "space/edit";
    }

    @PostMapping(value = "/edit/{id}")
    public String setEdit(@ModelAttribute Space space, @PathVariable Long id){
        Space originalSpace = spacesService.getSpace(id);
        if (originalSpace == null) {
            return "redirect:/space";
        }
        originalSpace.setLocation(space.getLocation());
        originalSpace.setCapacity(space.getCapacity());
        originalSpace.setType(space.getType());
        originalSpace.setName(space.getName());
        spacesService.addSpace(originalSpace);
        return "redirect:/space/details/" + id;
    }

    @PostMapping("/toggle/{id}")
    public String toggleSpaceStatus(@PathVariable Long id) {
        Space space = spacesService.getSpace(id);
        if (space != null) {
            space.setActive(!space.isActive());
            spacesService.addSpace(space);
        }
        return "redirect:/space";
    }

    private Space getActiveSpace(Long id) {
        Space space = spacesService.getSpace(id);
        if (space == null || !space.isActive()) {
            return null;
        }
        return space;
    }

    private boolean isValidRange(LocalDateTime from, LocalDateTime to) {
        return from != null && to != null && from.isBefore(to);
    }
}
