package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse; 
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import com.shopme.common.entity.User;

public class UserCsvExporter extends AbstractExporter {
    
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        // 1. KANI ANG SA-UP, BAI! Usa na lang ka linya ang mopuli sa imong karaan nga Step 1 ug 2.
        super.setResponseHeader(response, "text/csv", ".csv");
        
        // 2. Padayon na sa imong CsvBeanWriter logic
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), 
                CsvPreference.STANDARD_PREFERENCE);
        
        // 3. I-define ang Column Headers
        String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        
        // 4. I-define ang Field Mapping
        String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
        
        csvWriter.writeHeader(csvHeader);
        
        // 5. Isuwat na ang data sa matag user
        for (User user : listUsers) {
            csvWriter.write(user, fieldMapping);
        }
        
        csvWriter.close();
    }
}