package com.dathq.swd302.listingservice.service.impl;
import com.dathq.swd302.listingservice.dto.response.CountryResponse;
import com.dathq.swd302.listingservice.dto.response.ProvinceResponse;
import com.dathq.swd302.listingservice.dto.response.WardResponse;
import com.dathq.swd302.listingservice.mapper.LocationMapper;
import com.dathq.swd302.listingservice.model.Country;
import com.dathq.swd302.listingservice.model.Province;
import com.dathq.swd302.listingservice.model.Ward;
import com.dathq.swd302.listingservice.repository.CountryRepository;
import com.dathq.swd302.listingservice.repository.ProvinceRepository;
import com.dathq.swd302.listingservice.repository.WardRepository;
import com.dathq.swd302.listingservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final LocationMapper locationMapper;

    @Override
    public List<CountryResponse> getAllCountries() {
        log.info("Fetching all countries");
        List<Country> countries = countryRepository.findAll();
        return locationMapper.toCountryResponseList(countries);
    }

    @Override
    public List<ProvinceResponse> getProvincesByCountry(UUID countryId) {
        log.info("Fetching provinces for country: {}", countryId);
        List<Province> provinces = provinceRepository.findByCountryCountryIdOrderByName(countryId);
        return locationMapper.toProvinceResponseList(provinces);
    }

    @Override
    public List<WardResponse> getWardsByProvince(UUID provinceId) {
        log.info("Fetching wards for province: {}", provinceId);
        List<Ward> wards = wardRepository.findByProvinceProvinceIdOrderByName(provinceId);
        return locationMapper.toWardResponseList(wards);
    }

    @Override
    public WardResponse getWardById(UUID wardId) {
        log.info("Fetching ward: {}", wardId);
        Ward ward = wardRepository.findById(wardId)
                .orElseThrow(() -> new RuntimeException("Ward not found with id: " + wardId));

        WardResponse response = locationMapper.toWardResponse(ward);

        Province province = provinceRepository.findById(ward.getProvince().getProvinceId())
                .orElse(null);
        if (province != null) {
            response.setProvinceName(province.getName());

            Country country = countryRepository.findById(province.getCountry().getCountryId())
                    .orElse(null);
            if (country != null) {
                response.setCountryName(country.getName());
            }
        }

        return response;
    }

    @Override
    public List<WardResponse> searchWards(String keyword) {
        log.info("Searching wards with keyword: {}", keyword);
        List<Ward> wards = wardRepository.findByNameContainingIgnoreCase(keyword);
        return locationMapper.toWardResponseList(wards);
    }
}
