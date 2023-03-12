package com.gzw.kd.mail;

import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author 高志伟
 */

@SuppressWarnings("all")
public class JavaMailSender {

    private static class JavaMailSenderHolder {

        private static JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
    }

    private JavaMailSender() {
    }

    public static JavaMailSenderImpl getJavaMailSenderImpl() {
        return JavaMailSenderHolder.javaMailSenderImpl;
    }
}
