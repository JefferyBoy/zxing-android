# Zxing Android SDK
根据Google官网的zxing sdk，添加了仿微信、支付宝的条码扫描框，同时提供了ZxingUtils.java封装了生成条码、二维码的功能，二维码可添加logo等。可以生成标准的商品条码，自动计算校验位，设置显示文本位置等。

## 

可以设置文本显示位置（上方、下方），对其方式（左、中、右）

![UTOOLS1583217926225.png](http://yanxuan.nosdn.127.net/27dd5181bf347ecb8f050db8d394b80e.png)

支持生成标准商品格式的条码

![UTOOLS1583218015230.png](http://yanxuan.nosdn.127.net/813075addf832e935cae5fc48435cf5f.png)



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

直接调用提供Builder类可生成二维码和条码，以Bitmap对象返回。

```java
public void test(){
    //生成条码
 Bitmap bitmap = new BarCodeBuilder(editText.getText().toString().trim())
                .setFormat(BarcodeFormat.EAN_8)
                .setStandardFormat(true)
                .build();
}
public void test2(){
    //生成二维码
     Bitmap bitmap = new QRCodeBuilder(editText.getText().toString().trim())
                .build();
}

```