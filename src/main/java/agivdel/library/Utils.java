package agivdel.library;

public class Utils {

    public static void nullBookException(Book book) {
        if (book == null) {
            throw new IllegalArgumentException(Constants.nullBook);
        }
    }

    public static String nullOrEmptyToCorrect(String str) {
        if (str == null) {
            str = Constants.nullTitle;
        } else {
            str = str.trim();
        }
        return str;
    }

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

    public static boolean isNotNullOrEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

}
