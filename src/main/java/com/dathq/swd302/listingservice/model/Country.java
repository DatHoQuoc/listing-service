package com.dathq.swd302.listingservice.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID countryId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true, length = 3)
    private String code;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<Province> provinces;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
