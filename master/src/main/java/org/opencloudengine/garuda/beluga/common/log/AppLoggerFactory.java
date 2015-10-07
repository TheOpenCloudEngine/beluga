package org.opencloudengine.garuda.beluga.common.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.StatusPrinter;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by swsong on 2015. 8. 7..
 */
public class AppLoggerFactory {

    private static Map<String, Logger> loggerMap = new ConcurrentHashMap<>();

    private static final String WEBAPP_LOG_DIR = "webapp";
    private static final String APP_LOG_PATTERN1 = "[%date %level]";
    private static final String APP_LOG_PATTERN2 = "%msg%n";
    private static final String MAX_LOG_SIZE = "10MB";
    private static final int MAX_LOG_INDEX = 3;

    /**
     * 앱에 해당하는 로거를 가져온다.
     * */
    public static org.slf4j.Logger getLogger(String appId) {
        return loggerMap.get(appId);
    }

    public static org.slf4j.Logger createLogger(Environment env, String appId) {
        return createLogger(new File(env.logHomeFile(), WEBAPP_LOG_DIR), appId, null);
    }
    public static org.slf4j.Logger createLogger(Environment env, String appId, String identity) {
        return createLogger(new File(env.logHomeFile(), WEBAPP_LOG_DIR), appId, identity);
    }
    public static org.slf4j.Logger createLogger(File home, String appId) {
        return createLogger(home, appId, null);
    }
    public static org.slf4j.Logger createLogger(File home, String appId, String identity) {
        if(loggerMap.containsKey(appId)) {
            return loggerMap.get(appId);
        }

        String logHomePath = System.getProperty("log.path");

        String logFilePath = new File(home, appId + ".log").getAbsolutePath();
        String rollingLogNamePattern = new File(home, appId + ".%i.log.zip").getAbsolutePath();

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        String appLogPattern = null;
        if(identity != null) {
            appLogPattern = String.format("%s[%s] %s", APP_LOG_PATTERN1, identity, APP_LOG_PATTERN2);
        } else {
            appLogPattern = APP_LOG_PATTERN1 + " " + APP_LOG_PATTERN2;
        }

        /*
        * appender
        * */
        RollingFileAppender appender = new RollingFileAppender();
        appender.setName(appId + ".appender");
        appender.setContext(loggerContext);
        appender.setFile(logFilePath);

        /*
        * encoder
        * */
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern(appLogPattern);
        encoder.start();

        /*
        * policy
        * */
        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(appender);
        rollingPolicy.setFileNamePattern(rollingLogNamePattern);
        rollingPolicy.setMaxIndex(MAX_LOG_INDEX);
        rollingPolicy.start();

        SizeBasedTriggeringPolicy triggeringPolicy = new ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy();
        triggeringPolicy.setMaxFileSize(MAX_LOG_SIZE);
        triggeringPolicy.start();

        /*
        * set to appender
        * */
        appender.setEncoder(encoder);
        appender.setRollingPolicy(rollingPolicy);
        appender.setTriggeringPolicy(triggeringPolicy);
        appender.start();

        Logger logger = loggerContext.getLogger(appId);
        logger.addAppender(appender);
        logger.setAdditive(true);
        StatusPrinter.print(loggerContext);

        // log something
        logger.info("Logger [{}] generated!", appId);

        loggerMap.put(appId, logger);

        return logger;
    }
}
