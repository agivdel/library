package agivdel.library;

import java.util.List;

public interface Library {

    //регистрация новой книги
    void addNewBook(Book book);

    //студент берет книгу
    void borrowBook(Book book, String student);

    //студент возвращает книгу
    void returnBook(Book book, String student);

    //получить список свободных книг
    List<Book> findAvailableBooks();
}
