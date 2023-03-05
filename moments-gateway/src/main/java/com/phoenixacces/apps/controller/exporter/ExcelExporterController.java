package com.phoenixacces.apps.controller.exporter;

import com.phoenixacces.apps.models.livraison.PrintFicheColisAllDataModel;
import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import com.phoenixacces.apps.persistence.services.module.ExamRecordService;
import com.phoenixacces.apps.persistence.services.module.livraison.FicheBonDeCommandeService;
import com.phoenixacces.apps.persistence.services.parametrage.EntrepriseService;
import com.phoenixacces.apps.persistence.services.parametrage.SuiviDemandeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Module Livraisons - Exportation fichier excel")
@Slf4j
public class ExcelExporterController {

    private final ExamRecordService examRecordService;
    private final FicheBonDeCommandeService ficheBonDeCmdeSve;
    private final SuiviDemandeService suiviDemandeService;
    private final EntrepriseService entrepriseService;

    @Autowired
    public ExcelExporterController(ExamRecordService examRecordService,
                                   FicheBonDeCommandeService ficheBonDeCmdeSve,
                                   SuiviDemandeService suiviDemandeService,
                                   EntrepriseService entrepriseService) {
        this.examRecordService      = examRecordService;
        this.ficheBonDeCmdeSve      = ficheBonDeCmdeSve;
        this.suiviDemandeService    = suiviDemandeService;
        this.entrepriseService      = entrepriseService;
    }


    @RequestMapping(value = "/export-excel", method = RequestMethod.POST, produces="application/vnd.ms-excel")
    public void exportIntoExcel(@RequestBody PrintFicheColisAllDataModel data,  HttpServletResponse response) throws IOException {

        List<FicheBonDeCommande> listOfRecords = null;

        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=records_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        if(data.getType().equalsIgnoreCase("attente")){

            listOfRecords = ficheBonDeCmdeSve.findAll(entrepriseService.findOne(data.getIdEntreprise()), suiviDemandeService.findOne(5L));

        }
        else if(data.getType().equalsIgnoreCase("validé")){

            listOfRecords = ficheBonDeCmdeSve.findAll(entrepriseService.findOne(data.getIdEntreprise()), suiviDemandeService.findOne(6L));

        }
        else if(data.getType().equalsIgnoreCase("recup")){

            listOfRecords = ficheBonDeCmdeSve.findAll(entrepriseService.findOne(data.getIdEntreprise()), suiviDemandeService.findOne(7L));

        }
        else if(data.getType().equalsIgnoreCase("rejet")){

            listOfRecords = ficheBonDeCmdeSve.findAll(entrepriseService.findOne(data.getIdEntreprise()), suiviDemandeService.findOne(8L));

        }

        ExcelGenerator generator = new ExcelGenerator(listOfRecords);

        generator.generate(response);
    }
}
