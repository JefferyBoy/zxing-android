package xyz.mxlei.zxing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author mxlei
 * @date 2020/3/3
 * 条码图片处理转换
 */
public class BarCodeTransformer {

    /**
     * 转换EAN8标准格式
     * EAN8分为81个模块
     */
    public Bitmap EAN8(Bitmap bitmap, String content) {
        float pieceOfBitmap = 81;
        float pieceWidth = bitmap.getWidth() / pieceOfBitmap;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        float fourCharWidth = adjustTextSize(paint, content.substring(0, 4), pieceWidth * 28 * 0.8f);
        float oneCharHeight = paint.getFontMetrics().descent - paint.getFontMetrics().ascent;
        Bitmap dstBitmap = Bitmap.createBitmap(bitmap.getWidth(), (int) (bitmap.getHeight() + oneCharHeight / 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        //去除底部多出线条
        Bitmap whiteBitmap = Bitmap.createBitmap((int) Math.ceil(pieceWidth * 31), (int) Math.ceil(oneCharHeight), Bitmap.Config.ARGB_8888);
        Canvas whiteCanvas = new Canvas(whiteBitmap);
        whiteCanvas.drawColor(Color.WHITE);
        whiteCanvas.save();
        whiteCanvas.restore();
        canvas.drawBitmap(whiteBitmap, pieceWidth * 7, dstBitmap.getHeight() - whiteBitmap.getHeight(), null);
        canvas.drawBitmap(whiteBitmap, pieceWidth * 43, dstBitmap.getHeight() - whiteBitmap.getHeight(), null);
        //左边4位
        canvas.drawText(content.substring(0, 4), pieceWidth * 7 + ((pieceWidth * 31 - fourCharWidth) / 2), dstBitmap.getHeight() - oneCharHeight / 4, paint);
        //右边4位
        canvas.drawText(content.substring(4) + BarCodeVerifier.getCheckBit(content), pieceWidth * 43 + ((pieceWidth * 31 - fourCharWidth) / 2), dstBitmap.getHeight() - oneCharHeight / 4, paint);
        canvas.save();
        canvas.restore();
        return dstBitmap;
    }

    /**
     * 转换EAN13标准格式
     * EAN13条码分为15个部分，3个下沉模块，6个厂商码模块，6个产品码模块
     */
    public Bitmap EAN13(Bitmap bitmap, String content) {
        float pieceOfBitmap = 15;
        float pieceWidth = bitmap.getWidth() / pieceOfBitmap;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        float sixCharWidth = adjustTextSize(paint, content.substring(1, 7), pieceWidth * 6 * 0.9f);
        float oneCharWidth = sixCharWidth / 6f;
        float oneCharHeight = paint.getFontMetrics().descent - paint.getFontMetrics().ascent;

        Bitmap dstBitmap = Bitmap.createBitmap((int) (bitmap.getWidth() + oneCharWidth), (int) (bitmap.getHeight() + oneCharHeight / 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, oneCharWidth, 0, null);
        //去除底部多出线条
        Bitmap whiteBitmap = Bitmap.createBitmap((int) Math.ceil(pieceWidth * 6), (int) Math.ceil(oneCharHeight), Bitmap.Config.ARGB_8888);
        Canvas whiteCanvas = new Canvas(whiteBitmap);
        whiteCanvas.drawColor(Color.WHITE);
        whiteCanvas.save();
        whiteCanvas.restore();
        canvas.drawBitmap(whiteBitmap, oneCharWidth + pieceWidth - 1, dstBitmap.getHeight() - whiteBitmap.getHeight(), null);
        canvas.drawBitmap(whiteBitmap, oneCharWidth + pieceWidth * 8, dstBitmap.getHeight() - whiteBitmap.getHeight(), null);
        //左边一位数字
        canvas.drawText(content.substring(0, 1), 0, dstBitmap.getHeight() - oneCharHeight / 4, paint);
        //左边6位厂商码
        canvas.drawText(content.substring(1, 7), oneCharWidth + pieceWidth + ((pieceWidth * 6 - sixCharWidth) / 2), dstBitmap.getHeight() - oneCharHeight / 4, paint);
        //右边6位产品码
        canvas.drawText(content.substring(7) + BarCodeVerifier.getCheckBit(content), oneCharWidth + pieceWidth * 8 + ((pieceWidth * 6 - sixCharWidth) / 2), dstBitmap.getHeight() - oneCharHeight / 4, paint);
        canvas.save();
        canvas.restore();
        return dstBitmap;
    }

    /**
     * 转换UPC-A标准格式
     * 左侧空白区9个模块，其他与EAN13相同，共113个模块
     */
    public Bitmap UPC_A(Bitmap bitmap, String content) {
        float pieceOfBitmap = 113;
        float pieceWidth = bitmap.getWidth() / pieceOfBitmap;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        float fiveCharWidth = adjustTextSize(paint, content.substring(1, 7), pieceWidth * 35 * 0.95f);
        float oneCharWidth = fiveCharWidth / 5f;
        float oneCharHeight = paint.getFontMetrics().descent - paint.getFontMetrics().ascent;

        Bitmap dstBitmap = Bitmap.createBitmap((int) (bitmap.getWidth() + oneCharWidth * 2), (int) (bitmap.getHeight() + oneCharHeight / 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, oneCharWidth, 0, null);
        //去除底部多出线条
        Bitmap whiteBitmap = Bitmap.createBitmap((int) Math.ceil(pieceWidth * 40), (int) Math.ceil(oneCharHeight), Bitmap.Config.ARGB_8888);
        Canvas whiteCanvas = new Canvas(whiteBitmap);
        whiteCanvas.drawColor(Color.WHITE);
        whiteCanvas.save();
        whiteCanvas.restore();
        canvas.drawBitmap(whiteBitmap, oneCharWidth + pieceWidth * 14, dstBitmap.getHeight() - whiteBitmap.getHeight(), null);
        canvas.drawBitmap(whiteBitmap, oneCharWidth + pieceWidth * 59 - 1, dstBitmap.getHeight() - whiteBitmap.getHeight(), null);
        //左边一位数字
        canvas.drawText(content.substring(0, 1), 0, dstBitmap.getHeight() - oneCharHeight / 4, paint);
        //左边5位
        canvas.drawText(content.substring(1, 6), oneCharWidth + pieceWidth * 14 + ((pieceWidth * 42 - fiveCharWidth) / 2), dstBitmap.getHeight() - oneCharHeight / 4, paint);
        //右边5位
        canvas.drawText(content.substring(6), oneCharWidth + pieceWidth * 59 + ((pieceWidth * 42 - fiveCharWidth) / 2), dstBitmap.getHeight() - oneCharHeight / 4, paint);
        //右边校验位
        canvas.drawText(String.valueOf(BarCodeVerifier.getCheckBit(content)), oneCharWidth + bitmap.getWidth(), dstBitmap.getHeight() - oneCharHeight / 4, paint);
        canvas.save();
        canvas.restore();
        return dstBitmap;
    }

    /**
     * 转换UPC-E标准格式
     * 左侧空白区9个模块，右侧空白期7个模块
     */
    public Bitmap UPC_E(Bitmap bitmap, String content) {
        float pieceOfBitmap = 110;
        float pieceWidth = bitmap.getWidth() / pieceOfBitmap;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        float sixCharWidth = adjustTextSize(paint, content.substring(1, 7), pieceWidth * 75 * 0.7f);
        float oneCharWidth = sixCharWidth / 6f;
        float oneCharHeight = paint.getFontMetrics().descent - paint.getFontMetrics().ascent;

        Bitmap dstBitmap = Bitmap.createBitmap((int) (bitmap.getWidth() + oneCharWidth * 2), (int) (bitmap.getHeight() + oneCharHeight / 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, oneCharWidth, 0, null);
        //去除底部多出线条
        Bitmap whiteBitmap = Bitmap.createBitmap((int) Math.ceil(pieceWidth * 82), (int) Math.ceil(oneCharHeight), Bitmap.Config.ARGB_8888);
        Canvas whiteCanvas = new Canvas(whiteBitmap);
        whiteCanvas.drawColor(Color.WHITE);
        whiteCanvas.save();
        whiteCanvas.restore();
        canvas.drawBitmap(whiteBitmap, oneCharWidth + pieceWidth * 12, dstBitmap.getHeight() - whiteBitmap.getHeight(), null);
        //左边一位数字
        canvas.drawText(content.substring(0, 1), 0, dstBitmap.getHeight() - oneCharHeight / 4, paint);
        //中间6位数字
        canvas.drawText(content.substring(1, 7), oneCharWidth + pieceWidth * 14 + ((pieceWidth * 80 - sixCharWidth) / 2), dstBitmap.getHeight() - oneCharHeight / 4, paint);
        //右边校验位
        canvas.drawText(String.valueOf(BarCodeVerifier.getCheckBit(content)), oneCharWidth + bitmap.getWidth(), dstBitmap.getHeight() - oneCharHeight / 4, paint);
        canvas.save();
        canvas.restore();
        return dstBitmap;
    }

    private float adjustTextSize(Paint paint, String content, float width) {
//        System.out.println("控件宽度 = " + width);
        float textSize = paint.getTextSize();
        float measureWidth = paint.measureText(content);
        if (measureWidth < width) {
            while (measureWidth < width) {
                //增大字号
                paint.setTextSize(++textSize);
                measureWidth = paint.measureText(content);
//                System.out.println("测试宽度 = " + measureWidth + "\t字号 = " + textSize);
            }
            paint.setTextSize(--textSize);
            return measureWidth;
        } else {
            while (measureWidth > width) {
                //减小字号
                paint.setTextSize(--textSize);
                measureWidth = paint.measureText(content);
            }
            paint.setTextSize(++textSize);
            return measureWidth;
        }
    }

}
