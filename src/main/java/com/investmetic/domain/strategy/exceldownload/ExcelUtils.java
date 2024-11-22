package com.investmetic.domain.strategy.exceldownload;

import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class ExcelUtils implements ExcelSupport {

    private static final int MAX_ROW = 5000;
    private static final short DEFAULT_COLUMN_WIDTH = 300;
    private static final short DEFAULT_ROW_HEIGHT = 500;
    private static final short HEADER_FONT_COLOR = 255;
    private static final short HEADER_BACKGROUND_COLOR = 102;

    private SXSSFWorkbook workbook;
    private HttpServletResponse response;

    @Override
    public void connect(HttpServletResponse response) {
        this.workbook = new SXSSFWorkbook(-1); // 메모리 효율적 워크북
        this.response = response; // HTTP 응답 연결
    }

    @Override
    public void draw(Class<?> clazz, List<?> data) {
        try {
            List<?> modifiableData = new ArrayList<>(data); // 수정 가능한 리스트 생성
            createSheetWithData(clazz, modifiableData);
            modifiableData.clear();
        } catch (Exception e) {
            log.error("Excel Draw Error: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Excel 생성 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public void download(String fileName) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.EXCEL_DOWNLOAD_ERROR);
        } finally {
            closeWorkbook();
        }
    }

    private void createSheetWithData(Class<?> clazz, List<?> data) throws IllegalAccessException, IOException {
        String sheetName = findSheetName(clazz);
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_WIDTH);  // 기본 열 너비 설정
        sheet.setDefaultRowHeight(DEFAULT_ROW_HEIGHT);  // 기본 행 높이 설정

        List<String> headerNames = findHeaderNames(clazz);  // 헤더 추출
        createHeaders(sheet, headerNames);      // 헤더 작성
        createBody(sheet, clazz, data);         // 데이터 작성

        ((SXSSFSheet) sheet).flushRows(MAX_ROW); // 메모리 관리
    }

    private String findSheetName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ExcelSheet.class)) {
            return clazz.getAnnotation(ExcelSheet.class).name();
        }
        return "Sheet"; // 어노테이션이 없는 경우 기본 이름 사용
    }

    private void createHeaders(Sheet sheet, List<String> headerNames) {
        Row row = sheet.createRow(0);
        CellStyle headerCellStyle = createHeaderCellStyle();

        for (int i = 0; i < headerNames.size(); i++) {
            createCell(row, i, headerNames.get(i), headerCellStyle);
        }
    }

    private void createBody(Sheet sheet, Class<?> clazz, List<?> data) throws IllegalAccessException {
        int rowIndex = 1; // 데이터는 첫 번째 행 이후에 시작
        for (Object record : data) {
            Row row = sheet.createRow(rowIndex++);  // 새로운 행 생성
            List<Object> fieldValues = findFieldValues(clazz, record);

            for (int colIndex = 0; colIndex < fieldValues.size(); colIndex++) {
                createCell(row, colIndex, String.valueOf(fieldValues.get(colIndex)), null);
            }
        }
    }

    private CellStyle createHeaderCellStyle() {
        Font font = workbook.createFont();
        font.setColor(HEADER_FONT_COLOR);

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setFillForegroundColor(HEADER_BACKGROUND_COLOR);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);

        return style;
    }

    private void createCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (style != null) {
            cell.setCellStyle(style);
        }
        cell.setCellValue(value);
    }

    private List<String> findHeaderNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelColumn.class))
                .map(field -> field.getAnnotation(ExcelColumn.class).headerName())
                .collect(Collectors.toList());
    }

    private List<Object> findFieldValues(Class<?> clazz, Object obj) throws IllegalAccessException {
        List<Object> fieldValues = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelColumn.class)) {
                field.setAccessible(true);
                fieldValues.add(field.get(obj));
            }
        }
        return fieldValues;
    }

    private void closeWorkbook() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            log.error("Workbook Close Error: {}", e.getMessage(), e);
        }
    }
}