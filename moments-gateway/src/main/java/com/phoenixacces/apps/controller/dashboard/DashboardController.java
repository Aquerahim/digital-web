package com.phoenixacces.apps.controller.dashboard;

import com.phoenixacces.apps.enumerations.EtatLecture;
import com.phoenixacces.apps.enumerations.ProfileType;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import com.phoenixacces.apps.persistence.entities.module.assurance.SendMessage;
import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import com.phoenixacces.apps.persistence.entities.module.livraison.Livreurs;
import com.phoenixacces.apps.persistence.models.SendMessageDto;
import com.phoenixacces.apps.persistence.models.StatistiqueAssurance;
import com.phoenixacces.apps.persistence.models.StatistiqueDepotBoisson;
import com.phoenixacces.apps.persistence.models.StatistiqueLivraisonModel;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.authentification.UserService;
import com.phoenixacces.apps.persistence.services.module.assurance.ModelMessageService;
import com.phoenixacces.apps.persistence.services.module.assurance.PorteFeuilleClientService;
import com.phoenixacces.apps.persistence.services.module.assurance.SendMessageService;
import com.phoenixacces.apps.persistence.services.module.livraison.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Module Livraisons - Get Stat by Profile")
@Slf4j
public class DashboardController {

    private final UserService userService;
    private final ProfileService profileService;
    private final LivreursService livreursService;
    private final FicheBonDeCommandeService ficheBonDeCommandeService;
    private final MesClientsService mesClientsService;
    private final LivraisonsService livraisonsService;
    private final PorteFeuilleClientService porteFeuilleClientService;
    private final SendMessageService sendMessageService;

    @Autowired
    public DashboardController(
            ProfileService profileService,
            FicheBonDeCommandeService ficheBonDeCommandeService,
            LivreursService livreursService,
            MesClientsService mesClientsService,
            LivraisonsService livraisonsService,
            PorteFeuilleClientService porteFeuilleClientService,
            SendMessageService sendMessageService,
            UserService userService){
        this.profileService                 = profileService;
        this.ficheBonDeCommandeService      = ficheBonDeCommandeService;
        this.livreursService                = livreursService;
        this.mesClientsService              = mesClientsService;
        this.livraisonsService              = livraisonsService;
        this.porteFeuilleClientService      = porteFeuilleClientService;
        this.sendMessageService             = sendMessageService;
        this.userService                    = userService;
    }


    @GetMapping(value = "/get-stat-by-profile/{idDigital}/{module}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @Transactional(readOnly = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> getStat(@PathVariable(name = "idDigital") String idDigital,
                                     @PathVariable(name = "module") String module) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            double mtGlobal             = 0;

            double mtGlobalLiraison     = 0;

            double conso                = 0;

            if(module.equalsIgnoreCase("livraison")){

                //TODO ::: RECUPERATION DU MONTANT GLOBAL
                for (FicheBonDeCommande ficheBonDeCommande : ficheBonDeCommandeService.findAll(profileService.findOne(idDigital))) {

                    mtGlobal += ficheBonDeCommande.getMontantGlobal();

                    mtGlobalLiraison += ficheBonDeCommande.getMontantGlobalLivraison();
                }

                StatistiqueLivraisonModel stats = new StatistiqueLivraisonModel();



                stats.setTotalMtGlobal(mtGlobal);

                stats.setTotalMtLivraison(mtGlobalLiraison);

                stats.setNbreClient(mesClientsService.findAll(profileService.findOne(idDigital)).size());

                stats.setLivraisonsList(livraisonsService.findAllByProfile(profileService.findOne(idDigital)));

                stats.setLivreursList(livreursService.find(profileService.findOne(idDigital)));

                stats.setNbreLivreur(stats.getLivreursList().size());

                stats.setNbreLivreurDispo(livreursService.findAll(true, profileService.findOne(idDigital)).size());

                stats.setNbreLivreurNonDispo(livreursService.findAll(false, profileService.findOne(idDigital)).size());

                stats.setNbreLivreurSuspendu(livreursService.findAllLivreur(profileService.findOne(idDigital), false).size());

                stats.setProfileList(profileService.findAll(profileService.findOne(idDigital).getGareRoutiere().getCompagnie()));

                response = ResponseEntity.status(HttpStatus.OK).body(stats);

            }

