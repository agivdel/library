package agivdel.library;

public class Utils {

    //обычно такого рода методы называются
    // checkBook
    // checkBookIsNotNull
    // validateBook
    public static void nullBookException(Book book) {
        if (book == null) {
            throw new IllegalArgumentException(Constants.nullBook);
        }
    }

    //это метод ваще не клевый, почему - см agivdel.library.MultiDeliveryDesk.addNewBook
    public static String nullOrEmptyToCorrect(String str) {
        if (str == null) {
            str = Constants.nullTitle;
        } else {
            str = str.trim();
        }
        return str;
    }

    //этот метод не нужен, agivdel.library.Utils.notUniqueTitleNumbers покрывает всё
    public static boolean isNotUniqueTitle(String title, MultiDeliveryDesk mdd) {
        return mdd.getAvailableBooks().containsValue(title)
                || mdd.getBorrowedBooks().values().stream().anyMatch(b -> title.equals(b.bookTitle));
    }

    public static long notUniqueTitleNumbers(String title, MultiDeliveryDesk mdd) {
        return mdd.getAvailableBooks().values().stream().filter(title::equals).count()
                + mdd.getBorrowedBooks().values().stream().filter(b -> title.equals(b.bookTitle)).count();
    }

    public static void nullOrEmptyStudentNameException(String str) {
        if (!isNotNullOrEmpty(str)) {
            throw new IllegalArgumentException(Constants.notNullOrEmptyStudentName + Constants.notBorrowed);
        }
    }
    // этот метод - единственный, который помещен сюда по праву
    // остальные могут быть приватными методами MultiDeliveryDesk
    // вот если бы у тебя тут было бы больше одного класса библиотеки - тогда имело бы  смысл их вынести сюда чтоб использовать из обоих классов
    public static boolean isNotNullOrEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

}
