package com.factual.android.demo.base;

import java.io.Serializable;

import com.factual.android.demo.base.util.GeoUtils;
import com.google.android.maps.GeoPoint;

public class Shop implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private double latitude;
	private double longitude;
	private String phoneNumber;
	private String category;

	private String address;
	
	public String getAddressString() {
		StringBuffer sb = new StringBuffer();
		if (address != null)
			sb.append(address);
	//	if (addressExtended != null)
//			sb.append(" "+addressExtended);
//		if (poBox != null)
	//		sb.append(" "+poBox);
		sb.append(System.getProperty("line.separator"));
		if (locality != null)
			sb.append(locality);
		if (region != null)
			sb.append(", "+region);
		if (postcode != null)
			sb.append(" "+postcode);
		return sb.toString();
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
/*
	public String getAddressExtended() {
		return addressExtended;
	}

	public void setAddressExtended(String addressExtended) {
		this.addressExtended = addressExtended;
	}

	public String getPoBox() {
		return poBox;
	}

	public void setPoBox(String poBox) {
		this.poBox = poBox;
	}
*/
	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	//private String addressExtended;
	//private String poBox;
	private String locality;
	private String region;
	private String postcode;
	
	
	public Shop(String name, String category) {
		super();
		this.name = name;
		this.category = category;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public GeoPoint getGeoPoint() {
		return GeoUtils.getGeoPoint(latitude, longitude);
	}
	
	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}
}
