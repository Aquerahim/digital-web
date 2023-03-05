package com.phoenixacces.apps.controller.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenixacces.apps.enumerations.Direction;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.courrier.*;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.audits.AuditFlux;
import com.phoenixacces.apps.persistence.entities.module.Colis;
import com.phoenixacces.apps.persistence.entities.module.ServiceCourrier;
import com.phoenixacces.apps.persistence.entities.service.Birthday;
import com.phoenixacces.apps.persistence.services.audits.AuditFluxService;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.module.ColisService;
import com.phoenixacces.apps.persistence.services.module.ServiceCourrierService;
import com.phoenixacces.apps.persistence.services.parametrage.*;
import com.phoenixacces.apps.producer.JmsProducer;
import com.phoenixacces.apps.utiles.Utils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API pour les services destinés à l'utlisateur")
@Slf4j
@RequiredArgsConstructor
public class ServicesUtilisateurController {

    private final SuiviDemandeService demandeService;
    private final ProfileService profileService;
    private final ColisService colisService;
    private final ServiceCourrierService serviceCourrierService;
    private final EntrepriseService compagnieRoutiereService;
    private final SmsCredentialService smsCredentialService;
    private final SMSPhoenixAccesService smsEnvoye;
    private final TypeMessageService typeMessageService;
    private final TypeSouscrivantService typeSouscrivant;
    private final JmsProducer jmsProducer;
    private final AuditFluxService auditFluxService;
    private final BirthdayService birthdayService;

    @Value(value = "${frais.suivi.sms}")
    private String fraisSuiviSMS;



