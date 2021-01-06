package agivdel.library;

import java.util.*;
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
     * Метод преобразует title=null в title="null".
     * Метод принимает title="" и другие неуникальные title (т.е. книга с неуникальными title записывается).
     * Книга с неуникальным id не записывается (т.е. и перезаписать имеющуюся книгу нельзя).
     * Имеющуюся книгу нельзя изменить снаружи.
     */
    @Override
    public void addNewBook(Book book) {
        Utils.nullBookException(book);
        if (availableBooks.containsKey(book.id) || borrowedBooks.containsKey(book.id)) {
            throw new IllegalArgumentException(String.format(Constants.alreadyAddedId, book.id));
        }

        //давеча ты говорил, что null title - это не круто
        //таперича ты просто меняешь null на строку "null"
        //при этом просто пустую строку ты никак не маскируешь?
        //то есть у тебя возможно и "", и "null", хотя логически это одно и то же
        //обычно программисты заменяют null на пустую строку, или наоборот, чтоб в системе было единственное представление пустой строки
        String title = Utils.nullOrEmptyToCorrect(book.title);
        if (Utils.isNotUniqueTitle(title, this)) {
            System.out.println(Constants.notUniqueTitle + Utils.notUniqueTitleNumbers(title, this));
        }
        availableBooks.put(book.id, title);
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
        Utils.nullBookException(book);
        Utils.nullOrEmptyStudentNameException(student);
        if (!availableBooks.containsKey(book.id)) {
            throw new IllegalArgumentException(String.format(Constants.notAvailableBook, book.id) + Constants.notBorrowed);
        }
        var borrowedTitle = availableBooks.remove(book.id);
        borrowedBooks.put(book.id, new TitleAndStudentName(borrowedTitle, student));
        System.out.printf((Constants.tookTheBook) + "%n", student, book.id, borrowedTitle);
    }

    /**
     * При student=null, student="  ", book=null книга не возвращается.
     * Книгу может вернуть только тот, кто ее брал.
     */
    @Override
    public void returnBook(Book book, String student) {
        Utils.nullBookException(book);
        Utils.nullOrEmptyStudentNameException(student);
        if (!borrowedBooks.containsKey(book.id)) {
            throw new IllegalArgumentException(String.format(Constants.notBorrowedBook, book.id) + Constants.notReturned);
        }
        TitleAndStudentName returnedBook = borrowedBooks.get(book.id);
        if (!student.equals(returnedBook.studentName)) {
            throw new IllegalArgumentException(String.format(Constants.mustBeReturned, book.id, returnedBook.bookTitle, returnedBook.studentName) + Constants.notReturned);
        }
        borrowedBooks.remove(book.id);
        availableBooks.put(book.id, returnedBook.bookTitle);
        System.out.printf((Constants.returnedTheBook) + "%n", returnedBook.studentName, book.id, returnedBook.bookTitle);
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

    public Map<Integer, String> getAvailableBooks() {
        return availableBooks;
    }

    public Map<Integer, TitleAndStudentName> getBorrowedBooks() {
        return borrowedBooks;
    }
}
