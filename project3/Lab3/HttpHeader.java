package Lab3;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HttpHeader {
    private String request= null;

    public HttpHeader(String req){
        this.request = req;
    }

    public String getStartLine() {
	int idx = 0;
	idx = request.indexOf('\n');
        if(idx == -1){
            idx = request.length();
        }
        
        return request.substring(0, idx);
    }

    public String getHostLine() {
        int idx = request.toLowerCase().indexOf("host"); // insensitive to case of the keyword Host
	if (idx == -1) 
	    return null;
        
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
        String hostline = getHostLine();
	if(hostline == null)
	    return hostline;
        
	int start = hostline.toLowerCase().indexOf("host:");
        return hostline.substring(start+6, hostline.length()-1);
    }

    public static void printDateStamp() {
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("hh:mm:ss");
        String time = df.format(new Date());
        System.out.print(Calendar.DAY_OF_MONTH + " " + cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG, Locale.getDefault()) + " " + time + " - ");
    }
}
