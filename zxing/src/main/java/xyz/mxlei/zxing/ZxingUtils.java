package xyz.mxlei.zxing;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lei
 * @date :2019/9/18 10:51
 */
public class ZxingUtils {

    public static final int TEXT_LOCATION_NONE = 1;
    public static final int TEXT_LOCATION_TOP = 2;
    public static final int TEXT_LOCATION_BOTTOM = 3;
    public static final int TEXT_ALIGN_LEFT = 1;
    public static final int TEXT_ALIGN_CENTER = 2;
    public static final int TEXT_ALIGN_RIGHT = 3;

    /**
     * 生成QRCode（二维码）
     * */
    public static Bitmap createQRCode(String contentStr) throws WriterException {
        return createQRCode(contentStr, null);
    }

    /**
     * 生成QRCode（二维码）
     * @param contentStr    内容
     * @param logo          logo图片
     * @return 成功返回bitmap或失败返回null
     * @throws WriterException
     */
    public static Bitmap createQRCode(String contentStr, Bitmap logo) throws WriterException {
        return createQRCode(contentStr, logo, BarcodeFormat.QR_CODE);
    }

    /**
     * 生成QRCode（二维码）
     * @param contentStr    内容
     * @param logo          logo图片
     * @return 成功返回bitmap或失败返回null
     * @throws WriterException
     */
    public static Bitmap createQRCode(String contentStr, Bitmap logo, BarcodeFormat format) throws WriterException {
        if (TextUtils.isEmpty(contentStr)) {
            return null;
        }
        Map<EncodeHintType, Object> hints = new HashMap();
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 0);
        //2.31 -> 每个模块0.33毫米，一个单元7个模块
//        int barcodeWidth = (int) (contentStr.length() * 2.31) * 8;
//        int barcodeHeight  = barcodeWidth / 3;
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        //todo PDF417格式时生成二维码出错
        BitMatrix matrix = new MultiFormatWriter().encode(contentStr, format, 300, 300, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return setupLogo(bitmap, logo);
    }

