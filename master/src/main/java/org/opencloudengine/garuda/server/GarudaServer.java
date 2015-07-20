package org.opencloudengine.garuda.server;

import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.common.util.VersionConfigurer;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.RESTService;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.CountDownLatch;

/**
 * Garuda Master Server
 *
 * @author Sang Wook, Song
 * @since 1.0
 */
public class GarudaServer {

	private ServiceManager serviceManager;
	public static long startTime;
	public static GarudaServer instance;
	private static Logger logger;
	private boolean isRunning;

	private Thread shutdownHook;
	protected boolean keepAlive;

	private String serverHome;
	private static volatile Thread keepAliveThread;
	private static volatile CountDownLatch keepAliveLatch;
	private FileLock fileLock;
	private File lockFile;

	public static void main(String... args) throws GarudaException {
		if (args.length < 1) {
			usage();
			return;
		}

		GarudaServer server = new GarudaServer(args[0]);
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

	public static GarudaServer getInstance() {
		return instance;
	}

	public GarudaServer() {
	}

	public GarudaServer(String serverHome) {
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

		System.out.println("usage: java " + GarudaServer.class.getName() + " [ -help -config ]" + " {HomePath}");

	}

	public void start() throws GarudaException {
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
				System.err.println("Error! Another instance of GarudaServer is running at home path = " + serverHome);
				System.exit(1);
			}
		}
		Environment environment = new Environment(serverHome).init();
		this.serviceManager = new ServiceManager(environment);
		serviceManager.asSingleton();

		RESTService restService = serviceManager.createService("rest", RESTService.class);
        ClusterService clusterService = serviceManager.createService("cluster", ClusterService.class);

		logger = LoggerFactory.getLogger(GarudaServer.class);
		logger.info("File lock > {}", lockFile.getAbsolutePath());

		/*
		* Start Services
		* */

		startService(restService);
        startService(clusterService);


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

	private void startService(AbstractService service) {
		if (service != null) {
			try {
				service.start();
			} catch (Throwable e) {
				logger.error("", e);
			}
		}else{
			logger.error("Service is null {}", service.getClass().getSimpleName());

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
			}, "GarudaServer[keepAlive]");
			keepAliveThread.setDaemon(false);
			keepAliveThread.start();
		}
	}

	public void restart() throws GarudaException {
		logger.info("Restart GarudaServer!");

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

	public void stop() throws GarudaException {
		/*
		* Stop services
		* */
		serviceManager.stopService(RESTService.class);

		logger.info("GarudaServer shutdown!");
		isRunning = false;
	}

	public void close() throws GarudaException {

		/*
		* Close services
		* */
		serviceManager.closeService(RESTService.class);

		if (fileLock != null) {
			try {
				fileLock.release();
				logger.info("GarudaServer Lock Release! {}", fileLock);
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
				logger.info("Server Shutdown Requested!");
				GarudaServer.this.stop();
				GarudaServer.this.close();
				if (keepAliveLatch != null) {
					keepAliveLatch.countDown();
				}
			} catch (Throwable ex) {
				logger.error("GarudaServer.shutdownHookFail", ex);
			} finally {
				logger.info("Server Shutdown Complete!");
			}
		}
	}

}
