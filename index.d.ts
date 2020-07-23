
declare namespace AMapGeolocation {
  /**
   * 坐标信息
   * @see https://developer.mozilla.org/zh-CN/docs/Web/API/Coordinates
   */
  export interface Coordinates {
    /**
     * 纬度
     */
    latitude: number;
    /**
     * 经度
     */
    longitude: number;
    /**
     * 高度 - 海拔高度，以米为单位。
     */
    altitude: number;
    /**
     * 水平精度 - 位置的不确定性半径，以米为单位。
     */
    accuracy: number;
    /**
     * 移动方向，需要 GPS
     */
    heading: number;
    /**
     * 移动速度（米/秒），需要 GPS
     */
    speed: number;
    /**
     * 时间戳记 - 确定此位置的时间。
     */
    timestamp: number;
    /**
     * 是否有可用坐标
     */
    isAvailableCoordinate: boolean;
  }

  /**
   * 逆地理信息 + 坐标信息
   */
  export interface ReGeocode extends Coordinates {
    /**
     * 格式化地址
     */
    address: string;
    /**
     * 国家
     */
    country: string;
    /**
     * 省/直辖市
     */
    province: string;
    /**
     * 市
     */
    city: string;
    /**
     * 区
     */
    district: string;

    // ///乡镇
    // // 该字段从v2.2.0版本起不再返回数据,建议您使用AMapSearchKit的逆地理功能获取.
    // township: string;

    // ///社区
    // // 该字段从v2.2.0版本起不再返回数据,建议您使用AMapSearchKit的逆地理功能获取.
    // neighborhood: string;

    // ///建筑
    // // 该字段从v2.2.0版本起不再返回数据,建议您使用AMapSearchKit的逆地理功能获取.
    // building: string;

    /**
     * 城市编码
     */
    citycode: string;
    /**
     * 区域编码
     */
    adcode: string;
    /**
     * 街道名称
     */
    street: string;
    /**
     * 门牌号
     */
    number: string;
    /**
     * 兴趣点名称
     */
    POIName: string;
    /**
     * 所属兴趣点名称
     */
    AOIName: string;
  }
  /**
   * 配置高德地图 Key
   * @param apiKey 获取key: https://lbs.amap.com/api/ios-location-sdk/guide/create-project/get-key
   */
  export function setApiKey(scheme: string): void;
  /**
   * 开始连续定位
   */
  export function start(): void;
  /**
   * 停止更新位置信息
   */
  export function stop(): void;
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
  export function setDesiredAccuracy(accuracy: 0 | 1 | 2 | 3 | 4 | 5): void;
  /**
   * 获取当前定位
   */
  export function getCurrentLocation(): Promise<Coordinates | ReGeocode>;
  /**
   * 连续定位是否返回逆地理信息，默认NO。
   * Android 默认返回逆地理编码，而 iOS 需要手动设置。
   * @platform ios
   */
  export function setLocatingWithReGeocode(isReGeocode: boolean): void;
  /**
   * 连续定位监听事件
   * @param {Function} listener 
   */
  export function addLocationListener(listener?: (location: Coordinates | ReGeocode) => void): void;
}
