import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        final int CAPACITY = 20;
        final int LEFT_BOUNDARY = 1;
        final int RIGHT_BOUNDARY = 15;
        Random rn = new Random();
        int[] arr = new int[CAPACITY];
        HashMap<String, Integer> counter = new HashMap<>();

        for(int i = 0; i < CAPACITY; i++) {
            arr[i] = rn.nextInt(RIGHT_BOUNDARY - LEFT_BOUNDARY + 1) + LEFT_BOUNDARY;
        }

        StringBuilder sequence = new StringBuilder("[");
        String item;
        for(int i = 0; i < CAPACITY; i++) {
            item = Integer.toString(arr[i]);
            if(counter.containsKey(item)) {
                counter.put(item, counter.get(item) + 1);
            }
            else {
                counter.put(item, 1);
            }

            if(i == CAPACITY - 1) {
                sequence.append(item).append("]");
            }
            else {
                sequence.append(item).append(", ");
            }
        }

        System.out.println(sequence);
        for(Map.Entry<String, Integer> entry: counter.entrySet()) {
            if(entry.getValue() > 1 && entry.getValue() < 5) {
                System.out.println("Число '" + entry.getKey() + "' встречается " + entry.getValue() + " раза");
            }
            else if(entry.getValue() >= 5) {
                System.out.println("Число '" + entry.getKey() + "' встречается " + entry.getValue() + " раз");
            }
        }

    }
}