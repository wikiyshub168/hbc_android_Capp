package com.huangbaoche.hbcframe.data.net;


public class UrlLibs {

 	/**
 	 * 接口地址
 	 */

	public static String SERVER_HTTP_SCHEME_HTTP = "http://";
	public static String SERVER_HTTP_SCHEME_HTTPS = "https://";
	public static String SERVER_HTTP_SCHEME = SERVER_HTTP_SCHEME_HTTP;


	public static String SERVER_IP_HOST_PUBLIC_DEV = "api.dev.hbc.tech/";//开发
	public static String SERVER_IP_HOST_PUBLIC_EXAMINATION = "api.test.hbc.tech/";//test
	public static String SERVER_IP_HOST_PUBLIC_STAGE = "api2.huangbaoche.com/";//stage
	public static String SERVER_IP_HOST_PUBLIC_FORMAL = "api2.huangbaoche.com/";//生产

	public static String SERVER_IP_HOST_PUBLIC_DEFAULT = SERVER_IP_HOST_PUBLIC_DEV;//默认

	public static String SERVER_IP_HOST_PUBLIC = SERVER_HTTP_SCHEME+SERVER_IP_HOST_PUBLIC_DEFAULT;//主域名


// 	public static String SERVER_IP_PUBLIC_FOR_POI = "http://apig.huangbaoche.com/";//召展
 	public static String SERVER_IP_PUBLIC_FOR_POI = "https://api2.huangbaoche.com/";//poi



	//-------业务-------

	//ACCESS KEY
	public static String SERVER_IP_ACCESSKEY = SERVER_IP_HOST_PUBLIC +"passport/v1.0/getAccessKey?";
	//版本检测
	public static String SERVER_IP_CHECK_APP_VERSION = SERVER_IP_HOST_PUBLIC +"reflash/v1.0/checkAppVersion?";

	//首页
	public static String SERVER_IP_HOME = SERVER_IP_HOST_PUBLIC +"trade/v1.0/c/order/home?";


	//-------个人信息--------
	public static String SERVER_IP_PUBLIC_UER_CENTER = SERVER_IP_HOST_PUBLIC +"ucenter/v1.0/c/user/";
	/**验证码**/
	public static String SERVER_IP_CAPTCHA=SERVER_IP_PUBLIC_UER_CENTER+"captcha?";
	/**注册**/
	public static String SERVER_IP_REGISTER =SERVER_IP_PUBLIC_UER_CENTER+"register?";
	/**登录**/
	public static String SERVER_IP_LOGIN =SERVER_IP_PUBLIC_UER_CENTER+"login?";
	/**个人信息**/
	public static String SERVER_IP_INFORMATION =SERVER_IP_PUBLIC_UER_CENTER+"information?";
	/**修改个人信息**/
	public static String SERVER_IP_INFORMATION_UPDATE =SERVER_IP_PUBLIC_UER_CENTER+"information/update?";
	/**修改密码**/
	public static String SERVER_IP_PASSWORD_UPDATE =SERVER_IP_PUBLIC_UER_CENTER+"password/update?";
	/**忘记密码**/
	public static String SERVER_IP_PASSWORD_RESET =SERVER_IP_PUBLIC_UER_CENTER+"password/reset?";
	/**意见反馈**/
	public static String SERVER_IP_FEEDBACK_SAVE =SERVER_IP_PUBLIC_UER_CENTER+"feedback/save?";
	/**修改手机**/
	public static String SERVER_IP_MOBILE_UPDATE =SERVER_IP_PUBLIC_UER_CENTER+"mobile/update?";
	/**注销**/
	public static String SERVER_IP_LOGOUT =SERVER_IP_PUBLIC_UER_CENTER+"logout?";

	/**poi 地理搜索**/
	public static String SERVER_IP_POI =SERVER_IP_PUBLIC_FOR_POI+"poi/v1.0/c/places?";
	/**航班查询 通过航班号**/
	public static String SERVER_IP_FLIGHTS_BY_NO = SERVER_IP_HOST_PUBLIC +"flight/v1.0/c/flights?";
	/**航班查询 通过城市**/
	public static String SERVER_IP_FLIGHTS_BY_CITY = SERVER_IP_HOST_PUBLIC +"flight/v1.0/c/city/flights?";
	/**机场 暂时不用，使用DB**/
	public static String SERVER_IP_AIRPORT = SERVER_IP_HOST_PUBLIC +"price/v1.0/c/airports?";
	/**城市 暂时不用，使用DB**/
	public static String SERVER_IP_CITY = SERVER_IP_HOST_PUBLIC +"price/v1.0/c/cities?";



