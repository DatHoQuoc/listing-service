package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.dto.request.CreateListingRequest;
import com.dathq.swd302.listingservice.dto.request.LockCreditRequest;
import com.dathq.swd302.listingservice.dto.request.UpdateListingRequest;
import com.dathq.swd302.listingservice.dto.response.CreditLockResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.exception.InvalidListingStateException;
import com.dathq.swd302.listingservice.exception.ListingNotFoundException;
import com.dathq.swd302.listingservice.exception.UnauthorizedException;
import com.dathq.swd302.listingservice.mapper.ListingMapper;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.repository.AmenityRepository;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.repository.VirtualTourRepository;
import com.dathq.swd302.listingservice.repository.WardRepository;
import com.dathq.swd302.listingservice.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceImplTest {

    @Mock
    private ListingRepository listingRepository;
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private WardRepository wardRepository;
    @Mock
    private ListingMapper listingMapper;
    @Mock
    private ListingValidationService listingValidationService;
    @Mock
    private ImageService imageService;
    @Mock
    private DocumentService documentService;
    @Mock
    private MinIOStorageService minIOStorageService;
    @Mock
    private AIAnalysisProducerService aiAnalysisProducerService;
    @Mock
    private CreditServiceClient creditServiceClient;
    @Mock
    private VirtualTourRepository virtualTourRepository;

    @InjectMocks
    private ListingServiceImpl listingService;

    private UUID userId;
    private UUID listingId;
    private Listing listing;
    private ListingResponse listingResponse;
    private CreateListingRequest createRequest;
    private UpdateListingRequest updateRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        listingId = UUID.randomUUID();

        listing = new Listing();
        listing.setListingId(listingId);
        listing.setUserId(userId);
        listing.setStatus(ListingStatus.DRAFT);

        listingResponse = ListingResponse.builder()
                .listingId(listingId)
                .userId(userId)
                .status(ListingStatus.DRAFT)
                .build();

        createRequest = CreateListingRequest.builder()
                .title("Sample Listing")
                .description("Sample Description")
                .latitude(10.0)
                .longitude(106.0)
                .build();

        updateRequest = UpdateListingRequest.builder()
                .title("Updated Title")
                .build();
    }

    @Test
    void createDraft_ShouldReturnListingResponse() {
        // Arrange
        when(listingRepository.countByUserIdAndStatusIn(any(), any())).thenReturn(0L);
        when(listingMapper.toEntity(any())).thenReturn(listing);
        when(listingRepository.save(any())).thenReturn(listing);
        when(listingMapper.toResponse(any())).thenReturn(listingResponse);

        // Act
        ListingResponse result = listingService.createDraft(userId, createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getListingId()).isEqualTo(listingId);
        verify(listingRepository).save(any(Listing.class));
    }

    @Test
    void submitListing_ShouldUpdateStatusAndReturnResponse() {
        // Arrange
        String authHeader = "Bearer token";
        listing.setStatus(ListingStatus.DRAFT);

        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));
        when(virtualTourRepository.findByListing_ListingId(listingId)).thenReturn(Optional.empty());
        
        CreditLockResponse lockResponse = new CreditLockResponse(true, true, 0, "ref123", "Success");
        when(creditServiceClient.lockCreditForPost(eq(authHeader), any(LockCreditRequest.class)))
                .thenReturn(lockResponse);
        
        when(listingRepository.save(any())).thenReturn(listing);
        when(listingMapper.toResponse(any())).thenReturn(listingResponse);

        // Act
        listingService.submitListing(userId, listingId, authHeader);

        // Assert
        assertThat(listing.getStatus()).isEqualTo(ListingStatus.PENDING_REVIEW);
        verify(creditServiceClient).lockCreditForPost(eq(authHeader), any(LockCreditRequest.class));
        verify(aiAnalysisProducerService).sendComprehensiveAnalysisRequest(eq(userId), any());
    }

    @Test
    void updateListing_WhenOwnerAndDraft_ShouldUpdate() {
        // Arrange
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any())).thenReturn(listing);
        when(listingMapper.toResponse(any())).thenReturn(listingResponse);

        // Act
        ListingResponse result = listingService.updateListing(userId, listingId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(listingMapper).updateEntityFromRequest(eq(updateRequest), eq(listing));
        verify(listingRepository).save(listing);
    }

    @Test
    void updateListing_WhenNotOwner_ShouldThrowException() {
        // Arrange
        listing.setUserId(UUID.randomUUID());
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));

        // Act & Assert
        assertThatThrownBy(() -> listingService.updateListing(userId, listingId, updateRequest))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void updateListing_WhenNotDraft_ShouldThrowException() {
        // Arrange
        listing.setStatus(ListingStatus.PUBLISHED);
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));

        // Act & Assert
        assertThatThrownBy(() -> listingService.updateListing(userId, listingId, updateRequest))
                .isInstanceOf(InvalidListingStateException.class);
    }

    @Test
    void cancelSubmission_WhenPendingReview_ShouldRevertToDraft() {
        // Arrange
        listing.setStatus(ListingStatus.PENDING_REVIEW);
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any())).thenReturn(listing);
        when(listingMapper.toResponse(any())).thenReturn(listingResponse);

        // Act
        listingService.cancelSubmission(userId, listingId);

        // Assert
        assertThat(listing.getStatus()).isEqualTo(ListingStatus.DRAFT);
        verify(listingRepository).save(listing);
    }

    @Test
    void cancelSubmission_WhenNotPendingReview_ShouldThrowException() {
        // Arrange
        listing.setStatus(ListingStatus.DRAFT);
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));

        // Act & Assert
        assertThatThrownBy(() -> listingService.cancelSubmission(userId, listingId))
                .isInstanceOf(InvalidListingStateException.class);
    }

    @Test
    void deleteListing_WhenDraft_ShouldSetStatusDeleted() {
        // Arrange
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));

        // Act
        listingService.deleteListing(userId, listingId);

        // Assert
        assertThat(listing.getStatus()).isEqualTo(ListingStatus.DELETED);
        verify(listingRepository).save(listing);
    }

    @Test
    void deleteListing_WhenNotDraft_ShouldThrowException() {
        // Arrange
        listing.setStatus(ListingStatus.PUBLISHED);
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));

        // Act & Assert
        assertThatThrownBy(() -> listingService.deleteListing(userId, listingId))
                .isInstanceOf(InvalidListingStateException.class);
    }

    @Test
    void getListingById_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> listingService.updateListing(userId, listingId, updateRequest))
                .isInstanceOf(ListingNotFoundException.class);
    }

    @Test
    void createDraft_WithNullGeolocation_ShouldStillWork() {
        // Arrange
        createRequest.setLatitude(null);
        createRequest.setLongitude(null);
        when(listingRepository.countByUserIdAndStatusIn(any(), any())).thenReturn(0L);
        when(listingMapper.toEntity(any())).thenReturn(listing);
        when(listingRepository.save(any())).thenReturn(listing);
        when(listingMapper.toResponse(any())).thenReturn(listingResponse);

        // Act
        ListingResponse result = listingService.createDraft(userId, createRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(listingRepository).save(any(Listing.class));
    }

    @Test
    void deleteListing_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> listingService.deleteListing(userId, listingId))
                .isInstanceOf(ListingNotFoundException.class);
    }

    @Test
    void cancelSubmission_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> listingService.cancelSubmission(userId, listingId))
                .isInstanceOf(ListingNotFoundException.class);
    }

    @Test
    void hasFreeListing_WhenOne_ShouldReturnFalse() {
        // Arrange
        when(listingRepository.countByUserIdAndStatusIn(any(), any())).thenReturn(1L);

        // Act
        boolean result = listingService.hasFreeListing(userId);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void updateListingAmenities_WhenOwner_ShouldUpdate() {
        // Arrange
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));
        when(amenityRepository.findAllById(any())).thenReturn(java.util.Collections.emptyList());

        // Act
        listingService.updateListingAmenities(userId, listingId, java.util.Collections.emptyList());

        // Assert
        verify(listingRepository).save(listing);
    }

    @Test
    void updateListingAmenities_WhenNotOwner_ShouldThrowException() {
        // Arrange
        listing.setUserId(UUID.randomUUID());
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));

        // Act & Assert
        assertThatThrownBy(() -> listingService.updateListingAmenities(userId, listingId, java.util.Collections.emptyList()))
                .isInstanceOf(UnauthorizedException.class);
    }
}
