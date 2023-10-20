package com.shohhet.servletapp.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    EventType type;
    @ManyToOne(targetEntity = UserEntity.class)
    Integer userId;
    @ManyToOne(targetEntity = FileEntity.class)
    Integer fileId;
}
