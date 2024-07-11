import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Subtask_4_4 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Введите дату в формате 'дд.мм.гггг'");
        String date = in.next();
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formatForParseDate = new SimpleDateFormat("yyyy-MM-dd");
        Date parsingDate;

        try {
            parsingDate = ft.parse(date);
            System.out.println(formatForParseDate.format(parsingDate));
        } catch (ParseException e) {
            System.out.println("Нераспаршена с помощью " + ft);
        }

        in.close();
    }
}
