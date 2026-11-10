
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bookId;
    private String bookName;
    private String author;
    private boolean available;
    private String issuedTo;
    private String issuedToStudentId;
    private LocalDate issueDate;
    private LocalDate dueDate;

    public Book(String bookId, String bookName, String author) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.available = true;
        this.issuedTo = "";
        this.issuedToStudentId = "";
    }

    public String getBookId() { return bookId; }
    public String getBookName() { return bookName; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
    public String getIssuedTo() { return issuedTo; }
    public String getIssuedToStudentId() { return issuedToStudentId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }

    public void updateDetails(String name, String author) {
        this.bookName = name;
        this.author = author;
        System.out.println("Book details updated successfully!");
    }

    public void issueBook(String studentId, String studentName) {
        if (!available) {
            System.out.println("Book is already issued!");
            return;
        }

        available = false;
        issuedTo = studentName;
        issuedToStudentId = studentId;
        issueDate = LocalDate.now();
        dueDate = issueDate.plusDays(14);

        System.out.println("Book issued successfully!");
        System.out.println("Student ID: " + studentId);
        System.out.println("Issued To: " + studentName);
        System.out.println("Issue Date: " + issueDate);
        System.out.println("Due Date: " + dueDate);
    }

    public long calculateFine() {
        if (dueDate == null) return 0;

        LocalDate today = LocalDate.now();

        if (today.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, today) * 5;
        }

        return 0;
    }

    public void showDueDateReminder() {
        if (available || dueDate == null) {
            System.out.println("This book is not currently issued.");
            return;
        }

        LocalDate today = LocalDate.now();

        System.out.println("Book Name: " + bookName);
        System.out.println("Student ID: " + issuedToStudentId);
        System.out.println("Issued To: " + issuedTo);
        System.out.println("Due Date: " + dueDate);

        if (today.isAfter(dueDate)) {
            long lateDays = ChronoUnit.DAYS.between(dueDate, today);
            System.out.println("Book is overdue!");
            System.out.println("Late Days: " + lateDays);
            System.out.println("Fine: Rs. " + calculateFine());
        } else if (today.isEqual(dueDate)) {
            System.out.println("Return the book today!");
        } else {
            long remainingDays = ChronoUnit.DAYS.between(today, dueDate);
            System.out.println("Remaining Days: " + remainingDays);
        }
    }

    public void returnBook() {
        if (available) {
            System.out.println("Book is already available!");
            return;
        }

        long fine = calculateFine();

        available = true;
        issuedTo = "";
        issuedToStudentId = "";
        issueDate = null;
        dueDate = null;

        System.out.println("Book returned successfully!");
        System.out.println("Fine Amount: Rs. " + fine);
    }

    public void displayBook() {
        System.out.println("--------------------------");
        System.out.println("Book ID: " + bookId);
        System.out.println("Book Name: " + bookName);
        System.out.println("Author: " + author);
        System.out.println("Status: " + (available ? "Available" : "Issued"));

        if (!available) {
            System.out.println("Student ID: " + issuedToStudentId);
            System.out.println("Issued To: " + issuedTo);
            System.out.println("Due Date: " + dueDate);
        }

        System.out.println("--------------------------");
    }
}
