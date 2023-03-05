package com.phoenixacces.apps.controller.emodule.livraison;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenixacces.apps.enumerations.Direction;
import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.audits.AuditFlux;
import com.phoenixacces.apps.persistence.entities.module.livraison.Historique;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livreurs;
import com.phoenixacces.apps.persistence.entities.module.livraison.Notifications;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.entities.service.SmsEnvoye;
import com.phoenixacces.apps.persistence.services.audits.AuditFluxService;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.module.livraison.LivraisonsService;
import com.phoenixacces.apps.persistence.services.module.livraison.LivreursService;
import com.phoenixacces.apps.persistence.services.module.livraison.MesClientsService;
import com.phoenixacces.apps.persistence.services.module.livraison.NotificationsServices;
import com.phoenixacces.apps.persistence.services.parametrage.SMSPhoenixAccesService;
import com.phoenixacces.apps.persistence.services.parametrage.TypeMessageService;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Module Livraisons - Fonctionnalité Mes Livraisons")
@Slf4j
public class LivraisonsController {

    private final ProfileService profileService;
    private final JmsProducer jmsProducer;
    private final MesClientsService mesClientsService;
    private final LivraisonsService livraisonsService;
    private final NotificationsServices notificationsServices;
    private final HistoriqueService historiqueService;
    private final AuditFluxService auditFluxService;
    private final SMSPhoenixAccesService smsService;
    private final TypeMessageService typeMessageService;
    private final LivreursService livreursService;

    @Autowired
    public LivraisonsController(
            JmsProducer jmsProducer, ProfileService profileService,
            MesClientsService mesClientsService,
            LivraisonsService livraisonsService,
            HistoriqueService historiqueService,
            NotificationsServices notificationsServices,
            AuditFluxService auditFluxService,
            SMSPhoenixAccesService smsService,
            TypeMessageService typeMessageService,
            LivreursService livreursService){
        this.livraisonsService      = livraisonsService;
        this.jmsProducer            = jmsProducer;
        this.profileService         = profileService;
        this.mesClientsService      = mesClientsService;
        this.notificationsServices  = notificationsServices;
        this.historiqueService      = historiqueService;
        this.auditFluxService       = auditFluxService;
        this.smsService             = smsService;
        this.typeMessageService     = typeMessageService;
        this.livreursService        = livreursService;
    }



