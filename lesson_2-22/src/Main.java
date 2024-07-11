import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Введите дату в формате dd.MM.yyyy:");
        String date = in.next();

        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
        Date parsingDate;

        try {
            parsingDate = ft.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        GregorianCalendar gcalendar = new GregorianCalendar();
        gcalendar.setTime(parsingDate);

        gcalendar.add(Calendar.DATE, 45);
        System.out.println("Дата после увеличения на 45 дней: " + ft.format(gcalendar.getTime()));

        gcalendar.set(gcalendar.get(Calendar.YEAR), Calendar.JANUARY, 1);
        System.out.println("Дата после сдвига на начало года: " + ft.format(gcalendar.getTime()));

        gcalendar.setTime(parsingDate);
        int counterWorkDays = 0;
        while(counterWorkDays < 10) {
            if(gcalendar.get(Calendar.DAY_OF_WEEK) < 6) {
                counterWorkDays++;
            }
            gcalendar.add(Calendar.DATE, 1);
        }
        System.out.println("Дата после увеличения на 10 рабочих дней: " + ft.format(gcalendar.getTime()));


        System.out.println("Введите вторую дату в формате dd.MM.yyyy:");
        String newDate = in.next();
        Date parsingNewDate;

        try {
            parsingNewDate = ft.parse(newDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        GregorianCalendar newGcalendar = new GregorianCalendar();
        newGcalendar.setTime(parsingNewDate);

        long diff = parsingDate.getTime() - parsingNewDate.getTime();
        int days =  (int)(diff / (24 * 60 * 60 * 1000));
        int weeks = days / 7;
        int adjustment = Math.min(6, gcalendar.get(Calendar.DAY_OF_WEEK)) -  Math.min(6, newGcalendar.get(Calendar.DAY_OF_WEEK));

        if( adjustment < 0 ) {
            adjustment += 5;
        }

        counterWorkDays = weeks * 5 + adjustment;
        System.out.println("Количество рабочих дней между введенными датами: " + counterWorkDays);

        in.close();
    }
}