package com.dathq.swd302.listingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "provinces", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "country_id"}))
@Data
@NoArgsConstructor
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID provinceId;

    @Column(nullable = false)
    private String name;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
