package com.quote.utility;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.quote.vm.EmailVM;


@Component
public class SendEmail {
	
	@Value("${crm.admin.email}")
	String fromMail;
	
	@Value("${crm.admin.email.password}")
	String fromMailPassword;
	
	Properties props = new Properties();
	
	Session session = null;
	
	@PostConstruct
	public void init(){
		//Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromMail, fromMailPassword);
				}
			});
	}
	
	@Async
	public void sendEmail(EmailVM emailVM){
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailVM.getFrom()));
			message.setSubject(emailVM.getSubject());
			
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailVM.getTo()));
			
	        MimeBodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setContent(emailVM.getMailBody(), "text/html");
	 
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);
			
	       /* if (emailBodyImageMap != null && emailBodyImageMap.size() > 0) {
	            Set<String> setImageID = emailBodyImageMap.keySet();
	             
	            for (String contentId : setImageID) {
	                MimeBodyPart imagePart = new MimeBodyPart();
	                imagePart.setHeader("Content-ID", "<" + contentId + ">");
	                imagePart.setDisposition(MimeBodyPart.INLINE);
	                 
	                String imageFilePath = emailBodyImageMap.get(contentId);
	                imagePart.attachFile(imageFilePath);
	                multipart.addBodyPart(imagePart);
	            }
	        }*/
			
	        message.setContent(multipart);
			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}/*finally{
			for (Map.Entry<String,String> entry : emailBodyImageMap.entrySet()) {
				File file = new File(entry.getValue());
				if(file.exists()){
					file.delete();
				}
			}
		}*/
	}

}