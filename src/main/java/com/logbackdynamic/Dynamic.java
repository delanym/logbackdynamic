package com.logbackdynamic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
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

    while (true) {
      LOGGER.debug(UUID.randomUUID().toString());
      LOGGER.info(UUID.randomUUID().toString());
      LOGGER.warn(UUID.randomUUID().toString());
      TimeUnit.MILLISECONDS.sleep(100);

      // Switch log level dynamically at random intervals
      var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Dynamic.class);

      if (ThreadLocalRandom.current().nextInt(10) == 0) {
        logger.setLevel(Level.WARN);
        LOGGER.error("Switching log level to WARN");
      }

      if (ThreadLocalRandom.current().nextInt(20) == 0) {
        logger.setLevel(Level.INFO);
        LOGGER.error("Switching log level to INFO");
      }

      if (ThreadLocalRandom.current().nextInt(30) == 0) {
        LOGGER.error("Switching log level to OFF");
        logger.setLevel(Level.OFF);
      }
    }

  }
}
