package com.uniovi.sdi.bookspace.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String dni;
    private String name;
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Transient
    private String passwordConfirm;

    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Reservation> reservations;

    public User() {
    }

    public User(String dni, String name, String lastName, String password, String role) {
        this.dni = dni;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserRole() {
        return role;
    }

    public void setUserRole(String role) {
        this.role = role;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }
}