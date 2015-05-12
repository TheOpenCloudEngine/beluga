package org.opencloudengine.garuda.controller.component;

/**
 * Created by soul on 15. 4. 21.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
public class RouterConfig {
	private String host;
	private String user;
	//ssh 접속을 위해서 pwd 또는 keyPath 값을 설정해야 한다.
	private String pwdOrKeyPath;
	private SshClient.ChannelType channelType;
	private SshClient.LoginTypeType loginTypeType;

	private RouterConfig(Builder builder) {
		host = builder.host;
		user = builder.user;
		pwdOrKeyPath = builder.pwdOrKeyPath;
		channelType = builder.channelType;
		loginTypeType = builder.loginTypeType;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwdOrKeyPath() {
		return pwdOrKeyPath;
	}

	public void setPwdOrKeyPath(String pwdOrKeyPath) {
		this.pwdOrKeyPath = pwdOrKeyPath;
	}

	public SshClient.ChannelType getChannelType() {
		return channelType;
	}

	public void setChannelType(SshClient.ChannelType channelType) {
		this.channelType = channelType;
	}

	public SshClient.LoginTypeType getLoginTypeType() {
		return loginTypeType;
	}

	public void setLoginTypeType(SshClient.LoginTypeType loginTypeType) {
		this.loginTypeType = loginTypeType;
	}

	public static final class Builder {
		private String host;
		private String user;
		private String pwdOrKeyPath;
		private SshClient.ChannelType channelType;
		private SshClient.LoginTypeType loginTypeType;

		public Builder() {
		}

		public Builder withHost(String host) {
			this.host = host;
			return this;
		}

		public Builder withUser(String user) {
			this.user = user;
			return this;
		}

		public Builder withPwdOrKeyPath(String pwdOrKeyPath) {
			this.pwdOrKeyPath = pwdOrKeyPath;
			return this;
		}

		public Builder withChannelType(SshClient.ChannelType channelType) {
			this.channelType = channelType;
			return this;
		}

		public Builder withLoginTypeType(SshClient.LoginTypeType loginTypeType) {
			this.loginTypeType = loginTypeType;
			return this;
		}

		public RouterConfig build() {
			if (this.pwdOrKeyPath == null) {
				throw new RuntimeException("must set keypath or pwd");
			}
			return new RouterConfig(this);
		}
	}
}
