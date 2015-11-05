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
import com.zykj.xishuashua.utils.StringUtil;
import com.zykj.xishuashua.utils.TextUtil;
import com.zykj.xishuashua.utils.Tools;
import com.zykj.xishuashua.view.MyDialog;
import com.zykj.xishuashua.view.MyRequestDailog;

public class AlipayFragment extends Fragment implements OnClickListener{
	
	private String money;
	private EditText aci_mobile,aci_name,aci_money,aci_num;
	private Button aci_submit;
	private TextView alipay_allow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_user_alipay,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        money=getArguments().getString("money");
        initView();
    }

	private void initView() {
		alipay_allow = (TextView)getView().findViewById(R.id.alipay_allow);
		alipay_allow.setText("可提现金额："+money+"元");

		aci_mobile = (EditText)getView().findViewById(R.id.aci_mobile);
		aci_name = (EditText)getView().findViewById(R.id.aci_name);
		aci_money = (EditText)getView().findViewById(R.id.aci_money);
		aci_num = (EditText)getView().findViewById(R.id.aci_num);
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
			final String mobile = aci_mobile.getText().toString().trim();
			final String name = aci_name.getText().toString().trim();
			final String money = aci_money.getText().toString().trim();
			final String num = aci_num.getText().toString().trim();
			if(StringUtil.isEmpty(mobile)){
				Tools.toast(getActivity(), "支付宝账号不能为空!");
			}else if(StringUtil.isEmpty(name)){
				Tools.toast(getActivity(), "支付宝用户名不能为空!");
			}else if(StringUtil.isEmpty(money) && Float.valueOf(money) <= 0){
				Tools.toast(getActivity(), "支付宝金额不对!");
			}else if(!TextUtil.isMobile(num)){
				Tools.toast(getActivity(), "手机号格式不对!");
			}else{
				new MyDialog(getActivity(), R.style.MyDialog, "确认提现"+money+"元到支付宝?", "确定", "取消", new MyDialog.DialogClickListener() {
					@Override
					public void onRightBtnClick(Dialog dialog) {
						dialog.dismiss();
					}
					@Override
					public void onLeftBtnClick(Dialog dialog) {
						RequestParams params = new RequestParams();
						params.put("pdc_amount", Float.valueOf(money));//提现金额
						params.put("pdc_bank_name", "支付宝");
						params.put("pdc_bank_no", mobile);//支付宝账号
						params.put("pdc_bank_user", name);//支付宝用户名
						params.put("member_phone", num);//联系人手机号
						HttpUtils.paCashAdd(new HttpErrorHandler() {
							@Override
							public void onRecevieSuccess(JSONObject json) {
								Tools.toast(getActivity(), "申请提现成功，我们将会在1-3工作日内审核提现！");
								getmemberenvelopes();
								aci_mobile.setText("");
								aci_name.setText("");
								aci_money.setText("");
								aci_num.setText("");
							}
							@Override
							public void onRecevieFailed(String status, JSONObject json) {
								super.onRecevieFailed(status, json);
								Tools.toast(getActivity(), "申请提现失败");
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

				alipay_allow.setText("可提现金额："+money+"元");
				BaseApp.getModel().setMoney(money);//红包金额
			}
		});
	}
}
