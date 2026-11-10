
import java.util.ArrayList;
import java.io.*;

public class Library {

    private ArrayList<Book> books;
    private ArrayList<Student> students;
    private ArrayList<Transaction> transactions;

    private static final String BOOK_FILE = "books.dat";
    private static final String STUDENT_FILE = "students.dat";
    private static final String TRANSACTION_FILE = "transactions.dat";

    public Library() {
        books = loadData(BOOK_FILE);
        students = loadData(STUDENT_FILE);
        transactions = loadData(TRANSACTION_FILE);
    }

    @SuppressWarnings("unchecked")
    private <T> ArrayList<T> loadData(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input =
                new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<T>) input.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveObject(String fileName, Object data) {
        try (ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(fileName))) {
            output.writeObject(data);

        } catch (IOException e) {
            System.out.println("Error saving " + fileName + ": " + e.getMessage());
        }
    }

    private void saveData() {
        saveObject(BOOK_FILE, books);
        saveObject(STUDENT_FILE, students);
        saveObject(TRANSACTION_FILE, transactions);
    }

    // BOOK MANAGEMENT

    public void addBook(Book book) {
        if (searchBook(book.getBookId()) != null) {
            System.out.println("Book ID already exists!");
            return;
        }

        books.add(book);
        saveData();
        System.out.println("Book added successfully!");
    }

    public Book searchBook(String id) {
        for (Book book : books) {
            if (book.getBookId().equalsIgnoreCase(id)) {
                return book;
            }
        }
        return null;
    }

    public void displayAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        for (Book book : books) {
            book.displayBook();
        }
    }

    public void updateBook(String id, String name, String author) {
        Book book = searchBook(id);

        if (book == null) {
            System.out.println("Book not found!");
        } else {
            book.updateDetails(name, author);
            saveData();
        }
    }

    public void deleteBook(String id) {
        Book book = searchBook(id);

        if (book == null) {
            System.out.println("Book not found!");
        } else if (!book.isAvailable()) {
            System.out.println("Cannot delete an issued book!");
        } else {
            books.remove(book);
            saveData();
            System.out.println("Book deleted successfully!");
        }
    }

    // STUDENT MANAGEMENT

    public Student searchStudent(String id) {
        for (Student student : students) {
            if (student.getStudentId().equalsIgnoreCase(id)) {
                return student;
            }
        }
        return null;
    }

    public void registerStudent(Student student) {
        if (searchStudent(student.getStudentId()) != null) {
            System.out.println("Student ID already exists!");
            return;
        }

        students.add(student);
        saveData();
        System.out.println("Student registered successfully!");
    }

    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students registered.");
            return;
        }

        for (Student student : students) {
            student.displayStudent();
        }
    }

    private boolean hasIssuedBook(String studentId, String studentName) {
        for (Book book : books) {
            if (!book.isAvailable()) {
                boolean sameId = studentId.equalsIgnoreCase(
                    book.getIssuedToStudentId() == null
                        ? "" : book.getIssuedToStudentId()
                );

                boolean sameName = book.getIssuedToStudentId() == null
                    && studentName.equalsIgnoreCase(book.getIssuedTo());

                if (sameId || sameName) {
                    return true;
                }
            }
        }

        return false;
    }

    public void deleteStudent(String id) {
        Student student = searchStudent(id);

        if (student == null) {
            System.out.println("Student not found!");
        } else if (hasIssuedBook(id, student.getStudentName())) {
            System.out.println("Cannot delete student!");
            System.out.println("Return the issued book first.");
        } else {
            students.remove(student);
            saveData();
            System.out.println("Student deleted successfully!");
        }
    }

    // ISSUE BOOK

    public void issueBook(String bookId, String studentId) {
        Book book = searchBook(bookId);
        Student student = searchStudent(studentId);

        if (student == null) {
            System.out.println("Student is not registered!");
            return;
        }

        if (book == null) {
            System.out.println("Book not found!");
            return;
        }

        if (!book.isAvailable()) {
            System.out.println("Book is already issued!");
            return;
        }

        book.issueBook(student.getStudentId(), student.getStudentName());

        transactions.add(new Transaction(
            "ISSUE",
            book.getBookId(),
            book.getBookName(),
            student.getStudentId(),
            student.getStudentName(),
            0
        ));

        saveData();
    }

    // RETURN BOOK

    public void returnBook(String bookId) {
        Book book = searchBook(bookId);

        if (book == null) {
            System.out.println("Book not found!");
            return;
        }

        if (book.isAvailable()) {
            System.out.println("Book is already available!");
            return;
        }

        String studentId = book.getIssuedToStudentId();
        String studentName = book.getIssuedTo();
        long fine = book.calculateFine();

        book.returnBook();

        transactions.add(new Transaction(
            "RETURN",
            book.getBookId(),
            book.getBookName(),
            studentId == null ? "Unknown" : studentId,
            studentName,
            fine
        ));

        saveData();
    }

    // DUE DATE REMINDER

    public void showReminder(String bookId) {
        Book book = searchBook(bookId);

        if (book == null) {
            System.out.println("Book not found!");
        } else {
            book.showDueDateReminder();
        }
    }

    // STATISTICS

    public void displayStatistics() {
        int available = 0;
        int issued = 0;

        for (Book book : books) {
            if (book.isAvailable()) {
                available++;
            } else {
                issued++;
            }
        }

        System.out.println("\n========== LIBRARY STATISTICS ==========");
        System.out.println("Total Books: " + books.size());
        System.out.println("Available Books: " + available);
        System.out.println("Issued Books: " + issued);
        System.out.println("Registered Students: " + students.size());
        System.out.println("Total Transactions: " + transactions.size());
        System.out.println("========================================");
    }

    // TRANSACTION HISTORY

    public void displayTransactionHistory() {
        if (transactions.isEmpty()) {
            System.out.println("No transaction history available.");
            return;
        }

        System.out.println("\n========== TRANSACTION HISTORY ==========");

        for (Transaction transaction : transactions) {
            transaction.displayTransaction();
        }
    }

    // API ACCESS METHODS

    public ArrayList<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public ArrayList<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public ArrayList<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
}
