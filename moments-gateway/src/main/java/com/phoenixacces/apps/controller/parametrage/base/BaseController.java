package com.phoenixacces.apps.controller.parametrage.base;

import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.parametrage.*;
import com.phoenixacces.apps.persistence.entities.service.TypeMessage;
import com.phoenixacces.apps.persistence.services.parametrage.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API pour le parametrage de base")
@Slf4j
public class BaseController {

    private final SmsCredentialService smsCredentialService;
    private final TypeContratService typeContratService;
    private final TypeMessageService typeMessageService;
    private final MotifSuspensionCollaborationService motifSuspensionCollaborationService;
    private final PaysAutoriseService paysAutoriseService;
    private final TypeSouscrivantService typeSouscrivantService;
    private final SuiviDemandeService suiviDemandeService;
    private final DureeContratService dureeContratService;
    private final CiviliteService civiliteService;

    @Autowired
    public BaseController(SmsCredentialService smsCredentialService, TypeContratService typeContratService,
                          MotifSuspensionCollaborationService motifSuspensionCollaborationService,
                          TypeMessageService typeMessageService, PaysAutoriseService paysAutoriseService,
                          TypeSouscrivantService typeSouscrivantService, SuiviDemandeService suiviDemandeService,
                          DureeContratService dureeContratService, CiviliteService civiliteService) {
        this.smsCredentialService                   = smsCredentialService;
        this.typeContratService                     = typeContratService;
        this.motifSuspensionCollaborationService    = motifSuspensionCollaborationService;
        this.typeMessageService                     = typeMessageService;
        this.paysAutoriseService                    = paysAutoriseService;
        this.typeSouscrivantService                 = typeSouscrivantService;
        this.suiviDemandeService                    = suiviDemandeService;
        this.dureeContratService                    = dureeContratService;
        this.civiliteService                        = civiliteService;
    }


