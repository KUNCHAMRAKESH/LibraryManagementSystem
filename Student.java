
import java.io.Serializable;

public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private String studentId;
    private String studentName;
    private String email;

    public Student(String studentId, String studentName, String email) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getEmail() {
        return email;
    }

    public void displayStudent() {
        System.out.println("--------------------------");
        System.out.println("Student ID: " + studentId);
        System.out.println("Student Name: " + studentName);
        System.out.println("Email: " + email);
        System.out.println("--------------------------");
    }
}
