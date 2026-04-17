package com.shopme.admin.user;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.servlet.http.HttpServletResponse;

public class AbstractExporter {

    public void setResponseHeader(HttpServletResponse response, String contentType, String extension) throws IOException {
        // 1. Paghimo sa Timestamp (yyyy-MM-dd_HH-mm-ss)
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormatter.format(new Date());
        
        // 2. Pag-combine sa filename (pananglitan: users_2026-03-28_22-45-00.xlsx)
        String fileName = "users_" + timestamp + extension;

        // 3. I-set ang Content-Type (text/csv o application/octet-stream)
        response.setContentType(contentType);

        // 4. I-set ang Content-Disposition para mo-trigger ang download sa browser
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
    }
}
