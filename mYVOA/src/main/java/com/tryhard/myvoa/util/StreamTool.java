package com.tryhard.myvoa.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Chen on 2015/11/3.
 */
public class StreamTool {
    public static OutputStream getOutputStream(InputStream in) {
        byte[] buffer = new byte[1024];
        int length = 0;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            while((length = in.read(buffer)) != -1){
                os.write(buffer,0,length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os;
    }
}
