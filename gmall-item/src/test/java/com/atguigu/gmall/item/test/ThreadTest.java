package com.atguigu.gmall.item.test;

import java.util.concurrent.FutureTask;

public class ThreadTest {
    public static void main(String[] args) {
        FutureTask<Object> futureTask = new FutureTask<>(() -> {
            return "sss";
        });

        new Thread(futureTask).start();

        try {
            Object o = futureTask.get();
            System.out.println(o);
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}


//  线程池的回顾

//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("初始化CompletableFuture对象！");
////            int i = 1/0;
//            return "hello";
//        }).thenApply(t -> {
//            System.out.println("thenApply.......");
//            System.out.println("t....." + t);
//            return " thenApply";
//        }).whenCompleteAsync((t, u) -> {
//            System.out.println("whenCompleteAsync.......");
//            System.out.println("t....." + t);
//            System.out.println("u....." + u);
//        }).exceptionally(t -> {
//            System.out.println("exceptionally.......");
//            System.out.println("t....." + t);
//            return " exception";
//        }).handle((t, u) -> {
//            System.out.println("handle.......");
//            System.out.println("t....." + t);
//            System.out.println("u....." + u);
//            return " handler";
//        }).applyToEither(CompletableFuture.completedFuture("completedFuture"), (t) -> {
//            System.out.println("t: " + t);
//            System.out.println("两个线程完成后的一个新的业务逻辑");
//            return "thenCombine";
//        }).handle((t, u) -> {
//            System.out.println(t);
//            return "xxxx";
//        });


//    public static void main(String[] args) {

//        new MyThread().start();
//
//        System.out.println("主线程执行。。。。");
//
//        System.out.println("=================================");

//        new Thread(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("线程执行。。。。");
//        }).start();
//        System.out.println("主线程执行。。。。");

//        FutureTask<Object> futureTask = new FutureTask<>(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("1处理子进程的业务逻辑。。。");
//            return "xxxx";
//        });
//        new Thread(futureTask).start();
//        while(futureTask.isDone()){
//
//        }
//        System.out.println("2主线程的业务逻辑。。。。");
//        try {
//            System.out.println("3" + futureTask.get());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        ExecutorService threadPool = Executors.newFixedThreadPool(3);
//
//        for (int i = 0; i < 10; i++) {
//            int finalI = i;
//            FutureTask<Object> futureTask = new FutureTask<>(() -> {
//                try {
//                    TimeUnit.SECONDS.sleep(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("处理子进程的业务逻辑。。。" + Thread.currentThread().getName());
//                return "xxxx";
//            });
//            threadPool.submit(futureTask);
//            System.out.println("主线程的业务逻辑。。。" + i);
//            try {
//                System.out.println(futureTask.get() + String.valueOf(i));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }


//    }
