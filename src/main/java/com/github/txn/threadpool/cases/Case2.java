package com.github.txn.threadpool.cases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 用例2.
 *
 * @author yanghuan
 */
public class Case2 {

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
                    int childCount = 20;
                    CountDownLatch childLatch = new CountDownLatch(childCount);
                    for(int j = 0; j < childCount; j ++) {
                        try {
                            Thread.sleep(new Random().nextInt(100));

                            load_data(finalI, j);
                        }
                        catch (Exception ignore) {
                        }
                        finally {
                            childLatch.countDown();
                        }
                    }

                    childLatch.await();
                    logger.info("任务_{} cost time: {}", finalI, (System.currentTimeMillis() - start));
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

    private static void load_data(int i, int j) {
        try {
            CompletableFuture.runAsync(() -> {
                logger.info("加载 任务_{} 数据_{}", i, j);

                try {
                    Thread.sleep(new Random().nextInt(300));
                } catch (Exception ignore) { }

            }, threadPool).get(500, TimeUnit.MILLISECONDS);
        } catch (Exception ignore) { }
    }
}
