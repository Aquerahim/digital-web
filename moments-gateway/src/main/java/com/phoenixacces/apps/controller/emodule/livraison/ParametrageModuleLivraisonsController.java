package com.phoenixacces.apps.controller.emodule.livraison;

import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.colis.NatureColis;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typeengin.TypeEngins;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.typezone.TypeZoneCouverture;
import com.phoenixacces.apps.persistence.entities.parametrage.livraison.zonecouverture.ZoneCouverture;
import com.phoenixacces.apps.persistence.services.parametrage.mLivraison.TypeColisLivraisonService;
import com.phoenixacces.apps.persistence.services.parametrage.mLivraison.TypeEnginsService;
import com.phoenixacces.apps.persistence.services.parametrage.mLivraison.TypeZoneCouvertureService;
import com.phoenixacces.apps.persistence.services.parametrage.mLivraison.ZoneCouvertureService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API pour le Parametrage du Module Livraison")
@Slf4j
public class ParametrageModuleLivraisonsController {

    private final TypeZoneCouvertureService typeZoneCouvertureService;
    private final ZoneCouvertureService zoneCouvertureService;
    private final TypeColisLivraisonService typeColisLivraisonService;
    private final TypeEnginsService typeEnginsService;

    @Autowired
    public ParametrageModuleLivraisonsController(
            TypeZoneCouvertureService typeZoneCouvertureService,
            ZoneCouvertureService zoneCouvertureService,
            TypeColisLivraisonService typeColisLivraisonService,
            TypeEnginsService typeEnginsService
    ){
        this.typeZoneCouvertureService = typeZoneCouvertureService;
        this.zoneCouvertureService = zoneCouvertureService;
        this.typeColisLivraisonService = typeColisLivraisonService;
        this.typeEnginsService = typeEnginsService;
    }


