package org.opencloudengine.garuda.controller.component;

import com.jcraft.jsch.*;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import net.sf.expectit.filter.Filter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static net.sf.expectit.filter.Filters.*;
import static net.sf.expectit.matcher.Matchers.contains;
import static net.sf.expectit.matcher.Matchers.regexp;

/**
 * Ssh 접속을 통해 커맨드를 실행시키는 클래스이다.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */

public class SshClient {
	//30 seconds
	private final int TIME_OUT = 30 * 1000;

	private String userName;
	private String host;
	private String pwdOrKeyPath;
	private ChannelType channelType;
	private boolean initCmd = true;

	private Session session;
	private Channel channel;
	private ChannelSftp sftpChannel;

	public SshClient() {
		super();
	}

	private Expect expect;

	public enum ChannelType {
		shell, exec, sftp
	}

	public enum LoginTypeType {
		pwd, key
	}

	/**
	 * 초기 파라미터 설정
	 *
	 * @param userName     ex) root
	 * @param pwdOrKeyPath ex) testpwd or keyPath
	 * @param host         ex)test.cafe24.com
	 * @param channelType  ex)SshClient.ChannelType.shell
	 */
	public SshClient(String userName, String pwdOrKeyPath, String host, ChannelType channelType, LoginTypeType loginTypeType) {
		this.userName = userName;
		this.host = host;
		this.pwdOrKeyPath = pwdOrKeyPath;
		this.channelType = channelType;

		try {
			switch (loginTypeType) {
				case pwd:
					connectByPwd();
					break;
				case key:
					connectByPublicKey();
					break;
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("Can't connect msg - %s", e.getMessage()));
		}
	}

	public SshClient(RouterConfig routerConfig) {
		this.userName = routerConfig.getUser();
		this.host = routerConfig.getHost();
		this.pwdOrKeyPath = routerConfig.getPwdOrKeyPath();
		this.channelType = routerConfig.getChannelType();

		try {
			switch (routerConfig.getLoginTypeType()) {
				case pwd:
					connectByPwd();
					break;
				case key:
					connectByPublicKey();
					break;
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("Can't connect msg - %s", e.getMessage()));
		}
	}

	/**
	 * 비밀번호를 이용하여 SSH 접속한다.
	 *
	 * @throws Exception
	 */
	private void connectByPwd() throws Exception {
		close();

		JSch jsch = new JSch();
		jsch.setConfig("StrictHostKeyChecking", "no");

		session = jsch.getSession(userName, host, 22);
		session.setPassword(pwdOrKeyPath);
		session.connect(TIME_OUT);
		setConnect();

	}

	/**
	 * public key를 이용하여 SSH 접속한다.
	 *
	 * @throws Exception
	 */
	private void connectByPublicKey() throws Exception {
		close();

		JSch jsch = new JSch();
		jsch.addIdentity(pwdOrKeyPath);
		jsch.setConfig("StrictHostKeyChecking", "no");

		session = jsch.getSession(userName, host, 22);
		session.connect(TIME_OUT);
		setConnect();
	}

	private void setConnect() {
		try {
			channel = session.openChannel(channelType.toString());
			switch (channelType) {
				case sftp:
					sftpChannel = (ChannelSftp) channel;
					break;
				default:
					Filter filter = chain(removeColors(), removeNonPrintable());
					expect = new ExpectBuilder()
						.withOutput(channel.getOutputStream())
						.withInputs(channel.getInputStream(), channel.getExtInputStream())
						.withTimeout(TIME_OUT, TimeUnit.SECONDS)
						.withInputFilters(filter)
						.withExceptionOnFailure()
						.withExceptionOnFailure()
						.build();

			}

			channel.connect(TIME_OUT);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Unable connect host : %s", e.getMessage()));
		}

	}

	public void changeChannelType(ChannelType channelType) {
		this.channelType = channelType;
		setConnect();
	}

	/**
	 * 마지막 라인을 사용자가 직접 정하여 커맨드를 실행한다.
	 *
	 * @param cmd
	 * @param endLine
	 * @return
	 */
	public String runCommandWithEndLine(String cmd, String endLine) {
		String result = null;

		try {
			if (initCmd) {
				expect.expect(regexp(endLine));
				initCmd = false;
			}

			expect.sendLine(cmd);
			expect.expect(contains(cmd));
			return expect.expect(regexp(endLine)).getBefore();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public void runCommandWithoutReturn(String cmd, String userName) {
		try {
			String tempUserName = userName == null ? this.userName : userName;
			String endLine = String.format("(.*%s.*[#$])", tempUserName);
			expect.sendLine(cmd).expect(regexp(endLine));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * command를 실행한 후 Inputstream에서 마지막으로 지정된 String 이전 까지의 값을 리턴한다.
	 * 아래와 같은 경우 endLine 값을 지정한다.
	 * 1. [centos@docker-haproxy /]# 또는 root@v5060:~#
	 * 2. 위와 같은 경우 파라미터로 받은 userName을 조합하여 endLine을 생성한다.
	 *
	 * @param cmd      명령어
	 * @param userName 사용자를 변경한 경우를 변경된 사용자
	 * @return
	 */
	public String runCommand(String cmd, String userName) {
		String tempUserName = userName == null ? this.userName : userName;
		String endLine = String.format("(.*%s.*[#$])", tempUserName);
		return runCommandWithEndLine(cmd, endLine);
	}

	public boolean uploadLocalToRemote(String localLoc, String remoteLoc) {
		try {
			sftpChannel.put(new FileInputStream(localLoc), remoteLoc);
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	public boolean downloadRemoteToLocal(String remoteLoc, String localLoc) {
		try {
			sftpChannel.get(remoteLoc, localLoc);
		} catch (SftpException e) {
			throw new RuntimeException(e);
		}


		return true;
	}

	/**
	 * close resources
	 */
	public void close() {
		if (this.expect != null) {
			try {
				this.expect.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (this.channel != null) {
			this.channel.disconnect();
		}

		if (this.sftpChannel != null) {
			sftpChannel.exit();
		}

		if (session != null) {
			this.session.disconnect();
		}
	}
}
