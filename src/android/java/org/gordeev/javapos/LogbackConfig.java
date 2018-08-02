package org.gordeev.javapos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogbackConfig {

	public static void configure() {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
		lc.reset();

        // Логирование в logcat
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(lc);
		encoder.setPattern("%logger{12} [%thread] %msg%n");
		encoder.start();

		LogcatAppender logcatAppender = new LogcatAppender();
		logcatAppender.setContext(lc);
		logcatAppender.setEncoder(encoder);
		logcatAppender.start();

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.addAppender(logcatAppender);
    }
}