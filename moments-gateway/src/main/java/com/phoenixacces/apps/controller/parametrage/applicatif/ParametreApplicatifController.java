package com.phoenixacces.apps.controller.parametrage.applicatif;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.models.courrier.RecherchePaiementModel;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.OffreSmsEntreprise;
import com.phoenixacces.apps.persistence.entities.parametrage.RechargementCompteSms;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.entities.service.SmsEnvoye;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.parametrage.*;
import com.phoenixacces.apps.producer.JmsProducer;
import com.phoenixacces.apps.utiles.Utils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API parametrage applicatif du logiciel")
@Slf4j
public class ParametreApplicatifController {

    private final RechargementCompteSmsService rechargementCompteSmsService;
    private final JmsProducer jmsProducer;
    private final ProfileService profileService;
    private final SMSPhoenixAccesService smsPhoenixAccesService;
    private final TypeMessageService typeMessageService;
    private final MotifSuspensionCollaborationService motifSuspension;
    private final SmsCredentialService smsCredentialService;
    private final OffreEntrepriseService offreEntrepriseService;

    @Autowired
    public ParametreApplicatifController(
            RechargementCompteSmsService rechargementCompteSmsService,
            JmsProducer jmsProducer,
            ProfileService profileService,
            SMSPhoenixAccesService smsPhoenixAccesService,
            TypeMessageService typeMessageService,
            MotifSuspensionCollaborationService motifSuspension,
            SmsCredentialService smsCredentialService,
            OffreEntrepriseService offreEntrepriseService
    ) {
        this.rechargementCompteSmsService   = rechargementCompteSmsService;
        this.jmsProducer                    = jmsProducer;
        this.profileService                 = profileService;
        this.smsPhoenixAccesService         = smsPhoenixAccesService;
        this.typeMessageService             = typeMessageService;
        this.motifSuspension                = motifSuspension;
        this.smsCredentialService           = smsCredentialService;
        this.offreEntrepriseService         = offreEntrepriseService;
    }

    @Value("${api.sms.gateway}")
    private String gateway;

    @Value("${numero.digital}")
    private String numDigital;


