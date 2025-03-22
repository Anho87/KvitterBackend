package com.example.kvitter.entities;

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
    
    @Column(unique = true)
    private String userName;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Kvitter> kvitterList = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reply> replies = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "follower_id"), 
            inverseJoinColumns = @JoinColumn(name = "following_id") 
    )
    private List<User> following = new ArrayList<>();
    
    @ManyToMany(mappedBy = "following", fetch = FetchType.EAGER)
    private List<User> followers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Retweet> retweets = new ArrayList<>();
    
    public User(String email, String password, String userName, List<Kvitter> kvitterList) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.kvitterList = kvitterList;
    }
}
