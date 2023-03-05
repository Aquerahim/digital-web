package com.phoenixacces.apps.services;

import com.phoenixacces.apps.exceptions.MailerException;
import com.phoenixacces.apps.interfaces.IMailService;
import com.phoenixacces.apps.models.MailRequest;
import com.phoenixacces.apps.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MailService implements IMailService {

    private static final ExecutorService executor;
    private final Properties properties;
    private String fromName = null;
    private String fromAddress = null;
    private String env;

    static {
        executor = Executors.newSingleThreadExecutor();
    }

    {
        String hostName = CommonUtils.getConfigProperty("mail.smtp.host");
        String socketFactory = CommonUtils.getConfigProperty("mail.smtp.socketFactory.class");
        String socketFactoryPort = CommonUtils.getConfigProperty("mail.smtp.socketFactory.port");
        String smtpAuth = CommonUtils.getConfigProperty("mail.smtp.auth");
        String smtpPort = CommonUtils.getConfigProperty("mail.smtp.port");
        String enableStarttls = CommonUtils.getConfigProperty("mail.smtp.starttls.enable");
        env = CommonUtils.getConfigProperty("mailer.from.env");
        fromName = CommonUtils.getConfigProperty("mailer.from.name");
        fromAddress = CommonUtils.getConfigProperty("mailer.from.address");

        properties = System.getProperties();
        properties.setProperty("mail.smtp.host", hostName);

        if (socketFactory != null && !socketFactory.isEmpty()) {
            properties.put("mail.smtp.socketFactory.class", socketFactory);
        }

        if (socketFactoryPort != null && !socketFactoryPort.isEmpty()) {
            properties.put("mail.smtp.socketFactory.port", socketFactoryPort);
        }

        if (smtpAuth != null && !smtpAuth.isEmpty()) {
            properties.put("mail.smtp.auth", true);
        }

        if (smtpPort != null && !smtpPort.isEmpty()) {
            properties.put("mail.smtp.port", smtpPort);
        }

        if (enableStarttls != null && !enableStarttls.isEmpty()) {
            properties.put("mail.smtp.starttls.enable", true);
        }
    }


    @Override
    public void sendMail(final MailRequest request) throws Exception {
        Set<Callable<Boolean>> callables = new HashSet<Callable<Boolean>>();
        callables.add(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Boolean response = true;
                try {
                    // log.info("Send mail : {}", request);
                    sendSimpleEmail(request);
                } catch (MailerException e) {
                    log.error("[ MailService->sendMail ] - Exception : {}", e.getMessage());
                    e.printStackTrace();
                    response = false;
                } finally {
                    return response;
                }
            }
        });
        Boolean sendMail = executor.invokeAny(callables);
        if (!sendMail)
            throw new Exception("Exception while trying to send email !");
    }

    private synchronized void sendSimpleEmail(MailRequest request) throws MailerException {
        try {
            log.info("+++ +++ +++ +++ MailService::sendSimpleEmail --- START +++ +++ +++ +++");
            Map<String, ByteArrayDataSource> map = new HashMap<String, ByteArrayDataSource>();

            log.info("MailService::sendSimpleEmail -> instantiate session");
            Session session = Session.getDefaultInstance(this.properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(CommonUtils.getConfigProperty("mailer.session.username"), CommonUtils.getConfigProperty("mailer.session.password"));
                }
            });

            log.info("MailService::sendSimpleEmail -> instantiate mime message");
            MimeMessage message = new MimeMessage(session);

            // message.setFrom(new InternetAddress(request.getFromId(), request.getFromName()));
            message.setFrom(new InternetAddress(fromAddress, fromName));
            if (request.getToId() != null && !request.getToId().isEmpty()) {
                log.info("MailService::sendSimpleEmail -> set FromId :");
                for (String current : request.getToId().split(",")) {
                    log.info("\t\t- {}", current);
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(current));
                }
            }
            if (request.getCcId() != null && !request.getCcId().isEmpty()) {
                log.info("MailService::sendSimpleEmail -> set CcId :");
                for (String current : request.getCcId().split(",")) {
                    log.info("\t\t- {}", current);
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(current));
                }
            }
            if (request.getBccId() != null && !request.getBccId().isEmpty()) {
                log.info("MailService::sendSimpleEmail -> set BccId :");
                for (String current : request.getBccId().split(",")) {
                    log.info("\t\t- {}", current);
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(current));
                }
            }

            /*//CC
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(CommonUtils.getConfigProperty("mailer.hotLine.manager")));
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(CommonUtils.getConfigProperty("mailer.coo.medicis")));*/

            //BCC
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(CommonUtils.getConfigProperty("mailer.support.phoenix")));
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(CommonUtils.getConfigProperty("mailer.support.gmailphoenix")));

            log.info("MailService::sendSimpleEmail -> environment : {}", env);

            String subject = request.getSubject();
            if(env != null && env.equalsIgnoreCase("dev")){
                subject = new StringBuffer("DEV - ").append(subject).toString();
            }
            message.setSubject(subject);


            String content = null;
            if(env != null && env.equalsIgnoreCase("dev")) {
                content = "<b> DEV - DEV - DEV - DEV - DEV </b></br>"
                        .concat(request.getContent())
                        .concat("</br><b> DEV - DEV - DEV - DEV - DEV </b>");
                request.setContent(content);
            }

            log.info("MailService::sendSimpleEmail -> set subject : {}", subject);

            if (request.isReadReceipt()) {
                log.info("MailService::sendSimpleEmail -> set read receipt : {}", request.getFromId());
                message.addHeader("Disposition-Notification-To", request.getFromId());
            }
            if (request.isDeliveryReceipt()) {
                log.info("MailService::sendSimpleEmail -> set delivery receipt : {}", request.getFromId());
                message.addHeader("Return-Receipt-To", request.getFromId());
            }
            if (request.getImportance() != null) {
                log.info("MailService::sendSimpleEmail -> set importance : {}", request.getImportance().getLabel());
                message.addHeader("Importance", request.getImportance().getLabel());
                log.info("MailService::sendSimpleEmail -> set priority : {}", request.getPriority());
                message.addHeader("X-Priority", String.valueOf(request.getPriority().getLevel()));
            }

            // log.info("MailService::sendSimpleEmail -> set content message : {}", request.getContent());
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            // messageBodyPart.setContent(request.getContent(), "text/html");
            messageBodyPart.setText(request.getContent(), "UTF-8", "html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (!map.isEmpty()) {
                log.info("+++ +++ +++ +++ MailService::sendSimpleEmail --- size: {} +++ +++ +++ +++", map.entrySet().size());
                for (Map.Entry<String, ByteArrayDataSource> entry : map.entrySet()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.setDataHandler(new DataHandler(entry.getValue()));
                    attachmentPart.setFileName(entry.getKey());
                    attachmentPart.setDisposition(Part.ATTACHMENT);
                    multipart.addBodyPart(attachmentPart);
                }
            }

            log.info("MailService::sendSimpleEmail -> attach mail part together");
            message.setContent(multipart);

            log.info("+++ +++ +++ +++ MailService::sendSimpleEmail --- DONE +++ +++ +++ +++");
            Transport.send(message);
        } catch (MessagingException | RuntimeException | IOException e) {
            e.printStackTrace();
            log.error("[ MailService->sendSimpleEmail ] - Exception : {}", e.getMessage());
            throw new MailerException("Exception while trying to send email", e);
        }
    }
}
