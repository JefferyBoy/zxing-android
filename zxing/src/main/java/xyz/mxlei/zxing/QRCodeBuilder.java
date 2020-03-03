package xyz.mxlei.zxing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mxlei
 * @date 2020/3/3
 */
public class QRCodeBuilder {

    private String content;
    private Bitmap logoBitmap;

    public QRCodeBuilder(String content) {
        this.content = content;
    }

    public Bitmap build() {
        try {
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            Map<EncodeHintType, Object> hints = new HashMap();
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
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
            return setupLogo(bitmap, logoBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置logo
     */
    private Bitmap setupLogo(Bitmap qrCode, Bitmap logo) {
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
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
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
     * 裁剪为正方形位图
     *
     * @param bitmap
     * @param reqLength 希望取得的长度
     * @return
     */
    private Bitmap cutToSquareBitmap(Bitmap bitmap, int reqLength) {
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
            } catch (Exception e) {
                return null;
            }
        }
        return result;
    }

    public QRCodeBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public QRCodeBuilder setLogoBitmap(Bitmap logoBitmap) {
        this.logoBitmap = logoBitmap;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Bitmap getLogoBitmap() {
        return logoBitmap;
    }
}
