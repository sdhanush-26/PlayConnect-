package com.playconnect.util;

/**
 * Haversine formula — calculates great-circle distance between two
 * lat/long points, accounting for Earth's curvature. Far more accurate
 * over longer distances than the flat-earth approximation used as a
 * placeholder in PlayerSportService (Day 20) — that gets replaced with
 * this on Day 33 once nearby-player search is the actual focus.
 */
public class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * @return distance in kilometers between two coordinates.
     */
    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}