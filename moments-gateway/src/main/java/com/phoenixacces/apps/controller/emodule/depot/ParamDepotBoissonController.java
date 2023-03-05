package com.phoenixacces.apps.controller.emodule.depot;

import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.assurance.ModelMessage;
import com.phoenixacces.apps.persistence.entities.module.assurance.PorteFeuilleClient;
import com.phoenixacces.apps.persistence.entities.module.depot.*;
import com.phoenixacces.apps.persistence.entities.parametrage.NotificationSysteme;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.module.depot.*;
import com.phoenixacces.apps.persistence.services.parametrage.NotificationSystemeService;
import com.phoenixacces.apps.persistence.services.parametrage.SmsCredentialService;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Depot de Boisson Module Controller")
@Slf4j
public class ParamDepotBoissonController {

    @Value("${api.sms.gateway}")
    private String gateway;

    SimpleDateFormat datStr = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat heure = new SimpleDateFormat("HH:mm:ss");

    private final BanqueService banqueService;
    private final CompteBancaireService compteBancaireService;
    private final ModeVenteService modeVenteService;
    private final NatureDepotService natureDepotService;
    private final TypesPaiementService typePaiementService;
    private final CategorieBoissonService categorieBoissonService;
    private final MotifAvanceService motifAvanceService;
    private final DiplomeService diplomeService;
    private final PosteOccupeService posteOccupeService;
    private final FournisseurService fournisseurService;
    private final ProduitService produitService;
    private final ProfileService profileService;
    private final ReglementFactureService reglementFactureService;
    private final SmsCredentialService smsCredentialService;
    private final NotificationSystemeService notificationService;
    private final JmsProducer jmsProducer;

    @Autowired
    public ParamDepotBoissonController(
            BanqueService banqueService,
            CompteBancaireService compteBancaireService,
            ModeVenteService modeVenteService,
            NatureDepotService natureDepotService,
            TypesPaiementService typePaiementService,
            CategorieBoissonService categorieBoissonService,
            MotifAvanceService motifAvanceService,
            DiplomeService diplomeService,
            PosteOccupeService posteOccupeService,
            FournisseurService fournisseurService,
            ProduitService produitService,
            ProfileService profileService,
            ReglementFactureService reglementFactureService,
            SmsCredentialService smsCredentialService,
            JmsProducer jmsProducer,
            NotificationSystemeService notificationService) {
        this.banqueService                      = banqueService;
        this.compteBancaireService              = compteBancaireService;
        this.modeVenteService                   = modeVenteService;
        this.natureDepotService                 = natureDepotService;
        this.typePaiementService                = typePaiementService;
        this.categorieBoissonService            = categorieBoissonService;
        this.motifAvanceService                 = motifAvanceService;
        this.diplomeService                     = diplomeService;
        this.posteOccupeService                 = posteOccupeService;
        this.fournisseurService                 = fournisseurService;
        this.produitService                     = produitService;
        this.profileService                     = profileService;
        this.reglementFactureService            = reglementFactureService;
        this.smsCredentialService               = smsCredentialService;
        this.jmsProducer                        = jmsProducer;
        this.notificationService                = notificationService;
    }


