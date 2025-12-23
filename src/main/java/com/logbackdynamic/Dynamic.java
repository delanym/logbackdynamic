package com.logbackdynamic;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Dynamic {

  private static final Logger log = LoggerFactory.getLogger(Dynamic.class);

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

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(() -> log.info(UUID.randomUUID().toString()), 0, 1, TimeUnit.MILLISECONDS);

// Prevent shutdown
    scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
  }
}
