package org.opencloudengine.garuda.beluga.common.util;

import org.apache.commons.lang3.StringUtils;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Application Version을 표시하는 Configurer.
 *
 * @author Byoung Gon, Kim
 * @author Sang Wook, Song
 * @since 0.1
 */
public class VersionConfigurer {

    /**
     * SLF4J Logging
     */
    private static Logger logger = LoggerFactory.getLogger(VersionConfigurer.class);

    private static final long MEGA_BYTES = 1024 * 1024;

    private static final String UNKNOWN = "Unknown";

    public String printApplicationStartInfo(Environment environment) {
        System.setProperty("PID", SystemUtils.getPid());

        Properties properties = new Properties();
	    properties.setProperty("name", "GarudaServer");

//        InputStream inputStream = null;
//        try {
//            inputStream = context.getResourceAsStream("/WEB-INF/app.properties");
//            properties.load(inputStream);
//        } catch (Exception ex) {
//            throw new IllegalArgumentException("Cannot load a '/WEB/INF/app.properties' file.", ex);
//        } finally {
//            IOUtils.closeQuietly(inputStream);
//        }

        // See : http://patorjk.com/software/taag/#p=display&f=Slant&t=Garuda%20PaaS
        StringBuilder builder = new StringBuilder();
	    builder.append("\n" +
			    "   ______                     __     \n" +
			    "  / ____/___ ________  ______/ /___ _\n" +
			    " / / __/ __ `/ ___/ / / / __  / __ `/\n" +
			    "/ /_/ / /_/ / /  / /_/ / /_/ / /_/ / \n" +
			    "\\____/\\__,_/_/   \\__,_/\\__,_/\\__,_/  \n" +
			    "                                     ");
        printHeader(builder, "Application Information");
        Properties appProps = new Properties();
        appProps.put("Instance", StringUtils.isEmpty(System.getProperty("instance")) ? "** UNKNOWN **" : System.getProperty("instance"));
//        appProps.put("Application", properties.get("name"));
//        appProps.put("Version", properties.get("version"));
//        appProps.put("Build Date", properties.get("build.timestamp"));
//        appProps.put("Build Number", properties.get("build.number"));
//        appProps.put("Revision Number", properties.get("revision.number"));
//        appProps.put("Copyright", properties.get("copyright"));
//        appProps.put("Organization", properties.get("organization"));
//        appProps.put("Developers", properties.get("developers"));

        Properties systemProperties = System.getProperties();
        appProps.put("Java Version", systemProperties.getProperty("java.version", UNKNOWN) + " - " + systemProperties.getProperty("java.vendor", UNKNOWN));
        appProps.put("Current Working Directory", systemProperties.getProperty("user.dir", UNKNOWN));

        print(builder, appProps);

        Properties memPros = new Properties();
        final Runtime rt = Runtime.getRuntime();
        final long maxMemory = rt.maxMemory() / MEGA_BYTES;
        final long totalMemory = rt.totalMemory() / MEGA_BYTES;
        final long freeMemory = rt.freeMemory() / MEGA_BYTES;
        final long usedMemory = totalMemory - freeMemory;

        memPros.put("Maximum Allowable Memory", maxMemory + "MB");
        memPros.put("Total Memory", totalMemory + "MB");
        memPros.put("Free Memory", freeMemory + "MB");
        memPros.put("Used Memory", usedMemory + "MB");

        print(builder, memPros);

        printHeader(builder, "Java System Properties");
        Properties sysProps = new Properties();
        for (final Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
            sysProps.put(entry.getKey(), entry.getValue());
        }

        print(builder, sysProps);

        printHeader(builder, "System Environments");
        Map<String, String> getenv = System.getenv();
        Properties envProps = new Properties();
        Set<String> strings = getenv.keySet();
        for (String key : strings) {
            String message = getenv.get(key);
            envProps.put(key, message);
        }

        print(builder, envProps);

	    logger.info(builder.toString());
        logger.info("============================================================");
        logger.info(" {} ({}) started!", properties.get("name"), SystemUtils.getPid());
	    logger.info(" Home = {}", environment.home());
	    logger.info("============================================================");

	    return builder.toString();
    }

    private void printHeader(StringBuilder builder, String message) {
        builder.append(org.slf4j.helpers.MessageFormatter.format("\n== {} =====================\n", message).getMessage()).append("\n");
    }

    private void print(StringBuilder builder, Properties props) {
        int maxLength = getMaxLength(props);
        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = props.getProperty(key);
            builder.append("  ").append(key).append(getCharacter(maxLength - key.getBytes().length, " ")).append(" : ").append(value).append("\n");
        }
    }

    private int getMaxLength(Properties props) {
        Enumeration<Object> keys = props.keys();
        int maxLength = -1;
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (maxLength < 0) {
                maxLength = key.getBytes().length;
            } else if (maxLength < key.getBytes().length) {
                maxLength = key.getBytes().length;
            }
        }
        return maxLength;
    }

    /**
     * 지정한 크기 만큼 문자열을 구성한다.
     *
     * @param size      문자열을 구성할 반복수
     * @param character 문자열을 구성하기 위한 단위 문자열. 반복수만큼 생성된다.
     * @return 문자열
     */
    private static String getCharacter(int size, String character) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(character);
        }
        return builder.toString();
    }

}