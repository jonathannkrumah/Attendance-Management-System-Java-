/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package culao.attendancesystem;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author nkrumah
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String courseCode;
    private String courseName;
    private int level;
    private List<Student> students;
    private Map<String, Map<Integer, Boolean>> attendance;
    
    public Course(String courseCode, String courseName, int level) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.level = level;
        this.students = new ArrayList<>();
        this.attendance = new HashMap<>(); // Initialize attendance map
    }
    
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            if (!attendance.containsKey(student.getIndexNumber())) {
                attendance.put(student.getIndexNumber(), new HashMap<>());
            }
        }
    }
    
    public void removeStudent(String indexNumber) {
        students.removeIf(student -> student.getIndexNumber().equals(indexNumber));
        attendance.remove(indexNumber);
    }
    
    public void markAttendance(String indexNumber, int week, boolean present) {
        // Ensure student exists in attendance map
        if (!attendance.containsKey(indexNumber)) {
            attendance.put(indexNumber, new HashMap<>());
            System.out.println("Created new attendance record for student: " + indexNumber);
        }
        
        // Mark the attendance
        attendance.get(indexNumber).put(week, present);
        System.out.println("MARKED ATTENDANCE: " + indexNumber + " Week " + week + " = " + present);
        
        // Verify it was set
        Boolean check = attendance.get(indexNumber).get(week);
        System.out.println("VERIFICATION: " + indexNumber + " Week " + week + " = " + check);
    }
    
    public Boolean getAttendance(String indexNumber, int week) {
        if (attendance.containsKey(indexNumber)) {
            return attendance.get(indexNumber).get(week);
        }
        return null;
    }
    
    // Getters
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getLevel() { return level; }
    public List<Student> getStudents() { return students; }
    public Map<String, Map<Integer, Boolean>> getAttendance() { return attendance; }
    
    public void setLevel(int level) { this.level = level; }
    
    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (Level " + level + ")";
    }
}