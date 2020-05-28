package Lab3;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProxyServer {

    public static void main(String[] args){
        //TODO uncomment when ready
        int port = 1234;
        /*
        if(args.length != 2){
            throw new IllegalArgumentException("insufficient arguments");
        }
        String program = args[0];
        int port = Integer.parseInt(args[1]);
        if(port > 65535 || 1024 > port){ // todo valid range from 0 - 65535?
            throw new IllegalArgumentException("Invalid port number");
        }

        */

        try (ServerSocket tcpSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket connection = tcpSocket.accept();
                    Thread task = new Proxy(connection);
                    task.start();
                } catch (IOException ex) {
                }
            }
        }catch (IOException ex){
            System.err.println("Couldn't start server");
        }
    }


    private static class Proxy extends Thread {
        private Socket connection;
        Proxy (Socket connection){
            this.connection = connection;
            printDateStamp();
            System.out.println("Proxy listening on " + connection.getLocalSocketAddress()); //todo fix
        }

        private void printDateStamp() {
            Calendar cal = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("hh:mm:ss");
            String time = df.format(new Date());
            System.out.print(Calendar.DAY_OF_MONTH + " " + cal.getDisplayName(Calendar.MONTH,
                    Calendar.LONG, Locale.getDefault()) + " " + time + " - ");
        }

        private int parsePortNum(HttpHeader request){
            //parse first line and host line for port num
            // if no port specified - use 443 if https:// or 80 otherwise

            String hostLine = request.getHostLine();
            if(hostLine.substring(5, hostLine.length()-1).contains(":")) //TODO test edge cases
                return Integer.parseInt(hostLine);

            // look for one in te request line
//            String firstLine = request.getStartLine();
//            if(firstLine.contains(":")) //TODO fix logic
//                return Integer.parseInt(firstLine);

            if(hostLine.toLowerCase().contains("https://"))
                return 443;

            return 80;
        }

        @Override
        public void run() {
            //Implement Handler
            //Coming from origin server - header from user request
            // e.g. GET hostname - parse out hostname then send to host
            //e.g. CONNECT request --> set up back and forth request

            //Client sends request to web server
            Socket client = null;
            Socket server = null;
            try{
                byte[] request_bytes = new byte[1024];
                byte[] reply_bytes = new byte[4096];

                InputStream inFromClient = connection.getInputStream();
                OutputStream outToClient = connection.getOutputStream();


//                InputStream inFromServer = server.getInputStream();
             //   OutputStream outToServer = server.getOutputStream();


                //Request asks server to retrieve resource - identified by URI
                int bytes_read;
                HttpHeader request = null;
                while((bytes_read = inFromClient.read(request_bytes)) != -1){
                    outToClient.write(request_bytes, 0, bytes_read);
                    String requestString = new String(request_bytes, StandardCharsets.UTF_8);
                    request = new HttpHeader(requestString);
                    //System.out.println("to server-->\n" + request.getRequest()); //for debugging

                    //print out first line of each HTTP request
                    // must print at least the HTTP method and URI given on the request line,
                    // but you can also print the entire request line
                    // (which additionally includes the HTTP version) if that's easier
                    printDateStamp();
                    System.out.println(" >>> " + request.getStartLine());
                    outToClient.flush();
                }
                assert(request != null);

                //For non-CONNECT HTTP requests - edit the HTTP request header, send it
                // and any payload the request might carry to the origin server,
                // and then slightly edit the HTTP response header and
                // send it and any response payload back to the browser.
                if(!request.isConnect()){

                    System.out.println(request.getHostLine()); //for debugging
                   int port = parsePortNum(request);
                    System.out.println("Port: " + port); //debugging

                    request = request.transformRequestHeader();

                    //todo open a connection and send to browser

                } else { //is connect
                    //For CONNECT HTTP requests - establish TCP connection to the server named
                    // in the request, send an HTTP success response to the browser,
                    // then simply pass through any data sent by the browser
                    // or the remote server to the other end of the communication.
                    //TODO

                    System.err.println("Not yet implemented");
                    /*
                    try {
                        server = new Socket(connection.getInetAddress(), 1234); //todo fix hardcoded

                        System.out.println("connect to server");
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
                        out.write("HTTP/1.1 200 OK/\r\n\r\n");
                        out.flush();
                    } catch (IOException e) {
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
                        out.flush();
                        throw new RuntimeException(e);
                    }

                     */

                }




            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
