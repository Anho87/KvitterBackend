package com.example.kvitter.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "retweets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Retweet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "original_kvitter_id", nullable = false)
    private Kvitter originalKvitter;
}
