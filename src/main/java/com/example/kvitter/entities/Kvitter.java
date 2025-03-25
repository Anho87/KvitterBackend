package com.example.kvitter.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "kvitter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kvitter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(length = 280, nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private LocalDateTime createdDateAndTime;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "kvitter_hashtags",
            joinColumns = @JoinColumn(name = "kvitter_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> hashtags = new ArrayList<>();

    @JoinColumn(name = "is_private", nullable = false)
    private Boolean isPrivate;


    @OneToMany(mappedBy = "kvitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "kvitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "originalKvitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rekvitt> rekvitts = new ArrayList<>();

    public Kvitter(String message, User user, LocalDateTime createdDateAndTime, List<Hashtag> hashtags, boolean isPrivate) {
        this.message = message;
        this.user = user;
        this.createdDateAndTime = createdDateAndTime;
        this.hashtags = hashtags;
        this.isPrivate = isPrivate;
    }
}
