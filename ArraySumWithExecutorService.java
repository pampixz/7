import java.util.concurrent.*; //для работы с многопоточностью

public class ArraySumWithExecutorService {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Создаем массив
        int[] array = new int[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1; // Заполняем числами от 1 до 100
        }

        // Указываем количество потоков
        int numberOfThreads = 4;

        // Создаем пул потоков
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads); //создание с фикс кол-во потоков
                                                                                         //позволяет управлять кол-вом потоков, не создавая заново
        // Вычисляем размер каждой части массива
        int chunkSize = (int) Math.ceil((double) array.length / numberOfThreads); //общее кол-во элементов/кол-во потоков

        // Создаем список задач
        Future<Integer>[] tasks = new Future[numberOfThreads]; //tasks - массив для хранения асинхронных задач, чтобы хранить через .submit

        // Разделяем массив на части и отправляем задачи в пул потоков
        for (int i = 0; i < numberOfThreads; i++) { //определяет диапазон индексов массива, который обрабатывает каждый поток
            final int start = i * chunkSize; //каждый поток обрабатывает диапазон данных [start, end)
            final int end = Math.min(start + chunkSize, array.length); //индекс не выйдет за границы массива

            tasks[i] = executorService.submit(() -> { //возвращает объект типа Future<Integer>, который позволит получить результат
                int sum = 0; //вычисляет сумму чисел в диапазоне
                for (int j = start; j < end; j++) {
                    sum += array[j];
                }
                return sum;
            });
        }

        // Собираем результаты
        int totalSum = 0;
        for (Future<Integer> task : tasks) { //проходит по всем объектам Future
            totalSum += task.get(); // получаем результат выполнения задачи //блокирует текущий поток
        }

        // Завершаем работу пула потоков
        executorService.shutdown();

        // Выводим результат
        System.out.println("Сумма элементов массива: " + totalSum);
    }
}