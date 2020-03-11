package xyz.mxlei.zxing;

import com.google.zxing.BarcodeFormat;

import static com.google.zxing.BarcodeFormat.EAN_13;
import static com.google.zxing.BarcodeFormat.EAN_8;
import static com.google.zxing.BarcodeFormat.ITF;
import static com.google.zxing.BarcodeFormat.UPC_A;

/**
 * @author mxlei
 * @date 2020/3/3
 */
public class BarCodeVerifier {

    public static final String CONTENT_CODE_39 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%";
    /**
     * 检查条码内容的格式是否正确
     */
    public static boolean verifyBarcode(String content, BarcodeFormat format) {
        if (content == null && content.length() < 1) {
            return false;
        }
        switch (format) {
            case EAN_8:
                if (isAllNumber(content) && content.length() == 7) {
                    return true;
                } else {
                    return false;
                }
            case EAN_13:
                if (isAllNumber(content) && content.length() == 12) {
                    return true;
                } else {
                    return false;
                }
            case UPC_A:
                if (isAllNumber(content) && content.length() == 11) {
                    return true;
                } else {
                    return false;
                }
            case ITF:
                if (isAllNumber(content) && content.length() == 13) {
                    return true;
                } else {
                    return false;
                }
            case CODE_39:
                //共43个字符：26个大写字母、10个数字0-9，7特殊符号：-. $/+%
                for (char ch : content.toCharArray()) {
                    if(!CONTENT_CODE_39.contains(String.valueOf(ch))){
                        return false;
                    }
                }
                return true;
            case CODE_128:
                //ASCII 0-127共128个字符
                for (char ch : content.toCharArray()) {
                    if (ch < 0 || ch > 127) {
                        return false;
                    }
                }
                return true;
        }
        return true;
    }

    /**
     * 获取完整的条码内容（添加校验位）
     *
     * @return 成功返回内容，失败返回NULL
     */
    public static String getFullCode(String content, BarcodeFormat format) {
        if (verifyBarcode(content, format)) {
            int checkBit = getCheckBit(content, format);
            return content + checkBit;
        }
        return null;
    }

    /**
     * 获取条码的校验位
     */
    public static int getCheckBit(String content, BarcodeFormat format) {
        try {
            char[] array = content.toCharArray();
            double ou = 0;
            double ji = 0;
            for (int i = 1; i <= array.length; i++) {
                if (i % 2 == 0) {
                    ou += array[i - 1] - '0';
                } else {
                    ji += array[i - 1] - '0';
                }
            }
            if (format == EAN_8 || format == UPC_A || format == ITF) {
                ji = ji * 3;
            } else if (format == EAN_13) {
                ou = ou * 3;
            }
            double sum = ji + ou;
            int check = (10 - (int) sum % 10) % 10;
            return check;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static boolean isAllNumber(String content) {
        for (char ch : content.toCharArray()) {
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    private static boolean isAllNumberOrLetter(String content) {
        for (char ch : content.toCharArray()) {
            if (ch < '0' || ch > 'Z') {
                return false;
            }
        }
        return true;
    }
}
