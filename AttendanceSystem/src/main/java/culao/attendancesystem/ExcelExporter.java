/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package culao.attendancesystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author nkrumah
 */
public class ExcelExporter {
    
    public static void exportAttendanceToExcel(Course course, String filename) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance - " + course.getCourseCode());
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Index Number");
        
        // Add week columns
        for (int week = 1; week <= 12; week++) {
            headerRow.createCell(week + 1).setCellValue("Week " + week);
        }
        
        // Add student data
        int rowNum = 1;
        for (Student student : course.getStudents()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getName());
            row.createCell(1).setCellValue(student.getIndexNumber());
            
            // Add attendance data - FIXED: Get actual attendance data
            for (int week = 1; week <= 12; week++) {
                Boolean present = course.getAttendance(student.getIndexNumber(), week);
                String status = present == null ? "" : (present ? "P" : "A");
                row.createCell(week + 1).setCellValue(status);
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < 14; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filename)) {
            workbook.write(fileOut);
        }
        
        workbook.close();
    }
}