package Lab1;
import java.net.*;
import java.io.*;
public class server{
    public static void main(String[] args)throws IOException {
        ServerSocket ss = new ServerSocket( 4999);
        Socket s =ss.accept();

        System.out.println("Client connected");
        InputStreamReader in =new InputStreamReader(s.getInputStream());
        //BufferReader def : https://www.geeksforgeeks.org/java-io-bufferedreader-class-java/
        BufferedReader bf =new BufferedReader(in);
        String str =bf.readLine();
        System.out.println("client : "+str);

        PrintWriter pr =new PrintWriter(s.getOutputStream());
        //https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html
        pr.println("yes");
        pr.flush();
    }
}