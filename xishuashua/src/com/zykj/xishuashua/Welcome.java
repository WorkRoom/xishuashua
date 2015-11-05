package com.zykj.xishuashua;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.loopj.android.http.RequestParams;
import com.zykj.xishuashua.activity.IntroActivity;
import com.zykj.xishuashua.activity.MainActivity;
import com.zykj.xishuashua.http.HttpErrorHandler;
import com.zykj.xishuashua.http.HttpUtils;
import com.zykj.xishuashua.utils.AppShortCutUtil;
import com.zykj.xishuashua.utils.StringUtil;
import com.zykj.xishuashua.utils.Tools;

public class Welcome extends BaseActivity {
	
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private static Context context;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());
		initView(R.layout.ui_welcome);
		context = this;
		
		PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY,"BA4pFu68DGRMAyQ0NSgFf7S9");
		initLocation();
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				// Intent intent = new Intent(Welcome.this, MainActivity.class);
				// startActivity(intent);

				String is_intro = getSharedPreferenceValue(BaseApp.IS_INTRO);
				boolean should_intro = false;
				int version = Tools.getAppVersion(Welcome.this);
				String save_version = getSharedPreferenceValue(BaseApp.VERSION);
				int save_version_int = save_version.equals("") ? -1 : Integer
						.parseInt(save_version);

				if (is_intro.length() > 0 && version == save_version_int) {// 已经进行过指引,且版本号符合
					should_intro = false;
				} else {
					should_intro = true;
				}

				if (should_intro) {
					Intent intent = new Intent(Welcome.this, IntroActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(Welcome.this, MainActivity.class);
					startActivity(intent);
				}
				finish();
			}
		};
		timer.schedule(task, 2000);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//开启定位
		if(!mLocationClient.isStarted())
			mLocationClient.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//停止定位
		mLocationClient.stop();
	}
	
	private void checkLogin(){
		if(StringUtil.isEmpty(BaseApp.getModel().getUsername())){
			return;
		}
		RequestParams params = new RequestParams();
		params.put("login_name", BaseApp.getModel().getUsername());
		params.put("login_password", BaseApp.getModel().getPassword());
		params.put("lati", BaseApp.getModel().getLatitude());
		params.put("longi", BaseApp.getModel().getLongitude());
        HttpUtils.login(new HttpErrorHandler() {
			@Override
			public void onRecevieSuccess(JSONObject json) {
				JSONObject data = json.getJSONObject("data");
				if(data == null){BaseApp.getModel().clear();}
				else{
					BaseApp.getModel().setAvatar(data.getString("member_avatar"));//头像
					BaseApp.getModel().setMobile(data.getString("member_mobile"));//手机号
					BaseApp.getModel().setPassword(BaseApp.getModel().getPassword());//登录密码
					BaseApp.getModel().setUserid(data.getString("member_id"));//用户Id
					BaseApp.getModel().setUsername(data.getString("member_name"));//登录账号
				}
			}
			@Override
			public void onRecevieFailed(String status, JSONObject json) {
				BaseApp.getModel().clear();
				Tools.toast(Welcome.this, "登录失效!");
			}
		}, params);
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
			BaseApp.getModel().setLatitude(String.valueOf(location.getLatitude()));
			BaseApp.getModel().setLongitude(String.valueOf(location.getLongitude()));
			//Tools.toast(Welcome.this, "lat="+location.getLatitude()+"long="+location.getLongitude());
			Log.e("-----------------------", "lat="+location.getLatitude()+"long="+location.getLongitude());
			checkLogin();
		}
	}
	
	public static void setNoticeNum(String num){
		AppShortCutUtil.addNumShortCut(context, Welcome.class, true, num, true);
	}
}
