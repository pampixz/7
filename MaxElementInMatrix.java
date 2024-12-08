import java.util.concurrent.*; //для создания пула потоков и управлением асинхронным выполнением задач
//ExecuterService - пул потоков для управления задачами
//Future - позволяет асинхронно получать результаты их потоков
//InterruptedException, ExecutionException - исключения, связанные с многопоточностью
public class MaxElementInMatrix {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Создаем матрицу
        int[][] matrix = {
                {10, 20, 30, 40},
                {50, 60, 70, 80},
                {90, 100, 110, 120},
                {130, 140, 150, 160}
        };

        // Указываем количество потоков
        int numberOfThreads = 4;

        // Создаем пул потоков
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads); //пул из фиксированного кол-ва потоков

        // Вычисляем количество строк, обрабатываемых каждым потоком
        int rowsPerThread = (int) Math.ceil((double) matrix.length / numberOfThreads); //каждая задача обрабатывает одну строку матрицы

        // Массив задач
        Future<Integer>[] tasks = new Future[numberOfThreads]; //массив, используемый для хранения асинхронных задач

        // Разделяем матрицу на части и отправляем задачи в пул потоков
        for (int i = 0; i < numberOfThreads; i++) { //определяет диапазон строк
            final int startRow = i * rowsPerThread; //поток 0 обрабатывает строки с индексами [0,1], поток 1 - [2,3]
            final int endRow = Math.min(startRow + rowsPerThread, matrix.length);

            tasks[i] = executorService.submit(() -> { //обрабатывает диапазон строк и вычисляет максимальное значение
                int max = Integer.MIN_VALUE; //начальное значение максимума установлено в минимально возможное число
                for (int row = startRow; row < endRow; row++) { //цикл перебирает все элементы
                    for (int value : matrix[row]) {
                        max = Math.max(max, value);
                    }
                }
                return max;
            });
        }

        // Сравниваем результаты
        int globalMax = Integer.MIN_VALUE; //ждет, пока поток завершит выполнение задачи
        for (Future<Integer> task : tasks) {
            globalMax = Math.max(globalMax, task.get()); //находит глобальный максимум, сравнивая все максимумы
        }

        // Завершаем работу пула потоков
        executorService.shutdown();

        // Выводим результат
        System.out.println("Наибольший элемент в матрице: " + globalMax);
    }
}