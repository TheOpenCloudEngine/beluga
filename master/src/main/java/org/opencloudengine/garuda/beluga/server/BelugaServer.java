package org.opencloudengine.garuda.beluga.server;

import org.opencloudengine.garuda.beluga.action.ActionService;
import org.opencloudengine.garuda.beluga.api.RestAPIService;
import org.opencloudengine.garuda.beluga.cloud.ClustersService;
import org.opencloudengine.garuda.beluga.common.util.VersionConfigurer;
import org.opencloudengine.garuda.beluga.console.WebConsoleService;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.exception.BelugaException;
import org.opencloudengine.garuda.beluga.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.CountDownLatch;

/**
 * Beluga Server
 *
 * @author Sang Wook, Song
 * @since 1.0
 */
public class BelugaServer {

	private ServiceManager serviceManager;
	public static long startTime;
	public static BelugaServer instance;
	private static Logger logger;
	private boolean isRunning;

	private Thread shutdownHook;
	protected boolean keepAlive;

	private String serverHome;
	private static volatile Thread keepAliveThread;
	private static volatile CountDownLatch keepAliveLatch;
	private FileLock fileLock;
	private File lockFile;

	public static void main(String... args) throws BelugaException {
		if (args.length < 1) {
			usage();
			return;
		}

		BelugaServer server = new BelugaServer(args[0]);
		if (server.load(args)) {
			server.start();
		}
	}

	public void setKeepAlive(boolean b) {
		keepAlive = b;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public static BelugaServer getInstance() {
		return instance;
	}

	public BelugaServer() {
	}

	public BelugaServer(String serverHome) {
		this.serverHome = serverHome;
	}

	public boolean load() {
		return load(null);
	}

	public boolean load(String[] args) {

		boolean isConfig = false;

		// load 파라미터는 없을수도 있다.
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if (isConfig) {
					isConfig = false;
				} else if (args[i].equals("-config")) {
					isConfig = true;
				} else if (args[i].equals("-help")) {
					usage();
					return false;
				}
			}
		}

		setKeepAlive(true);
		return true;

	}

	protected static void usage() {

		System.out.println("usage: java " + BelugaServer.class.getName() + " [ -help -config ]" + " {HomePath}");

	}

	public void start() throws BelugaException {
		// 초기화 및 서비스시작을 start로 옮김.
		// 초기화로직이 init에 존재할 경우, 관리도구에서 검색엔진을 재시작할때, init을 호출하지 않으므로, 초기화를 건너뛰게
		// 됨.
		instance = this;

		if (serverHome == null) {
			System.err.println("Warning! No argument for \"{HomePath}\".");
			usage();
			System.exit(1);
		}

		File f = new File(serverHome);
		if (!f.exists()) {
			System.err.println("Warning! HomePath \"" + serverHome + "\" is not exist!");
			usage();
			System.exit(1);
		}

		if (fileLock == null) {
			lockFile = new File(serverHome, ".lock");
			try {
				FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel();
				fileLock = channel.tryLock();
			} catch (IOException e) {
				System.err.println("Error! Cannot create lock file \"" + lockFile.getAbsolutePath() + "\".");
				System.exit(1);
			}

			if (fileLock == null) {
				System.err.println("Error! Another instance of BelugaServer is running at home path = " + serverHome);
				System.exit(1);
			}
		}
		Environment environment = new Environment(serverHome).init();
		this.serviceManager = new ServiceManager(environment);
		serviceManager.asSingleton();

		serviceManager.registerService("rest", RestAPIService.class);
        serviceManager.registerService("cluster", ClustersService.class);
        serviceManager.registerService("action", ActionService.class);
		serviceManager.registerService("console", WebConsoleService.class);
		logger = LoggerFactory.getLogger(BelugaServer.class);
		logger.info("File lock > {}", lockFile.getAbsolutePath());

		/*
		* Start Services
		* */

        serviceManager.startServices();

 		if (shutdownHook == null) {
			shutdownHook = new ServerShutdownHook();
			Runtime.getRuntime().addShutdownHook(shutdownHook);
		}

		startTime = System.currentTimeMillis();

		new VersionConfigurer().printApplicationStartInfo(environment);
		isRunning = true;

		if (keepAlive) {
			setKeepAlive();
		}
	}

	private void setKeepAlive() {
		// keepAliveLatch 가 null일때만 실행되면, restart의 경우 이미 keep alive이므로 재실행하지 않는다.
		if (keepAliveLatch == null) {
			keepAliveLatch = new CountDownLatch(1);

			keepAliveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						keepAliveLatch.await();
					} catch (InterruptedException e) {
						// bail out
					}
				}
			}, "BelugaServer[keepAlive]");
			keepAliveThread.setDaemon(false);
			keepAliveThread.start();
		}
	}

	public void restart() throws BelugaException {
		logger.info("Restart BelugaServer!");

		stop();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignore) {
			// Thread가 인터럽트 걸리므로 한번더 시도.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignore2) {
			}
		}
		start();

	}

	public void stop() throws BelugaException {
		/*
		* Stop services
		* */
		serviceManager.stopServices();

		logger.info("BelugaServer shutdown!");
		isRunning = false;
	}

	public void close() throws BelugaException {

		/*
		* Close services
		* */
		serviceManager.closeServices();
        logger.info("BelugaServer close!");
        isRunning = false;

		if (fileLock != null) {
			try {
				fileLock.release();
				logger.info("BelugaServer Lock Release! {}", fileLock);
			} catch (IOException e) {
				logger.error("", e);
			}

			try {
				fileLock.channel().close();
			} catch (Exception e) {
				logger.error("", e);
			}

			try {
				lockFile.delete();
				logger.info("Remove .lock file >> {}", lockFile.getAbsolutePath());
			} catch (Exception e) {
				logger.error("", e);
			}
		}

	}

	protected class ServerShutdownHook extends Thread {

		@Override
		public void run() {
			try {
				logger.info("BelugaServer Shutdown Requested!");
				BelugaServer.this.stop();
				BelugaServer.this.close();
				if (keepAliveLatch != null) {
					keepAliveLatch.countDown();
				}
			} catch (Throwable ex) {
				logger.error("BelugaServer.shutdownHookFail", ex);
			} finally {
				logger.info("BelugaServer Shutdown Complete!");
			}
		}
	}

}
