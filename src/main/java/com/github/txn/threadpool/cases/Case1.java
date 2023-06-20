package com.github.txn.threadpool.cases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 用例1.
 *
 * @author yanghuan
 */
public class Case1 {

    private static final Logger logger = LoggerFactory.getLogger(Case1.class);

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(25, 50, (int) TimeUnit.MINUTES.toSeconds(5), TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), (r, executor) -> logger.error("线程池已满载，新任务将被丢弃!"));

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        int count = 10;
        CountDownLatch latch = new CountDownLatch(count);
        for(int i = 0; i < count; i ++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    Thread.sleep(new Random().nextInt(10));

                    load_data(finalI);
                }
                catch (Exception ignore) {
                }
                finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        long cost = System.currentTimeMillis() - start;
        logger.info("- - - - - - - - - - - - - ");
        logger.info("- - - - - - ^ - - - - - - ");
        logger.info("- - - - - - | - - - - - - ");
        logger.info("- - - - - 成功的 - - - - - ");
        logger.info("-  over cost time : {}  - ", cost);
        logger.info("- - - - - 失败的 - - - - - ");
        logger.info("- - - - - - | - - - - - - ");
        logger.info("- - - - - - v - - - - - - ");
        logger.info("- - - - - - - - - - - - - ");
    }

    private static void load_data(int i) {
        long start = System.currentTimeMillis();

        List<CompletableFuture> buildFutures = new ArrayList<>();
        for(int j = 0; j < 20; j ++) {
            int finalJ = j;
            buildFutures.add(CompletableFuture.runAsync(() -> {
                logger.info("加载 任务_{} 数据_{}", i, finalJ);

                try {
                    Thread.sleep(new Random().nextInt(200));
                } catch (Exception ignore) { }

            }, threadPool));
        }

        CompletableFuture<Void> finalFuture = CompletableFuture.allOf(buildFutures.toArray(new CompletableFuture[0]));
        try {
            finalFuture.get(500, TimeUnit.MILLISECONDS);
        } catch (Exception ignore) { }

        logger.info("任务_{} cost time: {}", i, (System.currentTimeMillis() - start));
    }
}
