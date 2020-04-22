package Lab1;


//import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
//import sun.security.x509.IPAddressName;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
public class ClientHandler extends Thread{
    //copy from lab1
    private  DatagramSocket udpSocket = null;
    private  ServerSocket tcpSocket = null;

    //    final  socket;
    int psecretA =0;
    int psecretB =Integer.MAX_VALUE;
    int psecretC =Integer.MAX_VALUE;
    private static int stageB_packetNum = 0;
    private static int stageB_numPackets;
    private static int stageB_packetLen;

    private static final int TIMEOUT = 1000;
    private static final String HOSTNAME = "localhost";
    private static InputStream in =null;
    private static OutputStream out =null;

    int tcp_port =0;
    short studentID=0;


    public ClientHandler(DatagramSocket udpSocket,int psecretA,int num) {

        this.udpSocket =udpSocket;
        this.psecretA  =psecretA;
        this.stageB_numPackets =num;

    }
    @Override
    public void run() {
        byte[] received=new byte[10000];//need to double check here
        DatagramPacket receivedPacket = null;
        while (true) {
            try {

                // Ask user what he wants

                // receive the answer from client
                receivedPacket = new DatagramPacket(received, received.length);// not sure how large I should assign here

                udpSocket.receive(receivedPacket);
                System.out.println("is bound "+udpSocket.isBound());
                ByteBuffer receivedBuf =ByteBuffer.wrap(receivedPacket.getData());
                System.out.println(Arrays.toString(receivedBuf.array()));
                int payload_len=receivedBuf.getInt(0);
                int clientPsecret=receivedBuf.getInt(4);
                short step=receivedBuf.getShort(8);
                short studentID=receivedBuf.getShort(10);
                if(step!=1){
                    if(udpSocket!=null&& udpSocket.isBound())closeUDPSocket();
                    if(tcpSocket!=null&& tcpSocket.isBound())closeTCPSocket();
                    System.out.println("break the connection because step !=1");
                    break;
                }

                if(clientPsecret==psecretA ) {
                    stageB(receivedPacket);
                }else if(clientPsecret==psecretB ){
                    stageC();
                }else if(clientPsecret==psecretC){
                    stageD();
                }


            } catch (IOException e) {
//                e.printStackTrace();
            }
        }



    }
    private void stageB(DatagramPacket receivedPacket){
        ByteBuffer receivedBuf =ByteBuffer.wrap(receivedPacket.getData());
        //header
        int payload_len=receivedBuf.getInt(0);
        int clientPsecret=receivedBuf.getInt(4);
        short step=receivedBuf.getShort(8);
        studentID=receivedBuf.getShort(10);

        //payload
        int packet_id=receivedBuf.getInt(12);
        // rest of payload is 0s

        assert(payload_len == stageB_packetLen + 4);
        assert(payload_len == receivedBuf.array().length - 12); // subtract 12 for header //TODO is this correct?

        //TODO check if they arrive in order ?

        //For each packet the server receives, send pack ACK packet
        ByteBuffer ackPacket = ByteBuffer.allocate(payload_len + 12);
        header head =new header(4,clientPsecret,2,studentID); // only send back int as payload
        ByteBuffer headerBuffer =head.byteBuffer;
        ackPacket.put(headerBuffer.array());
        ackPacket.putInt(packet_id);
        stageB_packetNum++;


        //For each received data packet, server randomly decides whether to ack that packet
//        int success = (int)Math.round(Math.random());
//        if(success == 0){
//            //close the socket
//            closeUDPSocket(); //TODO change this??
//            System.out.println("close udp socket");
//        }

        DatagramPacket UDPPacket =new DatagramPacket(ackPacket.array(),ackPacket.array().length,receivedPacket.getAddress(),receivedPacket.getPort());
        System.out.println("Address "+receivedPacket.getAddress().toString()+" port "+receivedPacket.getPort());
        try {
            System.out.println("stage B send back "+udpSocket.isBound()+" packat_id :"+stageB_packetNum);
            udpSocket.send(UDPPacket);
        } catch (IOException e){
            System.err.println("Failed to send");
        }


        //once server received all packets
        if(stageB_numPackets == stageB_packetNum){
            assert(packet_id == stageB_numPackets -1); // verify from stage A the client sent the correct # packets

            ByteBuffer resp = ByteBuffer.allocate(12+4+4);
            head = new header(8,clientPsecret,2,studentID);
            Random rand = new Random();
            tcp_port = rand.nextInt(1000)+1024;
            psecretB = rand.nextInt(1000);
            resp.put(head.byteBuffer.array());
            resp.putInt(tcp_port);
            resp.putInt(psecretB);
            DatagramPacket respPacket =new DatagramPacket(resp.array(),resp.array().length,receivedPacket.getAddress(),receivedPacket.getPort());
            try {
                udpSocket.send(respPacket);
                stageC();
            } catch (IOException e){
                System.err.println("Failed to send");
            }

        }else{
            System.out.println("server does not recieve every packet_id");
        }

    }

    //    private void stageB(){
//
//    }
    private void stageC(){
        ByteBuffer resp=null;


        try{

            initializeTCPSocket(tcp_port);
            Socket clientSocket = tcpSocket.accept();


            Random rand = new Random();
            int num2 = rand.nextInt(100);
            int len2 = rand.nextInt(100);
            psecretC = rand.nextInt(1000);

            //Assign to return val
            ByteBuffer responsePacket =ByteBuffer.allocate(12+14);
            header head =new header(len2,psecretB, 2,studentID);
            responsePacket.put(head.byteBuffer.array());
            responsePacket.putInt(num2);

            responsePacket.putInt(len2);
            responsePacket.putInt(psecretC);
            responsePacket.putChar('c');
            PrintWriter pr =new PrintWriter(clientSocket.getOutputStream());
            //https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html
            pr.println(Arrays.toString(responsePacket.array()));
            pr.flush();
            System.out.println("sent to client with "+Arrays.toString(responsePacket.array()));
        }catch (IOException e){
            System.err.println("connection failed");
        }


    }
    private void stageD(){

    }


    private  void closeUDPSocket() {
        udpSocket.close();
    }

    private  void initializeTCPSocket(int tcp_port) throws IOException {
        try {
            tcpSocket = new ServerSocket();
            //InetSocketAddress address = new InetSocketAddress(HOSTNAME, tcp_port);
            //tcpSocket.connect(address, TIMEOUT);

        } catch (IOException e){
            System.err.println("Could not connect");
            System.err.println(e);
        }
    }

    private  void closeTCPSocket() throws IOException {
        in.close();
        out.close();
        tcpSocket.close();
    }
}
