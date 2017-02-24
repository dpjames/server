import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
public class Server{
   public static final int PORT = 2265;
   public static final int POOLSIZE = 50;
   public static void main(String[] args) throws IOException{
      ExecutorService pool = Executors.newFixedThreadPool(POOLSIZE);
      ServerSocket s = new ServerSocket(PORT);   
      while(true){
         try{
            pool.execute(new Handle(s.accept()));
         }catch(Exception e){
             e.printStackTrace();
         }   
      }
   }
}
