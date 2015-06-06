package org.opencloudengine.garuda.Utils;

import com.jcraft.jsch.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by soo on 2015. 6. 5..
 */
public class SshUtil {

    Session jschSession;
    public Session getJschSession() {
        return jschSession;
    }
    public void setJschSession(Session jschSession) {
        this.jschSession = jschSession;
    }

    public Session sessionLogin(String host, String userId, String passwd) throws JSchException {
        JSch jsch=new JSch();

        Session session=jsch.getSession(userId, host, 22);
        session.setPassword(passwd);

        // username and password will be given via UserInfo interface.
        UserInfo ui = new MyUserInfo(){
            public void showMessage(String message){
            }
            public boolean promptYesNo(String message){
                return true;
            }
        };

        session.setUserInfo(ui);
        session.connect();
        setJschSession(session);
        return session;
    }

    public Session sessionLogin(String host, String userId, String passwd, String pemPath) throws JSchException{
        JSch jsch=new JSch();
        jsch.addIdentity(pemPath);

        Session session=jsch.getSession(userId, host, 22);
        session.setPassword(passwd);

        // username and password will be given via UserInfo interface.
        UserInfo ui = new MyUserInfo(){
            public void showMessage(String message){
            }
            public boolean promptYesNo(String message){
                return true;
            }
        };

        session.setUserInfo(ui);
        session.connect();
        setJschSession(session);
        return session;
    }

    public String runCommand(String command) throws Exception {

        if( getJschSession() == null )
            throw new Exception("not connected");

        String output = "";

        ChannelExec channel = (ChannelExec)getJschSession().openChannel("exec");

        ((ChannelExec)channel).setCommand(command);
        channel.setInputStream(null);
        channel.connect();

        // run
        InputStream in=channel.getInputStream();
        output = setInAndOutStream(channel, in);

        channel.disconnect();

        return output;
    }
    private static String setInAndOutStream(Channel channel, InputStream in) throws IOException, JSchException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder outPutResult = new StringBuilder("");
        int exitStatus = -100;
        String output = null;
        while (true) {
            while (true) {
                try {
                    String result = br.readLine();
                    if (result == null || "".equals(result))
                        break;
                    outPutResult.append(result);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    break;
                }
            }
            output = outPutResult.toString();
            System.out.println(output);
            if (channel.isClosed()) {
                exitStatus = channel.getExitStatus();
                break;
            }
            try{Thread.sleep(1000);}catch(Exception ee){}
        }
        return output;
    }

    public static abstract class MyUserInfo implements UserInfo, UIKeyboardInteractive{
        public String getPassword(){ return null; }
        public boolean promptYesNo(String str){ return false; }
        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return false; }
        public boolean promptPassword(String message){ return false; }
        public void showMessage(String message){ }
        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo){
            return null;
        }
    }

    public static class MyProgressMonitor implements SftpProgressMonitor{
        ProgressMonitor monitor;
        long count=0;
        long max=0;
        public void init(int op, String src, String dest, long max){
            this.max=max;
            monitor=new ProgressMonitor(null,
                    ((op==SftpProgressMonitor.PUT)? "put" : "get")+": "+src, "",  0, (int)max
            );
            count=0;
            percent=-1;
            monitor.setProgress((int)this.count);
            monitor.setMillisToDecideToPopup(1000);
        }
        private long percent=-1;
        public boolean count(long count){
            this.count+=count;

            if(percent>=this.count*100/max){ return true; }
            percent=this.count*100/max;

            monitor.setNote("Completed "+this.count+"("+percent+"%) out of "+max+".");
            monitor.setProgress((int)this.count);

            return !(monitor.isCanceled());
        }
        public void end(){
            monitor.close();
        }
    }

}
