package xyz.mxlei.zxing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.FormatException;

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
            case UPC_E:
                if (isAllNumber(content) && content.length() == 7) {
                    char ch = content.charAt(0);
                    if (ch == '0' || ch == '1') {
                        return true;
                    }
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
                    if (!CONTENT_CODE_39.contains(String.valueOf(ch))) {
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
            int checkBit = getCheckBit(content);
            return content + checkBit;
        }
        return null;
    }

    /**
     * 获取EAN、UPC条码的校验位
     */
    public static int getCheckBit(String content) {
        try {
            int length = content.length();
            int sum = 0;
            for (int i = length - 1; i >= 0; i -= 2) {
                int digit = content.charAt(i) - '0';
                if (digit < 0 || digit > 9) {
                    throw FormatException.getFormatInstance();
                }
                sum += digit;
            }
            sum *= 3;
            for (int i = length - 2; i >= 0; i -= 2) {
                int digit = content.charAt(i) - '0';
                if (digit < 0 || digit > 9) {
                    throw FormatException.getFormatInstance();
                }
                sum += digit;
            }
            return (1000 - sum) % 10;
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
