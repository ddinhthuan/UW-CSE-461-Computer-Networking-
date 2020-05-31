package Lab3;

public class HttpHeader {
    private String request= null;

    public HttpHeader(String req){
        this.request = req;
    }

    public String getStartLine() {
        int idx = request.indexOf('\n');
        return request.substring(0, idx);
    }

    public String getHostLine() {
        int idx = request.toLowerCase().indexOf("host"); // insensitive to case of the keyword Host
        String tmp = request.substring(idx, request.length()-1);
        //System.out.println("tmp: " + tmp);
        int idx2 = tmp.indexOf('\n');
        return tmp.substring(0, idx2);
    }

    public HttpHeader transformRequestHeader() {
        // filter the request and response streams,
        // removing any Connection: keep-alive, inserting a Connection: close, and
        // converting any Proxy-connection: keep-alive to Proxy-connection: close.

        // lower the HTTP version of the request to HTTP 1.0
         HttpHeader tmp = new HttpHeader(request
                 .replace("/1.1", "/1.0")
                 .replace("keep-alive", "close"));

       //  System.out.println("MODIFIED HEADER\n" + tmp.getRequest());
        return tmp;
    }

    public boolean isConnect(){
        //blank space necessary or else "CONNECTion"
        return request.toLowerCase().contains("connect ");
    }

    public String getRequest() {

        return request;
    }
    public String getVersion(){
        int idx = request.toLowerCase().indexOf("http/"); // insensitive to case of the keyword Host
        return request.substring(idx,idx+8);
    }
    public String getHost(){
        return getHostLine().split(": ")[1].toString().split(":")[0];
    }
    public int parsePortNum(){
        //parse first line and host line for port num
        // if no port specified - use 443 if https:// or 80 otherwise

        String hostLine = getHostLine();
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

}
