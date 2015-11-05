package com.zykj.xishuashua.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zykj.xishuashua.BaseActivity;
import com.zykj.xishuashua.R;
import com.zykj.xishuashua.view.MyCommonTitle;

public class UserLeagueActivity extends BaseActivity{
	
	private MyCommonTitle myCommonTitle;
	private int type;
	private TextView setting_content,setting_link;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_user_league);
		type = getIntent().getIntExtra("type", 1);
		
		initView();
	}

	private void initView() {
		myCommonTitle = (MyCommonTitle)findViewById(R.id.aci_mytitle);
		myCommonTitle.setTitle(type == 1?"公司简介":type == 2?"帮助":type == 3?"申请加盟":"版本更新");

		setting_content = (TextView)findViewById(R.id.setting_content);//内容
		setting_link = (TextView)findViewById(R.id.setting_link);//内容

		switch (type) {
		case 1:
			setting_content.setText("公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介公司简介");
			break;
		case 2:
			setting_content.setText("        闲刷是商家将广告内容和现金红包绑定按地理位置进行推送给用户的传播平台。系统有三个身份参与。普通注册用户，可以收到红包和广告；商家，发送广告和红包；系统管理员，可以管理注册用户和商家。商家在系统发送广告和红包金额，符合要求的用户便可以收到提示，点击便 收看广告和红包实现进帐。");
			setting_link.setVisibility(View.GONE);
			break;
		case 3:
			setting_content.setText("申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟申请加盟");
			break;
		case 4:
			setting_content.setText("已是最新版本");
			setting_link.setVisibility(View.GONE);
			break;
		}
	}
}
