package com.zykj.xishuashua.activity;

import android.os.Bundle;
import android.view.View;

import com.zykj.xishuashua.BaseActivity;
import com.zykj.xishuashua.R;
import com.zykj.xishuashua.view.MyCommonTitle;

public class UserFormActivity extends BaseActivity{
	
	private MyCommonTitle myCommonTitle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_user_form);
		
		initView();
	}

	private void initView() {
		myCommonTitle = (MyCommonTitle)findViewById(R.id.aci_mytitle);
		myCommonTitle.setTitle("用户");
		myCommonTitle.setBackBtnVisible(false);
	}

	@Override
	public void onClick(View v) {
		
	}
}
