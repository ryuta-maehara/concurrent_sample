import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ExecutorExample {
    public static void main(String[] args) throws InterruptedException {

        // AtomicLongでスレッドセーフなカウンタを用意
        AtomicLong counter = new AtomicLong(0);

        // スレッドプールを作成（CPUコア数に合わせるのが一般的）
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        int taskCount = 100;      // タスク数
        int incrementPerTask = 1000; // 各タスクがカウントする回数

        // タスクをExecutorに提出
        for (int i = 0; i < taskCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementPerTask; j++) {
                    // AtomicLongなら同期処理不要で安全にインクリメント可能
                    // synchronizedで排他制御不要
                    counter.incrementAndGet();
                }
            });
        }

        // すべてのタスク終了を待機（最大1分）
        executor.shutdown(); // 新しいタスク受付を停止
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            System.out.println("タスクが終了しませんでした");
            executor.shutdownNow(); // 強制終了
        }

        // 最終カウントを出力
        // 期待値: 100 * 1000 = 100,000
        System.out.println("最終カウント: " + counter.get());
    }
}