            if(module.equalsIgnoreCase("assurance")){

                StatistiqueAssurance stats = new StatistiqueAssurance();

                Profile profile = profileService.findOne(idDigital);

                List<SendMessageDto> sendMessageDtoList = new ArrayList<>();

                if(profile != null){

                    List<SendMessage> msgListTraite  = sendMessageService.findAll(false, profile, EtatLecture.TRAITE);

                    //List<SendMessage> msgListPending = sendMessageService.findAll(false, profile, EtatLecture.PENDING);

                    if (profile.getProfileType() == ProfileType.GESTIONNAIRE){

                        for (SendMessage sendMessage : sendMessageService.findAll(false, profile, EtatLecture.TRAITE)){

                            conso += sendMessage.getNbrePage();

                            SendMessageDto messageDto = new SendMessageDto();

                            messageDto.setCode("ID-"+sendMessage.getId());

                            messageDto.setNomPrenoms(sendMessage.getPorteFeuilleClient().getNomClient());

                            messageDto.setDateEnvoi(sendMessage.getDateEnvoi());

                            messageDto.setContact(sendMessage.getNumeroContact());

                            messageDto.setStatut(sendMessage.getStatut().toString());

                            messageDto.setEntreprise(sendMessage.getEntreprises().getCompagnie());

                            messageDto.setTypeMessage(sendMessage.getTypeMessage());

                            messageDto.setAuteur(sendMessage.getProfile().getNomPrenoms());

                            messageDto.setUserName(userService.findOne(sendMessage.getProfile()).getUsername());

                            messageDto.setMessage(sendMessage.getMessage());

                            messageDto.setPage(sendMessage.getNbrePage()+" Page(s)");

                            sendMessageDtoList.add(messageDto);
                        }

                        stats.setSendMessageList(sendMessageDtoList);

                        stats.setNbrSmsProgramme(sendMessageService.findAll(false, profile, EtatLecture.PENDING).size());

                        stats.setCoutConsommation(conso * 25);

                        stats.setNbrSmsDejaEnvoye(conso);
                    }
                    else{

                        stats.setNbrSmsProgramme(sendMessageService.findAll(false, profile.getGareRoutiere().getCompagnie(), EtatLecture.PENDING).size());

                        for (SendMessage sendMessage : sendMessageService.findAll(false, profile.getGareRoutiere().getCompagnie(), EtatLecture.TRAITE)){

                            conso += sendMessage.getNbrePage();

                            SendMessageDto messageDto = new SendMessageDto();

                            messageDto.setCode("ID-"+sendMessage.getId());

                            messageDto.setNomPrenoms(sendMessage.getPorteFeuilleClient().getNomClient());

                            messageDto.setDateEnvoi(sendMessage.getDateEnvoi());

                            messageDto.setContact(sendMessage.getNumeroContact());

                            messageDto.setStatut(sendMessage.getStatut().toString());

                            messageDto.setEntreprise(sendMessage.getEntreprises().getCompagnie());

                            messageDto.setTypeMessage(sendMessage.getTypeMessage());

                            messageDto.setAuteur(sendMessage.getProfile().getNomPrenoms());

                            messageDto.setUserName(userService.findOne(sendMessage.getProfile()).getUsername());

                            messageDto.setMessage(sendMessage.getMessage());

                            messageDto.setPage(sendMessage.getNbrePage()+" Page(s)");

                            sendMessageDtoList.add(messageDto);
                        }

                        stats.setSendMessageList(sendMessageDtoList);

                        stats.setCoutConsommation(conso * 25);

                        stats.setNbrSmsDejaEnvoye(conso);
                    }

                    stats.setNbreClientPorteFeuille(porteFeuilleClientService.findAll(profile.getGareRoutiere().getCompagnie()).size());
                }

                response = ResponseEntity.status(HttpStatus.OK).body(stats);
            }

            if(module.equalsIgnoreCase("depotBossion")){

                StatistiqueDepotBoisson stats = new StatistiqueDepotBoisson();

                stats.setMtAccompte(0);

                stats.setMtDepense(0);

                stats.setMtRemise(0);

                stats.setSolde(0);

                stats.setMtVenteCredit(0);

                stats.setMtRemise(0);

                response = ResponseEntity.status(HttpStatus.OK).body(stats);
            }
        }
        catch (Exception e) {
            // TODO: handle exception

           // e.printStackTrace();

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        finally {
            return response;
        }
    }
}
