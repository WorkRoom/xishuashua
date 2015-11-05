package com.zykj.xishuashua.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zykj.xishuashua.BaseActivity;
import com.zykj.xishuashua.R;
import com.zykj.xishuashua.adapter.CommonAdapter;
import com.zykj.xishuashua.adapter.ViewHolder;
import com.zykj.xishuashua.http.HttpErrorHandler;
import com.zykj.xishuashua.http.HttpUtils;
import com.zykj.xishuashua.utils.DateUtil;
import com.zykj.xishuashua.utils.StringUtil;
import com.zykj.xishuashua.view.MyCommonTitle;

/**
 * @author Administrator
 * 我的收益记录
 */
public class UserRecordActivity extends BaseActivity{
	
	private MyCommonTitle myCommonTitle;
    private ListView myListView;
//	private ArrayList<MyEnvelope> envelist;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_index_list);
//		envelist = getIntent().getParcelableArrayListExtra("envelist");
		
		initView();
		requestData();
	}

	/**
	 * 加载页面
	 */
	private void initView() {
		myCommonTitle = (MyCommonTitle)findViewById(R.id.aci_mytitle);
		myCommonTitle.setTitle("收益记录");
		
		myListView = (ListView)findViewById(R.id.advert_listview);
	}

    /**
     * 请求数据
     */
	private void requestData() {
		HttpUtils.getmemberenvelopes(new HttpErrorHandler() {
			@Override
			public void onRecevieSuccess(JSONObject json) {
				JSONArray jsonArray = json.getJSONArray("list");
				List<JSONObject> list = new ArrayList<JSONObject>();
				for (int i = 0; i < jsonArray.size(); i++) {
					list.add(jsonArray.getJSONObject(i));
				}
				myListView.setAdapter(new CommonAdapter<JSONObject>(UserRecordActivity.this, R.layout.ui_item_record, list) {
					@Override
					public void convert(ViewHolder holder, JSONObject jsonObject) {
						try {
							String bankName = jsonObject.getString("pdc_bank_name");
							if(StringUtil.isEmpty(bankName)){
								String enveTime = jsonObject.getString("membersawgoods_lastsawtime");
								String timeStr = StringUtil.isEmpty(enveTime)?System.currentTimeMillis()+"":enveTime+"000";
								String time = DateUtil.longToString(Long.parseLong(timeStr), "yyyy-MM-dd");
								holder.setText(R.id.record_name, jsonObject.getString("enve_serial")+"："+jsonObject.getString("store_name"))
									.setText(R.id.record_money, "浏览/分享红包"+jsonObject.getString("membersawgoods_gotpoint")+"元")
									.setText(R.id.gift_label, jsonObject.getString("enve_serial"))
									.setText(R.id.record_address, time);
							}else{
								String enveTime = jsonObject.getString("pdc_payment_time");
								String timeStr = StringUtil.isEmpty(enveTime)?System.currentTimeMillis()+"":enveTime+"000";
								String time = DateUtil.longToString(Long.parseLong(timeStr), "yyyy-MM-dd");
								holder.setText(R.id.record_name, "手机充值".equals(bankName)?"话费提现":"支付宝提现")
									.setText(R.id.record_money, "提现金额"+jsonObject.getString("pdc_amount")+"元")
									.setVisibility(R.id.gift_label, false)
									.setText(R.id.record_address, time);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
    }

	@Override
	public void onClick(View v) {
		
	}
}
