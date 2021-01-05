package agivdel.library;

public final class Constants {

    public static final String nullBook = "Book can't be null. "; /*А нахрена здесь и далее пробел после точки?*/
    public static final String alreadyAddedId = "The book.id %d already added. "; /*мешанина - тут ты используещь форматирование, а notUniqueTitle конкатинируешь */
    public static final String notEmptyBook = "book.title can`t be empty. "; //не используется - не используемыяй код нужно немедленно удалять, он все равно останется в истории гита
    public static final String notUniqueTitle = "The number of books with the same title is: ";
    public static final String nullTitle = "null";
    public static final String notNullOrEmptyStudentName = "Student name can`t be null or empty. ";
    public static final String notBorrowed = "The book not been borrowed. ";
    public static final String notReturned = "The book not been returned. ";
    public static final String notAvailableBook = "There is no book with id=%d in the list of available books. ";
    public static final String notBorrowedBook = "There is no book with id=%d in the list of borrowed books. ";
    public static final String tookTheBook = "Student %s was took the book {%d, %s}. "; // was - лишнее
    public static final String mustBeReturned = "The book {%d, %s} must be returned by student %s. ";
    public static final String returnedTheBook = "Student %s returned the book {%d, %s}. ";

    //у тебя тут не все константы на самом деле частные константы
    //ты их конкатинируешь или форматируешь
    //при этом логика конкатинации или форматирования размазана между константами и местом их использования
    //иными словами, гемора стало больше, а не меньше
    //я бы вместо констант сделал бы методы, которые принимали бы параметры, а возвращали готовую строку\
    //или даже сразу бросали исключение с этой сторкой

    //вынести константы в класс - это ок.
    //но тогда недурно бы еще этому классу сделать приватный конструктор и модификатор final
    private Constants() {
    }
}
