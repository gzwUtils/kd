package com.gzw.kd.mail;

import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.common.utils.FreemarkerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import static com.gzw.kd.common.Constants.*;


/**
 * @author 高志伟
 */
@Slf4j
@SuppressWarnings("all")
public class MailSend {

	private JavaMailSenderImpl javaMailSender = JavaMailSender.getJavaMailSenderImpl();

	public void sendEmail(String subject, String content, String[] toEmails,boolean html) {
		javaMailSenderConfig();
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper;
		try {
			if(html){
				Map<String, Object> map = new HashMap<>();
				map.put("message",content+STRING_EMPTY);
				content = FreemarkerUtils.freeMarkerRender(map, "/src/main/resources/templates/error/500.html");
			}
			messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			messageHelper.setFrom(SYSTEM_MAIL_ACCOUNT);
			messageHelper.setTo(toEmails);
			messageHelper.setSubject(subject);
			messageHelper.setText(content, html);
			messageHelper.setSentDate(new Date());
			javaMailSender.send(message);
		} catch (Exception e) {
			log.error("邮件发送失败 {} ",e.getMessage(),e);
			throw new GlobalException(ResultCodeEnum.UNKNOWN_ERROR.getCode(),e.getMessage(),e);
		}
	}


	public void sendEmail(String subject, String content, String[] toEmails,boolean html,String path) {
		javaMailSenderConfig();
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper;
		try {
			if(html){
				Map<String, Object> map = new HashMap<>();
				map.put("message",content);
				content = FreemarkerUtils.freeMarkerRender(map, "/src/main/resources/templates/"+path);
			}
			messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			messageHelper.setFrom(SYSTEM_MAIL_ACCOUNT);
			messageHelper.setTo(toEmails);
			messageHelper.setSubject(subject);
			messageHelper.setText(content, html);
			messageHelper.setSentDate(new Date());
			javaMailSender.send(message);
		} catch (Exception e) {
			log.error("邮件发送失败 {} ",e.getMessage(),e);
			throw new GlobalException(ResultCodeEnum.UNKNOWN_ERROR.getCode(),e.getMessage(),e);
		}
	}

	private void javaMailSenderConfig() {

		javaMailSender.setHost(SYSTEM_MAIL_HOST);
		Properties mailP = new Properties();
		mailP.put("mail.smtp.auth", true);
		mailP.put("mail.smtp.timeout", 25000);
		javaMailSender.setJavaMailProperties(mailP);
		// 用户名
		javaMailSender.setUsername(SYSTEM_MAIL_USER);
		// 邮箱授权码: 从qq邮箱网页版——账户——开启POP3/SMTP服务，然后获取授权码
		javaMailSender.setPassword(SYSTEM_MAIL_PASSWORD);
		// 默认编码
		javaMailSender.setDefaultEncoding("UTF-8");
	}

}
