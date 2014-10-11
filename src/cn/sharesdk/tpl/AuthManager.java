package cn.sharesdk.tpl;

import android.content.Context;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

public class AuthManager {
	
	private static SignupListener signupListener;
	private static PlatformActionListener  platformActionListener;
	
	/**
	 * 打开注册页面
	 * @param context
	 * @param platformID
	 */
	public static void showDetailPage(Context context, int platformID){
		//TODO
		if(signupListener != null && signupListener.isSignup()){
			SignupPage signupPage = new SignupPage();
			signupPage.setPlatform(platformID);
			signupPage.show(context, null);
		}
	}
	
	/**
	 * 通过接口调用，开发者实现的注册接口
	 * @param platform
	 * @return
	 */
	public static boolean doSignup(Platform platform){
		if(signupListener != null && signupListener.isSignup()){
			return signupListener.doSignup(platform);
		}
		return false;
	}
	
	@Deprecated
	public static PlatformActionListener getPlatformActionListener() {
		return platformActionListener;
	}

	@Deprecated
	public static void setPlatformActionListener(PlatformActionListener platformActionListener) {
		AuthManager.platformActionListener = platformActionListener;
	}

	public static SignupListener getSignupListener() {
		return signupListener;
	}

	public static void setSignupListener(SignupListener signupListener) {
		AuthManager.signupListener = signupListener;
	}
	
}
