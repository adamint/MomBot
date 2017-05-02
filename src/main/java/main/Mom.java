package main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Mom {
    public static Shard botLogsShard;
    public static int shardCount = 2;
    public static ScheduledExecutorService globalExecutorService = Executors.newScheduledThreadPool(5);

    public static void main(String[] args) throws Exception {
        ShardManager.register(shardCount);

        // Submit runnables to update
    }
}
