package com.phoenixacces.apps.controller.parametrage.service.compagnie;


import com.phoenixacces.apps.jms.messages.EmailMessage;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.module.assurance.SloganEntreprise;
import com.phoenixacces.apps.persistence.entities.parametrage.Entreprises;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.services.module.assurance.SloganEntrepriseService;
import com.phoenixacces.apps.persistence.services.parametrage.*;
import com.phoenixacces.apps.producer.JmsProducer;
import com.phoenixacces.apps.services.storage.service.StorageService;
import com.phoenixacces.apps.utiles.Utils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API pour le parametrage de la compagnie routière")
@Slf4j
public class CompagnieController {

    private final EntrepriseService compagnieRoutiereService;
    private final StorageService storageService;
    private final SmsCredentialService smsCredentialService;
    //private final TypeMessageService typeMessageService;
    private final PaysAutoriseService paysAutoriseService;
    private final TypeSouscrivantService typeSouscrivant;
    private final JmsProducer jmsProducer;

    private final SloganEntrepriseService sloganEntrepriseService;
    private final TypePaiementService typePaiementService;

    @Autowired
    public CompagnieController(EntrepriseService compagnieRoutiereService, StorageService storageService,
                               SmsCredentialService smsCredentialService,
                               JmsProducer jmsProducer, TypeSouscrivantService typeSouscrivant,
                               PaysAutoriseService paysAutoriseService,
                               SloganEntrepriseService sloganEntrepriseService,
                               TypePaiementService typePaiementService) {
        this.compagnieRoutiereService   = compagnieRoutiereService;
        this.storageService             = storageService;
        this.smsCredentialService       = smsCredentialService;
        //this.typeMessageService = typeMessageService;
        this.typePaiementService        = typePaiementService;
        this.jmsProducer                = jmsProducer;
        this.typeSouscrivant            = typeSouscrivant;
        this.paysAutoriseService        = paysAutoriseService;
        this.sloganEntrepriseService    = sloganEntrepriseService;
    }

    @Value(value = "${pays.autorise}")
    private String paysAutorise;

    @Value(value = "${default.password}")
    private String password;