    @GetMapping(value = "/find-all-courrier-envoye/{IdDigitalApps}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllCourrierEnvoyes(@PathVariable(name = "IdDigitalApps") String IdDigitalApps) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(serviceCourrierService.findAll(profileService.findOne(IdDigitalApps), false));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-courrier-envoye-par-cie/{cieId}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllCourrierEnvoyesByCie(@PathVariable(name = "cieId") Long cieId) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(serviceCourrierService.findAlls(compagnieRoutiereService.findOne(cieId), false));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/save-envoi-de-colis", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> saveEnvoiColis (@RequestBody CourrierModel courrier) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        ObjectMapper mapper = new ObjectMapper();
        String auditKey = Utils.instant2String();

        log.info("[ DI-GITAL :: ENVOI COURRIER ] ----- --------------- ----> {}", courrier);

        try {

            ServiceCourrier serviceCourrier = null;
            Colis colis     = null;
            SmsMessage sms  = null;

            if(courrier == null) {

                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new RequestResponse(
                        new RequestInformation(400, "Impossible de faire le mappage des données avec le model proposé"),
                        new RequestMessage(null, null)
                    )
                );
            }
            else {

                auditFluxService
                        .create(new AuditFlux(
                                null, auditKey,Instant.now(), "ENVOI COURRIER", "Enregistrement du colis à expédier",
                                mapper.writeValueAsString(courrier), "", Direction.INPUT
                        ));

                String numColis = Utils.numeroColis();

                if(serviceCourrierService.findOne(numColis, courrier.getGare().getCompagnie(), false) == null ) {
                    log.info("[ DI-GITAL :: ENVOI COURRIER ] ::: STEP 1 --------------- ----> Enregistrement des informations du courrier");
                    serviceCourrier = new ServiceCourrier();
                    serviceCourrier.setColisNumber(numColis);
                    serviceCourrier.setNomExpediteur(courrier.getNomExpediteur().toUpperCase());
                    serviceCourrier.setPhoneExpedietur(courrier.getPhoneExpedietur());
                    serviceCourrier.setTypeEnvoi(courrier.getTypeEnvoi());
                    serviceCourrier.setAdresseExpediteur(courrier.getAdresseExpediteur().toUpperCase());
                    serviceCourrier.setVilleExpeditrice(courrier.getVilleExpeditrice().toUpperCase());
                    serviceCourrier.setNomDestinataire(courrier.getNomDestinataire().toUpperCase());
                    serviceCourrier.setPhoneDestinatire(courrier.getPhoneDestinatire().toUpperCase());
                    serviceCourrier.setVilleDestinatrice(courrier.getVilleDestinatrice().toUpperCase());
                    serviceCourrier.setFrais(courrier.getFrais());
                    serviceCourrier.setValeurColis(courrier.getValeurColis());
                    serviceCourrier.setGare(courrier.getGare());
                    serviceCourrier.setSuiviDemande(demandeService.findOne(1L));
                    serviceCourrier.setProfile(courrier.getProfile());
                    serviceCourrier.setMonatFrais(courrier.getFraisEnvoi());
                    serviceCourrier.setMontantSuiviSMS(Double.parseDouble(fraisSuiviSMS));
                    serviceCourrier.setMontantTotalPayer(Double.parseDouble(fraisSuiviSMS) + serviceCourrier.getMonatFrais());
                    serviceCourrier.setValideur(Utils.generateCodeValidateur(3));
                    ServiceCourrier _ag_ = serviceCourrierService.create(serviceCourrier);

                    if (_ag_.getId() == null) {
                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                    else {

                        //Enregistrement des colis
                        if(!courrier.getColis().isEmpty()){
                            log.info("[ DI-GITAL :: ENVOI COURRIER ] ::: STEP 2 --------------- ----> Enregistrement du Colis");
                            for (ColisModel item : courrier.getColis()) {
                                colis = new Colis();
                                colis.setColisNumber(serviceCourrier.getColisNumber());
                                colis.setNatureColis(item.getNatureColis().toUpperCase());
                                colis.setDesignationColis(item.getDesignationColis().toUpperCase());
                                colisService.create(colis);
                            }
                        }

                        //Enregistrement des information sur l'anniversaire
                        if((courrier.getJourNaissance()> 0) && !courrier.getMoisNaissance().isEmpty()){
                            log.info("[ DI-GITAL :: ENVOI COURRIER ] ::: STEP 3 --------------- ----> Enregistrement du Birthday");
                            Birthday birthday = new Birthday();
                            birthday.setBirthday(courrier.getJourNaissance()+"/"+courrier.getMoisNaissance());
                            birthday.setAnniversaireux(serviceCourrier.getNomExpediteur());
                            birthday.setAnnee(""+ Calendar.YEAR);
                            birthday.setEnvoi(false);
                            birthdayService.create(birthday);
                        }

                        //Envoi des différents sms à l'expéditeur et au destinataire
                        String numberExp  = serviceCourrier.getGare().getCompagnie().getPaysAutorise().getIndicatif()+""+serviceCourrier.getPhoneExpedietur();
                        String numberDest = serviceCourrier.getGare().getCompagnie().getPaysAutorise().getIndicatif()+""+serviceCourrier.getPhoneDestinatire();
                        String prenomsExp = WordUtils.capitalize(serviceCourrier.getNomExpediteur().toLowerCase()).split(" ")[1];
                        String prenomsDesti = WordUtils.capitalize(serviceCourrier.getNomDestinataire().toLowerCase()).split(" ")[1];

                        if (!numberExp.isEmpty()){
                            log.info("[ DI-GITAL :: ENVOI SMS ] ::: STEP 4 --------------- ----> Envoi sms à l'expéditeur");
                            sms = new SmsMessage();
                            sms.setTypeMessage(4L);
                            sms.setToId(numberExp);
                            sms.setContent("Bonjour "+ prenomsExp +", votre courrier ou colis a bien été enregistré. Merci de faire confiance à "+serviceCourrier.getGare().getCompagnie().getAbbrev().toUpperCase()+".");
                            sms.setFromName(serviceCourrier.getNomExpediteur().toUpperCase());
                            sms.setUsername(serviceCourrier.getGare().getCompagnie().getSmsCredential().getUsername());
                            sms.setPassword(serviceCourrier.getGare().getCompagnie().getSmsCredential().getPassword());
                            sms.setSenderId(serviceCourrier.getGare().getCompagnie().getSmsCredential().getSenderId());
                            sms.setRefCourier(_ag_.getColisNumber());
                            sms.setCompagnieId(_ag_.getGare().getCompagnie().getId());
                            jmsProducer.send(new JmsMessage("SMS to send à l'expéditeur", Converter.pojoToJson(sms), SmsMessage.class));
                        }


                        if (!numberDest.isEmpty()){
                            log.info("[ DI-GITAL :: ENVOI SMS ] ::: STEP 5 --------------- ----> Envoi sms au destinataire");
                            sms = new SmsMessage();
                            sms.setTypeMessage(4L);
                            sms.setToId(numberDest);
                            sms.setContent("Bonjour "+ prenomsDesti +", un courrier en votre nom a été expédié par "+prenomsExp.toUpperCase()+". Vous serez informé a son arrivé à la gare "+serviceCourrier.getVilleDestinatrice().toUpperCase()+". Merci de faire confiance à "+serviceCourrier.getGare().getCompagnie().getAbbrev().toUpperCase()+". \nInfoline : "+serviceCourrier.getGare().getContact()+".");
                            sms.setFromName(serviceCourrier.getNomDestinataire().toUpperCase());
                            sms.setUsername(serviceCourrier.getGare().getCompagnie().getSmsCredential().getUsername());
                            sms.setPassword(serviceCourrier.getGare().getCompagnie().getSmsCredential().getPassword());
                            sms.setSenderId(serviceCourrier.getGare().getCompagnie().getSmsCredential().getSenderId());
                            sms.setRefCourier(_ag_.getColisNumber());
                            sms.setCompagnieId(_ag_.getGare().getCompagnie().getId());
                            jmsProducer.send(new JmsMessage("SMS to send destinataire", Converter.pojoToJson(sms), SmsMessage.class));
                        }


                        if (!numberDest.isEmpty()){
                            log.info("[ DI-GITAL :: ENVOI SMS ] ::: STEP 6 --------------- ----> Envoi sms du code validateur au destinataire");
                            sms = new SmsMessage();
                            sms.setTypeMessage(7L);
                            sms.setToId(numberDest);
                            sms.setContent("Bonjour "+ prenomsDesti +", votre code validateur pour le retrait de votre colis est "+serviceCourrier.getValideur()+". Merci de faire confiance à "+serviceCourrier.getGare().getCompagnie().getAbbrev().toUpperCase()+". \nInfoline : "+serviceCourrier.getGare().getContact()+".");
                            sms.setFromName(serviceCourrier.getNomDestinataire().toUpperCase());
                            sms.setUsername(serviceCourrier.getGare().getCompagnie().getSmsCredential().getUsername());
                            sms.setPassword(serviceCourrier.getGare().getCompagnie().getSmsCredential().getPassword());
                            sms.setSenderId(serviceCourrier.getGare().getCompagnie().getSmsCredential().getSenderId());
                            sms.setRefCourier(_ag_.getColisNumber());
                            sms.setCompagnieId(_ag_.getGare().getCompagnie().getId());
                            jmsProducer.send(new JmsMessage("SMS to send du code validateur au destinataire", Converter.pojoToJson(sms), SmsMessage.class));
                        }


                        //Audification de l'action
                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                            new RequestResponse(
                                new RequestInformation(201, "Successfully created data"),
                                new RequestMessage(null, null)
                            )
                        );

                        auditFluxService
                            .create(new AuditFlux(
                                    null, serviceCourrier.getColisNumber(), Instant.now(), "ENVOI COURRIER", "Enregistrement du colis à expédier",
                                    mapper.writeValueAsString(courrier), "ENVOI COURRIER :: Successfully created data", Direction.OUTPUT
                            ));

                        log.info("[ DI-GITAL :: ENVOI COURRIER ] ::: STEP FINAL --------------- ----> Done");
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new RequestResponse(
                                    new RequestInformation(400, "Données existante, Doublons évité"),
                                    new RequestMessage(null, "Fail")
                            )
                    );

                    auditFluxService
                            .create(new AuditFlux(
                                    null, auditKey,Instant.now(), "ENVOI COURRIER", "Enregistrement du colis à expédier",
                                    mapper.writeValueAsString(courrier), "Données existante, Doublons évité", Direction.OUTPUT
                            ));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

            auditFluxService
                    .create(new AuditFlux(
                            null, auditKey,Instant.now(), "ENVOI COURRIER", "Enregistrement du colis à expédier",
                            mapper.writeValueAsString(courrier), e.getMessage(), Direction.OUTPUT
                    ));
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-colis-attached/{id}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllColis(@PathVariable(name = "id") Long id) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(id != null){

                response = ResponseEntity.status(HttpStatus.OK).body(colisService.findAll(serviceCourrierService.findOne(id).getColisNumber()));
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




    @PostMapping(value = "/recherche-de-colis", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = CourrierModel.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> rechercheDeColis (@RequestBody RechercheCourrierModel search) {

        log.info("[ DI-GITAL WEB :: CONTROLLER ] SEARCH COLIS RESQUEST  =======================> param {}", search);

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Courrier crier = new Courrier();
        String message      = null;
        Birthday birthday   = null;
        List<CourrierMapping> courrierMappings = null;

        try {

            List<ServiceCourrier> svce = serviceCourrierService.findOne(search.getReference(), search.getNomDest(), search.getTelDest());

            if(svce == null){

                if(search.getReference() != null && search.getNomDest() == null && search.getTelDest() == null){
                    message = "Le colis que vous recherchez selon la r&eacute;f&eacute;rence n° "+search.getReference()+" n'a pas &eacute;t&eacute; retrouv&eacute; ou d&eacute;j&agrave; retir&eacute;";
                }
                else if(search.getNomDest() != null && search.getTelDest() == null && search.getReference() == null){
                    message = "Le colis que vous recherchez selon le nom du destinataire "+search.getNomDest().toUpperCase()+" n'a pas &eacute;t&eacute; retrouv&eacute; ou d&eacute;j&agrave; retir&eacute;\"";
                }
                else if(search.getTelDest() != null && search.getNomDest() == null && search.getReference() == null){
                    message = "Le colis que vous recherchez selon le n° de t&eacute;l&eacute;phone du destinataire "+search.getNomDest().toUpperCase()+" n'a pas &eacute;t&eacute; retrouv&eacute; ou d&eacute;j&agrave; retir&eacute;\"";
                }
                else{
                    message = "Le colis que vous recherchez avec les r&eacute;f&eacute;rences saisient n'ont pas &eacute;t&eacute; retrouv&eacute; ou d&eacute;j&agrave; retir&eacute;\"";
                }

                response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(

                    new RequestResponse(
                        new RequestInformation(400, message),
                        new RequestMessage(null, null)
                    )
                );
            }
            else{

                crier.setFind(svce.size());

                courrierMappings = new ArrayList<>();

                for (ServiceCourrier courrier : svce) {

                    CourrierMapping mapping = new CourrierMapping();

                    if (courrier.getNomDestinataire() != null && !courrier.getNomDestinataire().equals("")) {

                        birthday = birthdayService.findeOne(courrier.getNomDestinataire().toUpperCase());
                    }
                    mapping.setRefColis(courrier.getColisNumber());
                    mapping.setBirthDay(birthday != null ? birthday.getBirthday() : null);
                    mapping.setNomExpediteur(courrier.getNomExpediteur());
                    mapping.setPhoneExpedietur(courrier.getPhoneExpedietur());
                    mapping.setTypeEnvoi(courrier.getTypeEnvoi());
                    mapping.setAdresseExpediteur(courrier.getAdresseExpediteur());
                    mapping.setVilleExpeditrice(courrier.getVilleExpeditrice());
                    mapping.setNomDestinataire(courrier.getNomDestinataire());
                    mapping.setAdresseExpediteur(courrier.getAdresseExpediteur());
                    mapping.setPhoneDestinatire(courrier.getPhoneDestinatire());
                    mapping.setVilleDestinatrice(courrier.getVilleDestinatrice());
                    mapping.setFrais(courrier.getFrais());
                    mapping.setMontantFrais(courrier.getMonatFrais());
                    mapping.setValeurColis(courrier.getValeurColis());
                    mapping.setTotalPaye(courrier.getMontantTotalPayer());
                    mapping.setSuiviSms(courrier.getMontantSuiviSMS());
                    mapping.setGare(courrier.getGare());
                    mapping.setProfile(courrier.getProfile());
                    mapping.setColisList(colisService.findAll(courrier.getColisNumber()));
                    mapping.setSmsEnvoyes(smsEnvoye.findAll(courrier));
                    mapping.setSuivi(courrier.getSuiviDemande());
                    mapping.setCreation(courrier.getCreation());
                    mapping.setOrdre(courrier.getOrdre());
                    mapping.setValideur(courrier.getValideur());
                    courrierMappings.add(mapping);
                }

                crier.setList(courrierMappings);
                response = ResponseEntity.status(HttpStatus.OK).body(crier);
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            log.info("[ DI-GITAL WEB :: CONTROLLER ] SEARCH COLIS RESQUEST =======================>  DONE");
            return response;
        }
    }



    @PutMapping(value = "/valider-retrait-du-colis", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = CourrierModel.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> RetraitDeColis (@RequestBody RechercheCourrierModel rech) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        SmsMessage sms  = null;

        try {

            if(rech == null){

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises."),
                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                ServiceCourrier svce = serviceCourrierService.findOne(rech.getReference(), false);

                if(svce != null){

                    if(svce.getValideur().equalsIgnoreCase(rech.getNomDest())){

                        svce.setRetrait(true);
                        svce.setVersion(svce.getVersion() + 1);
                        svce.setLastUpdate(Instant.now());
                        ServiceCourrier _svce_ = serviceCourrierService.update(svce);

                        if(_svce_.getId() == null){
                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                    new RequestResponse(
                                            new RequestInformation(400, "Impossible de faire la lib&eacute;ration du colis. Fail request."),
                                            new RequestMessage(null, "Fail")
                                    )
                            );
                        }
                        else{

                            /*Envoi des différents sms à l'expéditeur et au destinataire*/
                            String numberExp = svce.getGare().getCompagnie().getPaysAutorise().getIndicatif()+""+svce.getPhoneExpedietur();
                            String prenomsExp = WordUtils.capitalize(svce.getNomExpediteur().toLowerCase()).split(" ")[1];

                            Date myDate = Date.from(svce.getCreation());

                            if (!numberExp.isEmpty()){
                                log.info("[ DI-GITAL :: ENVOI SMS ] ::: STEP 5 --------------- ----> Envoi sms au destinataire");
                                sms = new SmsMessage();
                                sms.setTypeMessage(8L);
                                sms.setToId(numberExp);
                                sms.setContent("Bonjour "+ prenomsExp +", Votre courrier ou colis expédié le "+new SimpleDateFormat("dd/MM/yyyy").format(myDate)+" a bien été rétiré par "+svce.getNomDestinataire()+". Merci de faire confiance à "+svce.getGare().getCompagnie().getAbbrev().toUpperCase()+".");
                                sms.setFromName(svce.getNomDestinataire().toUpperCase());
                                sms.setUsername(svce.getGare().getCompagnie().getSmsCredential().getUsername());
                                sms.setPassword(svce.getGare().getCompagnie().getSmsCredential().getPassword());
                                sms.setSenderId(svce.getGare().getCompagnie().getSmsCredential().getSenderId());
                                sms.setRefCourier(svce.getColisNumber());
                                sms.setCompagnieId(svce.getGare().getCompagnie().getId());
                                jmsProducer.send(new JmsMessage("SMS to send destinataire", Converter.pojoToJson(sms), SmsMessage.class));
                            }

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(201, "Retrait du colis effectué"),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Impossible de faire la lib&eacute;ration du colis. Code validtaeur non conforme."),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new RequestResponse(
                                    new RequestInformation(400, "Impossible de faire la lib&eacute;ration du colis. Num&eacute;ro colis non ou d&eacute;j&agrave; retir&eacute;."),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }

        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            log.info("[ DI-GITAL WEB :: CONTROLLER ] SEARCH COLIS RESQUEST =======================>  DONE");
            return response;
        }
    }


    @GetMapping(value = "/find-all-courrier-retire-par-cie/{cieId}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllCourrierRetirerByCie(@PathVariable(name = "cieId") Long cieId) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(serviceCourrierService.findAlls(compagnieRoutiereService.findOne(cieId), true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PutMapping(value = "/mise-a-jour-tracking-du-colis", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = CourrierModel.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> trackingColis (@RequestBody RechercheCourrierModel rech) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        SmsMessage sms  = null;

        try {

            if(rech == null){

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises."),
                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                ServiceCourrier svce = serviceCourrierService.findOne(rech.getReference(), false);

                if(svce != null){

                    svce.setSuiviDemande(demandeService.findOne(rech.getTarckingSuivant()));
                    svce.setVersion(svce.getVersion() + 1);
                    ServiceCourrier _svce_ = serviceCourrierService.update(svce);

                    if(_svce_.getId() == null){
                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Impossible de faire la lib&eacute;ration du colis. Fail request."),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else{

                        //Envoi des différents sms à l'expéditeur
                        String numberExp = svce.getGare().getCompagnie().getPaysAutorise().getIndicatif()+""+svce.getPhoneExpedietur();
                        String prenomsExp = WordUtils.capitalize(svce.getNomExpediteur().toLowerCase()).split(" ")[1];

                        Date myDate = Date.from(svce.getCreation());

                        if (!numberExp.isEmpty() && rech.getTarckingSuivant() == 2){
                            log.info("[ DI-GITAL :: ENVOI SMS ] ::: STEP 5 --------------- ----> Envoi sms au destinataire");
                            sms = new SmsMessage();
                            sms.setTypeMessage(9L);
                            sms.setToId(numberExp);
                            sms.setContent("Bonjour "+ prenomsExp +", Votre courrier ou colis expédié à la date du "+new SimpleDateFormat("dd/MM/yyyy").format(myDate)+" est en cours d'acheminement. Merci de faire confiance à "+svce.getGare().getCompagnie().getAbbrev().toUpperCase()+".");
                            sms.setFromName(svce.getNomDestinataire().toUpperCase());
                            sms.setUsername(svce.getGare().getCompagnie().getSmsCredential().getUsername());
                            sms.setPassword(svce.getGare().getCompagnie().getSmsCredential().getPassword());
                            sms.setSenderId(svce.getGare().getCompagnie().getSmsCredential().getSenderId());
                            sms.setRefCourier(svce.getColisNumber());
                            sms.setCompagnieId(svce.getGare().getCompagnie().getId());
                            jmsProducer.send(new JmsMessage("Message sent to the Sender", Converter.pojoToJson(sms), SmsMessage.class));
                        }

                        if (!numberExp.isEmpty() && rech.getTarckingSuivant() == 3){
                            log.info("[ DI-GITAL :: ENVOI SMS ] ::: STEP 5 --------------- ----> Envoi sms au destinataire");
                            sms = new SmsMessage();
                            sms.setTypeMessage(9L);
                            sms.setToId(numberExp);
                            sms.setContent("Bonjour "+ prenomsExp +", Votre courrier ou colis expédié à la date du "+new SimpleDateFormat("dd/MM/yyyy").format(myDate)+" est arrivé à destinantion. Un sms de notification a été envoyé au destinataire. Merci de faire confiance à "+svce.getGare().getCompagnie().getAbbrev().toUpperCase()+".");
                            sms.setFromName(svce.getNomDestinataire().toUpperCase());
                            sms.setUsername(svce.getGare().getCompagnie().getSmsCredential().getUsername());
                            sms.setPassword(svce.getGare().getCompagnie().getSmsCredential().getPassword());
                            sms.setSenderId(svce.getGare().getCompagnie().getSmsCredential().getSenderId());
                            sms.setRefCourier(svce.getColisNumber());
                            sms.setCompagnieId(svce.getGare().getCompagnie().getId());
                            jmsProducer.send(new JmsMessage("Message sent to the Sender", Converter.pojoToJson(sms), SmsMessage.class));
                        }

                        //Envoi des différents sms au destinataire
                        String numberDesti = svce.getGare().getCompagnie().getPaysAutorise().getIndicatif()+""+svce.getPhoneDestinatire();
                        String prenomsDesti = WordUtils.capitalize(svce.getNomDestinataire().toLowerCase()).split(" ")[1];

                        if (!numberDesti.isEmpty() && rech.getTarckingSuivant() == 3){
                            log.info("[ DI-GITAL :: ENVOI SMS ] ::: STEP 5 --------------- ----> Envoi sms au destinataire");
                            sms = new SmsMessage();
                            sms.setTypeMessage(9L);
                            sms.setToId(numberExp);
                            sms.setContent("Bonjour "+ prenomsDesti +", Un courrier ou colis expédié par "+svce.getNomDestinataire()+" à la date du "+new SimpleDateFormat("dd/MM/yyyy").format(myDate)+" est arrivé. Nous vous invitons à venir le retirer. \nMerci de faire confiance à "+svce.getGare().getCompagnie().getAbbrev().toUpperCase()+".");
                            sms.setFromName(svce.getNomDestinataire().toUpperCase());
                            sms.setUsername(svce.getGare().getCompagnie().getSmsCredential().getUsername());
                            sms.setPassword(svce.getGare().getCompagnie().getSmsCredential().getPassword());
                            sms.setSenderId(svce.getGare().getCompagnie().getSmsCredential().getSenderId());
                            sms.setRefCourier(svce.getColisNumber());
                            sms.setCompagnieId(svce.getGare().getCompagnie().getId());
                            jmsProducer.send(new JmsMessage("Message sent to the Receiver", Converter.pojoToJson(sms), SmsMessage.class));
                        }

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Mise à jour du tracking du colis effectué"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new RequestResponse(
                                    new RequestInformation(400, "Impossible de faire la mise à jour du tracking du colis. Num&eacute;ro colis non ou d&eacute;j&agrave; retir&eacute;."),
                                    new RequestMessage(null, "Fail")
                            )
                    );
                }
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            log.info("[ DI-GITAL WEB :: CONTROLLER ] SEARCH COLIS RESQUEST =======================>  DONE");
            return response;
        }
    }
}
