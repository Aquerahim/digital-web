package com.phoenixacces.apps.controller.emodule.livraison;

import com.phoenixacces.apps.jms.messages.EmailMessage;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.entities.module.livraison.PartenaireAffaire;
import com.phoenixacces.apps.persistence.entities.service.Birthday;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.authentification.UserService;
import com.phoenixacces.apps.persistence.services.module.livraison.MesClientsService;
import com.phoenixacces.apps.persistence.services.parametrage.BirthdayService;
import com.phoenixacces.apps.producer.JmsProducer;
import com.phoenixacces.apps.utiles.Utils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Module Livraison - Fonctionnalité Mes Clients")
@Slf4j
public class ClientsMdleClientsController {

    private final ProfileService profileService;
    private final BirthdayService birthdayService;
    private final JmsProducer jmsProducer;
    private final UserService userService;
    private final MesClientsService mesClientsService;

    @Autowired
    public ClientsMdleClientsController(
            JmsProducer jmsProducer, ProfileService profileService,
            UserService userService,
            BirthdayService birthdayService,
            MesClientsService mesClientsService){
        this.birthdayService = birthdayService;
        this.jmsProducer = jmsProducer;
        this.mesClientsService = mesClientsService;
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping(value = "/find-all-clients-module-livraison", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllClientsMdleLivraisonByCie() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(mesClientsService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-clients-module-livraison-by-profile/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllClientsMdleByCie(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(mesClientsService.findAll(profileService.findOne(idDigital)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-partaires-d-affaire", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewMesClientsMdle (@RequestBody PartenaireAffaire model) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            
            PartenaireAffaire data = null;
            Profile profile = null;
            User user       = null;
            User _user      = null;

            //System.out.println(">>>>>>> >>>> >>>>>>> >>>> >>>>>>> >>>>" + model.toString());

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.CREATED).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                if(mesClientsService.findOne(model.getNomComplet(), model.getTypeClient()) == null ) {

                    data = new PartenaireAffaire();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setNomComplet(model.getNomComplet().toUpperCase());

                    data.setTypeClient(model.getTypeClient());

                    data.setActivite(model.getActivite().toUpperCase());

                    data.setContact(model.getContact());

                    data.setBirthdate(model.getBirthdate());

                    data.setZoneCouverture(model.getZoneCouverture());

                    data.setSituationGeo(model.getSituationGeo().toUpperCase());

                    data.setPrecisonZone(model.getPrecisonZone());

                    data.setNomResponsable(model.getNomResponsable().toUpperCase());

                    data.setContactResponsable(model.getContactResponsable());

                    data.setNotifAnniv(model.isNotifAnniv());

                    data.setConnected(model.isConnected());

                    data.setEntreprise(model.getEntreprise());

                    data.setProfile(model.getProfile());

                    data.setEmail(model.getEmail().toLowerCase());

                    data.setLiaison(model.isConnected() ? model.getUsername() : "");

                    PartenaireAffaire mesClients = mesClientsService.create(data);

                    if (mesClients.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(

                                new RequestResponse(

                                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                    else {

                        //System.out.println(">>>>>>>>>>>>>>>>>>> IS ACCES >>>>>>>>>>>>>>>>>>>" + mesClients.isConnected());

                        if(mesClients.isConnected()){

                            //System.out.println(">>>>>>>>>>>>>>>>>>> CREATION DU PROFIL >>>>>>>>>>>>>>>>>>>");

                            //Mise en place de la durée d'expiration du compte
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.MONTH, 4);
                            Date dateChangPwd =  calendar.getTime();

                            //Creation du profile utilistauer
                            profile = new Profile();

                            profile.setNomPrenoms(model.getNomComplet().toUpperCase());

                            profile.setPhone(model.getContact());

                            profile.setProfileType(model.getProfileType());

                            profile.setBirthdate(model.getBirthdate());

                            profile.setEmail(model.getEmail());

                            profile.setGareRoutiere(model.getProfile().getGareRoutiere());

                            profile.setModule(model.getProfile().getModule());

                            Profile _profile_ = profileService.create(profile);

                            if (_profile_.getIdDigital() == null) {

                                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                    new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, null)
                                    )
                                );
                            }
                            else {

                                mesClients.setLiaison(_profile_.getIdDigital());

                                mesClientsService.update(mesClients);

                                if(mesClients.isNotifAnniv() && model.getBirthdate() != null){

                                    String birthDay = model.getBirthdate().toString().split("-")[2]+"/"+model.getBirthdate().toString().split("-")[1];
                                    String concerne = WordUtils.capitalize(model.getNomComplet().toLowerCase());

                                    if(birthdayService.findeOne(birthDay, model.getNomComplet(), ""+Calendar.YEAR) == null){

                                        Birthday birthday = new Birthday();

                                        birthday.setBirthday(model.getBirthdate().toString().split("-")[2]+"/"+model.getBirthdate().toString().split("-")[1]);

                                        birthday.setAnniversaireux(concerne);

                                        birthday.setAnnee(""+Calendar.YEAR);

                                        birthday.setEnvoi(false);

                                        birthdayService.create(birthday);
                                    }
                                }

                                //Création du compte utilsiateur
                                if(userService.findOne(_profile_) == null){

                                    user = new User();

                                    user.setProfile(_profile_);

                                    user.setUsername(model.getUsername());

                                    user.setRoles(model.getProfileType().getLabel());

                                    user.setDateChangPwd(dateChangPwd);

                                    _user = userService.create(user);
                                }

                                if(_user.getId() != null){

                                    String number       = model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+model.getContact();
                                    String promReceiver = WordUtils.capitalize(model.getNomComplet().toLowerCase()).split(" ")[1];

                                    if (!number.isEmpty()){

                                        SmsMessage sms = new SmsMessage();

                                        sms.setTypeMessage(1L);

                                        sms.setToId(number);

                                        sms.setContent("Bonjour "+ promReceiver +" et Bienvenue sur Di-Gital web. Vos identifiants de connexion à l'application vous a été envoyé sur votre e-mail donné.");

                                        sms.setFromName(profile.getNomPrenoms().toUpperCase());

                                        sms.setUsername(profile.getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                        sms.setPassword(profile.getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                        sms.setSenderId(profile.getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                        jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
                                    }

                                    if(!profile.getEmail().isEmpty()){

                                        EmailMessage email = new EmailMessage();

                                        email.setEmail(model.getEmail());

                                        email.setSubject("IDENTIFIANT DE CONNEXION - DI-GITAL Web");

                                        email.setType("RECORDING");

                                        email.setUsername(model.getUsername().toUpperCase());

                                        email.setDefaulPwd("di-gital");

                                        jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                                    }

                                    response = ResponseEntity.status(HttpStatus.CREATED).body(
                                            new RequestResponse(
                                                    new RequestInformation(201, "Successfully created data with creadential account user"),
                                                    new RequestMessage(null, null)
                                            )
                                    );
                                }
                                else{

                                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                            new RequestResponse(
                                                    new RequestInformation(400, "Cet enregistrement a déjà été faite. Doublons évité"),
                                                    new RequestMessage(null, "Fail")
                                            )
                                    );
                                }

                            }
                        }
                        else{

                            //Enregistrement HBD
                            if(mesClients.isNotifAnniv() && model.getBirthdate() != null){

                                String birthDay = model.getBirthdate().toString().split("-")[2]+"/"+model.getBirthdate().toString().split("-")[1];
                                String concerne = WordUtils.capitalize(model.getNomComplet().toLowerCase());

                                if(birthdayService.findeOne(birthDay, model.getNomComplet(), ""+Calendar.YEAR) == null){

                                    Birthday birthday = new Birthday();

                                    birthday.setBirthday(model.getBirthdate().toString().split("-")[2]+"/"+model.getBirthdate().toString().split("-")[1]);

                                    birthday.setAnniversaireux(concerne);

                                    birthday.setAnnee(""+Calendar.YEAR);

                                    birthday.setEnvoi(false);

                                    birthdayService.create(birthday);
                                }
                            }

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                    new RequestInformation(201, "Successfully created data not creadential account"),
                                    new RequestMessage(null, null)
                                )
                            );
                        }
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                            new RequestInformation(400, "Cet enregistrement a déjà été faite. Doublons évité"),
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



    @PutMapping(value = "/update-mes-clients", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = PartenaireAffaire.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteMesClientsMdle(@RequestBody PartenaireAffaire item) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(item == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                PartenaireAffaire up_data = mesClientsService.findOne(item.getOrdre());

                if(up_data != null) {

                    up_data.setNomComplet(item.getNomComplet().toUpperCase());

                    up_data.setTypeClient(item.getTypeClient());

                    up_data.setActivite(item.getActivite().toUpperCase());

                    up_data.setContact(item.getContact());

                    up_data.setZoneCouverture(item.getZoneCouverture());

                    up_data.setSituationGeo(item.getSituationGeo().toUpperCase());

                    up_data.setPrecisonZone(item.getPrecisonZone().toUpperCase());

                    up_data.setNomResponsable(item.getNomResponsable().toUpperCase());

                    up_data.setContactResponsable(item.getContactResponsable());

                    mesClientsService.update(up_data);

                    if (up_data.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.OK).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully updated data"),
                                        new RequestMessage(null, "Success")
                                )
                        );
                    }
                }
                else {

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

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }



    @PutMapping(value = "/delete-item-mes-clients", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = PartenaireAffaire.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteMesClientsMdle(@RequestBody PartenaireAffaire object) {

        try {
            if(object != null){

                PartenaireAffaire item = mesClientsService.findOne(object.getOrdre());

                if(item!= null) {

                    mesClientsService.disable(item.getId());

                    PartenaireAffaire _up_ = item;

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
                            new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getNomComplet()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getNomComplet()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-mes-clients", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "MesClients model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = PartenaireAffaire.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeMesClientsMdle(@RequestBody PartenaireAffaire object) {

        try {
            if(object != null){

                PartenaireAffaire item = mesClientsService.findOne(object.getOrdre());

                if(item!= null) {

                    mesClientsService.enable(item.getId());

                    PartenaireAffaire up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation du type message "+object.getNomComplet()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type messgae "+object.getNomComplet()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-mes-clients", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllMesClientsMdle() {

        try {

            List<PartenaireAffaire> items = mesClientsService.findAll();

            for (PartenaireAffaire item :items){

                PartenaireAffaire models = mesClientsService.findOne(item.getOrdre());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    mesClientsService.update(models);
                }
            }

            return getResponseEntity(mesClientsService.findAll().isEmpty());
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @GetMapping(value = "/get-client-by-profile-liaison/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> getClientByLiaisonProfile(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(mesClientsService.findOne(idDigital));
        }
        catch (Exception e) {
            // TODO: handle exception
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
