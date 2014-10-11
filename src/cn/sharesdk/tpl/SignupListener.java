package cn.sharesdk.tpl;

import cn.sharesdk.framework.Platform;

public interface SignupListener {
	/**
	 * 是否注册
	 * @return
	 */
	public boolean isSignup();
	
	/**
	 * 执行注册
	 * @return
	 */
	public boolean doSignup(Platform platform);
	
	/**
	 * 注册结果
	 * @param success
	 * @param result
	 */
	//public void signupResult(boolean success, String result);
}
