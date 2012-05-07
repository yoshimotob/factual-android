package com.factual.base;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.factual.R;
import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class ResultItemOverlay extends BalloonItemizedOverlay<ShopOverlayItem> {
	private Activity activity;
	public ResultItemOverlay(Drawable defaultMarker, MapView mapView, Activity activity) {
		super(boundCenter(defaultMarker), mapView);
		this.activity = activity;
	}
	
	public void overlayPopulate() {
		setLastFocusedIndex(-1);
		populate();
	}
	public void addOverlay(ShopOverlayItem overlay) {
	    mOverlays.add(overlay);
	}
	private ArrayList<ShopOverlayItem> mOverlays = new ArrayList<ShopOverlayItem>();
	@Override
	protected ShopOverlayItem createItem(int i) {
		return mOverlays.get(i);

	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	public void clear() {
		mOverlays.clear();
	}

	@Override
	protected boolean onBalloonTap(int index, final ShopOverlayItem item) {
		final Dialog pw = new Dialog(activity) {
			@Override
			public void onCreate(Bundle savedInstanceState) {
			    super.onCreate(savedInstanceState);
				LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View popupView = inflater.inflate(R.layout.business_detail_view, null, false);

				TextView itemAddress = (TextView) popupView
						.findViewById(R.id.itemAddress);
				itemAddress.setText(item.getShop().getAddressString());

				TextView itemCategory = (TextView) popupView
						.findViewById(R.id.itemCategory);
				itemCategory.setText(item.getShop().getCategory());

				String rawNum = item.getShop().getPhoneNumber();
				if (rawNum == null)
					rawNum = "";
				rawNum = rawNum.trim();
				
				final String phoneNumber = rawNum;
				Button callButton = (Button) popupView
						.findViewById(R.id.callButton);
				callButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						String uri = "tel:" + phoneNumber;
						 Intent intent = new Intent(Intent.ACTION_CALL);
						 intent.setData(Uri.parse(uri));
						 activity.startActivity(intent);
						
					}
				});
				if ("".equals(rawNum)) {
					callButton.setVisibility(View.GONE);
				} else {
					callButton.setText(String.format(activity.getString(R.string.call_string), phoneNumber));
					callButton.setVisibility(View.VISIBLE);
				}
				
				Button directionsButton = (Button) popupView
						.findViewById(R.id.directionsButton);
				directionsButton.setText(activity.getString(R.string.navigate));
				directionsButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						String url = String.format("geo:0,0?q=%.6f,%.6f (%s)", item.getShop().getLatitude(), item.getShop().getLongitude(), item.getShop().getName());
						Uri geoUrl = Uri.parse(url);
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
						geoUrl);
						activity.startActivity(intent);
					}
				});
				
			    setContentView(popupView);

		
			}
			
		};
		pw.setTitle(item.getShop().getName());
		pw.setCancelable(true);
		pw.show();

		return true;
	}
}