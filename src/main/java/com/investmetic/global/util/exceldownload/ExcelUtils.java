package com.investmetic.global.util.exceldownload;

import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class ExcelUtils implements ExcelSupport {

    private static final int MAX_ROW = 5000;
    private static final short DEFAULT_COLUMN_WIDTH = 300;
    private static final short DEFAULT_ROW_HEIGHT = 500;

    private SXSSFWorkbook workbook;
    private HttpServletResponse response;

    @Override
    public void connect(HttpServletResponse response) {
        this.workbook = new SXSSFWorkbook(-1); // 메모리 효율적 워크북
        this.response = response; // HTTP 응답 연결
    }


    /**
     * 엑셀 시트를 생성하고 데이터를 추가
     *
     * @param clazz 데이터 클래스 타입
     * @param data  시트에 추가할 DB 데이터 목록
     */
    @Override
    public void draw(Class<?> clazz, List<?> data) {
        try {
            List<?> modifiableData = new ArrayList<>(data); // 수정 가능한 리스트 생성
            createSheetWithData(clazz, modifiableData);
            modifiableData.clear();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EXCEL_CREATE_ERROR);
        }
    }


    /**
     * 엑셀파일 다운로드
     *
     * @param fileName 다운로드될 엑셀 파일 이름 (확장자는 자동 추가)
     */
    @Override
    public void download(String fileName) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        // 파일 이름을 헤더에 설정 (attachment로 다운로드)
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

    /**
     * 클래스와 데이터 목록을 기반으로 엑셀 시트를 생성
     *
     * @param clazz 데이터 클래스 타입 (헤더 및 데이터 추출에 사용)
     * @param data  시트에 추가할 데이터 목록
     */
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

    /**
     * 엑셀 시트에 데이터를 생성
     *
     * @param sheet 엑셀 데이터가 추가될 시트
     * @param clazz 데이터의 클래스 타입 (필드 정보를 읽기 위해 사용)
     * @param data  시트에 추가할 데이터 목록
     */
    private void createBody(Sheet sheet, Class<?> clazz, List<?> data) throws IllegalAccessException {
        int rowIndex = 1; // 데이터는 첫 번째 행 이후에 시작 ( 0번째 행은 헤더임)
        for (Object record : data) {
            Row row = sheet.createRow(rowIndex++);  // 새로운 행 생성
            List<Object> fieldValues = findFieldValues(clazz, record); // 값 추출

            for (int colIndex = 0; colIndex < fieldValues.size(); colIndex++) {
                createCell(row, colIndex, String.valueOf(fieldValues.get(colIndex)), null);
            }
        }
    }

    /**
     * 헤더 셀에 적용할 스타일을 생성
     *
     * @return 헤더 셀 스타일 (글꼴, 정렬, 테두리, 배경색 포함)
     */
    private CellStyle createHeaderCellStyle() {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12); // 글꼴 크기 설정
        font.setFontName("Arial");
        font.setBold(true); // 굵게 표시
        font.setColor(IndexedColors.WHITE.getIndex()); // 텍스트 색상: 흰색

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 가로 정렬: 가운데
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 세로 정렬: 가운데

        // 테두리 설정
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);

        // 배경 색상 설정
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); // 배경 색상 : 연파
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 단색 배경 설정

        style.setFont(font); // 글꼴 스타일 적용

        return style;
    }

    /**
     * 주어진 행(Row)에 새로운 셀을 생성하고 값을 설정
     *
     * @param row      셀이 추가될 행
     * @param colIndex 셀의 열 인덱스 (0부터 시작)
     * @param value    셀에 입력될 값
     * @param style    적용할 셀 스타일 (null일 경우 스타일 적용 안 함)
     */
    private void createCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex); // // 지정된 열 인덱스에 셀 생성
        if (style != null) {
            cell.setCellStyle(style); // 스타일 있으면 셀적용 (헤더)
        }
        cell.setCellValue(value); // 셀에 값설정
    }

    /***
     * 클래스의 필드에서 @ExcelColumn 어노테이션이 달린 필드를 찾아 헤더 이름 목록을 반환
     *
     * @param clazz 헤더 이름을 추출할 클래스(dto)
     * @return 헤더 이름 목록 (어노테이션의 headerName 값)
     */
    private List<String> findHeaderNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelColumn.class))
                .map(field -> field.getAnnotation(ExcelColumn.class).headerName())
                .collect(Collectors.toList());
    }

    /**
     * 객체의 값 중 @ExcelColumn 어노테이션이 달린 필드의 값 추출
     *
     * @param clazz 값 추출 대상 클래스(dto)
     * @param obj   값이 포함된 객체
     */
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
            throw new BusinessException(ErrorCode.EXCEL_DOWNLOAD_ERROR);
        }
    }
}