package com.emergency.system.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Unit tests for Haversine distance calculation.
 * No Spring context needed.
 */
class LocationServiceTest {

    @Test
    @DisplayName("Same coordinates → 0 metres")
    void samePoint_returnsZero() {
        double dist = LocationService.haversineDistance(27.1767, 78.0081, 27.1767, 78.0081);
        assertThat(dist).isCloseTo(0.0, within(0.001));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "Taj Mahal to Agra Fort (~2.5km), 27.1751, 78.0421, 27.1788, 78.0010, 2500, 400",
        "Nearby point (~100m),            27.1767, 78.0081, 27.1776, 78.0090, 100, 50",
        "Far apart (~500km),              28.6139, 77.2090, 22.5726, 88.3639, 500000, 20000"
    })
    @DisplayName("Haversine: {0}")
    void haversineDistance(String label, double lat1, double lon1,
                            double lat2, double lon2,
                            double expectedMeters, double toleranceMeters) {
        double dist = LocationService.haversineDistance(lat1, lon1, lat2, lon2);
        assertThat(dist).isCloseTo(expectedMeters, within(toleranceMeters));
    }

    @Test
    @DisplayName("Distance is symmetric: A→B == B→A")
    void haversine_isSymmetric() {
        double ab = LocationService.haversineDistance(27.1767, 78.0081, 27.1900, 78.0200);
        double ba = LocationService.haversineDistance(27.1900, 78.0200, 27.1767, 78.0081);
        assertThat(ab).isCloseTo(ba, within(0.001));
    }

    @Test
    @DisplayName("Distance is always non-negative")
    void haversine_nonNegative() {
        double dist = LocationService.haversineDistance(0, 0, -10, -10);
        assertThat(dist).isGreaterThanOrEqualTo(0);
    }
}
