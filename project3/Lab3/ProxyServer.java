package Lab3;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import static Lab3.HttpHeader.printDateStamp;


public class ProxyServer {

    public static void main(String[] args){
        //TODO uncomment when ready
	//        int port = 1234;
        
        if(args.length != 1){
            throw new IllegalArgumentException("insufficient arguments");
        }
        
        int port = Integer.parseInt(args[0]);
        if(port > 65535 || 1024 > port){ // todo valid range from 0 - 65535?
            throw new IllegalArgumentException("Invalid port number");
        }

        

        try (ServerSocket tcpSocket = new ServerSocket(port)) {
            printDateStamp();
            System.out.println("Proxy listening on " + tcpSocket.getLocalSocketAddress());
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
        final int TIMEOUT = 20000; //todo use timeout to avoid hanging on recv

        Proxy (Socket connection){
            this.connection = connection;
        }

        private int parsePortNum(HttpHeader request){
            //parse first line and host line for port num
            // if no port specified - use 443 if https:// or 80 otherwise

            //edge case e.g. "Host: http://google.com:32"
            //edge case e.g. "POST http://google.com/ "

            String hostLine = request.getHostLine();  //e.g. Host: firefox-settings-attachments.cdn.mozilla.net:443
            String hostAndport = hostLine.substring(6, hostLine.length()-1); //e.g. firefox-settings-attachments.cdn.mozilla.net:443
            int idx1=0;
            if(hostAndport.contains("http://") || hostAndport.contains("https://")  ){
                idx1 = 7; // just needs to be larger than 5 or 6
            }
            if(hostAndport.contains(":")) {
                idx1 = hostAndport.indexOf(":");
                return Integer.parseInt(hostAndport.substring(idx1+1, hostAndport.length() ));
            }


            // look for one in the request line
            String firstLine = request.getStartLine(); //e.g. CONNECT content-signature-2.cdn.mozilla.net:443 HTTP/1.1
            idx1 = 0; //beginning boundary
            if(firstLine.contains("http://") || firstLine.contains("https://")  ){
                idx1 = firstLine.indexOf(":")+1; // find first occurrence
            }

            int version_idx = firstLine.length()-1; //end boundary
            if(firstLine.toLowerCase().contains("http/")) {
                version_idx = firstLine.toLowerCase().indexOf("http/");
            }

            String modifiedFirstLine = firstLine.substring(idx1, version_idx);
            if(modifiedFirstLine.contains(":")) {
                return Integer.parseInt(modifiedFirstLine);
            }


            if(hostLine.toLowerCase().contains("https://"))
                return 443;

            return 80;
        }

        @Override
        public void run() {
            //Coming from origin server - header from user request
            // e.g. GET hostname - parse out hostname then send to host
            //e.g. CONNECT request --> set up back and forth request

            try{
                byte[] request_bytes = new byte[1024];

                InputStream inFromBrowser = connection.getInputStream();
                OutputStream outToClient = connection.getOutputStream();

                //Get header from browser
                inFromBrowser.mark(0);
                int bytes_read = inFromBrowser.read(request_bytes);
                if(bytes_read == -1) {
//                    System.err.println("there was an error reading"); // delete
                    connection.close();
                    inFromBrowser.close();
                    outToClient.close();
                }

                String requestString = new String(request_bytes, StandardCharsets.UTF_8);
                HttpHeader request = new HttpHeader(requestString);

                //print out first line of each HTTP request
                // must print at least the HTTP method and URI given on the request line,
                // but you can also print the entire request line
                // (which additionally includes the HTTP version) if that's easier
                printDateStamp();
                System.out.println(">>> " + request.getStartLine());


                //For non-CONNECT HTTP requests - edit the HTTP request header, send it
                // and any payload the request might carry to the origin server,
                // and then slightly edit the HTTP response header and
                // send it and any response payload back to the browser.

                String host = request.getHost();
                int port = parsePortNum(request);

//                if(port != 80)
//                    System.out.println("Connect to " + host + " ON PORT " + port);

                //TODO Think about a cleaner way to parse host without port inside
                //edge case: portquiz.net:12
                if(host.contains(":")) {
                    int idx1 = host.indexOf(":");
                    host = host.substring(0, idx1);
                }

                //open a TCP connection to server on specified port
                Socket proxyToServer = new Socket(InetAddress.getByName(host),port);
            //    proxyToServer.setSoTimeout(TIMEOUT); //todo - read time out error

                // branch code depending on request type
                if(!request.isConnect()){
                    //GET requests do not contain a message body - so request ends with a blank line
                    request = request.transformRequestHeader();

                    byte[] data = request.getRequest().getBytes();

                    //send data to the server
                    DataOutputStream dout=new DataOutputStream(proxyToServer.getOutputStream());
                    dout.write(data, 0, data.length);
                    dout.flush();

                    //send to client
                    Forward forward=new Forward(proxyToServer,connection);
                    forward.start();

                } else { //is connect
                    //For CONNECT HTTP requests - establish TCP connection to the server named
                    // in the request, send an HTTP success response to the browser,
                    // then simply pass through any data sent by the browser
                    // or the remote server to the other end of the communication.

                    if(!proxyToServer.isConnected()){
                        String responseToBrowser = request.getVersion() + " 502 Bad Gateway\r\n\r\n";
                        outToClient.write(responseToBrowser.getBytes());
                        return;
                    }

                  //send back HTTP 200 OK
                    DataOutputStream out =  new DataOutputStream(outToClient);
                    out.write("HTTP/1.0 200 OK\r\n\r\n".getBytes());

                    //Establish bidirectional channel for both sides to send simultaneously
                    //One thread reads from server socket and sends to client
                    Forward readFromServer = new Forward(proxyToServer,connection);
                    //other reads from client socket and sends to serve
                    Forward readFromClient = new Forward(connection,proxyToServer);
                    readFromClient.start();
                    readFromServer.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            //todo finally close sockets and streams
        }
    }

}