    @GetMapping(value = "/find-all-duree-contrat", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllDureeContrat() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            response = ResponseEntity.status(HttpStatus.OK).body(dureeContratService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-sms-credential/{senderid}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllSenderId(@PathVariable(name = "senderid") String senderid) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(senderid != null){

               response = ResponseEntity.status(HttpStatus.OK).body(smsCredentialService.findAll(senderid));
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


    @GetMapping(value = "/find-all-sms-credential", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllSmsCredential() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(smsCredentialService.findAlls());
            //response = ResponseEntity.status(HttpStatus.OK).body(smsCredentialService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-sms-credential-affected", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllSmsCredentialAffeted() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(smsCredentialService.findAllAffected());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/create-new-sms-credential", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewSmsCredential1 (@RequestBody SmsCredential model) {

        try {
            SmsCredential data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(smsCredentialService.findOne(model.getSenderId()) == null ) {
                    data = new SmsCredential();
                    data.setUsername(model.getUsername());
                    data.setPassword(model.getPassword());
                    data.setSenderId(model.getSenderId());
                    data.setRef(model.getRef().toUpperCase());
                    SmsCredential _ag_ = smsCredentialService.create(data);

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



    @PutMapping(value = "/update-sms-credential", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = SmsCredential.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteSmsCredential(@RequestBody SmsCredential item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                SmsCredential up_data = smsCredentialService.findOne(item.getId());

                if(up_data!= null) {
                    up_data.setUsername(item.getUsername());
                    up_data.setPassword(item.getPassword());
                    up_data.setSenderId(item.getSenderId());
                    up_data.setRef(item.getRef().toUpperCase());
                    smsCredentialService.update(up_data);
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



    @PutMapping(value = "/delete-item-sms-credential", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = SmsCredential.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteSmsCredential(@RequestBody SmsCredential object) {

        try {
            if(object != null){

                SmsCredential item = smsCredentialService.findOne(object.getId());
                if(item!= null) {
                    smsCredentialService.disable(item.getId());
                    SmsCredential up = item;
                    if (up.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de supprimer l'enregistrement selectionné"),
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
                            new RequestInformation(400, "Impossible de faire la désactivation de l'enregistrement selectionné "+object.getSenderId()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la désactivation de l'enregistrement selectionné "+object.getSenderId()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-sms-credential", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "SmsCredential model", notes = "TypeContrat model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = SmsCredential.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeTypeContrat(@RequestBody SmsCredential object) {

        try {
            if(object != null){

                SmsCredential item = smsCredentialService.findOne(object.getId());
                if(item!= null) {
                    smsCredentialService.enable(item.getId());
                    SmsCredential up = item;
                    if (up.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible d'activer l'enregistrement selectionné"),
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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement selectionné "+object.getSenderId()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement selectionné "+object.getSenderId()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-sms-credential", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllSmsCredential() {

        try {

            List<SmsCredential> items = smsCredentialService.findAll();

            for (SmsCredential item :items){

                SmsCredential models = smsCredentialService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    smsCredentialService.update(models);
                }
            }

            System.gc();

            if(smsCredentialService.findAll().isEmpty()) {

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


    //API Type Contrat
    @GetMapping(value = "/find-all-type-contrat-module-livraison", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeContratByModule() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeContratService.findAll("LIVRAISON"));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    //API Type Contrat
    @GetMapping(value = "/find-all-type-contrat", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeContrat() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeContratService.findAlls());
            //response = ResponseEntity.status(HttpStatus.OK).body(typeContratService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-type-contrat-disponible", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllDisponible() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeContratService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/create-new-type-contrat", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewTypeContrat (@RequestBody TypeContrat model) {

        try {
            TypeContrat data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(typeContratService.findOne(model.getTypecontrat()) == null ) {
                    data = new TypeContrat();
                    data.setTypecontrat(model.getTypecontrat().toUpperCase());
                    TypeContrat _ag_ = typeContratService.create(data);

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



    @PutMapping(value = "/update-type-contrat", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = TypeContrat.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteTypeContrat(@RequestBody TypeContrat item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                TypeContrat up_data = typeContratService.findOne(item.getId());

                if(up_data!= null) {
                    up_data.setTypecontrat(item.getTypecontrat().toUpperCase());
                    typeContratService.update(up_data);
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



    @PutMapping(value = "/delete-item-type-contrat", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeContrat.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteTypeContrat(@RequestBody TypeContrat object) {

        try {
            if(object != null){

                TypeContrat item = typeContratService.findOne(object.getId());
                if(item!= null) {
                    typeContratService.disable(item.getId());
                    TypeContrat up = item;
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
                            new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getTypecontrat()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getTypecontrat()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-type-contrat", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypeContrat model", notes = "TypeContrat model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeContrat.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeTypeContrat(@RequestBody TypeContrat object) {

        try {
            if(object != null){

                TypeContrat item = typeContratService.findOne(object.getId());
                if(item!= null) {
                    typeContratService.enable(item.getId());
                    TypeContrat up = item;
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
                            new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getTypecontrat()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getTypecontrat()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-type-contrat", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllTypeContrat() {

        try {

            List<TypeContrat> items = typeContratService.findAll();

            for (TypeContrat item :items){

                TypeContrat models = typeContratService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    typeContratService.update(models);
                }
            }

            System.gc();

            if(typeContratService.findAll().isEmpty()) {

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


    //API Motif Suspension Collaboration
    @GetMapping(value = "/find-all-motif-suspension-collaboration", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllMotifSuspensionCollaboration() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(motifSuspensionCollaborationService.findAlls());
            //response = ResponseEntity.status(HttpStatus.OK).body(motifSuspensionCollaborationService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-motif-suspension-collaboration", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewMotifSuspensionCollaboration (@RequestBody MotifSuspensionCollaboration model) {

        try {
            MotifSuspensionCollaboration data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(motifSuspensionCollaborationService.findOne(model.getMotif()) == null ) {
                    data = new MotifSuspensionCollaboration();
                    data.setMotif(model.getMotif().toUpperCase());
                    MotifSuspensionCollaboration _ag_ = motifSuspensionCollaborationService.create(data);

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


    @PutMapping(value = "/update-motif-suspension-collaboration", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = MotifSuspensionCollaboration.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteMotifSuspensionCollaboration(@RequestBody MotifSuspensionCollaboration item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                MotifSuspensionCollaboration up_data = motifSuspensionCollaborationService.findOne(item.getId());

                if(up_data!= null) {
                    up_data.setMotif(item.getMotif().toUpperCase());
                    motifSuspensionCollaborationService.update(up_data);
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


    @PutMapping(value = "/delete-item-motif-suspension-collaboration", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = MotifSuspensionCollaboration.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteMotifSuspensionCollaboration(@RequestBody MotifSuspensionCollaboration object) {

        try {
            if(object != null){

                MotifSuspensionCollaboration item = motifSuspensionCollaborationService.findOne(object.getId());
                if(item!= null) {
                    motifSuspensionCollaborationService.disable(item.getId());
                    MotifSuspensionCollaboration up = item;
                    if (up.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de supprimer le Motif Suspension Collaboration"),
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
                            new RequestInformation(400, "Impossible de faire l'activation du Motif Suspension Collaboration "+object.getMotif()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du Motif Suspension Collaboration "+object.getMotif()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-motif-suspension-collaboration", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MotifSuspensionCollaboration model", notes = "TypeContrat model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = MotifSuspensionCollaboration.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeMotifSuspensionCollaborationMdle(@RequestBody MotifSuspensionCollaboration object) {

        try {
            if(object != null){

                MotifSuspensionCollaboration item = motifSuspensionCollaborationService.findOne(object.getId());
                if(item!= null) {
                    motifSuspensionCollaborationService.enable(item.getId());
                    MotifSuspensionCollaboration up = item;
                    if (up.getId() == null) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible d'activer le Motif Suspension Collaboration"),
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
                            new RequestInformation(400, "Impossible de faire l'activation du Motif Suspension Collaboration "+object.getMotif()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du Motif Suspension Collaboration "+object.getMotif()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping(value = "/deleted-all-motif-suspension-collaboration", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllMotifSuspensionCollaboration() {

        try {

            List<MotifSuspensionCollaboration> items = motifSuspensionCollaborationService.findAll();

            for (MotifSuspensionCollaboration item :items){

                MotifSuspensionCollaboration models = motifSuspensionCollaborationService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    motifSuspensionCollaborationService.update(models);
                }
            }

            System.gc();

            if(motifSuspensionCollaborationService.findAll().isEmpty()) {

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


    //API Type Message
    @GetMapping(value = "/find-all-type-message", produces = "application/json")
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



    @GetMapping(value = "/find-all-type-message-disponible", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeMessageDisponible() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeMessageService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/create-new-type-message", consumes = "application/json", produces = "application/json")
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

                if(typeMessageService.findOne(model.getTypemessgae()) == null ) {
                    data = new TypeMessage();
                    data.setTypemessgae(model.getTypemessgae().toUpperCase());
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
                                    new RequestInformation(400, "Données existante, Doublons évité"),
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



    @PutMapping(value = "/update-type-message", consumes = "application/json", produces = "application/json")
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping(value = "/delete-item-type-message", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypeMessage model", notes = "Representative agent model")
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
                    typeMessageService.disable(item.getId());
                    TypeMessage up = item;
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
                            new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getTypemessgae()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getTypemessgae()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-type-message", consumes = "application/json", produces = "application/json")
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
                    typeMessageService.enable(item.getId());
                    TypeMessage up = item;
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
                            new RequestInformation(400, "Impossible de faire l'activation du type message "+object.getTypemessgae()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type messgae "+object.getTypemessgae()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-type-message", consumes = "application/json", produces = "application/json")
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



    //API Pays autorisé
    @GetMapping(value = "/find-all-pays-autorise", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllPaysAutorise() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(paysAutoriseService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-pays-autorise-disponible", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllPaysAutoriseDisponible() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(paysAutoriseService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/create-new-pays-autorise", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewPaysAutorise (@RequestBody PaysAutorise model) {

        try {
            PaysAutorise data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(paysAutoriseService.findOne(model.getPaysautorise().toUpperCase()) == null ) {
                    data = new PaysAutorise();
                    data.setPaysautorise(model.getPaysautorise().toUpperCase());
                    data.setIndicatif(model.getIndicatif());
                    PaysAutorise _ag_ = paysAutoriseService.create(data);

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
                                    new RequestInformation(400, "Données existante, Doublons évité"),
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



    @PutMapping(value = "/update-pays-autorise", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = PaysAutorise.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadtePaysAutorise(@RequestBody PaysAutorise item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                PaysAutorise up_data = paysAutoriseService.findOne(item.getId());

                if(up_data!= null) {
                    up_data.setPaysautorise(item.getPaysautorise().toUpperCase());
                    up_data.setIndicatif(item.getIndicatif());
                    paysAutoriseService.update(up_data);
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



    @PutMapping(value = "/delete-item-pays-autorise", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "PaysAutorise model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = PaysAutorise.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deletePaysAutorise(@RequestBody PaysAutorise object) {

        try {
            if(object != null){

                PaysAutorise item = paysAutoriseService.findOne(object.getId());
                if(item!= null) {
                    paysAutoriseService.disable(item.getId());
                    PaysAutorise up = item;
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
                            new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getPaysautorise()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getPaysautorise()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-pays-autorise", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "PaysAutorise model", notes = "PaysAutorise model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = PaysAutorise.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activePaysAutorise(@RequestBody PaysAutorise object) {

        try {
            if(object != null){

                PaysAutorise item = paysAutoriseService.findOne(object.getId());
                if(item!= null) {
                    paysAutoriseService.enable(item.getId());
                    PaysAutorise up = item;
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
                            new RequestInformation(400, "Impossible de faire l'activation du type message "+object.getPaysautorise()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type messgae "+object.getPaysautorise()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-pays-autorise", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllPaysAutorise() {

        try {

            List<PaysAutorise> items = paysAutoriseService.findAll();

            for (PaysAutorise item :items){

                PaysAutorise models = paysAutoriseService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    paysAutoriseService.update(models);
                }
            }

            System.gc();

            if(paysAutoriseService.findAll().isEmpty()) {

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


    //API Type Souscrivant
    @GetMapping(value = "/find-all-type-souscrivant", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeSouscrivant() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeSouscrivantService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-type-souscrivant-disponible", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeSouscrivantDisponible() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeSouscrivantService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/create-new-type-souscrivant", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewTypeSouscrivant (@RequestBody TypeSouscrivant model) {

        try {
            TypeSouscrivant data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(typeSouscrivantService.findOne(model.getTypesouscrivant()) == null ) {
                    data = new TypeSouscrivant();
                    data.setTypesouscrivant(model.getTypesouscrivant().toUpperCase());
                    TypeSouscrivant _ag_ = typeSouscrivantService.create(data);

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
                                    new RequestInformation(400, "Données existante, Doublons évité"),
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



    @PutMapping(value = "/update-type-souscrivant", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = TypeSouscrivant.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteTypeSouscrivant(@RequestBody TypeSouscrivant item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                TypeSouscrivant up_data = typeSouscrivantService.findById(item.getId());

                if(up_data!= null) {
                    up_data.setTypesouscrivant(item.getTypesouscrivant().toUpperCase());
                    typeSouscrivantService.update(up_data);
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



    @PutMapping(value = "/delete-item-type-souscrivant", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypeSouscrivant model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeSouscrivant.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteTypeSouscrivant(@RequestBody TypeSouscrivant object) {

        try {
            if(object != null){

                TypeSouscrivant item = typeSouscrivantService.findById(object.getId());
                if(item!= null) {
                    typeSouscrivantService.disable(item.getId());
                    TypeSouscrivant up = item;
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
                            new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getTypesouscrivant()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getTypesouscrivant()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-type-souscrivant", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypeSouscrivant model", notes = "TypeSouscrivant model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeSouscrivant.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeTypeSouscrivant(@RequestBody TypeSouscrivant object) {

        try {
            if(object != null){

                TypeSouscrivant item = typeSouscrivantService.findById(object.getId());
                if(item!= null) {
                    typeSouscrivantService.enable(item.getId());
                    TypeSouscrivant up = item;
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
                            new RequestInformation(400, "Impossible de faire l'activation du type message "+object.getTypesouscrivant()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type messgae "+object.getTypesouscrivant()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-type-souscrivant", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllTypeSouscrivant() {

        try {

            List<TypeSouscrivant> items = typeSouscrivantService.findAll();

            for (TypeSouscrivant item :items){

                TypeSouscrivant models = typeSouscrivantService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    typeSouscrivantService.update(models);
                }
            }

            System.gc();

            if(typeSouscrivantService.findAll().isEmpty()) {

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


    //API Suivi demande
    @GetMapping(value = "/find-all-suivi-demande", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllSuiviDemande() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(suiviDemandeService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    //API Suivi demande
    @GetMapping(value = "/find-all-suivi-demande-livraison/{suiviId}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllSuiviDemandeLivraison(@PathVariable(name = "suiviId") Long suiviId) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(suiviDemandeService.findAll(suiviId, "livraison"));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-suivi-demande-disponible", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllSuivis() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(suiviDemandeService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PostMapping(value = "/create-new-suivi-demande", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewSuiviDemande (@RequestBody SuiviDemande model) {

        try {
            SuiviDemande data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(suiviDemandeService.findOne(model.getSuivi()) == null ) {
                    data = new SuiviDemande();
                    data.setSuivi(model.getSuivi().toUpperCase());
                    SuiviDemande _ag_ = suiviDemandeService.create(data);

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



    @PutMapping(value = "/update-suivi-demande", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = SuiviDemande.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteSuiviDemande(@RequestBody SuiviDemande item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                SuiviDemande up_data = suiviDemandeService.findOne(item.getId());

                if(up_data!= null) {
                    up_data.setSuivi(item.getSuivi().toUpperCase());
                    suiviDemandeService.update(up_data);
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



    @PutMapping(value = "/delete-item-suivi-demande", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = SuiviDemande.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteSuiviDemande(@RequestBody SuiviDemande object) {

        try {
            if(object != null){

                SuiviDemande item = suiviDemandeService.findOne(object.getId());
                if(item!= null) {
                    suiviDemandeService.disable(item.getId());
                    SuiviDemande up = item;
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
                            new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getSuivi()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getSuivi()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-suivi-demande", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "SuiviDemande model", notes = "SuiviDemande model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = SuiviDemande.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeSuiviDemande(@RequestBody SuiviDemande object) {

        try {
            if(object != null){

                SuiviDemande item = suiviDemandeService.findOne(object.getId());
                if(item!= null) {
                    suiviDemandeService.enable(item.getId());
                    SuiviDemande up = item;
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
                            new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getSuivi()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getSuivi()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-suivi-demande", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllSuiviDemande() {

        try {

            List<SuiviDemande> items = suiviDemandeService.findAll();

            for (SuiviDemande item :items){

                SuiviDemande models = suiviDemandeService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    suiviDemandeService.update(models);
                }
            }

            System.gc();

            if(suiviDemandeService.findAll().isEmpty()) {

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




    //API Suivi demande
    @GetMapping(value = "/find-all-suivi-demande-disponible/{id}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllSuiviDemandeDispo(@PathVariable(name = "id") Long demandeId) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(suiviDemandeService.demandeDisponible(demandeId));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    //API Civilité
    @GetMapping(value = "/find-all-civilite", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllCivilite() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(civiliteService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-civilite", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewCivilite (@RequestBody Civilite model) {

        try {
            Civilite data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(civiliteService.findOne(model.getCivilite()) == null ) {

                    data = new Civilite();

                    data.setCivilite(model.getCivilite().toUpperCase());

                    data.setCodeCivilite(model.getCodeCivilite().toUpperCase());

                    Civilite _ag_ = civiliteService.create(data);

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



    @PutMapping(value = "/update-civilite", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = Civilite.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteCivilite(@RequestBody Civilite item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                Civilite up_data = civiliteService.findById(item.getId());

                if(up_data!= null) {

                    up_data.setCivilite(item.getCivilite().toUpperCase());

                    up_data.setCodeCivilite(item.getCodeCivilite().toUpperCase());

                    civiliteService.update(up_data);
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



    @PutMapping(value = "/delete-item-civilite", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Civilite.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteCivilite(@RequestBody Civilite object) {

        try {
            if(object != null){

                Civilite item = civiliteService.findById(object.getId());
                if(item!= null) {
                    civiliteService.disable(item.getId());
                    Civilite up = item;
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
                            new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getCivilite()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getCivilite()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-civilite", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Civilite model", notes = "Civilite model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Civilite.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeCivilite(@RequestBody Civilite object) {

        try {
            if(object != null){

                Civilite item = civiliteService.findById(object.getId());
                if(item!= null) {
                    civiliteService.enable(item.getId());
                    Civilite up = item;
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
                            new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getCivilite()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getCivilite()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-civilite", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllCivilite() {

        try {

            List<Civilite> items = civiliteService.findAll();

            for (Civilite item :items){

                Civilite models = civiliteService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    civiliteService.update(models);
                }
            }

            System.gc();

            if(civiliteService.findAll().isEmpty()) {

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
}
