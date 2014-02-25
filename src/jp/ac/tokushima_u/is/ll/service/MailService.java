package jp.ac.tokushima_u.is.ll.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import jp.ac.tokushima_u.is.ll.form.EmailModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 *
 * @author houbin
 */
@Service
public class MailService {
	
	private static final TransferQueue<EmailModel> systemMailQueue = new LinkedTransferQueue<EmailModel>();
	private static final TransferQueue<EmailModel> notificationMailQueue = new LinkedTransferQueue<EmailModel>();

    @Autowired
    @Qualifier("mailSender")
    private JavaMailSender mailSender;
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    @Qualifier("notificationSender")
    private JavaMailSender notificationSender;
//    @Autowired
//    private TaskExecutor taskExecutor;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    private Logger logger = LoggerFactory.getLogger(MailService.class);
    
    public MailService(){
    	new Thread(){
			@Override
			public void run() {
				try {
					logger.debug("Start new thread to send system mail");
					while(true){
						EmailModel model = systemMailQueue.take();
						sendMail(model);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}.start();
    	
    	new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					logger.debug("Start new thread to send notification mail");
					while(true){
						EmailModel model = notificationMailQueue.take();
						sendNotificationMail(model);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}.start();
    }
    
    public void sendSysMail(String sendTo, String subject, String templateName, ModelMap model) throws MessagingException, IOException, TemplateException {
        final EmailModel email = new EmailModel();
        email.setAddress(sendTo);
        email.setFrom(propertyService.getSystemMailAddress());
        email.setReplyTo(email.getFrom());
        email.setSubject(subject);
        email.setHtml(false);
        Configuration cfg = freeMarkerConfigurer.getConfiguration();
        Template t = cfg.getTemplate(templateName + ".ftl");
        StringWriter writer = new StringWriter();
        t.process(model, writer);
        email.setContent(writer.toString());
        try {
        	systemMailQueue.transfer(email);
		} catch (InterruptedException e) {
			logger.error("Error", e);
		}
    }

    public void sendMail(EmailModel email) throws MessagingException, IOException {
        if (email.getAddresses() == null || email.getAddresses().length == 0) {
            logger.error("No mail address");
            return;
        }
        startSendMail(email);
    }

    private void startSendMail(EmailModel email) throws MessagingException, IOException {
        MimeMessage mime = mailSender.createMimeMessage();
        boolean isMultipart = false;
        if (email.getAttachment() != null && email.getAttachment().length > 0) {
            isMultipart = true;
        }
        MimeMessageHelper helper = new MimeMessageHelper(mime, isMultipart, "utf-8");
        helper.setFrom(email.getFrom());//发件人
        helper.setTo(email.getAddress());//收件人

        if (StringUtils.hasLength(email.getCc())) {
            String cc[] = email.getCc().split(";");
            helper.setCc(cc);//抄送
        }

        if (StringUtils.hasLength(email.getBcc())) {
            String bcc[] = email.getBcc().split(";");
            helper.setBcc(bcc);
        }

        helper.setReplyTo(email.getReplyTo());//回复到
        helper.setSubject(email.getSubject());//邮件主题
        helper.setText(email.getContent(), email.isHtml());//true表示设定html格式
        if (isMultipart) {
            for (MultipartFile file : email.getAttachment()) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                String fileName = file.getOriginalFilename();
                try {
                    fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
                } catch (Exception e) {
                }
                helper.addAttachment(fileName, new ByteArrayResource(file.getBytes()));
            }
        }

        mailSender.send(mime);
    }

	public void sendNotificationMail(EmailModel email) throws MessagingException, IOException{
        if (email.getAddresses() == null || email.getAddresses().length == 0) {
            logger.error("No mail address");
            return;
        }
        startSendNoficationMail(email);
	}
	
    private void startSendNoficationMail(EmailModel email) throws MessagingException, IOException {
        MimeMessage mime = mailSender.createMimeMessage();
        boolean isMultipart = false;
        if (email.getAttachment() != null && email.getAttachment().length > 0) {
            isMultipart = true;
        }
        MimeMessageHelper helper = new MimeMessageHelper(mime, isMultipart, "utf-8");
        helper.setFrom(email.getFrom());//发件人
        helper.setTo(email.getAddress());//收件人

        if (StringUtils.hasLength(email.getCc())) {
            String cc[] = email.getCc().split(";");
            helper.setCc(cc);//抄送
        }

        if (StringUtils.hasLength(email.getBcc())) {
            String bcc[] = email.getBcc().split(";");
            helper.setBcc(bcc);
        }

        helper.setReplyTo(email.getReplyTo());//回复到
        helper.setSubject(email.getSubject());//邮件主题
        helper.setText(email.getContent(), email.isHtml());//true表示设定html格式
        if (isMultipart) {
            for (MultipartFile file : email.getAttachment()) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                String fileName = file.getOriginalFilename();
                try {
                    fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
                } catch (Exception e) {
                }
                helper.addAttachment(fileName, new ByteArrayResource(file.getBytes()));
            }
        }

        notificationSender.send(mime);
    }
}
