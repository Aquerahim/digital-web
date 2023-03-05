package com.phoenixacces.apps.controller.api;

import com.phoenixacces.apps.enumerations.StatutLivraison;
import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.jms.messages.EmailMessage;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.jwt.CustomUserDetailsService;
import com.phoenixacces.apps.jwt.JwtUtil;
import com.phoenixacces.apps.models.jwt.AuthenticationRequest;
import com.phoenixacces.apps.models.jwt.AuthenticationResponse;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.entities.module.livraison.Historique;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livraisons;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livreurs;
import com.phoenixacces.apps.persistence.entities.module.livraison.Notifications;
import com.phoenixacces.apps.persistence.entities.parametrage.MotifSuspensionCollaboration;
import com.phoenixacces.apps.persistence.models.api.livraison.*;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.authentification.UserService;
import com.phoenixacces.apps.persistence.services.module.livraison.LivraisonsService;
import com.phoenixacces.apps.persistence.services.module.livraison.LivreursService;
import com.phoenixacces.apps.persistence.services.module.livraison.NotificationsServices;
import com.phoenixacces.apps.persistence.services.parametrage.SMSPhoenixAccesService;
import com.phoenixacces.apps.persistence.services.parametrage.TypeMessageService;
import com.phoenixacces.apps.persistence.services.parametrage.mLivraison.HistoriqueService;
import com.phoenixacces.apps.producer.JmsProducer;
import com.phoenixacces.apps.utiles.Utils;
import io.swagger.annotations.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API pour l'application mobile de livraison")
@Slf4j
public class APILivraisonController {

    //RECUPERATION DES SERVICES
    private final ProfileService profileService;
    private final UserService userService;
    private final LivreursService livreursService;
    private final LivraisonsService livraisonsService;

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService customUserDetailsService;

    private final HistoriqueService historiqueService;

    private final JmsProducer jmsProducer;

    private final SMSPhoenixAccesService smsService;

    private final TypeMessageService typeMessageService;

    private final JwtUtil jwtUtil;


    //INITIALISATION DU SERVICES
    @Autowired
    public APILivraisonController(
            JmsProducer jmsProducer,
            ProfileService profileService,
            SMSPhoenixAccesService smsService,
            TypeMessageService typeMessageService,
            LivreursService livreursService,
            LivraisonsService livraisonsService,
            HistoriqueService historiqueService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService customUserDetailsService,
            JwtUtil jwtUtil,
            UserService userService
            //NotificationsServices notificationsServices
    ){
        this.jmsProducer                = jmsProducer;
        this.profileService             = profileService;
        this.smsService                 = smsService;
        this.typeMessageService         = typeMessageService;
        this.livreursService            = livreursService;
        this.livraisonsService          = livraisonsService;
        this.historiqueService          = historiqueService;
        this.authenticationManager      = authenticationManager;
        this.customUserDetailsService   = customUserDetailsService;
        this.userService                = userService;
        this.jwtUtil                    = jwtUtil;
    }


