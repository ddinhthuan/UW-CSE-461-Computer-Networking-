package Lab1.Part2;


//import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
//import sun.security.x509.IPAddressName;

import Lab1.header;

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
    private Socket clientSocket = null;

    private static OutputStream bufferedOutputStream =null;
    private static InputStream bufferdInputStream =null;

    //    final  socket;
    int psecretA =0;
    int psecretB =Integer.MAX_VALUE;
    int psecretC =Integer.MAX_VALUE;
    private static int stageB_packetNum = 0;
    private static int stageB_numPackets;
    private static char stageC_char;

    private static int stageC_numPackets;
    private static int stageC_packetLen;
  //  private static int stageB_packetLen;

    private static final int TIMEOUT = 1000;
    private static final String HOSTNAME = "localhost";
    private static InputStream in =null;
    private static OutputStream out =null;

    private boolean sentTCPPacket = false;

    int tcp_port =0;
    short studentID=0;


    public ClientHandler(DatagramSocket udpSocket,int psecretA,int num) {

        this.udpSocket =udpSocket;
        this.psecretA  =psecretA;
        stageB_numPackets =num;

    }

    @Override
    public void run() {
        byte[] received=new byte[10000];//need to double check here
        DatagramPacket receivedPacket = null;

        int clientPsecret;
        while (true) {
            try {

                if (!sentTCPPacket) {
                    // receive the answer from client
                    receivedPacket = new DatagramPacket(received, received.length);// not sure how large I should assign here

                    udpSocket.receive(receivedPacket);
                    System.out.println("is bound " + udpSocket.isBound());
                    ByteBuffer receivedBuf = ByteBuffer.wrap(receivedPacket.getData());
                    System.out.println(Arrays.toString(receivedBuf.array()));
                    int payload_len = receivedBuf.getInt(0);
                    clientPsecret = receivedBuf.getInt(4);
                    short step = receivedBuf.getShort(8);
                    short studentID = receivedBuf.getShort(10);
                    if (step != 1) {
                        if (udpSocket != null && udpSocket.isBound()) closeUDPSocket();
                        if (tcpSocket != null && tcpSocket.isBound()) closeTCPSocket();
                        System.out.println("break the connection because step !=1");
                        break;
                    }

                    if (clientPsecret == psecretA) {
                        stageB(receivedPacket);
                    }

                } else { // expecting TCP packet
                    System.out.println("Receiving client packet for part D");
                    in = clientSocket.getInputStream();
                    bufferdInputStream = new BufferedInputStream(in);
                    bufferdInputStream.mark(0);
                    //read your bufferdInputStream
                    bufferdInputStream.reset();
                    //read it again
                    byte[] data = new byte[1000];
                    int count = bufferdInputStream.read(data);

                    byte[] real =new byte[count+1];
                    if (count >= 0) System.arraycopy(data, 1, real, 1, count);
                    System.out.println("Response from client: " + Arrays.toString(real));

                    ByteBuffer resp = ByteBuffer.wrap(real);
                    bufferdInputStream.reset();

                    int payload_len = resp.getInt(0);
                    clientPsecret = resp.getInt(4);
                    short step = resp.getShort(8);
                    short studentID = resp.getShort(10);
                    if (clientPsecret == psecretC) {
                        stageD(resp);
                    }
                    break;
                }
            }catch (IOException e) {
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

  //      assert(payload_len == stageB_packetLen + 4);
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
//            break; //TODO change this??
//
//        }

        DatagramPacket UDPPacket =new DatagramPacket(ackPacket.array(),ackPacket.array().length,receivedPacket.getAddress(),receivedPacket.getPort());
        System.out.println("Address "+receivedPacket.getAddress().toString()+" port "+receivedPacket.getPort());
        try {
            System.out.println("stage B send back "+udpSocket.isBound()+" packet_id :"+stageB_packetNum);
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
                //after stage b, the server just opens up TCP socket and
                stageC();
            } catch (IOException e){
                System.err.println("Failed to send");
            }

        }else{
            System.out.println("server does not receive every packet_id");
        }

    }

    private void stageC(){
        //listens for an incoming client connection, as soon as it accepts that connection it will send part C
        System.out.println("Starting stage C");

        try{
            //initializeTCPSocket(tcp_port);
            tcpSocket = new ServerSocket(tcp_port);
            System.out.println("Server started");
            System.out.println("Waiting for a client...");

            clientSocket = tcpSocket.accept(); //blocks
            System.out.println("client connected: " + clientSocket.isConnected());

            //Send data

            Random rand = new Random();
            stageC_numPackets = rand.nextInt(20)+1;
            stageC_packetLen = rand.nextInt(25)+1;
            psecretC = rand.nextInt(1000)+1;
          //  char c =(char)(rand.nextInt(95) + 32);
            stageC_char = (char)(rand.nextInt(255) + 'a');
            System.out.println("Character c: " + stageC_char);

            //Assign to return val
            ByteBuffer responsePacket =ByteBuffer.allocate(12+16); //28 bytes
            header head =new header(13,psecretB, 2,studentID);
            responsePacket.put(head.byteBuffer.array());
            responsePacket.putInt(stageC_numPackets);
            responsePacket.putInt(stageC_packetLen);
            responsePacket.putInt(psecretC);
            responsePacket.putChar(stageC_char);
            //padding should be 0s

            System.out.println("num2: " + stageC_numPackets + " len2: " + stageC_packetLen +
                    " secret C: " + psecretC + " c: " + stageC_char);

            DataOutputStream dout=new DataOutputStream(clientSocket.getOutputStream());

            dout.write(responsePacket.array(), 0, responsePacket.array().length);
            dout.flush();

            System.out.println("sent to client with "+Arrays.toString(responsePacket.array()));
            System.out.println("Sent " + responsePacket.array().length + " bytes to client");
            sentTCPPacket = true;
        }catch (IOException e){
            System.err.println("connection failed");
        }

    }

    private void stageD(ByteBuffer receivedPacket) throws IOException {
        System.out.println("Entered stage D branch");
        System.out.println("Size of received packet: " + receivedPacket.array().length);
        int payload_len = receivedPacket.getInt(0);
       // clientPsecret = receivedPacket.getInt(4);
        short step = receivedPacket.getShort(8);
        short studentID = receivedPacket.getShort(10);


        //TODO
        boolean correctMessage = true;
/*
        byte[] info = receivedPacket.array();
        while(correctMessage) {
            for (int i = 12; i < 12 + stageC_packetLen; i++) {
                if (info[i] != Character.getNumericValue(stageC_char)) {
                    correctMessage = false;
                    break;
                }
            }
        }
        System.out.println("Exepected? " + stageC_packetLen * stageC_numPackets);

        System.out.println("Packet len: " + stageC_packetLen);
        System.out.println("Last byte: " + receivedPacket.array()[12+stageC_packetLen]);



 */
        //Send last packet to client
        if(correctMessage){
            Random rand = new Random();
            int psecretD = rand.nextInt(1000)+1;

            ByteBuffer responsePacket =ByteBuffer.allocate(12+4); //28 bytes
            header head =new header(4,psecretC, 2,studentID);
            responsePacket.put(head.byteBuffer.array());
            responsePacket.putInt(psecretD);

            DataOutputStream dout=new DataOutputStream(clientSocket.getOutputStream());

            dout.write(responsePacket.array(), 0, responsePacket.array().length);
            dout.flush();

            System.out.println("sent to client with "+Arrays.toString(responsePacket.array()));

        }

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
