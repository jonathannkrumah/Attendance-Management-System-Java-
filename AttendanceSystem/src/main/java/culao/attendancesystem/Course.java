/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package culao.attendancesystem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
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
    private Set<Integer> lockedWeeks = new HashSet<>();

    public Course(String courseCode, String courseName, int level) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.level = level;
        this.students = new ArrayList<>();
        this.attendance = new HashMap<>();
    }
    
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            // Ensure an entry exists for the student in the attendance map
            attendance.putIfAbsent(student.getIndexNumber(), new HashMap<>());
        }
    }
    
    public void removeStudent(String indexNumber) {
        students.removeIf(student -> student.getIndexNumber().equals(indexNumber));
        attendance.remove(indexNumber);
    }
    
    public void markAttendance(String indexNumber, int week, boolean present) {
        // Ensure student exists in attendance map
        attendance.putIfAbsent(indexNumber, new HashMap<>());
        
        // Mark the attendance for the specific student and week
        attendance.get(indexNumber).put(week, present);
        System.out.println("MARKED ATTENDANCE: " + indexNumber + " Week " + week + " = " + present);
    }
    
    public Boolean getAttendance(String indexNumber, int week) {
        // Check if student exists and if they have an entry for the week
        if (attendance.containsKey(indexNumber)) {
            return attendance.get(indexNumber).get(week);
        }
        return null; // Student not found or no record for that week
    }
    
    public void lockWeek(int week) {
    if (week >= 1 && week <= 12) {
        lockedWeeks.add(week);
        System.out.println("LOCKED Week " + week + " for course " + this.getCourseCode());
        }
    }
    
     public boolean isWeekLocked(int week) {
        boolean locked = lockedWeeks.contains(week);
        System.out.println("Checking if Week " + week + " is locked for " + this.getCourseCode() + ": " + locked); // Debug print
        return locked;
    }
    
    public void unlockWeek(int week) {
     if (week >= 1 && week <= 12) {
        lockedWeeks.remove(week);
        System.out.println("UNLOCKED Week " + week + " for course " + this.getCourseCode());
        }
    }
    
    void debugLoadedState() {
    System.out.println("=== Course " + this.getCourseCode() + " Internal State After Load ===");
    System.out.println("Students List Size: " + (this.students != null ? this.students.size() : "NULL"));
    if (this.students != null) {
        for (Student s : this.students) {
            System.out.println("  Student in list: " + s.getIndexNumber() + " (" + s.getName() + ")");
        }
    }
    System.out.println("Attendance Map Size: " + (this.attendance != null ? this.attendance.size() : "NULL"));
    if (this.attendance != null) {
        for (Map.Entry<String, Map<Integer, Boolean>> entry : this.attendance.entrySet()) {
            System.out.println("  Attendance entry for Index: " + entry.getKey() + " -> Data Map: " + entry.getValue() + " (Size: " + (entry.getValue() != null ? entry.getValue().size() : "NULL") + ")");
        }
    }
    System.out.println("Locked Weeks Set: " + (this.lockedWeeks != null ? this.lockedWeeks : "NULL"));
    System.out.println("===================================================================");
}
    
    public Set<Integer> getLockedWeeks() {
    // Return a copy to prevent external modification of the internal set
    return new HashSet<>(lockedWeeks);
    }
    
    // Getters
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getLevel() { return level; }
    public List<Student> getStudents() { return students; }
    
    // Consider returning a copy or specific data if needed elsewhere.
    public Map<String, Map<Integer, Boolean>> getAttendance() { return attendance; } 
    
    public void setLevel(int level) { this.level = level; }
    
    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (Level " + level + ")";
    }
}
    