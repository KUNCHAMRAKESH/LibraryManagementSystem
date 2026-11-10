
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiServer {

    private static final Library library = new Library();

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        server.createContext("/api/", ApiServer::handleRequest);

        server.setExecutor(null);
        server.start();

        System.out.println("================================");
        System.out.println("Library API Server Started!");
        System.out.println("Website API: http://localhost:8080/api");
        System.out.println("================================");
    }

    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        try {
            if (method.equals("GET") && path.equals("/api/books")) {
                sendJson(exchange, 200, booksJson());
            }

            else if (method.equals("GET") && path.equals("/api/students")) {
                sendJson(exchange, 200, studentsJson());
            }

            else if (method.equals("GET") && path.equals("/api/transactions")) {
                sendJson(exchange, 200, transactionsJson());
            }

            else if (method.equals("GET") && path.equals("/api/statistics")) {
                sendJson(exchange, 200, statisticsJson());
            }

            else if (method.equals("POST") && path.equals("/api/books")) {
                addBook(exchange);
            }

            else if (method.equals("POST") && path.equals("/api/students")) {
                addStudent(exchange);
            }

            else if (method.equals("POST") && path.equals("/api/issue")) {
                issueBook(exchange);
            }

            else if (method.equals("POST") && path.equals("/api/return")) {
                returnBook(exchange);
            }

            else {
                sendMessage(exchange, 404, "API endpoint not found");
            }

        } catch (Exception e) {
            sendMessage(exchange, 500, "Server error: " + e.getMessage());
        }
    }

    private static void addBook(HttpExchange exchange) throws IOException {

        String body = readBody(exchange);

        String id = getValue(body, "bookId");
        String name = getValue(body, "bookName");
        String author = getValue(body, "author");

        if (id.isBlank() || name.isBlank() || author.isBlank()) {
            sendMessage(exchange, 400, "Please enter all book details");
            return;
        }

        if (library.searchBook(id) != null) {
            sendMessage(exchange, 409, "Book ID already exists");
            return;
        }

        library.addBook(new Book(id, name, author));
        sendMessage(exchange, 201, "Book added successfully");
    }

    private static void addStudent(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        String id = getValue(body, "studentId");
        String name = getValue(body, "studentName");
        String email = getValue(body, "email");

        if (id.isBlank() || name.isBlank() || email.isBlank()) {
            sendMessage(exchange, 400, "Please enter all student details");
            return;
        }

        if (library.searchStudent(id) != null) {
            sendMessage(exchange, 409, "Student ID already exists");
            return;
        }

        library.registerStudent(new Student(id, name, email));
        sendMessage(exchange, 201, "Student registered successfully");
    }

    private static void issueBook(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        String studentId = getValue(body, "studentId");
        String bookId = getValue(body, "bookId");

        Student student = library.searchStudent(studentId);
        Book book = library.searchBook(bookId);

        if (student == null) {
            sendMessage(exchange, 404, "Student is not registered");
            return;
        }

        if (book == null) {
            sendMessage(exchange, 404, "Book not found");
            return;
        }

        if (!book.isAvailable()) {
            sendMessage(exchange, 409, "Book is already issued");
            return;
        }

        library.issueBook(bookId, studentId);
        sendMessage(exchange, 200, "Book issued successfully");
    }

    private static void returnBook(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);
        String bookId = getValue(body, "bookId");

        Book book = library.searchBook(bookId);

        if (book == null) {
            sendMessage(exchange, 404, "Book not found");
            return;
        }

        if (book.isAvailable()) {
            sendMessage(exchange, 409, "Book is already available");
            return;
        }

        long fine = book.calculateFine();

        library.returnBook(bookId);

        sendJson(exchange, 200,
            "{\"message\":\"Book returned successfully\","
            + "\"fine\":" + fine + "}");
    }

    private static String booksJson() {

        StringBuilder json = new StringBuilder("[");
        ArrayList<Book> books = library.getBooks();

        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                .append("\"bookId\":").append(quote(b.getBookId())).append(",")
                .append("\"bookName\":").append(quote(b.getBookName())).append(",")
                .append("\"author\":").append(quote(b.getAuthor())).append(",")
                .append("\"available\":").append(b.isAvailable()).append(",")
                .append("\"issuedTo\":").append(quote(b.getIssuedTo())).append(",")
                .append("\"dueDate\":").append(quote(
                    b.getDueDate() == null ? "" : b.getDueDate().toString()
                ))
                .append("}");
        }

        return json.append("]").toString();
    }

    private static String studentsJson() {

        StringBuilder json = new StringBuilder("[");
        ArrayList<Student> students = library.getStudents();

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                .append("\"studentId\":").append(quote(s.getStudentId())).append(",")
                .append("\"studentName\":").append(quote(s.getStudentName())).append(",")
                .append("\"email\":").append(quote(s.getEmail()))
                .append("}");
        }

        return json.append("]").toString();
    }

    private static String transactionsJson() {

        StringBuilder json = new StringBuilder("[");
        ArrayList<Transaction> transactions = library.getTransactions();

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                .append("\"transactionType\":").append(quote(t.getTransactionType())).append(",")
                .append("\"bookId\":").append(quote(t.getBookId())).append(",")
                .append("\"bookName\":").append(quote(t.getBookName())).append(",")
                .append("\"studentId\":").append(quote(t.getStudentId())).append(",")
                .append("\"studentName\":").append(quote(t.getStudentName())).append(",")
                .append("\"transactionDate\":").append(quote(
                    t.getTransactionDate() == null
                        ? "" : t.getTransactionDate().toString()
                )).append(",")
                .append("\"fine\":").append(t.getFine())
                .append("}");
        }

        return json.append("]").toString();
    }

    private static String statisticsJson() {

        int available = 0;
        int issued = 0;

        for (Book book : library.getBooks()) {
            if (book.isAvailable()) {
                available++;
            } else {
                issued++;
            }
        }

        return "{"
            + "\"totalBooks\":" + library.getBooks().size() + ","
            + "\"availableBooks\":" + available + ","
            + "\"issuedBooks\":" + issued + ","
            + "\"totalStudents\":" + library.getStudents().size() + ","
            + "\"totalTransactions\":" + library.getTransactions().size()
            + "}";
    }

    private static String readBody(HttpExchange exchange)
            throws IOException {

        try (InputStream input = exchange.getRequestBody()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String getValue(String json, String key) {

        Pattern pattern = Pattern.compile(
            "\"" + Pattern.quote(key)
            + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""
        );

        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return unescape(matcher.group(1));
        }

        return "";
    }

    private static String unescape(String value) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);

                switch (next) {
                    case '"': result.append('"'); break;
                    case '\\': result.append('\\'); break;
                    case '/': result.append('/'); break;
                    case 'n': result.append('\n'); break;
                    case 'r': result.append('\r'); break;
                    case 't': result.append('\t'); break;
                    case 'b': result.append('\b'); break;
                    case 'f': result.append('\f'); break;
                    default: result.append(next);
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private static String quote(String value) {

        if (value == null) value = "";

        return "\""
            + value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t")
            + "\"";
    }

    private static void addCorsHeaders(HttpExchange exchange) {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods", "GET, POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers", "Content-Type"
        );
    }

    private static void sendMessage(
            HttpExchange exchange, int status, String message)
            throws IOException {

        sendJson(exchange, status,
            "{\"message\":" + quote(message) + "}");
    }

    private static void sendJson(
            HttpExchange exchange, int status, String json)
            throws IOException {

        byte[] response = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
