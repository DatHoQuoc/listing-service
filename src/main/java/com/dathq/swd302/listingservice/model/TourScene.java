package com.dathq.swd302.listingservice.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "tour_scenes")
@Data
@NoArgsConstructor
public class TourScene {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sceneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private VirtualTour virtualTour;

    @Column(nullable = false)
    private String sceneName;

    @Column(nullable = false)
    private String panoramaUrl;

    @Column(nullable = false)
    private int sceneOrder;

    // 3D Position
    private Double positionX;
    private Double positionY;
    private Double positionZ;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode hotspotsJson;

    @CreationTimestamp
    private Instant createdAt;
}
