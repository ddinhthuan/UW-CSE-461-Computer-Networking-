package Lab1;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.Arrays;
import Lab1.header;
public class Part1{
    public static void stageA(){
        //Step 1 -- client sends single UDP packet
        DatagramSocket socket = null;
        int port = 12235;
        String hostname = "attu2.cs.washington.edu";
        try {
            socket = new DatagramSocket();  //open a socket on port 12235
            //connect to the server
            socket.setSoTimeout(10000); // set timeout on the connection - 10 seconds
            InetAddress host = InetAddress.getByName(hostname);

            String sendString = "hello world";
            byte[] buffer = sendString.getBytes();
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, host, port);
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.send(request); //send request
            socket.receive(response); //await reply

            String quote = new String(buffer, 0, response.getLength());
            System.out.println(quote);

        } catch (IOException ex){
            System.err.println("Could not connect to attu2.cs.washington.edu");
            System.err.println(ex);
        } finally {
            if(socket != null){
                socket.close(); //close disconnect
            }
        }


    }
//    public void stageB(){
//
//    }
//    public void stageC(){
//
//    }
//    public void stageD(){
//
//    }


    public static void main(String[] args)throws IOException{
        header head =new header(10,200,1,023);
        System.out.println("header : "+Arrays.toString(head.getHeader()));
        stageA();

    }
}