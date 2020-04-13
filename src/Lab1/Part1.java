/*
 add a different package name
 */
package Lab1;
//package part1;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.Arrays;
import Lab1.header;



public class Part1{
    public static DatagramPacket stageA(String hostname, int port){


        //Step 1 -- client sends single UDP packet
        DatagramSocket socket = null;
        DatagramPacket response =null;

        try {

            socket = new DatagramSocket();  //open a socket on port 12235
            //connect to the server
            socket.setSoTimeout(10000); // set timeout on the connection - 10 seconds
            InetAddress host = InetAddress.getByName(hostname);


            //note from the spec that "strings that are a sequence of characters ending with the character '\0'"
            String sendString = "hello world\0";

            byte[] sendStringBytes = sendString.getBytes();
            header head =new header(sendStringBytes.length,0,1,68);
            ByteBuffer headerBuffer =head.byteBuffer;
            //Padding header with string with padding to 4 byte align --> total is 24 byte
            ByteBuffer packetBuffer =ByteBuffer.allocate(headerBuffer.capacity()+sendStringBytes.length+(4-sendStringBytes.length%4));

            /*
             you can remove this, I just used it for debugging
             */
            System.out.println(byteArrayToHex(headerBuffer.array()));

            packetBuffer.put(headerBuffer.array());
            packetBuffer.put(sendStringBytes);

            System.out.println("send out packet : "+Arrays.toString(packetBuffer.array()));

            DatagramPacket request = new DatagramPacket(packetBuffer.array(), packetBuffer.array().length, host, port);
            // Not sure why you want to use this
            byte[] buffer2 = new byte[packetBuffer.array().length];
            response = new DatagramPacket(buffer2, buffer2.length);


            /*
             you don't need to establish a connection for udp, you just send
             */
//            socket.connect(host, port);
//            System.out.println("IsBound: " + socket.isBound());
//            System.out.println("isConnected : " + socket.isConnected());


            socket.send(request); //send request
            System.out.println("...packet sent successfully....");

            socket.receive(response); //await reply
            System.out.println("Received packet data : " +
                    Arrays.toString(response.getData()));

            /*
             some code I added to help you guys see the response a little easier :)
             */
            int num = ByteBuffer.wrap(response.getData()).getInt(12);
            int len = ByteBuffer.wrap(response.getData()).getInt(16);
            int udp_port = ByteBuffer.wrap(response.getData()).getInt(20);
            int secretA = ByteBuffer.wrap(response.getData()).getInt(24);
            System.out.println("num: " + num + " len: " + len + " port " + udp_port + " secretA " + secretA);
            System.out.println("stage A complete");
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

        return response;

    }
    public static void stageB(String hostname,DatagramPacket response){
        /*
        Extract para from stage A response
         */
        int num = ByteBuffer.wrap(response.getData()).getInt(12);
        int len = ByteBuffer.wrap(response.getData()).getInt(16);
        int udp_port = ByteBuffer.wrap(response.getData()).getInt(20);
        int secretA = ByteBuffer.wrap(response.getData()).getInt(24);

        //Create Header
        header head =new header(len+4,secretA,1,68);
        ByteBuffer headerBuffer = head.byteBuffer;

        //Some socket parameter
        int packet_id =0;
        int TIMEOUT_MILLIS =500;
        boolean success = true;
        DatagramSocket socket = null;
        while(packet_id!=num){
            //Create payloadbuffer with size of len + padding
            ByteBuffer payloadBuffer =ByteBuffer.allocate(4+len+(4-len%4)); //padding + len+4
            payloadBuffer.putInt(packet_id); //first 4 bytes of payload is packet_id
            System.out.println("size of payload "+payloadBuffer.array().length);

            //Create packetBuffer using both header and payload
            ByteBuffer packetBuffer =ByteBuffer.allocate(headerBuffer.capacity()+payloadBuffer.capacity());
 //           System.out.println(byteArrayToHex(headerBuffer.array()));
            packetBuffer.put(headerBuffer.array());
            packetBuffer.put(payloadBuffer.array());

            System.out.println("size of packet "+packetBuffer.array().length); //should be payload size + 12

            boolean receivedResponse = false;
            int tries = 0;
            int MAX_TRIES = 10;
           do {
                try {
                    socket = new DatagramSocket();  //open a socket
                    //connect to the server
                    if(tries == 0)
                        socket.setSoTimeout(1000);
                    else
                        socket.setSoTimeout(TIMEOUT_MILLIS); // set retransmission rate- 0.5 seconds

                    InetAddress host = InetAddress.getByName(hostname);
                    DatagramPacket request = new DatagramPacket(packetBuffer.array(), packetBuffer.array().length, host, udp_port);

                    byte[] buffer2 = new byte[packetBuffer.array().length];
                    response = new DatagramPacket(buffer2, buffer2.length);

                    socket.send(request); //send request
                    System.out.println("send out packet : "+Arrays.toString(packetBuffer.array()));
                    System.out.println("...packet " + packet_id + " sent successfully....");

                    socket.receive(response); //await reply

                    receivedResponse =true;
                    System.out.println("Received packet " + packet_id + " data : " +
                            Arrays.toString(response.getData()));
                    System.out.println();

                } catch (IOException ex) {
                       tries ++;
                        System.err.println("Could not get response");
                        System.err.println(ex);
                } finally {
                    if (socket != null)
                        socket.close();
                }
           }while((!receivedResponse)&&(tries<MAX_TRIES));


            if(!receivedResponse){
                System.out.println("No response -- giving up");
                success = false;
            }

            packet_id+=1;
        }

        if(success) {
            //TODO receive last packet containing two integers

 //           int tcp_port = ByteBuffer.wrap(response.getData()).getInt(12);
 //           int secretB = ByteBuffer.wrap(response.getData()).getInt(16);
 //           System.out.println("port " + tcp_port + " secretB " + secretB);
            System.out.println("stage B complete");
        }else {
            System.err.println("STAGE B FAILED");
        }
    }

    public static void stageC(String hostname,DatagramPacket response) throws IOException {
         /*
        Extract para from stage B response
         */
        //TODO fix these
        int tcp_port = ByteBuffer.wrap(response.getData()).getInt(12);
        int secretB = ByteBuffer.wrap(response.getData()).getInt(16);

        //Create Header
     //   header head =new header(len+4,secretB,1,68);
     //   ByteBuffer headerBuffer = head.byteBuffer;


        Socket socket = null;
        try {
            socket = new Socket(hostname, tcp_port);
            socket.setSoTimeout(1000);

            System.out.println("Connected");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //Read data from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while((line = reader.readLine()) != null)
                System.out.println(line);



        } catch (IOException e){
            System.err.println(e);
        } finally {
            if(socket != null){
                socket.close();
            }
        }


    }
//    public void stageD(){
//
//    }


    public static void main(String[] args)throws IOException{

        String hostname = "attu2.cs.washington.edu";
        int udp_port = 12235;
        DatagramPacket response=stageA(hostname,udp_port);
        System.out.println("----------------------------------------");
        stageB(hostname,response);
        System.out.println("----------------------------------------");

    }


    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b) + " ");
        return sb.toString();
    }

}