    @GetMapping(value = "/find-all-compagnie-routiere", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllCompagnieRoutiere() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(compagnieRoutiereService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-compagnie-routiere-type-souscrivant/{id}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAll(@PathVariable(name = "id") Long id) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(compagnieRoutiereService.findAll(typeSouscrivant.findById(id)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-type-paiement", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypePaiement() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            response = ResponseEntity.status(HttpStatus.OK).body(typePaiementService.findAll());
        }
        catch (Exception e) {

            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "upload-logo-compagnie-routiere", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<? extends Object> storeFile (@RequestParam(name = "file") MultipartFile multipartFile) throws Exception {
        ResponseEntity<? extends Object> responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).body("No content to upload");
        if(multipartFile != null){
            storageService.store(multipartFile);
            log.info("[ DI-GITAL {} - UPLOAD ] :: store upload file on server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        else{
            log.info("[ DI-GITAL {} - UPLOAD ] :: Fail to store file on server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }

        return null;
    }


    @PostMapping(value = "/create-new-compagnie-routiere", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewCompagnieRoutiere (@RequestBody Entreprises model) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            Entreprises data = null;

            if(model == null) {
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                if(compagnieRoutiereService.findOneByComapgnie(model.getCompagnie().toUpperCase()) == null ) {

                    data = new Entreprises();

                    data.setLogo(model.getLogo());

                    data.setCompagnie(model.getCompagnie().toUpperCase());

                    data.setAbbrev(model.getAbbrev().toUpperCase());

                    data.setAdresse(model.getAdresse().toUpperCase());

                    data.setBp(model.getBp() == null ? "N/D" : model.getBp().toUpperCase());

                    data.setEmail(model.getEmail());

                    data.setRccm(model.getRccm() == null ? "N/D" : model.getRccm().toUpperCase());

                    data.setWeb(model.getWeb());

                    data.setContact(model.getContact());

                    data.setResponsable(model.getResponsable().toUpperCase());

                    data.setContactResponsable(model.getContactResponsable());

                    data.setTypeContrat(model.getTypeContrat());

                    data.setDateEffetContrat(model.getDateEffetContrat());

                    data.setSmsCredential(model.getSmsCredential());

                    data.setLogo(model.getLogo());

                    data.setPaysAutorise(paysAutoriseService.findOne(1L));

                    data.setTaciteReconduit(model.getTypeContrat().getId() == 2);

                    data.setDateFinContrat(data.isTaciteReconduit() ? null : Utils.getDateExpirationOnMonth(model.getDureeContrat().getDuree()));

                    data.setDureeContrat(model.getDureeContrat());

                    data.setTauxCommLivreur(Math.round(model.getTauxCommLivreur()/100));

                    data.setSlogan(model.getSlogan());

                    data.setTypeSouscrivant(model.getTypeSouscrivant());

                    Entreprises entreprises = compagnieRoutiereService.create(data);

                    if (entreprises.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                    else {

                        //Mise à jour de l'affectation du Sender id séléection
                        SmsCredential smsCredential = smsCredentialService.findOne(model.getSmsCredential().getSenderId());
                        if(smsCredential != null){
                            smsCredentialService.affectedUpdate(smsCredential.getId());
                        }

                        //Envoi de sms au responsable pour notification
                        String number       = model.getPaysAutorise().getIndicatif()+""+model.getContactResponsable();
                        //String promReceiver = WordUtils.capitalize(model.getResponsable().toLowerCase()).split(" ")[1];

                        if (!number.isEmpty()){
                            SmsMessage sms = new SmsMessage();
                            sms.setTypeMessage(1L);
                            sms.setToId(number);
                            sms.setContent("Bonjour "+model.getAbbrev().toUpperCase()+", nous sommes ravis de vous compter parmi nos nouveaux collaborateurs. La configuration de votre espace pour votre entreprise est en cours.");
                            sms.setFromName(model.getResponsable().toUpperCase());
                            sms.setUsername(model.getSmsCredential().getUsername());
                            sms.setPassword(model.getSmsCredential().getPassword());
                            sms.setSenderId(model.getSmsCredential().getSenderId());
                            jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
                        }

                        if(!model.getEmail().isEmpty()){
                            EmailMessage email = new EmailMessage();
                            email.setEmail(model.getEmail());
                            email.setSubject("Di-Gital ::: Bienvenu dans l'univers numérique");
                            email.setType("BIENVENU");
                            jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                        }


                        //Enregistrement du slogan dans la table slogan
                        if(model.getTypeSouscrivant().getId() == 6 && model.getSlogan() != null){

                            SloganEntreprise sloganEntreprise = new SloganEntreprise();

                            sloganEntreprise.setEntreprises(entreprises);

                            sloganEntreprise.setSlogan(model.getSlogan());

                            sloganEntrepriseService.create(sloganEntreprise);
                        }


                        response =  ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new RequestResponse(
                                    new RequestInformation(400, "Données existante, Doublons évité"),
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



    @PutMapping(value = "/update-compagnie-routiere", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = Entreprises.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteCompagnieRoutiere(@RequestBody Entreprises item) {

        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                Entreprises up_data = compagnieRoutiereService.findOne(item.getId());

                if(up_data!= null) {
                    up_data.setCompagnie(item.getCompagnie().toUpperCase());
                    up_data.setAbbrev(item.getAbbrev().toUpperCase());
                    up_data.setAdresse(item.getAdresse().toUpperCase());
                    up_data.setBp(item.getBp());
                    up_data.setEmail(item.getEmail().toLowerCase());
                    up_data.setRccm(item.getRccm().toUpperCase());
                    up_data.setWeb(item.getWeb());
                    up_data.setContact(item.getContact());
                    up_data.setResponsable(item.getResponsable().toUpperCase());
                    up_data.setContactResponsable(item.getContactResponsable());
                    compagnieRoutiereService.update(up_data);

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



    @PutMapping(value = "/delete-item-compagnie-routiere", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "CompagnieRoutiere agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Entreprises.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteCompagnieRoutiere(@RequestBody Entreprises object) {

        try {
            if(object != null){

                Entreprises item = compagnieRoutiereService.findOne(object.getId());
                if(item!= null) {
                    compagnieRoutiereService.disable(item.getId());
                    Entreprises up = item;
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
                            new RequestInformation(400, "Impossible de faire la suppression de la compagnie routière "+object.getCompagnie()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de la compagnie routière "+object.getCompagnie()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-compagnie-routiere", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "CompagnieRoutiere model", notes = "TypeContrat model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Entreprises.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeCompagnieRoutiere(@RequestBody Entreprises object) {

        try {
            if(object != null){

                Entreprises item = compagnieRoutiereService.findOne(object.getId());
                if(item!= null) {
                    compagnieRoutiereService.enabled(item.getId());
                    Entreprises up = item;
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
                            new RequestInformation(400, "Impossible de faire l'activation de la compagnie "+object.getCompagnie()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de la compagnie "+object.getCompagnie()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
