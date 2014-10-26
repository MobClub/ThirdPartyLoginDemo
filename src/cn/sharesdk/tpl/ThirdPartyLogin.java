package cn.sharesdk.tpl;

import java.util.HashMap;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.sharesdk.demo.tpl.R;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.FakeActivity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/** 中文注释
 * ShareSDK 官网地址 ： http://www.mob.com </br>
 *1、这是用2.38版本的sharesdk，一定注意  </br>
 *2、如果要咨询客服，请加企业QQ 4006852216 </br>
 *3、咨询客服时，请把问题描述清楚，最好附带错误信息截图 </br>
 *4、一般问题，集成文档中都有，请先看看集成文档；减少客服压力，多谢合作  ^_^</br></br></br>
 *
 *The password of demokey.keystore is 123456
 **ShareSDK Official Website ： http://www.mob.com </br>
 *1、Be carefully, this sample use the version of 2.11 sharesdk  </br>
 *2、If you want to ask for help，please add our QQ whose number is 4006852216 </br>
 *3、Please describe detail of the question , if you have the picture of the bugs or the bugs' log ,that is better </br>
 *4、Usually, the answers of some normal questions is exist in our user guard pdf, please read it more carefully,thanks  ^_^
*/
public class ThirdPartyLogin extends FakeActivity implements OnClickListener, Callback, PlatformActionListener {
	private static final int MSG_SMSSDK_CALLBACK = 1;
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR= 3;
	private static final int MSG_AUTH_COMPLETE = 4;
	
	private String smssdkAppkey;
	private String smssdkAppSecret;
	private OnLoginListener signupListener;
	private Handler handler;
	//短信验证的对话框
	private Dialog msgLoginDlg;

	/** 填写从短信SDK应用后台注册得到的APPKEY和APPSECRET */
	public void setSMSSDKAppkey(String appkey, String appSecret) {
		smssdkAppkey = appkey;
		smssdkAppSecret = appSecret;
	}
	
	/** 设置授权回调，用于判断是否进入注册 */
	public void setOnLoginListener(OnLoginListener l) {
		this.signupListener = l;
	}
	
	public void onCreate() {
		// 初始化ui
		handler = new Handler(this);
		activity.setContentView(R.layout.tpl_login_page);
		(activity.findViewById(R.id.tvMsgRegister)).setOnClickListener(this);
		(activity.findViewById(R.id.tvWeixin)).setOnClickListener(this);
		(activity.findViewById(R.id.tvWeibo)).setOnClickListener(this);
		(activity.findViewById(R.id.tvQq)).setOnClickListener(this);
		(activity.findViewById(R.id.tvOther)).setOnClickListener(this);
	}
	
