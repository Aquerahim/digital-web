package com.phoenixacces.apps.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/*import com.phoenixacces.entities.SmsEnvoye;
import com.phoenixacces.jms.messages.EmailMessage;
import com.phoenixacces.jms.messages.JmsMessage;
import com.phoenixacces.jms.messages.SmsMessage;
import com.phoenixacces.jms.utilities.Converter;
import com.phoenixacces.mailer.MailerService;
import com.phoenixacces.services.SenderMessage;
import com.phoenixacces.services.TypeMessageService;
import com.phoenixacces.services.sms.SMSPhoenixAccesService;
import com.phoenixacces.services.templates.TemplatesService;*/
import com.phoenixacces.apps.jms.messages.EmailMessage;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.mailer.MailerService;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.entities.service.SmsEnvoye;
import com.phoenixacces.apps.persistence.services.module.ServiceCourrierService;
import com.phoenixacces.apps.persistence.services.parametrage.EntrepriseService;
import com.phoenixacces.apps.persistence.services.parametrage.SMSPhoenixAccesService;
import com.phoenixacces.apps.persistence.services.parametrage.TypeMessageService;
import com.phoenixacces.apps.persistence.services.templates.TemplatesService;
import com.phoenixacces.apps.services.SenderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JmsConsumer {

    /*@Autowired
    private Environment env;*/

    private final SMSPhoenixAccesService smsPhoenixAccesService;
    private final TypeMessageService typeMessageService;
    private final MailerService maillerService;
    private final TemplatesService templatesService;
    private final ServiceCourrierService serviceCourrierService;
    private final EntrepriseService compagnieRoutiere;

    public JmsConsumer(SMSPhoenixAccesService smsPhoenixAccesService, TypeMessageService typeMessageService,
                       MailerService maillerService, TemplatesService templatesService,
                       ServiceCourrierService serviceCourrierService, EntrepriseService compagnieRoutiere) {
        this.smsPhoenixAccesService = smsPhoenixAccesService;
        this.typeMessageService = typeMessageService;
        this.maillerService = maillerService;
        this.templatesService = templatesService;
        this.serviceCourrierService = serviceCourrierService;
        this.compagnieRoutiere = compagnieRoutiere;
    }


    @JmsListener(destination = "${activemq.message_queue}", containerFactory="jsaFactory")
    public void receiver(JmsMessage message) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            SmsCredential smsCredential = new  SmsCredential();

            if (Converter.jsonToPojo(message.getObject(), message.getType()) instanceof SmsMessage) {

                SmsMessage sms = (SmsMessage) mapper.readValue(message.getObject(), message.getType());

                System.out.println("Content Message ============> " + sms.getContent());

                smsCredential.setPassword(sms.getPassword());
                smsCredential.setUsername(sms.getUsername());
                smsCredential.setSenderId(sms.getSenderId());

                // TODO: 05/06/2021 - implémenter la logique d'envoie des sms
                SmsEnvoye smsEnvoye = new SmsEnvoye();
                smsEnvoye.setTypeMessage(typeMessageService.findById(sms.getTypeMessage()));
                smsEnvoye.setNumeroDestinataire(sms.getToId());
                smsEnvoye.setDestinataire(sms.getFromName());
                smsEnvoye.setCorpsMessage(sms.getContent());
                smsEnvoye.setSmsCredential(smsCredential);
                if(sms.getRefCourier() != null && sms.getCompagnieId() != null){
                    smsEnvoye.setServiceCourrier(serviceCourrierService.findOne(sms.getRefCourier(), compagnieRoutiere.findOne(sms.getCompagnieId()), false));
                }
                smsPhoenixAccesService.envoyeMessageBySouscrivant(smsEnvoye);
                //findOne(String colisNumber, CompagnieRoutiere cie)compagnieRoutiere
            }
            else {

                EmailMessage email = (EmailMessage) mapper.readValue(message.getObject(), message.getType());

                //log.info("<<< <<< JmsConsumer::receiver --- Type template : {} <<< <<<", email);
                //log.info("<<< <<< JmsConsumer::receiver --- Type template : {} <<< <<<", email.getType());

                String content      = templatesService.findByTemplateByType(email.getType()).getContent();
                String newContent   = null;

                //log.info("<<< <<< JmsConsumer::receiver --- CONTENT : {} <<< <<<", content);

                if(email.getType().equalsIgnoreCase("BIENVENU")){

                    newContent = content;
                }
                else if(email.getType().equalsIgnoreCase("RECORDING")){

                    newContent = content.replace("[username]", email.getUsername()).replace("[password]", email.getDefaulPwd());
                }

                else if(email.getType().equalsIgnoreCase("ACCUSE_RECEPTION")){

                    newContent = content.replace("[nomClient]", email.getUsername())

                            .replace("[nomEntreprise]", email.getDefaulPwd())

                            .replace("[emailEntreprise]", email.getSts())

                            .replace("[contactEntreprise]", email.getOther());
                }
                else if(email.getType().equalsIgnoreCase("NOTIFICATION_COLIS_JOURNALIER")){

                    newContent   = content.replace("[nomEntreprise]", email.getUsername());
                }
                else if(email.getType().equalsIgnoreCase("TRAITEMENT_BON")){

                    newContent = content.replace("[entite]", email.getUsername())

                            .replace("[staut]", email.getDefaulPwd())

                            .replace("[mailEntite]", email.getSts())

                            .replace("[phoneEntite]", email.getOther());
                }
                else if(email.getType().equalsIgnoreCase("NOTIF_TRAITEMENT")){

                    newContent = content;
                }
                else if(email.getType().equalsIgnoreCase("RESET_APP")){

                    newContent   = content.replace("[password]", email.getDefaulPwd());
                }
                else if(email.getType().equalsIgnoreCase("NOTIF_RESET")){

                    newContent   = content;
                }


                //log.info("<<< <<< JmsConsumer::receiver --- NEW CONTENT : {} <<< <<<", newContent);

                SenderMessage mail = new SenderMessage();
                mail.setEmail(email.getEmail());
                mail.setSubject(email.getSubject());
                mail.setFromName("Di-Gital");
                mail.setContent(newContent);
                maillerService.sendMail(mail);
            }

        } catch (JsonProcessingException e) {
            log.info("<<< <<< JmsConsumer::receiver --- Exception: {} <<< <<<", e.getMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
