import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введіть мінімальне значення діапазону: ");
        int minRange = scanner.nextInt();

        System.out.print("Введіть максимальне значення діапазону: ");
        int maxRange = scanner.nextInt();

        int arraySize = new Random().nextInt(21) + 40; // Випадковий розмір масиву між 40 і 60
        int[] numbers = new Random().ints(arraySize, minRange, maxRange + 1).toArray();

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<int[]>> futures = new ArrayList<>();
        Set<int[]> results = new CopyOnWriteArraySet<>();

        int chunkSize = (int) Math.ceil((double) arraySize / 4); // Використовуємо округлення вгору для обробки всіх елементів

        for (int i = 0; i < arraySize; i += chunkSize) {
            int end = Math.min(i + chunkSize, arraySize);
            int[] chunk = Arrays.copyOfRange(numbers, i, end);

            Callable<int[]> task = () -> {
                int[] partialResults = new int[chunk.length / 2 + chunk.length % 2]; // Обробка непарних частин
                for (int j = 0; j < chunk.length - 1; j += 2) {
                    partialResults[j / 2] = chunk[j] * chunk[j + 1];
                }
                if (chunk.length % 2 == 1) { // Обробка останнього єдиного елемента, якщо масив непарний
                    partialResults[partialResults.length - 1] = chunk[chunk.length - 1];
                }
                return partialResults;
            };
            futures.add(executor.submit(task));
        }

        try {
            long startTime = System.currentTimeMillis();

            for (Future<int[]> future : futures) {
                try {
                    results.add(future.get()); // Це буде блокувати, поки результат не буде доступний
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            long endTime = System.currentTimeMillis();

            System.out.println("Результати обробки:");
            for (int[] resultArray : results) {
                System.out.println(Arrays.toString(resultArray));
            }
            System.out.println("Час роботи програми: " + (endTime - startTime) + " ms");

        } finally {
            executor.shutdown();
        }
    }
}
