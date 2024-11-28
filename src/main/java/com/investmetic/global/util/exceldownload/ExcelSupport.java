package com.investmetic.global.util.exceldownload;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface ExcelSupport {
    void connect(HttpServletResponse response);
    void draw(Class<?> clazz, List<?> data);
    void download(String filename);
}
