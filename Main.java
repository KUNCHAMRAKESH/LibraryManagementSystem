
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Library library = new Library();

        int choice = 0;

        do {
            System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========");
            System.out.println("1. Add Book");
            System.out.println("2. Display All Books");
            System.out.println("3. Search Book");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. Due Date Reminder");
            System.out.println("7. Exit");
            System.out.println("8. Register Student");
            System.out.println("9. Display All Students");
            System.out.println("10. Search Student");
            System.out.println("11. Delete Student");
            System.out.println("12. Delete Book");
            System.out.println("13. Update Book");
            System.out.println("14. Library Statistics");
            System.out.println("15. Transaction History");
            System.out.println("===============================================");

            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
                choice = 0;
                continue;
            }

            switch (choice) {

                case 1:
                    System.out.print("Enter Book ID: ");
                    String bookId = sc.nextLine().trim();

                    System.out.print("Enter Book Name: ");
                    String bookName = sc.nextLine().trim();

                    System.out.print("Enter Author Name: ");
                    String author = sc.nextLine().trim();

                    if (bookId.isEmpty() || bookName.isEmpty() || author.isEmpty()) {
                        System.out.println("All book details are required!");
                    } else {
                        library.addBook(new Book(bookId, bookName, author));
                    }
                    break;

                case 2:
                    library.displayAllBooks();
                    break;

                case 3:
                    System.out.print("Enter Book ID: ");
                    bookId = sc.nextLine().trim();

                    Book foundBook = library.searchBook(bookId);

                    if (foundBook != null) {
                        foundBook.displayBook();
                    } else {
                        System.out.println("Book not found!");
                    }
                    break;

                case 4:
                    System.out.print("Enter Student ID: ");
                    String studentId = sc.nextLine().trim();

                    System.out.print("Enter Book ID: ");
                    bookId = sc.nextLine().trim();

                    library.issueBook(bookId, studentId);
                    break;

                case 5:
                    System.out.print("Enter Book ID: ");
                    bookId = sc.nextLine().trim();
                    library.returnBook(bookId);
                    break;

                case 6:
                    System.out.print("Enter Book ID: ");
                    bookId = sc.nextLine().trim();
                    library.showReminder(bookId);
                    break;

                case 7:
                    System.out.println("Thank you for using Library Management System!");
                    break;

                case 8:
                    System.out.print("Enter Student ID: ");
                    studentId = sc.nextLine().trim();

                    System.out.print("Enter Student Name: ");
                    String studentName = sc.nextLine().trim();

                    System.out.print("Enter Email: ");
                    String email = sc.nextLine().trim();

                    if (studentId.isEmpty() || studentName.isEmpty() || email.isEmpty()) {
                        System.out.println("All student details are required!");
                    } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                        System.out.println("Please enter a valid email!");
                    } else {
                        library.registerStudent(
                            new Student(studentId, studentName, email)
                        );
                    }
                    break;

                case 9:
                    library.displayAllStudents();
                    break;

                case 10:
                    System.out.print("Enter Student ID: ");
                    studentId = sc.nextLine().trim();

                    Student foundStudent = library.searchStudent(studentId);

                    if (foundStudent != null) {
                        foundStudent.displayStudent();
                    } else {
                        System.out.println("Student not found!");
                    }
                    break;

                case 11:
                    System.out.print("Enter Student ID to delete: ");
                    studentId = sc.nextLine().trim();
                    library.deleteStudent(studentId);
                    break;

                case 12:
                    System.out.print("Enter Book ID to delete: ");
                    bookId = sc.nextLine().trim();
                    library.deleteBook(bookId);
                    break;

                case 13:
                    System.out.print("Enter Book ID to update: ");
                    bookId = sc.nextLine().trim();

                    System.out.print("Enter New Book Name: ");
                    bookName = sc.nextLine().trim();

                    System.out.print("Enter New Author Name: ");
                    author = sc.nextLine().trim();

                    if (bookId.isEmpty() || bookName.isEmpty() || author.isEmpty()) {
                        System.out.println("All details are required!");
                    } else {
                        library.updateBook(bookId, bookName, author);
                    }
                    break;

                case 14:
                    library.displayStatistics();
                    break;

                case 15:
                    library.displayTransactionHistory();
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
            }

        } while (choice != 7);

        sc.close();
    }
}
