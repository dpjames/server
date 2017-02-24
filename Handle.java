import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
public class Handle implements Runnable{

       private Socket sock;
       public Handle(Socket sock){
           this.sock = sock;
       }
       public void run(){
         try{
            InputStream in = sock.getInputStream();
            OutputStream out = sock.getOutputStream();
            doAction(in, out);
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
   private static void doAction(InputStream in, OutputStream out) throws IOException{
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
