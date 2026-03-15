package com.dathq.swd302.listingservice.mapper;

import com.dathq.swd302.listingservice.dto.request.UpdateListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.model.Country;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.Province;
import com.dathq.swd302.listingservice.model.VirtualTour;
import com.dathq.swd302.listingservice.model.Ward;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListingMapperTest {

    private final ListingMapper mapper = Mappers.getMapper(ListingMapper.class);

    @Test
    void toResponse_ShouldMapLocationIdsAndFreePost() {
        UUID wardId = UUID.randomUUID();
        UUID provinceId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Ward ward = new Ward();
        ward.setWardId(wardId);

        Province province = new Province();
        province.setProvinceId(provinceId);

        Country country = new Country();
        country.setCountryId(countryId);

        Listing listing = new Listing();
        listing.setWard(ward);
        listing.setProvince(province);
        listing.setCountry(country);
        listing.setFreePost(true);

        ListingResponse response = mapper.toResponse(listing);

        assertThat(response).isNotNull();
        assertThat(response.getWardId()).isEqualTo(wardId);
        assertThat(response.getProvinceId()).isEqualTo(provinceId);
        assertThat(response.getCountryId()).isEqualTo(countryId);
        assertThat(response.getIsFreePost()).isTrue();
    }

    @Test
    void toDetailResponse_ShouldMapLocationIdsFreePostAndVirtualTourFlag() {
        UUID wardId = UUID.randomUUID();
        UUID provinceId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Ward ward = new Ward();
        ward.setWardId(wardId);

        Province province = new Province();
        province.setProvinceId(provinceId);

        Country country = new Country();
        country.setCountryId(countryId);

        Listing listing = new Listing();
        listing.setWard(ward);
        listing.setProvince(province);
        listing.setCountry(country);
        listing.setFreePost(true);
        listing.setVirtualTour(new VirtualTour());

        ListingDetailResponse response = mapper.toDetailResponse(listing);

        assertThat(response).isNotNull();
        assertThat(response.getWardId()).isEqualTo(wardId);
        assertThat(response.getProvinceId()).isEqualTo(provinceId);
        assertThat(response.getCountryId()).isEqualTo(countryId);
        assertThat(response.getIsFreePost()).isTrue();
        assertThat(response.getHasVirtualTour()).isTrue();
    }

    @Test
    void updateEntityFromRequest_ShouldMapLocationIds() {
        UUID wardId = UUID.randomUUID();
        UUID provinceId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        UpdateListingRequest request = UpdateListingRequest.builder()
                .wardId(wardId)
                .provinceId(provinceId)
                .countryId(countryId)
                .build();

        Listing listing = new Listing();

        mapper.updateEntityFromRequest(request, listing);

        assertThat(listing.getWard()).isNotNull();
        assertThat(listing.getWard().getWardId()).isEqualTo(wardId);
        assertThat(listing.getProvince()).isNotNull();
        assertThat(listing.getProvince().getProvinceId()).isEqualTo(provinceId);
        assertThat(listing.getCountry()).isNotNull();
        assertThat(listing.getCountry().getCountryId()).isEqualTo(countryId);
    }
}