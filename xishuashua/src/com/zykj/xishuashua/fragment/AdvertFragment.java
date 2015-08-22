package com.zykj.xishuashua.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.loopj.android.http.RequestParams;
import com.zykj.xishuashua.R;
import com.zykj.xishuashua.activity.GiftDetailActivity;
import com.zykj.xishuashua.adapter.GiftAdapter;
import com.zykj.xishuashua.http.EntityHandler;
import com.zykj.xishuashua.http.HttpUtils;
import com.zykj.xishuashua.model.Gift;
import com.zykj.xishuashua.utils.CommonUtils;
import com.zykj.xishuashua.utils.Tools;
import com.zykj.xishuashua.view.MyRequestDailog;
import com.zykj.xishuashua.view.XListView;
import com.zykj.xishuashua.view.XListView.IXListViewListener;

/**
 * @author Administrator
 * 红包通知里面的列表
 */
public class AdvertFragment extends Fragment implements IXListViewListener,OnItemClickListener{

    private String isperpetual="0";

	private static final String NUM = "5";//每页显示条数
	private int page = 1;
	private List<Gift> gifts = new ArrayList<Gift>();
	private GiftAdapter adapter;
    private XListView myListView;
	private Handler mHandler;

    public static AdvertFragment newInstance(String isperpetual){
    	AdvertFragment shopAssessFragment = new AdvertFragment();
        Bundle bundle=new Bundle();
        bundle.putString("iscontaintext",isperpetual);
        shopAssessFragment.setArguments(bundle);
        return shopAssessFragment;
    }

    /**
     * 加载页面
     */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myListView = new XListView(getActivity(), null);
        myListView.setDividerHeight(0);
        myListView.setLayoutParams(params);
        myListView.setPullLoadEnable(true);
		myListView.setXListViewListener(this);
        return myListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		mHandler = new Handler();
        isperpetual=getArguments().getString("iscontaintext");
        adapter = new GiftAdapter(getActivity(), R.layout.ui_item_gift, gifts, "0");
		myListView.setAdapter(adapter);
		myListView.setOnItemClickListener(this);
        requestData();
    }

    /**
     * 请求红包列表
     */
    public void requestData() {
    	RequestParams params = new RequestParams();
    	params.put("marketprice", isperpetual);
    	params.put("page", String.valueOf(page));
    	params.put("per_page", NUM);
		MyRequestDailog.showDialog(getActivity(), "");
		HttpUtils.getEnveList(new EntityHandler<Gift>(Gift.class) {
			@Override
			public void onReadSuccess(List<Gift> list) {
				MyRequestDailog.closeDialog();
				if(page == 1){gifts.clear();}
				gifts.addAll(list);
				adapter.notifyDataSetChanged();
			}
		}, params);
    }

	private void onLoad() {
		myListView.stopRefresh();
		myListView.stopLoadMore();
		myListView.setRefreshTime("刚刚");
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				page = 1;
				requestData();
				onLoad();
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				page += 1;
				requestData();
				onLoad();
			}
		}, 1000);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(!CommonUtils.CheckLogin()){ Tools.toast(getActivity(), "请先登录"); return; }
		Intent detailIntent = new Intent(getActivity(), GiftDetailActivity.class);
		detailIntent.putExtra("goods_id", gifts.get(position-1).getGoods_id());
		startActivity(detailIntent);
	}
}