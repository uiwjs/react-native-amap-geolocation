@uiw/react-native-amap-geolocation
----

[![NPM Version](https://img.shields.io/npm/v/@uiw/react-native-amap-geolocation.svg)](https://npmjs.org/package/@uiw/react-native-amap-geolocation)
![David](https://img.shields.io/david/peer/uiwjs/react-native-amap-geolocation)

React Native 高德地图定位模块，支持 Android/iOS。提供尽可能完善的原生接口，同时提供符合 Web 标准的 Geolocation API 以及 [完整的接口文档](https://uiwjs.github.io/react-native-amap-geolocation/)。

## 注意事项

⚠️ 高德地图定位部分 API 需要真机调试和 `Access WiFi Information` 权限。

<details>
<summary>Android：需要正确的设置 apiKey，获取 Key 的方法</summary>

<br />

官方获取 Key方法：https://lbs.amap.com/api/android-location-sdk/guide/create-project/get-key

A. 使用 `keytool`（jdk自带工具）获取 `SHA1`，默认 测试版本 `keystore` 路径 `<项目名称>/android/app/debug.keystore`

```bash
keytool -v -list -keystore  keystore文件路径
```

B. 获取 `PackageName`，获取路径 `<项目名称>/android/app/src/main/AndroidManifest.xml`

C. 在高德地图账号中设置 `SHA1` 和 `PackageName`。

<img src="https://raw.githubusercontent.com/uiwjs/react-native-amap-geolocation/master/imgs/sha1.png" />

D. 按照上面步骤正确设置你的 `apiKey` 才会起作用。

</details>

<details>
<summary>Android：无法获取逆地理信息的问题，KEY鉴权失败</summary>

1. APK 当前签名文件的 SHA1 与[高德开放平台](https://console.amap.com/)中设置不一致
2. Android Studio 开发者请注意调整 `build.gradle` 文件的 `applicationId` 与 `packageName` 一致，如不一致将会导致鉴权失败。
3. 工程中的 `packageName` 与[高德开放平台](https://console.amap.com/)中 `packageName` 设置不一致
4. 通过 `SDK` 提供的 `setApiKey(String key);` 接口设置 `Key`，注意 `Key` 设置要在 `SDK` 业务初始化之前。

</details>

<details>
<summary>iOS：获取逆地理信息需要高德地图配置 apiKey</summary>

<img src="https://raw.githubusercontent.com/uiwjs/react-native-amap-geolocation/master/imgs/amapkey.png" />

</details>

<details>
<summary>iOS：高德地图包需要 WiFi 权限</summary>

<br />

iOS 端高德地图包需要 WiFi 权限，否则报如下警告：

```
nehelper sent invalid result code [1] for Wi-Fi information request
```

需要在[开发者账号中设置 WiFi 权限](https://developer.apple.com)

<img src="https://raw.githubusercontent.com/uiwjs/react-native-amap-geolocation/master/imgs/identifiers.png" />

同时需要在 Xcode 中添加 `Access WiFi Information` 能力选项

<img src="https://raw.githubusercontent.com/uiwjs/react-native-amap-geolocation/master/imgs/xcode.png" />

</details>

<details>
<summary>iOS：需要保证"Background Modes"中的"Location updates"处于选中状态</summary>

<img src="https://raw.githubusercontent.com/uiwjs/react-native-amap-geolocation/master/imgs/xcode.png" />

1. 左侧目录中选中工程名，开启 `TARGETS` -> `Capabilities` -> `Background Modes`
2. 在 `Background Modes` 中勾选 `Location updates`

</details>

## 安装依赖

```bash
yarn add @uiw/react-native-amap-geolocation
# react-native version >= 0.60+
$ cd ios && pod install
```

## 基本用法

```javascript
import { Platform } from 'react-native';
import AMapGeolocation from '@uiw/react-native-amap-geolocation';

let apiKey = '';

if (Platform.OS === 'ios') {
  apiKey = '用于 iOS 的 apiKey';
}
if (Platform.OS === 'android') {
  apiKey = '用于 Android 的 apiKey';
}

// 设置 高德地图 apiKey
AMapGeolocation.setApiKey(apiKey);
// iOS 指定所需的精度级别
AMapGeolocation.setDesiredAccuracy(3);
// Android 指定所需的精度级别，可选设置，默认 高精度定位模式
AMapGeolocation.setLocationMode(1);
// 定位是否返回逆地理信息
AMapGeolocation.setLocatingWithReGeocode(true);

async function getCurrentLocation(){
  try {
    const json = await AMapGeolocation.getCurrentLocation();
    console.log('json:', json);
  } catch (error) {
    console.log('error:', error);
  }
}
```

## 定位监听函数

```js
import AMapGeolocation from '@uiw/react-native-amap-geolocation';

let apiKey = '';
if (Platform.OS === 'ios') {
  apiKey = '用于 iOS 的 apiKey';
}
if (Platform.OS === 'android') {
  apiKey = '用于 Android 的 apiKey';
}

// 设置 高德地图 apiKey
AMapGeolocation.setApiKey(apiKey);
// 定位是否返回逆地理信息
AMapGeolocation.setLocatingWithReGeocode(true);
// 当设备可以正常联网时，还可以返回该定位点的对应的中国境内位置信息（包括：省、市、区/县以及详细地址）。
AMapGeolocation.addLocationListener((location) => {
  console.log('返回定位信息', location);
  this.setState({
    location: JSON.stringify(location, null, 2), 
  });
});
```

## 逆地理编码

```js
import AMapGeolocation from '@uiw/react-native-amap-geolocation';

let apiKey = '';
if (Platform.OS === 'ios') {
  apiKey = '用于 iOS 的 apiKey';
}
if (Platform.OS === 'android') {
  apiKey = '用于 Android 的 apiKey';
}

// 设置 高德地图 apiKey
AMapGeolocation.setApiKey(apiKey);
// 定位是否返回逆地理信息
AMapGeolocation.setLocatingWithReGeocode(true);
```

## 坐标转换

坐标转换，支持将iOS自带定位 GPS/Google/MapBar/Baidu/MapABC 多种坐标系的坐标转换成高德坐标

```js
import AMapGeolocation from '@uiw/react-native-amap-geolocation';

// 将百度地图转换为 高德地图 经纬度
const resulte = await AMapGeolocation.coordinateConvert({
  latitude: 40.002172,
  longitude: 116.467357,
}, 0);
// => {longitude: 116.46071927031961, latitude: 39.99651501274128}
```

## 定位回调频率限制

```js
import AMapGeolocation from '@uiw/react-native-amap-geolocation';

// android，5 秒请求一次定位
AMapGeolocation.setInterval(5000);

// ios，设备移动超过 10 米才会更新位置信息
AMapGeolocation.setDistanceFilter(10);
```

## 错误处理

```bash
[NetworkInfo] Signal strength query returned error: Error Domain=NSPOSIXErrorDomain Code=13 "Permission denied", descriptor: <CTServiceDescriptor 0x283317100, domain=1, instance=1>
```

在 `Product` -> `Scheme` -> `Edit Scheme` -> `Run` -> `Arguments` -> `Environment Variables` 添加 `OS_ACTIVITY_MODE` `disable`

```bash
nehelper sent invalid result code [1] for Wi-Fi information request
```

配置 WiFi 权限

## 其它

当前工程基于 [@brodybits/create-react-native-module](https://github.com/brodybits/create-react-native-module) 初始化。

```bash
npx create-react-native-module --package-identifier com.uiwjs.react.geolocation --object-class-name RNAMapGeolocation --generate-example AMapGeolocation --example-react-native-version 0.63.0 --module-name @uiw/react-native-amap-geolocation --github-account uiwjs --author-name "Kenny Wong" --author-email "wowohoo@qq.com"
```

## 开发

```bash
cd example   # 进入实例 example 工程，根目录不需要安装，会引发错误
yarn install # 安装依赖

cd ios     # 进入 example/ios 目录安装依赖
pod instll # 安装依赖
```

## 相关连接 

- [高德地图：Android 端类文档](http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Location_Doc/index.html)
- [高德地图：iOS定位SDK V2.6.5](https://lbs.amap.com/api/ios-location-sdk/download/)
- [高德地图：Android 定位SDK V5.1.0](https://lbs.amap.com/api/android-location-sdk/download)
- [高德地图：提交 AppStore 必读](https://lbs.amap.com/api/ios-location-sdk/guide/create-project/idfa-guide)
