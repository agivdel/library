package agivdel.library;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

public class MultiDeliveryDeskTest {
    MultiDeliveryDesk mdd;
    List<Book> bookList;

    Book book0 = new Book();
    Book book1 = new Book();
    Book book2 = new Book();
    Book book3 = new Book();
    String student1 = "Emily";
    String student2 = "Olivia";
    String student3 = "Blunt";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void multiDeliveryDeskTest() {
    }

    @Test
    public void addNullBookTest() {
        exPrepare(Constants.nullBook);
        mdd.addNewBook(null);
    }

    @Test
    public void addNotUniqueIdTest() {
        exPrepare(String.format(Constants.alreadyAddedId, book1.id));
        addSomeBooks(book0);
        book1.id = 0;
        mdd.addNewBook(book1);
    }

    private void exPrepare(String message) {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage(message);
    }

    @Test
    public void addNullBookTitleTest() {
        mdd.addNewBook(new Book());
        Assert.assertEquals("null", mdd.findAvailableBooks().get(0).title);
    }

    @Test
    public void addEmptyBookTitleTest() {
        book0.title = "  ";
        mdd.addNewBook(book0);
        Assert.assertEquals("", mdd.findAvailableBooks().get(0).title);
    }

    @Test
    public void addNotUniqueTitleTest() {
        book0.id = 1;
        mdd.addNewBook(book0);
        book1.id = 2;
        mdd.addNewBook(book1);
        book2.id = 3;
        mdd.addNewBook(book2);
    }

    @Test
    public void addCorrectBookTest() {
        addSomeBooks(book0, book1, book2);
        Assert.assertEquals(0, mdd.findAvailableBooks().get(0).id);
        Assert.assertEquals("book2", mdd.findAvailableBooks().get(2).title);
    }

    /**
     * Выдача книг происходит по id, title может быть null, "" или совпадать с таковым других книг.
     * (Было бы логично выдавать именно по title,
     * но при создании объекта Book идет неявная инициализация корректным для системы id=0,
     * так что при незнании id будешь всегда получать книгу с id=0.
     * В такой ситуации будем считать, что истинный id запрошенной книги известен.)
     */
    @Test
    public void borrowNullBookTest() {
        exPrepare(Constants.nullBook);
        mdd.borrowBook(null, student1);
    }

    @Test
    public void borrowNullOrEmptyStudentTest() {
        exPrepare(Constants.notNullOrEmptyStudentName + Constants.notBorrowed);
        mdd.borrowBook(book0, null);
        mdd.borrowBook(book0, "  ");
    }

    @Test
    public void borrowNotAvailableIdTest() {
        book3.id = 10;
        exPrepare(String.format(Constants.notAvailableBook, book3.id) + Constants.notBorrowed);
        addSomeBooks(book0, book1, book2);
        mdd.borrowBook(book3, student1);
    }

    @Test
    public void borrowNotUniqueTitleTest() {
        addSomeBooks(book0, book1, book2);
        book3.id = 1;
        book3.title = "book0";
        mdd.borrowBook(book3, student1);
        Assert.assertEquals(2, mdd.findAvailableBooks().size());
        Assert.assertEquals(0, mdd.findAvailableBooks().get(0).id);
        Assert.assertEquals(2, mdd.findAvailableBooks().get(1).id);
    }

    @Test
    public void borrowCorrectBookTest() {
        addSomeBooks(book0, book1);
        mdd.borrowBook(book0, student1);
        Assert.assertEquals(1, mdd.findAvailableBooks().size());
        Assert.assertEquals(1, mdd.findAvailableBooks().get(0).id);
    }

    /**
     * При student=null, student="  ", book=null книга не возвращается.
     * Книгу может вернуть только тот, кто ее брал.
     */
    @Test
    public void returnNullBookTest() {
        exPrepare(Constants.nullBook);
        mdd.returnBook(null, student1);
    }

    @Test
    public void returnNullOrEmptyStudentTest() {
        exPrepare(Constants.notNullOrEmptyStudentName + Constants.notBorrowed);
        mdd.returnBook(book0, null);
        mdd.returnBook(book0, "  ");
    }

    @Test
    public void returnNotBorrowedIdTest() {
        book1.id = 1;
        exPrepare(String.format(Constants.notBorrowedBook, book1.id) + Constants.notReturned);
        mdd.addNewBook(book0);
        mdd.borrowBook(book0, student1);
        mdd.returnBook(book1, student1);
    }

    @Test
    public void returnByAnotherStudent() {
        book1.id = 1;
        book1.title = "book1";
        exPrepare(String.format(Constants.mustBeReturned, book1.id, book1.title, student1) + Constants.notReturned);
        mdd.addNewBook(book1);
        mdd.borrowBook(book1, student1);
        mdd.returnBook(book1, student2);
    }

    @Test
    public void returnCorrectTest() {
        addSomeBooks(book0, book1);
        mdd.borrowBook(book0, student1);
        mdd.returnBook(book0, student1);
        Assert.assertEquals(2, mdd.findAvailableBooks().size());
    }

    @Test
    public void findAvailableBooksTest() {
        addSomeBooks(book0, book1);
        Assert.assertEquals(2, mdd.findAvailableBooks().size());
    }

    @Test
    public void externalRewritingOfAvailableBooksTest() {
        addSomeBooks(book0);
        Book book = mdd.findAvailableBooks().get(0);
        book.id = 100;
        book.title = "hundred";
        Book newBook = mdd.findAvailableBooks().get(0);
        Assert.assertNotEquals(book.id, newBook.id);
        Assert.assertEquals(0, newBook.id);
    }

    @Before
    public void beforeTest() {
        mdd = new MultiDeliveryDesk();
        bookList = new ArrayList<>();
    }

    @After
    public void clear() {
        mdd = null;
        bookList = null;
    }

    private void addSomeBooks(Book...books) {
        for (int i = 0; i < books.length; i++) {
            books[i].id = i;
            books[i].title = "book" + i;
            mdd.addNewBook(books[i]);
        }
    }

    private void addSomeBooks(int number) {
        Book book = new Book();
        for (int i = 0; i < number; i++) {
            book.id = i;
            book.title = "book" + i;
            mdd.addNewBook(book);
        }
    }
}