	public void onDestroy() {
		//短信验证，在activity退出时，注销监听事件
		SMSSDK.unregisterAllEventHandler();
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.tvMsgRegister: {
				//短信登录
				popupMsgLogin();
				//Toast.makeText(this, "未完成短信登录", Toast.LENGTH_SHORT).show();
			} break;
			case R.id.tvWeixin: {
				//微信登录
				//测试时，需要打包签名；sample测试时，用项目里面的demokey.keystore
				//打包签名apk,然后才能产生微信的登录
				Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
				authorize(wechat);
			} break;
			case R.id.tvWeibo: {
				//新浪微博
				Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
				authorize(sina);
			} break;
			case R.id.tvQq: {
				//QQ空间
				Platform qzone = ShareSDK.getPlatform(QZone.NAME);
				authorize(qzone);
			} break;
			case R.id.tvOther: {
				//其他登录
				authorize(null);
			} break;
			case R.id.tvFacebook: {
				//facebook登录
				Dialog dlg = (Dialog) v.getTag();
				dlg.dismiss();
				Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
				authorize(facebook);
			} break;
			case R.id.tvTwitter: {
				//twitter 登录
				Dialog dlg = (Dialog) v.getTag();
				dlg.dismiss();
				Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
				authorize(twitter);
			} break;
		}
	}
	
	// 短信注册对话框
	private void popupMsgLogin() {
		msgLoginDlg = new Dialog(activity, R.style.WhiteDialog);
		View dlgView = View.inflate(activity, R.layout.tpl_msg_login_dialog, null);
		final EditText etPhone = (EditText) dlgView.findViewById(R.id.et_phone);
		final EditText etVerifyCode = (EditText) dlgView.findViewById(R.id.et_verify_code);
		Button btnGetVerifyCode = (Button) dlgView.findViewById(R.id.btn_get_verify_code);
		Button btnSendVerifyCode = (Button) dlgView.findViewById(R.id.btn_send_verify_code);
		btnGetVerifyCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = etPhone.getText().toString();
				if(TextUtils.isEmpty(phone)){
					Toast.makeText(activity, "请输入手机号码", Toast.LENGTH_SHORT).show();
				}else{
					SMSSDK.getVerificationCode("86", phone);
				}
			}
		});
		btnSendVerifyCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = etPhone.getText().toString();
				String verifyCode = etVerifyCode.getText().toString();
				if(TextUtils.isEmpty(verifyCode)){
					Toast.makeText(activity, "请输入验证码", Toast.LENGTH_SHORT).show();
				}else{
					SMSSDK.submitVerificationCode("86", phone, verifyCode);
				}
			}
		});
		msgLoginDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		msgLoginDlg.setContentView(dlgView);
		msgLoginDlg.show();
	}
	
	//执行授权,获取用户信息
	//文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
	private void authorize(Platform plat) {
		if (plat == null) {
			popupOthers();
			return;
		}
		
		plat.setPlatformActionListener(this);
		//关闭SSO授权
		plat.SSOSetting(true);
		plat.showUser(null);
	}
	
	//其他登录对话框
	private void popupOthers() {
		Dialog dlg = new Dialog(activity, R.style.WhiteDialog);
		View dlgView = View.inflate(activity, R.layout.tpl_other_plat_dialog, null);
		View tvFacebook = dlgView.findViewById(R.id.tvFacebook);
		tvFacebook.setTag(dlg);
		tvFacebook.setOnClickListener(this);
		View tvTwitter = dlgView.findViewById(R.id.tvTwitter);
		tvTwitter.setTag(dlg);
		tvTwitter.setOnClickListener(this);
		
		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlg.setContentView(dlgView);
		dlg.show();
	}
	
	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] {platform.getName(), res};
			handler.sendMessage(msg);
		}
	}
	
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		t.printStackTrace();
	}
	
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
			case MSG_AUTH_CANCEL: {
				//取消授权
				Toast.makeText(activity, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
			} break;
			case MSG_AUTH_ERROR: {
				//授权失败
				Toast.makeText(activity, R.string.auth_error, Toast.LENGTH_SHORT).show();
			} break;
			case MSG_AUTH_COMPLETE: {
				//授权成功
				Toast.makeText(activity, R.string.auth_complete, Toast.LENGTH_SHORT).show();
				Object[] objs = (Object[]) msg.obj;
				String platform = (String) objs[0];
				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
				if (signupListener != null && signupListener.onSignin(platform, res)) {
					SignupPage signupPage = new SignupPage();
					signupPage.setOnLoginListener(signupListener);
					signupPage.setPlatform(platform);
					signupPage.show(activity, null);
				}
			} break;
			case MSG_SMSSDK_CALLBACK: {
				if (msg.arg2 == SMSSDK.RESULT_ERROR) {
					Toast.makeText(activity, "操作失败", Toast.LENGTH_SHORT).show();
				} else {
					switch (msg.arg1) {
						case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE: {
							if(msgLoginDlg != null && msgLoginDlg.isShowing()){
								msgLoginDlg.dismiss();
							}
							Toast.makeText(activity, "提交验证码成功", Toast.LENGTH_SHORT).show();
							Message m = new Message();
							m.what = MSG_AUTH_COMPLETE;
							m.obj = new Object[] {"SMSSDK", (HashMap<String, Object>) msg.obj};
							handler.sendMessage(m);
						} break;
						case SMSSDK.EVENT_GET_VERIFICATION_CODE:{
							Toast.makeText(activity, "验证码已经发送", Toast.LENGTH_SHORT).show();
						} break;
					}
				}
			} break;
		}
		return false;
	}
	
	public void show(Context context) {
		initSDK(context);
		super.show(context, null);
	}
	
	private void initSDK(Context context) {
		//初始化sharesdk,具体集成步骤请看文档：
		//http://wiki.mob.com/Android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
		ShareSDK.initSDK(context);
		
		//短信验证初始化，具体集成步骤看集成文档：
		//http://wiki.mob.com/Android_%E7%9F%AD%E4%BF%A1SDK%E9%9B%86%E6%88%90%E6%96%87%E6%A1%A3
		SMSSDK.initSDK(context, smssdkAppkey, smssdkAppSecret);
		EventHandler eh = new EventHandler(){
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		//注册短信验证的监听
		SMSSDK.registerEventHandler(eh);
	}
	
}
