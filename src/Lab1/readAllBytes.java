package Lab1;

//import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class readAllBytes {
    public byte[] readAllBytes_fn(InputStream inputStream) throws IOException {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1) {
                outputStream.write(buf, 0, readLen);
                System.out.println("read len "+readLen);
            }

            System.out.println("output stream "+ Arrays.toString( outputStream.toByteArray())+" read len "+readLen);

            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        }
//        finally {
//            if (exception == null) inputStream.close();
//            else try {
//                inputStream.close();
//            } catch (IOException e) {
//                exception.addSuppressed(e);
//            }
//        }
    }
}