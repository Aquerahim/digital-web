package com.phoenixacces.apps.controller.emodule.assurance;

import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.assurance.*;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.NotificationSysteme;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.entities.service.Birthday;
import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
import com.phoenixacces.apps.persistence.models.GroupeDto;
import com.phoenixacces.apps.persistence.models.ModelPorteFeuille;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.authentification.UserService;
import com.phoenixacces.apps.persistence.services.module.assurance.*;
import com.phoenixacces.apps.persistence.services.parametrage.*;
import com.phoenixacces.apps.producer.JmsProducer;
import com.phoenixacces.apps.utiles.Utils;
import com.phoenixacces.apps.utiles.storages.StorageServiceExcel;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Assurance Module Controller")
@Slf4j
public class AssuranceModuleController {

    private final JmsProducer jmsProducer;
    private final EntrepriseService entrepriseService;
    private final ProfileService profileService;
    private final PorteFeuilleClientService porteFeuilleClientService;
    private final ModelMessageService modelMessageService;
    private final BirthdayService birthdayService;
    private final TypeMessageService typeMessageService;
    private final SloganEntrepriseService sloganEntrepriseService;
    private final TypeClientService typeClientService;
    private final ProduitAssuranceService produitAssuranceService;
    private final SendMessageService sendMessageService;
    private final ParametrageDateService parametrageDateService;
    private final NotificationSystemeService notificationService;
    private final SmsCredentialService smsCredentialService;
    private final StorageServiceExcel storageService;
    private final UserService userService;
    private final GroupeService groupeService;
    private final ProspectServices prospectServices;
    //Service pour l'import
    private final  CiviliteService civiliteService;
    private final  NotificationEnMasseService notificationEnMasseService;

    @Autowired
    public AssuranceModuleController(
            CiviliteService civiliteService,
            JmsProducer jmsProducer,
            ProfileService profileService,
            PorteFeuilleClientService porteFeuilleClientService,
            BirthdayService birthdayService,
            ModelMessageService modelMessageService,
            TypeMessageService typeMessageService,
            SloganEntrepriseService sloganEntrepriseService,
            EntrepriseService entrepriseService,
            TypeClientService typeClientService,
            ProduitAssuranceService produitAssuranceService,
            SendMessageService sendMessageService,
            ParametrageDateService parametrageDateService,
            NotificationSystemeService notificationService,
            SmsCredentialService smsCredentialService,
            StorageServiceExcel storageService,
            UserService userService,
            ProspectServices prospectServices,
            GroupeService groupeService,
            NotificationEnMasseService notificationEnMasseService
    ){
        this.civiliteService                    = civiliteService;
        this.jmsProducer                        = jmsProducer;
        this.profileService                     = profileService;
        this.porteFeuilleClientService          = porteFeuilleClientService;
        this.birthdayService                    = birthdayService;
        this.modelMessageService                = modelMessageService;
        this.typeMessageService                 = typeMessageService;
        this.sloganEntrepriseService            = sloganEntrepriseService;
        this.entrepriseService                  = entrepriseService;
        this.typeClientService                  = typeClientService;
        this.produitAssuranceService            = produitAssuranceService;
        this.sendMessageService                 = sendMessageService;
        this.parametrageDateService             = parametrageDateService;
        this.notificationService                = notificationService;
        this.smsCredentialService               = smsCredentialService;
        this.storageService                     = storageService;
        this.userService                        = userService;
        this.prospectServices                   = prospectServices;
        this.notificationEnMasseService         = notificationEnMasseService;
        this.groupeService                      = groupeService;
    }

    SimpleDateFormat datStr = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat heure = new SimpleDateFormat("HH:mm:ss");

    @Value(value = "${upload-dir}")
    private String INTPUT_FOLDER;

    @Value("${output-dir}")
    private String OUTPUT_FOLDER;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Value("${api.sms.gateway}")
    private String gateway;


