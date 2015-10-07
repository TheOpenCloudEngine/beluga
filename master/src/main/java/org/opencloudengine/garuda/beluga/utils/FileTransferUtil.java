package org.opencloudengine.garuda.beluga.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

/**
 * Created by soo on 2015. 6. 2..
 */
public class FileTransferUtil {

    public static void send(String lfile, String user, String passwd, String host, String path, String rfile, String pemPath) {
        InputStream is = null;
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(pemPath);

            Session session = jsch.getSession(user, host, 22);
            session.setPassword(passwd);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;

            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            is = classloader.getResourceAsStream(lfile);
            channelSftp.cd(path);
            channelSftp.put(is, rfile);

        } catch (Exception e) {
            System.out.println(e);
            try {
                if (is != null)
                    is.close();
            } catch (Exception ee) {
            }
        }
    }
}
