package Lab1;
import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
public class multiThreadServer {
    public void multiTrheadServer(int port)throws IOException {
        ServerSocket serverSocket =new ServerSocket(port);//(12235 );
        System.out.println("server start");
        while(true){

            Socket socket =null;
            try{
                System.out.print("try to accepet");
                socket= serverSocket.accept();
                System.out.println("A new client is connected :"+socket.toString());

                DataInputStream dataInputStream =new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream =new DataOutputStream(socket.getOutputStream());
                System.out.println("Assigning new thread for this client");
                Thread thread =new ClientHandler(socket,dataInputStream,dataOutputStream);
                thread.start();
            }
            catch (Exception e){
                socket.close();
                e.printStackTrace();

            }
        }


    }
}