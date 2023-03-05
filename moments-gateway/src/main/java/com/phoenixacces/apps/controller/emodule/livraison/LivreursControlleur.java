package com.phoenixacces.apps.controller.emodule.livraison;

import com.phoenixacces.apps.enumerations.ProfileType;
import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.jms.messages.EmailMessage;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livreurs;
import com.phoenixacces.apps.persistence.entities.module.livraison.Notifications;
import com.phoenixacces.apps.persistence.entities.service.Birthday;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.authentification.UserService;
import com.phoenixacces.apps.persistence.services.module.livraison.LivreursService;
import com.phoenixacces.apps.persistence.services.module.livraison.NotificationsServices;
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
@Api(description = "API Module Livreur - Fonctionnalité Mes Livreurs")
@Slf4j
public class LivreursControlleur {

    private final ProfileService profileService;
    private final BirthdayService birthdayService;
    private final JmsProducer jmsProducer;
    private final UserService userService;
    private final LivreursService livreursService;
    private final NotificationsServices notificationsServices;

    @Autowired
    public LivreursControlleur(
            JmsProducer jmsProducer, ProfileService profileService,
            UserService userService,
            BirthdayService birthdayService,
            LivreursService livreursService,
            NotificationsServices notificationsServices){
        this.birthdayService        = birthdayService;
        this.jmsProducer            = jmsProducer;
        this.livreursService        = livreursService;
        this.profileService         = profileService;
        this.userService            = userService;
        this.notificationsServices  = notificationsServices;
    }



