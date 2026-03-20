package com.uniovi.sdi.bookspace.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "space")
public class Space {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    private String location;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "space")
    private Set<Reservation> reservations;

    @OneToMany(mappedBy = "space")
    private Set<MaintenanceBlock> maintenanceBlocks;

    public Space() {
    }

    public Space(String name, String type, String location, int capacity, boolean active) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.capacity = capacity;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Set<MaintenanceBlock> getMaintenanceBlocks() {
        return maintenanceBlocks;
    }

    public void setMaintenanceBlocks(Set<MaintenanceBlock> maintenanceBlocks) {
        this.maintenanceBlocks = maintenanceBlocks;
    }
}