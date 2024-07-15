package lesson_4_4;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StudentWithClock implements Learner {
    private Learner learner;

    public StudentWithClock(Learner learner) {
        this.learner = learner;
    }

    @Override
    public void learn() {
        learner.learn();
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
        System.out.printf("Текущее время: %s", ft.format(date));
    }

}
