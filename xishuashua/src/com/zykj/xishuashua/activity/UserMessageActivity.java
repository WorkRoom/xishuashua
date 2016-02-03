package com.zykj.xishuashua.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSONObject;
import com.baidu.android.pushservice.PushManager;
import com.zykj.xishuashua.BaseActivity;
import com.zykj.xishuashua.R;
import com.zykj.xishuashua.http.HttpErrorHandler;
import com.zykj.xishuashua.http.HttpUtils;
import com.zykj.xishuashua.view.MyCommonTitle;

public class UserMessageActivity extends BaseActivity{
	
	private MyCommonTitle myCommonTitle;
	private ToggleButton toggle_push,toggle_sound;
	private String push,sound;
	private int statu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_user_msg);
		
		initView();
		requestData();
	}

	private void initView() {
		myCommonTitle = (MyCommonTitle)findViewById(R.id.aci_mytitle);
		myCommonTitle.setTitle("消息提示");
		
		toggle_push = (ToggleButton)findViewById(R.id.toggle_push);//推送设置
		toggle_sound = (ToggleButton)findViewById(R.id.toggle_sound);//消息音提示
		
		setListener(toggle_push, toggle_sound);
	}

	/**
	 * 请求推送设置和消息提示音状态
	 */
	private void requestData(){
		HttpUtils.getpushstate(new HttpErrorHandler() {
			@Override
			public void onRecevieSuccess(JSONObject json) {
				statu = json.getIntValue("data");
				toggle_push.setChecked(1 == statu);
			}
		});
		
		
		
		
//		HttpUtils.getpushandhint(new HttpErrorHandler() {
//			@Override
//			public void onRecevieSuccess(JSONObject json) {
//				push = json.getJSONObject("list").getString("member_push");
//				sound = json.getJSONObject("list").getString("member_hint");
//				toggle_push.setChecked("1".equals(push));
//				toggle_sound.setChecked("1".equals(sound));
//				if("1".equals(push)){
////					mPushAgent.disable();
//				}else{
////					mPushAgent.enable();
//				}
//				if("1".equals(sound)){
////					mPushAgent.setNoDisturbMode(23, 0, 6, 0);
//				}else{
////					mPushAgent.setNoDisturbMode(0, 0, 0, 0);
//				}
//			}
//		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.toggle_push:
			HttpUtils.setpushstate(new HttpErrorHandler() {
				@Override
				public void onRecevieSuccess(JSONObject json) {
					if(1 == statu && 1==json.getIntValue("result")){
						statu=0;
						toggle_push.setChecked(false);
						if(PushManager.isPushEnabled(UserMessageActivity.this))
							PushManager.stopWork(UserMessageActivity.this);
					}else if(0 == statu && 1==json.getIntValue("result")){
						statu=1;
						toggle_push.setChecked(true);
						if(!PushManager.isPushEnabled(UserMessageActivity.this))
							PushManager.resumeWork(UserMessageActivity.this);
					}
				}
			});
			break;
		case R.id.toggle_sound:
			break;
		default:
			break;
		}
//		push = view.getId() == R.id.toggle_push?"1".equals(push)?"0":"1":push;
//		sound = view.getId() == R.id.toggle_sound?"1".equals(sound)?"0":"1":sound;
//		HttpUtils.setpushandhint(new HttpErrorHandler() {
//			@Override
//			public void onRecevieSuccess(JSONObject json) {
//				toggle_push.setChecked("1".equals(push));
//				toggle_sound.setChecked("1".equals(sound));
//			}
//		}, push, sound);
	}
}
