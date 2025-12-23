package com.logbackdynamic;

import java.util.Map;

import org.slf4j.MDC;

/// Runnable-level propagation which must be applied manually, clutters code, and easy to forget.
/// Rather use MDCExecutorService.
final class MDCRunnable implements Runnable {
  private final Runnable delegate;
  private final Map<String, String> contextMap;

  MDCRunnable(Runnable delegate) {
    this.delegate = delegate;
    this.contextMap = MDC.getCopyOfContextMap();
  }

  @Override
  public void run() {
    Map<String, String> previous = MDC.getCopyOfContextMap();
    try {
      if (contextMap != null) {
        MDC.setContextMap(contextMap);
      } else {
        MDC.clear();
      }
      delegate.run();
    } finally {
      if (previous != null) {
        MDC.setContextMap(previous);
      } else {
        MDC.clear();
      }
    }
  }
}
