package com.phoenixacces.apps.controller.parametrage.notification;

import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.persistence.entities.parametrage.NotificationSysteme;
import com.phoenixacces.apps.persistence.services.authentification.ProfileService;
import com.phoenixacces.apps.persistence.services.parametrage.NotificationSystemeService;
import com.phoenixacces.apps.utiles.Utils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Notification Controller")
@Slf4j
public class NotificationController {

    private final NotificationSystemeService notificationService;
    private final ProfileService profileService;

    @Autowired
    private NotificationController (NotificationSystemeService notificationService,
                                    ProfileService profileService){
        this.notificationService    = notificationService;
        this.profileService         = profileService;
    }

    SimpleDateFormat datStr = new SimpleDateFormat("dd-MM-yyyy");

    SimpleDateFormat heure = new SimpleDateFormat("HH:mm:ss");


    @GetMapping(value = "/find-all-notification-by-profile/{IdDigitalApp}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> findAll(@PathVariable(name = "IdDigitalApp") String IdDigitalApp) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(IdDigitalApp != null){

                response = ResponseEntity.status(HttpStatus.OK).body(

                        notificationService.findAll(profileService.findOne(IdDigitalApp))
                );
            }
            else {

                response = ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
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


    @GetMapping(value = "/notification-deconnexion/{IdDigitalApp}", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer jwt_token", example = "Bearer jwt_token", required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return of list carried out successfully by the system", response = List.class),
            @ApiResponse(code = 400, message = "The transaction schema is invalid and therefore the transaction has not been created.", response = String.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. The error has been logged and is being investigated.", response = String.class)
    })
    public ResponseEntity<?> updateLastNotification(@PathVariable(name = "IdDigitalApp") String IdDigitalApp) {

        ResponseEntity response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            if(IdDigitalApp != null){

                NotificationSysteme notification = new NotificationSysteme();

                notification.setNotification("Fermeture de votre session à la date du "+datStr.format(new Date())+" à "+heure.format(new Date())+" a été effectué avec succès.");

                notification.setProfile(profileService.findOne(IdDigitalApp));

                notification.setReference(String.valueOf(Utils.generateRandom(6)));

                notification.setType(TypeNotification.Connexion);

                response = ResponseEntity.status(HttpStatus.OK).body(notificationService.create(notification));
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
