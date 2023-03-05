package com.phoenixacces.apps.controller.emodule.livraison;

import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.enumerations.TypeEnvoi;
import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.courrier.RechercheCourrierModel;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.module.livraison.*;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.module.livraison.LivraisonsService;
import com.phoenixacces.apps.persistence.services.module.livraison.NotificationsServices;
import com.phoenixacces.apps.persistence.services.parametrage.mLivraison.HistoriqueService;
import com.phoenixacces.apps.producer.JmsProducer;
import com.phoenixacces.apps.utiles.Utils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Module Livraisons - acheminement-colis")
@Slf4j
public class AcheminementController {

    private final JmsProducer jmsProducer;
    private final LivraisonsService livraisonsService;
    private final ProfileService profileService;
    private final NotificationsServices notificationsServices;
    private final HistoriqueService historiqueService;

    @Autowired
    public AcheminementController(
            JmsProducer jmsProducer,
            LivraisonsService livraisonsService,
            ProfileService profileService,
            NotificationsServices notificationsServices,
            HistoriqueService historiqueService){
        this.jmsProducer            = jmsProducer;
        this.livraisonsService      = livraisonsService;
        this.profileService         = profileService;
        this.notificationsServices  = notificationsServices;
        this.historiqueService      = historiqueService;
    }