    @GetMapping(value = "/find-all-portefeuille-client/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findallPorteFeuille(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(!idDigital.isEmpty()){

                Profile profile = profileService.findOne(idDigital);

                if(profile != null){

                    response = ResponseEntity.status(HttpStatus.OK).body(

                            porteFeuilleClientService.findAll(profile.getGareRoutiere().getCompagnie())
                    );
                }
                else{

                    response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/details-campagne-message/{idPorteFeuille}/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> detailsCampagne(@PathVariable(name = "idPorteFeuille") Long idPorteFeuille, @PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(!idDigital.isEmpty() && idPorteFeuille > 0){

                Profile profile = profileService.findOne(idDigital);

                if(profile != null){

                    ModelPorteFeuille modelPorteFeuille = new ModelPorteFeuille();

                    PorteFeuilleClient porteFeuilleClient = porteFeuilleClientService.findOne(idPorteFeuille);

                    int cpteMsgAnnule       = 0;
                    int cpteMsgPending      = 0;
                    int cpteMsgLu           = 0;
                    int cpteMsgNonLu        = 0;

                    if(porteFeuilleClient != null){

                        modelPorteFeuille.setPorteFeuilleClient(porteFeuilleClient);

                        modelPorteFeuille.setSendMessageList(sendMessageService.findAll(porteFeuilleClient));

                        for (SendMessage sendMessage : modelPorteFeuille.getSendMessageList()) {

                            if(sendMessage.getStatut() == EtatLecture.ANNULER){

                                cpteMsgAnnule++;
                            }

                            if(sendMessage.getStatut() == EtatLecture.PENDING){

                                cpteMsgPending++;
                            }

                            if(sendMessage.getStatut() == EtatLecture.LU){

                                cpteMsgLu++;
                            }

                            if(sendMessage.getStatut() == EtatLecture.NON_LU){

                                cpteMsgNonLu++;
                            }
                        }

                        modelPorteFeuille.setMessageAnnule(cpteMsgAnnule);

                        modelPorteFeuille.setMessageEnvoye(cpteMsgLu);

                        modelPorteFeuille.setMessageEnAttente(cpteMsgPending);

                        modelPorteFeuille.setMessageNonEnvoye(cpteMsgNonLu);

                        response = ResponseEntity.status(HttpStatus.OK).body(modelPorteFeuille);
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.OK).body("Aucun porte feuille client enregistré");
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
            }

        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-client-in-porte-feuille", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<Object> createdNewPorteFeuilleClient (@RequestBody PorteFeuilleClient entity) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        String number;

        try {

            if(entity == null) {

                response = ResponseEntity.status(HttpStatus.CREATED).body(

                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises")
                );
            }
            else {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");

                //RECUPERATION DES DATE DE FETE PROGRAMMEES
                String dateDepart           = parametrageDateService.findOne("date.nouvel.an").getDateDeb();
                String dateFeteNationnal    = parametrageDateService.findOne("date.fete.nationnal").getDateDeb();
                String dateFetePaix         = parametrageDateService.findOne("date.fete.paix").getDateDeb();
                String dateFetePaque        = parametrageDateService.findOne("date.fete.paques").getDateDeb();
                String dateFeteAssomption   = parametrageDateService.findOne("date.fete.assomption").getDateDeb();
                String dateFeteAscension    = parametrageDateService.findOne("date.fete.ascension").getDateDeb();
                String dateFeteTravail      = parametrageDateService.findOne("date.fete.travail").getDateDeb();
                String dateFeteNoel         = parametrageDateService.findOne("date.fete.noel").getDateDeb();
                String dateFeteDestin       = parametrageDateService.findOne("date.fete.destin").getDateDeb();
                String dateFeteFitr         = parametrageDateService.findOne("date.fete.fitr").getDateDeb();
                String dateFeteTabaski      = parametrageDateService.findOne("date.fete.tabaski").getDateDeb();
                String dateFeteMahomet      = parametrageDateService.findOne("date.fete.mahomet").getDateDeb();
                String dateFeteToussaint    = parametrageDateService.findOne("date.fete.toussaint").getDateDeb();
                String dateFetePentecote    = parametrageDateService.findOne("date.fete.pentecote").getDateDeb();


                //Vérfication du quota minimum d'sms qu'il faut pour la programmation
                SmsCredential credential = smsCredentialService.findOne(entity.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getId());

                if(credential != null){

                    Long quotaSms         = null;

                    if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                        number       = "225"+entity.getContact();

                        quotaSms     = credential.getNombreSmsLeTexto();

                    }else{

                        number       = entity.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+entity.getContact();

                        quotaSms      = credential.getNombreSms();
                    }

                    if(quotaSms > 30){

                        //VERIFION SI LE CLIENT N'A PAS ETE DEJA AJOUTE AU PORTE-FEUILLE
                        if(porteFeuilleClientService.findOne(entity.getNomClient().toUpperCase(), entity.getProduit(), entity.getEntreprises()) == null ) {

                            String nomClient = WordUtils.capitalize(entity.getNomClient().toLowerCase());

                            PorteFeuilleClient porteFeuilleClient = new PorteFeuilleClient();

                            porteFeuilleClient.setNomCapagne("CAMPAGNE SMS DE - "+ entity.getNomClient().toUpperCase());

                            porteFeuilleClient.setOrdre(Utils.generateRandom(8));

                            porteFeuilleClient.setCivilite(entity.getCivilite());

                            porteFeuilleClient.setNomClient(entity.getNomClient().toUpperCase());

                            porteFeuilleClient.setContact(entity.getContact());

                            porteFeuilleClient.setNumeroContrat(entity.getNumeroContrat() == null ? "" : entity.getNumeroContrat().toUpperCase());

                            porteFeuilleClient.setFete(entity.getFete());

                            porteFeuilleClient.setDateNaissance(entity.getDateNaissance());

                            porteFeuilleClient.setTypeClient(entity.getTypeClient());

                            porteFeuilleClient.setProduit(entity.getProduit());

                            porteFeuilleClient.setProfile(entity.getProfile());

                            porteFeuilleClient.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                            porteFeuilleClient.setNotifMsgBienvenu(entity.getNotifMsgBienvenu());

                            porteFeuilleClient.setNotifSouscription(entity.getSouscritPrdt());

                            porteFeuilleClient.setSouscritPrdt(entity.getSouscritPrdt());

                            porteFeuilleClient.setNotifAnniv(entity.getNotifAnniv());

                            if(entity.getDateNaissance() != null && entity.getNotifAnniv() != null && entity.getNotifAnniv().equalsIgnoreCase("OUI")){

                                String birthDay = entity.getDateNaissance().toString().split("-")[2]+"/"+entity.getDateNaissance().toString().split("-")[1];

                                if(birthdayService.findeOne(birthDay, entity.getNomClient(), ""+ Calendar.YEAR) == null){

                                    Birthday birthday = new Birthday();

                                    birthday.setBirthday(entity.getDateNaissance().toString().split("-")[2]+"/"+entity.getDateNaissance().toString().split("-")[1]);

                                    birthday.setAnniversaireux(nomClient);

                                    birthday.setAnnee(""+Calendar.YEAR);

                                    birthday.setEnvoi(false);

                                    birthdayService.create(birthday);
                                }
                            }

                            porteFeuilleClient.setNotifRappelEcheJ45(entity.getNotifRappelEcheJ45());

                            porteFeuilleClient.setNotifRappelEcheJ15(entity.getNotifRappelEcheJ15());

                            porteFeuilleClient.setNotifFeteNationale(entity.getNotifFeteNationale());

                            porteFeuilleClient.setNotifRappelVisiteTechnique(entity.getNotifRappelVisiteTechnique());

                            porteFeuilleClient.setImmatriculation(entity.getImmatriculation() == null ? "" : entity.getImmatriculation().toUpperCase());

                            porteFeuilleClient.setDateExpirationVisite(entity.getDateExpirationVisite());

                            porteFeuilleClient.setTypeVehicule(entity.getTypeVehicule());

                            porteFeuilleClient.setExpirationContrat(entity.getExpirationContrat());

                            if(entity.getCivilite().getCodeCivilite().equalsIgnoreCase("M.") && entity.getNotifs() != null){

                                porteFeuilleClient.setNotifFetePere(entity.getNotifs());

                                porteFeuilleClient.setNotifFeteMere("NON");
                            }

                            if((entity.getCivilite().getCodeCivilite().equalsIgnoreCase("MLLE") || entity.getCivilite().getCodeCivilite().equalsIgnoreCase("MME"))  && entity.getNotifs() != null){

                                porteFeuilleClient.setNotifFeteMere(entity.getNotifs());

                                porteFeuilleClient.setNotifFetePere("NON");
                            }

                            if(entity.getCivilite().getCodeCivilite().equalsIgnoreCase("ENTREPRISE") && entity.getNotifs() != null){

                                porteFeuilleClient.setNotifFetePere(entity.getNotifs());

                                porteFeuilleClient.setNotifFeteMere(entity.getNotifs());
                            }

                            porteFeuilleClient.setContact2(entity.getContact2() == null ? "" : entity.getContact2());

                            if(entity.getFete().equalsIgnoreCase("CATHOLIQUE") || entity.getFete().equalsIgnoreCase("MUSULMANE")){

                                porteFeuilleClient.setNotifFeteReligieuse("OUI");
                            }

                            if(entity.getFete().equalsIgnoreCase("AUCUNE")){

                                porteFeuilleClient.setNotifFeteReligieuse("NON");

                            }

                            PorteFeuilleClient client = porteFeuilleClientService.create(porteFeuilleClient);

                            if (client.getId() == null) {

                                response = ResponseEntity.status(HttpStatus.CREATED).body(

                                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                                );
                            }
                            else {

                                //ENVOI DU MESSAGE DE BIENVENU A LA JMS SI CELA EST CONFIGURE
                                sendWelComMessage(credential, client, number);


                                sendMsgThenToSubscribeProduct(client, number, credential);


                                //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Avis d’échéance à J -45 de son contrat
                                saveNotifAvisMois45(client, number, credential);


                                //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Avis d’échéance à J -15 de son contrat
                                saveNotifAvisMois15(credential, client, number);


                                //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification lors des fêtes nationales
                                saveNotificationFeteNationnale(client, number, formatter, dateDepart, dateFeteNationnal, dateFetePaix, dateFeteTravail, credential);


                                //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification lors des fêtes CATHOLIQUES
                                saveNotifFeteCatholique(formatter, dateFetePaque, dateFeteAssomption, dateFeteAscension, dateFeteNoel, dateFeteToussaint, dateFetePentecote, client, number, credential);


                                //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification lors des fêtes MUSULMANES
                                saveNotifFeteMusulmane(client, number, formatter, dateFeteDestin, dateFeteFitr, dateFeteTabaski, dateFeteMahomet, credential);


                                //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification expiration de sa visite technique
                                saveNotifExpirationVisiteTechnique(client, number, credential);


                                //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification anniversaire
                                saveBithDayNotification(client, number, credential);


                                //Enregistrement de la notification
                                saveNotification("Enregistrement du client ", client.getNomClient().toUpperCase(), " dans votre porte-feuille client le ", ".", client, TypeNotification.Add_Client);

                                response = ResponseEntity.status(HttpStatus.CREATED).body(

                                        new RequestInformation(201, "Successfully created data")
                                );
                            }
                        }
                        else {

                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                    new RequestInformation(400, "Cet enregistrement a déjà été faite. Doublons évité")
                            );
                        }
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(605, "Votre quota minimum d'sms pour la programmation est de 30 sms. Nous prions d'effectuer au rechargement de votre compte.")

                        );
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(400, "Votre quota d'sms ne vous permet pas d'avoir accès à la plate-forme.")

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


    private void sendMsgThenToSubscribeProduct(PorteFeuilleClient entity, String number, SmsCredential credential) throws Exception {

        System.out.println("==========> ENVOI DU MESSAGE DE SOUSCRIPTION QUAND IL SOUSCRIPT");
        if(entity.getSouscritPrdt() != null && entity.getSouscritPrdt().equalsIgnoreCase("OUI")){

            String nomCplet = entity.getCivilite().getCodeCivilite() + " "+ entity.getNomClient();

            String promReceiver = WordUtils.capitalize(nomCplet.toLowerCase());

            if (number != null && !number.isEmpty() && !promReceiver.isEmpty()){

                //RECUPERATION DU MESSAGE PARAMETRE
                ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(12L), entity.getProfile().getGareRoutiere().getCompagnie(), "PREMIÈRE SOUSCRIPTION");

                SloganEntreprise sloganEntreprise = sloganEntrepriseService.findOne(entity.getProfile().getGareRoutiere().getCompagnie());

                if(modelMessage != null && sloganEntreprise != null){

                    String message = modelMessage.getMessage().replace("[nomProduit]", WordUtils.capitalize(entity.getProduit().getProduit()))+"\n"+sloganEntreprise.getSlogan()+"";

                    SmsMessage sms = new SmsMessage();

                    sms.setTypeMessage(1L);

                    sms.setToId(number);

                    sms.setContent(message);

                    sms.setFromName(entity.getNomClient().toUpperCase());

                    sms.setUsername(entity.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                    sms.setPassword(entity.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                    sms.setSenderId(entity.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                    jmsProducer.send(new JmsMessage("Envoi du message de souscription", Converter.pojoToJson(sms), SmsMessage.class));


                    //MISE A JOUR DU QUOTA SMS
                    if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                        credential.setNombreSmsLeTexto(credential.getNombreSmsLeTexto() - 1);
                    }
                    else{

                        credential.setNombreSms(credential.getNombreSms() - 1);
                    }

                    smsCredentialService.update(credential);

                    //Enregistrement de la notification
                    saveNotification("L'envoi du message de prémière souscription au client ", promReceiver, " à la date du ", " a été effectué avec succès.", entity, TypeNotification.Connexion);
                }
            }
        }
    }



    @PostMapping(value = "/importe-porte-feuille-client-fichier/{profileId}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> importePorteFeuilleClient (@RequestParam(name = "file") MultipartFile multipartFile, @PathVariable(name = "profileId") String profileId) {

        ResponseEntity<? extends Object> response = ResponseEntity.status(HttpStatus.NO_CONTENT).body("No content to upload");
        storageService.store(multipartFile);
        log.info("[ DI-GITAL WEB {} - UPLOAD ] :: store upload file on server", sdf.format(new Date()));

        try {
            if (multipartFile.isEmpty()) {

                log.info("[ DI-GITAL WEB {} - UPLOAD ] :: ERROR = empty file to process", sdf.format(new Date()));

                response = ResponseEntity.status(HttpStatus.CREATED).body(

                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Please select a file to upload")
                );
            }
            else{

                //Recuperartion du profile connecté
                Profile profile = profileService.findOne(profileId);

                if(profile == null){

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(400, "Le profil connecté n'est pas autorisé a effectué cette opération.")

                    );
                }
                else{

                    SmsCredential credential = smsCredentialService.findOne(profile.getGareRoutiere().getCompagnie().getSmsCredential().getId());

                    if(credential != null){

                        Long quotaSms         = null;

                        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                            quotaSms     = credential.getNombreSmsLeTexto();
                        }else{

                            quotaSms      = credential.getNombreSms();
                        }

                        if(quotaSms > 30){

                            PorteFeuilleClient data = null;

                            FileInputStream excelFile = new FileInputStream(new File(new StringBuilder(INTPUT_FOLDER).append(File.separator).append(multipartFile.getOriginalFilename()).toString()));

                            Workbook workbook = new XSSFWorkbook(excelFile);

                            Sheet sheet = workbook.getSheet("Feuil1");

                            Iterator<Row> rows = sheet.iterator();

                            List<PorteFeuilleClient> lists = new ArrayList<PorteFeuilleClient>();

                            int rowNumber = 0;
                            while (rows.hasNext()) {
                                Row currentRow = rows.next();

                                // skip header
                                if (rowNumber == 0) {
                                    rowNumber++;
                                    continue;
                                }
                                Iterator<Cell> cellsInRow = currentRow.iterator();
                                PorteFeuilleClient client = new PorteFeuilleClient();

                                int cellIdx = 0;
                                while (cellsInRow.hasNext()) {

                                    Cell currentCell = cellsInRow.next();

                                    switch (cellIdx) {

                                        case 0:
                                            if(!currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -") || currentCell.getStringCellValue() != null){

                                                client.setTypeClient(typeClientService.findOne(currentCell.getStringCellValue().toUpperCase()));
                                            }
                                            else{
                                                client.setTypeClient(typeClientService.findById(1L));
                                            }
                                            break;

                                        case 1:
                                            if(!currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -") || currentCell.getStringCellValue() != null){

                                                client.setCivilite(civiliteService.findOne(currentCell.getStringCellValue().toUpperCase()));
                                            }
                                            else{
                                                client.setCivilite(civiliteService.findById(1L));
                                            }
                                            break;

                                        case 2:
                                            client.setNomClient(currentCell.getStringCellValue() != null ? currentCell.getStringCellValue().toUpperCase() : "NON-DÉFINI");
                                            break;

                                        case 3:
                                            client.setDateNaissance(currentCell.getDateCellValue() != null ? convertLocalDateToDate(currentCell.getDateCellValue()) : null);
                                        break;

                                        case 4:
                                            client.setContact(currentCell.getStringCellValue());
                                        break;

                                        case 5:
                                            client.setContact2(currentCell.getStringCellValue() == null ? client.getContact() : currentCell.getStringCellValue());

                                        break;

                                        case 6:
                                            if(currentCell.getStringCellValue().equalsIgnoreCase("OUI") && !currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -")){

                                                if(client.getCivilite().getCodeCivilite().equalsIgnoreCase("MONSIEUR")){

                                                    client.setNotifFetePere(currentCell.getStringCellValue());

                                                    client.setNotifFeteMere("NON");
                                                }

                                                if(client.getCivilite().getCodeCivilite().equalsIgnoreCase("MADEMOISELLE") || client.getCivilite().getCodeCivilite().equalsIgnoreCase("MADAME")){

                                                    client.setNotifFeteMere(currentCell.getStringCellValue());

                                                    client.setNotifFetePere("NON");
                                                }
                                            }
                                            else{

                                                client.setNotifFetePere("NON");

                                                client.setNotifFeteMere("NON");
                                            }
                                            break;

                                        case 7:
                                            client.setFete(currentCell.getStringCellValue() == null ? "NON" : currentCell.getStringCellValue().toUpperCase());

                                            if(client.getFete().equalsIgnoreCase("AUCUNE")){

                                                client.setNotifFeteReligieuse("NON");
                                            }
                                            else{
                                                client.setNotifFeteReligieuse("OUI");
                                            }
                                            break;

                                        case 8:
                                            client.setSouscritPrdt(currentCell.getStringCellValue() == null ? "NON" : currentCell.getStringCellValue().toUpperCase());
                                            break;

                                        case 9:
                                            client.setNumeroContrat(Objects.equals(currentCell.getStringCellValue(), "N/A") ? null : currentCell.getStringCellValue());
                                        break;

                                        case 10:
                                            //System.out.println("=====" + currentCell.getStringCellValue());
                                            if(!currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -")){

                                                client.setProduit(currentCell.getStringCellValue().equalsIgnoreCase("NON-APPLICABLE") ? null : produitAssuranceService.findOne(currentCell.getStringCellValue().toUpperCase()));
                                            }
                                            else {
                                                client.setProduit(null);
                                            }
                                            break;

                                        case 11:
                                            client.setExpirationContrat(currentCell.getDateCellValue() != null ? convertLocalDateToDate(currentCell.getDateCellValue()) : null);
                                            break;

                                        case 12:
                                            client.setNotifRappelVisiteTechnique(currentCell.getStringCellValue() == null ? "NON" : currentCell.getStringCellValue().toUpperCase());
                                            break;

                                        case 13:
                                            client.setTypeVehicule(Objects.equals(currentCell.getStringCellValue(), "NON-APPLICABLE") ? "" : currentCell.getStringCellValue().toUpperCase());
                                            break;

                                        case 14:
                                            client.setDateExpirationVisite(currentCell.getDateCellValue() == null ? null : convertLocalDateToDate(currentCell.getDateCellValue()));
                                            break;

                                        case 15:
                                            client.setImmatriculation(currentCell.getStringCellValue() == null ? "" : currentCell.getStringCellValue().toUpperCase());
                                            break;

                                        case 16:
                                            if(!currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -") || currentCell.getStringCellValue() != null){

                                                client.setNotifMsgBienvenu(currentCell.getStringCellValue());
                                            }
                                            else{
                                                client.setNotifMsgBienvenu("NON");
                                            }
                                            break;

                                        case 17:
                                            if(!currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -") || currentCell.getStringCellValue() != null){

                                                client.setNotifSouscription(currentCell.getStringCellValue());
                                            }
                                            else{
                                                client.setNotifSouscription("NON");
                                            }
                                            break;

                                        case 18:
                                            client.setNotifFeteNationale(currentCell.getStringCellValue() == null ? "NON" : "OUI");
                                            break;

                                        case 19:
                                            client.setNotifAnniv(client.getDateNaissance() == null ? "NON" : "OUI");
                                            break;

                                        case 20:
                                            if(!currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -") || currentCell.getStringCellValue() != null){

                                                client.setNotifRappelEcheJ45(currentCell.getStringCellValue());
                                            }
                                            else{
                                                client.setNotifRappelEcheJ45("NON");
                                            }
                                            break;

                                        case 21:
                                            if(!currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -") || currentCell.getStringCellValue() != null){

                                                client.setNotifRappelEcheJ15(currentCell.getStringCellValue());
                                            }
                                            else{
                                                client.setNotifRappelEcheJ15("NON");
                                            }
                                            break;

                                        case 22:

                                            client.setProfile(userService.findOne(currentCell.getStringCellValue()).getProfile());

                                            client.setEntreprises(client.getProfile().getGareRoutiere().getCompagnie());
                                        break;
                                    }
                                    cellIdx++;
                                }
                                lists.add(client);
                            }
                            workbook.close();

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");

                            //RECUPERATION DES DATE DE FETE PROGRAMMEES
                            String dateDepart           = parametrageDateService.findOne("date.nouvel.an").getDateDeb();
                            String dateFeteNationnal    = parametrageDateService.findOne("date.fete.nationnal").getDateDeb();
                            String dateFetePaix         = parametrageDateService.findOne("date.fete.paix").getDateDeb();
                            String dateFetePaque        = parametrageDateService.findOne("date.fete.paques").getDateDeb();
                            String dateFeteAssomption   = parametrageDateService.findOne("date.fete.assomption").getDateDeb();
                            String dateFeteAscension    = parametrageDateService.findOne("date.fete.ascension").getDateDeb();
                            String dateFeteTravail      = parametrageDateService.findOne("date.fete.travail").getDateDeb();
                            String dateFeteNoel         = parametrageDateService.findOne("date.fete.noel").getDateDeb();
                            String dateFeteDestin       = parametrageDateService.findOne("date.fete.destin").getDateDeb();
                            String dateFeteFitr         = parametrageDateService.findOne("date.fete.fitr").getDateDeb();
                            String dateFeteTabaski      = parametrageDateService.findOne("date.fete.tabaski").getDateDeb();
                            String dateFeteMahomet      = parametrageDateService.findOne("date.fete.mahomet").getDateDeb();
                            String dateFeteToussaint    = parametrageDateService.findOne("date.fete.toussaint").getDateDeb();
                            String dateFetePentecote    = parametrageDateService.findOne("date.fete.pentecote").getDateDeb();

                            for (PorteFeuilleClient entity : lists) {

                                log.info("[batch-import-data ok: {}", entity);

                                String number = null;

                                if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                                    number       = "225"+entity.getContact();

                                }else{

                                    number       = entity.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+entity.getContact();
                                }

                                //VERIFION SI LE CLIENT N'A PAS ETE DEJA AJOUTE AU PORTE-FEUILLE
                                if(porteFeuilleClientService.findOne(entity.getNomClient().toUpperCase(), entity.getProduit(), entity.getEntreprises()) == null ) {

                                    String nomClient = WordUtils.capitalize(entity.getNomClient().toLowerCase());

                                    PorteFeuilleClient porteFeuilleClient = new PorteFeuilleClient();

                                    porteFeuilleClient.setNomCapagne("CAMPAGNE SMS DE - "+ entity.getNomClient().toUpperCase());

                                    porteFeuilleClient.setOrdre(Utils.generateRandom(8));

                                    porteFeuilleClient.setCivilite(entity.getCivilite());

                                    porteFeuilleClient.setNomClient(entity.getNomClient().toUpperCase());

                                    porteFeuilleClient.setContact(entity.getContact());

                                    porteFeuilleClient.setNumeroContrat(entity.getNumeroContrat() == null ? "" : entity.getNumeroContrat().toUpperCase());

                                    porteFeuilleClient.setFete(entity.getFete());

                                    porteFeuilleClient.setDateNaissance(entity.getDateNaissance().toString().equalsIgnoreCase("01/01/2000") ? null : entity.getDateNaissance());

                                    porteFeuilleClient.setTypeClient(entity.getTypeClient());

                                    porteFeuilleClient.setProduit(entity.getProduit());

                                    porteFeuilleClient.setProfile(entity.getProfile());

                                    porteFeuilleClient.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                                    porteFeuilleClient.setNotifMsgBienvenu(entity.getNotifMsgBienvenu());

                                    porteFeuilleClient.setNotifSouscription(entity.getSouscritPrdt());

                                    porteFeuilleClient.setSouscritPrdt(entity.getSouscritPrdt());

                                    porteFeuilleClient.setProduit(entity.getProduit());

                                    porteFeuilleClient.setNotifAnniv(entity.getNotifAnniv());

                                    if(entity.getDateNaissance() != null && entity.getNotifAnniv() != null && entity.getNotifAnniv().equalsIgnoreCase("OUI")){

                                        String birthDay = entity.getDateNaissance().toString().split("-")[2]+"/"+entity.getDateNaissance().toString().split("-")[1];

                                        if(birthdayService.findeOne(birthDay, entity.getNomClient(), ""+ Calendar.YEAR) == null){

                                            Birthday birthday = new Birthday();

                                            birthday.setBirthday(entity.getDateNaissance().toString().split("-")[2]+"/"+entity.getDateNaissance().toString().split("-")[1]);

                                            birthday.setAnniversaireux(nomClient);

                                            birthday.setAnnee(""+Calendar.YEAR);

                                            birthday.setEnvoi(false);

                                            birthdayService.create(birthday);
                                        }
                                    }

                                    porteFeuilleClient.setNotifRappelEcheJ45(entity.getNotifRappelEcheJ45());

                                    porteFeuilleClient.setNotifRappelEcheJ15(entity.getNotifRappelEcheJ15());

                                    porteFeuilleClient.setNotifFeteNationale(entity.getNotifFeteNationale());

                                    porteFeuilleClient.setNotifRappelVisiteTechnique(entity.getNotifRappelVisiteTechnique());

                                    porteFeuilleClient.setImmatriculation(entity.getImmatriculation() == null ? "" : entity.getImmatriculation().toUpperCase());

                                    porteFeuilleClient.setDateExpirationVisite(entity.getDateExpirationVisite());

                                    porteFeuilleClient.setTypeVehicule(entity.getTypeVehicule());

                                    porteFeuilleClient.setExpirationContrat(entity.getExpirationContrat());

                                    porteFeuilleClient.setNotifFetePere(entity.getNotifFetePere());

                                    porteFeuilleClient.setNotifFeteMere(entity.getNotifFeteMere());

                                    porteFeuilleClient.setContact2(entity.getContact2() == null ? "" : entity.getContact2());

                                    if(entity.getFete().equalsIgnoreCase("CATHOLIQUE") || entity.getFete().equalsIgnoreCase("MUSULMANE")){

                                        porteFeuilleClient.setNotifFeteReligieuse("OUI");
                                    }

                                    if(entity.getFete().equalsIgnoreCase("AUCUNE")){

                                        porteFeuilleClient.setNotifFeteReligieuse("NON");

                                    }

                                    PorteFeuilleClient client = porteFeuilleClientService.create(porteFeuilleClient);

                                    if (client.getId() == null) {

                                        response = ResponseEntity.status(HttpStatus.CREATED).body(

                                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                                        );
                                    }
                                    else {

                                        //ENVOI DU MESSAGE DE BIENVENU A LA JMS SI CELA EST CONFIGURE
                                        sendWelComMessage(credential, client, number);


                                        sendMsgThenToSubscribeProduct(client, number, credential);

                                        //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Avis d’échéance à J -45 de son contrat
                                        saveNotifAvisMois45(client, number, credential);


                                        //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Avis d’échéance à J -15 de son contrat
                                        saveNotifAvisMois15(credential, client, number);

                                        //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification lors des fêtes nationales
                                        saveNotificationFeteNationnale(client, number, formatter, dateDepart, dateFeteNationnal, dateFetePaix, dateFeteTravail, credential);


                                       //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification lors des fêtes CATHOLIQUES
                                        saveNotifFeteCatholique(formatter, dateFetePaque, dateFeteAssomption, dateFeteAscension, dateFeteNoel, dateFeteToussaint, dateFetePentecote, client, number, credential);


                                        //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification lors des fêtes MUSULMANES
                                        saveNotifFeteMusulmane(client, number, formatter, dateFeteDestin, dateFeteFitr, dateFeteTabaski, dateFeteMahomet, credential);


                                        //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification expiration de sa visite technique
                                        saveNotifExpirationVisiteTechnique(client, number, credential);


                                        //CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification anniversaire
                                        saveBithDayNotification(client, number, credential);


                                        //Enregistrement de la notification
                                        saveNotification("Enregistrement du client ", client.getNomClient().toUpperCase(), " dans votre porte-feuille client le ", ".", client, TypeNotification.Add_Client);
                                    }
                                }
                            }
                            System.gc();


                            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                            //Move file
                            Path sourcePath         = Paths.get(INTPUT_FOLDER+"//"+multipartFile.getOriginalFilename());

                            Path destinationPath    = Paths.get(OUTPUT_FOLDER+timeStamp+"-archived-"+multipartFile.getOriginalFilename());

                            try{

                                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                                response = ResponseEntity.status(HttpStatus.CREATED).body(

                                        new RequestInformation(201, "Importation effectué avec succès et déplacement du fichier source effectué avec succès.")
                                );

                            } catch (Exception exception) {
                                //this is where the error will be thrown if the file did not move properly
                                //(null pointer etc...), you can place code here to run if there is an error
                                response = ResponseEntity.status(HttpStatus.CREATED).body(

                                        new RequestInformation(201, "Importation effectué avec succès mais sans déplacer le fichier source")
                                );
                            }
                        }
                        else{

                            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                            //Move file
                            Path sourcePath = Paths.get(INTPUT_FOLDER+"//"+multipartFile.getOriginalFilename());

                            Path destinationPath = Paths.get(OUTPUT_FOLDER+timeStamp+"-failed-"+multipartFile.getOriginalFilename());

                            try {

                                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                        new RequestInformation(400, "Votre quota minimum d'sms pour la programmation est de 30 sms. Nous prions d'effectuer au rechargement de votre compte.")

                                );
                            }
                            catch (Exception exception) {

                                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                        new RequestInformation(605, "Votre quota minimum d'sms pour la programmation est de 30 sms. Nous prions d'effectuer au rechargement de votre compte.")

                                );
                            }
                        }
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestInformation(400, "Votre quota d'sms ne vous permet pas d'avoir accès à la plate-forme.")

                        );
                    }
                }
            }
        }
        catch (Exception exception) {

            exception.printStackTrace();

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

        finally {

            return response;
        }
    }



    private void saveNotifAvisMois45(PorteFeuilleClient entity, String number, SmsCredential credential) throws Exception {
        if(entity.getNotifRappelEcheJ45().equalsIgnoreCase("OUI")
                && entity.getProduit() != null && entity.getNumeroContrat() != null && !number.isEmpty() && number != null){

            String message = null;

            String dateExpiration = entity.getExpirationContrat().toString().split("-")[2]+"/"+ entity.getExpirationContrat().toString().split("-")[1]+"/"+ entity.getExpirationContrat().toString().split("-")[0];

            ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(16L), entity.getProfile().getGareRoutiere().getCompagnie(), "AVIS D’ÉCHÉANCE À J -45");

            if(modelMessage != null){

                message = modelMessage.getMessage()

                        .replace("[produit]", WordUtils.capitalize(entity.getProduit().getProduit().toLowerCase()))

                        .replace("[numero]", entity.getNumeroContrat().toUpperCase())

                        .replace("[dateExpiration]", dateExpiration);

                SendMessage sendMessage = new SendMessage();

                sendMessage.setDateEnvoi(entity.getExpirationContrat().minusDays(45));

                sendMessage.setNumeroContact(number);

                sendMessage.setMessage(message);

                sendMessage.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());

                sendMessage.setStatut(EtatLecture.PENDING);

                sendMessage.setPorteFeuilleClient(entity);

                sendMessage.setProfile(profileService.findOne(entity.getProfile().getIdDigital()));

                double dtSms = Math.ceil(sendMessage.getMessage().length()/160);

                sendMessage.setNbrePage(dtSms == 0 ? 1 : dtSms);

                sendMessageService.create(sendMessage);


                //MISE A JOUR DU QUOTA SMS
                if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                    credential.setNombreSmsLeTexto(credential.getNombreSmsLeTexto() - 1);
                }
                else{

                    credential.setNombreSms(credential.getNombreSms() - 1);
                }

                smsCredentialService.update(credential);
            }
        }
    }


    private void saveNotificationFeteNationnale(PorteFeuilleClient entity, String number, DateTimeFormatter formatter,
                                                String dateDepart, String dateFeteNationnal, String dateFetePaix,
                                                String dateFeteTravail, SmsCredential credential) throws Exception {

        if(entity.getNotifFeteNationale().equalsIgnoreCase("OUI") && entity.getNumeroContrat() != null && number != null){

            int nbrPage = 0;

            for (int i = 0; i < 4; i++) {

                String message = null;

                String entreprise = WordUtils.capitalize(entity.getProfile().getGareRoutiere().getCompagnie().getCompagnie().toLowerCase());

                SendMessage sendMessage = new SendMessage();

                if(i== 0 && dateDepart != null){

                    LocalDate date = LocalDate.parse(dateDepart, formatter);

                    LocalDate localDate = date.plusYears(1);

                    //JOUR DE L'AN
                    ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(18L), entity.getProfile().getGareRoutiere().getCompagnie(), "NOUVEL AN");

                    SloganEntreprise sloganEntreprise = sloganEntrepriseService.findOne(entity.getProfile().getGareRoutiere().getCompagnie());

                    if(modelMessage != null && sloganEntreprise != null){

                        nbrPage = modelMessage.getNbrePage();

                        message = modelMessage.getMessage().replace("[nomEntreprise]", entreprise)+".\n"+sloganEntreprise.getSlogan();

                        sendMessage.setDateEnvoi(localDate);

                        sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());
                    }
                }


                ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(22L), entity.getProfile().getGareRoutiere().getCompagnie(), "FÊTE CIVILE ET RELIGIEUSE");

                if(i== 1 && dateFeteTravail != null){

                    LocalDate localDate = LocalDate.parse(dateFeteTravail, formatter).plusYears(1);

                    //FÊTE DU TRAVAIL
                    if(modelMessage != null){

                        nbrPage = modelMessage.getNbrePage();

                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "Fete du Travail");

                        sendMessage.setDateEnvoi(localDate);

                        sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());
                    }
                }

                if(i== 2 && dateFeteNationnal != null){

                    LocalDate localDate = LocalDate.parse(dateFeteNationnal, formatter).plusYears(1);

                    if(modelMessage != null){

                        nbrPage = modelMessage.getNbrePage();

                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "Fete Nationale");

                        sendMessage.setDateEnvoi(localDate);

                        sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());
                    }
                }

                if(i == 3 && dateFetePaix != null){

                    LocalDate localDate = LocalDate.parse(dateFetePaix, formatter).plusYears(1);

                    //JOURNÉE DE LA PAIX
                    if(modelMessage != null){

                        nbrPage = modelMessage.getNbrePage();

                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "Journée de la Paix");

                        sendMessage.setDateEnvoi(localDate);

                        sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());
                    }
                }

                sendMessage.setNumeroContact(number);

                sendMessage.setMessage(message);

                sendMessage.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                sendMessage.setStatut(EtatLecture.PENDING);

                sendMessage.setPorteFeuilleClient(entity);

                double dtSms = Math.ceil(sendMessage.getMessage().length()/160);

                sendMessage.setNbrePage(dtSms == 0 ? 1 : dtSms);

                sendMessage.setProfile(profileService.findOne(entity.getProfile().getIdDigital()));

                sendMessageService.create(sendMessage);



                //MISE A JOUR DU QUOTA SMS
                if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                    credential.setNombreSmsLeTexto(credential.getNombreSmsLeTexto() - nbrPage);
                }
                else{

                    credential.setNombreSms(credential.getNombreSms() - nbrPage);
                }

                smsCredentialService.update(credential);
            }
        }
    }

    private void saveNotifFeteMusulmane(PorteFeuilleClient entity, String number, DateTimeFormatter formatter,
                                        String dateFeteDestin, String dateFeteFitr, String dateFeteTabaski,
                                        String dateFeteMahomet, SmsCredential credential) {

        if(entity.getFete().equalsIgnoreCase("MUSULMANE") && entity.getNumeroContrat() != null && number != null){

            String entreprise = WordUtils.capitalize(entity.getProfile().getGareRoutiere().getCompagnie().getCompagnie().toLowerCase());

            ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(22L), entity.getProfile().getGareRoutiere().getCompagnie(), "FÊTE CIVILE ET RELIGIEUSE");

            if(modelMessage != null && !entreprise.isEmpty()){

                for (int i = 0; i < 4; i++) {

                    String message = null;

                    SendMessage sendMessage = new SendMessage();

                    if(i == 0 && dateFeteDestin != null) {

                        //Le lendemain de la nuit du destin
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "Fete de la nuit du destin");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFeteDestin, formatter).plusYears(1));
                    }

                    if(i == 1 && dateFeteFitr != null) {

                        //Fete de L'Aid al-Fitr
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "Fete de L'Aid al-Fitr");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFeteFitr, formatter).plusYears(1));
                    }

                    if(i == 2 && dateFeteTabaski != null) {

                        //Fête de la Tabaski
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "Fete de la Tabaski");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFeteTabaski, formatter).plusYears(1));
                    }

                    if(i == 3 && dateFeteMahomet != null) {

                        //Le lendemain de la naissance du Prophète Mahomet
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "Fete de la naissance du Prophète Mahomet");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFeteMahomet, formatter).plusYears(1));
                    }

                    sendMessage.setNumeroContact(number);

                    sendMessage.setMessage(message);

                    sendMessage.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                    sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());

                    sendMessage.setStatut(EtatLecture.PENDING);

                    sendMessage.setPorteFeuilleClient(entity);

                    double dtSms = Math.ceil(sendMessage.getMessage().length()/160);

                    sendMessage.setNbrePage(dtSms == 0 ? 1 : dtSms);

                    sendMessage.setProfile(profileService.findOne(entity.getProfile().getIdDigital()));

                    sendMessageService.create(sendMessage);

                    //MISE A JOUR DU QUOTA SMS
                    if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                        credential.setNombreSmsLeTexto((long) (credential.getNombreSmsLeTexto() - sendMessage.getNbrePage()));
                    }
                    else{

                        credential.setNombreSms((long) (credential.getNombreSms() - sendMessage.getNbrePage()));
                    }

                    smsCredentialService.updated(credential);
                }
            }
        }
    }

    private void saveBithDayNotification(PorteFeuilleClient entity, String number, SmsCredential credential) {

        System.out.println("CHARGEMENT DES MODELES DE MESSAGE POUR LE BATCH   --> Une notification anniversaire");

        if(entity.getDateNaissance() != null && entity.getNumeroContrat() != null && number != null){

            ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(19L), entity.getProfile().getGareRoutiere().getCompagnie(), "ANNIVERSAIRE");

            SloganEntreprise sloganEntreprise = sloganEntrepriseService.findOne(entity.getProfile().getGareRoutiere().getCompagnie());

            /*
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            int year = cal.get(Calendar.YEAR);

            System.out.println("1 ===============" + year);
            System.out.println("2 ===============" + y);

            Date date = new Date();
            ZoneId timeZone = ZoneId.systemDefault();
            LocalDate getLocalDate = date.toInstant().atZone(timeZone).toLocalDate();
            System.out.println(getLocalDate.getYear());*/

            if(modelMessage != null){

                LocalDate currentDate = LocalDate.now();
                int year = currentDate.getYear();

                String message = modelMessage.getMessage()

                        .replace("[nomEntreprise]", entity.getProfile().getGareRoutiere().getCompagnie().getCompagnie())

                        .replace("[slogan]", sloganEntreprise.getSlogan());

                SendMessage sendMessage = new SendMessage();

                sendMessage.setNumeroContact(number);

                sendMessage.setDateEnvoi(entity.getDateNaissance().withYear(year));

                sendMessage.setMessage(message);

                sendMessage.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                sendMessage.setStatut(EtatLecture.PENDING);

                sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());

                sendMessage.setPorteFeuilleClient(entity);

                double dtSms = Math.ceil(sendMessage.getMessage().length()/160);

                sendMessage.setNbrePage(dtSms == 0 ? 1 : dtSms);

                sendMessage.setProfile(profileService.findOne(entity.getProfile().getIdDigital()));

                sendMessageService.create(sendMessage);

                //MISE A JOUR DU QUOTA SMS
                if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                    credential.setNombreSmsLeTexto((long) (credential.getNombreSmsLeTexto() - sendMessage.getNbrePage()));
                }
                else{

                    credential.setNombreSms((long) (credential.getNombreSms() - sendMessage.getNbrePage()));
                }

                smsCredentialService.updated(credential);
            }
        }
    }


    private void sendWelComMessage(SmsCredential credential, PorteFeuilleClient entity, String number) throws Exception {

        //System.out.println("==========> ENVOI DU MESSAGE DE BIENVENU A LA JMS SI CELA EST CONFIGURE");
        if(entity.getNotifMsgBienvenu() != null && entity.getNotifMsgBienvenu().equalsIgnoreCase("OUI")){

            String nomCplet = entity.getCivilite().getCodeCivilite() + " "+ entity.getNomClient();

            String promReceiver = WordUtils.capitalize(nomCplet.toLowerCase());

            if (number != null && !number.isEmpty() && !promReceiver.isEmpty()){

                //RECUPERATION DU MESSAGE PARAMETRE
                ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(13L), entity.getProfile().getGareRoutiere().getCompagnie(), "BIENVENUE");

                SloganEntreprise sloganEntreprise = sloganEntrepriseService.findOne(entity.getProfile().getGareRoutiere().getCompagnie());

                if(modelMessage != null && sloganEntreprise != null){

                    String message = "Bonjour "+ promReceiver +", "+modelMessage.getMessage().replace("[nomEntreprise]", WordUtils.capitalize(entity.getProfile().getGareRoutiere().getCompagnie().getCompagnie().toLowerCase()))+"\n"+sloganEntreprise.getSlogan()+"";

                    SmsMessage sms = new SmsMessage();

                    sms.setTypeMessage(1L);

                    sms.setToId(number);

                    sms.setContent(message);

                    sms.setFromName(entity.getNomClient().toUpperCase());

                    sms.setUsername(entity.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                    sms.setPassword(entity.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                    sms.setSenderId(entity.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                    jmsProducer.send(new JmsMessage("Envoi du message de bienvenu", Converter.pojoToJson(sms), SmsMessage.class));


                    //MISE A JOUR DU QUOTA SMS
                    if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                        credential.setNombreSmsLeTexto(credential.getNombreSmsLeTexto() - 2);
                    }
                    else{

                        credential.setNombreSms(credential.getNombreSms() - 2);
                    }

                    smsCredentialService.updated(credential);

                    //Enregistrement de la notification
                    saveNotification("L'envoi du message de bienvenu au client ", promReceiver, " à la date du ", " a été effectué avec succès.", entity, TypeNotification.Connexion);
                }
            }
        }
    }


    private void saveNotifAvisMois15(SmsCredential credential, PorteFeuilleClient entity, String number) throws Exception {

        if(entity.getNotifRappelEcheJ15().equalsIgnoreCase("OUI")
                && entity.getProduit() != null && entity.getNumeroContrat() != null && number != null){

            String message = null;

            String dateExpiration = entity.getExpirationContrat().toString().split("-")[2]+"/"+ entity.getExpirationContrat().toString().split("-")[1]+"/"+ entity.getExpirationContrat().toString().split("-")[0];

            ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(17L), entity.getProfile().getGareRoutiere().getCompagnie(), "AVIS D’ÉCHÉANCE À J -15");

            if(modelMessage != null){

                message = modelMessage.getMessage()

                        .replace("[produit]", WordUtils.capitalize(entity.getProduit().getProduit().toLowerCase()))

                        .replace("[numero]", entity.getNumeroContrat().toUpperCase())

                        .replace("[dateExpiration]", dateExpiration);

                SendMessage sendMessage = new SendMessage();

                sendMessage.setDateEnvoi(entity.getExpirationContrat().minusDays(15));

                sendMessage.setNumeroContact(number);

                sendMessage.setMessage(message);

                sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());

                sendMessage.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                sendMessage.setStatut(EtatLecture.PENDING);

                sendMessage.setPorteFeuilleClient(entity);

                double dtSms = Math.ceil(sendMessage.getMessage().length()/160);

                sendMessage.setNbrePage(dtSms == 0 ? 1 : dtSms);

                sendMessage.setProfile(profileService.findOne(entity.getProfile().getIdDigital()));

                sendMessageService.create(sendMessage);

                //MISE A JOUR DU QUOTA SMS
                if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                    credential.setNombreSmsLeTexto((long) (credential.getNombreSmsLeTexto() - sendMessage.getNbrePage()));
                }
                else{

                    credential.setNombreSms((long) (credential.getNombreSms() - sendMessage.getNbrePage()));
                }

                smsCredentialService.updated(credential);
            }
        }
    }


    private void saveNotifFeteCatholique(DateTimeFormatter formatter, String dateFetePaque, String dateFeteAssomption,
                                         String dateFeteAscension, String dateFeteNoel, String dateFeteToussaint,
                                         String dateFetePentecote, PorteFeuilleClient entity, String number, SmsCredential credential) {

        if(entity.getFete().equalsIgnoreCase("CATHOLIQUE") && entity.getNumeroContrat() != null && number != null){

            ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(22L), entity.getProfile().getGareRoutiere().getCompagnie(), "FÊTE CIVILE ET RELIGIEUSE");

            String entreprise = WordUtils.capitalize(entity.getProfile().getGareRoutiere().getCompagnie().getCompagnie().toLowerCase());

            if(modelMessage != null && !entreprise.isEmpty()){


                for (int i = 0; i < 6; i++) {

                    String message = null;

                    SendMessage sendMessage = new SendMessage();

                    if(i== 0 && dateFetePaque != null) {

                        //PAQUES
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "la Paques");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFetePaque, formatter).plusYears(1));
                    }

                    if(i == 1) {

                        //Ascension
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "l'Ascension");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFeteAscension, formatter).plusYears(1));
                    }

                    if(i == 2 && dateFetePentecote != null) {

                        //Pentecôte
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "la Pentecote");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFetePentecote, formatter).plusYears(1));
                    }

                    if(i == 3 && dateFeteAssomption != null) {

                        //Assomption
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "l'Assomption");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFeteAssomption, formatter).plusYears(1));
                    }

                    if(i == 4 && dateFeteToussaint != null) {

                        //Fête de la Toussaint
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "la Toussaint");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFeteToussaint, formatter).plusYears(1));
                    }

                    if(i == 5 && dateFeteNoel != null) {

                        //Fête de Noël
                        message = modelMessage.getMessage()

                                .replace("[nomEntreprise]", entreprise)

                                .replace("[nomFete]", "la Noel");

                        sendMessage.setDateEnvoi(LocalDate.parse(dateFeteNoel, formatter).plusYears(1));
                    }

                    sendMessage.setNumeroContact(number);

                    sendMessage.setMessage(message);

                    sendMessage.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                    sendMessage.setStatut(EtatLecture.PENDING);

                    sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());

                    sendMessage.setPorteFeuilleClient(entity);

                    double dtSms = Math.ceil(sendMessage.getMessage().length()/160);

                    sendMessage.setNbrePage(dtSms == 0 ? 1 : dtSms);

                    sendMessage.setProfile(profileService.findOne(entity.getProfile().getIdDigital()));

                    sendMessageService.create(sendMessage);

                    //MISE A JOUR DU QUOTA SMS
                    if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                        credential.setNombreSmsLeTexto((long) (credential.getNombreSmsLeTexto() - sendMessage.getNbrePage()));
                    }
                    else{

                        credential.setNombreSms((long) (credential.getNombreSms() - sendMessage.getNbrePage()));
                    }

                    smsCredentialService.updated(credential);
                }
            }
        }
    }

    private void saveNotifExpirationVisiteTechnique(PorteFeuilleClient entity, String number, SmsCredential credential) {

        if(entity.getNotifRappelVisiteTechnique().equalsIgnoreCase("OUI")
                && entity.getNumeroContrat() != null && number != null && entity.getImmatriculation() != null){

            ModelMessage modelMessage = modelMessageService.findOne(typeMessageService.findById(25L), entity.getProfile().getGareRoutiere().getCompagnie(), "VISITE TECHNIQUE");

            System.out.println("BATCH   ------- ------------- ------> " + modelMessage);

            String dateExpirationVisite = entity.getDateExpirationVisite().toString().split("-")[2]+"/"+ entity.getDateExpirationVisite().toString().split("-")[1]+"/"+ entity.getDateExpirationVisite().toString().split("-")[0];

            SloganEntreprise sloganEntreprise = sloganEntrepriseService.findOne(entity.getProfile().getGareRoutiere().getCompagnie());

            if(modelMessage != null && sloganEntreprise != null){

                String message = modelMessage.getMessage()

                        .replace("[immatriculation]", entity.getImmatriculation().toUpperCase())

                        .replace("[dateExpirationVisite]", dateExpirationVisite)

                        .replace("[slogan]", sloganEntreprise.getSlogan());

                SendMessage sendMessage = new SendMessage();

                sendMessage.setNumeroContact(number);

                sendMessage.setDateEnvoi(entity.getDateExpirationVisite().minusDays(3));

                sendMessage.setMessage(message);

                sendMessage.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                sendMessage.setTypeMessage(modelMessage.getTypeMessage().getTypemessgae());

                sendMessage.setStatut(EtatLecture.PENDING);

                sendMessage.setPorteFeuilleClient(entity);

                sendMessage.setPorteFeuilleClient(entity);

                double dtSms = Math.ceil(sendMessage.getMessage().length()/160);

                sendMessage.setNbrePage(dtSms == 0 ? 1 : dtSms);

                sendMessage.setProfile(profileService.findOne(entity.getProfile().getIdDigital()));

                sendMessageService.create(sendMessage);

                //MISE A JOUR DU QUOTA SMS
                if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                    credential.setNombreSmsLeTexto((long) (credential.getNombreSmsLeTexto() - sendMessage.getNbrePage()));
                }
                else{

                    credential.setNombreSms((long) (credential.getNombreSms() - sendMessage.getNbrePage()));
                }

                smsCredentialService.updated(credential);
            }
        }
    }



    private void saveNotification(String x, String entity, String x1, String x2, PorteFeuilleClient entity1, TypeNotification add_Client) throws Exception {

        NotificationSysteme notification = new NotificationSysteme();

        notification.setNotification(x + entity + x1 +datStr.format(new Date())+" à "+heure.format(new Date())+ x2);

        notification.setProfile(entity1.getProfile());

        notification.setReference(String.valueOf(Utils.generateRandom(6)));

        notification.setType(add_Client);

        notificationService.create(notification);
    }


    public static LocalDate convertLocalDateToDate (Date date) {
        return date.toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDate();
    }

    /******
     * @API Parametrage Dates
     ****/
    @GetMapping(value = "/find-all-date-de-notification", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllParametrageDates() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(parametrageDateService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-date-de-notification", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewParametrageDates (@RequestBody ParametrageDates model) {

        try {

            ParametrageDates data = null;

            if(model == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(parametrageDateService.findOne(model.getDateDeb(), model.getTypeDateDeb()) == null ) {

                    data = new ParametrageDates();

                    data.setDateDeb(model.getDateDeb());

                    data.setTypeDateDeb(model.getTypeDateDeb().toLowerCase());

                    if (parametrageDateService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.CREATED).body(

                            new RequestResponse(
                                new RequestInformation(201, "Successfully created data"),
                                new RequestMessage(null, null)
                            )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Un slogan a été déjà parametré pour le compte de l'entreprise"),

                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/update-date-de-notification", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ParametrageDates.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteParametrageDates(@RequestBody ParametrageDates item) {

        try {

            if(item == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                ParametrageDates up_data = parametrageDateService.findById(item.getId());

                if(up_data!= null) {

                    up_data.setDateDeb(item.getDateDeb());

                    up_data.setTypeDateDeb(item.getTypeDateDeb().toUpperCase());

                    parametrageDateService.update(up_data);

                    if (up_data.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {


                        return ResponseEntity.status(HttpStatus.OK).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully updated data"),
                                        new RequestMessage(null, "Success")
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_GATEWAY.value(), "Données non existante"),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/delete-item-date-de-notification", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ParametrageDates.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteParametrageDates(@RequestBody ParametrageDates object) {

        try {
            if(object != null){

                ParametrageDates item = parametrageDateService.findById(object.getId());

                if(item!= null) {

                    parametrageDateService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {
                        throw new Exception("Impossible de désactiver la donnée");
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
                            new RequestInformation(400, "Impossible de faire la suppression"),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression"),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping(value = "/active-item-date-de-notification", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "ParametrageDates model", notes = "ParametrageDates model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ParametrageDates.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeParametrageDates(@RequestBody ParametrageDates object) {

        try {
            if(object != null){

                ParametrageDates item = parametrageDateService.findById(object.getId());

                if(item!= null) {

                    parametrageDateService.actionRequest(item.getId(), true);

                    ParametrageDates up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation"),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation"),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-date-de-notification", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllParametrageDates() {

        try {

            List<ParametrageDates> items = parametrageDateService.findAll();

            for (ParametrageDates item :items){

                ParametrageDates models = parametrageDateService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    parametrageDateService.update(models);
                }
            }

            System.gc();

            if(parametrageDateService.findAll().isEmpty()) {

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
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /******
     * @API Parametrage Slogan
     ****/
    @GetMapping(value = "/find-all-slogan-entreprise", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllSloganEntreprise() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(sloganEntrepriseService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-slogan-entreprise", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewSloganEntreprise (@RequestBody SloganEntreprise model) {

        try {
            SloganEntreprise data = null;

            if(model == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(sloganEntrepriseService.findOne(model.getEntreprises()) == null ) {

                    data = new SloganEntreprise();

                    data.setSlogan(model.getSlogan());

                    data.setEntreprises(model.getEntreprises());

                    SloganEntreprise _ag_ = sloganEntrepriseService.create(data);

                    if (_ag_.getId() == null) {
                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        //Mise à Jour dans la table entreprise
                        Entreprises entreprises = entrepriseService.findOne(model.getEntreprises().getId());

                        if(entreprises != null){

                            entreprises.setSlogan(model.getSlogan());

                            entrepriseService.update(entreprises);
                        }

                        return ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Un slogan a été déjà parametré pour le compte de l'entreprise "+model.getEntreprises().getCompagnie()+""),

                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/update-slogan-entreprise", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = SloganEntreprise.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteSloganEntreprise(@RequestBody SloganEntreprise item) {

        try {

            if(item == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                SloganEntreprise up_data = sloganEntrepriseService.findOne(item.getEntreprises());

                if(up_data!= null) {

                    up_data.setEntreprises(item.getEntreprises());

                    up_data.setSlogan(item.getSlogan());

                    sloganEntrepriseService.update(up_data);

                    if (up_data.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {

                        Entreprises entreprises = entrepriseService.findOne(item.getEntreprises().getId());

                        if(entreprises != null){

                            entreprises.setSlogan(item.getSlogan());

                            entrepriseService.update(entreprises);
                        }

                        return ResponseEntity.status(HttpStatus.OK).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully updated data"),
                                        new RequestMessage(null, "Success")
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_GATEWAY.value(), "Données non existante"),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/delete-item-slogan-entreprise", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = SloganEntreprise.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteSloganEntreprise(@RequestBody SloganEntreprise object) {

        try {
            if(object != null){

                SloganEntreprise item = sloganEntrepriseService.findOne(object.getEntreprises());

                if(item!= null) {

                    sloganEntrepriseService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {
                        throw new Exception("Impossible de désactiver la donnée");
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
                            new RequestInformation(400, "Impossible de faire la suppression du slogan pour le compte de l'entreprise "+object.getEntreprises().getCompagnie()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du slogan pour le compte de l'entreprise "+object.getEntreprises().getCompagnie()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-slogan-entreprise", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "SloganEntreprise model", notes = "SloganEntreprise model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = SloganEntreprise.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeSloganEntreprise(@RequestBody SloganEntreprise object) {

        try {
            if(object != null){

                SloganEntreprise item = sloganEntrepriseService.findOne(object.getEntreprises());

                if(item!= null) {

                    sloganEntrepriseService.actionRequest(item.getId(), true);

                    SloganEntreprise up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation du slogan pour le compte de l'entreprise "+object.getEntreprises().getCompagnie()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du slogan pour le compte de l'entreprise "+object.getEntreprises().getCompagnie()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-slogan-entreprise", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllSloganEntreprise() {

        try {

            List<SloganEntreprise> items = sloganEntrepriseService.findAll();

            for (SloganEntreprise item :items){

                SloganEntreprise models = sloganEntrepriseService.findOne(item.getEntreprises());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    sloganEntrepriseService.update(models);
                }
            }

            System.gc();

            if(sloganEntrepriseService.findAll().isEmpty()) {

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
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    /******
     * @API Parametrage Type Client Assurance
     ****/
    @GetMapping(value = "/find-all-type-client-assurance", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeClient() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeClientService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-type-client-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewTypeClient (@RequestBody TypeClient model) {

        try {
            TypeClient data = null;

            if(model == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(typeClientService.findOne(model.getType().toUpperCase()) == null ) {

                    data = new TypeClient();

                    data.setType(model.getType().toUpperCase());

                    TypeClient _ag_ = typeClientService.create(data);

                    if (_ag_.getId() == null) {
                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/update-type-client-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = TypeClient.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteTypeClient(@RequestBody TypeClient item) {

        try {

            if(item == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                TypeClient up_data = typeClientService.findById(item.getId());

                if(up_data!= null) {

                    up_data.setType(item.getType().toUpperCase());

                    typeClientService.update(up_data);

                    if (up_data.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.OK).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully updated data"),
                                        new RequestMessage(null, "Success")
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_GATEWAY.value(), "Données non existante"),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/delete-item-type-client-assurance", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeClient.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteTypeClient(@RequestBody TypeClient object) {

        try {
            if(object != null){

                TypeClient item = typeClientService.findById(object.getId());

                if(item!= null) {

                    typeClientService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {
                        throw new Exception("Impossible de désactiver la donnée");
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
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getType().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getType().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-type-client-assurance", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypeClient model", notes = "TypeClient model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeClient.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeTypeClient(@RequestBody TypeClient object) {

        try {
            if(object != null){

                TypeClient item = typeClientService.findById(object.getId());

                if(item!= null) {

                    typeClientService.actionRequest(item.getId(), true);

                    if (item.getId() == null) {

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getType().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getType().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-type-client-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllTypeClient() {

        try {

            List<TypeClient> items = typeClientService.findAll();

            for (TypeClient item :items){

                TypeClient models = typeClientService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    typeClientService.update(models);
                }
            }

            System.gc();

            if(typeClientService.findAll().isEmpty()) {

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
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    /******
     * @API Parametrage Produit Assurance
     ****/
    @GetMapping(value = "/find-all-produit-assurance", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllProduitAssurance() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(produitAssuranceService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-produit-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewProduitAssurance (@RequestBody ProduitAssurance model) {

        try {
            ProduitAssurance data = null;

            if(model == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(produitAssuranceService.findOne(model.getType().toUpperCase(), model.getProduit().toUpperCase()) == null ) {

                    data = new ProduitAssurance();

                    data.setType(model.getType().toUpperCase());

                    data.setProduit(model.getProduit().toUpperCase());

                    ProduitAssurance _ag_ = produitAssuranceService.create(data);

                    if (_ag_.getId() == null) {
                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Un produit a été déjà parametré pour le compte de l'entreprise"),

                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/update-produit-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ProduitAssurance.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteProduitAssurance(@RequestBody ProduitAssurance item) {

        try {

            if(item == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                ProduitAssurance up_data = produitAssuranceService.findById(item.getId());

                if(up_data!= null) {

                    up_data.setType(item.getType().toUpperCase());

                    up_data.setProduit(item.getProduit().toUpperCase());

                    produitAssuranceService.update(up_data);

                    if (up_data.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.OK).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully updated data"),
                                        new RequestMessage(null, "Success")
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_GATEWAY.value(), "Données non existante"),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/delete-item-produit-assurance", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ProduitAssurance.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteProduitAssurance(@RequestBody ProduitAssurance object) {

        try {
            if(object != null){

                ProduitAssurance item = produitAssuranceService.findOne(object.getType().toUpperCase(), object.getProduit().toUpperCase());

                if(item!= null) {

                    produitAssuranceService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {
                        throw new Exception("Impossible de désactiver la donnée");
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
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getProduit().toUpperCase()+" du type "+object.getType().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getProduit().toUpperCase()+" du type "+object.getType().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-produit-assurance", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "ProduitAssurance model", notes = "ProduitAssurance model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ProduitAssurance.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeProduitAssurance(@RequestBody ProduitAssurance object) {

        try {
            if(object != null){

                ProduitAssurance item = produitAssuranceService.findOne(object.getType().toUpperCase(), object.getProduit());

                if(item!= null) {

                    produitAssuranceService.actionRequest(item.getId(), true);

                    ProduitAssurance up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getProduit().toUpperCase()+" du type "+object.getType().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getProduit().toUpperCase()+" du type "+object.getType().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-produit-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllProduitAssurance() {

        try {

            List<ProduitAssurance> items = produitAssuranceService.findAll();

            for (ProduitAssurance item :items){

                ProduitAssurance models = produitAssuranceService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    produitAssuranceService.update(models);
                }
            }

            System.gc();

            if(produitAssuranceService.findAll().isEmpty()) {

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
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    /******
     * @API Parametrage Type Message Assurance
     ****/
    @GetMapping(value = "/find-all-type-message-assurance", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeMessage() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeMessageService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-type-message-assurance-by-module/{module}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeMessageByModule(@PathVariable(name = "module") String module) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeMessageService.findAll(module));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-type-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewTypeMessage (@RequestBody TypeMessage model) {

        try {
            TypeMessage data = null;

            if(model == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(typeMessageService.findOne(model.getTypemessgae().toUpperCase()) == null ) {

                    data = new TypeMessage();

                    data.setTypemessgae(model.getTypemessgae().toUpperCase());

                    data.setModule(model.getModule());

                    TypeMessage _ag_ = typeMessageService.create(data);

                    if (_ag_.getId() == null) {
                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/update-type-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = TypeMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteTypeMessage(@RequestBody TypeMessage item) {

        try {

            if(item == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                TypeMessage up_data = typeMessageService.findById(item.getId());

                if(up_data!= null) {

                    up_data.setTypemessgae(item.getTypemessgae().toUpperCase());

                    up_data.setModule(item.getModule());

                    typeMessageService.update(up_data);

                    if (up_data.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.OK).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully updated data"),
                                        new RequestMessage(null, "Success")
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_GATEWAY.value(), "Données non existante"),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/delete-item-type-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteTypeMessage(@RequestBody TypeMessage object) {

        try {
            if(object != null){

                TypeMessage item = typeMessageService.findById(object.getId());

                if(item!= null) {

                    typeMessageService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {
                        throw new Exception("Impossible de désactiver la donnée");
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
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getTypemessgae().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getTypemessgae().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-type-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypeMessage model", notes = "TypeMessage model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeTypeMessage(@RequestBody TypeMessage object) {

        try {
            if(object != null){

                TypeMessage item = typeMessageService.findById(object.getId());

                if(item!= null) {

                    typeMessageService.actionRequest(item.getId(), true);

                    if (item.getId() == null) {

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getTypemessgae().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getTypemessgae().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-type-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllTypeMessage() {

        try {

            List<TypeMessage> items = typeMessageService.findAll();

            for (TypeMessage item :items){

                TypeMessage models = typeMessageService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    typeMessageService.update(models);
                }
            }

            System.gc();

            if(typeMessageService.findAll().isEmpty()) {

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
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    /******
     * @API Parametrage Model Message Assurance
     ****/
    @GetMapping(value = "/find-all-modele-message-assurance", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllModelMessage() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(modelMessageService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-modele-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewModelMessage (@RequestBody ModelMessage model) {

        try {
            ModelMessage data = null;

            if(model == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(modelMessageService.findOne(model.getTypeMessage(), model.getEntreprises()) == null ) {

                    data = new ModelMessage();

                    data.setLibelle(model.getLibelle().toUpperCase());

                    data.setTypeMessage(model.getTypeMessage());

                    data.setMessage(model.getMessage());

                    data.setEntreprises(model.getEntreprises());

                    ModelMessage _ag_ = modelMessageService.create(data);

                    if (_ag_.getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/update-modele-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteModelMessage(@RequestBody ModelMessage item) {

        try {

            if(item == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                ModelMessage up_data = modelMessageService.findById(item.getId());

                if(up_data!= null) {

                    up_data.setLibelle(item.getLibelle().toUpperCase());

                    up_data.setTypeMessage(item.getTypeMessage());

                    up_data.setMessage(item.getMessage());

                    up_data.setEntreprises(item.getEntreprises());

                    modelMessageService.update(up_data);

                    if (up_data.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.OK).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully updated data"),
                                        new RequestMessage(null, "Success")
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_GATEWAY.value(), "Données non existante"),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/delete-item-modele-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteModelMessage(@RequestBody ModelMessage object) {

        try {
            if(object != null){

                ModelMessage item = modelMessageService.findById(object.getId());

                if(item!= null) {

                    modelMessageService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {
                        throw new Exception("Impossible de désactiver la donnée");
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
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getLibelle().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getLibelle().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-modele-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "ModelMessage model", notes = "ModelMessage model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeModelMessage(@RequestBody ModelMessage object) {

        try {
            if(object != null){

                ModelMessage item = modelMessageService.findById(object.getId());

                if(item!= null) {

                    modelMessageService.actionRequest(item.getId(), true);

                    if (item.getId() == null) {

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getLibelle().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getLibelle().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-modele-message-assurance", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllModelMessage() {

        try {

            List<ModelMessage> items = modelMessageService.findAll();

            for (ModelMessage item :items){

                ModelMessage models = modelMessageService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    modelMessageService.update(models);
                }
            }

            System.gc();

            if(modelMessageService.findAll().isEmpty()) {

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
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    /*private String getOuputFileName(){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return OUTPUT_FOLDER+timeStamp+"-archived-"+INPUT_FILE_NAME;
    }

    private void moveFile() throws IOException {
        log.info("[batch-import-renewal-policies] archivage du fichier source en cours");
        String name = getOuputFileName();
        try{
            Files.move(Paths.get(getInputFileName()), Paths.get(name), StandardCopyOption.REPLACE_EXISTING);
            log.info("[batch-import-renewal-policies] archivage ok: {}", name);
        } catch (IOException e){
            log.info("[batch-import-renewal-policies] une erreur s'est produite, le fichier n'a pas pu etre archivé");
            log.info("[batch-import-renewal-policies] verifier le chemain de destination");
            log.info("[batch-import-renewal-policies] input: {}", getInputFileName());
            log.info("[batch-import-renewal-policies] ouput: {}", name);
            log.info("[batch-import-renewal-policies] {}", e.getMessage());
        }
    }*/

    private String getInputFileName(String input_file_name){
        return  INTPUT_FOLDER+input_file_name;
    }



    @GetMapping(value = "/find-all-groupe", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllGroupe() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            List<GroupeDto> groupeDtos = new ArrayList<>();

            for (Groupe groupe : groupeService.findAlls()){

                GroupeDto dto = new GroupeDto();

                dto.setOrdre(""+groupe.getOrdre());

                dto.setNomGroupe(groupe.getNomGroupe());

                dto.setNbreContact(Long.valueOf(prospectServices.findAll(groupe).size()));

                dto.setProfileId(groupe.getProfile() == null ? "ND" : groupe.getProfile().getIdDigital());

                dto.setActive(groupe.isActive());

                groupeDtos.add(dto);
            }

            response = ResponseEntity.status(HttpStatus.OK).body(groupeDtos);
        }
        catch (Exception e) {
            // TODO: handle exception

            e.printStackTrace();
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/find-all-groupe-prospect/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findallGroupeProspect(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(!idDigital.isEmpty()){

                Profile profile = profileService.findOne(idDigital);

                if(profile != null){

                    //System.out.println("==== =====" + profile.getGareRoutiere().getCompagnie());

                    response = ResponseEntity.status(HttpStatus.OK).body(

                            groupeService.findAll(profile.getGareRoutiere().getCompagnie())
                    );
                }
                else{

                    response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-groupe", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewGroupe (@RequestBody Groupe model) {

        try {

            Groupe data = null;

            if(model == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(groupeService.findOne(model.getNomGroupe().toUpperCase(), model.getEntreprises()) == null ) {

                    data = new Groupe();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setNomGroupe(model.getNomGroupe().toUpperCase());

                    data.setProfile(model.getProfile());

                    data.setEntreprises(model.getEntreprises());

                    if (groupeService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        return ResponseEntity.status(HttpStatus.CREATED).body(

                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Un slogan a été déjà parametré pour le compte de l'entreprise"),

                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/update-groupe", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = Groupe.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteGroupe(@RequestBody GroupeDto item) {

        try {

            //System.out.println(">>>> >>>>> >>>>"+ item);

            if(item == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                Groupe up_data = groupeService.findByOrdre(item.getOrdre());

                if(up_data!= null) {

                    Profile profile = profileService.findOne(item.getProfileId());

                    up_data.setNomGroupe(item.getNomGroupe().toUpperCase());

                    up_data.setProfile(profile);

                    up_data.setEntreprises(profile.getGareRoutiere().getCompagnie());

                    groupeService.update(up_data);

                    if (up_data.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {


                        return ResponseEntity.status(HttpStatus.OK).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully updated data"),
                                        new RequestMessage(null, "Success")
                                )
                        );
                    }
                }
                else {

                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
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

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/delete-item-groupe", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Groupe.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteGroupe(@RequestBody Groupe object) {

        try {
            if(object != null){

                Groupe item = groupeService.findById(object.getId());

                if(item!= null) {

                    groupeService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {
                        throw new Exception("Impossible de désactiver la donnée");
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
                            new RequestInformation(400, "Impossible de faire la suppression"),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression"),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping(value = "/active-item-groupe", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Groupe model", notes = "Groupe model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Groupe.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeGroupe(@RequestBody Groupe object) {

        try {
            if(object != null){

                Groupe item = groupeService.findById(object.getId());

                if(item!= null) {

                    groupeService.actionRequest(item.getId(), true);

                    Groupe up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation"),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation"),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-groupe", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllGroupe() {

        try {

            List<Groupe> items = groupeService.findAll();

            for (Groupe item :items){

                Groupe models = groupeService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    groupeService.update(models);
                }
            }

            System.gc();

            if(groupeService.findAll().isEmpty()) {

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
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @GetMapping(value = "/find-all-prospect/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findallProspect(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(!idDigital.isEmpty()){

                Profile profile = profileService.findOne(idDigital);

                if(profile != null){

                    response = ResponseEntity.status(HttpStatus.OK).body(

                            prospectServices.findAll(profile.getGareRoutiere().getCompagnie())
                    );
                }
                else{

                    response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/create-new-prospect-in-porte-feuille", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<Object> createdNewPorteFeuilleProspect(@RequestBody Prospect entity) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(entity == null) {

                response = ResponseEntity.status(HttpStatus.CREATED).body(

                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises")
                );
            }
            else {

                //Vérfication de la disponibilité du groupe

                List<Groupe> groupeList = groupeService.findAll(entity.getEntreprises());

                if(groupeList.size() > 0){

                    //Vérifier si le contact à été rajouté dans le groupe
                    Prospect prs = prospectServices.findOne(entity.getNomPrenoms().toUpperCase(), entity.getGroupe(), entity.getEntreprises());

                    if(prs != null){

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestInformation(400, "Cet prospect que vous souhaitez ajouterest déjà présent dans le groupe "+entity.getGroupe().getNomGroupe()+".")

                        );
                    }
                    else{

                        Prospect prospect = new Prospect();

                        prospect.setOrdre(Utils.generateRandom(8));

                        prospect.setTypeProspect(entity.getTypeProspect());

                        prospect.setNomPrenoms(entity.getNomPrenoms().toUpperCase());

                        prospect.setAdresse(entity.getAdresse() == null ? "NA" : entity.getAdresse().toUpperCase());

                        prospect.setContact(entity.getContact());

                        prospect.setDateNaissance(entity.getDateNaissance());

                        prospect.setProfile(entity.getProfile());

                        prospect.setEntreprises(entity.getEntreprises());

                        prospect.setGroupe(entity.getGroupe());

                        prospect.setProduit(entity.getProduit());

                        if (prospectServices.create(prospect).getId() == null) {

                            response = ResponseEntity.status(HttpStatus.CREATED).body(

                                    new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                            );
                        }
                        else {

                            response = ResponseEntity.status(HttpStatus.CREATED).body(

                                    new RequestInformation(201, "Successfully created data")
                            );
                        }
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(400, "Aucun groupe n'est disponible. Nous vous invitons à créer un Groupe de Prospect avec l'ajout.")

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



    @PutMapping(value = "/update-mes-prospect", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = Prospect.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteProspect(@RequestBody Prospect item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                Prospect up_data = prospectServices.findOne(item.getId());

                if(up_data!= null) {

                    up_data.setTypeProspect(item.getTypeProspect());

                    up_data.setNomPrenoms(item.getNomPrenoms().toUpperCase());

                    up_data.setAdresse(item.getAdresse());

                    up_data.setContact(item.getContact());

                    up_data.setDateNaissance(item.getDateNaissance());

                    up_data.setGroupe(item.getGroupe());

                    up_data.setProduit(item.getProduit());

                    if (prospectServices.update(up_data).getId() == null) {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                        );
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(

                                new RequestInformation(201, "Successfully created data")
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(400, "Données non existante")
                    );
                }
            }
        }
        catch (Exception e) {

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }



    @PutMapping(value = "/delete-item-mes-prospect", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Prospect model", notes = "Prospect model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteProspect(@RequestBody Prospect object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                Prospect item = prospectServices.findOne(object.getId());

                if(item!= null) {

                    prospectServices.actionProspect(item.getId(), false);

                    if (item.getId() == null) {
                        throw new Exception("Impossible de désactiver la donnée");
                    }
                    else {


                        response = ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, "Successful deletion of the data"),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNomPrenoms().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNomPrenoms().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {

            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }



    @PutMapping(value = "/active-item-mes-prospect", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Prospect model", notes = "Prospect model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ParametrageDates.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeProspect(@RequestBody Prospect object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(object != null){

                Prospect item = prospectServices.findOne(object.getId());

                if(item!= null) {

                    prospectServices.actionProspect(item.getId(), true);

                    Prospect up = item;

                    if (up.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible d'activer le type contrat"),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, "Successful deletion of the data"),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire l'activation"),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation"),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        finally {

            return response;
        }
    }


    @PostMapping(value = "/importe-porte-feuille-prospect-fichier/{profileId}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> importeProspect (@RequestParam(name = "file") MultipartFile multipartFile, @PathVariable(name = "profileId") String profileId) {

        ResponseEntity<? extends Object> response = ResponseEntity.status(HttpStatus.NO_CONTENT).body("No content to upload");

        storageService.store(multipartFile);

        log.info("[ DI-GITAL WEB {} - UPLOAD ] :: store upload file on server", sdf.format(new Date()));

        try {
            if (multipartFile.isEmpty()) {

                log.info("[ DI-GITAL WEB {} - UPLOAD ] :: ERROR = empty file to process", sdf.format(new Date()));

                response = ResponseEntity.status(HttpStatus.CREATED).body(

                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Please select a file to upload")
                );
            }
            else{

                //Recuperartion du profile connecté
                Profile profile = profileService.findOne(profileId);

                if(profile == null){

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(400, "Le profil connecté n'est pas autorisé a effectué cette opération.")

                    );
                }
                else{

                    //Vérifier la disponibilité d'au moins un groupe
                    List<Groupe> groupeList = groupeService.findAll(profile.getGareRoutiere().getCompagnie());

                    if(groupeList.size() > 0){

                        Prospect data = null;

                        FileInputStream excelFile = new FileInputStream(new File(new StringBuilder(INTPUT_FOLDER).append(File.separator).append(multipartFile.getOriginalFilename()).toString()));

                        Workbook workbook = new XSSFWorkbook(excelFile);

                        Sheet sheet = workbook.getSheet("prospect");

                        Iterator<Row> rows = sheet.iterator();

                        List<Prospect> lists = new ArrayList<Prospect>();

                        int rowNumber = 0;

                        while (rows.hasNext()) {

                            Row currentRow = rows.next();

                            // skip header
                            if (rowNumber == 0) {
                                rowNumber++;
                                continue;
                            }

                            Iterator<Cell> cellsInRow   = currentRow.iterator();

                            Prospect prospect           = new Prospect();

                            int cellIdx = 0;

                            while (cellsInRow.hasNext()) {

                                Cell currentCell = cellsInRow.next();

                                switch (cellIdx) {

                                    case 0:
                                        if(!currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -") || currentCell.getStringCellValue() != null){

                                            prospect.setTypeProspect(typeClientService.findOne(currentCell.getStringCellValue().toUpperCase()));
                                        }
                                        else{
                                            prospect.setTypeProspect(typeClientService.findById(1L));
                                        }
                                    break;

                                    case 1:
                                        prospect.setNomPrenoms(currentCell.getStringCellValue() != null ? currentCell.getStringCellValue().toUpperCase() : "NON-DÉFINI");
                                    break;

                                    case 2:
                                        prospect.setDateNaissance(currentCell.getDateCellValue() != null ? convertLocalDateToDate(currentCell.getDateCellValue()) : null);
                                    break;

                                    case 3:
                                        prospect.setContact(currentCell.getStringCellValue());
                                    break;

                                    case 4:

                                        //System.out.println("===== =======" + currentCell.getStringCellValue());

                                        if(!currentCell.getStringCellValue().equalsIgnoreCase("- SELECTION UNE VALEUR -")){

                                            prospect.setProduit(currentCell.getStringCellValue().equalsIgnoreCase("NON-APPLICABLE") ? null : produitAssuranceService.findOne(currentCell.getStringCellValue().toUpperCase()));
                                        }
                                        else {
                                            prospect.setProduit(null);
                                        }
                                    break;

                                    case 5:

                                        //System.out.println("== ==== ====>"+ currentCell.getStringCellValue());

                                        if(currentCell.getStringCellValue().equalsIgnoreCase("NA")){

                                            prospect.setGroupe(groupeService.findById(1L));
                                        }
                                        else{

                                            prospect.setGroupe(groupeService.findOne(currentCell.getStringCellValue(), profile.getGareRoutiere().getCompagnie()));
                                        }
                                    break;

                                    case 6:

                                        prospect.setProfile(userService.findOne(currentCell.getStringCellValue()).getProfile());

                                        prospect.setEntreprises(prospect.getProfile().getGareRoutiere().getCompagnie());

                                    break;

                                    case 7:
                                        prospect.setAdresse(currentCell.getStringCellValue());
                                    break;
                                }

                                cellIdx ++;
                            }

                            lists.add(prospect);
                        }

                        workbook.close();

                        //Enregistrement en base des données
                        for (Prospect entity : lists) {

                            /*log.info("[batch-import-data prospect ok: {}", entity);
                            log.info("[batch-import-data prospect ok: {}", entity.getNomPrenoms());
                            log.info("[batch-import-data prospect ok: {}", entity.getGroupe());
                            log.info("[batch-import-data prospect ok: {}", entity.getEntreprises());*/

                            if(entity.getNomPrenoms() != null && entity.getGroupe() != null && entity.getEntreprises() != null){

                                if(prospectServices.findOne(entity.getNomPrenoms().toUpperCase(), entity.getGroupe(), entity.getEntreprises()) == null){

                                    Prospect prspt = new Prospect();

                                    prspt.setOrdre(Utils.generateRandom(8));

                                    prspt.setTypeProspect(entity.getTypeProspect());

                                    prspt.setNomPrenoms(entity.getNomPrenoms().toUpperCase());

                                    prspt.setAdresse(entity.getAdresse());

                                    prspt.setContact(entity.getContact());

                                    prspt.setDateNaissance(entity.getDateNaissance());

                                    prspt.setProfile(entity.getProfile());

                                    prspt.setEntreprises(entity.getEntreprises());

                                    prspt.setGroupe(entity.getGroupe());

                                    prspt.setProduit(entity.getProduit());

                                    if (prospectServices.create(prspt).getId() == null) {

                                        response = ResponseEntity.status(HttpStatus.CREATED).body(

                                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                                        );
                                    }
                                }
                            }
                        }
                        System.gc();

                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                        //Move file
                        Path sourcePath         = Paths.get(INTPUT_FOLDER+"//"+multipartFile.getOriginalFilename());

                        Path destinationPath    = Paths.get(OUTPUT_FOLDER+timeStamp+"-archived-prospect-"+multipartFile.getOriginalFilename());

                        try{

                            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                            response = ResponseEntity.status(HttpStatus.CREATED).body(

                                    new RequestInformation(201, "Importation effectué avec succès et déplacement du fichier source effectué avec succès.")
                            );

                        } catch (Exception exception) {
                            //this is where the error will be thrown if the file did not move properly
                            //(null pointer etc...), you can place code here to run if there is an error
                            response = ResponseEntity.status(HttpStatus.CREATED).body(

                                    new RequestInformation(201, "Importation effectué avec succès mais sans déplacer le fichier source")
                            );
                        }
                    }
                    else{

                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                        //Move file
                        Path sourcePath = Paths.get(INTPUT_FOLDER+"//"+multipartFile.getOriginalFilename());

                        Path destinationPath = Paths.get(OUTPUT_FOLDER+timeStamp+"-failed-prospect-"+multipartFile.getOriginalFilename());

                        try {

                            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                    new RequestInformation(400, "Aucun groupe n'est défini pour votre entreprise.")

                            );
                        }
                        catch (Exception exception) {

                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                    new RequestInformation(400, "Aucun groupe n'est défini pour votre entreprise.")

                            );
                        }
                    }
                }
            }
        }
        catch (Exception exception) {

            exception.printStackTrace();

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

        finally {

            return response;
        }
    }


    @GetMapping(value = "/find-one-modele-mesage/{idDigital}/{typeHash}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findOneModeleMessage(@PathVariable(name = "idDigital") String idDigital, @PathVariable(name = "typeHash") String typeHash) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(!idDigital.isEmpty()){

                Profile profile = profileService.findOne(idDigital);

                if(profile != null){

                    ModelMessage message = modelMessageService.findOne(profile.getGareRoutiere().getCompagnie(), typeHash.toUpperCase());

                    if(message == null){

                        response = ResponseEntity.status(HttpStatus.OK).body(null );
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.OK).body(message);
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/create-new-campagne-message-en-masse", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdCampagneMessageEnMasse(@RequestBody NotificationEnMasse entity) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            NotificationEnMasse data    = null;

            String number               = null;

            //System.out.println("=== ===== ======" + entity);

            if(entity == null) {

                response = ResponseEntity.status(HttpStatus.CREATED).body(

                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises")
                );
            }
            else {

                //CHECK QUOTA SMS
                SmsCredential credential = smsCredentialService.findOne(entity.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getId());

                Long quotaSms         = null;

                if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                    quotaSms     = credential.getNombreSmsLeTexto();

                }else{

                    quotaSms      = credential.getNombreSms();
                }

                if(quotaSms > 30){

                    //VERIFIONS SI CETTE CAMPAGNE N'A PAS ETE DEJA ENVOYE
                    if(notificationEnMasseService.findOne(entity.getNomCampagne().toUpperCase(), entity.getEntreprises()) == null ) {

                        data = new NotificationEnMasse();

                        data.setOrdre(Utils.generateRandom(8));

                        data.setNomCampagne(entity.getNomCampagne().toUpperCase());

                        data.setProfile(entity.getProfile());

                        data.setEntreprises(entity.getEntreprises());

                        data.setSourceData(entity.getSourceData());

                        data.setTypeMessage(entity.getTypeMessage());

                        data.setInspirationModele(entity.getInspirationModele());

                        data.setNomGroup(entity.getGroupeProspect() == null ? null : entity.getGroupeProspect().getNomGroupe());

                        data.setDateEnvoi(entity.getDateEnvoi());

                        data.setMessage(entity.getMessage());

                        if (notificationEnMasseService.create(data).getId() == null) {

                            //throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                            response = ResponseEntity.status(HttpStatus.CREATED).body(

                                    new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                            );
                        }
                        else {

                            double dtSms = 0;

                            //RECUPERATION DES RECEPTEURS DE MESSAGE

                            if(entity.getSourceData().equalsIgnoreCase("PORTE-FEUILLE")){

                                List<PorteFeuilleClient> clientList = porteFeuilleClientService.findAll(entity.getEntreprises());

                                if(clientList.size() > 0){

                                    for (PorteFeuilleClient porteFeuilleClient : clientList){

                                        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                                            number       = "225"+porteFeuilleClient.getContact();

                                        }else{

                                            number       = entity.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+porteFeuilleClient.getContact();
                                        }

                                        dtSms = Math.ceil(entity.getMessage().length()/160);

                                        SendMessage sendMessage = new SendMessage();

                                        sendMessage.setDateEnvoi(entity.getDateEnvoi());

                                        sendMessage.setNumeroContact(number);

                                        sendMessage.setMessage(entity.getMessage());

                                        sendMessage.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                                        sendMessage.setTypeMessage(entity.getTypeMessage());

                                        sendMessage.setStatut(EtatLecture.PENDING);

                                        sendMessage.setProfile(profileService.findOne(entity.getProfile().getIdDigital()));

                                        sendMessage.setNbrePage(dtSms == 0 ? 1 : dtSms);

                                        sendMessage.setPorteFeuilleClient(porteFeuilleClient);

                                        sendMessageService.create(sendMessage);


                                        //DECOMPTE DU VOLUME SMS
                                        credential.setNombreSmsLeTexto((long) (credential.getNombreSmsLeTexto() - sendMessage.getNbrePage()));

                                        credential.setNombreSms((long) (credential.getNombreSms() - sendMessage.getNbrePage()));

                                        smsCredentialService.updated(credential);
                                    }
                                }
                            }

                            if(entity.getSourceData().equalsIgnoreCase("PROSPECT")){

                                List<Prospect> prospectList = prospectServices.findAll(entity.getGroupeProspect(), entity.getEntreprises());

                                if(prospectList.size() > 0){

                                    for (Prospect prospect : prospectList){

                                        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                                            number       = "225"+prospect.getContact();

                                        }else{

                                            number       = entity.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+prospect.getContact();
                                        }

                                        dtSms = Math.ceil(entity.getMessage().length()/160);

                                        SendMessage sendMessage = new SendMessage();

                                        sendMessage.setDateEnvoi(entity.getDateEnvoi());

                                        sendMessage.setNumeroContact(number);

                                        sendMessage.setMessage(entity.getMessage());

                                        sendMessage.setEntreprises(entity.getProfile().getGareRoutiere().getCompagnie());

                                        sendMessage.setTypeMessage(entity.getTypeMessage());

                                        sendMessage.setStatut(EtatLecture.PENDING);

                                        sendMessage.setProfile(profileService.findOne(entity.getProfile().getIdDigital()));

                                        sendMessage.setNbrePage(dtSms == 0 ? 1 : dtSms);

                                        sendMessageService.create(sendMessage);


                                        //DECOMPTE DU VOLUME SMS
                                        credential.setNombreSmsLeTexto((long) (credential.getNombreSmsLeTexto() - sendMessage.getNbrePage()));

                                        credential.setNombreSms((long) (credential.getNombreSms() - sendMessage.getNbrePage()));

                                        smsCredentialService.updated(credential);
                                    }
                                }
                            }

                            //Enregistrement de la notification
                            NotificationSysteme notification = new NotificationSysteme();

                            notification.setNotification("Création notofication en masse " + entity.getNomCampagne().toUpperCase() + " le " +datStr.format(new Date())+" à "+heure.format(new Date()));

                            notification.setProfile(entity.getProfile());

                            notification.setReference(String.valueOf(Utils.generateRandom(6)));

                            notification.setType(TypeNotification.Creation_Masse);

                            notificationService.create(notification);


                            response = ResponseEntity.status(HttpStatus.CREATED).body(

                                    new RequestInformation(201, "Successfully created data")
                            );
                        }
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Cette camapagne a déjà été enregistrée")
                        );
                    }

                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(605, "Votre quota minimum d'sms pour la programmation est de 30 sms. Nous prions d'effectuer au rechargement de votre compte.")

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


    @GetMapping(value = "/find-all-campagne-message-en-masse/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllNotifMasse(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(!idDigital.isEmpty()){

                Profile profile = profileService.findOne(idDigital);

                if(profile != null){

                    response = ResponseEntity.status(HttpStatus.OK).body(

                            notificationEnMasseService.findAll(profile.getGareRoutiere().getCompagnie())
                    );
                }
                else{

                    response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PutMapping(value = "/delete-item-campagne-message-en-masse", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Groupe.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteCampagneMessageEnMasse(@RequestBody NotificationEnMasse object) {

        try {
            if(object != null){

                NotificationEnMasse item = notificationEnMasseService.findById(object.getId());

                if(item!= null) {

                    notificationEnMasseService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {
                        throw new Exception("Impossible de désactiver la donnée");
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
                            new RequestInformation(400, "Impossible de faire la suppression"),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression"),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping(value = "/active-item-campagne-message-en-masse", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Groupe model", notes = "Groupe model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Groupe.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeeCampagneMessageEnMasse(@RequestBody NotificationEnMasse object) {

        try {
            if(object != null){

                NotificationEnMasse item = notificationEnMasseService.findById(object.getId());

                if(item!= null) {

                    notificationEnMasseService.actionRequest(item.getId(), true);

                    NotificationEnMasse up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation"),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation"),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @GetMapping(value = "/find-all-sender-message/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllSenderMessage(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(!idDigital.isEmpty()){

                Profile profile = profileService.findOne(idDigital);

                if(profile != null){

                    response = ResponseEntity.status(HttpStatus.OK).body(

                            sendMessageService.findAll(profile.getGareRoutiere().getCompagnie(), false)
                    );
                }
                else{

                    response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }

}
