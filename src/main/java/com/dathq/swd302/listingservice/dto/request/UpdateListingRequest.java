package com.dathq.swd302.listingservice.dto.request;
import com.dathq.swd302.listingservice.model.enums.ListingType;
import com.dathq.swd302.listingservice.model.enums.PropertyType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateListingRequest {
    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    private ListingType listingType;

    private PropertyType propertyType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Size(max = 3, message = "Currency code must be 3 characters")
    private String priceCurrency;

    private String pricePeriod;

    private Boolean negotiable;

    @DecimalMin(value = "0.0", inclusive = false, message = "Area must be greater than 0")
    private BigDecimal areaSqm;

    @Min(value = 0, message = "Bedrooms must be 0 or greater")
    private Integer bedrooms;

    @Min(value = 0, message = "Bathrooms must be 0 or greater")
    private Integer bathrooms;

    @Min(value = 0, message = "Floors must be 0 or greater")
    private Integer floors;

    private Integer floorNumber;

    @Min(value = 1800, message = "Year built must be valid")
    @Max(value = 2100, message = "Year built must be valid")
    private Integer yearBuilt;

    private UUID wardId;

    private UUID provinceId;

    private UUID countryId;

    @Size(max = 500, message = "Street address must not exceed 500 characters")
    private String streetAddress;

    @Size(max = 255, message = "Building name must not exceed 255 characters")
    private String buildingName;

    private Double latitude;

    private Double longitude;
}
