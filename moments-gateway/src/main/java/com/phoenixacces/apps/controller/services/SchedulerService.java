package com.phoenixacces.apps.controller.services;

import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.entities.module.assurance.SendMessage;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.service.SmsEnvoye;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.authentification.UserService;
import com.phoenixacces.apps.persistence.services.module.assurance.SendMessageService;
import com.phoenixacces.apps.persistence.services.parametrage.EntrepriseService;
import com.phoenixacces.apps.persistence.services.parametrage.MotifSuspensionCollaborationService;
import com.phoenixacces.apps.persistence.services.parametrage.SMSPhoenixAccesService;
import com.phoenixacces.apps.persistence.services.parametrage.TypeMessageService;
import com.phoenixacces.apps.producer.JmsProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SchedulerService {

    private final SendMessageService sendMessageService;
    private final JmsProducer jmsProducer;
    private final ProfileService profileService;
    private final UserService userService;
    private final EntrepriseService entrepriseService;
    private final MotifSuspensionCollaborationService motifSuspensionSvce;
    private final SMSPhoenixAccesService smsPhoenixAccesService;
    private final TypeMessageService typeMessageService;

    @Value("${api.sms.gateway}")
    private String gateway;

    @Autowired
    public SchedulerService(
            SendMessageService sendMessageService,
            JmsProducer jmsProducer,
            ProfileService profileService,
            UserService userService,
            EntrepriseService entrepriseService,
            MotifSuspensionCollaborationService motifSuspensionSvce,
            SMSPhoenixAccesService smsPhoenixAccesService,
            TypeMessageService typeMessageService) {
        this.sendMessageService         = sendMessageService;
        this.jmsProducer                = jmsProducer;
        this.entrepriseService          = entrepriseService;
        this.userService                = userService;
        this.profileService             = profileService;
        this.motifSuspensionSvce        = motifSuspensionSvce;
        this.smsPhoenixAccesService     = smsPhoenixAccesService;
        this.typeMessageService         = typeMessageService;
    }

    public void pushMessage (LocalDate date) throws Exception {

        log.info(">>>>>>>>> Lancement du Push message à la date du ----------+ {}", date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        List<SendMessage> sendMessageList = sendMessageService.findAll(EtatLecture.PENDING);

        int i = 0;
        int k = 0;

        if(sendMessageList.size() > 0 && !sendMessageList.isEmpty()){

            for (SendMessage sendMessage : sendMessageList){

                if(sdf.parse(date.toString()).equals(sdf.parse(sendMessage.getDateEnvoi().toString()))){

                    SmsMessage sms = new SmsMessage();

                    sms.setTypeMessage(27L);

                    sms.setToId(sendMessage.getNumeroContact());

                    sms.setContent(sendMessage.getMessage());

                    sms.setFromName(sendMessage.getTypeMessage().toUpperCase());

                    sms.setUsername(sendMessage.getEntreprises().getSmsCredential().getUsername());

                    sms.setPassword(sendMessage.getEntreprises().getSmsCredential().getPassword());

                    sms.setSenderId(sendMessage.getEntreprises().getSmsCredential().getSenderId());

                    jmsProducer.send(new JmsMessage("Envoi du message de bienvenu", Converter.pojoToJson(sms), SmsMessage.class));


                    //Mise à Jour de  l'envoi
                    SendMessage message = sendMessageService.findOne(sendMessage.getId(), EtatLecture.PENDING);

                    if(message != null){

                        message.setActive(false);

                        message.setStatut(EtatLecture.TRAITE);

                        message.setVersion(message.getVersion() + 1);

                        sendMessageService.update(message);
                    }

                    i ++;
                }

                if(sdf.parse(date.toString()).before(sdf.parse(sendMessage.getDateEnvoi().toString()))){

                    SendMessage message = sendMessageService.findOne(sendMessage.getId(), EtatLecture.PENDING);

                    if(message != null){

                        message.setActive(false);

                        message.setStatut(EtatLecture.REJETER);

                        message.setVersion(message.getVersion() + 1);

                        sendMessageService.update(message);
                    }
                    k++;
                }
            }
        }
        log.info(">>>>>>>>> Le nombre de message programmé pour envoi est de                + >>>>>>>>> >>>>>>>>> + {}", i);
        log.info(">>>>>>>>> Le nombre de message programmé antérieur à la dtae du jour est  + >>>>>>>>> >>>>>>>>> + {}", k);
    }


    public void rappelExpirationAccount (LocalDate date) throws Exception {

        log.info(">>>>>>>>> Lancement de la tâche cron pour le rappel de la fin du utilisation du service par l'entreprise ----------+ {}", date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        List<Entreprises> entreprisesList = entrepriseService.findAll();

        int q = 0;

        String number;

        if(entreprisesList.size() > 0){

            for (Entreprises entreprises : entreprisesList){

                Entreprises entreprises1 = entrepriseService.findOne(entreprises.getId());

                if(entreprises1.getTypeContrat().getId() != 1 || entreprises.getTypeContrat().getId() != 2){

                    if(entreprises1.getDateRappelFinContrat() != null && sdf.parse(date.toString()).equals(sdf.parse(entreprises1.getDateRappelFinContrat().toString()))){

                        if(entreprises1.getNotificateur() == 0 && entreprises1.getNotificateur() != null){

                            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                            if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                                number       = "225"+entreprises.getContact();

                            }else{

                                number       = "+225"+entreprises.getContact();
                            }

                            //Envoi du message de rappel
                            String message = "Cher Partenaire "+ entreprises.getCompagnie().toUpperCase() +", votre abonnement au service Di-Gital Web arrive a échénace le "+formatter.format(entreprises.getDateFinContrat())+". Prière vous rapprocher de votre gestionnaire au 0757873487 pour renouvellement de votre licence numérique.";

                            SmsEnvoye smsEnvoye = new SmsEnvoye();

                            smsEnvoye.setTypeMessage(typeMessageService.findById(11L));

                            smsEnvoye.setNumeroDestinataire(number);

                            smsEnvoye.setDestinataire(entreprises.getCompagnie().toUpperCase());

                            smsEnvoye.setCorpsMessage(message);

                            smsPhoenixAccesService.smsSendToDigitalWeb(smsEnvoye);

                            q++;


                            entreprises1.setNotificateur(1L);

                            entrepriseService.update(entreprises1);
                        }
                    }
                }
            }
        }

        log.info(">>>>>>>>> Le nombre de message de rappel envoyé est de                    + >>>>>>>>> >>>>>>>>> + {}", q);
    }


    public void disableAccount (LocalDate date) throws Exception {

        log.info(">>>>>>>>> Lancement de la tâche cron pour la désactivation des compte entreprise ----------+ {}", date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        List<Entreprises> entreprisesList = entrepriseService.findAll();

        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        int q = 0;

        if(entreprisesList.size() > 0){

            for (Entreprises entreprises : entreprisesList){

                if(entreprises.getTypeContrat().getId() != 1 || entreprises.getTypeContrat().getId() != 2){

                    if(entreprises.getDateFinContrat() != null && sdf.parse(date.toString()).equals(sdf.parse(entreprises.getDateFinContrat().toString()))){

                        //Mise en place de script de désactivation
                        Entreprises entreprise = entrepriseService.findOne(entreprises.getId());

                        if(entreprise != null){

                            entreprise.setActive(false);

                            entreprise.setMotifSuspensionCollaboration(motifSuspensionSvce.findOne(9L));

                            entrepriseService.update(entreprise);


                            //Désactivation de tous les profiles du compte expiré
                            List<Profile> profileList = profileService.findAll(entreprise);

                            if(profileList.size() > 0){

                                for (Profile profile : profileList){

                                    Profile profile1 = profileService.findOne(profile.getIdDigital());

                                    if(profile1 != null){

                                        profile1.setActive(false);

                                        profileService.update(profile1);


                                        //Désaction de tous les compte users
                                        List<User> userList = userService.findAll();

                                        if(userList.size() > 0){

                                            for (User use : userList){

                                                if(Objects.equals(use.getProfile().getGareRoutiere().getCompagnie().getId(), entreprises.getId())){

                                                    User user = userService.findOne(use.getId());

                                                    user.setActive(false);

                                                    userService.update(user);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        q++;
                    }
                }
            }

            if(q > 0){

                String number;
                //Recupération des entreprises désactivées

                List<Entreprises> eList = entrepriseService.findAll(false);

                if(eList.size() > 0){

                    for (Entreprises entreprises : eList){

                        Entreprises entreprises1 = entrepriseService.findOne(entreprises.getId());

                        if(entreprises1.getNotificateur() == 1){

                            if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                                number       = "225"+entreprises.getContact();

                            }else{

                                number       = "+225"+entreprises.getContact();
                            }


                            String message = "Cher Partenaire "+entreprises.getCompagnie().toUpperCase()+", votre licence pour l'utilisation des services Di-Gital Web a été désactivée. Licence non renouvelée dans les délais. Date d'expiration "+formatter.format(entreprises.getDateFinContrat())+". Prière vous rapprocher de votre gestionnaire au 0757873487 pour renouvellement de votre licence numérique.";

                            SmsEnvoye smsEnvoye = new SmsEnvoye();

                            smsEnvoye.setTypeMessage(typeMessageService.findById(11L));

                            smsEnvoye.setNumeroDestinataire(number);

                            smsEnvoye.setDestinataire(entreprises.getCompagnie().toUpperCase());

                            smsEnvoye.setCorpsMessage(message);

                            smsPhoenixAccesService.smsSendToDigitalWeb(smsEnvoye);


                            entreprises1.setNotificateur(2L);

                            entrepriseService.update(entreprises1);
                        }
                    }
                }
            }
        }

        log.info(">>>>>>>>> Le nombre de message de rappel envoyé est de                    + >>>>>>>>> >>>>>>>>> + {}", q);
    }
}