    @PostMapping(value = "/authorization-livreur-profile-data", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = ProfileDto.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<ProfileDto> getLivreurData(@RequestBody AuthenticationRequest request){

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        ProfileDto profileDto       = new ProfileDto();

        double montantCommission    = 0;

        double globaleCommission    = 0;

        int succesLivraison         = 0;

        int failLivraison           = 0;

        int cancelLivraison         = 0;

        int pendingLivraison        = 0;

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            Livreurs livreur = livreursService.findOneByUsername(request.getUsername().toUpperCase());

            if(livreur == null){

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestMessage(

                                HttpStatus.BAD_REQUEST.value()+"",

                                "Aucun livreur n'a été trouvé via la variable passée en paramètre. Prère vous procheer de l'administrateur"
                        )
                );
            }
            else{

                List<Livraisons> livraisonsList = livraisonsService.findAllByProfile(livreur.getProfile());

                for (Livraisons livraisons : livraisonsList) {

                    if(livraisons.getStatutLivraison().equals(StatutLivraison.LIVRE)){

                        montantCommission += livraisons.getCommissionLivreur();

                        succesLivraison ++;
                    }

                    if(livraisons.getStatutLivraison().equals(StatutLivraison.ANNULER)){

                        cancelLivraison ++;
                    }

                    if(livraisons.getStatutLivraison().equals(StatutLivraison.NON_LIVRE)){

                        failLivraison ++;
                    }

                    if(livraisons.getStatutLivraison().equals(StatutLivraison.EN_ATTENTE)){

                        pendingLivraison ++;
                    }

                    globaleCommission+= livraisons.getCommissionLivreur();
                }

                profileDto.setIdDigital(livreur.getProfile().getIdDigital());

                profileDto.setDateNaissance(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(livreur.getProfile().getBirthdate()));

                profileDto.setEmail(livreur.getProfile().getEmail());

                profileDto.setGenre(livreur.getProfile().getGenre().getKey());

                profileDto.setNomPrenoms(livreur.getProfile().getNomPrenoms().toUpperCase());

                profileDto.setContact(livreur.getContact());

                profileDto.setTauxCommission(Math.round(livreur.getTauxComm() * 100)+"%");

                profileDto.setCommision(montantCommission);

                profileDto.setCommisionGlobal(globaleCommission);

                profileDto.setTypeProfile(livreur.getProfile().getProfileType().getLabel());

                profileDto.setEnginAssure(livreur.getAssureEngin());

                profileDto.setDateFinAssurance(livreur.getAssureEngin().equalsIgnoreCase("OUI") ? DateTimeFormatter.ofPattern("dd.MM.yyyy").format(livreur.getDateFinAssurance()) : "N/A");

                profileDto.setAssureur(livreur.getAssureEngin().equalsIgnoreCase("OUI") ? livreur.getCompagnieAssurance() : "N/A");

                profileDto.setTypeEngin(livreur.getTypeEngin().getTypeengins());

                profileDto.setNbrLivrSucces(succesLivraison);

                profileDto.setNbrLivrAnnule(cancelLivraison);

                profileDto.setNbrLivrNoSucces(failLivraison);

                profileDto.setNbrLivrPending(pendingLivraison);

                profileDto.setNbrLivrAssigne(livraisonsList.size());

                profileDto.setNotifAnniv(livreur.isNotifAnniv() ? "OUI" : "NON");

                profileDto.setAppDownload(livreur.getProfile().isActive() ? "OUI" : "NON");

                profileDto.setJwt(jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(request.getUsername())));

                profileDto.setPremiereCnx(userService.findOne(livreur.getProfile()).getDefaultPassword() == null ? "NON" : "OUI");

                response = ResponseEntity.status(HttpStatus.OK).body(profileDto);
            }

        } catch (BadCredentialsException e) {

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(

                new RequestMessage(

                    HttpStatus.BAD_REQUEST.value()+"",

                    "Aucun livreur n'a été trouvé via la variable passée en paramètre. Prère vous procheer de l'administrateur"
                )
            );
        }

        /*final UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);*/

        //return response;
        //return ResponseEntity.ok(new AuthenticationResponse(jwt));

       finally {
            return response;
        }
    }



    @PutMapping(value = "/update-password-livreur-apps", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = String.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> updatePassword(@RequestBody AuthenticationRequest authenticationRequest) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(authenticationRequest == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestMessage(

                                HttpStatus.BAD_REQUEST.value()+"",

                                "Aucun livreur n'a été trouvé via la variable passée en paramètre. Prère vous procheer de l'administrateur"
                        )
                );
            }
            else {

                User user = userService.findOne(authenticationRequest.getUsername());

                if (user == null){

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestMessage(

                                    HttpStatus.BAD_REQUEST.value()+"",

                                    "L'utilisateur demandé n'a pas été trouvé. Impossible de faire la mise à jour du mot de passe utilisateur"
                            )
                    );
                }
                else{

                    Calendar cal = Calendar.getInstance();

                    cal.add(Calendar.MONTH, 3);


                    user.setLastUpdate(Instant.now());

                    user.setPassword(BCrypt.hashpw(authenticationRequest.getPassword(), BCrypt.gensalt()));

                    user.setFirstConnexion(0);

                    user.setDateChangPwd(cal.getTime());

                    user.setDefaultPassword(null);

                    User _user_ = userService.update(user);

                    if(_user_.getId() == null){

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestMessage(

                                        HttpStatus.BAD_REQUEST.value()+"",

                                        "Impossible de faire la mise à jour du mot de passe utilisateur"
                                )
                        );
                    }
                    else{

                        //TODO ::: ENVOI DES NOTIFICATION - MESSAGE DE BIENVENU
                        System.out.println(" === ==== =============> " + _user_.getProfile().getPhone());
                        String numberLivreur = _user_.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+_user_.getProfile().getPhone();

                        String promReceiver = WordUtils.capitalize(_user_.getProfile().getNomPrenoms().toLowerCase()).split(" ")[1];

                        //System.out.println(" numberLivreur === ==== =============> " + numberLivreur);
                        //System.out.println(" promReceiver  === ==== =============> " + promReceiver);

                        if (!numberLivreur.isEmpty()){

                            SmsMessage sms = new SmsMessage();

                            sms.setTypeMessage(4L);

                            sms.setToId(numberLivreur);

                            sms.setContent("Bonjour "+ promReceiver +". \nNous vous souhaitons la bienvenue sur l'espace dédié de "+_user_.getProfile().getGareRoutiere().getCompagnie().getCompagnie()+". Votre compte est maintenant actif.");

                            sms.setFromName(_user_.getProfile().getNomPrenoms());

                            sms.setUsername(_user_.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                            sms.setPassword(_user_.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                            sms.setSenderId(_user_.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                            jmsProducer.send(new JmsMessage("Sending welcome message to livreur", Converter.pojoToJson(sms), SmsMessage.class));
                        }

                        response = ResponseEntity.status(HttpStatus.OK).body(

                                new RequestMessage(

                                        HttpStatus.OK.value()+"",

                                        "Mise à jour du mot de passe effectué avec succès"
                                )
                        );
                    }
                }
            }
        }

        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(

                    new RequestMessage(
                            HttpStatus.INTERNAL_SERVER_ERROR.value()+"",
                            e.getMessage()
                    )
            );
        }

        finally {
            return response;
        }
    }



    @PutMapping(value = "/reset-password", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = ProfileDto.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> resetPassword(@RequestBody AuthenticationRequest authenticationRequest) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(authenticationRequest == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestMessage(

                                HttpStatus.BAD_REQUEST.value()+"",

                                "Aucun livreur n'a été trouvé via la variable passée en paramètre. Prère vous procheer de l'administrateur"
                        )
                );
            }
            else {

                User user = userService.findOne(authenticationRequest.getUsername());

                if(user!= null){

                    user.setLastUpdate(Instant.now());

                    user.setFirstConnexion(1);

                    user.setPassword(BCrypt.hashpw("di-gital", BCrypt.gensalt()));

                    user.setDefaultPassword("di-gital");

                    User _user_ = userService.update(user);

                    if(_user_.getId() == null){

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestMessage(

                                        HttpStatus.BAD_REQUEST.value()+"",

                                        "Impossible de faire la mise à jour du mot de passe utilisateur"
                                )
                        );
                    }
                    else{

                        //TODO ::: ENVOI DES NOTIFICATION - MESSAGE DE REINITIALISATION
                        String numberLivreur = user.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+user.getProfile().getPhone();

                        String promReceiver = WordUtils.capitalize(user.getProfile().getNomPrenoms().toLowerCase()).split(" ")[1];

                        if (!numberLivreur.isEmpty()){

                            SmsMessage sms = new SmsMessage();

                            sms.setTypeMessage(4L);

                            sms.setToId(numberLivreur);

                            sms.setContent("Bonjour "+ promReceiver +", votre demande de renitialisation de mot de passe a été effectué avec succès. Le nouveau mot de passe vous a été adressé par mail.");

                            sms.setFromName(user.getProfile().getNomPrenoms());

                            sms.setUsername(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                            sms.setPassword(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                            sms.setSenderId(user.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                            jmsProducer.send(new JmsMessage("Sending welcome message to livreur", Converter.pojoToJson(sms), SmsMessage.class));
                        }


                        if(!user.getProfile().getEmail().isEmpty()){

                            EmailMessage email = new EmailMessage();

                            email.setEmail(user.getProfile().getEmail());

                            email.setSubject("REINITIALISATION MOT DE PASSE DE CONNEXION - APPLICATION MOBILE LIVREUR");

                            email.setType("RESET_APP");

                            email.setUsername(user.getProfile().getUsername().toUpperCase());

                            email.setDefaulPwd("di-gital");

                            jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                        }

                        response = ResponseEntity.status(HttpStatus.OK).body(

                                new RequestMessage(

                                        HttpStatus.OK.value()+"",

                                        "Rénitialisation effectué avec succès"
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestMessage(

                                    HttpStatus.BAD_REQUEST.value()+"",

                                    "Le compte demandé n'a pa été trouvé. Impossible de faire la reinitialisation du mot de passe utilisateur"
                            )
                    );
                }
            }
        }

        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(

                    new RequestMessage(
                            HttpStatus.INTERNAL_SERVER_ERROR.value()+"",
                            e.getMessage()
                    )
            );
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/get-livraison/{username}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = RequestMessage.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = RequestMessage.class)
    })
    public ResponseEntity<?> getLivraisonDedicated(@PathVariable(name = "username") String username) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(username != null){

                Livreurs livreur = livreursService.findOneByUsername(username.toUpperCase());

                if(livreur == null){

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestMessage(

                                    HttpStatus.BAD_REQUEST.value()+"",

                                    "Aucun livreur n'a été trouvé via la variable passée en paramètre. Prère vous procheer de l'administrateur"
                            )
                    );
                }
                else{

                    //VERIFIONS L'EXISTANCE DU COMPTE
                    Profile profile = profileService.findOne(livreur.getProfile().getIdDigital());

                    if(profile == null){

                        System.out.println(">>>>> [ API LIVRAISON ] <<<<< ::::: >>>>>>>>>>> PROFIL NON INEXISTANT <<<<<<<<<<<<<");

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestMessage(

                                        HttpStatus.BAD_REQUEST.value()+"",

                                        "La variable passée en paramètre n'est pas enregistré dans la base de données Di-Gital Web. Prère vous procheer de l'administrateur"
                                )
                        );
                    }
                    else{

                        List<Produits> produits = null;

                        List<LivraisonsDto> livraisonsDtos = new ArrayList<>();


                        //RECUPERATION DE LA LISTE DES LIVRAIONS POUR LE LIVREUR DESIGNE

                        List<Livraisons> livraisonsList = livraisonsService.findAllByProfile(profileService.findOne(profile.getIdDigital()));

                        if(livraisonsList.size() == 0){

                            response = ResponseEntity.status(HttpStatus.OK).body(

                                    new RequestMessage(

                                            HttpStatus.OK.value()+"",

                                            "Aucune liste disponible"
                                    )
                            );
                        }
                        else{

                            for (Livraisons livrs : livraisonsList) {

                                DateTimeFormatter formatter =
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd") //dd.MM.yyyy
                                                .withLocale( Locale.ENGLISH )
                                                .withZone( ZoneId.systemDefault() );

                                LivraisonsDto livraisons   = new LivraisonsDto();

                                produits = new ArrayList<>();

                                String[] array = livrs.getNomPrenomsDestinataire().split(" ");

                                livraisons.setId(livrs.getReference());

                                livraisons.setStatut(livrs.getStatutLivraison().getKey());

                                livraisons.setLieu(livrs.getZoneLivraison().getZoneCouverture() + " " + livrs.getPrecisonLieuLivraison());

                                livraisons.setDate_attribution(formatter.format( livrs.getCreation()));

                                livraisons.setDate_livraison(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(livrs.getDatelivraison()));
                                //livraisons.setDate_livraison(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(livrs.getDatelivraison()));

                                livraisons.setDateLivraison(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy").format(livrs.getDatelivraison()));

                                livraisons.setCommission(livrs.getCommissionLivreur());



                                //RENSEIGNER L'OBJET CLIENT

                                Client client           = new Client();

                                client.setContact(livrs.getContactDestinataire());

                                client.setNom(array[0]);

                                client.setPrenom(array.length > 2 ? array[1]+" " +array[2] : array[1]);
                                //client.setPrenom(livrs.getNomPrenomsDestinataire().split(" ").length > 2 ? livrs.getNomPrenomsDestinataire().split(" ")[livrs.getNomPrenomsDestinataire().split(" ").length - 2]+" " +livrs.getNomPrenomsDestinataire().split(" ")[livrs.getNomPrenomsDestinataire().split(" ").length - 1] : livrs.getNomPrenomsDestinataire().split(" ")[livrs.getNomPrenomsDestinataire().split(" ").length - 1]);

                                livraisons.setClient(client);



                                // REMPLISSAGE DE L'OBJET PRODUIT

                                Produits prdts  = new Produits();

                                prdts.setId("P-"+Utils.generateRandom(11));

                                prdts.setNom(livrs.getDescriptionColis().toUpperCase());

                                prdts.setTypeProduit(livrs.getNatureColis().getTypecolis());

                                prdts.setQuantite(String.valueOf(livrs.getQte()));

                                produits.add(prdts);



                                livraisons.setProduits(produits);

                                livraisonsDtos.add(livraisons);
                            }

                            response = ResponseEntity.status(HttpStatus.OK).body(livraisonsDtos);
                        }
                    }
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                    new RequestMessage(

                            HttpStatus.BAD_REQUEST.value()+"",

                            "Aucune variable passée en paramètre"
                    )
                );
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            //e.printStackTrace();

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(

                    new RequestMessage(
                        HttpStatus.INTERNAL_SERVER_ERROR.value()+"",
                        e.getMessage()
                    )
            );
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/get-commission-to-date/{username}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = CommissionDto.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = RequestMessage.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = RequestMessage.class)
    })
    public ResponseEntity<?> getLastValueCommisionToDate(@PathVariable(name = "username") String username) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            CommissionDto commissionDto     = new CommissionDto();

            double montantCommission        = 0;

            int succesLivraison             = 0;


            if(username != null){

                Livreurs livreur = livreursService.findOneByUsername(username.toUpperCase());

                if(livreur == null){

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestMessage(

                                    HttpStatus.BAD_REQUEST.value()+"",

                                    "Aucun livreur n'a été trouvé via la variable passée en paramètre. Prère vous procheer de l'administrateur"
                            )
                    );
                }
                else{

                    List<Livraisons> livraisonsList = livraisonsService.findAllByProfile(livreur.getProfile());

                    for (Livraisons livraisons : livraisonsList) {

                        if(livraisons.getStatutLivraison().equals(StatutLivraison.LIVRE)){

                            montantCommission += livraisons.getCommissionLivreur();

                            succesLivraison ++;
                        }
                    }
                    /*
                    Locale locale = new Locale("fr", "FR");
                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                    String date = dateFormat.format(new Date());
                    System.out.print(date);
                     */

                    commissionDto.setCommision(montantCommission);

                    commissionDto.setSuccesLivraison(succesLivraison);

                    commissionDto.setDateDemande(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));

                    response = ResponseEntity.status(HttpStatus.OK).body(commissionDto);
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestMessage(

                                HttpStatus.BAD_REQUEST.value()+"",

                                "Aucune variable passée en paramètre"
                        )
                );
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(

                    new RequestMessage(
                            HttpStatus.INTERNAL_SERVER_ERROR.value()+"",
                            e.getMessage()
                    )
            );
        }

        finally {
            return response;
        }
    }



    @PutMapping(value = "/validation-livraison", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = RequestMessage.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = RequestMessage.class)
    })
    public ResponseEntity<?> validerLivraison(@RequestBody RequestDto requestDto) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(requestDto != null){

                Livraisons item = livraisonsService.findOne(requestDto.getRef());

                if(item != null){

                    if(item.getStatutLivraison() == StatutLivraison.LIVRE){

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestMessage(

                                        HttpStatus.BAD_REQUEST.value()+"",

                                        "Cette livraison a déjà été effective. Aucune autre action n'est possible encore."
                                )
                        );
                    }
                    else {

                        String number = item.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+item.getContactDestinataire();

                        //TODO ::: TRAITEMENT QUAND LA LIVRAISON EST VALIDE
                        if(requestDto.getStatut() == StatutLivraison.LIVRE){

                            item.setStatutLivraison(requestDto.getStatut());

                            item.setMessageRemerciement(1);

                            item.setEtatNotifArchemi("EFFECTUÉ");

                            item.setNotifs("REMERCIEMENT");

                            //TODO ::: ENVOI DES NOTIFICATION - CAS DU CLIENT ACHETEUR
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



                            //TODO ::: ENVOI DES NOTIFICATION - CAS DU CLIENT PARTENAIRE
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
                        }

                            /*
                            if(requestDto.getStatut() == StatutLivraison.ANNULER){

                                item.setStatutLivraison(StatutLivraison.ANNULER);

                                item.setNotifs("ANNULATION");

                                //TODO ::: ENVOI DES NOTIFICATION - CAS DU CLIENT PARTENAIRE
                                String numberPartenaire = item.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+item.getClient().getContact();

                                if (!item.getClient().getContact().isEmpty() && !numberPartenaire.isEmpty()){

                                    SmsMessage sms = new SmsMessage();

                                    sms.setTypeMessage(4L);

                                    sms.setToId(numberPartenaire);

                                    sms.setContent("Cher Partenaire, Nous vous remercions d'avoir choisi "+item.getEntreprise().getCompagnie().toUpperCase()+" pour votre livraison. L'annulation de la livraison du colis à la référence "+item.getReference()+" a été annulé. Prière contacter la hotLine si la demande ne vient de vous au "+item.getProfile().getGareRoutiere().getCompagnie().getContact());

                                    sms.setFromName(item.getProfile().getNomPrenoms());

                                    sms.setUsername(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                    sms.setPassword(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                    sms.setSenderId(item.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                    jmsProducer.send(new JmsMessage("Sending sms to business partner", Converter.pojoToJson(sms), SmsMessage.class));
                                }


                                //TODO ::: ENVOI DES NOTIFICATION - CAS DE L'ENTREPRISE DE LIVRAISON
                                if(!item.getProfile().getGareRoutiere().getCompagnie().getEmail().isEmpty()){

                                    EmailMessage email = new EmailMessage();

                                    email.setEmail(item.getProfile().getGareRoutiere().getCompagnie().getEmail());

                                    email.setSubject("LIVRAISON ANNULÉE PAR LE LIVREUR");

                                    email.setType("LIVRAISON_CANCEL");

                                    email.setUsername(item.getProfile().getUsername().toUpperCase());

                                    email.setDefaulPwd("di-gital");

                                    jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                                }
                            }
                            */

                        livraisonsService.update(item);

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                        LocalDateTime now = LocalDateTime.now();


                        //TODO ::: MISE EN PLACE DE L'HISTORIQUE
                        Historique historique = new Historique();

                        historique.setLibelle(requestDto.getStatut() == StatutLivraison.LIVRE ? "Message de remerciement pour informer les différents acteurs a été envoyé par "+ item.getEntreprise().getCompagnie()+"  à "+dtf.format(now) : "Message de notification pour informer le client d'un éventuelle contre-temps envoyé à "+dtf.format(now));

                        historique.setLivraisons(item);

                        historique.setOrdre(Utils.generateRandom(8));

                        historique.setTypeNotification(TypeNotification.INFO);

                        Historique hist = historiqueService.create(historique);

                        if(hist.getId() == null){

                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                    new RequestMessage(

                                            HttpStatus.BAD_REQUEST.value()+"",

                                            "Impossible de finaliser le traitement de la demande"
                                    )
                            );
                        }
                        else {

                            response = ResponseEntity.status(HttpStatus.OK).body(

                                    new RequestMessage(

                                            HttpStatus.OK.value()+"",

                                            "Traitement de la demande effectuée avec succès"
                                    )
                            );
                        }
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestMessage(

                                    HttpStatus.BAD_REQUEST.value()+"",

                                    "Aucune livraison n'a été trouvé. Prière vérifier la référence saisie"
                            )
                    );
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestMessage(

                                HttpStatus.BAD_REQUEST.value()+"",

                                "Aucune variable passée en paramètre"
                        )
                );
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



    @GetMapping(value = "/confidentialite", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = ConfidentialDto.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = RequestMessage.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = RequestMessage.class)
    })
    public ResponseEntity<?> getLastValueCommisionToDate() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            ConfidentialDto confidentialDto = new ConfidentialDto();

            confidentialDto.setPc("Le traitement de vos données personnelles et le respect de votre privée sont au centre de nos attentions. " +
                    "\nVeuillez noter que cette politique de confidentialité est susceptible d'être modifiée ou complétée " +
                    "à tout moment par « Phoenix Acces Limited », notamment en vue de se conformer à toute évolution législative, réglementaire, jurisprudentielle " +
                    "ou technologique. Dans un tel cas, la date de sa mise à jour sera clairement identifiée en tête de la présente politique. " +
                    "\nCes modifications engagent l'Utilisateur dès leur mise en ligne. Il convient par conséquent que l'Utilisateur consulte régulièrement la présente politique de confidentialité de prendre connaissance de ses éventuelles modifications.\n" +
                    "Aussi, la présente politique de confidentialité des données a pour objet de vous informer sur ces mesures et engagements." +
                    "\nTraitement de vos données personnelles : Nous veillons à ne collecter que les données strictement nécessaires à la finalité des traitements mis en œuvre et à ne pas utiliser les données obtenues à des fins différentes de celles officiellement notifiées.");

            confidentialDto.setCgu("1. Les frais de maintenance concernent la maintenance de l'application hébergée sur nos serveurs, y compris les mises à jour périodiques ne sont pas à la charge du client. " +
                    "\n2. Situation de non-réponse : En cas de non-réponse de la part du Client pendant plus de 4 jours malgré les rappels de la part de la Société, la Société pourra mettre l'utilisation de l'application en attente. " +
                    "En cas d'absence de réponse du Client pendant plus de 142 jours malgré les rappels de la part de la Société, la Société pourra résilier le contrat." +
                    "\n3. La formule d'abonnement est prépayé. toute fois le client pourra le faire par tacite reconduction après validation avec les différentes partie. Un paiement effectué n'est pas remboursable. " +
                    "\n4. Le délai de livraison de la solution est de 3 à 10 jours ouvrés.");

            response = ResponseEntity.status(HttpStatus.OK).body(confidentialDto);

        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(

                    new RequestMessage(
                            HttpStatus.INTERNAL_SERVER_ERROR.value()+"",
                            e.getMessage()
                    )
            );
        }

        finally {
            return response;
        }
    }
}
