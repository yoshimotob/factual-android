package com.factual.base.util;

import com.google.android.maps.GeoPoint;

public class GeoUtils {
	public static GeoPoint getGeoPoint(double latitude, double longitude) {
		GeoPoint point = new GeoPoint(convertLatLong(latitude),
				convertLatLong(longitude));
		return point;
	}

	public static int convertLatLong(double latLong) {
		return new Double(latLong * Math.pow(10, 6)).intValue();
	}

	public static double convertLatLongFromE6(int latLong) {
		return (new Double(latLong)) / Math.pow(10, 6);
	}

	public static GeoPoint getBestCurrentPoint(GeoPoint currentGuess) {
		return resolveGeoPoint(currentGuess, getDefaultPoint());
	}

	public static GeoPoint resolveGeoPoint(GeoPoint point, GeoPoint defaultPoint) {
		return (point != null) ? point : defaultPoint;
	}
	
	public static GeoPoint getDefaultPoint() {
		return getGeoPoint(getDefultLatitude(), getDefaultLongitude());
	}
	
	public static double getDefaultLongitude() {
		return -118.44583511352539;
	}

	public static double getDefultLatitude() {
		return 34.037155430324916;
	}

	public static double getInitialLatSpan() {
		return 0.5;
	}

	public static double getInitialLongSpan() {
		return 0.5;
	}
}
