package com.tim.system.server.netty;

import java.nio.ByteBuffer;

/**
 * Created by eminxta on 2016/07/27.
 */
public class LoopBackTimeStamp {
    private long sendTimeStamp;
    private long recvTimeStamp;

    public LoopBackTimeStamp(){
        this.sendTimeStamp = System.nanoTime();
    }

    public byte[] toByteArray(){
        final int byteOfLong = Long.SIZE / Byte.SIZE;
        byte[] ba = new byte[byteOfLong * 2];
        byte[] t1 = ByteBuffer.allocate(byteOfLong).putLong(sendTimeStamp).array();
        byte[] t2 = ByteBuffer.allocate(byteOfLong).putLong(recvTimeStamp).array();
        return null;
    }
}
