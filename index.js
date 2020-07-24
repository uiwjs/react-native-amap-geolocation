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
   * 获取当前定位
   */
  static getCurrentLocation() {
    return NativeModules.RNAMapGeolocation.getCurrentLocation();
  }
  /**
   * 连续定位是否返回逆地理信息，默认NO。
   * Android 默认返回逆地理编码，而 iOS 需要手动设置。
   * @platform ios
   */
  static setLocatingWithReGeocode(isReGeocode) {
    if (Platform.OS === "ios") {
      return NativeModules.RNAMapGeolocation.setLocatingWithReGeocode(isReGeocode);
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