package com.zykj.xishuashua.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.zykj.xishuashua.BaseActivity;
import com.zykj.xishuashua.BaseApp;
import com.zykj.xishuashua.R;
import com.zykj.xishuashua.view.MyCommonTitle;

public class MapActivity extends BaseActivity{

	private MyCommonTitle myCommonTitle;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private String latitude,longitude,address;
	
	private boolean isFirstIn = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		initView(R.layout.ui_map_activity);
		latitude = getIntent().getStringExtra("lat");//BaseApp.getModel().getLatitude();
		longitude = getIntent().getStringExtra("long");//BaseApp.getModel().getLongitude();
		address = getIntent().getStringExtra("address");

		initView();
		initLocation();
	}

	private void initLocation() {
		mLocationClient = new LocationClient(this);
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);

		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		
		mLocationClient.setLocOption(option);
	}
	
	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			MyLocationData data = new MyLocationData.Builder()//
				.accuracy(location.getRadius())//
				.latitude(Double.valueOf(latitude))//
				.longitude(Double.valueOf(longitude))//
				.build();
			mBaiduMap.setMyLocationData(data);
			
			final LatLng latLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
			if(isFirstIn){
				//LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;
			}
			final TextView tv = new TextView(MapActivity.this);
			tv.setBackgroundResource(R.drawable.bg_round_grey);
			tv.setPadding(30, 10, 30, 10);
			tv.setText(address);
			
//			Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
//			p.y -= 27;
//			LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);
//			
//			InfoWindow infoWindow = new InfoWindow(tv, ll, 0);
//			mBaiduMap.showInfoWindow(infoWindow);

			mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
				@Override
				public void onMapLoaded() {
					Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
					p.y -= 27;
					LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);
					
					InfoWindow infoWindow = new InfoWindow(tv, ll, 0);
					mBaiduMap.showInfoWindow(infoWindow);
				}
			});
		}
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		myCommonTitle = (MyCommonTitle) findViewById(R.id.aci_mytitle);
		myCommonTitle.setTitle("地址");
		
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//开启定位
		mBaiduMap.setMyLocationEnabled(true);
		if(!mLocationClient.isStarted())
			mLocationClient.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//停止定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}
}