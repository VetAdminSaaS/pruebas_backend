package apiFactus.factusBackend.integration.notification.email.service;

import apiFactus.factusBackend.integration.notification.email.dto.Mail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public Mail createMail(String to, String subject, Map<String, Object> model, String from) {
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setModel(model);
        return mail;

    }
    public void sendEmail(Mail mail, String templateName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        Context context = new Context();
        context.setVariables(mail.getModel());

        String html = templateEngine.process(templateName, context);
        helper.setTo(mail.getTo());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        mailSender.send(message);


    }
    public void sendEmailWithAttachment(Mail mail, String templateName, byte[] attachment, String fileName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
        Context context = new Context();
        context.setVariables(mail.getModel());

        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());
        helper.setText(templateEngine.process(templateName, context), true);
        helper.setFrom(mail.getFrom());

        if(attachment != null && attachment.length > 0) {
            helper.addAttachment(fileName, new ByteArrayResource(attachment));

        }else {
            log.warn("No se adjuntó ningún archivo porque el PDF está vacío.");
        }
        mailSender.send(message);


    }
}