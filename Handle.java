import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

import javax.security.auth.login.LoginException;
public class Handle implements Runnable{

   private Socket sock;
   public Handle(Socket sock){
      this.sock = sock;
   }
   public void run(){
   OutputStream out = null;
   InputStream in = null; 
   try{
      in = sock.getInputStream();
      out = sock.getOutputStream();
      out.write(authenticate(in, out));
      doAction(in, out);
      in.close();
      out.close();
   }catch(Exception e){
      e.printStackTrace();
   }
}

   private int authenticate(InputStream in, OutputStream out)throws IOException{
      int length = in.read();

      byte[] buff = new byte[length];
      in.read(buff);
      String user = "";
      for(int i = 0; i < buff.length; i++){
         user+=(char)buff[i];
      }
      length = in.read();
      System.out.println(length);
      buff = new byte[length];
      in.read(buff);
      return login(user, buff);
   }
   private boolean checkPass(String user, byte[] pass) {
      return false;
   }
   private int login(String user, byte[] pswd){
      try{
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         md.update(pswd);
         pswd = md.digest();
         if(checkPass(user,pswd)){
            return 0;
         }
      }catch(NoSuchAlgorithmException e){
         e.printStackTrace();
      }
      return -1;
   }

   private void getFile(InputStream in, String name)throws IOException{
      System.out.println("getting file: " + name);
      int max = 1024;
      byte[] bytefile = new byte[max]; 
      int read = 0;
      int cur = 0;
      while(read > -1){
         read = in.read(bytefile, cur, bytefile.length - cur);
         if(read>0){
            cur+=read;
         }
         if(cur>=max){
            max*=2;
            bytefile = Arrays.copyOf(bytefile, max);
         }
      }
      byte2file(bytefile, name, cur);
   }
   private void byte2file(byte[] arr, String name, int end)throws IOException{
      File f = new File(name);
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(arr, 0, end);
      fos.close();
   }
   private void sendFile(OutputStream out, String name)throws IOException{
      System.out.println("Sending file: " + name);
      byte[] file = file2byte(name);
      out.write(file);
   }
   private byte[] file2byte(String name)throws IOException{
      File f = new File(name);
      FileInputStream fis = new FileInputStream(f);
      byte[] buf = new byte[(int)f.length()];
      fis.read(buf);
      fis.close();
      return buf;
   }
   private void doAction(InputStream in, OutputStream out) throws IOException{
      int mode = in.read();
      int size = in.read();
      byte[] bname = new byte[size];
      int read = in.read(bname);
      String name = "";
      for(int i = 0; i < bname.length; i++){
         name+=(char)bname[i];
      }
      System.out.println("file: " + name);
      if(mode == 0){
         sendFile(out, name);   
      }else if(mode == 1){
         getFile(in, name);
      }
   }
}
