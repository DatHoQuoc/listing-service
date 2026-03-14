package com.dathq.swd302.listingservice.mapper;

import com.dathq.swd302.listingservice.dto.response.AmenityResponse;
import com.dathq.swd302.listingservice.model.Amenity;
import com.dathq.swd302.listingservice.model.enums.AmenityCategory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AmenityMapperTest {

    private final AmenityMapper mapper = Mappers.getMapper(AmenityMapper.class);

    @Test
    void toResponse_ShouldMapAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        Amenity amenity = new Amenity();
        amenity.setAmenityId(id);
        amenity.setAmenityName("Pool");
        amenity.setAmenityCategory(AmenityCategory.FACILITIES.name());
        amenity.setIconUrl("pool.png");

        // Act
        AmenityResponse response = mapper.toResponse(amenity);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAmenityId()).isEqualTo(id);
        assertThat(response.getAmenityName()).isEqualTo("Pool");
        assertThat(response.getAmenityCategory()).isEqualTo(AmenityCategory.FACILITIES.name());
        assertThat(response.getIconUrl()).isEqualTo("pool.png");
    }

    @Test
    void toResponseList_ShouldMapCollection() {
        // Arrange
        Amenity a1 = new Amenity();
        a1.setAmenityName("Pool");
        Amenity a2 = new Amenity();
        a2.setAmenityName("Gym");

        // Act
        List<AmenityResponse> responses = mapper.toResponseList(Arrays.asList(a1, a2));

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getAmenityName()).isEqualTo("Pool");
        assertThat(responses.get(1).getAmenityName()).isEqualTo("Gym");
    }

    @Test
    void toResponse_WhenNull_ShouldReturnNull() {
        // Act
        AmenityResponse response = mapper.toResponse(null);

        // Assert
        assertThat(response).isNull();
    }

    @Test
    void toResponseList_WhenNull_ShouldReturnNull() {
        // Act
        List<AmenityResponse> responses = mapper.toResponseList(null);

        // Assert
        assertThat(responses).isNull();
    }
}
