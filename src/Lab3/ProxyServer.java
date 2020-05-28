package Lab3;

import Test.Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProxyServer {

    private static ServerSocket tcpSocket = null;
    public static void main(String[] args) {
        //TODO uncomment when ready
        /*
        if(args.length != 2){
            throw new IllegalArgumentException("insufficient arguments");
        }
        String program = args[0];
        int port = Integer.parseInt(args[1]);
        if(port > 65535 || 1024 > port){ // todo valid range from 0 - 65535?
            throw new IllegalArgumentException("Invalid port number");
        }

        ProxyServer proxyServer = new ProxyServer(port);
        */
        int port = 1234;
        try {
            tcpSocket = new ServerSocket(port);
            //printDateStamp();
            System.out.println("Proxy listening on " + tcpSocket.getLocalSocketAddress()); //todo fix

            //System.out.println("Server started");
            //System.out.println("Waiting for a client...");
            while(true){
                //ThreadProxy threadProxy = new ThreadProxy(tcpSocket.accept(),port);
                Listener listener = new Listener(tcpSocket.accept());
                System.out.println("connected create thread");
                Thread thread = new Thread(listener);
                thread.start();
            }

            } catch (IOException e) {
            System.err.println("Could not connect");
            System.err.println(e);
        }
    }

}

    class Listener implements Runnable {
        Socket proxySocket=null;
        Listener(Socket proxySocket){
            this.proxySocket = proxySocket;
        }
        private void printDateStamp() {
            Calendar cal = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("hh:mm:ss");
            String time = df.format(new Date());
            System.out.print(Calendar.DAY_OF_MONTH + " " + cal.getDisplayName(Calendar.MONTH,
                    Calendar.LONG, Locale.getDefault()) + " " + time + " - ");
        }

        @Override
        public void run() {
            //Parse first line

            //Implement Handler
            //Coming from origin server - header from user request
            // e.g. GET hostname - parse out hostname then send to host
            //modify keep alive flag - set to false if GET request
            //e.g. CONNECT request --> set up back and forth request

            //Client sends request to web server
            Socket client = null;
            Socket server = null;
            try{
                byte[] request_bytes = new byte[1024];
                byte[] reply_bytes = new byte[4096];

                InputStream inFromClient = proxySocket.getInputStream();
                OutputStream outToClient = proxySocket.getOutputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inFromClient));
                String line= reader.readLine();
                reader.mark(0);
                reader.reset();
                System.out.println("=========================="+line+"========================");
                //connect socket to server
                if(line.contains("CONNECT")) {
                    try {
                        server = new Socket(proxySocket.getInetAddress(), 1234); //todo fix hardcoded

                        System.out.println("connect to server");
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
                        out.write("HTTP/1.1 200 OK/\r\n\r\n");
                        out.flush();
                    } catch (IOException e) {
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
                        out.flush();
                        throw new RuntimeException(e);
                    }
                }
                    InputStream inFromServer = server.getInputStream();
                    OutputStream outToServer = server.getOutputStream();


                    //Request asks server to retrieve resource - identified by URI
                    int bytes_read;
                    while ((bytes_read = inFromClient.read(request_bytes)) != -1) {
                        outToServer.write(request_bytes, 0, bytes_read);
                        String requestString = new String(request_bytes, StandardCharsets.UTF_8);
                        HttpHeader request = new HttpHeader(requestString);
                        //        System.out.println("to server-->\n" + request); //for debugging

                        //print out first line of each HTTP request
                        // must print at least the HTTP method and URI given on the request line,
                        // but you can also print the entire request line
                        // (which additionally includes the HTTP version) if that's easier
                        printDateStamp();
                        System.out.println(" >>> " + request.getStartLine());

                        //    System.out.println(request.getHostLine()); //for debugging

                        outToServer.flush();
                    }

//Server processes request - sends response
            } catch (IOException e) {
                e.printStackTrace();
            }





            //Coming from client
                    //1. Connect command
                    //2. Non connect Command


        }

    }



