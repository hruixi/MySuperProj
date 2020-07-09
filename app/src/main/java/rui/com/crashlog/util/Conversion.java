package rui.com.crashlog.util;


import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

public class Conversion {
    /**
     * 十六进制字符串转为Byte数组,每两个十六进制字符转为一个Byte
     *
     * @param hexString 十六进制字符串
     * @return 转换结果
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        int length = hexString.length() / 2;
        // toCharArray将此字符串转换为一个新的字符数组。
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    //charToByte返回在指定字符的第一个发生的字符串中的索引，即返回匹配字符  '1'
    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }


    public static short bytes2Short2(byte[] b) {
        short i = (short) (((b[1] & 0xff) << 8) | b[0] & 0xff);
        return i;
    }

    //将字节数组转换为16进制字符串

    /**
     * 将字节数组转换为16进制字符串，格式如： 1c 0a dd ff
     * @param bytes 待转化的字节数组
     * @return 16进制字符串
     */
    public static String BinaryToHexString(byte[] bytes) {
        String hexStr = "0123456789abcdef";
        String result = "";
        String hex = "";
        for (byte b : bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            result += hex + " ";
        }
        return result;
    }

    /**
     * 将字节数组转换为16进制字符串，格式如： 1c 0a dd ff
     * @param bytes 待转化的字节数组
     * @param length  有效字符数组的长度
     * @return 16进制字符串
     */
    public static String BinaryToHexString(byte[] bytes, int length) {
        String hexStr = "0123456789abcdef";
        String result = "";
        String hex = "";
        for (int i = 0; i < length; i++) {
            hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
            result += hex + " ";
        }
        return result;
    }

    public String printHexString(byte[] b) {
        String strHex = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            strHex += hex + " ";
        }
        return strHex;
    }

    //java ip转16进制
    public static String ipToLong(String ipString) {
        String[] ip = ipString.split("\\.");
        StringBuffer sb = new StringBuffer();
        for (String str : ip) {
            String strs = Integer.toHexString(Integer.parseInt(str));
            if (!(strs.length() % 2 == 0)) {
                strs = "0" + Integer.toHexString(Integer.parseInt(str));
            }
            sb.append(strs);
        }
        return sb.toString();
    }

    public static byte[] ipv4Address2BinaryArray(String ipAdd) {
        byte[] binIP = new byte[4];
        String[] strs = ipAdd.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            binIP[i] = (byte) Integer.parseInt(strs[i]);
        }
        return binIP;
    }

    public static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public byte[] byteStitching(String methodHeads, String deviceIds, String states) throws UnsupportedEncodingException {
        int length = states.length();
        DecimalFormat df = new DecimalFormat("0000");
        String formatLength = df.format(length);
        byte[] methodHead = new Conversion().HexString2Bytes(methodHeads + deviceIds + formatLength);
        byte[] state = states.getBytes("ASCII");
        byte[] msg = new byte[methodHead.length + state.length];
        System.arraycopy(methodHead, 0, msg, 0, methodHead.length);
        System.arraycopy(state, 0, msg, methodHead.length, state.length);
        return msg;
    }


}
