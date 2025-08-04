/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package culao.attendancesystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nkrumah
 */
public class ExcelImporter {
     public static List<Student> importStudents(String filePath) throws Exception {
        List<Student> students = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                
                Cell nameCell = row.getCell(0);
                Cell indexCell = row.getCell(1);
                
                if (nameCell != null && indexCell != null) {
                    String name = nameCell.getStringCellValue();
                    String indexNumber = indexCell.getStringCellValue();
                    
                    if (!name.isEmpty() && !indexNumber.isEmpty()) {
                        students.add(new Student(name, indexNumber));
                    }
                }
            }
        }
        
        return students;
    }
    
}
