package course.spring.jyra.utils;

import java.time.LocalDateTime;

public class DateConverter {

    public static String convertDate(LocalDateTime localDateTime) {
        return String.format("%d.%d.%d %d:%d",
                localDateTime.getDayOfMonth(),
                localDateTime.getMonthValue(),
                localDateTime.getYear(),
                localDateTime.getHour(),
                localDateTime.getMinute());
    }
}
