package com.dathq.swd302.listingservice.common.util;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityUtils {
//    public static UUID getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
//            throw new UnauthorizedException("User is not authenticated");
//        }
//
//        Object principal = authentication.getPrincipal();
//
//        // Case 1: The principal is the UserDetails object (common in stateful or some JWT setups)
//        if (principal instanceof UserDetails) {
//            return UUID.fromString(((UserDetails) principal).getUsername());
//        }
//
//        // Case 2: The principal is a String (common in simple JWT converters where principal = sub)
//        if (principal instanceof String) {
//            return UUID.fromString((String) principal);
//        }
//
//        // Fallback: Try getting the name from the authentication object
//        try {
//            return UUID.fromString(authentication.getName());
//        } catch (IllegalArgumentException e) {
//            throw new UnauthorizedException("Unable to resolve User ID from security context");
//        }
//    }
}
