package com.zykj.xishuashua.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.zykj.xishuashua.BaseActivity;
import com.zykj.xishuashua.BaseApp;
import com.zykj.xishuashua.R;
import com.zykj.xishuashua.http.AbstractHttpHandler;
import com.zykj.xishuashua.http.HttpErrorHandler;
import com.zykj.xishuashua.http.HttpUtils;
import com.zykj.xishuashua.utils.StringUtil;
import com.zykj.xishuashua.utils.TextUtil;
import com.zykj.xishuashua.utils.Tools;
import com.zykj.xishuashua.view.MyRequestDailog;

public class LoginActivity extends BaseActivity implements Callback,PlatformActionListener{
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR= 3;
	private static final int MSG_AUTH_COMPLETE = 4;
	
	//private static final String TAG = LoginActivity.class.getName();
	
	private ImageView back;
	private EditText uu_username,uu_password;

	private Handler handler;
	private int thirdLogin = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_user_login);
        //device_token = UmengRegistrar.getRegistrationId(this);
		handler = new Handler(this.getMainLooper(),this);
		ShareSDK.initSDK(this);
		initView();
	}

	private void initView() {
		back = (ImageView)findViewById(R.id.aci_back_btn);

		uu_username = (EditText)findViewById(R.id.uu_username);//用户名
		uu_password = (EditText)findViewById(R.id.uu_password);//密码
		Button app_league_in = (Button)findViewById(R.id.app_league_in);//商家加盟
		Button login_in = (Button)findViewById(R.id.app_login_in);//登录

		TextView forgetpwd = (TextView)findViewById(R.id.forgetpwd);//忘记密码
		TextView login_register = (TextView)findViewById(R.id.login_register);//没有账号?
		TextView login_quickly = (TextView)findViewById(R.id.login_quickly);//手机号快速注册
		
		TextView login_weibo = (TextView)findViewById(R.id.login_weibo);//微博
		TextView login_qq = (TextView)findViewById(R.id.login_qq);//qq
		TextView login_weixin = (TextView)findViewById(R.id.login_weixin);//微信
		
		setListener(back,app_league_in,forgetpwd,login_register,login_quickly,login_weibo,login_qq,login_weixin,app_league_in,login_in);//点击事件
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.aci_back_btn:
			startActivity(new Intent(this, MainActivity.class));
			MainActivity.switchTabActivity(2);
			break;
		case R.id.app_league_in://商家加盟
			break;
		case R.id.app_login_in://登录
	        String username=uu_username.getText().toString().trim();
	        final String password=uu_password.getText().toString().trim();
	        if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password)){
	            Tools.toast(LoginActivity.this, "请先填写账号和密码");
	            return;
	        }else if(!TextUtil.isMobile(username)){
	            Tools.toast(LoginActivity.this, "手机号格式不对");
	            return;
	        }
			RequestParams params = new RequestParams();
			params.put("login_name", username);
			params.put("login_password", password);
			params.put("lati", BaseApp.getModel().getLatitude());
			params.put("longi", BaseApp.getModel().getLongitude());
	        HttpUtils.login(new AbstractHttpHandler() {
				@Override
				public void onJsonSuccess(JSONObject json) {
			       String status= json.getString("result");
			        if(TextUtils.isEmpty(status) || !status.equals("1")){
			            Tools.toast(LoginActivity.this, "账号密码错误");
			        }else{
						JSONObject data = json.getJSONObject("data");
						BaseApp.getModel().setAvatar(data.getString("member_avatar"));//头像
						BaseApp.getModel().setMobile(data.getString("member_mobile"));//手机号
						BaseApp.getModel().setPassword(password);//登录密码
						BaseApp.getModel().setUserid(data.getString("member_id"));//用户Id
						BaseApp.getModel().setUsername(data.getString("member_name"));//登录账号
				        submitDeviceToken("device_token", data.getString("member_id"));
			        }
				}
			}, params);
			break;
		case R.id.forgetpwd://忘记密码
			startActivity(new Intent(LoginActivity.this,UserRegisterActivity.class).putExtra("type", "forget"));
			break;
		case R.id.login_register://没有账号?
			startActivity(new Intent(LoginActivity.this,UserRegisterActivity.class).putExtra("type", "register"));
			break;
		case R.id.login_quickly://手机号快速注册
			startActivity(new Intent(LoginActivity.this,UserRegisterActivity.class).putExtra("type", "register"));
			break;
		case R.id.login_weibo://微博
			thirdLogin = 1;
			MyRequestDailog.showDialog(this, "");
			Platform sina = ShareSDK.getPlatform(this, SinaWeibo.NAME);
			sina.setPlatformActionListener(this);
			sina.SSOSetting(true);
			sina.authorize();
			break;
		case R.id.login_qq://qq
			thirdLogin = 1;
			MyRequestDailog.showDialog(this, "");
			Platform qq = ShareSDK.getPlatform(this, QQ.NAME);
			qq.setPlatformActionListener(this);
			qq.SSOSetting(true);
			qq.authorize();
			break;
		case R.id.login_weixin://微信
			thirdLogin = 1;
			MyRequestDailog.showDialog(this, "");
			Platform wechat = ShareSDK.getPlatform(this, Wechat.NAME);
			wechat.setPlatformActionListener(this);
			wechat.SSOSetting(true);
			wechat.authorize();
			break;
		}
	}
	
	private void submitDeviceToken(String userid, final String member_id){
		HttpUtils.getchannelid(new HttpErrorHandler() {
			@Override
			public void onRecevieSuccess(JSONObject json) {
				setResult(Activity.RESULT_OK, getIntent().putExtra("member_id", member_id));
				MyRequestDailog.closeDialog();
				finish();
			}
			@Override
			public void onRecevieFailed(String status, JSONObject json) {
				MyRequestDailog.closeDialog();
			}
		}, BaseApp.getModel().getChannelid());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(thirdLogin == 1){
			MyRequestDailog.showDialog(this, "");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyRequestDailog.closeDialog();
	}

	@Override
	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		Message msg = new Message();
		msg.what = MSG_AUTH_COMPLETE;
		msg.obj = platform;
		handler.sendMessage(msg);
	}

	@Override
	public void onError(Platform platform, int action, Throwable t) {
		handler.sendEmptyMessage(MSG_AUTH_ERROR);
	}

	@Override
	public void onCancel(Platform platform, int action) {
		handler.sendEmptyMessage(MSG_AUTH_CANCEL);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		thirdLogin = 0;
		switch(msg.what) {
			case MSG_AUTH_CANCEL: {
				//取消授权
				MyRequestDailog.closeDialog();
				Tools.toast(this, "取消登录");
			} break;
			case MSG_AUTH_ERROR: {
				//授权失败
				MyRequestDailog.closeDialog();
				Tools.toast(this, "登录失败");
			} break;
			case MSG_AUTH_COMPLETE: {
				//授权成功
				Tools.toast(this, "登录成功");
				Platform platform = (Platform) msg.obj;
				String icon = platform.getDb().getUserIcon();
				String nickname = platform.getDb().getUserName();
				String userid = platform.getDb().getUserId();
				String gender = platform.getDb().getUserGender();
				RequestParams params = new RequestParams();
				params.put("openid", userid);
				params.put("member_name", nickname);
				params.put("member_sex", "m".equals(gender)?"1":"2");
				params.put("member_avatar", icon);
				params.put("member_lati", BaseApp.getModel().getLatitude());
				params.put("member_longi", BaseApp.getModel().getLongitude());
				HttpUtils.thirdpartlogin(new HttpErrorHandler() {
					@Override
					public void onRecevieSuccess(JSONObject json) {
						JSONObject data = json.getJSONObject("data");
						BaseApp.getModel().setAvatar(data.getString("member_avatar"));
						BaseApp.getModel().setUserid(data.getString("member_id"));//用户Id
						BaseApp.getModel().setUsername(data.getString("member_name"));//登录账号
				        submitDeviceToken("device_token", data.getString("member_id"));
					}
					@Override
					public void onRecevieFailed(String status, JSONObject json) {
						MyRequestDailog.closeDialog();
					}
				}, params);
			} break;
		}
		return false;
	}
}
