import { NativeModules, NativeEventEmitter } from 'react-native';

const eventEmitter = new NativeEventEmitter(NativeModules.RNAMapGeolocation);

export default class AMapGeolocation {
  /**
   * 配置高德地图 Key
   * @param apiKey 获取key: https://lbs.amap.com/api/ios-location-sdk/guide/create-project/get-key
   */
  static setApiKey(apiKey) {
    return NativeModules.RNAMapGeolocation.setApiKey(apiKey);
  }
  /**
   * 开始定位
   */
  static start() {
    return NativeModules.RNAMapGeolocation.start();
  }
  /**
   * 停止更新位置信息
   */
  static stop() {
    return NativeModules.RNAMapGeolocation.stop();
  }
  /**
   * 是否已经开始持续定位了
   */
  static isStarted() {
    return NativeModules.RNAMapGeolocation.isStarted();
  }
  /**
   * 定位超时时间，最低2s
   * @param {number} number 默认设置为2s
   */
  static setLocationTimeout(number = 2) {
    return NativeModules.RNAMapGeolocation.setLocationTimeout(number);
  }
  /**
   * 逆地理请求超时时间，最低2s
   * @param {number} number 默认设置为2s
   */
  static setReGeocodeTimeout(number = 2) {
    return NativeModules.RNAMapGeolocation.setReGeocodeTimeout(number);
  }
  /**
   * 用于指定所需的精度级别。
   * 单位米，默认为 kCLLocationAccuracyBest。定位服务会尽可能去获取满足desiredAccuracy的定位结果，但不保证一定会得到满足期望的结果。
   * 注意：设置为 kCLLocationAccuracyBest 或 kCLLocationAccuracyBestForNavigation 时，
   * 单次定位会在达到 locationTimeout 设定的时间后，将时间内获取到的最高精度的定位结果返回。
   * 高德提供了 kCLLocationAccuracyBest 参数，设置该参数可以获取到精度在10m 左右的定位结果，但是相应的需要付出比较长的时间（10s左右），
   * 越高的精度需要持续定位时间越长。
   * 推荐：kCLLocationAccuracyHundredMeters，一次还不错的定位，偏差在百米左右，超时时间设置在2s-3s左右即可。
   * @param {number} accuracy `0~5`
   * - 0 => kCLLocationAccuracyBestForNavigation
   * - 1 => kCLLocationAccuracyBest
   * - 2 => kCLLocationAccuracyNearestTenMeters
   * - 3 => kCLLocationAccuracyHundredMeters
   * - 4 => kCLLocationAccuracyKilometer
   * - 5 => kCLLocationAccuracyThreeKilometers
   * @platform ios
   */
  static setDesiredAccuracy(accuracy) {
    if (Platform.OS === "ios") {
      return NativeModules.RNAMapGeolocation.setDesiredAccuracy(accuracy);
    }
  }
  /**
   * 设置定位模式。默认值：Hight_Accuracy 高精度模式
   * android 默认定位模式，目前支持三种定位模式
   * - 1 => `Hight_Accuracy` 高精度定位模式：在这种定位模式下，将同时使用高德网络定位和卫星定位,优先返回精度高的定位
   * - 2 => `Battery_Saving` 低功耗定位模式：在这种模式下，将只使用高德网络定位
   * - 3 => `Device_Sensors` 仅设备定位模式：在这种模式下，将只使用卫星定位。
   * @param {number} mode `1~3`
   * @platform android
   */
  static setLocationMode(mode = 1) {
    if (Platform.OS === "android") {
      let str = 'Hight_Accuracy';
      switch (mode) {
        case 1: str = 'Hight_Accuracy'; break;
        case 2: str = 'Battery_Saving'; break;
        case 3: str = 'Device_Sensors'; break;
        default: break;
      }
      return NativeModules.RNAMapGeolocation.setLocationMode(str);
    }
  }
  /**
   * 获取当前定位
   */
  static getCurrentLocation() {
    return NativeModules.RNAMapGeolocation.getCurrentLocation();
  }
  /**
   * 设置是否单次定位
   * @default false
   * @platform android
   */
  static setOnceLocation(isOnceLocation) {
    if (Platform.OS === "android") {
      return NativeModules.RNAMapGeolocation.setOnceLocation(isOnceLocation);
    }
  }
  /**
   * 定位是否返回逆地理信息，为了与 android 保持一致，默认 值为 true。
   * @platform ios 默认值：false, 返回地址信息，需要手动设置
   * @platform android 默认值：true, 返回地址信息
   */
  static setLocatingWithReGeocode(isReGeocode = true) {
    if (Platform.OS === "ios") {
      return NativeModules.RNAMapGeolocation.setLocatingWithReGeocode(isReGeocode);
    }
    if (Platform.OS === "android") {
      return NativeModules.RNAMapGeolocation.setNeedAddress(isReGeocode);
    }
  }
  /**
   * 设置发起定位请求的时间间隔，单位：毫秒，默认值：2000毫秒
   * @platform android
   */
  static setInterval(interval) {
    if (Platform.OS === "android") {
      return NativeModules.RNAMapGeolocation.setInterval(interval);
    }
  }
  /**
   * 设置是否允许调用 WIFI 刷新。
   * 默认值为true，当设置为false时会停止主动调用WIFI刷新，将会极大程度影响定位精度，但可以有效的降低定位耗电
   * @platform android
   * @default true
   */
  static setWifiScan(interval) {
    if (Platform.OS === "android") {
      return NativeModules.RNAMapGeolocation.setWifiScan(interval);
    }
  }
  /**
   * 连续定位监听事件
   * @param {Function} listener 
   */
  static addLocationListener(listener) {
    return eventEmitter.addListener('AMapGeolocation', (info) => {
      let errorInfo = undefined;
      if (info.errorCode || info.errorInfo) {
        errorInfo = {
          code: info.errorCode,
          message: info.errorInfo
        };
      }
      listener && listener(info, errorInfo);
    });
  }
}