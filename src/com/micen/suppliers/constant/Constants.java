package com.micen.suppliers.constant;


public class Constants
{
	public static String APP_FINISH_ACTION = "com.micen.suppliers.finish";
	public static String APP_RELOGIN_ACTION = "com.micen.suppliers.relogin";
	// App标示，用于消息推送注册以及接收信息的验证
	public static String APP_KEY = "supplier";
	// 根据后台接口配置，进行更改相应的接口
	public static String APP_PORT_VERSION_CODE = "2";

	public static String sharedPreDBName = "smallDatabase";

	public static float density = 0;

	public static String enDateTemplate = "MMM dd, yyyy";

	public static final Long DAY_TIME = 86400000L;

	/**
	 * 日期选择器最小时间常量（距离今天的时间差）
	 */
	public static int DAY_OFFSET = 6;

	/**
	 * Flag for develop mode
	 */
	public static final boolean DEV_MODE = false;

	/**
	 * 发送询盘请求码
	 */
	public static int SEND_INQUIRE_CODE = 0x00000000;
	/**
	 * 删除询盘后，返回上一个界面进行界面数据刷新标识
	 */
	public static int REFRESH_MESSAGE_CODE = 0x00000001;

	/**
	 * 邮件回复时，默认原文最大字符数
	 */
	public static final int REPLAY_MESSAGE_LENGTH = 4000;

	/**
	 * 添加快捷询盘内容最大字符数
	 */
	public static final int ADD_SHORT_CUT_LENGTH = 400;
	/**
	 * 百度词典
	 */
	public static final String API_KEY = "jgxG3cQ3k6OWCrVN1LDDWEkw";
	/**
	 * Umeng数据埋点Key
	 */
	public static final String UMENG_APPKEY = "55c9adcf67e58e7a8000409a";
	/**
	 * Umeng推广渠道
	 */
	public static final String UMENG_CHANNEL = "Wandoujia";
}
