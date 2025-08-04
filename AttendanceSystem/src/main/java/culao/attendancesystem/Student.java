/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package culao.attendancesystem;
import java.io.Serializable;

/**
 *
 * @author nkrumah
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String indexNumber;
    
    public Student(String name, String indexNumber) {
        this.name = name;
        this.indexNumber = indexNumber;
    }
    
    // Getters and setters
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public String getIndexNumber() { 
        return indexNumber; 
    }
    
    public void setIndexNumber(String indexNumber) { 
        this.indexNumber = indexNumber; 
    }
    
    @Override
    public String toString() {
        return name + " (" + indexNumber + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return indexNumber.equals(student.indexNumber);
    }
    
    @Override
    public int hashCode() {
        return indexNumber.hashCode();
    }
}