package com.app.oauth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity  // Tells JPA this is a database entity (like a MongoDB schema)
@Table(name = "users") //– Maps to the users table in PostgreSQL
@Data
@NoArgsConstructor
@AllArgsConstructor //– Lombok generates getters/setters/constructors automatically
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String username;

    @Column(nullable = false)
    private  String email;

    @Column(nullable = false)
    private String password;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void  onCreate(){
        createdAt = LocalDateTime.now();
    }
}
