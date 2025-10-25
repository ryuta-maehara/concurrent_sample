import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.Random;

// 配列の合計を計算する再帰タスク
class SumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10; // 分割する最小単位
    private final int[] array;
    private final int start, end;

    public SumTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;

        // 1. 小さなタスクは順次処理
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            // 2. タスクを2つに分割
            int mid = start + length / 2;
            SumTask leftTask = new SumTask(array, start, mid);
            SumTask rightTask = new SumTask(array, mid, end);

            // 3. 左側タスクをfork()で別スレッドで実行
            leftTask.fork();

            // 4. 右側は現在のスレッドで計算（スレッドの無駄を減らす）
            long rightResult = rightTask.compute();

            // 5. 左側の結果をjoin()で取得して統合
            long leftResult = leftTask.join();

            return leftResult + rightResult; // 結果を統合
        }
    }
}

public class ForkJoinExample {
    public static void main(String[] args) {
        // 6. 大きなデータを用意（ここでは100個のランダム整数）
        int[] data = new Random().ints(100, 1, 100).toArray();

        // 7. ForkJoinPoolを作成（スレッドプールとして管理）
        ForkJoinPool pool = new ForkJoinPool();

        // 8. 再帰タスクをForkJoinPoolで実行
        long total = pool.invoke(new SumTask(data, 0, data.length));

        // 9. 結果を表示
        System.out.println("配列の合計: " + total);
    }
}