import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;


public record BoundedBlockingQueue(BlockingQueue<Integer> queueParam) implements Runnable {
    static BlockingQueue<Integer> queue = new LinkedBlockingDeque<>(10);

    static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println(t + " throws exception: " + e);
            Thread.currentThread().interrupt();
            System.out.println("remove 1 from: "+queue);
            int prevSize = queue.size();
            System.out.println("PREVSIZE: " + prevSize);
            Iterator<Integer> it = queue.iterator();
            queue = new LinkedBlockingDeque<>(prevSize * 2);
            System.out.println("SIZE: " + queue.remainingCapacity());
            while(it.hasNext())  {
                try {
                    queue.put(it.next());
                    System.out.println("updated queue is: "+queue);
                } catch (InterruptedException ex) {
                    e.printStackTrace();
                }
            }
            Thread tNew = new Thread(new BoundedBlockingQueue(queue));
            tNew.setUncaughtExceptionHandler(handler);
            tNew.start();
        }
    };

    @Override
    public void run() {
        for (int i = 1; i < 100; i++) {
            System.out.println("[Producer] Put : " + i);
            queueParam.add(i);
            System.out.println("[Producer] Queue remainingCapacity : " + queueParam.remainingCapacity());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(queueParam);
        queueParam.add(74);


    }

    public static void main(String[] args) {

        Thread t = new Thread(new BoundedBlockingQueue(queue));
        t.setUncaughtExceptionHandler((new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(t + " throws exception: " + e);
                Thread.currentThread().interrupt();
                System.out.println("remove 1 from: "+queue);
                int prevSize = queue.size();
                System.out.println("PREVSIZE: " + prevSize);
                Iterator<Integer> it = queue.iterator();
                queue = new LinkedBlockingDeque<>(prevSize * 2);
                System.out.println("SIZE: " + queue.remainingCapacity());
                while(it.hasNext())  {
                    try {
                        queue.put(it.next());
                        System.out.println("updated queue is: "+queue);
                    } catch (InterruptedException ex) {
                        e.printStackTrace();
                    }
                }
                System.out.println("updated queue is: "+queue);
                Thread tNew = new Thread(new BoundedBlockingQueue(queue));
                tNew.setUncaughtExceptionHandler(handler);
                tNew.start();
            }
        }));
        t.start();


    }

}