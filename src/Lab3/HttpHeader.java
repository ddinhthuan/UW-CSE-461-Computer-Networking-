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

    public String modifyKeepAlive() {
        // filter the request and response streams,
        // removing any Connection: keep-alive, inserting a Connection: close, and
        // converting any Proxy-connection: keep-alive to Proxy-connection: close.

        return request.replace("keep-alive", "close");
    }

    public String getRequest() {
        return request;
    }

}
