package com.dathq.swd302.listingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.List;
@Entity
@Table(name = "wards", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "province_id"}))
@Data
@NoArgsConstructor
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID wardId;

    @Column(nullable = false)
    private String name;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
