import java.net.*;
import java.io.*;
import java.util.*;
public class Client{
   private static final int PORT = 2265;
   private static final String IP = "127.0.0.1"; // 47.32.178.27
   public static void main(String[] args)throws IOException{
      if(args.length !=4){
         System.out.println("Usage: java client [action] [file] [username] [password]");
         System.exit(1);
      }
      int mode = getMode(args[0]);
      try{
         Socket sock = new Socket(IP,PORT);
         InputStream in = sock.getInputStream();
         OutputStream out = sock.getOutputStream();
         out.write(args[2].length());
         out.write(args[2].getBytes());
         out.flush();
         out.write(args[3].length());
         out.write(args[3].getBytes());
         out.flush();
         doAction(mode, args[1], in, out);
         in.close();
         out.close();
      }catch(Exception e){
         e.printStackTrace();
      }

   }
   private static void getFile(InputStream in, String name)throws IOException{
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
   private static void byte2file(byte[] arr, String name, int end)throws IOException{
      File f = new File(name);
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(arr, 0, end);
      fos.close();
   }
   private static void sendFile(OutputStream out, String name)throws IOException{
      byte[] file = file2byte(name);
      out.write(file);
   }
   private static byte[] file2byte(String name)throws IOException{
      File f = new File(name);
      FileInputStream fis = new FileInputStream(f);
      byte[] buf = new byte[(int)f.length()];
      fis.read(buf);
      fis.close();
      return buf;
   }
   private static void doAction(int mode, String name, InputStream in, OutputStream out)throws IOException{
      out.write(mode);
      byte[] namebytes = name.getBytes();
      out.write(namebytes.length);
      out.write(namebytes);
      if(mode == 0){
         getFile(in, name);
      }else if(mode == 1){
         sendFile(out, name);
      }

   }
   private static int getMode(String s) throws IOException{
      if(s.equalsIgnoreCase("get")){
         return 0;
      }else if(s.equalsIgnoreCase("send")){
         return 1;
      }else{
         System.out.println("no action to be done");
         System.exit(1);
         return -1;
      }
   }
}
