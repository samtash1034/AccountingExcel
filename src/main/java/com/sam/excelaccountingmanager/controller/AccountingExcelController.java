package com.sam.excelaccountingmanager.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class AccountingExcelController {

    @PostMapping(path = "/excel/import")
    public ResponseEntity<byte[]> importAccountingExcel(@RequestPart MultipartFile excel, Integer month) throws IOException {
        try (InputStream is = excel.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            Workbook outputWorkbook = new XSSFWorkbook();
            Sheet outputSheet = outputWorkbook.createSheet("記帳紀錄");

            List<String> newColumnOrder = List.of("K", "A", "D", "E", "F", "J", "N");
            List<Integer> keepIndexes = getKeepIndexes(newColumnOrder, sheet);

            int outputRowIndex = 0;
            generateOutputHeader(sheet, outputSheet, keepIndexes, outputRowIndex++);

            filterAndOutputRows(sheet, outputSheet, keepIndexes, month, outputRowIndex);

            return generateResponse(outputWorkbook, month);
        }
    }

    private List<Integer> getKeepIndexes(List<String> newColumnOrder, Sheet sheet) {
        Iterator<Row> rows = sheet.iterator();
        Row headerRow = rows.next();

        List<Integer> keepIndexes = new ArrayList<>();
        for (String columnName : newColumnOrder) {
            for (Cell cell : headerRow) {
                String currentColumnName = CellReference.convertNumToColString(cell.getColumnIndex());
                if (currentColumnName.equals(columnName)) {
                    keepIndexes.add(cell.getColumnIndex());
                    break;
                }
            }
        }
        return keepIndexes;
    }

    private void generateOutputHeader(Sheet sheet, Sheet outputSheet, List<Integer> keepIndexes, int outputRowIndex) {
        Row headerRow = sheet.getRow(0);
        Row outputHeaderRow = outputSheet.createRow(outputRowIndex);
        int outputCellIndex = 0;
        for (Integer columnIndex : keepIndexes) {
            Cell headerCell = headerRow.getCell(columnIndex);
            Cell outputCell = outputHeaderRow.createCell(outputCellIndex++);
            outputCell.setCellValue(headerCell.getStringCellValue());
        }
    }

    private void filterAndOutputRows(Sheet sheet, Sheet outputSheet, List<Integer> keepIndexes, Integer month, int outputRowIndex) {
        Iterator<Row> rows = sheet.iterator();
        rows.next(); // skip the header row

        while (rows.hasNext()) {
            Row row = rows.next();

            if (shouldSkipRow(row, month)) {
                continue;
            }

            outputRow(row, outputSheet, keepIndexes, outputRowIndex++);
        }
    }

    private boolean shouldSkipRow(Row row, Integer month) {
        Cell recordTypeCell = row.getCell(2);
        if (recordTypeCell != null && recordTypeCell.getCellType() == CellType.STRING) {
            String recordType = recordTypeCell.getStringCellValue();
            if (!recordType.equals("支出") && !recordType.equals("記錄類型")) {
                return true;
            }
        }

        Cell dateCell = row.getCell(10);
        if (dateCell != null && dateCell.getCellType() == CellType.NUMERIC) {
            LocalDate dateValue = dateCell.getLocalDateTimeCellValue().toLocalDate();
//            int lastYear = LocalDate.now().getYear() - 1;
            return !(dateValue.getYear() == LocalDate.now().getYear() && dateValue.getMonth() == Month.of(month));
        }

        return true;
    }

    private void outputRow(Row row, Sheet outputSheet, List<Integer> keepIndexes, int outputRowIndex) {
        Row outputRow = outputSheet.createRow(outputRowIndex);
        int outputCellIndex = 0;
        for (Integer columnIndex : keepIndexes) {
            Cell cell = row.getCell(columnIndex);
            Cell outputCell = outputRow.createCell(outputCellIndex++);
            if (cell != null) {
                switch (cell.getCellType()) {
                    case STRING:
                        outputCell.setCellValue(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        outputCell.setCellValue(cell.getNumericCellValue());
                        break;
                    default:
                        outputCell.setCellValue("");
                }
            }
        }
    }

    private ResponseEntity<byte[]> generateResponse(Workbook outputWorkbook, Integer month) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (bos) {
            outputWorkbook.write(bos);
        }

        String encodedFileName = URLEncoder.encode(month + "月份帳本.xlsx", StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

        return new ResponseEntity<>(bos.toByteArray(), headers, HttpStatus.OK);
    }
}

