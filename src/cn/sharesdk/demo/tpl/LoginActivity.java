package cn.sharesdk.demo.tpl;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;


/** 中文注释
 * ShareSDK 官网地址 ： http://www.sharesdk.cn </br>
 *1、这是用2.38版本的sharesdk，一定注意  </br>
 *2、如果要咨询客服，请加企业QQ 4006852216 </br>
 *3、咨询客服时，请把问题描述清楚，最好附带错误信息截图 </br>
 *4、一般问题，集成文档中都有，请先看看集成文档；减少客服压力，多谢合作  ^_^</br></br></br>
 *
 *The password of demokey.keystore is 123456
 **ShareSDK Official Website ： http://www.sharesdk.cn </br>
 *1、Be carefully, this sample use the version of 2.11 sharesdk  </br>
 *2、If you want to ask for help，please add our QQ whose number is 4006852216 </br>
 *3、Please describe detail of the question , if you have the picture of the bugs or the bugs' log ,that is better </br>
 *4、Usually, the answers of some normal questions is exist in our user guard pdf, please read it more carefully,thanks  ^_^
*/
public class LoginActivity extends Activity implements Callback, 
		OnClickListener, PlatformActionListener {
	private static final int MSG_USERID_FOUND = 1;
	private static final int MSG_LOGIN = 2;
	private static final int MSG_AUTH_CANCEL = 3;
	private static final int MSG_AUTH_ERROR= 4;
	private static final int MSG_AUTH_COMPLETE = 5;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDK.initSDK(this);
		
		setContentView(R.layout.third_party_login_page);
		findViewById(R.id.wechat).setOnClickListener(this);
		findViewById(R.id.tvQq).setOnClickListener(this);
	}
	
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.wechat: {
				authorize(new Wechat(this));
			}
			break;
			case R.id.tvQq: {
				authorize(new QZone(this));
			}
			break;
		}
	}
	
	private void authorize(Platform plat) {	
		if(plat.isValid()) {
			String userId = plat.getDb().getUserId();
			if (!TextUtils.isEmpty(userId)) {
				UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
				login(plat.getName(), userId, null);
				return;
			}
		}
		plat.setPlatformActionListener(this);
		plat.SSOSetting(true);
		plat.showUser(null);
	}
	
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
			login(platform.getName(), platform.getDb().getUserId(), res);
		}
		System.out.println(res);
		System.out.println("------User Name ---------" + platform.getDb().getUserName());
		System.out.println("------User ID ---------" + platform.getDb().getUserId());
	}
	
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
		}
		t.printStackTrace();
	}
	
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
		}
	}
	
	private void login(String plat, String userId, HashMap<String, Object> userInfo) {
		Message msg = new Message();
		msg.what = MSG_LOGIN;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}
	
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
			case MSG_USERID_FOUND: {
				Toast.makeText(this, R.string.userid_found, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_LOGIN: {
				
				String text = getString(R.string.logining, msg.obj);
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
				System.out.println("---------------");
				
//				Builder builder = new Builder(this);
//				builder.setTitle(R.string.if_register_needed);
//				builder.setMessage(R.string.after_auth);
//				builder.setPositiveButton(R.string.ok, null);
//				builder.create().show();
			}
			break;
			case MSG_AUTH_CANCEL: {
				Toast.makeText(this, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
				System.out.println("-------MSG_AUTH_CANCEL--------");
			}
			break;
			case MSG_AUTH_ERROR: {
				Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT).show();
				System.out.println("-------MSG_AUTH_ERROR--------");
			}
			break;
			case MSG_AUTH_COMPLETE: {
				Toast.makeText(this, R.string.auth_complete, Toast.LENGTH_SHORT).show();
				System.out.println("--------MSG_AUTH_COMPLETE-------");
			}
			break;
		}
		return false;
	}
	
}
