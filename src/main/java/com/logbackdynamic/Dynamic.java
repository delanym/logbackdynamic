package com.logbackdynamic;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Dynamic {

  private static final Logger LOGGER = LoggerFactory.getLogger(Dynamic.class);

  public static void main(String[] args) throws InterruptedException, JoranException {

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
    var contextMap = MDC.getCopyOfContextMap();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    scheduler.scheduleAtFixedRate(() -> {
                                    MDC.setContextMap(contextMap);
                                    try {
                                      LOGGER.info(UUID.randomUUID().toString());
                                    } finally {
                                      MDC.clear();
                                    }
                                  },
                                  0,
                                  1,
                                  TimeUnit.MILLISECONDS);

// Prevent shutdown
    scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
  }
}
