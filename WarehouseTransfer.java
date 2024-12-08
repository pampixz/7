import java.util.concurrent.*;
import java.util.*;

class LoaderRealization implements Runnable {
    private final List<Integer> weights;
    private final int maxWeight;
    private static final Object lock = new Object(); // Для синхронизации доступа
    private static int loadCounter = 1; // Счётчик отправок

    public LoaderRealization(List<Integer> weights, int maxWeight) {
        this.weights = weights;
        this.maxWeight = maxWeight;
    }

    @Override
    public void run() {
        List<Integer> currentLoad = new ArrayList<>();
        int currentWeight = 0;

        while (true) {
            synchronized (lock) {
                if (weights.isEmpty()) {
                    break;
                }

                // Забираем товар, если он помещается в текущую нагрузку
                int weight = weights.get(0);
                if (currentWeight + weight <= maxWeight) {
                    currentLoad.add(weight);
                    currentWeight += weight;
                    weights.remove(0);
                } else if (currentLoad.isEmpty()) {
                    // Если груз слишком тяжелый и грузчики не могут его взять
                    System.out.println("Товар весом " + weight + " кг слишком тяжёлый для переноса.");
                    weights.remove(0);
                } else {
                    // Если текущая загрузка завершена
                    break;
                }
            }
        }

        // Вывод информации о текущей загрузке
        if (!currentLoad.isEmpty()) {
            synchronized (lock) {
                System.out.println("Загрузка #" + loadCounter++ + ": " + currentLoad + " кг (вес: " + currentWeight + " кг).");
            }
            unload(currentLoad);
        }
    }

    private void unload(List<Integer> load) {
        try {
            System.out.println("Перенос товаров: " + load);
            Thread.sleep(1000); // Имитация времени разгрузки
            System.out.println("Разгрузка завершена: " + load);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Ошибка при разгрузке.");
        }
    }
}

public class WarehouseTransfer {
    public static void main(String[] args) {
        // Товары и их веса
        List<Integer> weights = new ArrayList<>(Arrays.asList(
                40, 50, 30, 20, 70, 60, 90, 80, 10, 40, 30, 25, 35, 45
        ));

        // Максимальный вес для загрузки
        int maxWeight = 150;

        // Количество грузчиков
        int numberOfLoaders = 3;

        // Создаем пул потоков
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfLoaders);

        // Запускаем грузчиков
        for (int i = 0; i < numberOfLoaders; i++) {
            executorService.execute(new LoaderRealization(weights, maxWeight));
        }

        // Завершаем работу пула
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS); // Ожидаем завершения всех задач
        } catch (InterruptedException e) {
            System.out.println("Ошибка при завершении работы пула потоков.");
        }

        System.out.println("Все товары перенесены.");
    }
}