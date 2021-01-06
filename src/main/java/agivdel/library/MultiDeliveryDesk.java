package agivdel.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Получилось несколько абонементов библиотеки, каждый со своими независимыми списками книг.
 * Книгу может возвращать только тот, кто ее брал.
 */
public class MultiDeliveryDesk implements Library {
    private final Map<Integer, String> availableBooks;
    private final Map<Integer, TitleAndStudentName> borrowedBooks;

    public MultiDeliveryDesk() {
        availableBooks = new ConcurrentHashMap<>();
        borrowedBooks = new ConcurrentHashMap<>();
    }

    /**
     * Метод не принимает null вместо книги.
     * Метод преобразует title из null и любого числа пробелов в title="".
     * Метод принимает title="" и другие неуникальные title (т.е. книга с неуникальными title записывается).
     * Книга с неуникальным id не записывается (т.е. и перезаписать имеющуюся книгу нельзя).
     * Имеющуюся книгу нельзя изменить снаружи.
     */
    @Override
    public void addNewBook(Book book) {
        checkBookIsNotNull(book);
        if (availableBooks.containsKey(book.id) || borrowedBooks.containsKey(book.id)) {
            throw new IllegalArgumentException(String.format("The book.id %d already added.", book.id));
        }
        String title = nullOrEmptyToCorrect(book.title);
        long notUniqueTitles = notUniqueTitleNumbers(title);
        if (notUniqueTitles > 0) {
            System.out.printf("The number of books with the same title is: %d%n", notUniqueTitles);
        }
        availableBooks.put(book.id, title);
    }

    public void checkBookIsNotNull(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book can't be null.");
        }
    }

    public String nullOrEmptyToCorrect(String str) {
        return str == null ? "" :  str.trim();
    }

    public long notUniqueTitleNumbers(String title) {
        return availableBooks.values().stream().filter(title::equals).count()
                + borrowedBooks.values().stream().filter(b -> title.equals(b.bookTitle)).count();
    }

    /**
     * Выдача книг происходит по id, title может быть null, "" или совпадать с таковым других книг.
     * (Было бы логично выдавать именно по title,
     * но при создании объекта Book идет неявная инициализация корректным для системы id=0,
     * так что при незнании id будешь всегда получать книгу с id=0.
     * В такой ситуации будем считать, что истинный id запрошенной книги известен.)
     */
    @Override
    public void borrowBook(Book book, String student) {
        checkBookIsNotNull(book);
        nullOrEmptyStudentNameException(student);
        if (!availableBooks.containsKey(book.id)) {
            throw new IllegalArgumentException(String.format("There is no book with id=%d in the list of available books. The book not been borrowed.", book.id));
        }
        var borrowedTitle = availableBooks.remove(book.id);
        borrowedBooks.put(book.id, new TitleAndStudentName(borrowedTitle, student));
        System.out.printf("Student %s took the book {%d, %s}.%n", student, book.id, borrowedTitle);
    }

    public void nullOrEmptyStudentNameException(String str) {
        if (!isNotNullOrEmpty(str)) {
            throw new IllegalArgumentException("Student name can`t be null or empty. The book not been borrowed.");
        }
    }

    public boolean isNotNullOrEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

    /**
     * При student=null, student="  ", book=null книга не возвращается.
     * Книгу может вернуть только тот, кто ее брал.
     */
    @Override
    public void returnBook(Book book, String student) {
        checkBookIsNotNull(book);
        nullOrEmptyStudentNameException(student);
        if (!borrowedBooks.containsKey(book.id)) {
            throw new IllegalArgumentException(
                    String.format("There is no book with id=%d in the list of borrowed books. The book not been returned.", book.id)
            );
        }
        TitleAndStudentName returnedBook = borrowedBooks.get(book.id);
        if (!student.equals(returnedBook.studentName)) {
            throw new IllegalArgumentException(
                    String.format("The book {%d, %s} must be returned by student %s. The book not been returned.%n",
                    book.id, returnedBook.bookTitle, returnedBook.studentName)
            );
        }
        borrowedBooks.remove(book.id);
        availableBooks.put(book.id, returnedBook.bookTitle);
        System.out.printf("Student %s returned the book {%d, %s}.%n", returnedBook.studentName, book.id, returnedBook.bookTitle);
    }

    @Override
    public List<Book> findAvailableBooks() {
        List<Book> bookList = new ArrayList<>();
        Book book;
        for (Map.Entry<Integer, String> entry : availableBooks.entrySet()) {
            book = new Book();
            book.id = entry.getKey();
            book.title = entry.getValue();
            bookList.add(book);
        }
        return bookList;
    }
}