    @GetMapping(value = "/find-all-livraison-by-profile-with-notification/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllLivraisonsByProfile(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            response = ResponseEntity.status(HttpStatus.OK).body(livraisonsService.findAllByProfile(profileService.findOne(idDigital), "OUI"));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-livraison-en-cours-by-profile-with-notification/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> livraisonEncours(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            response = ResponseEntity.status(HttpStatus.OK).body(livraisonsService.findAllByProfile(profileService.findOne(idDigital), "OUI", StatutLivraison.EN_COURS));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PutMapping(value = "/achiminement-colis-notification-customer", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Livreurs.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> notifCustomerForAchiminement(@RequestBody Livraisons object) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(object != null){

                Livraisons item = livraisonsService.findOne(object.getReference());

                String message = null;
                String number  = null;

                if(item!= null) {

                    //TODO ::: RECUPÉRATION DU MESSAGE DE NOTIFICATION
                    Notifications notifications = notificationsServices.findOne(object.getReference(), false);

                    if(notifications != null){

                        //+2250757419436
                        //number = "+2250749676733";
                        number = notifications.getContact();

                        if(TypeNotification.LIVRAISON == notifications.getTypeNotification()){

                            //message = "Cher(e) client(e) votre colis est en cours d'acheminement. Nous restons disponible pour tout autres informations au "+notifications.getProfile().getGareRoutiere().getCompagnie().getContact();

                            message = "Cher(e) client(e) votre colis de type "+item.getNatureColis().getTypecolis()+" et décris comme "+item.getDescriptionColis()+" est en cours d'acheminement. Pour d'amples informations nous restons disponible au "+notifications.getProfile().getGareRoutiere().getCompagnie().getContact();
                        }

                        //TODO ::: ENVOI DU MESSAGE AU DESTINATAIRE
                        if (!number.isEmpty()){

                            SmsMessage sms = new SmsMessage();

                            sms.setTypeMessage(4L);

                            sms.setToId(number);

                            sms.setContent(message);

                            sms.setFromName(notifications.getProfile().getNomPrenoms());

                            sms.setUsername(notifications.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                            sms.setPassword(notifications.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                            sms.setSenderId(notifications.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                            jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
                        }


                        //TODO ::: MISE A JOUR DE L'ETAT DU MESSAGE DU MESSAGE
                        notifications.setEnvoi(true);

                        Notifications _notif_ = notificationsServices.update(notifications);

                        if(_notif_.getId() != null){

                            //TODO ::: MISE A JOUR DE L'ETAT D'ACHIMINEMENT DE LA LIVRAISON
                            item.setEtatNotifArchemi("EFFECTUÉ");

                            item.setStatutLivraison(StatutLivraison.EN_COURS);

                            Livraisons _livr_ = livraisonsService.update(item);

                            if(_livr_.getId() != null){

                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                                LocalDateTime now = LocalDateTime.now();

                                //TODO ::: MISE EN PLACE DE L'HISTORIQUE
                                Historique historique = new Historique();

                                historique.setLibelle("Message de notification pour informer le client de la livraison de son colis a été envoyé par "+ _livr_.getEntreprise().getCompagnie()+"  à "+dtf.format(now));

                                historique.setLivraisons(_livr_);

                                historique.setOrdre(Utils.generateRandom(8));

                                historique.setTypeNotification(TypeNotification.INFO);

                                historiqueService.create(historique);

                                response = ResponseEntity.status(HttpStatus.OK).body(
                                    new RequestResponse(
                                        new RequestInformation(201, "Message de notification envoyé avec succès au Destinataire"),
                                        new RequestMessage(null, null)
                                    )
                                );
                            }
                            else{

                                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                    new RequestInformation(400, "Impossible de faire la mise à jour de l'etat d'acheminement de la livraison à la référence "+object.getReference()),
                                    new RequestMessage(null, "Success")
                                ));
                            }
                        }
                        else{

                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                    new RequestInformation(400, "Impossible de faire la mise à jour de la notification de la livraison à la référence "+object.getReference()),
                                    new RequestMessage(null, "Success")
                            ));
                        }
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Un message de notification d'acheminement a déjà été envoyé au destinataire ou  n'a pas été activé pour ce destinataire."),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de recuperer la livraison à la référence "+object.getReference()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getReference()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {

            e.printStackTrace();

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PutMapping(value = "/notification-acteur", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Livreurs.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> remerciemntClientAndCustomer(@RequestBody Livraisons object) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(object != null) {

                Livraisons item = livraisonsService.findOne(object.getReference());

                if(item!= null) {

                    String message = null;

                    if(item.getStatutLivraison() ==  StatutLivraison.EN_COURS){

                        System.out.println(">>>>>>>>>> TODO ::: TYPE ACTION "+object.getAction().toUpperCase()+" <<<<<<<<<<");

                        String number = item.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+item.getContactDestinataire();

                        //TODO ::: ENVOI DU MESSAGE DE REMERCIEMENT AU DESTINATAIRE
                        if(object.getAction().equalsIgnoreCase("Remerciement")) {

                            if(item.getNotifs() != null && item.getNotifs().equalsIgnoreCase("REMERCIEMENT")){

                                response = ResponseEntity.status(HttpStatus.OK).body(
                                    new RequestResponse(
                                        new RequestInformation(200, "Un message de notification pour rermercier le client à déjà été envoyé au client."),
                                        new RequestMessage(null, null)
                                    )
                                );
                            }
                            else{

                                //VERIFIONS SI UN MESSAGE DE REMERCIEMENT N'A PAS ETE ENVOYE
                                if(item.getMessageRemerciement() == 0){

                                    if (!item.getContactDestinataire().isEmpty() && !number.isEmpty()){

                                        SmsMessage sms1 = new SmsMessage();

                                        sms1.setTypeMessage(4L);

                                        sms1.setToId(number);

                                        sms1.setContent("Cher(e) client(e), Nous vous remercions d'avoir choisi "+item.getClient().getNomComplet()+" pour votre achat. Nous vous disons à bientot pour de potentiels achats.");

                                        sms1.setFromName(item.getProfile().getNomPrenoms());

                                        sms1.setUsername(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                        sms1.setPassword(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                        sms1.setSenderId(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                        jmsProducer.send(new JmsMessage("Sending to customer", Converter.pojoToJson(sms1), SmsMessage.class));
                                    }


                                    //TODO ::: ENVOI DU MESSAGE DE REMERCIEMENT AU CLIENT DE LA PART DE L'ENTREPRISE SOUSCRIPTRICE
                                    String numberPartenaire = item.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+item.getClient().getContact();

                                    if (!item.getClient().getContact().isEmpty() && !numberPartenaire.isEmpty()){

                                        SmsMessage sms = new SmsMessage();

                                        sms.setTypeMessage(4L);

                                        sms.setToId(numberPartenaire);

                                        sms.setContent("Cher Partenaire, Nous vous remercions d'avoir choisi "+item.getEntreprise().getCompagnie().toUpperCase()+" pour votre livraison. Le colis à la référence "+item.getReference()+" a été livré.");

                                        sms.setFromName(item.getProfile().getNomPrenoms());

                                        sms.setUsername(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                        sms.setPassword(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                        sms.setSenderId(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                        jmsProducer.send(new JmsMessage("Sending to partner", Converter.pojoToJson(sms), SmsMessage.class));
                                    }

                                    item.setMessageRemerciement(1);

                                    item.setStatutLivraison(StatutLivraison.LIVRE);

                                    item.setNotifs("REMERCIEMENT");

                                    livraisonsService.update(item);



                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                                    LocalDateTime now = LocalDateTime.now();

                                    //TODO ::: MISE EN PLACE DE L'HISTORIQUE
                                    Historique historique = new Historique();

                                    historique.setLibelle(object.getAction().equalsIgnoreCase("Remerciement") ? "Message de remerciement pour informer les différents acteurs a été envoyé par "+ item.getEntreprise().getCompagnie()+"  à "+dtf.format(now) : "Message de notification pour informer le client de l'arriver du colis à destination à "+dtf.format(now));

                                    historique.setLivraisons(item);

                                    historique.setOrdre(Utils.generateRandom(8));

                                    historique.setTypeNotification(TypeNotification.INFO);

                                    historiqueService.create(historique);


                                    response = ResponseEntity.status(HttpStatus.OK).body(
                                            new RequestResponse(
                                                    new RequestInformation(201, "L'action selectionnée a été réalisée avec succès."),
                                                    new RequestMessage(null, null)
                                            )
                                    );
                                }
                                else{

                                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                            new RequestInformation(400, "Un message de reciement a été déjà envoyé au différents acteurs (Client et Partenaire d'affaire)."),
                                            new RequestMessage(null, "Success")
                                    ));
                                }
                            }
                        }

                        if(object.getAction().equalsIgnoreCase("Livreur-Arrivé")) {

                            if(item.getNotifs() != null && item.getNotifs().equalsIgnoreCase("Livreur-Arrivé")){

                                response = ResponseEntity.status(HttpStatus.OK).body(
                                    new RequestResponse(
                                        new RequestInformation(200, "Un message de notification pour l'arriver du livreur à déjà été envoyé au client."),
                                        new RequestMessage(null, null)
                                    )
                                );
                            }
                            else{

                                message = "Cher(e) client(e), Votre colis est arrivé au lieu de recupération indiqué. Le livreur est joignable au "+item.getLivreur().getContact()+". \nNous vous remercions pour la confiance";

                                if (!item.getContactDestinataire().isEmpty() && !number.isEmpty()){

                                    SmsMessage sms = new SmsMessage();

                                    sms.setTypeMessage(4L);

                                    sms.setToId(number);

                                    sms.setContent(message);

                                    sms.setFromName(item.getProfile().getNomPrenoms());

                                    sms.setUsername(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                    sms.setPassword(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                    sms.setSenderId(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                    jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
                                }

                                item.setNotifs("LIVREUR-ARRIVÉ");

                                livraisonsService.update(item);

                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                                LocalDateTime now = LocalDateTime.now();

                                //TODO ::: MISE EN PLACE DE L'HISTORIQUE
                                Historique historique = new Historique();

                                historique.setLibelle(object.getAction().equalsIgnoreCase("Remerciement") ? "Message de remerciement pour informer les différents acteurs a été envoyé par "+ item.getEntreprise().getCompagnie()+"  à "+dtf.format(now) : "Message de notification pour informer le client de l'arriver du colis à destination à "+dtf.format(now));

                                historique.setLivraisons(item);

                                historique.setOrdre(Utils.generateRandom(8));

                                historique.setTypeNotification(TypeNotification.INFO);

                                historiqueService.create(historique);


                                response = ResponseEntity.status(HttpStatus.OK).body(
                                        new RequestResponse(
                                                new RequestInformation(201, "L'action selectionnée a été réalisée avec succès."),
                                                new RequestMessage(null, null)
                                        )
                                );
                            }
                        }
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                            new RequestInformation(200, "Impossible d'envoyer un message de remerciement car le statut du contrat n'est pas en cours."),
                            new RequestMessage(null, "Success")
                        ));
                    }

                }

                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de recuperer la livraison à la référence "+object.getReference()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getReference()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {

            e.printStackTrace();

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/recherche-de-colis-pour-acheminement", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = FicheBonDeCommande.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> rechercheFicheRecuperation (@RequestBody RechercheCourrierModel rechModel) {

        log.info("[ DI-GITAL WEB :: CONTROLLER ] SEARCH COLIS ACHEMINENET RESQUEST  =======================> param {}", rechModel);

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            Livraisons item = null;

            if(rechModel == null){

                response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(

                    new RequestResponse(
                        new RequestInformation(400, "Impossible de retrouver le colis pour acheminement avec les références données"),
                        new RequestMessage(null, null)
                    )
                );
            }
            else{

                if(rechModel.getOrdre() != null && rechModel.getReference() != null){

                    item = livraisonsService.findOne(rechModel.getOrdre(), rechModel.getReference());

                }

                else if(rechModel.getOrdre() == null && rechModel.getReference() != null){

                    item = livraisonsService.findOne(rechModel.getReference());
                }

                else if(rechModel.getOrdre() != null && rechModel.getReference() == null){

                    item = livraisonsService.findOneByOrdre(rechModel.getOrdre());
                }

                System.out.println(item);

                if(item != null){

                    //Livraisons livr = new Livraisons();

                    item.setAction(item.getStatutLivraison().getKey());

                    response = ResponseEntity.status(HttpStatus.OK).body(item);
                }
                else{

                    response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new RequestResponse(
                            new RequestInformation(400, "Impossible de retrouver le colis pour acheminement avec les références données"),
                            new RequestMessage(null, null)
                        )
                    );
                }
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            //e.printStackTrace();
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            log.info("[ DI-GITAL WEB :: CONTROLLER ] SEARCH COLIS ACHEMINENET RESQUEST =======================>  DONE");
            return response;
        }
    }
}
