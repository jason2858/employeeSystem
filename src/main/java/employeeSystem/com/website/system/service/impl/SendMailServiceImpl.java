package com.yesee.gov.website.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.swing.text.AbstractDocument.Content;
import javax.tools.Tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.service.SendMailService;
import com.yesee.gov.website.util.Config;

@Service("sendMailService")
public class SendMailServiceImpl implements SendMailService {

	private static final Logger logger = LogManager.getLogger(SendMailServiceImpl.class);

	@Override
	public void sendEmail(String recipient, String subject, String text) throws Exception {
		Config config = Config.getInstance();
		// 取得資料庫中的SMTP資料
		String host = config.getValue("mail_smtpsetting_host");
		String mailTitle = config.getValue("mail_title_prefix");
		String mailEnable = config.getValue("mail_enable");

		if (!"Y".equals(mailEnable)) {
			logger.info("skip send mail");
			return;
		}

		int port = Integer.parseInt(config.getValue("mail_smtpsetting_port"));
		// 寄信信箱,密碼
//		final String email = config.getValue("mail_smtpsetting_account");
//		final String password = config.getValue("mail_smtpsetting_password");// your password
//
//		Properties props = new Properties();
//		props.put("mail.smtp.host", host);
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.smtp.port", port);

		// 測試使用1 $$
		final String email = "oopmailmail987@gmail.com";
		final String password = "0okm9IJN";// your password

		Properties props = new Properties();
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", 587);
		// $$

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, password);
			}
		});
		try {
			// 正式使用2
//			Message message = new MimeMessage(session);
			// 測試使用2 $$
			MimeMessage message = new MimeMessage(session);
			// $$

			// 寄信信箱
			message.setFrom(new InternetAddress(email));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
			message.setSubject((mailTitle == null ? "" : mailTitle) + subject);
			// 正式使用3
//			message.setText(text);
			// 測試使用3 $$
			MimeMultipart mp = new MimeMultipart();
			BodyPart body = new MimeBodyPart();
			body.setContent(text, "text/plain;charset=utf-8");
			mp.addBodyPart(body);
			message.setContent(mp);
			// $$

			Transport transport = session.getTransport("smtp");
			// 正式使用4
//			transport.connect(host, port, email, password);
			// 測試使用4 $$
			transport.connect("smtp.gmail.com", 587, "oopmailmail987@gmail.com", "0okm9IJN");
			// $$

			Transport.send(message);
			logger.info("Email to " + recipient + " 已寄出.");
		} catch (MessagingException e) {
			logger.error(e);
		}
	}
}
