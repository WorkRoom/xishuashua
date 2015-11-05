package com.zykj.xishuashua.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.zykj.xishuashua.BaseActivity;
import com.zykj.xishuashua.R;
import com.zykj.xishuashua.adapter.StoreGiftAdapter;
import com.zykj.xishuashua.http.EntityHandler;
import com.zykj.xishuashua.http.HttpErrorHandler;
import com.zykj.xishuashua.http.HttpUtils;
import com.zykj.xishuashua.model.Gift;
import com.zykj.xishuashua.utils.StringUtil;
import com.zykj.xishuashua.utils.Tools;
import com.zykj.xishuashua.view.MyCommonTitle;
import com.zykj.xishuashua.view.MyRequestDailog;
import com.zykj.xishuashua.view.XListView;
import com.zykj.xishuashua.view.XListView.IXListViewListener;

public class UserStoreActivity extends BaseActivity implements IXListViewListener,OnItemClickListener{

	private static final String NUM = "5";//每页显示条数
	private int page = 1;//////////////////////////////////////////////////////////////////
	private Handler mHandler = new Handler();
	
	private MyCommonTitle myCommonTitle;
	private RelativeLayout bottom_bar;
	private Button btn_delete;
    private XListView myListView;
	private StoreGiftAdapter adapter;
	private List<Gift> gifts = new ArrayList<Gift>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_index_news);
		
		initView();
		requestData();
	}

	/**
	 * 加载页面
	 */
	private void initView() {
		myCommonTitle = (MyCommonTitle)findViewById(R.id.aci_mytitle);
		myCommonTitle.setEditTitle("编辑");
		myCommonTitle.setLisener(this, null);
		myCommonTitle.setTitle("我的收藏");
		bottom_bar = (RelativeLayout)findViewById(R.id.bottom_bar);
		btn_delete = (Button)findViewById(R.id.btn_delete);
		setListener(btn_delete);
		
		myListView = (XListView)findViewById(R.id.advert_listview);
		adapter = new StoreGiftAdapter(UserStoreActivity.this, R.layout.ui_item_gift, gifts);
		myListView.setAdapter(adapter);
		myListView.setDividerHeight(0);
		myListView.setPullLoadEnable(true);
		myListView.setXListViewListener(this);
		myListView.setOnItemClickListener(this);

		handler.sendEmptyMessage(1);
	}
	

    @Override
	public void onClick(View view) {
    	switch (view.getId()) {
		case R.id.aci_edit_btn:
			/**编辑*/
			adapter.setChecked(!adapter.isChecked());
			bottom_bar.setVisibility(adapter.isChecked()?View.VISIBLE:View.GONE);
			myCommonTitle.setEditTitle(adapter.isChecked()?"取消":"编辑");
			break;
		case R.id.btn_delete:
			/**删除*/
			new AlertDialog.Builder(this).setTitle("删除").setMessage("您确定要删除吗?")
	            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialogInterface, int position) {
					StringBuffer str = new StringBuffer();
					for (int i = 0; i < gifts.size(); i++) {
		                if(gifts.get(i).isChecked()){
		                    String id= gifts.get(i).getGoods_id();
		                    str.append(id + ",");
		                }
					}
		            RequestParams giftdel = new RequestParams();
		            giftdel.put("goods_id",str.substring(0,str.length()-1));
		            HttpUtils.deletecollect(new HttpErrorHandler() {
						@Override
						public void onRecevieSuccess(JSONObject json) {
		        			Tools.toast(UserStoreActivity.this, "删除成功");
		    				page = 1;
		    				requestData();
						}
					},giftdel);
		        }
	     	}).setNegativeButton("取消",null).show();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View convertView, int position, long checkedId) {
		Gift gift = gifts.get(position-1);
        if(!adapter.isChecked()){
            /**收藏详情界面*/
    		if("news".equals(gift.getStore_name())){
    			startActivity(new Intent(UserStoreActivity.this, IndexNewDetailActivity.class).putExtra("newId", gift.getGoods_id()));
    		}else{
    			startActivity(new Intent(UserStoreActivity.this, GiftDetailActivity.class)
    				.putExtra("goods_id", gift.getGoods_id()).putExtra("saw", gift.getSaw()));
    		}
        }else{
        	gift.setChecked(!gift.isChecked());
            adapter.notifyDataSetChanged();
        }
	}

	/**
     * 请求数据
     */
    public void requestData() {
		MyRequestDailog.showDialog(this, "");
		RequestParams params = new RequestParams();
		params.put("page", page);//当前第几页
		params.put("per_page", NUM);//每页条数
		HttpUtils.getmembercollect(new EntityHandler<Gift>(Gift.class) {
			@Override
			public void onReadSuccess(List<Gift> list) {
				MyRequestDailog.closeDialog();
				if(page == 1){gifts.clear();}
				gifts.addAll(list);
				adapter.notifyDataSetChanged();
			}
		}, params);
    }
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				//①：其实在这块需要精确计算当前时间
				for(int index =0;index<gifts.size();index++){
					Gift gift = gifts.get(index);
					if(gift.getCurrentSeconds() == null){
						long continueTime = Long.parseLong(StringUtil.toString(gift.getGoods_marketprice(), "0"));
						long startTime = Long.parseLong(StringUtil.toString(gift.getGoods_selltime(), "0"));
						long seconds = startTime + continueTime - System.currentTimeMillis()/1000;
						gift.setCurrentSeconds(String.valueOf(seconds));
					}
					long time = Long.parseLong(gift.getCurrentSeconds());
					if(time>1){//判断是否还有条目能够倒计时，如果能够倒计时的话，延迟一秒，让它接着倒计时
						gift.setCurrentSeconds(String.valueOf(time-1));
					}else{
						gift.setCurrentSeconds("0");
					}
				}
				//②：for循环执行的时间
				adapter.notifyDataSetChanged();
				handler.sendEmptyMessageDelayed(1, 1000);
				break;
			}
		}
	};

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

	private void onLoad() {
		myListView.stopRefresh();
		myListView.stopLoadMore();
		myListView.setRefreshTime("刚刚");
	}
}
