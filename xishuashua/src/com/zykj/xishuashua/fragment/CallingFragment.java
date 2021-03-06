package com.zykj.xishuashua.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.zykj.xishuashua.BaseApp;
import com.zykj.xishuashua.R;
import com.zykj.xishuashua.activity.GiftPerpetualActivity;
import com.zykj.xishuashua.activity.MainActivity;
import com.zykj.xishuashua.http.HttpErrorHandler;
import com.zykj.xishuashua.http.HttpUtils;
import com.zykj.xishuashua.utils.CommonUtils;
import com.zykj.xishuashua.utils.TextUtil;
import com.zykj.xishuashua.utils.Tools;
import com.zykj.xishuashua.view.MyDialog;
import com.zykj.xishuashua.view.MyRequestDailog;

public class CallingFragment extends Fragment implements OnClickListener{
	
	private String money;
	private EditText aci_mobile;
	private RadioGroup radioall;
	private Button aci_submit;
	private TextView alipay_allow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_user_calling,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        money=getArguments().getString("money");
        initView();
    }

	private void initView() {
		alipay_allow = (TextView)getView().findViewById(R.id.alipay_allow);
		alipay_allow.setText("余额："+money+"元");

		radioall = (RadioGroup)getView().findViewById(R.id.radioall);
		aci_mobile = (EditText)getView().findViewById(R.id.aci_mobile);
		aci_submit = (Button)getView().findViewById(R.id.aci_submit);
		
		TextView go_advice = (TextView)getView().findViewById(R.id.go_advice);
		TextView go_share = (TextView)getView().findViewById(R.id.go_share);
		setListener(go_advice, go_share, aci_submit);
	}

	public void setListener(View... view) {
		for (int i = 0; i < view.length; i++) {
			view[i].setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.go_advice:
			/** 刷广告 */
			MainActivity.switchTabActivity(1);
			getActivity().finish();
			break;
		case R.id.go_share:
			/** 继续分享 */
			if(!CommonUtils.CheckLogin()){ Tools.toast(getActivity(), "请先登录"); return; }
			getmemberinterests();
			break;
		case R.id.aci_submit:
			int checkedId = radioall.getCheckedRadioButtonId();
			final String money = checkedId == R.id.radio1?"10":checkedId == R.id.radio2?"30":"50";
			final String mobile = aci_mobile.getText().toString().trim();
			if(!TextUtil.isMobile(mobile)){
				Tools.toast(getActivity(), "手机号格式不对!");
			}else if(checkedId<0){
				Tools.toast(getActivity(), "请选择金额");
			}else{
				new MyDialog(getActivity(), R.style.MyDialog, "确认充值"+money+"元到手机?", "确定", "取消", new MyDialog.DialogClickListener() {
					@Override
					public void onRightBtnClick(Dialog dialog) {
						dialog.dismiss();
					}
					@Override
					public void onLeftBtnClick(final Dialog dialog) {
						RequestParams params = new RequestParams();
						params.put("pdc_amount", money);
						params.put("pdc_bank_name", "手机充值");
						params.put("pdc_bank_no", mobile);
						HttpUtils.phonerecharge(new HttpErrorHandler() {
							@Override
							public void onRecevieSuccess(JSONObject json) {
								dialog.dismiss();
								Tools.toast(getActivity(), "申请充值成功，我们将会在1-3工作日内审核充值！");
								getmemberenvelopes();
//								aci_mobile.setText("");
							}
							@Override
							public void onRecevieFailed(String status, JSONObject json) {
								super.onRecevieFailed(status, json);
								Tools.toast(getActivity(), "申请充值失败");
							}
						}, params);
					}
				}, false).show();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * @param viewId
	 * 获取用户选择的兴趣标签
	 */
	private void getmemberinterests(){
		HttpUtils.getmemberinterests(new HttpErrorHandler() {
			@Override
			public void onRecevieSuccess(JSONObject json) {
				JSONArray jsonArray = json.getJSONArray("list");
				String interestIds = "";
				for (int i = 0; i < jsonArray.size(); i++) {
					interestIds += jsonArray.getJSONObject(i).getString("interest_id")+",";
				}
				interestIds = interestIds.length()>0?interestIds.substring(0,interestIds.length()-1):"";
				startActivity(new Intent(getActivity(), GiftPerpetualActivity.class).putExtra("interestIds", interestIds));//永久红包
			}
		});
	}
	
	private void getmemberenvelopes(){
		MyRequestDailog.showDialog(getActivity(), "");
		HttpUtils.available_predeposit(new HttpErrorHandler() {
			@Override
			public void onRecevieSuccess(JSONObject json) {
				MyRequestDailog.closeDialog();
				JSONObject obj = json.getJSONObject("data");
				String money = obj.getString("available_predeposit");

				alipay_allow.setText("金额："+money+"元");
				BaseApp.getModel().setMoney(money);//红包金额
			}
		});
	}
}
