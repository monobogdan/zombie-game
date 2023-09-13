package com.monobogdan.allzombiesarebastards.z2d;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Loader {
    static final int THREAD_COUNT = 2;
    public boolean hasPendingAssets;

    private ExecutorService threadPool;

    Loader() {
        Engine.log("Allocating threadpool with %d threads", THREAD_COUNT);
        threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

    }


}