	//--------查询价格-------

	/**查询价格 接机**/
	public static String SERVER_IP_PRICE_PICKUP =UrlLibs.SERVER_IP_HOST_PUBLIC +"price/v1.0/c/airportPickupPrice?";
	/**查询价格 送机**/
	public static String SERVER_IP_PRICE_TRANSFER =UrlLibs.SERVER_IP_HOST_PUBLIC +"price/v1.0/c/airportTransferPrice?";
	/**查询价格 日租包车**/
	public static String SERVER_IP_PRICE_DAILY =UrlLibs.SERVER_IP_HOST_PUBLIC +"price/v1.1/c/dailyPrice?";
	/**查询价格 单次用车**/
	public static String SERVER_IP_PRICE_SINGLE =UrlLibs.SERVER_IP_HOST_PUBLIC +"price/v1.0/c/singlePrice?";

	//-------订单类--------
	public static String SERVER_IP_TRADE =UrlLibs.SERVER_IP_HOST_PUBLIC +"trade/v1.0/c/order/";

	public static String SERVER_IP_TRADE_1_1 =UrlLibs.SERVER_IP_HOST_PUBLIC +"trade/v1.1/c/order/";
	/**提交订单 接机**/
	public static String SERVER_IP_SUBMIT_PICKUP =UrlLibs.SERVER_IP_TRADE+"pickup?";
	/**提交订单 送机**/
	public static String SERVER_IP_SUBMIT_TRANSFER =UrlLibs.SERVER_IP_TRADE+"transfer?";
	/**提交订单 日租包车**/
	public static String SERVER_IP_SUBMIT_DAILY =UrlLibs.SERVER_IP_TRADE_1_1+"daily?";
	/**提交订单 单次用车**/
	public static String SERVER_IP_SUBMIT_SINGLE =UrlLibs.SERVER_IP_TRADE+"single?";
	/**取消订单**/
	public static String SERVER_IP_ORDER_CANCEL =UrlLibs.SERVER_IP_TRADE+"cancel?";
	/**修改订单**/
	public static String SERVER_IP_ORDER_EDIT =UrlLibs.SERVER_IP_TRADE_1_1+"edit?";
	/**订单列表**/
	public static String SERVER_IP_ORDER_LIST =UrlLibs.SERVER_IP_TRADE_1_1+"list?";
	/**订单详情**/
	public static String SERVER_IP_ORDER_DETAIL =UrlLibs.SERVER_IP_TRADE_1_1+"detail?";
	/**订单增项费用**/
	public static String SERVER_IP_ORDER_OVER_PRICE =UrlLibs.SERVER_IP_TRADE+"cost/additional?";

	/**对车导评价**/
	public static String SERVER_IP_GUIDES_COMMENTS =SERVER_IP_TRADE+"evaluate?";

	/**订单支付 支付宝**/
	public static String SERVER_IP_ORDER_PAY_ID =UrlLibs.SERVER_IP_HOST_PUBLIC +"trade/v1.0/c/pay/getmobilepayurl?";

	/**优惠券**/
	public static String SERVER_IP_COUPONS =UrlLibs.SERVER_IP_HOST_PUBLIC +"marketing/v1.0/c/coupons?";
	/**优惠券 绑定**/
	public static String SERVER_IP_COUPONS_BIND =UrlLibs.SERVER_IP_HOST_PUBLIC +"marketing/v1.0/c/coupons/bind?";

	/**图片上传**/
	public static String SERVER_IP_PIC_UPLOAD =UrlLibs.SERVER_IP_HOST_PUBLIC +"file/v1.0/upload?";

	/**IM token**/
	public static String SERVER_IP_IM_TOKEN =UrlLibs.SERVER_IP_HOST_PUBLIC +"communication/v1.0/e/im/token?";
	/**IM 通知更新为已读**/
	public static String SERVER_IP_IM_UPDATE =UrlLibs.SERVER_IP_HOST_PUBLIC +"communication/v1.0/c/im/update?";
	/**IM 通知更新为已读**/
	public static String SERVER_IP_IM_TOKEN_UPDATE =UrlLibs.SERVER_IP_HOST_PUBLIC +"trade/v1.0/c/order/im/token/update?";


}
