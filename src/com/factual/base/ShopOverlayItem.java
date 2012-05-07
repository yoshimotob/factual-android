package com.factual.base;

import com.google.android.maps.OverlayItem;

public class ShopOverlayItem extends OverlayItem {

	private Shop shop;

	public ShopOverlayItem(Shop shop) {
		super(shop.getGeoPoint(), shop.getName(), shop.getAddressString());
		this.shop = shop;
	}

	public Shop getShop() {
		return shop;
	}

}
