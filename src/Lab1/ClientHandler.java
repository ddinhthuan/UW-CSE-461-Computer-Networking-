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
                InputStreamReader in =new InputStreamReader(socket.getInputStream());
                //BufferReader def : https://www.geeksforgeeks.org/java-io-bufferedreader-class-java/
                BufferedReader bf =new BufferedReader(in);
                String str =bf.readLine();
                System.out.println("server : "+str);
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
//        ByteBuffer received=null;
//        ByteBuffer toreturn;
//        while(true){
//            try{
//                readAllBytes rb =new readAllBytes();
//                byte[] disBuf=rb.readAllBytes_fn(in);
//                received =ByteBuffer.wrap(disBuf);
//                if(received.equals("/0")){
//                    System.out.println("Client " + this.socket + " sends exit...");
//                    System.out.println("Closing this connection.");
//                    this.socket.close();
//                    System.out.println("Connection closed");
//                    break;
//                }
//                PrintWriter pr =new PrintWriter(socket.getOutputStream());
//                int stage =0;
//                //I tried to manage different stage using switch
//                switch(stage){
//                    case 1 :
//                        pr.println();
//                        break;
//
//                    case 2 :
//                        pr.println();
//                        break;
//                    case 3:
//                        pr.println();
//                        break;
//                    default:
//                        pr.println();
//                        break;
//
//                }
//                pr.flush();
//
//            }catch (IOException e) {
//                e.printStackTrace();
//            }
//            try
//            {
//                // closing resources
//                this.in.close();
//                this.out.close();
//
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//        }
//    }

    }
}