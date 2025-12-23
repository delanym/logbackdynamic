package com.logbackdynamic;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Dynamic {

  private static final Logger LOGGER = LoggerFactory.getLogger(Dynamic.class);

  static void main(String[] args) throws InterruptedException, JoranException {

    System.setProperty("log.dir", "dynamiclogs");
    System.setProperty("app.name", "installationstarter");

    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.reset();

    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(
        Dynamic.class.getClassLoader()
            .getResourceAsStream("installationstarter/logback.xml")
    );

    MDC.put("module", "CommandCentre");

    ExecutorService executor = MDCVirtualExecutors.newVirtualThreadPerTaskExecutor();

    // Submit 5 repeating tasks
    for (int i = 0; i < 5; i++) {
      executor.execute(() -> {
        try {
          while (true) {
            LOGGER.info(UUID.randomUUID().toString());
            TimeUnit.MILLISECONDS.sleep(500);
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
    }

    // Loom virtual threads created via Thread.ofVirtual().factory() are daemon threads by default,
    // and in Java, the JVM exits when all non-daemon threads are finished.
    // So we use this hack to keep the main thread alive forever.
    Thread.currentThread().join();

  }
}
