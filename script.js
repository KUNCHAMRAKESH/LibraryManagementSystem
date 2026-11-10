
const API = "http://localhost:8080/api";

async function request(endpoint, options = {}) {
    const response = await fetch(`${API}/${endpoint}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options.headers
        }
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Request failed");
    }

    return data;
}



function showSection(sectionId) {
    document.querySelectorAll(".section").forEach(section => {
        section.classList.remove("active");
    });

    document.getElementById(sectionId).classList.add("active");

    if (sectionId === "dashboard") loadDashboard();
    if (sectionId === "books") loadBooks();
    if (sectionId === "students") loadStudents();
    if (sectionId === "history") loadTransactions();
}



async function loadDashboard() {
    try {
        const data = await request("statistics");

        document.getElementById("totalBooks").textContent =
            data.totalBooks;

        document.getElementById("totalStudents").textContent =
            data.totalStudents;

        document.getElementById("issuedBooks").textContent =
            data.issuedBooks;

    } catch (error) {
        console.error("Dashboard error:", error.message);
    }
}



async function loadBooks() {
    try {
        const books = await request("books");

        const rows = books.map(book => `
            <tr>
                <td>${escapeHTML(book.bookId)}</td>
                <td>${escapeHTML(book.bookName)}</td>
                <td>${escapeHTML(book.author)}</td>
                <td>${book.available ? "Available" : "Issued"}</td>
                <td>${escapeHTML(book.dueDate || "-")}</td>
            </tr>
        `).join("");

        document.getElementById("bookList").innerHTML = `
            <table>
                <tr>
                    <th>Book ID</th>
                    <th>Book Name</th>
                    <th>Author</th>
                    <th>Status</th>
                    <th>Due Date</th>
                </tr>
                ${rows || '<tr><td colspan="5">No books found</td></tr>'}
            </table>
        `;

    } catch (error) {
        showError(error);
    }
}



document.getElementById("bookForm").addEventListener("submit", async event => {
    event.preventDefault();

    try {
        await request("books", {
            method: "POST",
            body: JSON.stringify({
                bookId: document.getElementById("bookId").value.trim(),
                bookName: document.getElementById("bookName").value.trim(),
                author: document.getElementById("author").value.trim()
            })
        });

        alert("Book added successfully!");

        event.target.reset();
        loadBooks();
        loadDashboard();

    } catch (error) {
        showError(error);
    }
});



async function loadStudents() {
    try {
        const students = await request("students");

        const rows = students.map(student => `
            <tr>
                <td>${escapeHTML(student.studentId)}</td>
                <td>${escapeHTML(student.studentName)}</td>
                <td>${escapeHTML(student.email)}</td>
            </tr>
        `).join("");

        document.getElementById("studentList").innerHTML = `
            <table>
                <tr>
                    <th>Student ID</th>
                    <th>Student Name</th>
                    <th>Email</th>
                </tr>
                ${rows || '<tr><td colspan="3">No students found</td></tr>'}
            </table>
        `;

    } catch (error) {
        showError(error);
    }
}


document.getElementById("studentForm").addEventListener("submit", async event => {
    event.preventDefault();

    try {
        await request("students", {
            method: "POST",
            body: JSON.stringify({
                studentId: document.getElementById("studentId").value.trim(),
                studentName: document.getElementById("studentName").value.trim(),
                email: document.getElementById("email").value.trim()
            })
        });

        alert("Student registered successfully!");

        event.target.reset();
        loadStudents();
        loadDashboard();

    } catch (error) {
        showError(error);
    }
});



document.getElementById("issueForm").addEventListener("submit", async event => {
    event.preventDefault();

    try {
        const data = await request("issue", {
            method: "POST",
            body: JSON.stringify({
                studentId: document.getElementById("issueStudentId").value.trim(),
                bookId: document.getElementById("issueBookId").value.trim()
            })
        });

        document.getElementById("issueMessage").textContent =
            data.message || "Book issued successfully!";

        event.target.reset();

        loadBooks();
        loadDashboard();

    } catch (error) {
        document.getElementById("issueMessage").textContent = error.message;
    }
});



document.getElementById("returnForm").addEventListener("submit", async event => {
    event.preventDefault();

    try {
        const data = await request("return", {
            method: "POST",
            body: JSON.stringify({
                bookId: document.getElementById("returnBookId").value.trim()
            })
        });

        document.getElementById("returnMessage").textContent =
            `${data.message}. Fine: ₹${data.fine}`;

        event.target.reset();

        loadBooks();
        loadDashboard();
        loadTransactions();

    } catch (error) {
        document.getElementById("returnMessage").textContent = error.message;
    }
});


async function loadTransactions() {
    try {
        const transactions = await request("transactions");

        const rows = transactions.map(item => `
            <tr>
                <td>${escapeHTML(item.transactionType)}</td>
                <td>${escapeHTML(item.bookId)}</td>
                <td>${escapeHTML(item.studentId)}</td>
                <td>${escapeHTML(item.studentName)}</td>
                <td>${escapeHTML(item.transactionDate)}</td>
                <td>₹${item.fine}</td>
            </tr>
        `).join("");

        document.getElementById("transactionList").innerHTML = `
            <table>
                <tr>
                    <th>Type</th>
                    <th>Book ID</th>
                    <th>Student ID</th>
                    <th>Student Name</th>
                    <th>Date</th>
                    <th>Fine</th>
                </tr>
                ${rows || '<tr><td colspan="6">No transactions found</td></tr>'}
            </table>
        `;

    } catch (error) {
        showError(error);
    }
}


function escapeHTML(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}



function showError(error) {
    alert(error.message || "Unable to connect to Java server.");
}



loadDashboard();
