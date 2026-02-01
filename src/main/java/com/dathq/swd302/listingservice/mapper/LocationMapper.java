package com.dathq.swd302.listingservice.mapper;
import com.dathq.swd302.listingservice.dto.response.CountryResponse;
import com.dathq.swd302.listingservice.dto.response.ProvinceResponse;
import com.dathq.swd302.listingservice.dto.response.WardResponse;
import com.dathq.swd302.listingservice.model.Country;
import com.dathq.swd302.listingservice.model.Province;
import com.dathq.swd302.listingservice.model.Ward;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    CountryResponse toCountryResponse(Country country);

    List<CountryResponse> toCountryResponseList(List<Country> countries);

    ProvinceResponse toProvinceResponse(Province province);

    List<ProvinceResponse> toProvinceResponseList(List<Province> provinces);

    WardResponse toWardResponse(Ward ward);

    List<WardResponse> toWardResponseList(List<Ward> wards);
}
