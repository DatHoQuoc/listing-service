package com.dathq.swd302.listingservice.service;


import com.dathq.swd302.listingservice.dto.response.CountryResponse;
import com.dathq.swd302.listingservice.dto.response.ProvinceResponse;
import com.dathq.swd302.listingservice.dto.response.WardResponse;

import java.util.List;
import java.util.UUID;
public interface LocationService {
    /**
     * Get all countries
     *
     * @return list of countries
     */
    List<CountryResponse> getAllCountries();

    /**
     * Get all provinces for a country
     *
     * @param countryId country ID
     * @return list of provinces
     */
    List<ProvinceResponse> getProvincesByCountry(UUID countryId);

    /**
     * Get all wards for a province
     *
     * @param provinceId province ID
     * @return list of wards
     */
    List<WardResponse> getWardsByProvince(UUID provinceId);

    /**
     * Get ward by ID
     *
     * @param wardId ward ID
     * @return ward details with province and country info
     */
    WardResponse getWardById(UUID wardId);

    /**
     * Search wards by name
     *
     * @param keyword search keyword
     * @return matching wards
     */
    List<WardResponse> searchWards(String keyword);
}
