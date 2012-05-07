package com.factual.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.factual.driver.Circle;
import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.factual.R;
import com.factual.driver.ReadResponse;
import com.factual.driver.android.Android;
import com.factual.base.util.GeoUtils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class BusinessSearchActivity extends MapActivity implements LocationListener {
	// UI
	private ImageButton searchButton;
	private ImageButton centerMyLocationButton;
	private ImageButton moreResultsButton;
	private EditText searchText;
	private SearchMap mapView;
	private Button filterButton;
	private TextView currentLocationText;
	private MyLocationOverlay myLocationOverlay;
	private MaskOverlay maskOverlay;
	private ResultItemOverlay resultsOverlay;
	private Dialog searchingDialog;
	private Dialog aboutDialog;

	private ShopRetrievalTask currentSearchTask;
	// State
	private static List<CategoryGroup> categoryResults = new ArrayList<CategoryGroup>();
	private static CategoryGroup selectedGroup = null;
	private static Location currentLocation;
	private static List<Shop> results = new ArrayList<Shop>();
	private static int offset = 0;
	private static double savedCenterLat;
	private static double savedCenterLong;
	private static String searchTerm;
	private static boolean initialized = false;

	private static final int maxResultCount = 100;
	private static final int fetchLimit = 20;
	private static final int maxRange = 8000000;
	
	protected Factual factual = new Factual("U3QbOw7bW9nxDN4TppoqQlxwnJmSISUbJ3h3pRj1", "2AFaSQYCFjSsDkZKLOhpT8QE2zQRLCUxcJnMMfSa");
	
	protected boolean isFree() {
		return true;
	}
	
	protected String getAdBannerId() {
		return "";
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		factual.setPlatform(new Android(this));
		
		setContentView(R.layout.main);

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		

		if (searchingDialog == null) {
			searchingDialog = new Dialog(this) {
				@Override
				public void onCreate(Bundle savedInstanceState) {
				    super.onCreate(savedInstanceState);
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View searchingView = inflater.inflate(R.layout.searching_popup,
							null, false);
					setContentView(searchingView);
				}

			};
			searchingDialog.setCanceledOnTouchOutside(false);
			searchingDialog.setCancelable(false);
			searchingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			/*
			searchingDialog.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) { 
					if (currentSearchTask != null) { 
						currentSearchTask.cancel(false); 
					}
				}
			});
			*/
		}
		if (aboutDialog == null) {
			aboutDialog = new Dialog(this) {
				@Override
				public void onCreate(Bundle savedInstanceState) {
				    super.onCreate(savedInstanceState);
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View searchingView = inflater.inflate(R.layout.about_popup,
							null, false);
					TextView deviceInfo = (TextView) searchingView.findViewById(R.id.deviceInfo);

					StringBuffer sb = new StringBuffer();
					sb.append(Build.BOARD);
					sb.append(System.getProperty("line.separator"));
					//sb.append(Build.BOOTLOADER);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.BRAND);
					sb.append(System.getProperty("line.separator"));
					//sb.append(Build.CPU_ABI);
					sb.append(System.getProperty("line.separator"));
					//sb.append(Build.CPU_ABI2);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.DEVICE);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.DISPLAY);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.FINGERPRINT);
					sb.append(System.getProperty("line.separator"));
					//sb.append(Build.HARDWARE);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.HOST);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.ID);
					sb.append(System.getProperty("line.separator"));
					//sb.append(Build.MANUFACTURER);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.MODEL);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.PRODUCT);
					sb.append(System.getProperty("line.separator"));
					//sb.append(Build.SERIAL);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.TAGS);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.TIME);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.TYPE);
					sb.append(System.getProperty("line.separator"));
					//sb.append(Build.UNKNOWN);
					sb.append(System.getProperty("line.separator"));
					sb.append(Build.USER);
					
					deviceInfo.setText(sb.toString());
					
					setContentView(searchingView);
				}

			};
			aboutDialog.setCanceledOnTouchOutside(true);
			aboutDialog.setCancelable(true);
			aboutDialog.setTitle(getString(R.string.instructions));
		}


		if (currentLocationText == null) {
			currentLocationText = (TextView) findViewById(R.id.currentLocation);
		    currentLocationText.setBackgroundColor(Color.WHITE);

			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			String locationProvider = LocationManager.GPS_PROVIDER;
			locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this);
		    onLocationChanged(locationManager.getLastKnownLocation(locationProvider));
		}
		
		
		if (filterButton == null) {
			filterButton = (Button) findViewById(R.id.filterButton);
			filterButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					showCategoryPopup();
				}
			});
		}

		if (searchText == null) {
			searchText = (EditText) findViewById(R.id.SearchTextView);
			searchText.setOnEditorActionListener(new OnEditorActionListener(){

				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
				    if(actionId == EditorInfo.IME_ACTION_SEARCH) {
						fetchFullResults();
				    }
				    InputMethodManager imm = 
				        (InputMethodManager) searchText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
				    return false;
				}});
		}

		if (searchButton == null) {
			searchButton = (ImageButton) findViewById(R.id.searchButton);
			searchButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					fetchFullResults();
				}

			});
		}

		if (centerMyLocationButton == null) {
			centerMyLocationButton = (ImageButton) findViewById(R.id.centerMyLocation);
			centerMyLocationButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					centerCurrentPoint();
				}
			});
		}

		if (moreResultsButton == null) {
			moreResultsButton = (ImageButton) findViewById(R.id.moreResults);
			moreResultsButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					offset += fetchLimit;
					fetchResults();
				}
			});
		}

		if (mapView == null) {
			mapView = (SearchMap) findViewById(R.id.mapview);
			mapView.setBuiltInZoomControls(true);
			
			List<Overlay> mapOverlays = mapView.getOverlays();

			myLocationOverlay = new MyLocationOverlay(mapView.getContext(), mapView);

			mapOverlays.add(myLocationOverlay);

			maskOverlay = new MaskOverlay(mapView, null, null);
			mapOverlays.add(maskOverlay);

			Drawable resultDrawable = getResources().getDrawable(
					R.drawable.marker);
			resultsOverlay = new ResultItemOverlay(resultDrawable, mapView,
					this);
			resultsOverlay.overlayPopulate();

			mapView.getOverlays().add(resultsOverlay);
			mapView.setResultsOverlay(resultsOverlay);

			if (!initialized) {
				centerCurrentPoint();
			}
			refreshForGroup();

		}

		initialized = true;
	}

	@Override
	public void onResume() {
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		super.onResume();
	}

	@Override
	public void onPause() {
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
		super.onPause();
	}
	
	@Override  
	  public boolean onCreateOptionsMenu(Menu menu) {  
	    menu.add(getString(R.string.instructions));  
	    return super.onCreateOptionsMenu(menu);  
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		aboutDialog.show();
		return true;
	}

	protected Query applyPreFilter(Query query) {
		return query;
	}

	protected boolean isCategoryFilterPresent() {
		return false;
	}
	
	private Location getLocation(){
		Location location = myLocationOverlay.getLastFix();
		if (location == null) {
			LocationManager locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
			String locationProvider = LocationManager.GPS_PROVIDER;
			location = locationManager
					.getLastKnownLocation(locationProvider);
		}
		return location;
	}
	
	private GeoPoint getCurrentPoint() {
		Location lastKnownLocation = getLocation();
		GeoPoint lastKnownPoint = null;
		if (lastKnownLocation != null) {
			lastKnownPoint = GeoUtils.getGeoPoint(lastKnownLocation.getLatitude(),
					lastKnownLocation.getLongitude());
		}
		return GeoUtils.getBestCurrentPoint(lastKnownPoint);
	}


	private CategoryGroup getAllGroup(List<Shop> results) {
		return new CategoryGroup(getString(R.string.all_category), results);
	}

	private void fetchFullResults() {
		searchTerm = searchText.getText().toString().trim();
		/*
		if (!isCategoryFilterPresent()) {
			if ("".equals(searchTerm)) {
				Toast.makeText(BusinessSearchActivity.this,
						getString(R.string.please_enter_text), Toast.LENGTH_SHORT).show();
				return;
			}
		}
		 */
		savedCenterLat = GeoUtils.convertLatLongFromE6(mapView
				.getMapCenter().getLatitudeE6());
		savedCenterLong = GeoUtils.convertLatLongFromE6(mapView
				.getMapCenter().getLongitudeE6());
		offset = 0;
		fetchResults();
	}
	
	private void fetchResults() {
		currentSearchTask = new ShopRetrievalTask();
		searchingDialog.show();
		currentSearchTask.execute();
	}

	private void centerCurrentPoint() {
		GeoPoint point = getCurrentPoint();
		mapView.getController().animateTo(point);
		if (mapView.getZoomLevel() < 15)
			mapView.getController().setZoom(15);
	}

	public class ShopRetrievalTask extends AsyncTask {
		private boolean error = false;
		@Override
		protected Object doInBackground(Object... params) {
			List<Shop> shops = new ArrayList<Shop>();
			double centerLat = savedCenterLat;
			double centerLong = savedCenterLong;
			double searchMeters =  maxRange;
			try {
				Query query = new Query().search(searchTerm).limit(fetchLimit).within(new Circle(centerLat, centerLong, new Double(searchMeters).intValue())).offset(offset).sortAsc("$distance").only("name","category","latitude","longitude","tel","address","locality","region","postcode");
				query = applyPreFilter(query);
				ReadResponse response = factual.fetch("places", query);
				List<Map<String, Object >> data = response.getData();
	
				for (Map<String, Object> item : data) {
					String name = (String) item.get("name");
					String category =(String)  item.get("category");
					String phoneNumber = (String) item.get("tel");
					if (category == null || "null".equals(category))
						category = getString(R.string.no_category);
					Shop shop = new Shop(name, category);
					shop.setLatitude((Double) item.get("latitude"));
					shop.setLongitude((Double) item.get("longitude"));
					shop.setPhoneNumber(phoneNumber);
					shop.setAddress((String) item.get("address"));
					shop.setLocality((String) item.get("locality"));
					shop.setRegion((String) item.get("region"));
					shop.setPostcode((String) item.get("postcode"));
					shops.add(shop);
				}
			} catch (Exception e) {
				e.printStackTrace();
				error = true;
			}

			/*
			 * 
			 * try { Thread.sleep(1000); } catch (InterruptedException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 * 
			 * shops.add(new Shop("blah", "Grocery")); shops.add(new
			 * Shop("blah 2", "Grocery")); shops.add(new Shop("blah 2",
			 * "Hardware"));
			 * 
			 * int i = 1;
			 * 
			 * for (Shop shop : shops) { shop.setLatitude(getCurrentLatitude() +
			 * i); shop.setLongitude(getCurrentLongitude() + i); i++; }
			 */
			 /*
			int latSpan = savedLatSpan;
			int longSpan = savedLongSpan;
			*/
			//double mPerDeg = 111325; // m / degree;
			//String searchUrl = "http://api.factual.com/v2/tables/s4OOB4/read?APIKey=Sg7CnC4NIkt5vTYl27FROKv3TXIbfn3kcLmCbjrIO8zAZ9wNjqBOsPYqHhATMaXm&filters={\"$search\":\"%s\",\"$loc\":{\"$within\":{\"$center\":[[%.6f,%.6f],%f]}}}&limit=%d&offset=%d&sort=$distance:asc";
			//String searchUrl = "http://api.v3.factual.com/t/global?filters={\"country\":\"US\"}&q=%s&geo={\"$circle\":{\"$center\":[%.6f,%.6f],\"$meters\":%f}}&limit=%d&offset=%d&include_count=false&select=name,category,latitude,longitude,tel,address,locality,region,postcode&sort=$distance:asc&KEY=2AFaSQYCFjSsDkZKLOhpT8QE2zQRLCUxcJnMMfSa";
			// "http://api.factual.com/v2/tables/s4OOB4/read?APIKey=Sg7CnC4NIkt5vTYl27FROKv3TXIbfn3kcLmCbjrIO8zAZ9wNjqBOsPYqHhATMaXm&filters={\"$loc\":{\"$within_dist\":[34.0371554303249,-118.445835113525,1000]}}&sort={\"factual_id\":1}";
			// "http://api.factual.com/v2/tables/nbY9Fw/read?APIKey=Sg7CnC4NIkt5vTYl27FROKv3TXIbfn3kcLmCbjrIO8zAZ9wNjqBOsPYqHhATMaXm";

			if (offset == 0)
				results.clear();

			results.addAll(shops);
			return null;
		}

		@Override
		protected void onProgressUpdate(Object... progress) {
		}

		@Override
		protected void onPostExecute(Object result) {
			searchingDialog.dismiss();
			
			if (error) {
				Toast.makeText(BusinessSearchActivity.this, getString(R.string.error_retrieving_data),
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			categoryResults.clear();
			categoryResults.add(getAllGroup(results));

			if (results.size() == 0) {
				selectedGroup = null;
				Toast.makeText(BusinessSearchActivity.this, String.format(getString(R.string.no_results), searchTerm),
						Toast.LENGTH_SHORT).show();
				refreshForGroup();
			} else {
				searchingDialog.dismiss();

				if (isCategoryFilterPresent()) {
					selectedGroup = categoryResults.get(0);
					refreshForGroup();
				} else {
					Map<String, List<Shop>> savedCategoryMap = new HashMap<String, List<Shop>>();
					
					for (Shop s : results) {
						String category = s.getCategory();
						if (!savedCategoryMap.containsKey(category))
							savedCategoryMap.put(category, new ArrayList<Shop>());
						savedCategoryMap.get(category).add(s);
					}
	
					for (Entry<String, List<Shop>> entry : savedCategoryMap
							.entrySet()) {
						CategoryGroup group = new CategoryGroup(entry.getKey(),
								entry.getValue());
						categoryResults.add(group);
						if (offset > 0 && selectedGroup != null && selectedGroup.getCategoryName().equals(group.getCategoryName()))
							selectedGroup = group;
					}
					Collections.sort(categoryResults);
	
					// Only 1 category
					if (categoryResults.size() == 2) {
						selectedGroup = categoryResults.get(1);
						refreshForGroup();
					} else {
						if (offset == 0) {			
							showCategoryPopup();
						} else {
							refreshForGroup();
						}
					}
					
				}
				
				if (results.size() == offset+fetchLimit) {
					if (results.size() + fetchLimit > maxResultCount) {
						Toast.makeText(getApplicationContext(), String.format(getString(R.string.max_results), maxResultCount), Toast.LENGTH_SHORT).show();
					}
				}


			}

		}

	}

	private void refreshForGroup() {
		
		if (results.size() == offset+fetchLimit) {
			if (results.size() + fetchLimit > maxResultCount) {
				moreResultsButton.setVisibility(View.GONE);
			} else {
				moreResultsButton.setVisibility(View.VISIBLE);
			}
		} else {
			moreResultsButton.setVisibility(View.GONE);
		}
			maskOverlay.setCenterPoint(new GeoPoint(GeoUtils.convertLatLong(savedCenterLat),
					GeoUtils.convertLatLong(savedCenterLong)));

		if (results.size() % fetchLimit == 0 && results.size() > 0) {
			maskOverlay.setFarthestPoint(results.get(results.size() - 1).getGeoPoint());
		} else {
			double mPerDeg = 111325;
			double degrees = maxRange/mPerDeg;
			int degreesInt = GeoUtils.convertLatLong(degrees);
			maskOverlay.setFarthestPoint(new GeoPoint(maskOverlay.getCenterPoint().getLatitudeE6()+degreesInt, maskOverlay.getCenterPoint().getLongitudeE6()));
		}

		resultsOverlay.setFocus(null);
		resultsOverlay.clear();
		if (selectedGroup != null) {
			filterButton.setText(selectedGroup.getCategoryName());
			for (Shop shop : selectedGroup.getShops()) {
				ShopOverlayItem overlayitem = new ShopOverlayItem(shop);
				resultsOverlay.addOverlay(overlayitem);
			}
		}
		if (categoryResults.size() > 1) {
			filterButton.setVisibility(View.VISIBLE);
		} else {
			filterButton.setVisibility(View.GONE);
		}
		resultsOverlay.overlayPopulate();
		mapView.invalidate();

		if (results.size() > 0) {
			Shop closestShop = results.get(0);
			int latSpan = mapView.getLatitudeSpan();
			int longSpan = mapView.getLongitudeSpan();
			int centerLat = mapView.getMapCenter().getLatitudeE6();
			int centerLong = mapView.getMapCenter().getLongitudeE6();
			int closestShopLat = GeoUtils.convertLatLong(closestShop.getLatitude());
			int closestShopLong = GeoUtils.convertLatLong(closestShop.getLongitude());
			// If closest shop is out of bounds of the map viewport, pan it into view
			if (closestShopLat > centerLat + latSpan/2 || closestShopLat < centerLat - latSpan/2 || closestShopLong > centerLong + longSpan/2 || closestShopLong < centerLong - longSpan/2) {
				int newLatSpan = Math.abs(closestShopLat - centerLat)*2;
				int newLongSpan = Math.abs(closestShopLong - centerLong)*2;
				mapView.getController().zoomToSpan(newLatSpan, newLongSpan);
			}
			
		}
	}

	private void showCategoryPopup() {


		final Dialog pw = new Dialog(this) {
			@Override
			public void onCreate(Bundle savedInstanceState) {
			    super.onCreate(savedInstanceState);
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View popupView = inflater.inflate(R.layout.category_popup, null, false);

				ListView categoryListView = (ListView) popupView
						.findViewById(R.id.CategoryListView);

			    setContentView(popupView);


			    //getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

				WindowManager mWinMgr = (WindowManager) categoryListView.getContext()
						.getSystemService(Context.WINDOW_SERVICE);

			    categoryListView.setAdapter(new ArrayAdapter<CategoryGroup>(
						categoryListView.getContext(), R.layout.category_list_item,
						categoryResults.toArray(new CategoryGroup[] {})) {
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						LayoutInflater mInflater = LayoutInflater.from(this
								.getContext());
						View row;
						if (null == convertView) {
							row = mInflater.inflate(R.layout.category_list_item, null);
						} else {
							row = convertView;
						}
						TextView tv = (TextView) row.findViewById(R.id.categoryString);
						CategoryGroup categoryGroup = getItem(position);
						String catString = String.format(getString(R.string.category_pattern), categoryGroup.getShops().size(), categoryGroup.getCategoryName());
						tv.setText(catString);
						return row;
					}
				});
				categoryListView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						CategoryGroup group = (CategoryGroup) parent
								.getItemAtPosition(position);
						selectedGroup = group;
						dismiss();
					}
				});
			}
			
		};

		//pw.getWindow().setBackgroundDrawable(new ColorDrawable(0xa0000000));
		pw.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pw.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface arg0) {
				if (selectedGroup == null && categoryResults.size() > 0)
					selectedGroup = categoryResults.get(0);
				refreshForGroup();
			}
		});

		pw.setCancelable(true);

		pw.show();

	}

	public static class MaskOverlay extends Overlay {
		Paint pTouch;
		private MapView mapView;
		private GeoPoint centerPoint;

		public GeoPoint getCenterPoint() {
			return centerPoint;
		}

		public void setCenterPoint(GeoPoint centerPoint) {
			this.centerPoint = centerPoint;
		}

		public GeoPoint getFarthestPoint() {
			return farthestPoint;
		}

		public void setFarthestPoint(GeoPoint farthestPoint) {
			this.farthestPoint = farthestPoint;
		}

		private GeoPoint farthestPoint;

		public MaskOverlay(MapView mapView, GeoPoint centerPoint,
				GeoPoint farthestPoint) {
			this.mapView = mapView;
			this.centerPoint = centerPoint;
			this.farthestPoint = farthestPoint;
		}

		@Override
		public void draw(Canvas canvas, MapView mv, boolean shadow) {

			if (centerPoint == null || farthestPoint == null) {
				super.draw(canvas, mv, shadow);
				return;
			}

			Paint mPaint = new Paint();
			mPaint.setDither(true);
			mPaint.setColor(Color.GRAY);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(2);
			mPaint.setAlpha(50);

			Point centerPointPixels = new Point();
			Projection projection = mv.getProjection();
			projection.toPixels(centerPoint, centerPointPixels);

			Point farthestPointPixels = new Point();
			projection.toPixels(farthestPoint, farthestPointPixels);

			double distance = Math.sqrt(Math.pow(
					Math.abs(centerPointPixels.x - farthestPointPixels.x), 2)
					+ Math.pow(
							Math.abs(centerPointPixels.y
									- farthestPointPixels.y), 2));

			pTouch = new Paint(Paint.ANTI_ALIAS_FLAG);
			pTouch.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
			pTouch.setColor(Color.TRANSPARENT);
			// pTouch.setMaskFilter(new BlurMaskFilter(15, Blur.NORMAL));

			WindowManager mWinMgr = (WindowManager) mapView.getContext()
					.getSystemService(Context.WINDOW_SERVICE);

			Bitmap bitmap = Bitmap.createBitmap(mWinMgr.getDefaultDisplay()
					.getWidth(), mWinMgr.getDefaultDisplay().getHeight(),
					Bitmap.Config.ARGB_8888);
			Canvas c2 = new Canvas(bitmap);

			c2.drawRect(0, 0, mWinMgr.getDefaultDisplay().getWidth(), mWinMgr
					.getDefaultDisplay().getHeight(), mPaint);
			c2.drawCircle(centerPointPixels.x, centerPointPixels.y, new Double(
					distance).floatValue(), pTouch);
			canvas.drawBitmap(bitmap, 0, 0, null);


			Paint centerDot = new Paint();
			centerDot.setDither(true);
			centerDot.setColor(Color.GRAY);
			centerDot.setStyle(Paint.Style.FILL_AND_STROKE);
			centerDot.setStrokeJoin(Paint.Join.ROUND);
			centerDot.setStrokeCap(Paint.Cap.ROUND);
			centerDot.setStrokeWidth(2);
			centerDot.setAlpha(126);
			canvas.drawCircle(centerPointPixels.x, centerPointPixels.y, 5, centerDot);

			super.draw(canvas, mv, shadow);

		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLocationChanged(Location loc) {
		if (loc != null) {
			String location = "Lat: "+loc.getLatitude()+", Long:"+loc.getLongitude();
			currentLocationText.setText(location);
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}