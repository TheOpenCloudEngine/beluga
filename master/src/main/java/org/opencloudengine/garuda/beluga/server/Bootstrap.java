package org.opencloudengine.garuda.beluga.server;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Daemon Bootstrap
 * 내부적으로 BelugaServer class 를 구동시키며, /lib/ 하위의 jar 파일들을 모아서 classpath로 만들어준다.
 *
 * @author Sang Wook, Song
 * @since 1.0
 */
public class Bootstrap {

    private static Bootstrap daemon;
    private Object serverDaemon = null;

    protected ClassLoader serverLoader = null;
    private final String serverClass = "org.opencloudengine.garuda.beluga.server.BelugaServer";

    private String serverHome;
    private String mode;
    private String LIB_PATH = "/lib/";
    private String DEV_LIB_PATH = "/WEB-INF/lib/";

    public static void main(String[] args) {

        if (daemon == null) {
            // Don't set daemon until init() has completed
            Bootstrap bootstrap = new Bootstrap();
            try {
                bootstrap.init();
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }
            daemon = bootstrap;
        } else {
            // When running as a service the call to stop will be on a new
            // thread so make sure the correct class loader is used to prevent
            // a range of class not found exceptions.
            Thread.currentThread().setContextClassLoader(daemon.serverLoader);
        }

        try {
            args = new String[]{"start"};
            daemon.load(args);
            daemon.start();

        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }

    }

    protected void usage() {
        System.out.println("usage: java " + getClass().getName() + " [ -help ]" + " {HomePath}");

    }

    public void init() throws Exception {
        mode = System.getProperty("mode");
        if(mode != null){
            if(mode.equals("dev")){
                LIB_PATH = DEV_LIB_PATH;
            }
        }
        serverHome = System.getProperty("server.home");

        if (serverHome == null) {
            System.err.println("Warning! Please set env variable \"server.home\".");
            usage();
            System.exit(1);
        }

        File f = new File(serverHome);
        if (!f.exists()) {
            System.err.println("Warning! Path \"" + serverHome + "\" is not exist!");
            usage();
            System.exit(1);
        }

        serverLoader = initClassLoader();
        Thread.currentThread().setContextClassLoader(serverLoader);
        // Load our startup class and call its process() method
        Class<?> startupClass = serverLoader.loadClass(serverClass);
        Constructor<?> constructor = startupClass.getConstructor(new Class[]{String.class});
        serverDaemon = constructor.newInstance(new Object[]{serverHome});
    }

    public void load(String[] arguments) throws Exception {
        // 검색엔진으로 전달되는 args를 받아서 셋팅해준다.
        // 대부분 -D옵션을 통해 전달받으므로 아직까지는 셋팅할 내용은 없다.
        // Call the load() method
        String methodName = "load";
        Object[] param = null;
        Class<?>[] paramTypes = null;
        if (arguments != null && arguments.length > 0) {
            paramTypes = new Class[1];
            paramTypes[0] = arguments.getClass();
            param = new Object[1];
            param[0] = arguments;
        }
        Method method = serverDaemon.getClass().getMethod(methodName, paramTypes);
        method.invoke(serverDaemon, param);
    }

    public void initLibUrls(String libDir, Set<URL> set) throws IOException {
        File[] files = new File(libDir).listFiles();
        for (File file : files) {
            if (file.getName().matches(".*\\.jar$")) {
                file = file.getCanonicalFile();
                URL url = file.toURI().toURL();
                set.add(url);
            }
            if (file.isDirectory()) {
                initLibUrls(file.getAbsolutePath(), set);
            }
        }
    }

    private URLClassLoader initClassLoader() {
        String libDir = serverHome + LIB_PATH;

        Set<URL> set = new LinkedHashSet<URL>();
        try {
            initLibUrls(libDir, set);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        final URL[] urls = set.toArray(new URL[set.size()]);

        return new URLClassLoader(urls);
    }

    public void start() throws Exception {
        if (serverDaemon == null)
            init();

        Method method = serverDaemon.getClass().getMethod("start", (Class[]) null);
        method.invoke(serverDaemon, (Object[]) null);

    }

    public void stop() throws Exception {
        Method method = serverDaemon.getClass().getMethod("stop", (Class[]) null);
        method.invoke(serverDaemon, (Object[]) null);
    }
}
