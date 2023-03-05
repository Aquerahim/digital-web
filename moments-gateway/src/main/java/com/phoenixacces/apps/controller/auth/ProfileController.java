package com.phoenixacces.apps.controller.auth;

import com.phoenixacces.apps.jms.messages.EmailMessage;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.models.user.CompteUtilisateur;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.entities.service.Birthday;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.authentification.UserService;
import com.phoenixacces.apps.persistence.services.parametrage.BirthdayService;
import com.phoenixacces.apps.persistence.services.parametrage.SmsCredentialService;
import com.phoenixacces.apps.producer.JmsProducer;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API pour la création du profil")
@Slf4j
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final BirthdayService birthdayService;
    private final JmsProducer jmsProducer;
    private final SmsCredentialService smsCredentialService;

    @Value("${api.sms.gateway}")
    private String gateway;

    @Autowired
    public ProfileController(ProfileService profileService,
                             UserService userService,
                             JmsProducer jmsProducer,
                             BirthdayService birthdayService,
                             SmsCredentialService smsCredentialService){
        this.profileService         = profileService;
        this.userService            = userService;
        this.birthdayService        = birthdayService;
        this.jmsProducer            = jmsProducer;
        this.smsCredentialService   = smsCredentialService;
    }


    @GetMapping(value = "/find-all-compte-utilisateur", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllProfile() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            response = ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
            //response = ResponseEntity.status(HttpStatus.OK).body(profileService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-compte-utilisateur", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewSmsCredential (@RequestBody Profile profile) {

        try {

            Profile data = null;
            User user = null;

            if(profile == null) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                return getRequestResponseResponseEntity(profile);
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private ResponseEntity<RequestResponse> getRequestResponseResponseEntity(Profile profile) throws Exception {
        Profile data;
        if(profileService.findOneProfile(profile.getGareRoutiere(), profile.getPhone(), profile.getProfileType()) == null ) {

            //Création du profile
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 4);
            Date dateChangPwd =  calendar.getTime();

            data = new Profile();

            data.setNomPrenoms(profile.getNomPrenoms().toUpperCase());

            data.setPhone(profile.getPhone());

            data.setProfileType(profile.getProfileType());

            data.setBirthdate(profile.getBirthdate());

            data.setEmail(profile.getEmail());

            data.setGareRoutiere(profile.getGareRoutiere());

            data.setModule(profile.getModule());

            data.setFacebook(profile.getFacebook() == null ? "https://web.facebook.com/mobileplussms" : profile.getFacebook());

            data.setTwitter(profile.getTwitter() == null ? "https://web.facebook.com/mobileplussms" : profile.getTwitter());

            data.setInstagram(profile.getInstagram() == null ? "https://web.facebook.com/mobileplussms" : profile.getInstagram());

            data.setSkype(profile.getSkype() == null ? "https://web.facebook.com/mobileplussms" : profile.getSkype());

            Profile _profile_ = profileService.create(data);

            if (_profile_.getIdDigital() == null) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                //Enregistrement HBD
                savingHbd(data, _profile_);


                //Création du compte utilsiateur
                savingUserAccount(profile, dateChangPwd, _profile_);

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
                            new RequestInformation(400, "L'utilsiateur " + profile.getNomPrenoms() + " possède déjà un compte de ce type de profile"),
                            new RequestMessage(null, "Fail")
                    )
            );
        }
    }

    private void savingUserAccount(Profile profile, Date dateChangPwd, Profile _profile_) throws Exception {
        User user;
        if(userService.findOne(_profile_) == null){
            user = new User();
            user.setProfile(_profile_);
            user.setUsername(profile.getUsername());
            user.setRoles("UTLISATEUR - "+ profile.getGareRoutiere().getCompagnie().getAbbrev().toUpperCase());
            user.setDateChangPwd(dateChangPwd);
            userService.create(user);
        }

        String number       = profile.getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+ profile.getPhone();
        String promReceiver = WordUtils.capitalize(profile.getNomPrenoms().toLowerCase()).split(" ")[1];

        if (!number.isEmpty()){
            SmsMessage sms = new SmsMessage();
            sms.setTypeMessage(1L);
            sms.setToId(number);
            sms.setContent("Bonjour "+ promReceiver +" et bienvenue sur Di-Gital web. Vos identifiants de connexion à l'application vous a été envoyé sur votre e-mail donné.");
            sms.setFromName(profile.getNomPrenoms().toUpperCase());
            sms.setUsername(profile.getGareRoutiere().getCompagnie().getSmsCredential().getUsername());
            sms.setPassword(profile.getGareRoutiere().getCompagnie().getSmsCredential().getPassword());
            sms.setSenderId(profile.getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());
            jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
        }

        if(!profile.getEmail().isEmpty()){
            EmailMessage email = new EmailMessage();
            email.setEmail(profile.getEmail());
            email.setSubject("IDENTIFIANT DE CONNEXION - DI-GITAL Web");
            email.setType("RECORDING");
            email.setUsername(profile.getUsername().toUpperCase());
            email.setDefaulPwd("di-gital");
            jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
        }
    }

    private void savingHbd(Profile data, Profile _profile_) throws Exception {
        if(data.getBirthdate() != null){

            String birthDay = data.getBirthdate().toString().split("-")[2]+"/"+ data.getBirthdate().toString().split("-")[1];
            String concerne = WordUtils.capitalize(_profile_.getNomPrenoms().toLowerCase());

            if(birthdayService.findeOne(birthDay, _profile_.getNomPrenoms(), ""+Calendar.YEAR) == null){
                Birthday birthday = new Birthday();
                birthday.setBirthday(data.getBirthdate().toString().split("-")[2]+"/"+ data.getBirthdate().toString().split("-")[1]);
                birthday.setAnniversaireux(concerne);
                birthday.setAnnee(""+Calendar.YEAR);
                birthday.setEnvoi(false);
                birthdayService.create(birthday);
            }
        }
    }


    @PutMapping(value = "/update-compte-utilisateur", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> updateUser(@RequestBody CompteUtilisateur item) {

        try {

            if(item == null) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                    new RequestResponse(
                            new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),
                            new RequestMessage(null, "Fail")
                    )
                );
            }
            else {

                Profile profile = profileService.findOne(item.getIdDigital());

                if(profile != null) {

                    profile.setNomPrenoms(item.getNomPrenoms().toUpperCase());

                    profile.setPhone(item.getPhone());

                    profileService.update(profile);

                    if (profile.getIdDigital() == null) {

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
                else{

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



    @PutMapping(value = "/update-profile", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> updateUserProfile(@RequestBody Profile item) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                    new RequestResponse(
                            new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),
                            new RequestMessage(null, "Fail")
                    )
                );
            }
            else {

                Profile profile = profileService.findOne(item.getIdDigital());

                if(profile != null) {

                    profile.setNomPrenoms(item.getNomPrenoms().toUpperCase());

                    profile.setPhone(item.getPhone());

                    profile.setBirthdate(item.getBirthdate());

                    profile.setSkype(item.getSkype());

                    profile.setInstagram(item.getInstagram());

                    profile.setTwitter(item.getTwitter());

                    profile.setFacebook(item.getFacebook());

                    profile.setEmail(item.getEmail());

                    profileService.update(profile);

                    if (profile.getIdDigital() == null) {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(
                                    new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                    new RequestMessage(null, "Fail")
                            )
                        );
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.OK).body(profileService.findOneUser(item.getUsername()));
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

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/checking-sms/{smsId}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> checkingSms(@PathVariable(name = "smsId") Long smsId) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(smsId != null){

                Long nbreSms = null;

                SmsCredential credential = smsCredentialService.findOne(smsId);

                if(credential != null){

                    if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                        nbreSms = credential.getNombreSmsLeTexto();
                    }
                    else{

                        nbreSms = credential.getNombreSms();
                    }

                    response = ResponseEntity.status(HttpStatus.OK).body(nbreSms);
                }
                else{

                    response = ResponseEntity.status(HttpStatus.OK).body(nbreSms);
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
}
