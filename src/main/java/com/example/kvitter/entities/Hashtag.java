package com.example.kvitter.entities;

import com.example.kvitter.entities.Kvitter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hashtag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String hashtag;

    private LocalDateTime createdDateAndTime;
    
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "kvitter_hashtags",
            joinColumns = @JoinColumn(name = "hashtag_id"),
            inverseJoinColumns = @JoinColumn(name = "kvitter_id")
    )
    private List<Kvitter> kvitters;

    public Hashtag(String hashtag) {
        this.hashtag = hashtag;
    }
    public Hashtag(String hashtag, LocalDateTime localDateTime) {
        this.hashtag = hashtag;
        this.createdDateAndTime = localDateTime;
    }
    
}