    @GetMapping(value = "/find-all-livreur-module-livraison", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllLivreursByCie() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(livreursService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/find-all-livreur-module-livraison-disponible/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllLivreursDisponible(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(livreursService.findAll(true, profileService.findOne(idDigital)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/find-all-livreur-module-livraison-by-profile/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllLivreurByCie(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(livreursService.findAll(profileService.findOne(idDigital)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PutMapping(value = "/delete-item-livreur", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Livreurs.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteMesClients(@RequestBody Livreurs object) {

        try {
            if(object != null){

                Livreurs item = livreursService.findOne(object.getOrdre());

                if(item!= null) {

                    livreursService.disable(item.getId());

                    Livreurs _up_ = item;

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
                            new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getNomPrenoms()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getNomPrenoms()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




    @PutMapping(value = "/active-item-livreur", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "MesClients model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Livreurs.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeMesClients(@RequestBody Livreurs object) {

        try {
            if(object != null){

                Livreurs item = livreursService.findOne(object.getOrdre());

                if(item!= null) {

                    livreursService.enable(item.getId());

                    Livreurs up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation du type message "+object.getNomPrenoms()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type messgae "+object.getNomPrenoms()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping(value = "/deleted-all-livreur", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllMesClients() {

        try {

            List<Livreurs> items = livreursService.findAll();

            for (Livreurs item :items){

                Livreurs models = livreursService.findOne(item.getOrdre());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    livreursService.update(models);
                }
            }

            return getResponseEntity(livreursService.findAll().isEmpty());
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PostMapping(value = "/create-new-livreur", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewLivreurs (@RequestBody Livreurs model) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            Profile profile         = null;
            Profile _profile_       = null;
            User user               = null;
            User _user              = null;
            Livreurs livreurs       = null;
            Date dateChangPwd       = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.CREATED).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                //TODO :: VERIFIONS LUNICITE DU NUMERO DE TELEPHONE
                if(livreursService.findOne(model.getContact()) == null ) {

                    //TODO :: VERIFIONS LUNICITE DE L'E-MAIL DU LIVREUR
                    if(profileService.findOneEmail(model.getEmail()) == null){

                        //TODO :: VERIFIONS LUNICITE DU NUMERO DE TELEPHONE ET DU COMPTE
                        if(livreursService.findOne(model.getNomPrenoms().toUpperCase(), model.getContact()) == null ) {

                            System.out.println(">>>>>>>>>>>> TODO 0 :: CREATION DE L'IDENTITE NUMERIQUE DU LIVREUR <<<<<<<<<<<<<<");

                            //TODO :: CREATION DE L'IDENTITE NUMERIQUE DU LIVREUR

                            livreurs = new Livreurs();

                            livreurs.setOrdre(Utils.generateRandom(8));

                            livreurs.setContact(model.getContact());

                            livreurs.setNotifAnniv(model.isNotifAnniv());

                            livreurs.setEntreprise(model.getEntreprise());

                            livreurs.setProfile(model.getProfile());

                            livreurs.setUsername(model.getUsername());

                            livreurs.setNomPrenoms(model.getNomPrenoms().toUpperCase());

                            livreurs.setEntreprise(model.getEntreprise());

                            livreurs.setTypeContrat(model.getTypeContrat());

                            livreurs.setTypeEngin(model.getTypeEngin());

                            livreurs.setAssureEngin(model.getAssureEngin());

                            livreurs.setCompagnieAssurance(model.getCompagnieAssurance());

                            livreurs.setDateFinAssurance(model.getDateFinAssurance());

                            livreurs.setTauxComm(model.getTauxComm() != 0 ? model.getTauxComm()/100 : 0);

                            Livreurs _livreurs = livreursService.create(livreurs);

                            if (_livreurs.getId() == null) {

                                response = ResponseEntity.status(HttpStatus.CREATED).body(

                                        new RequestResponse(

                                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                                new RequestMessage(null, null)
                                        )
                                );
                            }
                            else {

                                //TODO :: ENREGISTREMENT DE LA NOTIFICATION POUR L'ASSURANCE
                                System.out.println(">>>>>>>>>>>> TODO 1 :: ENREGISTREMENT DE LA NOTIFICATION <<<<<<<<<<<<<<");
                                if(model.getAssureEngin() != null && model.getAssureEngin().equalsIgnoreCase("OUI")){

                                    Calendar calendar = Calendar.getInstance();

                                    calendar.add(Calendar.DAY_OF_MONTH, -5);

                                    Date dateEnvoi =  calendar.getTime();


                                    Notifications notifications = new Notifications();

                                    notifications.setTypeNotification(TypeNotification.ASSURANCE);

                                    notifications.setDateEnvoi(dateEnvoi);

                                    notifications.setNomDestinataire(model.getProfile().getGareRoutiere().getCompagnie().getResponsable());

                                    notifications.setContact(model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+model.getProfile().getGareRoutiere().getCompagnie().getContactResponsable());

                                    notifications.setNomLivreur(model.getNomPrenoms().toUpperCase());

                                    notifications.setModule(model.getProfile().getModule());

                                    notificationsServices.create(notifications);
                                }

                                //TODO :: VERIFIONS L'EXISTANCE DU PROFIL //e.getGareRoutiere(), e.getPhone(),true, e.getProfileType()
                                _profile_ = profileService.findOneProfile(model.getProfile().getGareRoutiere(), model.getContact(), ProfileType.LIVREUR);

                                if(_profile_ == null){

                                    //TODO :: CREATION DU PROFILE DU LIVREUR
                                    System.out.println(">>>>>>>>>>>> TODO 2 :: CREATION DU PROFILE DU LIVREUR <<<<<<<<<<<<<<");
                                    //Creation du profile utilistauer
                                    profile = new Profile();

                                    profile.setNomPrenoms(model.getNomPrenoms().toUpperCase());

                                    profile.setPhone(model.getContact());

                                    profile.setProfileType(ProfileType.LIVREUR);

                                    profile.setBirthdate(model.getBirthdate());

                                    profile.setEmail(model.getEmail());

                                    profile.setGareRoutiere(model.getProfile().getGareRoutiere());

                                    profile.setModule(model.getProfile().getModule());

                                    _profile_ = profileService.create(profile);

                                    if (_profile_.getIdDigital() == null) {

                                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                                new RequestResponse(
                                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                                        new RequestMessage(null, null)
                                                )
                                        );
                                    }
                                    else {

                                        Calendar calendar = Calendar.getInstance();
                                        calendar.add(Calendar.MONTH, 4);
                                        dateChangPwd =  calendar.getTime();

                                        //TODO :: CREATION DU COMPTE DIGIT-AL WEB DU LIVREUR
                                        System.out.println(">>>>>>>>>>>> TODO 3 :: CREATION DU COMPTE DIGIT-AL WEB DU LIVREUR <<<<<<<<<<<<<<");
                                        if(userService.findOne(_profile_) == null){

                                            user = new User();

                                            user.setProfile(_profile_);

                                            user.setUsername(model.getUsername());

                                            user.setRoles(ProfileType.PARTENAIRE.getLabel());

                                            user.setDateChangPwd(dateChangPwd);

                                            _user = userService.create(user);
                                        }

                                        if(_user.getId() != null){

                                            //TODO :: ENREGISTREMENT DE LA DATE D'ANNIVERSAIRE DU LIVREUR
                                            System.out.println(">>>>>>>>>>>> TODO 4 :: ENREGISTREMENT DE LA DATE D'ANNIVERSAIRE DU LIVREUR <<<<<<<<<<<<<<");

                                            if(model.isNotifAnniv() && model.getBirthdate() != null){


                                                String birthDay = model.getBirthdate().toString().length() > 1 ? model.getBirthdate().toString().split("-")[2]+"/"+model.getBirthdate().toString().split("-")[1] : model.getBirthdate().toString().split("-")[0];
                                                String concerne = WordUtils.capitalize(model.getNomPrenoms().toLowerCase());

                                                if(birthdayService.findeOne(birthDay, model.getNomPrenoms(), ""+Calendar.YEAR) == null){

                                                    Birthday birthday = new Birthday();

                                                    birthday.setBirthday(model.getBirthdate().toString().split("-")[2]+"/"+model.getBirthdate().toString().split("-")[1]);

                                                    birthday.setAnniversaireux(concerne);

                                                    birthday.setAnnee(""+Calendar.YEAR);

                                                    birthday.setEnvoi(false);

                                                    birthdayService.create(birthday);
                                                }
                                            }

                                            String number       = model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+model.getContact();
                                            String promReceiver = WordUtils.capitalize(model.getNomPrenoms().toLowerCase()).split(" ")[1];

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
                                    }
                                }
                                else{

                                    response = ResponseEntity.status(HttpStatus.CREATED).body(
                                        new RequestResponse(
                                            new RequestInformation(201, "Nous avons pas pu créer le profil de ce livreur car il en a possède déjà un."),
                                            new RequestMessage(null, null)
                                        )
                                    );
                                }
                            }
                        }
                        else{

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Le compte de ce livreur a déjà été créer. Nous vous invitons à consulter la liste mise à disposition"),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }

                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                            new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "L'adresse e-mail "+model.getEmail().toLowerCase()+" utilisé a déjà été utilisé par un autre livreur"),
                                new RequestMessage(null, null)
                            )
                        );
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.CREATED).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Le numéro de téléphone "+model.getContact()+" utilisé a déjà été utilisé par un autre livreur"),
                                    new RequestMessage(null, null)
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



    @PutMapping(value = "/update-livreur", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteLivreurs(@RequestBody Livreurs item) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            //System.out.println(item.toString());
            if(item == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                Livreurs up_data = livreursService.findOneByOndre(item.getOrdre());

                if(up_data != null) {

                    up_data.setNomPrenoms(item.getNomPrenoms().toUpperCase());

                    up_data.setContact(item.getContact());

                    up_data.setEntreprise(item.getEntreprise());

                    livreursService.update(up_data);

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



    @PutMapping(value = "/update-livreur-disponibilite", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteDisponibiliteLivreurs(@RequestBody Livreurs item) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            System.out.println(item.toString());
            if(item == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                Livreurs up_data = livreursService.findOneByOndre(item.getOrdre());

                if(up_data != null) {

                    up_data.setDisponible(item.isDisponible());

                    livreursService.update(up_data);

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
