package xyz.mxlei.zxing;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import static com.google.zxing.BarcodeFormat.EAN_13;
import static com.google.zxing.BarcodeFormat.EAN_8;
import static com.google.zxing.BarcodeFormat.ITF;
import static com.google.zxing.BarcodeFormat.UPC_A;
import static com.google.zxing.BarcodeFormat.UPC_E;

/**
 * @author mxlei
 * @date 2020/3/3
 */
public class BarCodeBuilder {

    private String content;
    private BarcodeFormat format = BarcodeFormat.EAN_13;
    private int textLocation = TEXT_LOCATION_BOTTOM;
    private int textAlignment = TEXT_ALIGN_CENTER;
    private boolean standardFormat = false;
    private BarCodeTransformer transformer;

    public static final int TEXT_LOCATION_NONE = 1;
    public static final int TEXT_LOCATION_TOP = 2;
    public static final int TEXT_LOCATION_BOTTOM = 3;
    public static final int TEXT_ALIGN_LEFT = 1;
    public static final int TEXT_ALIGN_CENTER = 2;
    public static final int TEXT_ALIGN_RIGHT = 3;

    public BarCodeBuilder(String content) {
        this.content = content;
    }

    public Bitmap build() {
        boolean check = BarCodeVerifier.verifyBarcode(content, format);
        if (!check) {
            return null;
        }
        try {
            Bitmap bitmap = generateBarcodeBitmap();
            Bitmap dstBitmap;
            if (standardFormat) {
                if (transformer == null) {
                    transformer = new BarCodeTransformer();
                }
                switch (format) {
                    case EAN_8:
                        dstBitmap = transformer.EAN8(bitmap, content);
                        break;
                    case EAN_13:
                        dstBitmap = transformer.EAN13(bitmap, content);
                        break;
                    case UPC_A:
                        dstBitmap = transformer.UPC_A(bitmap, content);
                        break;
                    case UPC_E:
                        dstBitmap = transformer.UPC_E(bitmap, content);
                        break;
                    default:
                        dstBitmap = addTextToBitmap(bitmap);
                }
            } else {
                dstBitmap = addTextToBitmap(bitmap);
            }
            return dstBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap addTextToBitmap(Bitmap bitmap) {
        //添加文字
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        float textWidth = setTextSize(paint, dpToPx(20), bitmap.getWidth() * 0.8f, content);//文本宽度
        float textHeight = textLocation == TEXT_LOCATION_NONE ? 0 :
                paint.getFontMetrics().bottom -                 //文本高度
                        paint.getFontMetrics().top;
        float textX = 0;
        float textY = 0;
        Bitmap bitmapTxt = Bitmap.createBitmap(bitmap.getWidth(), (int) (bitmap.getHeight() + textHeight), Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmapTxt);
            canvas.drawColor(Color.WHITE);
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
                    textY = bitmap.getHeight() + textHeight * 0.7f;
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
                if (format == EAN_8 || format == EAN_13 || format == UPC_A || format == UPC_E || format == ITF) {
                    canvas.drawText(content + BarCodeVerifier.getCheckBit(content), textX, textY, paint);
                } else {
                    canvas.drawText(content, textX, textY, paint);
                }
            }
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap.recycle();
            bitmapTxt.recycle();
            bitmapTxt = null;
            e.getStackTrace();
        }
        return bitmapTxt;
    }

    /**
     * 设置文本大小
     */
    private float setTextSize(Paint paint, float textSize, float bitmapWidth, String contentStr) {
        paint.setTextSize(textSize);
        float textWidth = paint.measureText(contentStr);        //文本宽度
        if (textWidth < bitmapWidth - 50) {
            return textWidth;
        } else {
            return setTextSize(paint, textSize - 3, bitmapWidth, contentStr);
        }
    }

    private int dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    /**
     * 调用Zxing生成条码图片
     */
    private Bitmap generateBarcodeBitmap() throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap();
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 0);
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        String encodeContent = content;
        if (format == ITF) {
            encodeContent = content + BarCodeVerifier.getCheckBit(content);
        }
        BitMatrix matrix = new MultiFormatWriter().encode(encodeContent, format, 500, 300, hints);
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
        return bitmap;
    }

    public BarCodeBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public BarCodeBuilder setFormat(BarcodeFormat format) {
        this.format = format;
        return this;
    }

    public BarCodeBuilder setTextLocation(int textLocation) {
        this.textLocation = textLocation;
        return this;
    }

    public BarCodeBuilder setTextAlignment(int textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public BarCodeBuilder setStandardFormat(boolean standardFormat) {
        this.standardFormat = standardFormat;
        return this;
    }

    public String getContent() {
        return content;
    }

    public BarcodeFormat getFormat() {
        return format;
    }

    public int getTextLocation() {
        return textLocation;
    }

    public int getTextAlignment() {
        return textAlignment;
    }

    public boolean isStandardFormat() {
        return standardFormat;
    }
}
