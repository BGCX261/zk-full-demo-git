package org.hxzon.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentUtil {
    private static ExecutorService excutor = Executors.newFixedThreadPool(5);

    public static void execute(Runnable run) {
        excutor.execute(run);
    }
}
