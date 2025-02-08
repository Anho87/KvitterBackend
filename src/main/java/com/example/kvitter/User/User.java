package com.example.kvitter.User;

import com.example.kvitter.Kvitter.Kvitter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"users\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "email", unique = true)
    private String email;
    private String password;
    
    private String firstName;
    private String lastName;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Kvitter> kvitterList = new ArrayList<>();

    public User(UUID id, String email, String firstName, String lastName, List<Kvitter> kvitterList) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.kvitterList = kvitterList;
    }

    public User(String email, String password, String firstName, String lastName, List<Kvitter> kvitterList) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.kvitterList = kvitterList;
    }
}
