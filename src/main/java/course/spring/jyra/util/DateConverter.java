package course.spring.jyra.util;

import java.time.LocalDateTime;

public class DateConverter {

    public static String convertDate(LocalDateTime localDateTime) {
        return String.format("%d:%d %d/%d/%d",
                localDateTime.getHour(),
                localDateTime.getMinute(),
                localDateTime.getDayOfMonth(),
                localDateTime.getMonthValue(),
                localDateTime.getYear());
    }
}
