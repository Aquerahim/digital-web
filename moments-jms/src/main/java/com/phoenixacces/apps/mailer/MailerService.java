package com.phoenixacces.apps.mailer;

/*import com.phoenixacces.enums.Importance;
import com.phoenixacces.enums.Priority;
import com.phoenixacces.exceptions.RequestValidationException;
import com.phoenixacces.interfaces.IMailService;
import com.phoenixacces.models.MailRequest;
import com.phoenixacces.models.MailRequest2;
import com.phoenixacces.services.MailService;
import com.phoenixacces.services.SenderMessage;
import com.phoenixacces.utils.CommonUtils;*/
import com.phoenixacces.apps.enums.Importance;
import com.phoenixacces.apps.enums.Priority;
import com.phoenixacces.apps.exceptions.RequestValidationException;
import com.phoenixacces.apps.interfaces.IMailService;
import com.phoenixacces.apps.models.MailRequest;
import com.phoenixacces.apps.models.MailRequest2;
import com.phoenixacces.apps.services.MailService;
import com.phoenixacces.apps.services.SenderMessage;
import com.phoenixacces.apps.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailerService {

    private final IMailService mailer = new MailService();

    @Autowired
    private Environment env;

    public void sendMail(SenderMessage mailSenderMessage) throws Exception {

        MailRequest2 mailRequest2 = new MailRequest2();
        mailRequest2.setToId(mailSenderMessage.getEmail()); //A qui on veut envoyer
        mailRequest2.setSubject(mailSenderMessage.getSubject()); //Le sujet
        mailRequest2.setContent(mailSenderMessage.getContent()); // le contenbu
        mailRequest2.setCcId(mailSenderMessage.getCcId()); //Les personnes en copie
        mailRequest2.setFromName(mailSenderMessage.getFromName()); //le nom de la personne a qui on veut envoyer
        mailRequest2.setFromId(env.getProperty("mailer.from.address")); //l'email de la personne qui envoy le mail
        mailRequest2.setPriority(Priority.valueOf("LEVEL_1"));
        mailRequest2.setImportance(Importance.valueOf("HIGH"));
        mailer.sendMail(buildMail(mailRequest2));
        /*log.info("+++ +++ +++ +++ [{}] MailNotificationActor::sendMessage --- DONE +++ +++ +++ +++", mailSenderMessage.getEmail());
        log.info("<<< <<< MailNotificationActor::sendMessage >>> >>>>  Template : {} <<< <<<", mailRequest2.getContent());*/
    }


    // Build mail content
    private MailRequest buildMail(MailRequest2 request) throws RequestValidationException {
        MailRequest mail = new MailRequest();
        mail.setToId(validateStrParamArg(request.getToId()));
        mail.setSubject(validateStrParamArg(request.getSubject()));
        mail.setContent(validateStrParamArg(request.getContent()));
        if (isValidStrParamArg(request.getToName())) {
            mail.setToName(request.getToName());
        }
        if (isValidStrParamArg(request.getCcId())) {
            mail.setCcId(request.getCcId());
        }
        if (isValidStrParamArg(request.getCcName())) {
            mail.setCcName(request.getCcName());
        }
        if (isValidStrParamArg(request.getBccId())) {
            mail.setBccId(request.getBccId());
        }
        if (isValidStrParamArg(request.getBccName())) {
            mail.setBccId(request.getBccName());
        }


        mail.setFromId(isValidStrParamArg(request.getFromId()) ? request.getFromId() : CommonUtils.getConfigProperty("mailer.from.id"));
        mail.setFromName(isValidStrParamArg(request.getFromName()) ? request.getFromName() : CommonUtils.getConfigProperty("mailer.from.name"));
        mail.setReadReceipt(request.isReadReceipt());
        mail.setDeliveryReceipt(request.isDeliveryReceipt());
        mail.setPriority(request.getPriority());
        mail.setImportance(request.getImportance());
        return mail;
    }


    // Control on mail params
    private boolean isValidStrParamArg(String input) {
        return input != null && !input.isEmpty();
    }


    // Control on mail params
    private String validateStrParamArg(String input) throws RequestValidationException {
        if (input == null || input.isEmpty()) {
            throw new RequestValidationException(input + " is an invalid input argument for a required field");
        }
        return input;
    }
}