    // TODO:: API TYPE DE ZONE DE COUVERTURE
    @GetMapping(value = "/find-all-type-zone", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeZoneCouverture() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeZoneCouvertureService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-type-zone", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewTypeZoneCouverture (@RequestBody TypeZoneCouverture model) {

        try {
            TypeZoneCouverture data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(typeZoneCouvertureService.findOne(model.getTypezone()) == null ) {

                    data = new TypeZoneCouverture();

                    data.setTypezone(model.getTypezone().toUpperCase());

                    TypeZoneCouverture _ag_ = typeZoneCouvertureService.create(data);

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



    @PutMapping(value = "/update-type-zone", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = TypeZoneCouverture.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteTypeZoneCouverture(@RequestBody TypeZoneCouverture item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                TypeZoneCouverture up_data = typeZoneCouvertureService.findOne(item.getId());

                if(up_data!= null) {
                    
                    up_data.setTypezone(item.getTypezone().toUpperCase());
                    
                    typeZoneCouvertureService.update(up_data);
                    
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



    @PutMapping(value = "/delete-item-type-zone", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeZoneCouverture.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteTypeZoneCouverture(@RequestBody TypeZoneCouverture object) {

        try {
            if(object != null){

                TypeZoneCouverture item = typeZoneCouvertureService.findOne(object.getId());

                if(item!= null) {

                    typeZoneCouvertureService.disable(item.getId());

                    if (item.getId() == null) {
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
                            new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getTypezone()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getTypezone()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-type-zone", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypeZoneCouverture model", notes = "TypeZoneCouverture model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeZoneCouverture.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeTypeZoneCouverture(@RequestBody TypeZoneCouverture object) {

        try {
            if(object != null){

                TypeZoneCouverture item = typeZoneCouvertureService.findOne(object.getId());

                if(item!= null) {

                    typeZoneCouvertureService.enable(item.getId());

                    TypeZoneCouverture up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getTypezone()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getTypezone()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-type-zone", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllTypeZoneCouverture() {

        try {

            List<TypeZoneCouverture> items = typeZoneCouvertureService.findAll();

            for (TypeZoneCouverture item :items){

                TypeZoneCouverture models = typeZoneCouvertureService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    typeZoneCouvertureService.update(models);
                }
            }

            return getResponseEntity(typeZoneCouvertureService.findAll().isEmpty());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    // TODO:: API ZONE DE COUVERTURE
    @GetMapping(value = "/find-all-zone-couverture-by-type/{typeZoneId}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllZoneCouvertureByType(@PathVariable(name = "typeZoneId") Long typeZoneId) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(zoneCouvertureService.findAll(typeZoneCouvertureService.findOne(typeZoneId)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/find-all-zone-couverture-disponible", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllZoneCouvertureDisponible() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(zoneCouvertureService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-zone-couverture", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewZoneCouverture (@RequestBody ZoneCouverture model) {

        try {
            ZoneCouverture data = null;

            if(model == null) {

                return ResponseEntity.status(HttpStatus.CREATED).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {

                if(zoneCouvertureService.findOne(model.getZoneCouverture(), model.getTypeZoneCouverture()) == null ) {
                    
                    data = new ZoneCouverture();

                    data.setZoneCouverture(model.getZoneCouverture().toUpperCase());

                    data.setTypeZoneCouverture(model.getTypeZoneCouverture());

                    ZoneCouverture _ag_ = zoneCouvertureService.create(data);

                    if (_ag_.getId() == null) {

                        return ResponseEntity.status(HttpStatus.CREATED).body(

                                new RequestResponse(

                                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, null)
                                )
                        );
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
                                    new RequestInformation(400, "Cet enregistrement a déjà été faite. Doublons évité"),
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



    @PutMapping(value = "/update-zone-couverture", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ZoneCouverture.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteZoneCouverture(@RequestBody ZoneCouverture item) {
        try {
            if(item == null) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new RequestResponse(
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, null)
                        )
                );
            }
            else {
                ZoneCouverture up_data = zoneCouvertureService.findOne(item.getId());

                if(up_data!= null) {

                    up_data.setZoneCouverture(item.getZoneCouverture().toUpperCase());

                    up_data.setTypeZoneCouverture(item.getTypeZoneCouverture());

                    zoneCouvertureService.update(up_data);

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



    @PutMapping(value = "/delete-item-zone-couverture", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "ZoneCouverture model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ZoneCouverture.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteZoneCouverture(@RequestBody ZoneCouverture object) {

        try {
            if(object != null){

                ZoneCouverture item = zoneCouvertureService.findOne(object.getId());

                if(item!= null) {

                    zoneCouvertureService.disable(item.getId());

                    ZoneCouverture _up_ = item;

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
                            new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getZoneCouverture()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getZoneCouverture()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-zone-couverture", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "ZoneCouverture model", notes = "ZoneCouverture model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ZoneCouverture.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeZoneCouverture(@RequestBody ZoneCouverture object) {

        try {
            if(object != null){

                ZoneCouverture item = zoneCouvertureService.findOne(object.getId());

                if(item!= null) {

                    zoneCouvertureService.enable(item.getId());

                    ZoneCouverture up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation du type message "+object.getZoneCouverture()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type messgae "+object.getZoneCouverture()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-zone-couverture", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllZoneCouverture() {

        try {

            List<ZoneCouverture> items = zoneCouvertureService.findAll();

            for (ZoneCouverture item :items){

                ZoneCouverture models = zoneCouvertureService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    zoneCouvertureService.update(models);
                }
            }

            return getResponseEntity(zoneCouvertureService.findAll().isEmpty());
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    // TODO:: API TYPE DE COLIS
    @GetMapping(value = "/find-all-type-colis", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeColis() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeColisLivraisonService.findAlls());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-type-colis", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewTypeColis (@RequestBody NatureColis model) {

        try {
            NatureColis data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(typeColisLivraisonService.findOne(model.getTypecolis()) == null ) {

                    data = new NatureColis();

                    data.setTypecolis(model.getTypecolis().toUpperCase());

                    NatureColis _ag_ = typeColisLivraisonService.create(data);

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



    @PutMapping(value = "/update-type-colis", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = NatureColis.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteTypeColis(@RequestBody NatureColis item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                NatureColis up_data = typeColisLivraisonService.findOne(item.getId());

                if(up_data!= null) {

                    up_data.setTypecolis(item.getTypecolis().toUpperCase());

                    typeColisLivraisonService.update(up_data);

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



    @PutMapping(value = "/delete-item-type-colis", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = NatureColis.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteTypeColis(@RequestBody NatureColis object) {

        try {
            if(object != null){

                NatureColis item = typeColisLivraisonService.findOne(object.getId());

                if(item!= null) {

                    typeColisLivraisonService.disable(item.getId());

                    if (item.getId() == null) {
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
                            new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getTypecolis()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getTypecolis()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-type-colis", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypeColis model", notes = "TypeColis model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = NatureColis.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeTypeColis(@RequestBody NatureColis object) {

        try {
            if(object != null){

                NatureColis item = typeColisLivraisonService.findOne(object.getId());

                if(item!= null) {

                    typeColisLivraisonService.enable(item.getId());

                    NatureColis up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getTypecolis()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getTypecolis()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-type-colis", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllTypeColis() {

        try {

            List<NatureColis> items = typeColisLivraisonService.findAll();

            for (NatureColis item :items){

                NatureColis models = typeColisLivraisonService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    typeColisLivraisonService.update(models);
                }
            }

            return getResponseEntity(typeColisLivraisonService.findAll().isEmpty());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




    @PostMapping(value = "/create-new-type-engins", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewTypeEngins (@RequestBody TypeEngins model) {

        try {
            TypeEngins data = null;
            if(model == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {

                if(typeEnginsService.findOne(model.getTypeengins()) == null ) {

                    data = new TypeEngins();

                    data.setTypeengins(model.getTypeengins().toUpperCase());

                    TypeEngins _ag_ = typeEnginsService.create(data);

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



    @PutMapping(value = "/update-type-engins", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = TypeEngins.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteTypeEngins(@RequestBody TypeEngins item) {
        try {
            if(item == null) {
                throw new Exception("Impossible d'enregistrer les informations soumises");
            }
            else {
                TypeEngins up_data = typeEnginsService.findOne(item.getId());

                if(up_data!= null) {

                    up_data.setTypeengins(item.getTypeengins().toUpperCase());

                    typeEnginsService.update(up_data);

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



    @PutMapping(value = "/delete-item-type-engins", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeEngins.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteTypeEngins(@RequestBody TypeEngins object) {

        try {
            if(object != null){

                TypeEngins item = typeEnginsService.findOne(object.getId());

                if(item!= null) {

                    typeEnginsService.disable(item.getId());

                    if (item.getId() == null) {
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
                            new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getTypeengins()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type contrat "+object.getTypeengins()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/active-item-type-engins", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypeEngins model", notes = "TypeEngins model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypeEngins.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeTypeEngins(@RequestBody TypeEngins object) {

        try {
            if(object != null){

                TypeEngins item = typeEnginsService.findOne(object.getId());

                if(item!= null) {

                    typeEnginsService.enable(item.getId());

                    TypeEngins up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getTypeengins()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type contrat "+object.getTypeengins()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-type-engins", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllTypeEngins() {

        try {

            List<TypeEngins> items = typeEnginsService.findAll();

            for (TypeEngins item :items){

                TypeEngins models = typeEnginsService.findOne(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    typeEnginsService.update(models);
                }
            }

            return getResponseEntity(typeEnginsService.findAll().isEmpty());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    // TODO:: API TYPE DE COLIS
    @GetMapping(value = "/find-all-type-engins", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllTypeEngins() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typeEnginsService.findAlls());
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