    @GetMapping(value = "/find-all-banque", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllBanque() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(banqueService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-banque", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listBanque() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(banqueService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-banque", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewBanque (@RequestBody Banque model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            Banque data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(banqueService.findOne(model.getNomBanque().toUpperCase(), true) == null ) {

                    data = new Banque();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setNomBanque(model.getNomBanque().toUpperCase());

                    if (banqueService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-banque", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteBanque(@RequestBody Banque item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                Banque banque = banqueService.findById(item.getId());

                if(banque!= null) {

                    banque.setNomBanque(item.getNomBanque().toUpperCase());

                    banqueService.update(banque);

                    if (banque.getId() == null) {

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


    @PutMapping(value = "/delete-item-banque", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Banque.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteBanque(@RequestBody Banque object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                Banque item = banqueService.findById(object.getId());

                if(item!= null) {

                    banqueService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getNomBanque().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNomBanque().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNomBanque().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-banque", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Banque model", notes = "Banque model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Banque.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeBanque(@RequestBody Banque object) {

        try {
            if(object != null){

                Banque item = banqueService.findById(object.getId());

                if(item!= null) {

                    banqueService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNomBanque().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNomBanque().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-banque", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllBanque() {

        try {

            List<Banque> items = banqueService.findAll();

            for (Banque item :items){

                Banque models = banqueService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    banqueService.update(models);
                }
            }
            System.gc();

            if(banqueService.findAll().isEmpty()) {

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



    @GetMapping(value = "/find-all-compte-bancaire", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllCompteBancaire() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(compteBancaireService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-compte-bancaire", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewCompteBancaire (@RequestBody CompteBancaire model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            CompteBancaire data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(compteBancaireService.findOne(model.getBanque(), model.getEntreprises(), true) == null ) {

                    data = new CompteBancaire();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setBanque(model.getBanque());

                    data.setNumeroDeCompte(model.getNumeroDeCompte());

                    data.setNomGestionnaire(model.getNomGestionnaire() == null ? "NA" : model.getNomGestionnaire().toUpperCase());

                    data.setContactGestionnaire(model.getContactGestionnaire() == null ? "NA" : model.getContactGestionnaire());

                    data.setEntreprises(model.getEntreprises());

                    if (compteBancaireService.create(data).getId() == null) {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(

                                new RequestResponse(

                                        new RequestInformation(400, "Quelques choses ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),

                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.CREATED).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-compte-bancaire", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteCompteBancaire(@RequestBody CompteBancaire item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                CompteBancaire up_data = compteBancaireService.findById(item.getId());

                if(up_data!= null) {

                    up_data.setBanque(item.getBanque());

                    up_data.setNumeroDeCompte(item.getNumeroDeCompte());

                    up_data.setNomGestionnaire(item.getNomGestionnaire() == null ? "NA" : item.getNomGestionnaire().toUpperCase());

                    up_data.setContactGestionnaire(item.getContactGestionnaire() == null ? "NA" : item.getContactGestionnaire());

                    compteBancaireService.update(up_data);

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


    @PutMapping(value = "/delete-item-compte-bancaire", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = CompteBancaire.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteCompteBancaire(@RequestBody CompteBancaire object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                CompteBancaire item = compteBancaireService.findById(object.getId());

                if(item!= null) {

                    compteBancaireService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getBanque().getNomBanque().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getBanque().getNomBanque().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getBanque().getNomBanque().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-compte-bancaire", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "CompteBancaire model", notes = "CompteBancaire model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = CompteBancaire.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeCompteBancaire(@RequestBody CompteBancaire object) {

        try {
            if(object != null){

                CompteBancaire item = compteBancaireService.findById(object.getId());

                if(item!= null) {

                    compteBancaireService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getBanque().getNomBanque().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getBanque().getNomBanque().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-compte-bancaire", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllCompteBancaire() {

        try {

            List<CompteBancaire> items = compteBancaireService.findAll();

            for (CompteBancaire item :items){

                CompteBancaire models = compteBancaireService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    compteBancaireService.update(models);
                }
            }

            System.gc();

            if(compteBancaireService.findAll().isEmpty()) {

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


    @GetMapping(value = "/find-all-mode-de-vente", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllModeVente() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(modeVenteService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-mode-de-vente", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listModeVente() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(modeVenteService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-mode-de-vente", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewModeVente (@RequestBody ModeVente model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            ModeVente data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(modeVenteService.findOne(model.getModeVente().toUpperCase(), true) == null ) {

                    data = new ModeVente();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setModeVente(model.getModeVente().toUpperCase());

                    if (modeVenteService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-mode-de-vente", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteModeVente(@RequestBody ModeVente item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                ModeVente ModeVente = modeVenteService.findById(item.getId());

                if(ModeVente!= null) {

                    ModeVente.setModeVente(item.getModeVente().toUpperCase());

                    modeVenteService.update(ModeVente);

                    if (ModeVente.getId() == null) {

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


    @PutMapping(value = "/delete-item-mode-de-vente", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ModeVente.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteModeVente(@RequestBody ModeVente object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                ModeVente item = modeVenteService.findById(object.getId());

                if(item!= null) {

                    modeVenteService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getModeVente().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getModeVente().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getModeVente().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-mode-de-vente", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "ModeVente model", notes = "ModeVente model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ModeVente.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeModeVente(@RequestBody ModeVente object) {

        try {
            if(object != null){

                ModeVente item = modeVenteService.findById(object.getId());

                if(item!= null) {

                    modeVenteService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getModeVente().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getModeVente().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-mode-de-vente", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllModeVente() {

        try {

            List<ModeVente> items = modeVenteService.findAll();

            for (ModeVente item :items){

                ModeVente models = modeVenteService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    modeVenteService.update(models);
                }
            }
            System.gc();

            if(modeVenteService.findAll().isEmpty()) {

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


    @GetMapping(value = "/find-all-nature-depot", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllNatureDepot() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(natureDepotService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-nature-depot", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listNatureDepot() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(natureDepotService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-nature-depot", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewNatureDepot (@RequestBody NatureDepot model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            NatureDepot data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(natureDepotService.findOne(model.getNatMouvt().toUpperCase(), true) == null ) {

                    data = new NatureDepot();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setNatMouvt(model.getNatMouvt().toUpperCase());

                    if (natureDepotService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-nature-depot", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteNatureDepot(@RequestBody NatureDepot item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                NatureDepot NatureDepot = natureDepotService.findById(item.getId());

                if(NatureDepot!= null) {

                    NatureDepot.setNatMouvt(item.getNatMouvt().toUpperCase());

                    natureDepotService.update(NatureDepot);

                    if (NatureDepot.getId() == null) {

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


    @PutMapping(value = "/delete-item-nature-depot", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = NatureDepot.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteNatureDepot(@RequestBody NatureDepot object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                NatureDepot item = natureDepotService.findById(object.getId());

                if(item!= null) {

                    natureDepotService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getNatMouvt().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNatMouvt().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNatMouvt().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-nature-depot", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "NatureDepot model", notes = "NatureDepot model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = NatureDepot.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeNatureDepot(@RequestBody NatureDepot object) {

        try {
            if(object != null){

                NatureDepot item = natureDepotService.findById(object.getId());

                if(item!= null) {

                    natureDepotService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNatMouvt().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNatMouvt().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-nature-depot", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllNatureDepot() {

        try {

            List<NatureDepot> items = natureDepotService.findAll();

            for (NatureDepot item :items){

                NatureDepot models = natureDepotService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    natureDepotService.update(models);
                }
            }
            System.gc();

            if(natureDepotService.findAll().isEmpty()) {

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


    @GetMapping(value = "/find-all-type-paiement-depot", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> allTypesPaiement() {

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


    @GetMapping(value = "/liste-type-paiement-depot", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listTypesPaiement() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(typePaiementService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-type-paiement-depot", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewTypesPaiement (@RequestBody TypesPaiement model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            TypesPaiement data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(typePaiementService.findOne(model.getTypePaiement().toUpperCase(), true) == null ) {

                    data = new TypesPaiement();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setTypePaiement(model.getTypePaiement().toUpperCase());

                    if (typePaiementService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-type-paiement-depot", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteTypesPaiement(@RequestBody TypesPaiement item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                TypesPaiement typePaiement = typePaiementService.findById(item.getId());

                if(typePaiement!= null) {

                    typePaiement.setTypePaiement(item.getTypePaiement().toUpperCase());

                    typePaiementService.update(typePaiement);

                    if (typePaiement.getId() == null) {

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


    @PutMapping(value = "/delete-item-type-paiement-depot", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypesPaiement.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteTypesPaiement(@RequestBody TypesPaiement object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                TypesPaiement item = typePaiementService.findById(object.getId());

                if(item!= null) {

                    typePaiementService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getTypePaiement().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getTypePaiement().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getTypePaiement().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-type-paiement-depot", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypePaiement model", notes = "TypePaiement model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = TypesPaiement.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeTypesPaiement(@RequestBody TypesPaiement object) {

        try {
            if(object != null){

                TypesPaiement item = typePaiementService.findById(object.getId());

                if(item!= null) {

                    typePaiementService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getTypePaiement().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getTypePaiement().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-type-paiement-depot", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllTypesPaiement() {

        try {

            List<TypesPaiement> items = typePaiementService.findAll();

            for (TypesPaiement item :items){

                TypesPaiement models = typePaiementService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    typePaiementService.update(models);
                }
            }
            System.gc();

            if(typePaiementService.findAll().isEmpty()) {

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


    @GetMapping(value = "/find-all-categorie-boisson", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> allCategorieBoisson() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(categorieBoissonService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-categorie-boisson", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listCategorieBoisson() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(categorieBoissonService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-categorie-boisson", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewCategorieBoisson (@RequestBody CategorieBoisson model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            CategorieBoisson data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(categorieBoissonService.findOne(model.getCategorieBoisson().toUpperCase(), true) == null ) {

                    data = new CategorieBoisson();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setCategorieBoisson(model.getCategorieBoisson().toUpperCase());

                    if (categorieBoissonService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-categorie-boisson", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteCategorieBoisson(@RequestBody CategorieBoisson item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                CategorieBoisson typePaiement = categorieBoissonService.findById(item.getId());

                if(typePaiement!= null) {

                    typePaiement.setCategorieBoisson(item.getCategorieBoisson().toUpperCase());

                    categorieBoissonService.update(typePaiement);

                    if (typePaiement.getId() == null) {

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


    @PutMapping(value = "/delete-item-categorie-boisson", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = CategorieBoisson.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteCategorieBoisson(@RequestBody CategorieBoisson object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                CategorieBoisson item = categorieBoissonService.findById(object.getId());

                if(item!= null) {

                    categorieBoissonService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getCategorieBoisson().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getCategorieBoisson().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getCategorieBoisson().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-categorie-boisson", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypePaiement model", notes = "TypePaiement model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = CategorieBoisson.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeCategorieBoisson(@RequestBody CategorieBoisson object) {

        try {
            if(object != null){

                CategorieBoisson item = categorieBoissonService.findById(object.getId());

                if(item!= null) {

                    categorieBoissonService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getCategorieBoisson().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getCategorieBoisson().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-categorie-boisson", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllCategorieBoisson() {

        try {

            List<CategorieBoisson> items = categorieBoissonService.findAll();

            for (CategorieBoisson item :items){

                CategorieBoisson models = categorieBoissonService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    categorieBoissonService.update(models);
                }
            }
            System.gc();

            if(categorieBoissonService.findAll().isEmpty()) {

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


    @GetMapping(value = "/find-all-motif-avance-sur-salaire", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> allMotifAvance() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(motifAvanceService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-motif-avance-sur-salaire", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listMotifAvance() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(motifAvanceService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-motif-avance-sur-salaire", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewMotifAvance (@RequestBody MotifAvance model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            MotifAvance data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(motifAvanceService.findOne(model.getMotif().toUpperCase(), true) == null ) {

                    data = new MotifAvance();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setMotif(model.getMotif().toUpperCase());

                    if (motifAvanceService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-motif-avance-sur-salaire", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteMotifAvance(@RequestBody MotifAvance item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                MotifAvance typePaiement = motifAvanceService.findById(item.getId());

                if(typePaiement!= null) {

                    typePaiement.setMotif(item.getMotif().toUpperCase());

                    motifAvanceService.update(typePaiement);

                    if (typePaiement.getId() == null) {

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


    @PutMapping(value = "/delete-item-motif-avance-sur-salaire", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = MotifAvance.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteMotifAvance(@RequestBody MotifAvance object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                MotifAvance item = motifAvanceService.findById(object.getId());

                if(item!= null) {

                    motifAvanceService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getMotif().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getMotif().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getMotif().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-motif-avance-sur-salaire", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypePaiement model", notes = "TypePaiement model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = MotifAvance.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeMotifAvance(@RequestBody MotifAvance object) {

        try {
            if(object != null){

                MotifAvance item = motifAvanceService.findById(object.getId());

                if(item!= null) {

                    motifAvanceService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getMotif().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getMotif().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-motif-avance-sur-salaire", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllMotifAvance() {

        try {

            List<MotifAvance> items = motifAvanceService.findAll();

            for (MotifAvance item :items){

                MotifAvance models = motifAvanceService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    motifAvanceService.update(models);
                }
            }
            System.gc();

            if(motifAvanceService.findAll().isEmpty()) {

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


    @GetMapping(value = "/find-all-diplome", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> allDiplome() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(diplomeService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-diplome", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listDiplome() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(diplomeService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-diplome", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewDiplome (@RequestBody Diplome model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            Diplome data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(diplomeService.findOne(model.getDiplome().toUpperCase(), true) == null ) {

                    data = new Diplome();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setDiplome(model.getDiplome().toUpperCase());

                    if (diplomeService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-diplome", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteDiplome(@RequestBody Diplome item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                Diplome typePaiement = diplomeService.findById(item.getId());

                if(typePaiement!= null) {

                    typePaiement.setDiplome(item.getDiplome().toUpperCase());

                    diplomeService.update(typePaiement);

                    if (typePaiement.getId() == null) {

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


    @PutMapping(value = "/delete-item-diplome", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Diplome.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteDiplome(@RequestBody Diplome object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                Diplome item = diplomeService.findById(object.getId());

                if(item!= null) {

                    diplomeService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getDiplome().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getDiplome().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getDiplome().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-diplome", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypePaiement model", notes = "TypePaiement model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Diplome.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeDiplome(@RequestBody Diplome object) {

        try {
            if(object != null){

                Diplome item = diplomeService.findById(object.getId());

                if(item!= null) {

                    diplomeService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getDiplome().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getDiplome().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-diplome", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllDiplome() {

        try {

            List<Diplome> items = diplomeService.findAll();

            for (Diplome item :items){

                Diplome models = diplomeService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    diplomeService.update(models);
                }
            }
            System.gc();

            if(diplomeService.findAll().isEmpty()) {

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


    @GetMapping(value = "/find-all-poste-occupe", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> allPosteOccupe() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(posteOccupeService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-poste-occupe", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listPosteOccupe() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(posteOccupeService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-poste-occupe", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewPosteOccupe (@RequestBody PosteOccupe model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            PosteOccupe data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(posteOccupeService.findOne(model.getPoste().toUpperCase(), true) == null ) {

                    data = new PosteOccupe();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setPoste(model.getPoste().toUpperCase());

                    if (posteOccupeService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-poste-occupe", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadtePosteOccupe(@RequestBody PosteOccupe item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                PosteOccupe typePaiement = posteOccupeService.findById(item.getId());

                if(typePaiement!= null) {

                    typePaiement.setPoste(item.getPoste().toUpperCase());

                    posteOccupeService.update(typePaiement);

                    if (typePaiement.getId() == null) {

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


    @PutMapping(value = "/delete-item-poste-occupe", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = PosteOccupe.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deletePosteOccupe(@RequestBody PosteOccupe object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                PosteOccupe item = posteOccupeService.findById(object.getId());

                if(item!= null) {

                    posteOccupeService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getPoste().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getPoste().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getPoste().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-poste-occupe", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypePaiement model", notes = "TypePaiement model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = PosteOccupe.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activePosteOccupe(@RequestBody PosteOccupe object) {

        try {
            if(object != null){

                PosteOccupe item = posteOccupeService.findById(object.getId());

                if(item!= null) {

                    posteOccupeService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getPoste().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getPoste().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-poste-occupe", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllPosteOccupe() {

        try {

            List<PosteOccupe> items = posteOccupeService.findAll();

            for (PosteOccupe item :items){

                PosteOccupe models = posteOccupeService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    posteOccupeService.update(models);
                }
            }
            System.gc();

            if(posteOccupeService.findAll().isEmpty()) {

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


    @GetMapping(value = "/find-all-forunisseur", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> allFournisseur() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(fournisseurService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-forunisseur", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listFournisseur() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(fournisseurService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-fournisseur-by-profile/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listFournisseurByEntreprise(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(!idDigital.isEmpty()){

                Profile profile = profileService.findOne(idDigital);

                if(profile != null){

                    response = ResponseEntity.status(HttpStatus.OK).body(fournisseurService.findAll(true, profile.getGareRoutiere().getCompagnie()));
                }
                else{

                    response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
                }
            }
            else{

                response = ResponseEntity.status(HttpStatus.OK).body("Not Authorize");
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


    @PostMapping(value = "/create-new-forunisseur", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewFournisseur (@RequestBody Fournisseur model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            Fournisseur data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                Profile profile = profileService.findOne(model.getProfile().getIdDigital());

                if(profile != null){

                    if(fournisseurService.findOne(model.getNomComplet().toUpperCase(), true) == null ) {

                        data = new Fournisseur();

                        data.setOrdre(Utils.generateRandom(8));

                        data.setNomComplet(model.getNomComplet().toUpperCase());

                        data.setContact(model.getContact());

                        data.setAdresse(model.getAdresse() == null ? "NA" : model.getAdresse().toUpperCase());

                        data.setProfile(model.getProfile());

                        data.setEntreprises(model.getProfile().getGareRoutiere().getCompagnie());

                        if (fournisseurService.create(data).getId() == null) {

                            throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                        }
                        else {

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(201, "Successfully created data"),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestResponse(

                                        new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Profil non disponible"),

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


    @PutMapping(value = "/update-forunisseur", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteFournisseur(@RequestBody Fournisseur item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                Fournisseur items = fournisseurService.findById(item.getId());

                if(items!= null) {

                    items.setNomComplet(item.getNomComplet().toUpperCase());

                    items.setContact(item.getContact());

                    items.setAdresse(item.getAdresse() == null ? "NA" : item.getAdresse().toUpperCase());

                    fournisseurService.update(items);

                    if (items.getId() == null) {

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


    @PutMapping(value = "/delete-item-forunisseur", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Fournisseur.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteFournisseur(@RequestBody Fournisseur object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                Fournisseur item = fournisseurService.findById(object.getId());

                if(item!= null) {

                    fournisseurService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getNomComplet().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNomComplet().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNomComplet().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-forunisseur", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypePaiement model", notes = "TypePaiement model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Fournisseur.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeFournisseur(@RequestBody Fournisseur object) {

        try {
            if(object != null){

                Fournisseur item = fournisseurService.findById(object.getId());

                if(item!= null) {

                    fournisseurService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNomComplet().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNomComplet().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-forunisseur", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllFournisseur() {

        try {

            List<Fournisseur> items = fournisseurService.findAll();

            for (Fournisseur item :items){

                Fournisseur models = fournisseurService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    fournisseurService.update(models);
                }
            }
            System.gc();

            if(fournisseurService.findAll().isEmpty()) {

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



    @GetMapping(value = "/find-all-produit", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllProduit() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(produitService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-produit", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listProduit() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(produitService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-produit", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewProduit (@RequestBody Produit model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            Produit data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                if(produitService.findOne(model.getNomBoisson().toUpperCase(), true) == null ) {

                    data = new Produit();

                    data.setOrdre(Utils.generateRandom(8));

                    data.setCategorie(model.getCategorie());

                    data.setNomBoisson(model.getNomBoisson().toUpperCase());

                    data.setPeremption(model.getPeremption());

                    data.setPrixAchat(model.getPrixAchat());

                    data.setPrixVteDemiGros(model.getPrixVteDemiGros());

                    data.setPrixVteEnGros(model.getPrixVteEnGros());

                    data.setMontantVendu(model.getMontantVendu());

                    data.setQteEnStock(model.getQteEnStock());

                    data.setSeuilAlerte(model.getSeuilAlerte());

                    data.setMontantVenduEspere(model.getMontantVenduEspere());

                    data.setProfile(model.getProfile());

                    data.setPrixVente(model.getPrixVente());

                    data.setEntreprises(model.getProfile().getGareRoutiere().getCompagnie());

                    if (produitService.create(data).getId() == null) {

                        throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                    }
                    else {

                        response = ResponseEntity.status(HttpStatus.CREATED).body(
                                new RequestResponse(
                                        new RequestInformation(201, "Successfully created data"),
                                        new RequestMessage(null, null)
                                )
                        );
                    }
                }
                else {

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestResponse(

                                    new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

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


    @PutMapping(value = "/update-produit", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteProduit(@RequestBody Produit item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                Produit produit = produitService.findById(item.getId());

                if(produit!= null) {

                    produit.setCategorie(item.getCategorie());

                    produit.setNomBoisson(item.getNomBoisson().toUpperCase());

                    produit.setPeremption(item.getPeremption());

                    produit.setPrixAchat(item.getPrixAchat());

                    produit.setPrixVteDemiGros(item.getPrixVteDemiGros());

                    produit.setPrixVteEnGros(item.getPrixVteEnGros());

                    produit.setMontantVenduEspere(item.getMontantVenduEspere());

                    produit.setQteEnStock(item.getQteEnStock());

                    produit.setSeuilAlerte(item.getSeuilAlerte());

                    produit.setPrixVente(item.getPrixVente());


                    produitService.update(produit);

                    if (produit.getId() == null) {

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


    @PutMapping(value = "/delete-item-produit", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Produit.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteProduit(@RequestBody Produit object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                Produit item = produitService.findById(object.getId());

                if(item!= null) {

                    produitService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getNomBoisson().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNomBoisson().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNomBoisson().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-produit", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Produit model", notes = "Produit model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = Produit.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeProduit(@RequestBody Produit object) {

        try {
            if(object != null){

                Produit item = produitService.findById(object.getId());

                if(item!= null) {

                    produitService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNomBoisson().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNomBoisson().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/deleted-all-produit", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllProduit() {

        try {

            List<Produit> items = produitService.findAll();

            for (Produit item :items){

                Produit models = produitService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    produitService.update(models);
                }
            }
            System.gc();

            if(produitService.findAll().isEmpty()) {

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



    @GetMapping(value = "/find-all-reception-de-la-facture", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> allReceptionFacture() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(reglementFactureService.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @GetMapping(value = "/liste-reception-de-la-facture", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> listReceptionFacture() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(reglementFactureService.findAll(true));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }


    @PostMapping(value = "/create-new-reception-de-la-facture", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewReceptionFacture (@RequestBody ReceptionFacture model) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            String numberAdmin;

            String numFournisseur;

            ReceptionFacture data = null;

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                        new RequestResponse(

                                new RequestInformation(400, "Impossible d'enregistrer les informations soumises"),

                                new RequestMessage(null, "Fail")
                        )
                );
            }
            else {

                SmsCredential credential = smsCredentialService.findOne(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getId());

                if(credential != null){

                    Long quotaSms         = null;

                    if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                        numberAdmin             = "225"+model.getProfile().getPhone();

                        numFournisseur          = "225"+model.getFournisseur().getContact();

                        quotaSms                = credential.getNombreSmsLeTexto();

                    }
                    else{

                        numberAdmin             = model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+model.getProfile().getPhone();

                        numFournisseur          = model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+model.getFournisseur().getContact();

                        quotaSms                = credential.getNombreSmsLeTexto();
                    }

                    if(quotaSms > 10){

                        if(reglementFactureService.findOne(model.getStatutFacture().toUpperCase(), true, model.getFournisseur()) == null ) {

                            data = new ReceptionFacture();

                            data.setOrdre(Utils.generateRandom(8));

                            data.setNumeroFacture(model.getNumeroFacture().toUpperCase());

                            data.setNotifFournisseur(model.getNotifFournisseur().toUpperCase());

                            data.setTypeNotif(model.getTypeNotif());

                            data.setDateReception(model.getDateReception());

                            data.setFournisseur(model.getFournisseur());

                            data.setStatutFacture(model.getStatutFacture());

                            data.setAccompteFacture(model.getAccompteFacture());

                            data.setMontantRestant(model.getMontantTotalFacture() -model.getAccompteFacture());

                            data.setMontantTotalFacture(model.getMontantTotalFacture());

                            data.setProfile(model.getProfile());

                            data.setEntreprises(model.getEntreprises());

                            if (reglementFactureService.create(data).getId() == null) {

                                throw new Exception("Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies");
                            }
                            else {

                                //NOTIFICATION DE L'ADMINISTRATEUR DE L'ETAT D'ENREGISTREMENT DE LA FACTURE
                                sendNotifActor(numberAdmin, data, credential, 0);


                                //Enregistrement de la notification
                                saveNotification("L'enregistrement de la facture du fournisseur ", data.getFournisseur().getNomComplet(), " à la date du ", " a été enregistré avec succès.", data.getProfile(), TypeNotification.Connexion);


                                //NOTIFICATION FOURNISSEUR
                                sendNotifActor(numFournisseur, data, credential, 1);


                                response = ResponseEntity.status(HttpStatus.CREATED).body(
                                        new RequestResponse(
                                                new RequestInformation(201, "Successfully created data"),
                                                new RequestMessage(null, null)
                                        )
                                );
                            }
                        }
                        else {

                            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                    new RequestResponse(

                                            new RequestInformation(400, "Cet enregistrement a été déjà parametré"),

                                            new RequestMessage(null, "Fail")
                                    )
                            );
                        }
                    }
                    else{

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                                new RequestInformation(605, "Votre quota minimum d'sms pour la programmation est de 10 sms. Nous prions d'effectuer au rechargement de votre compte.")

                        );
                    }
                }
                else{

                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                            new RequestInformation(400, "Votre quota d'sms ne vous permet pas d'avoir accès à la plate-forme.")

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

    private void sendNotifActor(String numero, ReceptionFacture data, SmsCredential credential, int position) {

        String message;

        if (position == 0){

            message = "L'enregistrement de la facture du fournisseur "+ data.getFournisseur().getNomComplet().toUpperCase()+" a été enregistré avec succès.";
        }
        else{

            message = "Cher fournisseur "+ data.getFournisseur().getNomComplet().toUpperCase()+" votre facture a été traitée. Prière contacter votre interlocuteur pour en savoir plus.";
        }


        SmsMessage sms = new SmsMessage();

        sms.setTypeMessage(1L);

        sms.setToId(numero);

        sms.setContent(message);

        sms.setFromName(data.getFournisseur().getNomComplet());

        sms.setUsername(data.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

        sms.setPassword(data.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

        sms.setSenderId(data.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

        jmsProducer.send(new JmsMessage("Envoi du message de bienvenu", Converter.pojoToJson(sms), SmsMessage.class));


        long rtSms = (long) Math.ceil(sms.getContent().length()/160);

        long dtSms = rtSms == 0 ? 1 : rtSms;



        //MISE A JOUR DU QUOTA SMS
        if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

            credential.setNombreSmsLeTexto(credential.getNombreSmsLeTexto() - dtSms);
        }
        else{

            credential.setNombreSms(credential.getNombreSms() - dtSms);
        }

        smsCredentialService.updated(credential);
    }


    @PutMapping(value = "/update-reception-de-la-facture", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = ModelMessage.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteReceptionFacture(@RequestBody ReceptionFacture item) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(item == null) {

                response = ResponseEntity.status(HttpStatus.OK).body(
                        new RequestResponse(
                                new RequestInformation(201, "Impossible d'enregistrer les informations soumises"),
                                new RequestMessage(null, "Success")
                        )
                );
            }
            else {

                ReceptionFacture donnees = reglementFactureService.findById(item.getId());

                if(donnees!= null) {

                    donnees.setNumeroFacture(item.getNumeroFacture().toUpperCase());

                    donnees.setNotifFournisseur(item.getNotifFournisseur().toUpperCase());

                    donnees.setTypeNotif(item.getTypeNotif());

                    donnees.setDateReception(item.getDateReception());

                    donnees.setFournisseur(item.getFournisseur());

                    donnees.setStatutFacture(item.getStatutFacture());

                    donnees.setAccompteFacture(item.getAccompteFacture());

                    donnees.setMontantRestant(item.getMontantTotalFacture() -item.getAccompteFacture());

                    donnees.setMontantTotalFacture(item.getMontantTotalFacture());

                    donnees.setProfile(item.getProfile());

                    donnees.setEntreprises(item.getEntreprises());

                    if (reglementFactureService.update(donnees).getId() == null) {

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


    @PutMapping(value = "/delete-item-reception-de-la-facture", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Representative agent model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ReceptionFacture.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteReceptionFacture(@RequestBody ReceptionFacture object) {

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            if(object != null){

                ReceptionFacture item = reglementFactureService.findById(object.getId());

                if(item!= null) {

                    reglementFactureService.actionRequest(item.getId(), false);

                    if (item.getId() == null) {

                        response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                                new RequestInformation(400, "Impossible de désactiver la donnée "+object.getNumeroFacture().toUpperCase()),
                                new RequestMessage(null, "Success")
                        ));
                    }
                    else {
                        response =  ResponseEntity.status(HttpStatus.OK).body(new RequestResponse(
                                new RequestInformation(201, ""),
                                new RequestMessage(null, "Success")
                        ));
                    }
                }
                else {
                    response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                            new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNumeroFacture().toUpperCase()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                response =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression de l'enregistrement "+object.getNumeroFacture().toUpperCase()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/active-item-reception-de-la-facture", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "TypePaiement model", notes = "TypePaiement model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = ReceptionFacture.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeReceptionFacture(@RequestBody ReceptionFacture object) {

        try {
            if(object != null){

                ReceptionFacture item = reglementFactureService.findById(object.getId());

                if(item!= null) {

                    reglementFactureService.actionRequest(item.getId(), true);

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
                            new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNumeroFacture()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation de l'enregistrement "+object.getNumeroFacture()),
                        new RequestMessage(null, "Success")
                ));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping(value = "/deleted-all-reception-de-la-facture", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllReceptionFacture() {

        try {

            List<ReceptionFacture> items = reglementFactureService.findAll();

            for (ReceptionFacture item :items){

                ReceptionFacture models = reglementFactureService.findById(item.getId());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    reglementFactureService.update(models);
                }
            }
            System.gc();

            if(reglementFactureService.findAll().isEmpty()) {

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


    private void saveNotification(String x, String entity, String x1, String x2, Profile profile, TypeNotification add_Client) throws Exception {

        NotificationSysteme notification = new NotificationSysteme();

        notification.setNotification(x + entity + x1 +datStr.format(new Date())+" à "+heure.format(new Date())+ x2);

        notification.setProfile(profile);

        notification.setReference(String.valueOf(Utils.generateRandom(6)));

        notification.setType(add_Client);

        notificationService.create(notification);
    }
}
