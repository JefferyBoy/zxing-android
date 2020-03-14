# Android条码二维码
基于Google的开源库Zxing封装，增加了常用的实用性功能。满足开发需求。

```gradle
dependencies {
	//使用本条码库
	implementation 'xyz.mxlei:zxing:1.1.3'
}
allprojects {
    repositories {
    	//声明库所在的仓库地址
        maven { url  "https://dl.bintray.com/xyz-mxlei/maven" }
    }
}
```

## 1. 生成普通带文本条码

生成带文本的一维条码，可以设置文本的位置和对其方式

```java
 Bitmap bitmap = 
     new BarCodeBuilder("12345678")		//条码内容
         .setFormat(BarcodeFormat.EAN8) //条码格式
         .setTextLocation(BarCodeBuilder.TEXT_LOCATION_BOTTOM)//文本在底部
         .setTextAlignment(BarCodeBuilder.TEXT_ALIGN_CENTER)  //文本居中对齐
         .build();						//生成条码Bitmap
```

![UTOOLS1583217926225.png](http://yanxuan.nosdn.127.net/27dd5181bf347ecb8f050db8d394b80e.png)

支持的条码格式如下（也可以自己添加其他的格式）

1. EAN8
2. EAN13
3. UPC-A
4. UPC-E
5. ITF
6. CODE39
7. CODE128

## 2. 生成标准商品格式的条码

```java
 Bitmap bitmap = 
     new BarCodeBuilder("12345678")		//条码内容
         .setFormat(BarcodeFormat.EAN8) //条码格式
     	 .setStandardFormat(true)		//是否标准格式
         .build();						//生成条码Bitmap
```

标准格式支持如下：

1. EAN8
2. EAN13
3. UPC-A
4. UPC-E

![UTOOLS1583218015230.png](http://yanxuan.nosdn.127.net/813075addf832e935cae5fc48435cf5f.png)

## 3. 生成带LOGO的二维码

```java
 Bitmap bitmap = 
     new QRCodeBuilder("二维码内容")
     	.setLogoBitmap(logoBitmap)
        .build();
```



## 4.扫描二维码和条码

提供仿照微信、支付宝的二维码扫描界面，可以直接跳转到Activity，然后监听扫描结果，也可以继承BaseCaptureActivity后自定义扫描界面。

![](http://yanxuan.nosdn.127.net/0929337b7c3dc76aa5c527f387272c16.png)

```java
public static final int REQUEST_CODE = 12121;

//跳转到扫描二维码界面
public void toScan(){
    startActivityForResult(ZxingCaptureActivity.class, REQUEST_CODE);
}

//获取扫描结果
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

```java


```