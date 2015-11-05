package com.zykj.xishuashua.http;

/**
 * @author Administrator
 * 服务器路径
 */
public class UrlContants {

    public static final String BASE_URL = "http://115.28.167.43/appif/api.php?";//192.168.199.174//115.28.167.43
    
    public static final String ABATARURL = "http://115.28.167.43/data/upload/shop/avatar/";//头像
    
    public static final String GIFTIMGURL = "http://115.28.167.43/data/upload/shop/store/goods/";//红包图片

    public static final String BASEURL = BASE_URL+"%s";

    public static final String jsonData = "list";

    public static final String ERROR = "{\"result\":0,\"result_text\":\"\",\"list\":[]}";

    public static final String LOGIN = "m=user&a=login";//登录

    public static final String MEMBERINFO = "m=user&a=getmember_info";//用户信息
    
    public static final String REGISTER = "m=user&a=register";//注册

    public static final String FORGETPWD = "m=user&a=resetPassword";//忘记密码
    
    public static final String AVATAR = "m=user&a=uploadtouxiang";//上传头像
    
    public static final String UPDATEUSERINFO= "m=user&a=edit";//修改用户信息

    public static final String SPECIALLIST = "m=user&a=getSpecialList";//获轮播图

    public static final String NEWSLIST = "m=user&a=getnewslist";//获取新闻资讯
    
    public static final String ENVELIST = "m=user&a=getgoodslist";//获取红包列表
    
    public static final String FORONEUSER = "m=user&a=getenvelist_foroneuser";//获取红包通知

    public static final String GOODDETAIL = "m=user&a=getGoodsDetaiL";//红包详情

    public static final String MEMBERENVELOPE = "m=user&a=getmemberenvelopes";//红包详情
    
    public static final String GEIPUSHANDHINT = "m=user&a=getpushandhint";//获取推送设置和消息提示音状态
    
    public static final String SEIPUSHANDHINT = "m=user&a=setpushandhint";//获取推送设置和消息提示音状态
    
    public static final String GETMEMBERCOLLECT = "m=user&a=getmembercollect_withdetail";//我的收藏
    
    public static final String GETALLINTEREST = "m=user&a=getallinterests";//兴趣标签
    
    public static final String MEMBERINTEREST = "m=user&a=getmemberinterests";//获取用户已选标签
    
    public static final String ADDINTERESTSMETA = "m=user&a=addinterestsmeta";//提交用户选择的标签
    
    public static final String CLICKFAVORITE = "m=user&a=clickfavorite";//用户点赞红包
    
    public static final String CLICKCOLLECT = "m=user&a=clickcollect";//用户收藏红包
    
    public static final String SUBMITCOMMENT = "m=user&a=submitcomment";//用户提交评论
    
    public static final String GETCOMMENT = "m=user&a=getcomment";//评论列表
    
    public static final String SOMEKINDENVELIST = "m=user&a=getsomekindenvelist";//获取红包
    
    public static final String GETENVEPOINTS = "m=user&a=getenvePoints";//抢红包
    
    public static final String GETCHANNELID = "m=user&a=getchannelid";//上传设备Id
    
    public static final String POINTSFORSHARE = "m=user&a=getenvePointsForShare";//分享红包获得钱
    
    public static final String COMMENTFAVORITE = "m=user&a=clickCommentFavorite";//评论点赞
    
    public static final String DELETECOLLECT = "m=user&a=deletecollect";//删除收藏订单
    
    public static final String THIRDPARTLOGIN = "m=user&a=thirdpartlogin";//第三方登录
    
    public static final String PERTEMCOUNT = "m=user&a=get_per_tem_count";//获取首页即时、永久未读红包
    
    public static final String NOTICECOUNT = "m=user&a=getnoticecount";//红包通知未读数
    
    public static final String PDCASHADD = "m=user&a=pd_cash_add";//提现
    
    public static final String AVAILABLEPREDEPOSIT = "m=user&a=pd_cash_add_record_available_predeposit";//获取用户余额以及红包个数
    
    public static final String PHONERECHARGE= "m=user&a=pd_cash_add_phonerecharge";//手机充值
    
    public static final String SWITCHSTATE= "m=user&a=push_switch_state";//获取推送状态
    
    public static final String SETPUSHSWITCH= "m=user&a=push_swith";//设置推送开关
    
    public static String getUrl(String token){
        if(token==null || token.equals("")){
            return BASE_URL;
        }
      return  String.format(BASEURL,token);
    }
}
