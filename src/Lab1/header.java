package Lab1;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.nio.*;
//From piazza
//So client send psecret 0 on the header with "hello world" on the payload
// then server send back psecret 0 on the header and secretA on the payload
// (random number generated, lets say secretA = 200), then client send back psecret = 200
// on its header?

public class header{
    ByteBuffer byteBuffer=ByteBuffer.allocate(12);
    public header(int payload_len,int psecret,int step, int studentID){
        byteBuffer.putInt(payload_len);

        System.out.println("payload"+Arrays.toString(byteBuffer.array()));
        byteBuffer.putInt(psecret);
        System.out.println("psecret"+Arrays.toString(byteBuffer.array()));
        byteBuffer.putShort((short)step);
        System.out.println("step"+Arrays.toString(byteBuffer.array()));
        byteBuffer.putShort((short)studentID);
        System.out.println("ID"+Arrays.toString(byteBuffer.array()));
    }
    public byte[] getHeader(){
        System.out.println(Arrays.toString(byteBuffer.array()));
        return byteBuffer.array();
    }
}