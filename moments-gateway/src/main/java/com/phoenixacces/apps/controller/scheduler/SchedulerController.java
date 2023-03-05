package com.phoenixacces.apps.controller.scheduler;

import com.phoenixacces.apps.controller.services.SchedulerService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Api(description = "Scheduler du module assurance")
@RequestMapping(value = "${env.ws}")
@Controller
@Slf4j
@EnableScheduling
public class SchedulerController {

    private final SchedulerService assuranceSchedulerService;

    @Autowired
    public SchedulerController(SchedulerService assuranceSchedulerService){
        this.assuranceSchedulerService    = assuranceSchedulerService;
    }

    // Envoi des messages programmés
    @Scheduled(cron = "${scheduler-sender-message}")
    public void sendMessage() throws Exception {

        log.info("+-------------+ [0] Lancement de scheduler pour l'envoi des messages +-------------+");

        assuranceSchedulerService.pushMessage(LocalDate.now());
    }



    // Envoi des messages programmés
    @Scheduled(cron = "${scheduler.rappel-compte-entreprise}")
    public void rappelExpirationAccount() throws Exception {

        log.info("+-------------+ [1] Lancement de scheduler pour la désactivation des comptes +-------------+");

        assuranceSchedulerService.rappelExpirationAccount(LocalDate.now());
    }



    // Envoi des messages programmés
    @Scheduled(cron = "${scheduler.expired-compte-entreprise}")
    public void disableAccount() throws Exception {

        log.info("+-------------+ [2] Lancement de scheduler pour la désactivation des comptes +-------------+");

        assuranceSchedulerService.disableAccount(LocalDate.now());
    }
}
