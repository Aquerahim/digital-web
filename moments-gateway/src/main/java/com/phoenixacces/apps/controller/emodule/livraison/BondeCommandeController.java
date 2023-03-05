package com.phoenixacces.apps.controller.emodule.livraison;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenixacces.apps.enumerations.Direction;
import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.jms.messages.EmailMessage;
import com.phoenixacces.apps.jms.messages.JmsMessage;
import com.phoenixacces.apps.jms.messages.SmsMessage;
import com.phoenixacces.apps.jms.utilities.Converter;
import com.phoenixacces.apps.models.courrier.RechercheCourrierModel;
import com.phoenixacces.apps.models.response.RequestInformation;
import com.phoenixacces.apps.models.response.RequestMessage;
import com.phoenixacces.apps.models.response.RequestResponse;
import com.phoenixacces.apps.persistence.entities.audits.AuditFlux;
import com.phoenixacces.apps.persistence.entities.module.livraison.*;
import com.phoenixacces.apps.persistence.entities.parametrage.SmsCredential;
import com.phoenixacces.apps.persistence.entities.service.SmsEnvoye;
import com.phoenixacces.apps.persistence.models.MdleFicheComande;
import com.phoenixacces.apps.persistence.services.audits.AuditFluxService;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.module.livraison.BonDeCommandeService;
import com.phoenixacces.apps.persistence.services.module.livraison.ClientsService;
import com.phoenixacces.apps.persistence.services.module.livraison.FicheBonDeCommandeService;
import com.phoenixacces.apps.persistence.services.module.livraison.MesClientsService;
import com.phoenixacces.apps.persistence.services.parametrage.EntrepriseService;
import com.phoenixacces.apps.persistence.services.parametrage.SuiviDemandeService;
import com.phoenixacces.apps.persistence.services.parametrage.TypeMessageService;
import com.phoenixacces.apps.persistence.services.parametrage.mLivraison.HistoriqueService;
import com.phoenixacces.apps.producer.JmsProducer;
import com.phoenixacces.apps.utiles.Utils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Module Livraisons - Bon de Commande")
@Slf4j
public class BondeCommandeController {

    private final ClientsService clientsService;
    private final JmsProducer jmsProducer;
    private final ProfileService profileService;
    private final AuditFluxService auditFluxService;
    private final MesClientsService mesClientsService;
    private final FicheBonDeCommandeService ficheBonDeCmdeSve;
    private final HistoriqueService historiqueService;
    private final BonDeCommandeService bonDeCommandeService;
    private final EntrepriseService entrepriseService;
    private final SuiviDemandeService suiviDemandeService;
    private final TypeMessageService typeMessageService;

    @Autowired
    public BondeCommandeController (
            JmsProducer jmsProducer,
            ProfileService profileService,
            AuditFluxService auditFluxService,
            MesClientsService mesClientsService,
            FicheBonDeCommandeService ficheBonDeCmdeSve,
            HistoriqueService historiqueService,
            BonDeCommandeService bonDeCommandeService,
            EntrepriseService entrepriseService,
            SuiviDemandeService suiviDemandeService,
            TypeMessageService typeMessageService,
            ClientsService clientsService
    ){
        this.jmsProducer            = jmsProducer;
        this.profileService         = profileService;
        this.auditFluxService       = auditFluxService;
        this.mesClientsService      = mesClientsService;
        this.ficheBonDeCmdeSve      = ficheBonDeCmdeSve;
        this.historiqueService      = historiqueService;
        this.bonDeCommandeService   = bonDeCommandeService;
        this.entrepriseService      = entrepriseService;
        this.suiviDemandeService    = suiviDemandeService;
        this.typeMessageService     = typeMessageService;
        this.clientsService         = clientsService;
    }


