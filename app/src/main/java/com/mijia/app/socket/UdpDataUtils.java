package com.mijia.app.socket;

import android.util.Log;

import java.io.UnsupportedEncodingException;

public class UdpDataUtils {


    /**
     * 获取数据string
     *
     * @param cmd
     * @param all
     * @param index
     * @param json
     * @return
     */
    public static String getData(byte cmd, int all, int index, String json) {
        return new String(getDataByte(cmd, all, index, json));
    }


    /**
     * 获取数据byte[]
     *
     * @param cmd
     * @param all
     * @param index
     * @param json
     * @return
     */
    public static byte[] getDataByte(byte cmd, int all, int index, String json) {

        byte[] databyte = null;
        try {
            databyte = json.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (databyte == null) {
            databyte = new byte[0];
        }
        byte[] headByte = new byte[15];
        headByte[0] = cmd;
        //all
        byte[] allBytes = intToBytes(all);
        headByte[1] = allBytes[0];
        headByte[2] = allBytes[1];
        headByte[3] = allBytes[2];
        headByte[4] = allBytes[3];
        //index
        byte[] indexBytes = intToBytes(index);
        headByte[5] = indexBytes[0];
        headByte[6] = indexBytes[1];
        headByte[7] = indexBytes[2];
        headByte[8] = indexBytes[3];
        //lenght
        byte[] lenghtbytes = intToBytes(databyte.length);
        headByte[9] = lenghtbytes[0];
        headByte[10] = lenghtbytes[1];
        headByte[11] = lenghtbytes[2];
        headByte[12] = lenghtbytes[3];
        //crc
        byte[] crcBytes = setParamCRC(databyte);
        headByte[13] = crcBytes[0];
        headByte[14] = crcBytes[1];

        //allByte
//        byte[] bytes = (new String(headByte) + new String(databyte)).getBytes();
        byte[] bytes = byteMerger(headByte, databyte);

//        System.out.println("--- upd_send " + cmd + "  " + json);
        Log.i("UDP_SEND", cmd + "json=====" + json.toString());
        return bytes;
    }


    /**
     * 拼接两个byte数组
     *
     * @param byte_1
     * @param byte_2
     * @return
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    /**
     * 获取byte[] crc校验码
     *
     * @param buf
     * @return
     */
    public static byte[] setParamCRC(byte[] buf) {
        int MASK = 0x0001, CRCSEED = 0x0810;
        int remain = 0;

        byte val;
        for (int i = 0; i < buf.length; i++) {
            val = buf[i];
            for (int j = 0; j < 8; j++) {
                if (((val ^ remain) & MASK) != 0) {
                    remain ^= CRCSEED;
                    remain >>= 1;
                    remain |= 0x8000;
                } else {
                    remain >>= 1;
                }
                val >>= 1;
            }
        }

        byte[] crcByte = new byte[2];
        crcByte[0] = (byte) ((remain >> 8) & 0xff);
        crcByte[1] = (byte) (remain & 0xff);

        // 将生成的CRC返回
        return crcByte;
    }


    /**
     * int 转byte[] 高位在前 低位在后
     *
     * @param value
     * @return
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     *   
     *     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes（）配套使用 
     *     
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }


    /**
     * int 转 byte[] 两位  与 bytesToInt2 配套使用
     *
     * @param value
     * @return
     */
    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * byte【】 转 int intToBytes2 配套使用
     *
     * @param src
     * @param offset
     * @return
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) (
                ((src[offset] & 0xFF) << 8)
                        | (src[offset + 1] & 0xFF));
        return value;
    }
}
