package com.phoenixacces.apps.controller.exporter;

import com.phoenixacces.apps.persistence.entities.module.livraison.FicheBonDeCommande;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public class ExcelGenerator {

    private List<FicheBonDeCommande> listRecords;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public ExcelGenerator(List<FicheBonDeCommande> listRecords) {
        this.listRecords = listRecords;
        workbook = new XSSFWorkbook();
    }


    private void writeHeader() {

        sheet = workbook.createSheet("Exam Records");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setBold(true);

        font.setFontHeight(12);

        style.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.getIndex());

        style.setFont(font);

        createCell(row, 0, "Référence", style);
        createCell(row, 1, "Type Livraison", style);
        createCell(row, 2, "Montant Global Article", style);
        createCell(row, 3, "Date Livraison souhaitée", style);
        createCell(row, 4, "Montant Global Livraison", style);
        createCell(row, 5, "Nombre articles", style);
        createCell(row, 6, "Lieu Recupération", style);
        createCell(row, 7, "Statut Traitement", style);
        createCell(row, 8, "Date Demande", style);

    }


    private void createCell(Row row, int columnCount, Object value, CellStyle style) {

        sheet.autoSizeColumn(columnCount);

        Cell cell = row.createCell(columnCount);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }

        else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }

        else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }

        else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
        }

        else {
            cell.setCellValue((String) value);
        }

        cell.setCellStyle(style);
    }


    private void write() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontHeight(12);

        style.setFont(font);

        for (FicheBonDeCommande record : listRecords) {

            Row row = sheet.createRow(rowCount++);

            int columnCount = 0;

            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                    .withLocale( Locale.FRENCH )
                    .withZone( ZoneId.systemDefault() );

            createCell(row, columnCount++, record.getReference(), style);

            createCell(row, columnCount++, record.getTypeLivraison(), style);

            createCell(row, columnCount++, new java.text.DecimalFormat("#,##0").format(Double.valueOf(record.getMontantGlobal())), style);

            createCell(row, columnCount++, record.getDatelivraisonSouhaite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), style);

            createCell(row, columnCount++, new java.text.DecimalFormat("#,##0").format(Double.valueOf(record.getMontantGlobalLivraison())), style);

            createCell(row, columnCount++, record.getNbreArticle(), style);

            createCell(row, columnCount++, record.getZoneRecuperation().getZoneCouverture()+" - "+ record.getPrecisionZoneRecup(), style);

            createCell(row, columnCount++, record.getSuiviDemande().getSuivi(), style);

            createCell(row, columnCount++, formatter.format( record.getCreation() ), style);

            // new java.text.DecimalFormat("#,##0").format(Double.valueOf(record.getMontantGlobal()))

        }
    }


    public void generate(HttpServletResponse response) throws IOException {

        writeHeader();

        write();

        ServletOutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        workbook.close();

        outputStream.close();

    }
}
