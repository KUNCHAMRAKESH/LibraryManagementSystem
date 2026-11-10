
import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transactionType;
    private String bookId;
    private String bookName;
    private String studentId;
    private String studentName;
    private LocalDate transactionDate;
    private long fine;

    public Transaction(String type, String bookId, String bookName,
                       String studentId, String studentName, long fine) {
        this.transactionType = type;
        this.bookId = bookId;
        this.bookName = bookName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.transactionDate = LocalDate.now();
        this.fine = fine;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public long getFine() {
        return fine;
    }

    public void displayTransaction() {
        System.out.println("------------------------------");
        System.out.println("Transaction: " + transactionType);
        System.out.println("Book ID: " + bookId);
        System.out.println("Book Name: " + bookName);
        System.out.println("Student ID: " + studentId);
        System.out.println("Student Name: " + studentName);
        System.out.println("Date: " + transactionDate);

        if (transactionType.equals("RETURN")) {
            System.out.println("Fine: Rs. " + fine);
        }

        System.out.println("------------------------------");
    }
}
