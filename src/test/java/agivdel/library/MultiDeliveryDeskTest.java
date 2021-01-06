package agivdel.library;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MultiDeliveryDeskTest {
    MultiDeliveryDesk mdd;

    Book book0 = new Book();
    Book book1 = new Book();
    Book book2 = new Book();
    Book book3 = new Book();
    String student1 = "Emily";
    String student2 = "Olivia";
    String student3 = "Blunt";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void beforeTest() {
        mdd = new MultiDeliveryDesk();
    }

    @Test
    public void multiDeliveryDeskTest() {
        Assert.assertNotNull(mdd);
    }

    @Test
    public void addNullBookTest() {
        exPrepare("Book can't be null.");
        mdd.addNewBook(null);
    }

    @Test
    public void addNotUniqueIdTest() {
        exPrepare(String.format("The book.id %d already added.", book1.id));
        addSomeBooks(book0);
        book1.id = 0;
        mdd.addNewBook(book1);
    }

    private void exPrepare(String message) { // название не оч хорошее, лучше - expectException
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage(message);
    }

    @Test
    public void addNullBookNullTitleConvertTest() {
        mdd.addNewBook(new Book());
        Assert.assertEquals("null", mdd.findAvailableBooks().get(0).title);
    }

    @Test
    public void addNewBookEmptyTitleTrimTest() {
        book0.title = "  ";
        mdd.addNewBook(book0);
        Assert.assertEquals("", mdd.findAvailableBooks().get(0).title);
    }

    @Test
    public void addNewBookNotUniqueTitlesPossibleTest() {
        Assert.assertEquals(0, mdd.findAvailableBooks().size());
        book0.id = 1;
        mdd.addNewBook(book0);
        book1.id = 2;
        mdd.addNewBook(book1);
        book2.id = 3;
        mdd.addNewBook(book2);
        Assert.assertEquals(3, mdd.findAvailableBooks().size());
    }

    @Test
    public void addCorrectBookTest() {
        addSomeBooks(book0, book1, book2);

        Assert.assertEquals(0, mdd.findAvailableBooks().get(0).id);
        Assert.assertEquals("book0", mdd.findAvailableBooks().get(0).title);
        Assert.assertEquals(1, mdd.findAvailableBooks().get(1).id);
        Assert.assertEquals("book1", mdd.findAvailableBooks().get(1).title);
        Assert.assertEquals(2, mdd.findAvailableBooks().get(2).id);
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
        exPrepare("Book can't be null.");
        mdd.borrowBook(null, student1);
    }

    @Test
    public void borrowNullOrEmptyStudentTest() {
        exPrepare("Student name can`t be null or empty. The book not been borrowed.");
        mdd.borrowBook(book0, null);
        mdd.borrowBook(book0, "  ");
    }

    @Test
    public void borrowNotAvailableIdTest() {
        book3.id = 10;
        exPrepare(String.format("There is no book with id=%d in the list of available books. The book not been borrowed.", book3.id));
        addSomeBooks(book0, book1, book2);
        mdd.borrowBook(book3, student1);
    }

    @Test
    public void borrowNotUniqueTitleTest() {
        addSomeBooks(book0, book1, book2);
        book3.id = 1;
        book3.title = "book0";
        mdd.borrowBook(book3, student2);
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
        exPrepare("Book can't be null.");
        mdd.returnBook(null, student1);
    }

    @Test
    public void returnNullOrEmptyStudentTest() {
        exPrepare("Student name can`t be null or empty. The book not been borrowed.");
        mdd.returnBook(book0, null);
        mdd.returnBook(book0, "  ");
    }

    @Test
    public void returnNotBorrowedIdTest() {
        book1.id = 1;
        exPrepare(String.format("There is no book with id=%d in the list of borrowed books. The book not been returned.", book1.id));
        mdd.addNewBook(book0);
        mdd.borrowBook(book0, student1);
        mdd.returnBook(book1, student1);
    }

    @Test
    public void returnByAnotherStudentTest() {
        book1.id = 1;
        book1.title = "book1";
        exPrepare(String.format("The book {%d, %s} must be returned by student %s. The book not been returned.", book1.id, book1.title, student1));
        mdd.addNewBook(book1);
        mdd.borrowBook(book1, student1);
        Assert.assertEquals(0, mdd.findAvailableBooks().size());
        mdd.returnBook(book1, student3);
        Assert.assertEquals(0, mdd.findAvailableBooks().size());
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
        addSomeBooks(2);
        Assert.assertEquals(2, mdd.findAvailableBooks().size());
    }

    @Test
    public void externalRewritingOfAvailableBooksTest() {
        addSomeBooks(1);
        Book book = mdd.findAvailableBooks().get(0);
        book.id = 100;
        book.title = "hundred";
        Book newBook = mdd.findAvailableBooks().get(0);
        Assert.assertNotEquals(book.id, newBook.id);
        Assert.assertEquals(0, newBook.id);
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
