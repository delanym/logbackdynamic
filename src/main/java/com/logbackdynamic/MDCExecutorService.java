package com.logbackdynamic;

import org.slf4j.MDC;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/// This propagates MDC for ExecutorService (submit/execute).
class MDCExecutorService implements ExecutorService {

  protected final ExecutorService delegate;

  MDCExecutorService(ExecutorService delegate) {
    this.delegate = delegate;
  }

  /* ---------- wrapping ---------- */

  protected Runnable wrap(Runnable task) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return () -> {
      Map<String, String> previous = MDC.getCopyOfContextMap();
      try {
        if (contextMap != null) {
          MDC.setContextMap(contextMap);
        } else {
          MDC.clear();
        }
        task.run();
      } finally {
        if (previous != null) {
          MDC.setContextMap(previous);
        } else {
          MDC.clear();
        }
      }
    };
  }

  protected <T> Callable<T> wrap(Callable<T> task) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return () -> {
      Map<String, String> previous = MDC.getCopyOfContextMap();
      try {
        if (contextMap != null) {
          MDC.setContextMap(contextMap);
        } else {
          MDC.clear();
        }
        return task.call();
      } finally {
        if (previous != null) {
          MDC.setContextMap(previous);
        } else {
          MDC.clear();
        }
      }
    };
  }

  /* ---------- ExecutorService ---------- */

  @Override
  public void execute(Runnable command) {
    delegate.execute(wrap(command));
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    return delegate.submit(wrap(task));
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    return delegate.submit(wrap(task), result);
  }

  @Override
  public Future<?> submit(Runnable task) {
    return delegate.submit(wrap(task));
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
      throws InterruptedException {
    return delegate.invokeAll(tasks.stream().map(this::wrap).toList());
  }

  @Override
  public <T> List<Future<T>> invokeAll(
      Collection<? extends Callable<T>> tasks,
      long timeout,
      TimeUnit unit) throws InterruptedException {
    return delegate.invokeAll(tasks.stream().map(this::wrap).toList(), timeout, unit);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
      throws InterruptedException, ExecutionException {
    return delegate.invokeAny(tasks.stream().map(this::wrap).toList());
  }

  @Override
  public <T> T invokeAny(
      Collection<? extends Callable<T>> tasks,
      long timeout,
      TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return delegate.invokeAny(tasks.stream().map(this::wrap).toList(), timeout, unit);
  }

  /* ---------- lifecycle ---------- */

  @Override public void shutdown() { delegate.shutdown(); }
  @Override public List<Runnable> shutdownNow() { return delegate.shutdownNow(); }
  @Override public boolean isShutdown() { return delegate.isShutdown(); }
  @Override public boolean isTerminated() { return delegate.isTerminated(); }
  @Override public boolean awaitTermination(long timeout, TimeUnit unit)
      throws InterruptedException {
    return delegate.awaitTermination(timeout, unit);
  }
}
