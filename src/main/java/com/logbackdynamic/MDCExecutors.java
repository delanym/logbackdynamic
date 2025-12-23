package com.logbackdynamic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class MDCExecutors {

  private MDCExecutors() {}

  public static ScheduledExecutorService scheduled(int threads) {
    return wrap(Executors.newScheduledThreadPool(threads));
  }

  public static ExecutorService fixed(int threads) {
    return wrap(Executors.newFixedThreadPool(threads));
  }

  private static ScheduledExecutorService wrap(ScheduledExecutorService delegate) {
    return new MDCScheduledExecutorService(delegate);
  }

  private static ExecutorService wrap(ExecutorService delegate) {
    return new MDCExecutorService(delegate);
  }
}
