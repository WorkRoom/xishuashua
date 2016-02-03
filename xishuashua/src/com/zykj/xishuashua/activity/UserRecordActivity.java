package com.zykj.xishuashua.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

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
import com.zykj.xishuashua.view.XListView;
import com.zykj.xishuashua.view.XListView.IXListViewListener;

/**
 * @author Administrator
 * 我的收益记录
 */
public class UserRecordActivity extends BaseActivity implements IXListViewListener{
	
	private MyCommonTitle myCommonTitle;
    private XListView myListView;
	private CommonAdapter<JSONObject> recordAdapter;
	private Handler mHandler = new Handler();//异步加载或刷新
	private List<JSONObject> records = new ArrayList<JSONObject>();
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
		
		myListView = (XListView)findViewById(R.id.advert_listview);
		recordAdapter = new CommonAdapter<JSONObject>(UserRecordActivity.this, R.layout.ui_item_record, records) {
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
						holder.setText(R.id.record_name, "手机充值".equals(bankName)?"话费提现":"支付宝提现".equals(bankName)?"支付宝提现":bankName)
							.setText(R.id.record_money, "提现金额"+jsonObject.getString("pdc_amount")+"元")
							.setVisibility(R.id.gift_label, false)
							.setText(R.id.record_address, time);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		myListView.setAdapter(recordAdapter);
		myListView.setDividerHeight(0);
		myListView.setPullLoadEnable(false);
		myListView.setXListViewListener(this);
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
				for (int i = 0; i < (jsonArray==null?0:jsonArray.size()); i++) {
					list.add(jsonArray.getJSONObject(i));
				}
				records.clear();
				records.addAll(list);
				recordAdapter.notifyDataSetChanged();
			}
		});
    }

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onRefresh() {
		/**下拉刷新 重建*/
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				requestData();
				myListView.stopRefresh();
				myListView.setRefreshTime("刚刚");
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {
	}
}