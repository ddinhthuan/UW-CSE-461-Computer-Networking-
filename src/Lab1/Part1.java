/*
 add a different package name
 */
package Lab1;
//package part1;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class Part1{

    private static DatagramSocket udpSocket = null;
    private static Socket tcpSocket = null;
    private static InputStream in = null;
    private static InputStream bufferdInputStream =null;

//    private static final String HOSTNAME = "attu2.cs.washington.edu";
    private static final String HOSTNAME = "localhost";

    private static final int TIMEOUT = 1000;

    private static void initializeUDPSocket() {
        try {
            //connect to the server
            udpSocket = new DatagramSocket();
            udpSocket.setSoTimeout(TIMEOUT); // set timeout on the connection - 10 seconds

        } catch (IOException ex){
             System.err.println("Could not connect to " + HOSTNAME);
             System.err.println(ex);
        }
    }

    private static void closeUDPSocket() {
        udpSocket.close();
    }

    private static void initializeTCPSocket(int tcp_port) throws IOException {
        try {
            tcpSocket = new Socket();
            InetSocketAddress address = new InetSocketAddress(HOSTNAME, tcp_port);
            tcpSocket.connect(address, TIMEOUT);

        } catch (IOException e){
            System.err.println("Could not connect");
            System.err.println(e);
        }
    }

    private static void closeTCPSocket() throws IOException {
      tcpSocket.close();
       bufferdInputStream.close();
       in.close();
    }


    public static DatagramPacket stageA(int port){

        //Step 1 -- client sends single UDP packet
        DatagramPacket response =null;
        try {
            initializeUDPSocket();
            InetAddress host = InetAddress.getByName(HOSTNAME);

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
           // System.out.println(byteArrayToHex(headerBuffer.array()));

            packetBuffer.put(headerBuffer.array());
            packetBuffer.put(sendStringBytes);
            System.out.println("send out packet : "+Arrays.toString(packetBuffer.array()));
//            System.out.println("byte to hext: "+byteArrayToHex(packetBuffer.array()));

            DatagramPacket request = new DatagramPacket(packetBuffer.array(), packetBuffer.array().length, host, port);
            byte[] buffer2 = new byte[packetBuffer.array().length];
            response = new DatagramPacket(buffer2, buffer2.length);

            /*
             you don't need to establish a connection for udp, you just send
             */
            udpSocket.send(request); //send request
            System.out.println("...packet sent successfully....");

            udpSocket.receive(response); //await reply
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
//            System.out.println("byte array to string"+byteArrayToHex(response.getData()));
           // String quote = new String(buffer, 0, response.getLength());
            //System.out.println(quote);

        } catch (IOException ex){
            System.err.println(ex);
            closeUDPSocket();
        }

        return response;
    }

    public static DatagramPacket stageB(DatagramPacket response){
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
        while(packet_id!=num){
            //Create payloadbuffer with size of len + padding
            ByteBuffer payloadBuffer =ByteBuffer.allocate(4+len+(4-len%4)); //padding + len+4
            payloadBuffer.putInt(packet_id); //first 4 bytes of payload is packet_id
     //       System.out.println("size of payload "+payloadBuffer.array().length);

            //Create packetBuffer using both header and payload
            ByteBuffer packetBuffer =ByteBuffer.allocate(headerBuffer.capacity()+payloadBuffer.capacity());
 //           System.out.println(byteArrayToHex(headerBuffer.array()));
            packetBuffer.put(headerBuffer.array());
            packetBuffer.put(payloadBuffer.array());
//            System.out.println("size of packet "+packetBuffer.array().length); //should be payload size + 12

            boolean receivedResponse = false;
            int tries = 0;
           do {
                try {
                    // should already be connected to server

                    if(tries != 0)
                        udpSocket.setSoTimeout(TIMEOUT_MILLIS); // set retransmission rate- 0.5 seconds

                    InetAddress host = InetAddress.getByName(HOSTNAME);
                    DatagramPacket request = new DatagramPacket(packetBuffer.array(), packetBuffer.array().length, host, udp_port);

                    byte[] buffer2 = new byte[packetBuffer.array().length];
                    response = new DatagramPacket(buffer2, buffer2.length);

                    udpSocket.send(request); //send request
//                    System.out.println("send out packet : "+Arrays.toString(packetBuffer.array()));
//                    System.out.println("...packet " + packet_id + " sent successfully....");

                    udpSocket.receive(response); //await reply

                    //Verify server acknowledged packet by replying with identifier
                    receivedResponse =true;
//                    System.out.println("Received packet " + packet_id + " data : " +
//                            Arrays.toString(response.getData()));
                    //          ByteBuffer.wrap(response.getData()).getInt(12));
                    System.out.println("payloadLen: " + ByteBuffer.wrap(response.getData()).getInt(0) + " psecret: " +
                            ByteBuffer.wrap(response.getData()).getInt(4) + " step: " + ByteBuffer.wrap(response.getData()).getShort(8) +
                            " studentID: " + ByteBuffer.wrap(response.getData()).getShort(10) + " packetNum: " + ByteBuffer.wrap(response.getData()).getInt(12));


//                    System.out.println("byte array to string"+byteArrayToHex(response.getData()));
//                    System.out.println();

                } catch (IOException ex) {
                       tries ++;
                      System.err.println("Could not get response, trying again");
                      System.err.println("Sending on port: " + udp_port);
//                       System.err.println(ex);
                }

           }while((!receivedResponse));

            packet_id+=1;
        }

        System.out.println();
        //Once all acks received
        try{
            byte[] buffer2 = new byte[head.byteBuffer.array().length+8]; // 12 + 4 + 4
            response = new DatagramPacket(buffer2, buffer2.length);
            assert udpSocket != null;
            udpSocket.receive(response); //await reply

            System.out.println("Received final packet " +
                        Arrays.toString(response.getData()));

        }catch (IOException e){
            e.printStackTrace();
        } finally {
            closeUDPSocket();
        }

        int tcp_port = ByteBuffer.wrap(response.getData()).getInt(12);
        int secretB = ByteBuffer.wrap(response.getData()).getInt(16);
        System.out.println("tcp port " + tcp_port + " secretB " + secretB);
        System.out.println("stage B complete");

        return response;
    }

    public static ByteBuffer stageC(DatagramPacket response) throws IOException {
         /*
        Extract para from stage B response
         */
        int tcp_port = ByteBuffer.wrap(response.getData()).getInt(12);
   //     int secretB = ByteBuffer.wrap(response.getData()).getInt(16);

        initializeTCPSocket(tcp_port);

        //Just connect to the server then wait to receive data
        //after stage b, the server just opens up TCP socket and listens
        // for an incoming client connection,
        // as soon as it accepts that connection it will send part C
        // Reference: https://us.edstem.org/courses/403/discussion/30687

        //    System.out.println("Connected? " + socket.isConnected());
        ByteBuffer resp = null;
        byte[] real =null;
        try {
            //Read data from the server
            in = tcpSocket.getInputStream();
            bufferdInputStream = new BufferedInputStream(in);
            bufferdInputStream.mark(0);
            //read your bufferdInputStream
            bufferdInputStream.reset();
            //read it again

            byte[] data = new byte[1000];

            int count = bufferdInputStream.read(data);
            real =new byte[count+1];
            if (count >= 0) System.arraycopy(data, 1, real, 1, count);
            byte[] inBuf =real;
           System.out.println("Response: " + Arrays.toString(inBuf));
           System.out.println("Read " + inBuf.length + " bytes from server");
//            System.out.println("byte array to string"+byteArrayToHex(inBuf));
            resp = ByteBuffer.wrap(inBuf);
            bufferdInputStream.reset();

            int num2 = resp.getInt(12);
            int len2 = resp.getInt(16);
            int secretC = resp.getInt(20);
            char c = resp.getChar(24);

            System.out.println("num2: " + num2 + " len2: " + len2 +
                    " secret C: " + secretC + " c: " + c);
            System.out.println("stage C complete");


        } catch (IOException e){
            System.err.println(e);
            closeTCPSocket();
        }
        //don't close connection

        return resp;
    }

    public static ByteBuffer stageD(ByteBuffer partC) throws IOException {
     /*
        Extract para from stage C response
     */
        int num2 = partC.getInt(12);
        int len2 = partC.getInt(16);
        int secretC = partC.getInt(20);
        char c = partC.getChar(24);

        //Create Header
        //Client sends num2 payloads each of length len2
        // num2 separate payloads each with a separate header on the client side
        // but it will show up as a stream of concatenated packets on the server side.
        //https://us.edstem.org/courses/403/discussion/24141

        int packetNum =1;
        header head =new header(len2,secretC,1,68);
        while(packetNum <= num2){
            ByteBuffer headerBuffer = head.byteBuffer;

            //create Payload
            //each payload containing all bytes set to the character c
            ByteBuffer initialPayload = ByteBuffer.allocate(len2);
            for(int i=0; i<len2-1; ++i) {
                initialPayload.putChar(i, c);
            }

            ByteBuffer payloadBuffer =ByteBuffer.allocate(len2+(4-len2%4)); //padding -- 4 byte aligned -- padding should be 0s
            payloadBuffer.put(initialPayload.array());

            //Create packetBuffer using both header and payload
            ByteBuffer packetBuffer =ByteBuffer.allocate(headerBuffer.capacity()+payloadBuffer.capacity());
            packetBuffer.put(headerBuffer.array());
            packetBuffer.put(payloadBuffer.array());
//
            DataOutputStream dout=new DataOutputStream(tcpSocket.getOutputStream());

            dout.write(packetBuffer.array(), 0, packetBuffer.array().length);
            dout.flush();

            packetNum+=1;
        }

        ByteBuffer resp = null;
        byte[] real = null;
        try {
            byte[] data = new byte[1000];

            int count =bufferdInputStream.read(data);
            real =new byte[count+1];
            if (count >= 0) System.arraycopy(data, 1, real, 1, count);
            bufferdInputStream.reset();


            System.out.println("Response: "+Arrays.toString(real));
            //Read data from the server
            //Server responds with one integer payload
            in = tcpSocket.getInputStream();
            resp = ByteBuffer.wrap(real);

            int secretD = resp.getInt(12);
            System.out.println("secret D: " + secretD );
            System.out.println("stage D complete");


        } catch (IOException e){
            System.err.println(e);
        } finally {
            closeTCPSocket();
        }

        return resp;
    }


    public static void main(String[] args)throws IOException{
        int udp_port = 12235;
        udp_port =2425;
        DatagramPacket responseA=stageA(udp_port);
        System.out.println("----------------------------------------");
        DatagramPacket responseB=stageB(responseA);
        System.out.println("----------------------------------------");
        ByteBuffer responseC = stageC(responseB);
        System.out.println("----------------------------------------");
   //     ByteBuffer responseD = stageD(responseC);
        System.out.println("----------------------------------------");
        System.out.println("Part 1 Secrets: ");
        System.out.println("Stage a: " + ByteBuffer.wrap(responseA.getData()).getInt(24));
        System.out.println("Stage b: " + ByteBuffer.wrap(responseB.getData()).getInt(16));
        System.out.println("Stage c: " + ByteBuffer.wrap(responseC.array()).getInt(20));
     //   System.out.println("Stage d: " + ByteBuffer.wrap(responseD.array()).getInt(12));
    }


    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b) + " ");
        return sb.toString();
    }

//


}