    /**
     * 生成条码
     * @param contentStr    内容
     * @param format        格式
     * @param textAlignment 文本对齐 {@link #TEXT_ALIGN_LEFT} {@link #TEXT_ALIGN_CENTER} {@link #TEXT_ALIGN_RIGHT}
     * @param textLocation  文本位置 {@link #TEXT_LOCATION_TOP} {@link #TEXT_LOCATION_BOTTOM} {@link #TEXT_LOCATION_NONE}
     * @return 成功返回bitmap或失败返回null
     * @throws WriterException
     */
    public static Bitmap createBarCode(String contentStr, BarcodeFormat format, int textLocation, int textAlignment) throws WriterException {
        if (TextUtils.isEmpty(contentStr)) {
            return null;
        }
        Map<EncodeHintType, Object> hints = new HashMap();
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        //2.31 -> 每个模块0.33毫米，一个单元7个模块
//        int barcodeWidth = (int) (contentStr.length() * 2.31) * 8;
//        int barcodeHeight  = barcodeWidth / 3;
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 0);
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(contentStr, format, 340, 80, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000; // 黑色
                } else {
                    pixels[y * width + x] = 0xffffffff;// 白色
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        //添加文字
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
//        paint.setTextSize(ViewUtils.dpToPx(16));
        float textWidth = setTextSize(paint, dpToPx(16), bitmap.getWidth(), contentStr);//文本宽度
        float textHeight = textLocation == TEXT_LOCATION_NONE ? 0 :
                paint.getFontMetrics().bottom -                 //文本高度
                        paint.getFontMetrics().top;
        float textX = 0;
        float textY = 0;
        Bitmap bitmapTxt = Bitmap.createBitmap(bitmap.getWidth(), (int) (bitmap.getHeight() + textHeight), Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmapTxt);
            switch (textLocation) {
                case TEXT_LOCATION_NONE:
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    break;
                case TEXT_LOCATION_TOP:
//                    textX = bitmap.getWidth() / 2f - textWidth / 2;
                    textY = textHeight * 0.8f;
                    canvas.drawBitmap(bitmap, 0, textHeight, null);
//                    canvas.drawText(contentStr, textX, textY, paint);
                    break;
                case TEXT_LOCATION_BOTTOM:
//                    textX = bitmap.getWidth() / 2f - textWidth / 2;
                    textY = bitmap.getHeight() + textHeight * 0.8f;
                    canvas.drawBitmap(bitmap, 0, 0, null);
//                    canvas.drawText(contentStr, textX, textY, paint);
                    break;
            }
            switch (textAlignment) {
                case TEXT_ALIGN_LEFT:
                    textX = 0;

                    break;
                case TEXT_ALIGN_CENTER:
                    textX = bitmap.getWidth() / 2f - textWidth / 2;

                    break;
                case TEXT_ALIGN_RIGHT:
                    textX = bitmap.getWidth() - textWidth;

                    break;
            }
            if (textLocation != TEXT_LOCATION_NONE) {
                canvas.drawText(contentStr, textX, textY, paint);
            }
//            canvas.drawText(contentStr,bitmap.getWidth()/2f-textWidth/2,bitmap.getHeight()+textHeight,paint);
            canvas.save();//canvas.save(Canvas.ALL_SAVE_FLAG)
            canvas.restore();
        } catch (Exception e) {
            bitmap.recycle();
            bitmapTxt.recycle();
            bitmap = null;
            bitmapTxt = null;
            e.getStackTrace();
        }
        return bitmapTxt;
    }

    /**
     * 设置文本大小
     * */
    private static float setTextSize(Paint paint, float textSize, float bitmapWidth, String contentStr) {
        paint.setTextSize(textSize);
        float textWidth = paint.measureText(contentStr);        //文本宽度
        if (textWidth < bitmapWidth - 50) {
            return textWidth;
        } else {
            return setTextSize(paint, textSize - 3, bitmapWidth, contentStr);
        }
    }

    /**
     * 设置logo
     * */
    public static Bitmap setupLogo(Bitmap qrCode, Bitmap logo) {
        if (logo == null || qrCode == null) return qrCode;
        //获取图片的宽高
        int srcWidth = qrCode.getWidth();
        int srcHeight = qrCode.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0 || logoWidth == 0 || logoHeight == 0) return qrCode;

        logo = cutToSquareBitmap(logo, Math.min(logoWidth, logoHeight));//裁剪
        logoWidth = logo.getWidth();
        logoHeight = logo.getHeight();

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 7 / logoWidth;
        int logoLeft = (srcWidth - logoWidth) / 2;//Logo位置
        int logoTop = (srcHeight - logoHeight) / 2;
        int logoBorderSize = (int) (3 / scaleFactor);//logo边框
        int logoBorderRadius = 5;//logo边框圆角

        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(qrCode, 0, 0, null);

            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);//缩放

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            int borderLeft = logoLeft - logoBorderSize;
            int borderRight = logoLeft + logoWidth + logoBorderSize;
            int borderTop = logoTop - logoBorderSize;
            int borderBottom = logoTop + logoHeight + logoBorderSize;
            canvas.drawRoundRect(new RectF(borderLeft, borderTop, borderRight, borderBottom), logoBorderRadius, logoBorderRadius, paint);
            canvas.drawBitmap(logo, logoLeft, logoTop, null);

            canvas.save();//canvas.save(Canvas.ALL_SAVE_FLAG)
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    /**
     * 解析图片二维码
     *
     * @param qrCodeImg
     * @return
     */
    public static String parseQRCode(Bitmap qrCodeImg) {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);

        try {
            Result result = new QRCodeReader().decode(Bitmap2BinaryBitmap(qrCodeImg), hints);
            return result.getText();
        } catch (Exception ex) {
            return "";
        }
    }

    private static BinaryBitmap Bitmap2BinaryBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        //获取像素
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);

        return new BinaryBitmap(new HybridBinarizer(source));
    }

    /**
     * 裁剪为正方形位图
     *
     * @param bitmap
     * @param reqLength 希望取得的长度
     * @return
     */
    public static Bitmap cutToSquareBitmap(Bitmap bitmap, int reqLength) {
        if (null == bitmap || reqLength <= 0) return null;

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg >= reqLength && heightOrg >= reqLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = reqLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg);
            int scaledWidth = widthOrg > heightOrg ? longerEdge : reqLength;
            int scaledHeight = widthOrg > heightOrg ? reqLength : longerEdge;
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - reqLength) / 2;
            int yTopLeft = (scaledHeight - reqLength) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, reqLength, reqLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }
        return result;
    }

    private static int dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}
