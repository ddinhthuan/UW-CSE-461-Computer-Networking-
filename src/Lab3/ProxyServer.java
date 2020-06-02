package Lab3;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
        final int TIMEOUT = 15000;

        Proxy (Socket connection){
            this.connection = connection;
            printDateStamp();
           //System.out.println("Proxy listening on " + connection.getLocalSocketAddress()); //todo fix

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
            String hostAndport = hostLine.substring(6, hostLine.length()-1);
            int idx1=0;
            if(hostAndport.substring(6,hostAndport.length()-1).contains(":")) {//TODO test edge cases
                idx1 = hostAndport.indexOf(":");
                return Integer.parseInt(hostAndport.substring(idx1+1, hostAndport.length() ));
            }

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

            try{
                byte[] request_bytes = new byte[1024];
//                byte[] reply_bytes = new byte[4096];

                InputStream inFromBrowser = connection.getInputStream();
                OutputStream outToClient = connection.getOutputStream();

                //Get header from browser
                inFromBrowser.mark(0);
                int bytes_read = inFromBrowser.read(request_bytes);
                if(bytes_read == -1)
                    System.err.println("there was an error reading");

                String requestString = new String(request_bytes, StandardCharsets.UTF_8);
                HttpHeader request = new HttpHeader(requestString);

                //print out first line of each HTTP request
                // must print at least the HTTP method and URI given on the request line,
                // but you can also print the entire request line
                // (which additionally includes the HTTP version) if that's easier
                printDateStamp();
                System.out.println(" >>> " + request.getStartLine());


                //For non-CONNECT HTTP requests - edit the HTTP request header, send it
                // and any payload the request might carry to the origin server,
                // and then slightly edit the HTTP response header and
                // send it and any response payload back to the browser.

       //         System.out.println("request"+request.getHostLine().split(":")[1].toString());

                String host = request.getHost();
                int port = parsePortNum(request);
                //System.out.println("Connect to " + host + " on port " + port);

                if(!request.isConnect()){
                    // System.out.println("ENTERED GET BRANCH");


                    //open a TCP connection to server on specified port
                    Socket proxyToServer = new Socket(InetAddress.getByName(host),port);
                    proxyToServer.setSoTimeout(TIMEOUT);
                    //System.out.println("Socket connected to server? " + proxyToServer.isConnected());

                    //GET requests do not contain a message body - so request ends with a blank line
                    //todo check if it contains blank line termination
                    request = request.transformRequestHeader();

                    //send data to the server

                   byte[] data = request.getRequest().getBytes();
//                    OutputStream outToServer = proxyToServer.getOutputStream();
//                    outToServer.write(data);
//                    PrintWriter writer = new PrintWriter(outToServer, true);

                    DataOutputStream dout=new DataOutputStream(proxyToServer.getOutputStream());
                    //System.out.println("Get request"+ byteArrayToHex(data));

                    dout.write(data, 0, data.length);
                    dout.flush();


                    //System.out.println("Sent to server: ");
                    //send to client
                    Forward forward=new Forward(proxyToServer,connection);
                    forward.start();


                } else { //is connect
                    //For CONNECT HTTP requests - establish TCP connection to the server named
                    // in the request, send an HTTP success response to the browser,
                    // then simply pass through any data sent by the browser
                    // or the remote server to the other end of the communication.

                    //System.out.println("ENTERED CONNECT BRANCH");
                    //TODO Think about a cleaner way to parse host without port inside
                     int idx1 =host.indexOf(":");
                     host = host.substring(0,idx1);
                   //============================
                        Socket proxyToServer = new Socket(host,port);

                        if(!proxyToServer.isConnected()){
                            String responseToBrowser = request.getVersion() + " 502 Bad Gateway\r\n\r\n";
                            outToClient.write(responseToBrowser.getBytes());
                            return;
                        }

                      //  System.out.println("connect to server");
                        DataOutputStream out =  new DataOutputStream(outToClient);
                        out.write("HTTP/1.0 200 OK\r\n\r\n".getBytes());
                        //ut.write(request.getVersion()+" 200 OK/\r\n\r\n");


                        //send to client
                        Forward readFromServer = new Forward(proxyToServer,connection);
                        //send to server
                        Forward readFromClient = new Forward(connection,proxyToServer);
                        readFromClient.start();
                        readFromServer.start();



                    }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b) + " ");
        return sb.toString();
    }

}
