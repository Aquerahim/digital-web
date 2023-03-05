package com.phoenixacces.apps.controller.print;

import com.lowagie.text.DocumentException;
import com.phoenixacces.apps.models.livraison.PrintFicheColisAllDataModel;
import com.phoenixacces.apps.models.livraison.PrintFicheColisDayModel;
import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import com.phoenixacces.apps.persistence.entities.parametrage.SuiviDemande;
import com.phoenixacces.apps.persistence.services.module.livraison.FicheBonDeCommandeService;
import com.phoenixacces.apps.persistence.services.parametrage.SuiviDemandeService;
import com.phoenixacces.apps.services.print.ImpressionService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.allegro.finance.tradukisto.ValueConverters;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "${env.ws}")
@Api(description = "API Module Livraisons - Visualisation Document")
@Slf4j
public class VisualisationDocumentController {

    @Autowired
    private ImpressionService impressionService;

    @Autowired
    private FicheBonDeCommandeService ficheBonDeCommandeService;

    @Autowired
    private SuiviDemandeService suiviDemandeService;

    ValueConverters converter = ValueConverters.FRENCH_INTEGER;

    private static final String JASPER_FICHE_COLIS_JOURNALIER = "FicheBonDeCommandeProduit";


    @RequestMapping(value = "/fiche-de-colis-journalier", method = RequestMethod.POST, produces = "application/pdf")
    public @ResponseBody
    void PRINT_FICHE_DAY (@RequestBody PrintFicheColisDayModel data, HttpServletResponse response) throws DocumentException, IOException {

        try {

            List<JasperPrint> jasperPrintList = new ArrayList<>();

            String[] exemplaire = {"Exemplaire "+data.getNomPartenaire(), "Exemplaire "+data.getNomEntreprise()};

            for (String s : exemplaire) {

                data.setExemplaire(s);

                data.setAdresse(WordUtils.capitalize(data.getAdresse().toLowerCase()));

                data.setEmail(data.getEmail().toLowerCase());

                data.setMontantLettre("# Ce présent bon de recupération de colis journalier est arrêté à la somme de " + converter.asWords((int) data.getMontantGlobal()) + " Francs Cfa. #");

                List<PrintFicheColisDayModel> printReceiptModels = new LinkedList<>();

                printReceiptModels.add(data);

                JasperPrint jasperPrint = impressionService.loadAndFillReport(JASPER_FICHE_COLIS_JOURNALIER, printReceiptModels);

                jasperPrintList.add(jasperPrint);
            }

            impressionService.exportPdf(response, jasperPrintList);
        }
        catch (Exception e) {
            //e.printStackTrace();
            log.debug(e.getMessage());
        }
    }

    @RequestMapping(value = "/fiche-all-colis-journalier", method = RequestMethod.POST, produces = "application/pdf")
    public @ResponseBody
    void PRINT_FICHE_ALL_DATA (@RequestBody PrintFicheColisAllDataModel data, HttpServletResponse response) throws DocumentException, IOException {

        try {

            List<JasperPrint> jasperPrintList = new ArrayList<>();

            String[] exemplaire = {"Exemplaire Partenaire d'affaire"};

            for (String s : exemplaire) {

                data.setExemplaire(s);

                data.setNbrTraite(ficheBonDeCommandeService.findAll(suiviDemandeService.findOne(6L)).size());

                data.setPending(ficheBonDeCommandeService.findAll(suiviDemandeService.findOne(5L)).size());

                data.setRecupere(ficheBonDeCommandeService.findAll(suiviDemandeService.findOne(7L)).size());

                data.setTotal(data.getList().size());

                System.out.println(">>> >>>>>> >>>>>>>>>>" + data.toString());

                List<PrintFicheColisAllDataModel> printReceiptModels = new LinkedList<>();

                printReceiptModels.add(data);

                JasperPrint jasperPrint = impressionService.loadAndFillReport("FicheBonDeCommande", printReceiptModels);

                jasperPrintList.add(jasperPrint);
            }

            impressionService.exportPdf(response, jasperPrintList);
        }
        catch (Exception e) {
            //e.printStackTrace();
            log.debug(e.getMessage());
        }
    }
}
