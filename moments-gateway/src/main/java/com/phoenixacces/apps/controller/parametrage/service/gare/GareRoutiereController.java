package com.phoenixacces.apps.controller.parametrage.service.gare;

import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.parametrage.EntitesOrOE;
import com.phoenixacces.apps.persistence.services.parametrage.EntrepriseService;
import com.phoenixacces.apps.persistence.services.parametrage.EntitesOrOEService;
import com.phoenixacces.apps.persistence.services.parametrage.SmsCredentialService;
import com.phoenixacces.apps.persistence.services.parametrage.TypeMessageService;
import com.phoenixacces.apps.producer.JmsProducer;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API pour le parametrage de la gare routière")
@Slf4j
public class GareRoutiereController {

    private final EntitesOrOEService gareRoutiereService;
    private final SmsCredentialService smsCredentialService;
    private final TypeMessageService typeMessageService;
    private final EntrepriseService compagnieRoutiereService;
    private final JmsProducer jmsProducer;

    @Autowired
    public GareRoutiereController(SmsCredentialService smsCredentialService, TypeMessageService typeMessageService,
                                  JmsProducer jmsProducer, EntitesOrOEService gareRoutiereService,
                                  EntrepriseService compagnieRoutiereService){
        this.smsCredentialService = smsCredentialService;
        this.typeMessageService = typeMessageService;
        this.jmsProducer = jmsProducer;
        this.gareRoutiereService = gareRoutiereService;
        this.compagnieRoutiereService = compagnieRoutiereService;
    }

    @GetMapping(value = "/find-all-gare-routiere", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllGareRoutiere() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(gareRoutiereService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-gare-routiere-disponible", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllGareRoutiereDisponible() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(gareRoutiereService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-gare-routiere-disponible-compagnie/{id}", produces = "application/json")
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
            response = ResponseEntity.status(HttpStatus.OK).body(gareRoutiereService.findAll(compagnieRoutiereService.findOne(id)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/create-new-gare-routiere", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewGareRoutiere (@RequestBody EntitesOrOE model) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            EntitesOrOE data = null;

            if(model == null) {

                //throw new Exception("Impossible d'enregistrer les informations soumises");

                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                if(gareRoutiereService.findOne(model.getCompagnie(), model.getGareRoutiere().toUpperCase()) == null ) {

                    data = new EntitesOrOE();

                    data.setGareRoutiere(model.getGareRoutiere().toUpperCase());

                    data.setContactResponsableGareRoutiere(model.getContactResponsableGareRoutiere());

                    data.setNomresponsable(model.getNomresponsable().toUpperCase());

                    data.setSiteGeoGareRoutiere(model.getSiteGeoGareRoutiere().toUpperCase());

                    data.setCompagnie(model.getCompagnie());

                    data.setContact(model.getContact());

                    data.setFax(model.getFax() == null ? "" : model.getFax());

                    EntitesOrOE entitesOrOE = gareRoutiereService.create(data);

                    if (entitesOrOE.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestResponse(

                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                    else {

                        //Envoi de l'sms au responable de la gare
                        String number  = model.getCompagnie().getPaysAutorise().getIndicatif()+""+model.getContactResponsableGareRoutiere();

                        if (!number.isEmpty()){

                            SmsMessage sms = new SmsMessage();

                            sms.setTypeMessage(4L);

                            sms.setToId(number);

                            sms.setContent("Bonjour "+ WordUtils.capitalize(model.getNomresponsable().toLowerCase())+", suite à la collaboration avec votre entreprise, la configuration de l'espace pour votre agence "+model.getGareRoutiere().toUpperCase()+" est effective.");

                            sms.setFromName(model.getNomresponsable().toUpperCase());

                            sms.setUsername(model.getCompagnie().getSmsCredential().getUsername());

                            sms.setPassword(model.getCompagnie().getSmsCredential().getPassword());

                            sms.setSenderId(model.getCompagnie().getSmsCredential().getSenderId());

                            jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
                        }


                        String number1  = model.getCompagnie().getPaysAutorise().getIndicatif()+""+model.getCompagnie().getContactResponsable();

                        if (!number1.isEmpty()){

                            SmsMessage smsMessage = new SmsMessage();

                            smsMessage.setTypeMessage(4L);

                            smsMessage.setToId(number1);

                            smsMessage.setContent("Bonjour "+ WordUtils.capitalize(model.getCompagnie().getResponsable().toLowerCase())+", suite à la collaboration avec votre entreprise, la configuration de l'espace pour l'agence "+model.getGareRoutiere().toUpperCase()+" est effective.");

                            smsMessage.setFromName(model.getNomresponsable().toUpperCase());

                            smsMessage.setUsername(model.getCompagnie().getSmsCredential().getUsername());

                            smsMessage.setPassword(model.getCompagnie().getSmsCredential().getPassword());

                            smsMessage.setSenderId(model.getCompagnie().getSmsCredential().getSenderId());

                            jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(smsMessage), SmsMessage.class));
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



    @PutMapping(value = "/update-gare-routiere", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = EntitesOrOE.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteGareRoutiere(@RequestBody EntitesOrOE item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                EntitesOrOE up_data = gareRoutiereService.findById(item.getId());

                if(up_data!= null) {

                    up_data.setGareRoutiere(item.getGareRoutiere().toUpperCase());
                    up_data.setContactResponsableGareRoutiere(item.getContactResponsableGareRoutiere().toUpperCase());
                    up_data.setNomresponsable(item.getNomresponsable().toUpperCase());
                    up_data.setSiteGeoGareRoutiere(item.getSiteGeoGareRoutiere().toUpperCase());
                    up_data.setCompagnie(item.getCompagnie());
                    up_data.setContact(item.getContact());
                    up_data.setFax(item.getFax());
                    gareRoutiereService.update(up_data);

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
    

    @PutMapping(value = "/delete-item-gare-routiere", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "GareRoutiere agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = EntitesOrOE.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteGareRoutiere(@RequestBody EntitesOrOE object) {

        try {
            if(object != null){

                EntitesOrOE item = gareRoutiereService.findById(object.getId());
                
                if(item!= null) {
                    
                    gareRoutiereService.disable(item.getId());
                    
                    EntitesOrOE up = item;
                    
                    if (up.getId() == null) {
                        
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée"),
                                new RequestMessage(null, "Fail")
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
                            new RequestInformation(400, "Impossible de faire la suppression de la gare routière "+object.getGareRoutiere()),
                            new RequestMessage(null, "Fail")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de la gare routière "+object.getGareRoutiere()),
                        new RequestMessage(null, "Fail")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-gare-routiere", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "GareRoutiere model", notes = "TypeContrat model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = EntitesOrOE.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeGareRoutiere(@RequestBody EntitesOrOE object) {

        try {
            if(object != null){

                EntitesOrOE item = gareRoutiereService.findById(object.getId());
                
                if(item!= null) {
                    gareRoutiereService.enabled(item.getId());
                    EntitesOrOE up = item;
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
                        new RequestInformation(400, "Impossible de faire l'activation de la gare routière "+object.getGareRoutiere()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
