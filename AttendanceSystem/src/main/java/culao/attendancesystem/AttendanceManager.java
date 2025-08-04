/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package culao.attendancesystem;
import java.util.*;
import java.io.*;

/**
 *
 * @author nkrumah
 */
public class AttendanceManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Semester> semesters;
    private static final String DATA_FILE = "attendance_data.ser";
    
    public AttendanceManager() {
        semesters = new HashMap<>();
        loadFromFile();
    }
    
    public void addSemester(Semester semester) {
        semesters.put(semester.getSemesterCode(), semester);
    }
    
    public Semester getSemester(String semesterCode) {
        return semesters.get(semesterCode);
    }
    
    public Collection<Semester> getAllSemesters() {
        return semesters.values();
    }
    
    public void saveToFile() {
        try {
            // Ensure directory exists
            File dataFile = new File(DATA_FILE);
            if (dataFile.getParentFile() != null) {
                dataFile.getParentFile().mkdirs();
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                oos.writeObject(semesters);
                System.out.println("SUCCESS: Data saved to " + DATA_FILE);
                System.out.println("Saved " + semesters.size() + " semesters");
                
                // Debug: Print what we're saving
                for (Map.Entry<String, Semester> entry : semesters.entrySet()) {
                    Semester sem = entry.getValue();
                    System.out.println("  Semester: " + sem.getSemesterCode() + " - " + sem.getSemesterName());
                    System.out.println("  Courses: " + sem.getCourseMap().size());
                    for (Course course : sem.getCourses()) {
                        System.out.println("    Course: " + course.getCourseCode() + " - " + course.getCourseName());
                        System.out.println("    Students: " + course.getStudents().size());
                        System.out.println("    Attendance records: " + course.getAttendance().size());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                semesters = (Map<String, Semester>) ois.readObject();
                System.out.println("SUCCESS: Data loaded from " + DATA_FILE);
                System.out.println("Loaded " + semesters.size() + " semesters");
                
                // Debug: Print what we loaded
                for (Map.Entry<String, Semester> entry : semesters.entrySet()) {
                    Semester sem = entry.getValue();
                    System.out.println("  Semester: " + sem.getSemesterCode() + " - " + sem.getSemesterName());
                    System.out.println("  Courses: " + sem.getCourseMap().size());
                }
            } catch (Exception e) {
                System.err.println("ERROR loading data file: " + e.getMessage());
                e.printStackTrace();
                semesters = new HashMap<>();
            }
        } else {
            System.out.println("INFO: No existing data file found, starting fresh");
            semesters = new HashMap<>();
        }
    }
}