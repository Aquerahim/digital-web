package com.phoenixacces.apps.services.print;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Service
@Slf4j
public class ImpressionService {

    /** RESPONSE_HEADER_NAME **/
    private final String RESPONSE_HEADER_NAME = "Content-disposition";

    /** PDF_HEADER_VALUE */
    private final String RESPONSE_HEADER_VALUE = "attachment;filename=";

    /** RESPONSE_PDF_CONTENT_TYPE */
    private final String RESPONSE_PDF_CONTENT_TYPE = "application/pdf";

    /** PDF_EXT **/
    private final String PDF_EXT = ".pdf";

    /** JASPER_EXT **/
    private final String JASPER_EXT = ".jasper";


    @Value("${jasper.path}")
    private @Getter String jasperPath;


    /**
     * exporter en un seul pdf
     *
     * @param response
     * @param jasperPrintList
     *            void
     */
    public void exportPdf(HttpServletResponse response, List<JasperPrint> jasperPrintList) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        ExporterInput input = SimpleExporterInput.getInstance(jasperPrintList);
        SimpleOutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(baos);
        exporter.setExporterInput(input);
        exporter.setExporterOutput(output);

        //log.info("exportPdf ============> << {} >>", input);
        try {

            exporter.exportReport();
            byte[] bytes = baos.toByteArray();
            // prepare response
            response.addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE + "Fiche" + PDF_EXT);
            response.setContentType(RESPONSE_PDF_CONTENT_TYPE);
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();

        }
        catch (JRException | IOException e) {
            log.info("[ PDFPrinterService :: loadAndFillReport ] - ERROR = {}\n{}", e.getCause(), e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param jasperFileName
     * @param dataSource
     * @return JasperPrint
     */
    public JasperPrint loadAndFillReport(String jasperFileName, @SuppressWarnings("rawtypes") List dataSource) {
        JasperPrint jasperPrint = null;
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        //System.getProperty("user.dir")
        try {
            String filePath = new StringBuilder(jasperPath).append(jasperFileName).append(JASPER_EXT).toString().replace("//", File.separator);
            //log.info("[ PDFPrinterService :: loadAndFillReport ] - filePath = {}", filePath);
            File report = new File(filePath);

            log.info("[ PDFPrinterService :: loadAndFillReport ] - file exists {}", report.exists() ? "Yes" : "No");
            FileInputStream fis = new FileInputStream(report);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);
            JasperReport jaspertReport = (JasperReport) JRLoader.loadObject(bufferedInputStream);
            jasperPrint = JasperFillManager.fillReport(jaspertReport, null,
                    new JRBeanCollectionDataSource(dataSource, false));
            log.info("[ PDFPrinterService :: loadAndFillReport ] - SUCCESS !!!");

        }

        catch (FileNotFoundException | JRException e) {
            log.info("[ PDFPrinterService :: loadAndFillReport ] - ERROR = {}\n{}", e.getCause(), e.getMessage());
            e.printStackTrace();

        }

        finally {
            log.info("[ PDFPrinterService :: loadAndFillReport ] - jasperPrint == null ? {}", jasperPrint == null ? "Yes" : "No");
            log.info("[ PDFPrinterService :: loadAndFillReport ] - fin");
            return jasperPrint;
        }
    }
}