    @GetMapping(value = "/find-all-type-offre-sms-entreprise", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllOffreEntreprise() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(offreEntrepriseService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-rechargement-compte-sms", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllRechargement() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(rechargementCompteSmsService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-rechargement-compte-sms-by-entrpise/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllRechargementByEntreprise(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            Profile profile = profileService.findOne(idDigital);

            if(profile != null){

                response = ResponseEntity.status(HttpStatus.OK).body(rechargementCompteSmsService.findAll(profile.getGareRoutiere().getCompagnie()));

            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
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


    @GetMapping(value = "/find-all-rechargement-compte-sms/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllRechargementByProfil(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            Profile profile = profileService.findOne(idDigital);

            if(profile != null){

                response = ResponseEntity.status(HttpStatus.OK).body(rechargementCompteSmsService.findAll(profile));

            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
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


    @PostMapping(value = "/create-new-rechargement-sms-account", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<Object> createdNewRechargementSmsAccount (@RequestBody RechargementCompteSms compteSms) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        String number                   = null;
        String numberDigital            = null;

        try {

            if(compteSms == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises")
                );
            }
            else {

                //Vérification de l'existance du profil
                Profile profile = profileService.findOne(compteSms.getProfile().getIdDigital());

                if(profile == null){

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Votre profil n'est pas configuré sur la plate-forme. Veuillez-vous rapprocher de votre administrateur")
                    );
                }
                else{

                    //VERIFIONS L'EXISTANCE DE LA REFERENCE
                    if(rechargementCompteSmsService.findOne(compteSms.getRefPaiement()) == null){

                        RechargementCompteSms rechargementCompteSms = new RechargementCompteSms();

                        rechargementCompteSms.setNbreSms(compteSms.getNbreSms());

                        rechargementCompteSms.setOrdre(Utils.generateCodeValidateur(6));

                        rechargementCompteSms.setEntreprises(compteSms.getProfile().getGareRoutiere().getCompagnie());

                        rechargementCompteSms.setProfile(compteSms.getProfile());

                        rechargementCompteSms.setTypeFormule(compteSms.getTypeFormule().toUpperCase());

                        rechargementCompteSms.setNomFormule(compteSms.getNomFormule().toUpperCase());

                        rechargementCompteSms.setNumPayeur(compteSms.getNumPayeur());

                        rechargementCompteSms.setRefPaiement(compteSms.getRefPaiement().toUpperCase());

                        rechargementCompteSms.setTypePaiement(compteSms.getTypePaiement());

                        rechargementCompteSms.setMontantRechargement(compteSms.getMontantRechargement());

                        rechargementCompteSms.setTaxeApplique(compteSms.getTotalPayer() - compteSms.getMontantRechargement());

                        rechargementCompteSms.setTotalPayer(compteSms.getTotalPayer());

                        if (rechargementCompteSmsService.create(rechargementCompteSms) == null) {

                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                    new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                            );
                        }
                        else {

                            response = ResponseEntity.status(HttpStatus.CREATED).body(

                                    new RequestInformation(HttpStatus.CREATED.value(), "Succes")
                            );

                            notifyActeur(compteSms, profile);
                        }

                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "La référence de paiement saisie existe déjà dans le système.")
                        );
                    }
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

    private void notifyActeur(RechargementCompteSms compteSms, Profile profile) throws UnirestException {
        String number;
        String numberDigital;
        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

            number          = "225"+ compteSms.getProfile().getPhone();

            numberDigital   = "225"+numDigital;

        }else{

            number          = compteSms.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+ compteSms.getProfile().getPhone();

            numberDigital   = compteSms.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+numDigital;
        }


        //ENVOI SMS DE NOTIFICATION AU CLIENT
        String nomReceiver = WordUtils.capitalize(compteSms.getProfile().getNomPrenoms().toLowerCase());

        String message = "Bonjour "+ nomReceiver +", La demande de rechargement de votre compte a été effectuée avec succès. La requete est encours de traitement.";

        SmsEnvoye smsEnvoye = new SmsEnvoye();

        smsEnvoye.setTypeMessage(typeMessageService.findById(27L));

        smsEnvoye.setNumeroDestinataire(number);

        smsEnvoye.setDestinataire(profile.getNomPrenoms().toUpperCase());

        smsEnvoye.setCorpsMessage(message);

        smsPhoenixAccesService.smsSendToDigitalWeb(smsEnvoye);


        //ENVOI SMS DE NOTIFICATION AU GESTIONNAIRE DE DI-GITAL WEB
        String msg = "Une demande de "+WordUtils.capitalize(typeMessageService.findById(27L).getTypemessgae().toLowerCase())+" a été effectuée par "+ compteSms.getProfile().getGareRoutiere().getGareRoutiere()+". La reférence du paiement est "+ compteSms.getRefPaiement().toUpperCase()+" et le numéro marchand est "+ compteSms.getNumPayeur()+".";

        SmsEnvoye sms = new SmsEnvoye();

            sms.setTypeMessage(typeMessageService.findById(27L));

            sms.setNumeroDestinataire(numberDigital);

            sms.setDestinataire("DI-GITAL WEB ACTOR");

            sms.setCorpsMessage(msg);

        smsPhoenixAccesService.smsSendToDigitalWeb(sms);
    }


    @PutMapping(value = "/update-rechargement-sms-account", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = Entreprises.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteRechargementSmsAccount(@RequestBody RechargementCompteSms item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        String numberDigital            = null;

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises")
                );
            }
            else {

                RechargementCompteSms up = rechargementCompteSmsService.findOne(item.getId());

                if(up!= null) {

                    up.setNbreSms(item.getNbreSms());

                    up.setNumPayeur(item.getNumPayeur());

                    up.setRefPaiement(item.getRefPaiement());

                    up.setTypePaiement(item.getTypePaiement());

                    up.setMontantRechargement(item.getMontantRechargement());

                    up.setTaxeApplique(item.getTaxeApplique());

                    up.setTotalPayer(item.getTotalPayer());

                    if (rechargementCompteSmsService.update(up) == null) {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                        );
                    }
                    else {

                        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                            numberDigital   = "225"+numDigital;

                        }else{

                            numberDigital   = item.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+numDigital;
                        }

                        //ENVOI SMS DE NOTIFICATION AU GESTIONNAIRE DE DI-GITAL WEB
                        String msg = "Une modification a été appliquée à la demande de "+typeMessageService.findById(27L).getTypemessgae()+" de l'entreprise "+item.getProfile().getGareRoutiere().getGareRoutiere()+". Prière prendre cela en compte.";

                        SmsEnvoye sms = new SmsEnvoye();

                        sms.setTypeMessage(typeMessageService.findById(27L));

                        sms.setNumeroDestinataire(numberDigital);

                        sms.setDestinataire("DI-GITAL WEB ACTOR");

                        sms.setCorpsMessage(msg);

                        smsPhoenixAccesService.smsSendToDigitalWeb(sms);


                        response = ResponseEntity.status(HttpStatus.CREATED).body(

                                new RequestInformation(HttpStatus.CREATED.value(), "Succes")
                        );
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "L'enregistrement demandé n'a pas été retrouvé.")
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


    @PutMapping(value = "/traitement-requete-rechargement-sms-account", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = Entreprises.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> processingRechargementSmsAccount(@RequestBody RechargementCompteSms item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises")
                );
            }
            else {

                RechargementCompteSms rechargementCompteSms = rechargementCompteSmsService.findOne(item.getRefPaiement());

                if(rechargementCompteSms!= null) {

                    rechargementCompteSms.setStatut(item.getStatut());

                    rechargementCompteSms.setMotif(item.getStatut() == EtatLecture.TRAITE ?  motifSuspension.findOne(8L) : motifSuspension.findOne(4L));

                    RechargementCompteSms recahrge = rechargementCompteSmsService.update(rechargementCompteSms);

                    if (recahrge.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                        );
                    }
                    else {

                        notifActor(rechargementCompteSms);


                        //Rechargement du compte
                        rechargementSmsAccount(item, rechargementCompteSms);


                        response = ResponseEntity.status(HttpStatus.OK).body(

                                new RequestInformation(HttpStatus.OK.value(), "Succes")
                        );
                    }
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

    private void rechargementSmsAccount(RechargementCompteSms item, RechargementCompteSms rechargementCompteSms) {

        if(item.getStatut() == EtatLecture.TRAITE){

            SmsCredential smsCredential = smsCredentialService.findOne(rechargementCompteSms.getEntreprises().getSmsCredential().getSenderId());

            smsCredential.setNombreSms(smsCredential.getNombreSms() + rechargementCompteSms.getNbreSms());

            smsCredential.setNombreSmsLeTexto(smsCredential.getNombreSmsLeTexto() + rechargementCompteSms.getNbreSms());

            smsCredentialService.updated(smsCredential);
        }
    }

    private void notifActor(RechargementCompteSms rechargementCompteSms) throws UnirestException {

        String number;

        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

            number          = "225"+ rechargementCompteSms.getProfile().getPhone();

        }else{

            number          = rechargementCompteSms.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+ rechargementCompteSms.getProfile().getPhone();
        }

        String stat         = rechargementCompteSms.getStatut() == EtatLecture.TRAITE ? "traité" : "rejeté";

        String msgSuite     = rechargementCompteSms.getStatut() == EtatLecture.TRAITE ? "Votre compte sms a été rechargé de "+ rechargementCompteSms.getNbreSms()+" sms." : "Votre compte sms n'a pas été rechargé.";

        //ENVOI SMS DE NOTIFICATION AU CLIENT
        String nomReceiver = WordUtils.capitalize(rechargementCompteSms.getProfile().getNomPrenoms().toLowerCase());

        String message = "Bonjour "+ nomReceiver +", votre demande de rechargement de votre compte a été "+stat+" avec succès. "+msgSuite+"";

        SmsEnvoye smsEnvoye = new SmsEnvoye();

        smsEnvoye.setTypeMessage(typeMessageService.findById(27L));

        smsEnvoye.setNumeroDestinataire(number);

        smsEnvoye.setDestinataire(rechargementCompteSms.getProfile().getNomPrenoms().toUpperCase());

        smsEnvoye.setCorpsMessage(message);

        smsPhoenixAccesService.smsSendToDigitalWeb(smsEnvoye);
    }


    @PostMapping(value = "/recherche-paiement-en-attente", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> recherchePaiement (@RequestBody RecherchePaiementModel rechModel) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            List<RechargementCompteSms> rechargementCompteSms = null;

            if(rechModel.getRefPaiement() == null && rechModel.getNumPayeur() == null){

                response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(

                        new RequestResponse(
                                new RequestInformation(400, "Impossible de retrouver le colis pour acheminement avec les références données"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else{

                if(rechModel.getRefPaiement() != null && rechModel.getNumPayeur() != null){

                    rechargementCompteSms = rechargementCompteSmsService.findOneByRefPaiement(rechModel.getRefPaiement().toUpperCase());

                }

                else if(rechModel.getRefPaiement() == null && rechModel.getNumPayeur() != null){

                    rechargementCompteSms = rechargementCompteSmsService.findAllByNumPayeur(rechModel.getNumPayeur());
                }

                else if(rechModel.getRefPaiement() != null && rechModel.getNumPayeur() == null){

                    rechargementCompteSms = rechargementCompteSmsService.findOneByRefPaiement(rechModel.getRefPaiement().toUpperCase());
                }

                if(rechargementCompteSms != null){

                    response = ResponseEntity.status(HttpStatus.OK).body(rechargementCompteSms);
                }
                else{

                    response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new RequestResponse(
                                    new RequestInformation(400, "Impossible de retrouver la/les demande(s) de rechargement avec les références données"),
                                    new RequestMessage(null, null)
                            )
                    );
                }
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



    @GetMapping(value = "/find-all-offre-entreprise-sms", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllOffreSmsEntreprise() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(offreEntrepriseService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-offre-entreprise-sms", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewOffreSmsEntreprise (@RequestBody OffreSmsEntreprise model) {

        try {
            OffreSmsEntreprise data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(offreEntrepriseService.findOne(model.getNomFormule(), model.getTypeFormule()) == null ) {

                    data = new OffreSmsEntreprise();

                    data.setTypeFormule(model.getTypeFormule().toUpperCase());

                    data.setNomFormule(model.getNomFormule().toUpperCase());

                    data.setTotalPayer(model.getTotalPayer());

                    data.setTaxeApplique(model.getTaxeApplique());

                    data.setMontantRechargement(model.getMontantRechargement());

                    data.setNbreSms(model.getNbreSms());

                    data.setOrdre(Utils.generateCodeValidateur(6));


                    if (offreEntrepriseService.create(data).getId() == null) {

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
                                    new RequestInformation(400, "Données existante, Doublons évité"),
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



    @PutMapping(value = "/update-offre-entreprise-sms", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = OffreSmsEntreprise.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteOffreSmsEntreprise(@RequestBody OffreSmsEntreprise item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                OffreSmsEntreprise up_data = offreEntrepriseService.findOne(item.getId());

                if(up_data!= null) {

                    up_data.setNbreSms(item.getNbreSms());

                    up_data.setMontantRechargement(item.getMontantRechargement());

                    up_data.setTotalPayer(item.getTotalPayer());

                    up_data.setTaxeApplique(item.getTaxeApplique());

                    if (offreEntrepriseService.update(up_data).getId() == null) {

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



    @PutMapping(value = "/delete-item-offre-entreprise-sms", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = OffreSmsEntreprise.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteOffreSmsEntreprise(@RequestBody OffreSmsEntreprise object) {

        try {
            if(object != null){

                OffreSmsEntreprise item = offreEntrepriseService.findOne(object.getId());
                if(item!= null) {
                    offreEntrepriseService.disable(item.getId());
                    OffreSmsEntreprise up = item;
                    if (up.getId() == null) {
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
                            new RequestInformation(400, "Impossible de faire la suppression de la formule "+object.getNomFormule().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de la formule "+object.getNomFormule().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-offre-entreprise-sms", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "OffreSmsEntreprise model", notes = "OffreSmsEntreprise model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = OffreSmsEntreprise.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeOffreSmsEntreprise(@RequestBody OffreSmsEntreprise object) {

        try {
            if(object != null){

                OffreSmsEntreprise item = offreEntrepriseService.findOne(object.getId());
                if(item!= null) {
                    offreEntrepriseService.enable(item.getId());

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
                            new RequestInformation(400, "Impossible de faire l'activation de la formule "+object.getNomFormule().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de la formule "+object.getNomFormule().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-offre-entreprise-sms", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllOffreSmsEntreprise() {

        try {

            List<OffreSmsEntreprise> items = offreEntrepriseService.findAll();

            for (OffreSmsEntreprise item :items){

                OffreSmsEntreprise models = offreEntrepriseService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    offreEntrepriseService.update(models);
                }
            }

            return getResponseEntity(offreEntrepriseService.findAll().isEmpty());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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


    @PostMapping(value = "/create-new-rechargement-sms-account-entreprise", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<Object> createdNewRechargementSmsAccountEtreprise (@RequestBody RechargementCompteSms compteSms) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        String number                   = null;
        String numberDigital            = null;

        try {

            if(compteSms == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises")
                );
            }
            else {

                //Vérification de l'existance du profil
                Profile profile = profileService.findOne(compteSms.getProfile().getIdDigital());

                if(profile == null){

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Votre profil n'est pas configuré sur la plate-forme. Veuillez-vous rapprocher de votre administrateur")
                    );
                }
                else{

                    //VERIFIONS L'EXISTANCE DE LA REFERENCE
                    if(rechargementCompteSmsService.findOne(compteSms.getRefPaiement().toUpperCase()) == null){

                        RechargementCompteSms rechargementCompteSms = new RechargementCompteSms();

                        rechargementCompteSms.setNbreSms(compteSms.getOffreSmsEntreprise().getNbreSms());

                        rechargementCompteSms.setOrdre(Utils.generateCodeValidateur(6));

                        rechargementCompteSms.setEntreprises(compteSms.getProfile().getGareRoutiere().getCompagnie());

                        rechargementCompteSms.setProfile(compteSms.getProfile());

                        rechargementCompteSms.setTypeFormule(compteSms.getOffreSmsEntreprise().getTypeFormule().toUpperCase());

                        rechargementCompteSms.setNomFormule(compteSms.getOffreSmsEntreprise().getTypeFormule()+" - "+compteSms.getOffreSmsEntreprise().getNomFormule());

                        rechargementCompteSms.setNumPayeur(compteSms.getNumPayeur());

                        rechargementCompteSms.setRefPaiement(compteSms.getRefPaiement().toUpperCase());

                        rechargementCompteSms.setTypePaiement(compteSms.getTypePaiement());

                        rechargementCompteSms.setMontantRechargement(compteSms.getOffreSmsEntreprise().getMontantRechargement());

                        rechargementCompteSms.setTaxeApplique(compteSms.getOffreSmsEntreprise().getTaxeApplique());

                        rechargementCompteSms.setTotalPayer(compteSms.getOffreSmsEntreprise().getTotalPayer());

                        if (rechargementCompteSmsService.create(rechargementCompteSms) == null) {

                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                    new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies")
                            );
                        }
                        else {

                            response = ResponseEntity.status(HttpStatus.CREATED).body(

                                    new RequestInformation(HttpStatus.CREATED.value(), "Succes")
                            );

                            notifyActeur(compteSms, profile);
                        }

                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "La référence de paiement saisie existe déjà dans le système.")
                        );
                    }
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

}
