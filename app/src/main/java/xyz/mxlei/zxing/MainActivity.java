package xyz.mxlei.zxing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText editText;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        editText = findViewById(R.id.edit);
    }

    public void clickEAN13ByZxing(View view) {
        Bitmap bitmap = new BarCodeBuilder(editText.getText().toString().trim())
                .setFormat(BarcodeFormat.EAN_13)
                .setStandardFormat(true)
                .build();
        if (bitmap != null) {
            Log.d(TAG, "Zxing生成条码 宽度 = " + bitmap.getWidth() + "\t高度 = " + bitmap.getHeight());
            imageView.setImageBitmap(bitmap);
        }
    }

    public void clickEAN8(View view) {
        Bitmap bitmap = new BarCodeBuilder(editText.getText().toString().trim())
                .setFormat(BarcodeFormat.EAN_8)
                .setStandardFormat(false)
                .build();
        if (bitmap != null) {
            Log.d(TAG, "Zxing生成条码 宽度 = " + bitmap.getWidth() + "\t高度 = " + bitmap.getHeight());
            imageView.setImageBitmap(bitmap);
        }
    }

    public void clickUPC_A(View view) {
        Bitmap bitmap = new BarCodeBuilder(editText.getText().toString().trim())
                .setFormat(BarcodeFormat.UPC_A)
                .setStandardFormat(true)
                .build();
        if (bitmap != null) {
            Log.d(TAG, "Zxing生成条码 宽度 = " + bitmap.getWidth() + "\t高度 = " + bitmap.getHeight());
            imageView.setImageBitmap(bitmap);
        }
    }

    public void clickUPC_E(View view) {
        Bitmap bitmap = new BarCodeBuilder(editText.getText().toString().trim())
                .setFormat(BarcodeFormat.UPC_E)
                .setStandardFormat(true)
                .build();
        if (bitmap != null) {
            Log.d(TAG, "Zxing生成条码 宽度 = " + bitmap.getWidth() + "\t高度 = " + bitmap.getHeight());
            imageView.setImageBitmap(bitmap);
        }
    }

    public void clickITF_14(View view) {
        Bitmap bitmap = new BarCodeBuilder(editText.getText().toString().trim())
                .setFormat(BarcodeFormat.ITF)
                .setStandardFormat(true)
                .build();
        if (bitmap != null) {
            Log.d(TAG, "Zxing生成条码 宽度 = " + bitmap.getWidth() + "\t高度 = " + bitmap.getHeight());
            imageView.setImageBitmap(bitmap);
        }
    }

    public void clickCode39(View view){
        Bitmap bitmap = new BarCodeBuilder(editText.getText().toString().trim())
                .setFormat(BarcodeFormat.CODE_39)
                .setStandardFormat(true)
                .build();
        if (bitmap != null) {
            Log.d(TAG, "Zxing生成条码 宽度 = " + bitmap.getWidth() + "\t高度 = " + bitmap.getHeight());
            imageView.setImageBitmap(bitmap);
        }
    }
    public void clickCode128(View view){
        Bitmap bitmap = new BarCodeBuilder(editText.getText().toString().trim())
                .setFormat(BarcodeFormat.CODE_128)
                .setStandardFormat(true)
                .build();
        if (bitmap != null) {
            Log.d(TAG, "Zxing生成条码 宽度 = " + bitmap.getWidth() + "\t高度 = " + bitmap.getHeight());
            imageView.setImageBitmap(bitmap);
        }
    }
    public void clickQRCode(View view) {
        Bitmap bitmap = new QRCodeBuilder(editText.getText().toString().trim())
                .setLogoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .build();
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void clickGetWidth(View view) {
        Float width = Float.valueOf(imageView.getWidth());
        Float height = Float.valueOf(imageView.getHeight());
        Log.d("宽度", "" + width);
        Log.d("高度", "" + height);
        Log.d("宽高比", "" + width / height);

    }

}