    @GetMapping(value = "/find-all-livraison", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllLivraisons() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(livraisonsService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/find-all-livraison-by-profile/{idDigital}", produces = "application/json")
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
            response = ResponseEntity.status(HttpStatus.OK).body(livraisonsService.findAllByProfile(profileService.findOne(idDigital)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-livraison-succes-by-profile/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllLivraisonsSuccesByProfile(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(livraisonsService.findAllLivraionByProfile(profileService.findOne(idDigital) , StatutLivraison.LIVRE));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-livraison-pending-by-profile/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllLivraisonsPendingByProfile(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(livraisonsService.findAllLivraionByProfile(profileService.findOne(idDigital) , StatutLivraison.EN_ATTENTE));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/find-all-historique-livraison-by-ref/{reference}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllLivraisonsByReference(@PathVariable(name = "reference") String reference) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(historiqueService.findAll(livraisonsService.findOne(reference)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PutMapping(value = "/delete-item-livraison", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Livreurs.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteLivraisons(@RequestBody Livraisons object) {

        try {
            if(object != null){

                Livraisons item = livraisonsService.findOne(object.getReference());

                if(item!= null) {

                    livraisonsService.disable(item.getId());

                    Livraisons _up_ = item;

                    if (_up_.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Impossible de faire la désactivation de l'élement sélectionner."),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {
                        return ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, "Successful deletion of the data"),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getReference()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getReference()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




    @PutMapping(value = "/active-item-livraison", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "MesClients model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Livraisons.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeLivraisons(@RequestBody Livraisons object) {

        try {
            if(object != null){

                Livraisons item = livraisonsService.findOne(object.getReference());

                if(item!= null) {

                    livraisonsService.enable(item.getId());

                    Livraisons up = item;

                    if (up.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible d'activer le type contrat"),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        return ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, "Successful deletion of the data"),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire l'activation du type message "+object.getReference()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type messgae "+object.getReference()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping(value = "/deleted-all-livraison", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllLivraisons() {

        try {

            List<Livraisons> items = livraisonsService.findAll();

            for (Livraisons item :items){

                Livraisons models = livraisonsService.findOne(item.getReference());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    livraisonsService.update(models);
                }
            }

            return getResponseEntity(livraisonsService.findAll().isEmpty());
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PostMapping(value = "/create-new-bon-de-livraison", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewLivraison (@RequestBody Livraisons model) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        String auditKey = Utils.instant2String();

        try {

            Notifications notifications     = null;
            Livraisons livraison            = null;

            System.out.println(">>>>>>>>>>>>>>>> DEBUT DU PROCESS DE CREATION DU BON DE LIVRAISON <<<<<<<<<<<<<<<<<<");

            if(model == null && model.getQte() == 0) {

                response = ResponseEntity.status(HttpStatus.CREATED).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                auditFluxService
                    .create(new AuditFlux(
                            null, auditKey, Instant.now(),
                            TypeNotification.LIVRAISON.toString(),
                            "ENREGISTREMENT LIVRAISON POUR LE CLIENT "+ model.getNomPrenomsDestinataire().toUpperCase(),
                            new ObjectMapper().writeValueAsString(model), "", Direction.INPUT
                    ));

                //TODO :: VERIFIONS LUNICITE DE LA REFERENCE DU COLIS
                if(livraisonsService.findOne(model.getReference().toUpperCase()) == null ) {

                    System.out.println(">>>>>>>>>>>>>>>> ETAPE 01 - START <<<<<<<<<<<<<<<<<<");

                    //TODO :: CREATION DE LA LIVRAISON ET AFFECTATION AU LIVREUR DISPONIBLE
                    livraison = new Livraisons();

                    livraison.setOrdre(Utils.generateRandom(8));

                    livraison.setReference(model.getReference().toUpperCase());

                    livraison.setDatelivraison(model.getDatelivraison());

                    livraison.setNotifClient(model.getNotifClient().toUpperCase());

                    livraison.setEntreprise(model.getEntreprise());

                    livraison.setProfile(model.getProfile());

                    livraison.setInfoComplementaire(model.getInfoComplementaire());

                    livraison.setStatutLivraison(StatutLivraison.EN_ATTENTE);

                    livraison.setLivreur(model.getLivreur());

                    livraison.setZoneLivraison(model.getZoneLivraison());

                    livraison.setZoneRecuperation(model.getZoneRecuperation());

                    livraison.setPrecisionZoneRecup(model.getPrecisionZoneRecup());

                    livraison.setPrecisonLieuLivraison(model.getPrecisonLieuLivraison());

                    livraison.setClient(model.getClient());

                    livraison.setNatureColis(model.getNatureColis());

                    livraison.setDescriptionColis(model.getDescriptionColis());

                    livraison.setPrixLivraison(model.getPrixLivraison());

                    livraison.setMontantColis(model.getMontantColis());

                    livraison.setNomPrenomsDestinataire(model.getNomPrenomsDestinataire().toUpperCase());

                    livraison.setContactDestinataire(model.getContactDestinataire());

                    livraison.setEtatNotifArchemi(model.getNotifClient().equalsIgnoreCase("OUI") ? "NON EFFECTUÉ" : "N/A");

                    livraison.setQte(model.getQte() == 0 ? 1 : model.getQte());

                    Livraisons lvson = livraisonsService.create(livraison);

                    if (lvson.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                            new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                new RequestMessage(null, null)
                            )
                        );
                    }
                    else {

                        //TODO :: MISE A JOUR DE LA COMMISSION
                        System.out.println(">>>>>>>>>>>>>>>> ETAPE 00 - TODO :: MISE A JOUR DE LA COMMISSION <<<<<<<<<<<<<<<<<<");
                        if(model.getLivreur().getTypeContrat().getId() == 5){

                            double comTotal = 0;

                            //TODO ::: GET LIVREUR INFORMATION
                            Livreurs livreurs = livreursService.findOne(model.getLivreur().getId());

                            if(livreurs != null){

                                comTotal = livreurs.getCommission() + Math.round(model.getPrixLivraison() * (livreurs.getTauxComm()/100));

                                livreurs.setCommission(Math.round(comTotal));

                                livreursService.update(livreurs);
                            }
                        }


                        System.out.println(">>>>>>>>>>>>>>>> ETAPE 01 - FIN <<<<<<<<<<<<<<<<<<");

                        if(model.getNotifClient().equalsIgnoreCase("OUI")){

                            System.out.println(">>>>>>>>>>>>>>>> TODO :: ENREGISTREMENT DE LA NOTIFICATION SMS DU CLIENT LORSQUE LE COURSIER DEMARRERA LA LIVRAISON <<<<<<<<<<<<<<<<<<");

                            //TODO :: ENREGISTREMENT DE LA NOTIFICATION SMS POUR LE COMPTE DU CLIENT

                            Calendar calendar = Calendar.getInstance();

                            calendar.add(Calendar.DAY_OF_MONTH, 1);

                            Date dateEnvoi =  calendar.getTime();


                            notifications = new Notifications();

                            notifications.setRef(model.getReference().toUpperCase());

                            notifications.setTypeNotification(TypeNotification.LIVRAISON);

                            notifications.setDateEnvoi(dateEnvoi);

                            notifications.setNomDestinataire(model.getNomPrenomsDestinataire().toUpperCase());

                            notifications.setContact(model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+model.getContactDestinataire());

                            notifications.setNomLivreur(lvson.getLivreur().getNomPrenoms());

                            notifications.setModule(model.getProfile().getModule());

                            notifications.setContactLivreur(lvson.getLivreur().getContact());

                            notifications.setProfile(model.getProfile());

                            notificationsServices.create(notifications);

                            //System.out.println(">>>>>>>>>>>>>>>> ETAPE 02 - FIN <<<<<<<<<<<<<<<<<<");

                        }

                        //System.out.println(">>>>>>>>>>>>>>>> TODO :: ENVOI SMS AU LIVREUR POUR NOTIFICATIONS <<<<<<<<<<<<<<<<<<");

                        if(!model.getProfile().getGareRoutiere().getCompagnie().getAbbrev().equalsIgnoreCase("FIFUN-LC")){

                            //TODO :: ENVOI SMS AU LIVREUR POUR NOTIFICATIONS
                            String number       = model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+lvson.getLivreur().getContact();
                            String nomReceiver  = lvson.getLivreur().getNomPrenoms().length() > 1 ? WordUtils.capitalize(lvson.getLivreur().getNomPrenoms().toLowerCase()).split(" ")[1] : WordUtils.capitalize(lvson.getLivreur().getNomPrenoms().toLowerCase()).split(" ")[0];
                            String nomClient    = lvson.getNomPrenomsDestinataire();
                            String message      = "Bonjour "+ nomReceiver +", Une livraison vous a été confié de la part de "+lvson.getEntreprise().getCompagnie()+" pour le compte du client "+nomClient+". Consulter l'application pour de amples informations. InfoLine : "+model.getProfile().getGareRoutiere().getCompagnie().getContact()+"";

                            if (!number.isEmpty()){

                                SmsMessage smsMessage = new SmsMessage();

                                smsMessage.setTypeMessage(4L);

                                smsMessage.setToId(number);

                                smsMessage.setContent(message);

                                smsMessage.setFromName(lvson.getLivreur().getNomPrenoms());

                                smsMessage.setUsername(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                smsMessage.setPassword(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                smsMessage.setSenderId(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(smsMessage), SmsMessage.class));
                            }
                        }

                        //TODO ::: ENVOI DE L'SMS AU PARTENAIRE AFIN QUI PUISSE SUIVRE SON COLIS A LIVRER
                        String numberPartenaireAff       = model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+lvson.getClient().getContact();
                        String nomClientPartenaireAff    = lvson.getClient().getNomComplet();
                        String messagePartenaireAff      = "Cher Partenaire "+ nomClientPartenaireAff +", votre colis est en cours d'achiminement pour le compte de "+model.getNomPrenomsDestinataire().toUpperCase()+". Le code "+model.getReference().toUpperCase()+" pour le suivi. Merci pour la confiance accordée";
                        //String messagePartenaireAff      = "Cher Partenaire "+ nomClientPartenaireAff +", votre colis est en cours d'achiminement. Le code "+model.getReference().toUpperCase()+" pour le suivi. Merci pour la confiance accordée";

                        if (!numberPartenaireAff.isEmpty()){

                            SmsMessage smsMsg = new SmsMessage();

                            smsMsg.setTypeMessage(4L);

                            smsMsg.setToId(numberPartenaireAff);

                            smsMsg.setContent(messagePartenaireAff);

                            smsMsg.setFromName(nomClientPartenaireAff);

                            smsMsg.setUsername(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                            smsMsg.setPassword(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                            smsMsg.setSenderId(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                            jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(smsMsg), SmsMessage.class));
                        }



                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                        LocalDateTime now = LocalDateTime.now();

                        //System.out.println(">>>>>>>>>>>>>>>> ENREGISTREMENT DE L'HISTORISATION DateTimeFormatter <<<<<<<<<<<<<<<<<<" + dtf.format(now));

                        // TODO ::: ENREGISTREMENT DE L'HISTORISATION
                        Historique historique = new Historique();

                        historique.setLibelle("Enregistrement du colis pour le compte du destinataire "+ model.getNomPrenomsDestinataire().toUpperCase()+" pour une livraison prévu pour le "+ model.getDatelivraison()+". \nAffectation effectué par " +model.getProfile().getNomPrenoms()+"  à "+dtf.format(now));

                        historique.setLivraisons(lvson);

                        historique.setOrdre(Utils.generateRandom(8));

                        historique.setTypeNotification(TypeNotification.ENREGISTREMENT);

                        Historique hist = historiqueService.create(historique);

                        if(hist.getId() == null){

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Echec de l'enregistrement de l'historique de la livraion à la référence " + model.getReference().toUpperCase()),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }
                        else{

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(201, "Successfully created data with creadential account user"),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.CREATED).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_REQUEST.value(), "La livraison à la référence "+model.getReference()+" a été déjà enregistré. Prière reinitialiser le formulaire si vous souhaitez faire un nouveau enregistrement."),
                                    new RequestMessage(null, null)
                            )
                    );

                    auditFluxService
                        .create(new AuditFlux(
                            null, auditKey, Instant.now(),
                            TypeNotification.LIVRAISON.toString(),
                            "ENREGISTREMENT LIVRAISON POUR LE CLIENT "+ model.getNomPrenomsDestinataire().toUpperCase(),
                            new ObjectMapper().writeValueAsString(model),
                            "La livraison à la référence "+model.getReference()+" a été déjà enregistré. Prière reinitialiser le formulaire si vous souhaitez faire un nouveau enregistrement.",
                            Direction.OUTPUT
                        ));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

            auditFluxService
                .create(new AuditFlux(
                    null, auditKey, Instant.now(),
                    TypeNotification.LIVRAISON.toString(),
                    "Echec de l'enregistrement du colis pour le compte du client "+ model.getNomPrenomsDestinataire().toUpperCase(),
                    new ObjectMapper().writeValueAsString(model), e.getMessage(), Direction.OUTPUT
                ));
        }

        finally {

            return response;
        }
    }



    @PutMapping(value = "/update-bon-de-livraison", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteLivraisons(@RequestBody Livraisons update){

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(update == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                //TODO ::: RECUPERATION DE LA LIVRAISON ENREGISTRES
                Livraisons livraisons = livraisonsService.findOne(update.getOrdre(), update.getReference());

                if(livraisons != null){

                    livraisons.setDatelivraison(update.getDatelivraison());

                    livraisons.setNotifClient(update.getNotifClient().toUpperCase());

                    livraisons.setInfoComplementaire(update.getInfoComplementaire());

                    livraisons.setLivreur(update.getLivreur());

                    livraisons.setZoneLivraison(update.getZoneLivraison());

                    livraisons.setZoneRecuperation(update.getZoneRecuperation());

                    livraisons.setPrecisionZoneRecup(update.getPrecisionZoneRecup());

                    livraisons.setPrecisonLieuLivraison(update.getPrecisonLieuLivraison());

                    livraisons.setClient(update.getClient());

                    livraisons.setNatureColis(update.getNatureColis());

                    livraisons.setDescriptionColis(update.getDescriptionColis());

                    livraisons.setPrixLivraison(update.getPrixLivraison());

                    livraisons.setMontantColis(update.getMontantColis());

                    livraisons.setNomPrenomsDestinataire(update.getNomPrenomsDestinataire().toUpperCase());

                    livraisons.setContactDestinataire(update.getContactDestinataire());

                    Livraisons lvson = livraisonsService.update(livraisons);

                    if (lvson.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                    else {

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                        LocalDateTime now = LocalDateTime.now();

                        // TODO ::: ENREGISTREMENT DE L'HISTORISATION
                        Historique historique = new Historique();

                        historique.setLibelle("Une mise à jour a été effectué sur la livraison du client "+ update.getNomPrenomsDestinataire().toUpperCase()+". \nModification effectué par " +update.getProfile().getNomPrenoms()+"  le "+dtf.format(now));

                        historique.setLivraisons(lvson);

                        historique.setOrdre(Utils.generateRandom(8));

                        historique.setTypeNotification(TypeNotification.ENREGISTREMENT);

                        Historique hist = historiqueService.create(historique);

                        if(hist.getId() == null){

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Echec de l'enregistrement de l'historique de la livraion à la référence " + update.getReference().toUpperCase()),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }
                        else{

                            //TODO :: ENVOI SMS AU LIVREUR POUR NOTIFICATIONS
                            String number       = update.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+update.getLivreur().getContact();
                            String nomReceiver  = update.getLivreur().getNomPrenoms().length() > 1 ? WordUtils.capitalize(update.getLivreur().getNomPrenoms().toLowerCase()).split(" ")[1] : WordUtils.capitalize(update.getLivreur().getNomPrenoms().toLowerCase()).split(" ")[0];
                            String nomClient    = update.getNomPrenomsDestinataire();

                            if (!number.isEmpty()){

                                SmsMessage sms = new SmsMessage();

                                sms.setTypeMessage(4L);

                                sms.setToId(number);

                                sms.setContent("Bonjour "+ nomReceiver +", La livraison à la référence "+update.getReference()+" qui vous a été confié par "+update.getEntreprise().getCompagnie()+" pour le compte du client "+nomClient+" a subi quelques modifications. Nous vous prions de vous connecter à l'application mobile pour consulter votre Bon de Livraison ou contacter l'opérateur. Info-Line :"+update.getProfile().getGareRoutiere().getCompagnie().getContact()+"");

                                sms.setFromName(update.getLivreur().getNomPrenoms());

                                sms.setUsername(update.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                sms.setPassword(update.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                sms.setSenderId(update.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
                            }

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(201, "Successfully updated data"),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_GATEWAY.value(), "Données non existante"),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
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



    @PutMapping(value = "/non-case-traitement-bon-de-livraison", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteLivraisonNonCases(@RequestBody Livraisons update){

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(update == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                //TODO ::: RECUPERATION DE LA LIVRAISON ENREGISTRES
                Livraisons livraisons = livraisonsService.findOne(update.getOrdre(), update.getReference());

                if(livraisons != null){

                    livraisons.setStatutLivraison(update.getStatutRefus());

                    livraisons.setMotifNonLivraison(update.getMotifNonLivraison().toUpperCase());

                    livraisons.setNotifs(update.getStatutRefus().getKey());

                    Livraisons lvson = livraisonsService.update(livraisons);

                    if (lvson.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                    else {

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                        LocalDateTime now = LocalDateTime.now();

                        // TODO ::: ENREGISTREMENT DE L'HISTORISATION
                        Historique historique = new Historique();

                        historique.setLibelle("Livraison classée en non-case pour motif "+update.getStatutRefus().getKey()+". Non-Case traité par " +update.getProfile().getNomPrenoms()+"  le "+dtf.format(now));

                        historique.setLivraisons(lvson);

                        historique.setOrdre(Utils.generateRandom(8));

                        historique.setTypeNotification(TypeNotification.FEEDBACK);

                        Historique hist = historiqueService.create(historique);

                        if(hist.getId() == null){

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Echec de l'enregistrement de l'historique de la livraion à la référence " + update.getReference().toUpperCase()),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }
                        else{

                            //TODO :: ENVOI SMS AU PARTENAIRE AFFAIRE POUR NOTIFICATIONS
                            String number       = update.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+update.getClient().getContact();
                            String nomClient    = update.getClient().getNomComplet();
                            String msg0         = "Cher Partenaire, Le colis à la référence "+update.getReference()+" a été placé en non-case. Prière le consulter pour de amples informations. \nInfoLine : "+update.getProfile().getGareRoutiere().getCompagnie().getContact()+"";

                            if (!number.isEmpty()){

                                SmsMessage sms = new SmsMessage();

                                sms.setTypeMessage(4L);

                                sms.setToId(number);

                                sms.setContent(msg0);

                                sms.setFromName(nomClient);

                                sms.setUsername(update.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                sms.setPassword(update.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                sms.setSenderId(update.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
                            }

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(201, "Successfully updated data"),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_GATEWAY.value(), "Données non existante"),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
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

    public static ResponseEntity<?> getResponseEntity(boolean empty) throws Exception {
        System.gc();

        if(empty) {

            RequestResponse requestResponse = new RequestResponse(
                    new RequestInformation(201, "Successful deletion of the data"),
                    new RequestMessage(null, "Success")
            );

            return ResponseEntity.status(HttpStatus.OK).body(requestResponse);
        }
        else {

            throw new Exception("Impossible de faire la suppression en masse des données");
        }
    }

}
