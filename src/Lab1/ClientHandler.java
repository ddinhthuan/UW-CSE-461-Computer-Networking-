package Lab1;


import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;

public class ClientHandler extends Thread{
    final Socket socket;
    final InputStream dis;
    final OutputStream dos;
    public ClientHandler(Socket socket, InputStream in, OutputStream out){

        this.socket =socket;
        this.dis = in;
        this.dos =out;
    }
    @Override
    public void run() {
        byte[] received;
        String toreturn;
        while (true) {
            try {

                // Ask user what he wants

                // receive the answer from client
                readAllBytes rb = new readAllBytes();
                received = rb.readAllBytes_fn(dis);
                ByteBuffer receivedBuf =ByteBuffer.wrap(received);
                int payload=receivedBuf.getInt(0);
                int psecret=receivedBuf.getInt(4);
                int step=receivedBuf.getShort(8);
                int studentID=receivedBuf.getShort(10);



                if (received.equals("/0")) {
                    System.out.println("Client " + this.socket + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.socket.close();
                    System.out.println("Connection closed");
                    break;
                }

                // creating Date object

                // write on output stream based on the
                // answer from the client
                PrintWriter pr = new PrintWriter(socket.getOutputStream());
                pr.println("Exit");
                pr.flush();
//                switch (received) {
//
//                    case "Date" :
//                        toreturn = "000";
//
//                        break;
//
//                    case "Time" :
//                        break;
//
//                    default:
//                        break;
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void stageA(){

    }
    private void stageB(){

    }
    private void stageC(){

    }
    private void stageD(){

    }
    private void stopConnection(){

    }
}