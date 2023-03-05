package com.phoenixacces.apps.controller.auth;


import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.jms.messages.EmailMessage;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.jwt.CustomUserDetailsService;
import com.phoenixacces.apps.jwt.JwtUtil;
import com.phoenixacces.apps.models.jwt.AuthenticationRequest;
import com.phoenixacces.apps.models.jwt.AuthenticationResponse;
import com.phoenixacces.apps.models.reset.ResetModel;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.models.user.AuthModel;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.entities.parametrage.NotificationSysteme;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.entities.service.SmsEnvoye;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.authentification.UserService;
import com.phoenixacces.apps.persistence.services.parametrage.NotificationSystemeService;
import com.phoenixacces.apps.persistence.services.parametrage.SMSPhoenixAccesService;
import com.phoenixacces.apps.persistence.services.parametrage.SmsCredentialService;
import com.phoenixacces.apps.persistence.services.parametrage.TypeMessageService;
import com.phoenixacces.apps.producer.JmsProducer;
import com.phoenixacces.apps.utiles.Utils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "Authentication and Registration API")
@Slf4j
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;
    private final UserService userService;
    private final SMSPhoenixAccesService smsPhoenixAccesService;
    private final TypeMessageService typeMessageService;
    private final SmsCredentialService smsCredentialService;
    private final NotificationSystemeService notificationService;

    private final JmsProducer jmsProducer;


    @Value("${api.service.username}")
    private String username;
    @Value("${api.service.password}")
    private String password;

    @Value("${api.sms.gateway}")
    private String gateway;

    SimpleDateFormat datStr = new SimpleDateFormat("dd-MM-yyyy");

    SimpleDateFormat heure = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil,
                                    ProfileService profileService, UserService userService,
                                    SMSPhoenixAccesService smsPhoenixAccesService, TypeMessageService typeMessageService,
                                    SmsCredentialService smsCredentialService,
                                    NotificationSystemeService notificationService, JmsProducer jmsProducer
                                    ) {
        this.authenticationManager      = authenticationManager;
        this.customUserDetailsService   = customUserDetailsService;
        this.jwtUtil                    = jwtUtil;
        this.profileService             = profileService;
        this.userService                = userService;
        this.smsPhoenixAccesService     = smsPhoenixAccesService;
        this.typeMessageService         = typeMessageService;
        this.smsCredentialService       = smsCredentialService;
        this.notificationService        = notificationService;
        this.jmsProducer                = jmsProducer;
    }

    @ApiOperation(value = "Authorization", notes = "Allow to authorization transactions to middleware. Return a token form authorize session requests.")
    @GetMapping(value = "authorization", produces = "application/json")
    @Transactional(readOnly = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authorized", response = String.class),
            @ApiResponse(code = 400, message = "Bad Request", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    public ResponseEntity<?> authorization() throws Exception {
        log.info("[ CONTROLLER ] - AuthenticationController::authorization --- START");
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            /*log.info("[ CONTROLLER ] - AuthenticationController::authorization --- generate token : Fail!");
            log.info("[ CONTROLLER ] - AuthenticationController::authorization --- Exception : {}", e.getMessage());
            log.info("[ CONTROLLER ] - AuthenticationController::authorization --- DONE");*/
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);
        /*log.info("[ CONTROLLER ] - AuthenticationController::authorization --- generate token : Succes!");
        log.info("[ CONTROLLER ] - AuthenticationController::authorization --- DONE");*/
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }


    @ApiOperation(value = "Authentication", notes = "Allow to authenticate user. Return a token form authorize session requests.")
    @PostMapping(value = "/authentication", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> authentication(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        return ResponseEntity.ok(new AuthenticationResponse(jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername()))));
    }


    @PostMapping(value = "/find-one-profile", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The data has been correctly created in the system", response = Profile.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findProfile (@RequestBody AuthenticationRequest authenticationRequest) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(authenticationRequest != null){

                SmsEnvoye smsEnvoye = new SmsEnvoye();
                AuthModel authModel = new AuthModel();

                long code = Utils.generateRandom(6);

                SmsCredential smsCredential = new  SmsCredential();

                User user = profileService.findOneUser(authenticationRequest.getUsername());

                if(user != null){

                    String number       = "";

                    Long nombre         = null;

                    smsCredential.setPassword(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                    smsCredential.setUsername(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                    smsCredential.setSenderId(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                    smsCredential.setToken(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getToken());

                    smsCredential.setSenderLeTexto(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderLeTexto());


                    //Mise à jour du nombre de sms
                    SmsCredential credential = smsCredentialService.findOne(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getId());

                    if(credential != null){

                        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                            number       = "225"+user.getProfile().getPhone();

                            nombre       = credential.getNombreSmsLeTexto();
                        }
                        else{

                            number       = user.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+user.getProfile().getPhone();

                            nombre       = credential.getNombreSms();
                        }

                        if(nombre > 1){

                            String destinataire = user.getProfile().getNomPrenoms().toUpperCase();

                            String promReceiver = WordUtils.capitalize(user.getProfile().getNomPrenoms().toLowerCase()).split(" ")[1];

                            String message      = "Bonjour "+ promReceiver +", votre code d'autorisation temporaire pour votre connexion unique est "+ code+". Ne le partagez avec personne";

                            smsEnvoye.setTypeMessage(typeMessageService.findById(6L));

                            smsEnvoye.setNumeroDestinataire(number);

                            smsEnvoye.setDestinataire(destinataire);

                            smsEnvoye.setCorpsMessage(message);

                            smsEnvoye.setSmsCredential(smsCredential);

                            if(authenticationRequest.getPassword().equalsIgnoreCase("di-gital")){

                                if(profileService.findOne(authenticationRequest.getUsername(), authenticationRequest.getPassword()) == null){

                                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                        new RequestResponse(

                                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Les paramètres d'identifications mise à dispositions ne sont pas correctes"),

                                            new RequestMessage(null, "Bad request")
                                        )
                                    );
                                }
                                else{

                                    smsPhoenixAccesService.envoyeMessageBySouscrivant(smsEnvoye);

                                    if(smsEnvoye.getId() == null){

                                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                                new RequestResponse(

                                                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible de transmettre le code OTP via sms. Prière vérifier votre connexion internet."),

                                                        new RequestMessage(null, "Bad request")
                                                )
                                        );
                                    }
                                    else{

                                        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                                            credential.setNombreSmsLeTexto(credential.getNombreSmsLeTexto() - 1);
                                        }
                                        else{

                                            credential.setNombreSms(credential.getNombreSms() - 1);
                                        }

                                        smsCredentialService.update(credential);

                                        authModel.setUser(profileService.findOne(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

                                        authModel.setCodeOTP(code);


                                        //Enregistrement de la notification
                                        NotificationSysteme notification = new NotificationSysteme();

                                            notification.setNotification("Connexion à votre espace privé le "+datStr.format(new Date())+" à "+heure.format(new Date())+" a été effectué avec succès.");

                                            notification.setProfile(user.getProfile());

                                            notification.setReference(String.valueOf(code));

                                            notification.setType(TypeNotification.Connexion);

                                        notificationService.create(notification);

                                        response = ResponseEntity.status(HttpStatus.OK).body(authModel);
                                    }
                                }
                            }
                            else {

                                if(user.getFirstConnexion() == 0){

                                    smsPhoenixAccesService.envoyeMessageBySouscrivant(smsEnvoye);

                                    if(smsEnvoye.getId() == null){

                                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                                new RequestResponse(
                                                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible de transmettre le code OTP via sms. Prière vérifier votre connexion internet."),
                                                        new RequestMessage(null, "Bad request")
                                                )
                                        );
                                    }
                                    else{


                                        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                                            credential.setNombreSmsLeTexto(credential.getNombreSmsLeTexto() - 1);
                                        }
                                        else{

                                            credential.setNombreSms(credential.getNombreSms() - 1);
                                        }

                                        smsCredentialService.update(credential);

                                        authModel.setUser(user);

                                        authModel.setCodeOTP(code);

                                        response = ResponseEntity.status(HttpStatus.OK).body(authModel);
                                    }
                                }
                                else{

                                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                            new RequestResponse(
                                                    new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Les paramètres d'identifications mise à dispositions ne sont pas correctes"),
                                                    new RequestMessage(null, "Bad request")
                                            )
                                    );
                                }
                            }
                        }
                        else{

                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                    new RequestResponse(
                                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Votre quota d'sms ne vous permet pas d'avoir accès à la plate-forme."),
                                            new RequestMessage(null, "Bad request")
                                    )
                            );
                        }
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestResponse(
                                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Votre quota d'sms ne vous permet pas d'avoir accès à la plate-forme."),
                                        new RequestMessage(null, "Bad request")
                                )
                        );
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Aucun n'utilisateur ne correspond au nom utilisateur indiqué"),
                                new RequestMessage(null, "Bad request")
                        )
                     );
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                    new RequestResponse(
                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "l'objet de mapping est vide."),
                            new RequestMessage(null, "Bad request")
                    )
                );
            }
        }
        catch (Exception e) {

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PostMapping(value = "/get-code-to-update-password", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The data has been correctly created in the system", response = Profile.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> getCodePassword (@RequestBody AuthenticationRequest authenticationRequest) {

        try {

            if(authenticationRequest != null){

                SmsEnvoye smsEnvoye = new SmsEnvoye();
                AuthModel authModel = new AuthModel();

                long  code = Utils.generateRandom(6);

                SmsCredential smsCredential = new  SmsCredential();

                User user = profileService.findOneUser(authenticationRequest.getUsername());

                if(user != null){

                    smsCredential.setPassword(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());
                    smsCredential.setUsername(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());
                    smsCredential.setSenderId(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                    String number       = user.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+user.getProfile().getPhone();
                    String destinataire = user.getProfile().getNomPrenoms().toUpperCase();
                    String promReceiver = WordUtils.capitalize(user.getProfile().getNomPrenoms().toLowerCase()).split(" ")[1];
                    String message      = "Bonjour "+ promReceiver +",\nVotre code OTP pour la modification de votre mot de passe Di-Gital web est "+ code+". Ne le partagez avec personne.";

                    smsEnvoye.setTypeMessage(typeMessageService.findById(6L));
                    smsEnvoye.setNumeroDestinataire(number);
                    smsEnvoye.setDestinataire(destinataire);
                    smsEnvoye.setCorpsMessage(message);
                    smsEnvoye.setSmsCredential(smsCredential);
                    smsPhoenixAccesService.envoyeMessageBySouscrivant(smsEnvoye);

                    if(smsEnvoye.getId() == null){

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestResponse(

                                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible de transmettre le code OTP via sms. Prière vérifier votre connexion internet."),

                                        new RequestMessage(null, "Bad request")
                                )
                        );
                    }
                    else{

                        authModel.setUser(profileService.findOne(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

                        authModel.setCodeOTP(code);

                        return ResponseEntity.status(HttpStatus.OK).body(authModel);
                    }
                }
                else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Aucun n'utilisateur ne correspond au nom utolisateur indiqué"),
                                    new RequestMessage(null, "Bad request")
                            )
                    );
                }
            }
            else{

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "l'objet de mapping est vide."),
                                new RequestMessage(null, "Bad request")
                        )
                );
            }
        }
        catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/update-password", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = AuthenticationRequest.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> updatePassword(@RequestBody AuthenticationRequest authenticationRequest) {

        try {

            if(authenticationRequest == null) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                    new RequestResponse(
                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                            new RequestMessage(null, "Bad request")
                    )
                );
            }
            else {

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 3);
                Date dateChangPwd = calendar.getTime();


                User user = userService.findOne(authenticationRequest.getUsername());

                user.setLastUpdate(Instant.now());
                user.setPassword(BCrypt.hashpw(authenticationRequest.getPassword(), BCrypt.gensalt()));
                user.setFirstConnexion(0);
                user.setDateChangPwd(dateChangPwd);
                user.setDefaultPassword(null);
                User _user_ = userService.update(user);

                if(_user_.getId() == null){

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible de faire la mise à jour du mot de passe utilisateur"),
                                    new RequestMessage(null, "Bad request")
                            )
                    );
                }
                else{


                    //Enregistrement de la notification
                    NotificationSysteme notification = new NotificationSysteme();

                    notification.setNotification("Le changement de votre mot de passe le "+datStr.format(new Date())+" à "+heure.format(new Date())+" a été effectué avec succès.");

                    notification.setProfile(_user_.getProfile());

                    notification.setReference(String.valueOf(Utils.generateRandom(6)));

                    notification.setType(TypeNotification.Update);

                    notificationService.create(notification);

                    return ResponseEntity.status(HttpStatus.OK).body(
                            new RequestResponse(
                                    new RequestInformation(HttpStatus.OK.value(), "Mise à jour du mot de passe effectué avec succès"),
                                    new RequestMessage(null, "Mise à jour du mot de passe effectué avec succès")
                            )
                    );
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/last-connection", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "User connexion model", notes = "User connexion model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly coonected in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> lastConnexion(@RequestBody AuthenticationRequest authenticationRequest) {

        try {
            if(authenticationRequest == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                //Enregistrement de la notification
                NotificationSysteme notification = new NotificationSysteme();

                notification.setNotification("Connexion à votre espace privé le "+datStr.format(new Date())+" à "+heure.format(new Date())+" a été effectué avec succès.");

                notification.setProfile(userService.findOne(authenticationRequest.getUsername()).getProfile());

                notification.setReference(String.valueOf(Utils.generateRandom(6)));

                notification.setType(TypeNotification.Connexion);

                NotificationSysteme notf = notificationService.create(notification);

                if(notf.getId() != null){

                    userService.lastConnexion(authenticationRequest.getUsername());

                    return ResponseEntity.ok(new AuthenticationResponse(jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername()))));
                }
                else{

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Impossible de mettre à jour la table des notifications");
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping(value = "/verifi-user", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "reset-user-password", notes = "User connexion model")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly coonected in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> checkingUserExist(@RequestBody ResetModel resetModel) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(!resetModel.getEmail().isEmpty()){

                Profile profile = profileService.findOneEmail(resetModel.getEmail().toLowerCase());

                if(profile != null){

                    //ENVOI DU CODE OTP POUR FINALISATION DU FLOW DE RESET

                    String promReceiver = WordUtils.capitalize(profile.getNomPrenoms().toLowerCase()).split(" ")[1];

                    String number       = "225"+profile.getPhone();

                    long code           = Utils.generateRandom(6);

                    String message      = "Bonjour "+ promReceiver +", votre code d'autorisation temporaire pour la reinitialisation de vos accès est le "+ code+". Ne le partagez avec personne";


                    SmsEnvoye smsEnvoye = new SmsEnvoye();

                    smsEnvoye.setTypeMessage(typeMessageService.findById(6L));

                    smsEnvoye.setNumeroDestinataire(number);

                    smsEnvoye.setDestinataire(profile.getNomPrenoms().toUpperCase());

                    smsEnvoye.setCorpsMessage(message);

                    smsPhoenixAccesService.smsAuthorization(smsEnvoye);


                    //Retour du code à l'utilsiatzeur

                    ResetModel model = new ResetModel();

                    model.setEmail(resetModel.getEmail());

                    model.setCodeOTP(String.valueOf(code));

                    model.setTelephone(profile.getPhone());


                    //Enregistrement de la notification
                    NotificationSysteme notification = new NotificationSysteme();

                    notification.setNotification("Demande de code OTP pour réinitialisation de votre espace privé le "+datStr.format(new Date())+" à "+heure.format(new Date())+" a été effectué avec succès.");

                    notification.setProfile(profile);

                    notification.setReference(String.valueOf(code));

                    notification.setType(TypeNotification.Connexion);

                    if(notificationService.create(notification).getId() != null){

                        response = ResponseEntity.status(HttpStatus.OK).body(model);
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossible de transmettre le code OTP au numéro indiqué lors de la création de compte");
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le profil n'existe pas ou est sans doute désactiver");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Authorize");
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


    @PutMapping(value = "/reset-password", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "reset-user-password", notes = "reset-password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly coonected in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> resetPassword(@RequestBody ResetModel request) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(request.getEmail() != null && request.getTelephone() != null){

                Profile profile = profileService.findOneBy2Param(request.getEmail().toLowerCase(), request.getTelephone());

                if(profile != null){

                    //TODO ::: CHECKING PROFIL STATUS

                    if(profile.isActive()){

                        // TODO ::: ACCOUNT IS OKAY. APPLY THE RENITIALIZATION METHOD

                        User user = userService.findOne(profile);

                        if (user != null && user.isActive()){

                            //Date expiration du mot de passe par defaut
                            Calendar calendar = Calendar.getInstance();

                            calendar.add(Calendar.MONTH, 1);

                            Date dateChangPwd =  calendar.getTime();


                            user.setFirstConnexion(1);

                            user.setPassword(BCrypt.hashpw("di-gital", BCrypt.gensalt()));

                            user.setDefaultPassword("di-gital");

                            user.setDateChangPwd(dateChangPwd);

                            if(userService.update(user).getId() != null){

                                //ENvoi de mail de notification
                                if(!profile.getEmail().isEmpty()){

                                    EmailMessage email = new EmailMessage();

                                    email.setEmail(profile.getEmail());

                                    email.setSubject("RÉINITIALISATION IDENTIFIANT DE CONNEXION - DI-GITAL Web");

                                    email.setType("RESET_APP");

                                    email.setDefaulPwd("di-gital");

                                    jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                                }

                                //Enregistrement de la notification
                                NotificationSysteme notification = new NotificationSysteme();

                                notification.setNotification("Reinitialisation des accès de votre espace privé le "+datStr.format(new Date())+" à "+heure.format(new Date())+" a été effectué avec succès.");

                                notification.setProfile(profile);

                                notification.setReference(String.valueOf(Utils.generateRandom(6)));

                                notification.setType(TypeNotification.Connexion);

                                NotificationSysteme notf = notificationService.create(notification);

                                if(notf.getId() != null){

                                    //ENVOI SMS POUR OPERATION REUSSIE
                                    String number       = "225"+profile.getPhone();

                                    String message      = "Votre demande de reinitialisation de vos paramètre à votre espace privé Di-Gital Web a été effectué avec succès.";

                                    SmsEnvoye smsEnvoye = new SmsEnvoye();

                                    smsEnvoye.setTypeMessage(typeMessageService.findById(6L));

                                    smsEnvoye.setNumeroDestinataire(number);

                                    smsEnvoye.setDestinataire(profile.getNomPrenoms().toUpperCase());

                                    smsEnvoye.setCorpsMessage(message);

                                    smsPhoenixAccesService.smsAuthorization(smsEnvoye);

                                    //ENvoi de mail de notification
                                    if(!profile.getEmail().isEmpty()){

                                        EmailMessage email = new EmailMessage();

                                        email.setEmail(profile.getEmail());

                                        email.setSubject("RÉINITIALISATION IDENTIFIANT DE CONNEXION - DI-GITAL Web");

                                        email.setType("RESET_APP");

                                        jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                                    }

                                    response = ResponseEntity.status(HttpStatus.OK).body(
                                            new RequestResponse(
                                                    new RequestInformation(HttpStatus.OK.value(), "Réinitialisation du mot de passe effectué avec succès"),
                                                    new RequestMessage(""+HttpStatus.OK.value(), "Réinitialisation du mot de passe effectué avec succès")
                                            ));
                                }
                                else{

                                    response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Impossible de mettre à jour la table des notifications");
                                }
                            }
                            else{

                                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossible de traiter votre requête. Prière vous rapprocher de l'administrateur partenaire ou du service support Di-Gital Web pour resolution.");
                            }
                        }
                        else{

                            response = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Le profil n'a pas de compte. prière vous rapprocher de l'administrateur partenaire ou du service support Di-Gital Web pour resolution.");
                        }
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le profil a été désactivé. Impossible d'effectuer la reinitialisation du mot de passe");
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Le profil n'existe pas ou est sans doute désactivé");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Not Authorize");
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
