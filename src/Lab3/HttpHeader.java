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

}
