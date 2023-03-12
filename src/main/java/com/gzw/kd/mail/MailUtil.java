package com.gzw.kd.mail;

/**
 * @author 高志伟
 */

@SuppressWarnings("all")
public class MailUtil {

	private static class MailUtilHolder {
		private static MailSend mailSend = new MailSend();
	}
	private MailUtil() {

	}
	public static MailSend getMailSend() {
		return MailUtilHolder.mailSend;
	}
}
