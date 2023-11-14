package com.shohhet.servletapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Data
@EqualsAndHashCode(exclude = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<EventEntity> events;
}
