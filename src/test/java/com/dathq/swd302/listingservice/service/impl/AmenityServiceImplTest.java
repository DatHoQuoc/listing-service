package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.dto.response.AmenityResponse;
import com.dathq.swd302.listingservice.mapper.AmenityMapper;
import com.dathq.swd302.listingservice.model.Amenity;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.enums.AmenityCategory;
import com.dathq.swd302.listingservice.repository.AmenityRepository;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmenityServiceImplTest {

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private AmenityMapper amenityMapper;

    @InjectMocks
    private AmenityServiceImpl amenityService;

    private Amenity amenity;
    private AmenityResponse amenityResponse;
    private UUID amenityId;

    @BeforeEach
    void setUp() {
        amenityId = UUID.randomUUID();
        amenity = new Amenity();
        amenity.setAmenityId(amenityId);
        amenity.setAmenityName("Pool");
        amenity.setAmenityCategory(AmenityCategory.FACILITIES.name());

        amenityResponse = AmenityResponse.builder()
                .amenityId(amenityId)
                .amenityName("Pool")
                .amenityCategory(AmenityCategory.FACILITIES.name())
                .build();
    }

    @Test
    void getAllAmenities_ShouldReturnList() {
        // Arrange
        when(amenityRepository.findAllByOrderByAmenityCategoryAscAmenityNameAsc()).thenReturn(Collections.singletonList(amenity));
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.singletonList(amenityResponse));

        // Act
        List<AmenityResponse> result = amenityService.getAllAmenities();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmenityName()).isEqualTo("Pool");
        verify(amenityRepository).findAllByOrderByAmenityCategoryAscAmenityNameAsc();
    }

    @Test
    void getAmenityById_WhenExists_ShouldReturnResponse() {
        // Arrange
        when(amenityRepository.findById(amenityId)).thenReturn(Optional.of(amenity));
        when(amenityMapper.toResponse(amenity)).thenReturn(amenityResponse);

        // Act
        AmenityResponse result = amenityService.getAmenityById(amenityId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAmenityId()).isEqualTo(amenityId);
        verify(amenityRepository).findById(amenityId);
    }

    @Test
    void getAmenityById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(amenityRepository.findById(amenityId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> amenityService.getAmenityById(amenityId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Amenity not found");
    }

    @Test
    void getAmenitiesByCategory_WhenCategoryExists_ShouldReturnList() {
        // Arrange
        when(amenityRepository.findByAmenityCategoryOrderByAmenityName(AmenityCategory.FACILITIES.name()))
                .thenReturn(Collections.singletonList(amenity));
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.singletonList(amenityResponse));

        // Act
        List<AmenityResponse> result = amenityService.getAmenitiesByCategory(AmenityCategory.FACILITIES);

        // Assert
        assertThat(result).hasSize(1);
        verify(amenityRepository).findByAmenityCategoryOrderByAmenityName(AmenityCategory.FACILITIES.name());
    }

    @Test
    void getAmenitiesByCategory_WhenEmpty_ShouldReturnEmptyList() {
        // Arrange
        when(amenityRepository.findByAmenityCategoryOrderByAmenityName(anyString())).thenReturn(Collections.emptyList());
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.emptyList());

        // Act
        List<AmenityResponse> result = amenityService.getAmenitiesByCategory(AmenityCategory.SECURITY);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void searchAmenities_WhenKeywordMatches_ShouldReturnList() {
        // Arrange
        String keyword = "Pool";
        when(amenityRepository.findByAmenityNameContainingIgnoreCase(keyword)).thenReturn(Collections.singletonList(amenity));
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.singletonList(amenityResponse));

        // Act
        List<AmenityResponse> result = amenityService.searchAmenities(keyword);

        // Assert
        assertThat(result).hasSize(1);
        verify(amenityRepository).findByAmenityNameContainingIgnoreCase(keyword);
    }

    @Test
    void searchAmenities_WhenNoMatch_ShouldReturnEmptyList() {
        // Arrange
        String keyword = "Gym";
        when(amenityRepository.findByAmenityNameContainingIgnoreCase(keyword)).thenReturn(Collections.emptyList());
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.emptyList());

        // Act
        List<AmenityResponse> result = amenityService.searchAmenities(keyword);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getListingAmenities_WhenListingExists_ShouldReturnList() {
        // Arrange
        UUID listingId = UUID.randomUUID();
        Listing listing = new Listing();
        listing.setListingId(listingId);
        listing.setAmenities(Collections.singletonList(amenity));

        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.singletonList(amenityResponse));

        // Act
        List<AmenityResponse> result = amenityService.getListingAmenities(listingId);

        // Assert
        assertThat(result).hasSize(1);
        verify(listingRepository).findById(listingId);
    }

    @Test
    void getListingAmenities_WhenListingNotExists_ShouldThrowException() {
        // Arrange
        UUID listingId = UUID.randomUUID();
        when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> amenityService.getListingAmenities(listingId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Listing not found");
    }

    @Test
    void searchAmenities_WithEmptyKeyword_ShouldReturnList() {
        // Arrange
        String keyword = "";
        when(amenityRepository.findByAmenityNameContainingIgnoreCase(keyword)).thenReturn(Collections.singletonList(amenity));
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.singletonList(amenityResponse));

        // Act
        List<AmenityResponse> result = amenityService.searchAmenities(keyword);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void searchAmenities_WithLongKeyword_ShouldReturnEmpty() {
        // Arrange
        String keyword = "ThisIsAVeryLongKeywordThatShouldNotMatchAnything";
        when(amenityRepository.findByAmenityNameContainingIgnoreCase(keyword)).thenReturn(Collections.emptyList());
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.emptyList());

        // Act
        List<AmenityResponse> result = amenityService.searchAmenities(keyword);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getAmenitiesByCategory_NEARBY_ShouldReturnList() {
        // Arrange
        amenity.setAmenityCategory(AmenityCategory.NEARBY.name());
        when(amenityRepository.findByAmenityCategoryOrderByAmenityName(AmenityCategory.NEARBY.name()))
                .thenReturn(Collections.singletonList(amenity));
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.singletonList(amenityResponse));

        // Act
        List<AmenityResponse> result = amenityService.getAmenitiesByCategory(AmenityCategory.NEARBY);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void getAmenitiesByCategory_PARKING_ShouldReturnList() {
        // Arrange
        amenity.setAmenityCategory(AmenityCategory.PARKING.name());
        when(amenityRepository.findByAmenityCategoryOrderByAmenityName(AmenityCategory.PARKING.name()))
                .thenReturn(Collections.singletonList(amenity));
        when(amenityMapper.toResponseList(any())).thenReturn(Collections.singletonList(amenityResponse));

        // Act
        List<AmenityResponse> result = amenityService.getAmenitiesByCategory(AmenityCategory.PARKING);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void getAmenityById_WhenRepositoryThrows_ShouldPropagate() {
        // Arrange
        when(amenityRepository.findById(any())).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThatThrownBy(() -> amenityService.getAmenityById(amenityId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");
    }
}