    @GetMapping(value = "/find-all-bon-de-commande", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllFicheBonDeCommande() {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(ficheBonDeCmdeSve.findAll());
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/find-all-bon-de-commande-by-profile/{idDigital}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllFicheBonDeCommandeByProfile(@PathVariable(name = "idDigital") String idDigital) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            response = ResponseEntity.status(HttpStatus.OK).body(ficheBonDeCmdeSve.findAll(profileService.findOne(idDigital)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/find-all-bon-de-commande-by-entreprise/{idEntreprise}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllFicheBonDeCommandeByEntreprisePending(@PathVariable(name = "idEntreprise") Long idEntreprise) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            response = ResponseEntity.status(HttpStatus.OK).body(

                new MdleFicheComande(

                    ficheBonDeCmdeSve.findAll(entrepriseService.findOne(idEntreprise), suiviDemandeService.findOne(5L)),

                    ficheBonDeCmdeSve.findAll(entrepriseService.findOne(idEntreprise), suiviDemandeService.findOne(6L)),

                    ficheBonDeCmdeSve.findAll(entrepriseService.findOne(idEntreprise), suiviDemandeService.findOne(7L)),

                    ficheBonDeCmdeSve.findAll(entrepriseService.findOne(idEntreprise), suiviDemandeService.findOne(8L))
                )
            );
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/find-all-bon-de-commande-by-entreprise-resolved/{idEntreprise}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllFicheBonDeCommandeByEntrepriseResolved(@PathVariable(name = "idEntreprise") Long idEntreprise) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            response = ResponseEntity.status(HttpStatus.OK).body(ficheBonDeCmdeSve.findAll(entrepriseService.findOne(idEntreprise), suiviDemandeService.findOne(6L)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @GetMapping(value = "/find-all-historique-bon-de-commande-by-ref/{reference}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAllFicheBonDeCommandeByReference(@PathVariable(name = "reference") String reference) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            response = ResponseEntity.status(HttpStatus.OK).body(historiqueService.findAll(ficheBonDeCmdeSve.findOne(reference)));
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PutMapping(value = "/delete-item-bon-de-commande", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "Representative agent model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = FicheBonDeCommande.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteFicheBonDeCommande(@RequestBody FicheBonDeCommande object) {

        try {
            if(object != null){

                FicheBonDeCommande item = ficheBonDeCmdeSve.findOne(object.getReference());

                if(item!= null) {

                    ficheBonDeCmdeSve.disable(item.getId());

                    FicheBonDeCommande _up_ = item;

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
                            new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getReference()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire la suppression du type message "+object.getReference()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




    @PutMapping(value = "/active-item-bon-de-commande", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "MesClients model", notes = "MesClients model")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = FicheBonDeCommande.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> activeFicheBonDeCommande(@RequestBody FicheBonDeCommande object) {

        try {
            if(object != null){

                FicheBonDeCommande item = ficheBonDeCmdeSve.findOne(object.getReference());

                if(item!= null) {

                    ficheBonDeCmdeSve.enable(item.getId());

                    FicheBonDeCommande up = item;

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
                            new RequestInformation(400, "Impossible de faire l'activation du type message "+object.getReference()),
                            new RequestMessage(null, "Success")
                    ));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponse(
                        new RequestInformation(400, "Impossible de faire l'activation du type messgae "+object.getReference()),
                        new RequestMessage(null, "Success")
                ));
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping(value = "/deleted-all-bon-de-commande", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> deleteAllFicheBonDeCommande() {

        try {

            List<FicheBonDeCommande> items = ficheBonDeCmdeSve.findAll();

            for (FicheBonDeCommande item :items){

                FicheBonDeCommande models = ficheBonDeCmdeSve.findOne(item.getReference());

                if(!models.toString().isEmpty()) {

                    models.setActive(false);

                    ficheBonDeCmdeSve.update(models);
                }
            }

            return getResponseEntity(ficheBonDeCmdeSve.findAll().isEmpty());
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PostMapping(value = "/save-bon-de-commande", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> createdNewLivraison (@RequestBody FicheBonDeCommande model) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        String auditKey = Utils.instant2String();

        try {

            System.out.println(">>>>>>>>>>>>>>>> DEBUT DU PROCESS DE CREATION DU BON DE COMMANDE OU RECEUPERATION DE COLIS <<<<<<<<<<<<<<<<<<");

            if(model == null) {

                response = ResponseEntity.status(HttpStatus.CREATED).body(
                    new RequestResponse(
                        new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Impossible d'enregistrer les informations soumises pour le bon de commande"),
                        new RequestMessage(null, null)
                    )
                );
            }
            else{

                //TODO ::: RECUPERATION DES INFORMATIONS DU PARTENAIRE
                PartenaireAffaire clients = mesClientsService.findOne(model.getOwnerNumber());

                if(clients == null){

                    response = ResponseEntity.status(HttpStatus.CREATED).body(
                        new RequestResponse(
                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Le partanaire d'affaire demandé n'existe pas. Nous vous prions de le créer avant toute opérations"),
                            new RequestMessage(null, null)
                        )
                    );
                }
                else{

                    //TODO ::: ENREGISTREMENT DU BON DE RECUPERATION DE COLIS
                    System.out.println(">>>>>>>>>>>>>>>> TODO ::: ENREGISTREMENT DU BON DE RECUPERATION DE COLIS <<<<<<<<<<<<<<<<<<");
                    if(ficheBonDeCmdeSve.findOne(model.getReference()) == null){

                        auditFluxService

                            .create(new AuditFlux(
                                    null, auditKey, Instant.now(),
                                    TypeNotification.LIVRAISON.toString(),
                                    "ENREGISTREMENT DU BON DE RECUPERATION DE COLIS POUR LE PARTENAIRE "+ clients.getNomComplet().toUpperCase(),
                                    new ObjectMapper().writeValueAsString(model), "", Direction.INPUT
                                )
                            );

                        double mtGlobal  = 0;

                        double mtGlobalLiraison  = 0;

                        //TODO ::: RECUPERATION DU MONTANT GLOBAL
                        for (int i = 0; i < model.getBonDeCommandeList().size(); i++) {

                            mtGlobal += model.getBonDeCommandeList().get(i).getMontantColis();

                            mtGlobalLiraison += model.getBonDeCommandeList().get(i).getPrixLivraison();
                        }

                        FicheBonDeCommande ficheBonDeCommande = new FicheBonDeCommande();

                        ficheBonDeCommande.setOrdre(Utils.generateRandom(8));

                        ficheBonDeCommande.setReference(model.getReference());

                        ficheBonDeCommande.setZoneRecuperation(model.getZoneRecuperation());

                        ficheBonDeCommande.setPrecisionZoneRecup(model.getPrecisionZoneRecup().toUpperCase());

                        ficheBonDeCommande.setProfile(model.getProfile());

                        ficheBonDeCommande.setMontantGlobal(mtGlobal);

                        ficheBonDeCommande.setMontantGlobalLivraison(mtGlobalLiraison);

                        ficheBonDeCommande.setPartenaire(clients);

                        ficheBonDeCommande.setDatelivraisonSouhaite(model.getDatelivraisonSouhaite());

                        ficheBonDeCommande.setTypeLivraison(model.getTypeLivraison());

                        ficheBonDeCommande.setNbreArticle(model.getBonDeCommandeList().size());

                        ficheBonDeCommande.setEntreprise(model.getProfile().getGareRoutiere().getCompagnie());

                        FicheBonDeCommande fchCmde  = ficheBonDeCmdeSve.create(ficheBonDeCommande);

                        if (fchCmde.getId() == null) {

                            response = ResponseEntity.status(HttpStatus.CREATED).body(
                                    new RequestResponse(
                                            new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                            new RequestMessage(null, null)
                                    )
                            );
                        }
                        else {

                            //TODO ::: ENREGISTREMENT DU BON DE LIVRAISON
                            if(bonDeCommandeService.findAll(fchCmde.getOrdre()).size() == 0){

                                BonDeCommande bonDeCommande = null;

                                for (BonDeCommande item : model.getBonDeCommandeList()){

                                    bonDeCommande = new BonDeCommande();

                                    bonDeCommande.setOrdre(fchCmde.getOrdre());

                                    bonDeCommande.setDestinataire(item.getDestinataire().toUpperCase());

                                    bonDeCommande.setContactDestinataire(item.getContactDestinataire());

                                    bonDeCommande.setElementCassable(item.getElementCassable());

                                    bonDeCommande.setNomProduit(item.getNomProduit().toUpperCase());

                                    bonDeCommande.setNatureColis(item.getNatureColis());

                                    bonDeCommande.setMontantColis(item.getMontantColis());

                                    bonDeCommande.setPrixLivraison(item.getPrixLivraison());

                                    bonDeCommande.setZoneLivraison(item.getZoneLivraison());

                                    bonDeCommande.setLieuDeLivraison(item.getLieuDeLivraison().toUpperCase());

                                    bonDeCommandeService.create(bonDeCommande);
                                }
                            }


                            if(clientsService.findAll(fchCmde.getOrdre()).size() == 0){

                                Clients clients1 = null;

                                for (BonDeCommande item : model.getBonDeCommandeList()){

                                    clients1 = new Clients();

                                    clients1.setOrdre(fchCmde.getOrdre());

                                    clients1.setOrdreClient(Utils.generateRandom(6));

                                    clients1.setDestinataire(item.getDestinataire().toUpperCase());

                                    clients1.setContactDestinataire(item.getContactDestinataire());

                                    clients1.setNomProduit(item.getNomProduit().toUpperCase());

                                    clients1.setMontantColis(item.getMontantColis());

                                    clients1.setPartenaire(clients);

                                    clientsService.create(clients1);
                                }
                            }


                            //TODO ::: ENREGISTREMENT DE L'HISTORIQUE DE LA FICHE DE COMMANDE OU RECEUPERATION DE COLIS
                            System.out.println(">>>>>>>>>>>>>>>> TODO ::: ENREGISTREMENT DE L'HISTORIQUE DE LA FICHE DE COMMANDE OU RECEUPERATION DE COLIS <<<<<<<<<<<<<<<<<<");

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                            LocalDateTime now = LocalDateTime.now();


                            Historique historique = new Historique();

                            historique.setLibelle("Enregistrement et soumission du bon de récupértaion de colis journalier par "+ model.getProfile().getNomPrenoms().toUpperCase()+" à la société de livraison "+ fchCmde.getProfile().getGareRoutiere().getCompagnie().getCompagnie()+". \nSoumission effectué par " +model.getProfile().getNomPrenoms()+ " pour le compte de "+fchCmde.getPartenaire().getNomComplet().toUpperCase()+" à "+dtf.format(now));

                            historique.setFicheBonDeCommande(fchCmde);

                            historique.setOrdre(Utils.generateRandom(8));

                            historique.setTypeNotification(TypeNotification.ENREGISTREMENT);

                            Historique hist = historiqueService.create(historique);

                            if(hist.getId() == null){

                                response = ResponseEntity.status(HttpStatus.CREATED).body(
                                        new RequestResponse(
                                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Echec de l'enregistrement de l'historique de la livraion à la référence " + model.getReference().toUpperCase()),
                                                new RequestMessage(null, null)
                                        )
                                );
                            }
                            else{

                                //TODO ::: ENVOI DES NOTIFICATION (SMS ET EMAIL) A LA SOCIETE DE LIVRAION
                                //System.out.println(">>>>>>>>>>>>>>>> TODO ::: ENVOI DES NOTIFICATION (SMS ET EMAIL) A LA SOCIETE DE LIVRAION <<<<<<<<<<<<<<<<<<");

                                //ENVOI EMAIL AU CLIENT OU PARTEANIRE DE LA SOCIETE SOUSCRIPTRICE
                                if(!clients.getEmail().isEmpty()){

                                    //System.out.println(">>>>>>>>>>>>>>>> ENVOI EMAIL AU CLIENT OU PARTEANIRE DE LA SOCIETE SOUSCRIPTRICE <<<<<<<<<<<<<<<<<<");

                                    EmailMessage email = new EmailMessage();

                                    email.setEmail(clients.getEmail());

                                    email.setSubject("ACCUSÉ DE RECEPTION POUR LE BON DE COLIS JOURNALIER " + model.getReference());

                                    email.setType("ACCUSE_RECEPTION");

                                    email.setUsername(fchCmde.getPartenaire().getNomComplet().toUpperCase()); //Non client

                                    email.setDefaulPwd(fchCmde.getProfile().getGareRoutiere().getCompagnie().getCompagnie().toUpperCase()); //nomEntreprise

                                    email.setSts(fchCmde.getProfile().getGareRoutiere().getCompagnie().getEmail().toLowerCase()); //emailEntreprise

                                    email.setOther(fchCmde.getProfile().getGareRoutiere().getCompagnie().getContact()+" - "+ fchCmde.getProfile().getGareRoutiere().getCompagnie().getContactResponsable()); //contactEntreprise

                                    jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                                }


                                //ENVOI DU EMAIL A LA SOCIETE SOUSCRIPTRICE
                                if(!fchCmde.getProfile().getGareRoutiere().getCompagnie().getEmail().isEmpty()){

                                    //System.out.println(">>>>>>>>>>>>>>>> ENVOI DU EMAIL A LA SOCIETE SOUSCRIPTRICE <<<<<<<<<<<<<<<<<<");

                                    EmailMessage email = new EmailMessage();

                                    email.setEmail(model.getProfile().getGareRoutiere().getCompagnie().getEmail());

                                    email.setSubject("DEMANDE DE RECUPERATION D'UN BON DE COLIS JOURNALIER - " + fchCmde.getPartenaire().getNomComplet().toUpperCase());

                                    email.setType("NOTIFICATION_COLIS_JOURNALIER");

                                    email.setUsername(fchCmde.getPartenaire().getNomComplet().toUpperCase()); //nomEntreprise

                                    jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                                }


                                //TODO ::: ENVOI SMS AU DIFFERENTS ACTEURS
                                //ENVOI SMS AU CLIENTS
                                String number       = model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+clients.getContact();
                                String promReceiver = fchCmde.getPartenaire().getNomComplet();
                                String msg = "Bonjour "+ promReceiver +", Votre demande de récupération de colis par l'entreprise de livraison a été enregistrée et envoyée avec succès. Consulter l'application pour le suivi.";

                                if (!number.isEmpty()){

                                    SmsCredential smsCredential = new  SmsCredential();

                                    SmsEnvoye smsEnvoye         = new SmsEnvoye();

                                    smsCredential.setPassword(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());
                                    smsCredential.setUsername(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());
                                    smsCredential.setSenderId(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                    smsEnvoye.setTypeMessage(typeMessageService.findById(4L));

                                    smsEnvoye.setNumeroDestinataire(number);

                                    smsEnvoye.setDestinataire(fchCmde.getPartenaire().getNomComplet().toUpperCase());

                                    smsEnvoye.setCorpsMessage(msg);

                                    smsEnvoye.setSmsCredential(smsCredential);


                                    /*System.out.println(">>>>>>>>>>>>>>>> ENVOI SMS AU CLIENTS <<<<<<<<<<<<<<<<<<");

                                    SmsMessage sms = new SmsMessage();

                                    sms.setTypeMessage(4L);

                                    sms.setToId(number);

                                    sms.setContent(msg);

                                    sms.setFromName(fchCmde.getPartenaire().getNomComplet().toUpperCase());

                                    sms.setUsername(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                    sms.setPassword(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                    sms.setSenderId(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                    jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));*/

                                }

                                //ENVOI SMS A LA SOCIETE SOUSCRIPTRICE
                                String number2       = model.getProfile().getGareRoutiere().getCompagnie().getPaysAutorise().getIndicatif()+""+model.getProfile().getGareRoutiere().getCompagnie().getContact();

                                if (!number2.isEmpty()){

                                    String promReceiver2 = model.getProfile().getGareRoutiere().getCompagnie().getCompagnie().toUpperCase();
                                    String content       = "Bonjour "+ promReceiver2 +", Une demande de récupération de colis par votre partenaire "+fchCmde.getPartenaire().getNomComplet().toUpperCase()+" vous a été adressé. Nous prions de consulter l'application.";

                                    System.out.println(">>>>>>>>>>>>>>>> ENVOI SMS A LA SOCIETE SOUSCRIPTRICE <<<<<<<<<<<<<<<<<<");

                                    SmsMessage sms = new SmsMessage();

                                    sms.setTypeMessage(4L);

                                    sms.setToId(number);

                                    sms.setContent(content);

                                    sms.setFromName(promReceiver2);

                                    sms.setUsername(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getUsername());

                                    sms.setPassword(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getPassword());

                                    sms.setSenderId(model.getProfile().getGareRoutiere().getCompagnie().getSmsCredential().getSenderId());

                                    jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
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
                                new RequestInformation(HttpStatus.BAD_REQUEST.value(), "Ce bon de recupération a été déjà envoyé. Nous vous prions de cosulter le tracking dans la partie Tracking Colis"),
                                new RequestMessage(null, null)
                            )
                        );
                    }
                }
            }
        }

        catch (Exception e) {

            e.printStackTrace();

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

            auditFluxService
                    .create(new AuditFlux(
                            null, auditKey, Instant.now(),
                            TypeNotification.LIVRAISON.toString(),
                            "Echec de l'enregistrement du bon de recupération de colis pour le compte du clien",
                            new ObjectMapper().writeValueAsString(model), e.getMessage(), Direction.OUTPUT
                    ));
        }

        finally {

            return response;
        }
    }



    @PostMapping(value = "/recherche-de-fiche-recuperation-colis", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The data has been correctly created in the system", response = FicheBonDeCommande.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> rechercheFicheRecuperation (@RequestBody RechercheCourrierModel search) {

        log.info("[ DI-GITAL WEB :: CONTROLLER ] SEARCH COLIS RESQUEST  =======================> param {}", search);

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            FicheBonDeCommande svce = ficheBonDeCmdeSve.findOne(search.getReference());

            if(svce == null){

                response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(

                    new RequestResponse(
                        new RequestInformation(400, "La fiche de bon de commande &agrave; la r&eacute;f&eacute;rence n° "+search.getReference()+" n'a pas &eacute;t&eacute; retrouv&eacute;"),
                        new RequestMessage(null, null)
                    )
                );
            }
            else{

                svce.setBonDeCommandeList(bonDeCommandeService.findAll(svce.getOrdre()));

                response = ResponseEntity.status(HttpStatus.OK).body(svce);
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            //e.printStackTrace();
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            log.info("[ DI-GITAL WEB :: CONTROLLER ] SEARCH COLIS RESQUEST =======================>  DONE");
            return response;
        }
    }



    @GetMapping(value = "/find-one-fiche-bon-de-commande-by-ref/{reference}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = FicheBonDeCommande.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findOneFicheBonDeCommandeByReference(@PathVariable(name = "reference") String reference) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            FicheBonDeCommande fche = ficheBonDeCmdeSve.findOne(reference);

                fche.setBonDeCommandeList(bonDeCommandeService.findAll(fche.getOrdre()));

            response = ResponseEntity.status(HttpStatus.OK).body(fche);
        }
        catch (Exception e) {
            // TODO: handle exception
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }



    @PutMapping(value = "/update-article-de-la-fiche", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteArticleFiche(@RequestBody BonDeCommande item) {

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

                BonDeCommande up_data = bonDeCommandeService.findById(item.getId());

                if(up_data != null) {

                    up_data.setDestinataire(item.getDestinataire().toUpperCase());

                    up_data.setContactDestinataire(item.getContactDestinataire());

                    up_data.setElementCassable(item.getElementCassable());

                    up_data.setNomProduit(item.getNomProduit().toUpperCase());

                    up_data.setNatureColis(item.getNatureColis());

                    up_data.setMontantColis(item.getMontantColis());

                    up_data.setPrixLivraison(item.getPrixLivraison());

                    BonDeCommande bnCmd = bonDeCommandeService.update(up_data);

                    if (bnCmd.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {

                        //MISE A JOUR DU MONTANT GLOBALE SUR LA FICHE
                        double newMtGlobal = 0;

                        double mtGlobalLiraison  = 0;

                        for (BonDeCommande bonDeCommande : bonDeCommandeService.findAll(item.getOrdre())) {

                            newMtGlobal += bonDeCommande.getMontantColis();

                            mtGlobalLiraison += bonDeCommande.getPrixLivraison();
                        }

                        FicheBonDeCommande fchCmde  = ficheBonDeCmdeSve.findOne(item.getOrdre());

                        fchCmde.setMontantGlobal(newMtGlobal);

                        fchCmde.setMontantGlobalLivraison(mtGlobalLiraison);

                        FicheBonDeCommande _fchCmde_ = ficheBonDeCmdeSve.update(fchCmde);

                        if (_fchCmde_.getId() == null) {

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

            e.printStackTrace();

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }



    @PutMapping(value = "/update-basic-info-de-la-fiche", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> upadteBasicInfoFiche(@RequestBody FicheBonDeCommande item) {

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

                FicheBonDeCommande up_data = ficheBonDeCmdeSve.findOne(item.getReference());

                if(up_data != null) {

                    up_data.setTypeLivraison(item.getTypeLivraison().toUpperCase());

                    up_data.setZoneRecuperation(item.getZoneRecuperation());

                    up_data.setDatelivraisonSouhaite(item.getDatelivraisonSouhaite());

                    up_data.setPrecisionZoneRecup(item.getPrecisionZoneRecup().toUpperCase());

                    FicheBonDeCommande fchCmde = ficheBonDeCmdeSve.update(up_data);

                    if (fchCmde.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new RequestResponse(
                                new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                new RequestMessage(null, "Fail")
                            )
                        );
                    }
                    else {

                        //MISE A JOUR DU MONTANT GLOBALE SUR LA FICHE
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

            e.printStackTrace();

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {

            return response;
        }
    }


    @PutMapping(value = "/traitement-du-bon-soumis", consumes = "application/json", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The agent has been correctly created in the system", response = RequestResponse.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> traitementBon(@RequestBody FicheBonDeCommande item) {

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

                FicheBonDeCommande up_data = ficheBonDeCmdeSve.findOne(item.getReference());

                if(up_data != null) {

                    up_data.setSuiviDemande(item.getStatuTraitement());

                    FicheBonDeCommande bnCmd = ficheBonDeCmdeSve.update(up_data);

                    if (bnCmd.getId() == null) {

                        response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new RequestResponse(
                                        new RequestInformation(400, "Quelque chose ne va pas dans l'insertion de la donnée, veuillez vérifier les données fournies"),
                                        new RequestMessage(null, "Fail")
                                )
                        );
                    }
                    else {

                        //ENVOI DES NOTIFICATION SMS AU CLIENT OU PARTENAIRE D'AFFAIRE
                        String number2       = item.getPartenaire().getEntreprise().getPaysAutorise().getIndicatif()+""+item.getPartenaire().getContact();
                        String promReceiver2 = item.getPartenaire().getNomComplet().toUpperCase();
                        String message       = null;

                        if(item.getStatuTraitement().getId() == 6){

                            //ENVOI DES NOTIFICATION SMS AU CLIENT OU PARTENAIRE D'AFFAIRE
                            message = "Partenaire votre bon de récupération de colis journalier a été traités par la société de livraison. Vous pourriez consulter l'état dans l'application.";

                            //ENVOI DU MAIL DE NOTIFICATION AU CLIENT OU PARTENAIRE D'AFFAIRE
                            if(item.getPartenaire() != null && !item.getPartenaire().getEmail().isEmpty()){

                                System.out.println(">>>>>>>>>>>>>>>> ENVOI DU MAIL DE NOTIFICATION AU CLIENT OU PARTENAIRE D'AFFAIRE <<<<<<<<<<<<<<<<<<");

                                EmailMessage email = new EmailMessage();

                                email.setEmail(item.getPartenaire().getEmail());

                                email.setSubject("TRAITEMENT BON DE RÉCÉPTION - REF : " + item.getReference());

                                email.setType("TRAITEMENT_BON");

                                email.setUsername(item.getEntreprise().getCompagnie().toUpperCase()); //entite

                                email.setDefaulPwd(item.getStatuTraitement().getSuivi().toUpperCase()); //staut

                                email.setSts(item.getEntreprise().getEmail().toLowerCase()); //mailEntite

                                email.setOther(item.getEntreprise().getContact()+" - "+ item.getEntreprise().getContactResponsable()); //phoneEntite

                                jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                            }


                            //ENVOI DU MAIL DE NOTIFICATION AU CLIENT DE DIGITAL-WEB
                            if(item.getEntreprise() != null && !item.getEntreprise().getEmail().isEmpty()){

                                System.out.println(">>>>>>>>>>>>>>>> ENVOI EMAIL AU CLIENT DE DIGIT-AL <<<<<<<<<<<<<<<<<<");

                                EmailMessage email = new EmailMessage();

                                email.setEmail(item.getEntreprise().getEmail());

                                email.setSubject("TRAITEMENT BON DE RÉCÉPTION - REF : " + item.getReference());

                                email.setType("NOTIF_TRAITEMENT");

                                jmsProducer.send(new JmsMessage("Mail to send", Converter.pojoToJson(email), EmailMessage.class));
                            }
                        }


                        if(item.getStatuTraitement().getId() == 7){
                            message = "Partenaire votre bon de récupération de colis journalier a été retiré par la société de livraison. Vous pourriez consulter l'état dans l'application.";
                        }

                        if(item.getStatuTraitement().getId() == 8){

                            message = "Partenaire votre bon de récupération de colis journalier a été rejeté par la société de livraison. Vous pourriez consulter l'état dans l'application.";
                        }

                        if (!number2.isEmpty() && message != null){

                            SmsMessage sms = new SmsMessage();

                            sms.setTypeMessage(4L);

                            sms.setToId(number2);

                            sms.setContent(message);

                            sms.setFromName(promReceiver2);

                            sms.setUsername(item.getPartenaire().getEntreprise().getSmsCredential().getUsername());

                            sms.setPassword(item.getPartenaire().getEntreprise().getSmsCredential().getPassword());

                            sms.setSenderId(item.getPartenaire().getEntreprise().getSmsCredential().getSenderId());

                            jmsProducer.send(new JmsMessage("SMS to send", Converter.pojoToJson(sms), SmsMessage.class));
                        }



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

            e.printStackTrace();

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
