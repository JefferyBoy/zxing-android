# Zxing Android SDK
根据Google官网的zxing sdk，添加了仿微信、支付宝的条码扫描框，同时提供了ZxingUtils.java封装了生成条码、二维码的功能，二维码可添加logo等。

## 1. 扫描二维码和条码

提供了三个Activity可供选择

![](http://yanxuan.nosdn.127.net/0929337b7c3dc76aa5c527f387272c16.png)

```java
public static final int REQUEST_CODE = 12121;

public void toScan(){
    startActivityForResult(ZxingCaptureActivity.class, REQUEST_CODE);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
     if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_BARCODE && data != null) {
           //结果
           String content = data.getStringExtra(DefaultCaptureActivity.KEY_CONTENT);
           //格式
           BarcodeFormat format = (BarcodeFormat) data.getSerializableExtra(DefaultCaptureActivity.KEY_BARCODE_FORMAT);
           Log.d("扫描结果",content);
     }
}
```


## 2. 生成二维码和条码

直接调用ZxingUtils中提供的静态方法可生成二维码和条码，以Bitmap对象返回。

```java
public class ZxingUtils {
        public static final int TEXT_LOCATION_NONE = 1;
        public static final int TEXT_LOCATION_TOP = 2;
        public static final int TEXT_LOCATION_BOTTOM = 3;
        public static final int TEXT_ALIGN_LEFT = 1;
        public static final int TEXT_ALIGN_CENTER = 2;
        public static final int TEXT_ALIGN_RIGHT = 3;
        
        /**
         * 生成QRCode（二维码）
         * @param contentStr    内容
         * @param logo          logo图片
         * @return 成功返回bitmap或失败返回null
         * @throws WriterException
         */
        public static Bitmap createQRCode(String contentStr, Bitmap logo) throws WriterException {

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
            
        }
}
```