package usage;

import java.util.Calendar;
import java.util.Date;

import org.springframework.format.datetime.joda.DateTimeParser;

public class Test {

    public static void main(String[] args) {
        System.out.println("query : " + new Date(1567987200000L));
        long time = 1567983600000L;
        
        Date d = new Date(time);
        System.out.println("start : " + d);
        
        Date date = new Date(1567987200000L);
        System.out.println("end : " + date);
        
        date.setTime(time);
        System.out.println(date);
        
        System.out.println("--------------------");
        Calendar calendar = Calendar.getInstance();
//        Calendar.set(100, 12, 23);
        calendar.set(2000, 0, 23, 0, 0, 0);
        Date time2 = calendar.getTime();
        System.out.println(time2);
        
    }
}
