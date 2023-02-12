package com.huihuang.queryfile.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.BitSet;

/**
 *  编码集工具类
 * @author: JiangJi
 * @Descriotion:
 * @Date:Created in 2023/2/12 18:16
 */
public class EncodeUtil {

    private static int BYTE_SIZE = 8;
    public static String CODE_UTF8 = "UTF-8";
    public static String CODE_UTF8_BOM = "UTF-8_BOM";
    public static String CODE_GBK = "GBK";
    public static String CODE_UNICODE = "Unicode";
    public static String CODE_UTF16 = "UTF-16";

    /**
     * 通过文件获取编码集名称
     *
     * @param file
     * @param ignoreBom
     * @return
     * @throws Exception
     */
    public static String getEncode(File file, boolean ignoreBom) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            try (BufferedInputStream bis = new BufferedInputStream(fileInputStream)){
                return getEncode(bis, ignoreBom);
            }
        }
    }

    /**
     * 通过文件缓存流获取编码集名称
     *
     * @param bis
     * @return
     * @throws Exception
     */
    public static String getEncode(BufferedInputStream bis, boolean ignoreBom) throws Exception {
        bis.mark(0);

        String encodeType = StringUtils.EMPTY;
        byte[] head = new byte[3];
        bis.read(head);
        if (head[0] == -1 && head[1] == -2) {
            encodeType = CODE_UTF16;
        } else if (head[0] == -2 && head[1] == -1) {
            encodeType = CODE_UNICODE;
        } //带BOM
        else if (head[0] == -17 && head[1] == -69 && head[2] == -65) {
            if (ignoreBom) {
                encodeType = CODE_UTF8;
            } else {
                encodeType = CODE_UTF8_BOM;
            }
        } else if (CODE_UNICODE.equals(encodeType)) {
            encodeType = CODE_UTF16;
        } else if (isUTF8(bis)) {
            encodeType = CODE_UTF8;
        } else {
            encodeType = CODE_GBK;
        }
        return encodeType;
    }

    /**
     * 是否是无BOM的UTF8格式，不判断常规场景，只区分无BOM UTF8和GBK
     *
     * @param bis
     * @return
     */
    private static boolean isUTF8(BufferedInputStream bis) throws Exception {
        bis.reset();
        int code = bis.read();
        do {
            BitSet bitSet = convert2BitSet(code);
            //判断是否为单字节
            if (bitSet.get(0)) {
                //多字节时，再读取N个字节
                if (!checkMultiByte(bis, bitSet)) {
                    return false;
                }
            } else {
                //单字节时什么都不用做，再次读取字节
            }
            code = bis.read();
        } while (code != -1);
        return true;
    }

    /**
     * 将整形转为BitSet
     *
     * @param code
     * @return
     */
    private static BitSet convert2BitSet(int code) {
        BitSet bitSet = new BitSet(BYTE_SIZE);

        for (int i = 0; i < BYTE_SIZE; i++) {
            int tmp3 = code >> (BYTE_SIZE - i - 1);
            int tmp2 = 0x1 & tmp3;
            if (tmp2 == 1) {
                bitSet.set(i);
            }
        }
        return bitSet;
    }

    /**
     * 检测多字节，判断是否为utf8，已经读取了一个字节
     *
     * @param bis
     * @param bitSet
     * @return
     */
    private static boolean checkMultiByte(BufferedInputStream bis, BitSet bitSet) throws Exception {
        int count = getCountOfSequential(bitSet);
        //已经读取了一个字节，不能再读取
        byte[] bytes = new byte[count - 1];
        bis.read(bytes);
        for (byte b : bytes) {
            if (!checkUtf8Byte(b)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测bitSet中从开始有多少个连续的1
     *
     * @param bitSet
     * @return
     */
    private static int getCountOfSequential(BitSet bitSet) {
        int count = 0;
        for (int i = 0; i < BYTE_SIZE; i++) {
            if (bitSet.get(i)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * 检测单字节，判断是否为utf8
     *
     * @param b
     * @return
     */
    private static boolean checkUtf8Byte(byte b) {
        BitSet bitSet = convert2BitSet(b);
        return bitSet.get(0) && !bitSet.get(1);
    }

}
