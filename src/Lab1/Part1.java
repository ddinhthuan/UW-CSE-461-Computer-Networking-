package Lab1;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.Arrays;
import Lab1.header;

import javax.swing.plaf.synth.SynthTextAreaUI;

import static Lab1.ByteBufferUtils.concat;

public class Part1{
    public static void stageA(){

        //Step 1 -- client sends single UDP packet
        DatagramSocket socket = null;
        int port = 12235;
        String hostname = "attu3.cs.washington.edu";
        try {
            socket = new DatagramSocket();  //open a socket on port 12235
            //connect to the server
            socket.setSoTimeout(10000); // set timeout on the connection - 10 seconds
            InetAddress host = InetAddress.getByName(hostname);

            String sendString = "hello world";
            byte[] sendStringBytes = sendString.getBytes();
            header head =new header(sendStringBytes.length,0,1,123);
            ByteBuffer headerBuffer =head.byteBuffer;
            //Padding header with string with padding to 4 byte align --> total is 24 byte
            ByteBuffer packetBuffer =ByteBuffer.allocate(headerBuffer.capacity()+sendStringBytes.length+(4-sendStringBytes.length%4));
            packetBuffer.put(headerBuffer.array());
            packetBuffer.put(sendStringBytes);

            System.out.println("send out packet length : "+packetBuffer.array().length);

            DatagramPacket request = new DatagramPacket(packetBuffer.array(), packetBuffer.array().length, host, port);
            // Not sure why you want to use this
            byte[] buffer2 = new byte[packetBuffer.array().length];
            DatagramPacket response = new DatagramPacket(buffer2, buffer2.length);

            socket.connect(host, port);
            System.out.println("IsBound: " + socket.isBound());
            System.out.println("isConnected : " + socket.isConnected());

            socket.send(request); //send request
            System.out.println("...packet sent successfully....");

            socket.receive(response); //await reply
            System.out.println("Received packet data : " +
                    Arrays.toString(response.getData()));

           // String quote = new String(buffer, 0, response.getLength());
            //System.out.println(quote);

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

        stageA();

    }
}