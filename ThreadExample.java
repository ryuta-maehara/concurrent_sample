import java.util.ArrayList;
import java.util.List;

// 共有リソース：カウンタ
class Counter {
    private int count = 0;

    // synchronizedで排他制御
    // 複数スレッドから呼ばれても同時に1スレッドしかアクセスできない
    public synchronized void increment() {
        count++;
    }

    // countを取得するメソッドもsynchronizedで排他制御
    public synchronized int getCount() {
        return count;
    }
}

public class ThreadExample {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        List<Thread> threads = new ArrayList<>();

        // 100個のタスクを手動でThread生成
        // ここでは各Threadが1000回ずつカウントを増加させる
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.increment(); // 排他制御済み
                }
            });
            threads.add(t); // スレッドリストに追加
            t.start(); // 手動でスレッド開始
        }

        // 全スレッド終了待機
        // join()で各スレッドが終わるまでメインスレッドをブロック
        for (Thread t : threads) {
            t.join();
        }

        // 最終的なカウントを出力
        // 期待値: 100 * 1000 = 100,000
        System.out.println("最終カウント: " + counter.getCount());
    }
}
