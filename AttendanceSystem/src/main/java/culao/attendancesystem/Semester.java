/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package culao.attendancesystem;
import java.util.*;
import java.io.Serializable;

/**
 *
 * @author nkrumah
 */
public class Semester implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String semesterName;
    private String semesterCode;
    private Map<String, Course> courses;
    
    public Semester(String semesterCode, String semesterName) {
        this.semesterCode = semesterCode;
        this.semesterName = semesterName;
        this.courses = new HashMap<>();
    }
    
    public void addCourse(Course course) {
        courses.put(course.getCourseCode(), course);
    }
    
    public Course getCourse(String courseCode) {
        return courses.get(courseCode);
    }
    
    public void removeCourse(String courseCode) {
        if (courses != null) {
            Course removed = courses.remove(courseCode);
            if (removed != null) {
                System.out.println("Removed course from semester: " + courseCode);
            } else {
                System.out.println("Course not found for removal in semester: " + courseCode);
            }
        }
    }
    
    public Collection<Course> getCourses() {
    // Simply return the values (Course objects) from the map
    if (courses != null) {
        return courses.values();
    }
    return new ArrayList<>(); // Return an empty collection if courses map is null
}
    
    // Getters
    public String getSemesterName() { return semesterName; }
    public String getSemesterCode() { return semesterCode; }
    public Map<String, Course> getCourseMap() { return courses; }
    
    @Override
    public String toString() {
        return semesterName + " (" + semesterCode + ")";
    }
}