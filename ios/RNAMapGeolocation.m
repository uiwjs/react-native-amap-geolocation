#import "RNAMapGeolocation.h"

@implementation RNAMapGeolocation {
    AMapLocationManager *_manager;
    CLLocationManager *_clManager;
    BOOL _isStarted;
}

RCT_EXPORT_MODULE()

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _manager = [[AMapLocationManager alloc] init];
        _manager.delegate = self;
        // https://lbs.amap.com/api/ios-location-sdk/guide/get-location/singlelocation
        // 由于苹果系统的首次定位结果为粗定位，其可能无法满足需要高精度定位的场景。
        // 所以，高德提供了 kCLLocationAccuracyBest 参数，设置该参数可以获取到精度在 10m 左右的定位结果，但是相应的需要付出比较长的时间（10s左右），越高的精度需要持续定位时间越长。
        // 推荐：kCLLocationAccuracyHundredMeters，一次还不错的定位，偏差在百米左右，超时时间设置在2s-3s左右即可。
        [_manager setDesiredAccuracy: kCLLocationAccuracyHundredMeters];
        // 设置不允许系统暂停定位
        [_manager setPausesLocationUpdatesAutomatically: NO];
        // 设置允许在后台定位
        [_manager setAllowsBackgroundLocationUpdates: YES];
        // 设置允许连续定位逆地理
        [_manager setLocatingWithReGeocode:YES];
        
        [[AMapServices sharedServices] setEnableHTTPS:YES];

        _clManager = [CLLocationManager new];
        _isStarted = NO;
        _isStarted = NO;

    }
    return self;
}

RCT_EXPORT_METHOD(setApiKey: (NSString *)apiKey) {
    [AMapServices sharedServices].apiKey = apiKey;
}

// 定位超时时间，最低2s，默认设置为2s
RCT_EXPORT_METHOD(setLocationTimeout: (int)value) {
    _manager.locationTimeout = value;
}
// 逆地理请求超时时间，最低2s，默认设置为2s
RCT_EXPORT_METHOD(setReGeocodeTimeout: (int)value) {
    _manager.reGeocodeTimeout = value;
}
// 用于指定所需的精度级别。 定位服务将尽最大努力达到您想要的精度。 但是，不能保证。
// 为了优化电源性能，请确保为您的使用情况指定适当的精度（例如，当仅需要粗略位置时，使用较大的精度值）。
// 0 => kCLLocationAccuracyBestForNavigation
// 1 => kCLLocationAccuracyBest
// 2 => kCLLocationAccuracyNearestTenMeters
// 3 => kCLLocationAccuracyHundredMeters
// 4 => kCLLocationAccuracyKilometer
// 5 => kCLLocationAccuracyThreeKilometers
RCT_EXPORT_METHOD(setDesiredAccuracy: (NSInteger)accuracy) {
    switch (accuracy) {
        case 0:
            [_manager setDesiredAccuracy: kCLLocationAccuracyBestForNavigation];
            break;
        case 1:
            [_manager setDesiredAccuracy: kCLLocationAccuracyBest];
            break;
        case 2:
            [_manager setDesiredAccuracy: kCLLocationAccuracyNearestTenMeters];
            break;
        case 3:
            [_manager setDesiredAccuracy: kCLLocationAccuracyHundredMeters];
            break;
        case 4:
            [_manager setDesiredAccuracy: kCLLocationAccuracyKilometer];
            break;
        case 5:
            [_manager setDesiredAccuracy: kCLLocationAccuracyThreeKilometers];
            break;
        default:
            break;
    }
}

// 是否已经开始定位了
RCT_EXPORT_METHOD(isStarted) {
    _isStarted = YES;
    [_manager startUpdatingLocation];
}


RCT_EXPORT_METHOD(isStarted: resolver: (RCTPromiseResolveBlock)resolve rejecter: (RCTPromiseRejectBlock)reject) {
    resolve(@[@(_isStarted)]);
}

// 开始定位
RCT_EXPORT_METHOD(start) {
    _isStarted = YES;
    [_manager startUpdatingLocation];
}

// 停止更新位置信息
RCT_EXPORT_METHOD(stop) {
    _isStarted = NO;
    [_manager stopUpdatingLocation];
}

// 连续定位是否返回逆地理信息，默认NO。
RCT_EXPORT_METHOD(setLocatingWithReGeocode: (BOOL)value) {
    [_manager setLocatingWithReGeocode: value];
}

// 获取当前定位
RCT_EXPORT_METHOD(getCurrentLocation: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    [_clManager requestWhenInUseAuthorization];
    // - kCLAuthorizationStatusAuthorizedWhenInUse 用户已授权仅在使用您的应用程序时使用其位置。 
    // - kCLAuthorizationStatusAuthorizedAlways 用户已授权在任何时间使用其位置。
    // - kCLAuthorizationStatusNotDetermined 用户尚未对此应用做出选择
    if (status == kCLAuthorizationStatusAuthorizedAlways || status ==  kCLAuthorizationStatusAuthorizedWhenInUse || status ==  kCLAuthorizationStatusNotDetermined) {
        [_manager requestLocationWithReGeocode: YES completionBlock:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
            if (error) {
                reject([NSString stringWithFormat:@"%ld",(long)error.code], error.localizedDescription, error);
            } else {
                NSLog(@"location:定位信息:%@", location);
                NSLog(@"regeocode:逆地理信息:%@", regeocode);
                if (regeocode) {
                    NSLog(@"reGeocode:逆地理信息:%@", regeocode);
                }
                 id json = [self json:location reGeocode:regeocode];
                 [NSUserDefaults.standardUserDefaults setObject: json forKey: RNAMapGeolocation.storeKey];
                 resolve(json);
            }
        }];
    } else {
         reject(@"-10086", @"location unauthorized", nil);
    }
}

+ (NSString *)storeKey {
    return @"RNAMapGeolocation";
}

- (id)json:(CLLocation *)location reGeocode:(AMapLocationReGeocode *)reGeocode {
    
    BOOL flag = AMapLocationDataAvailableForCoordinate(location.coordinate);
    // 逆地理信息
    if (reGeocode) {
        return @{
            @"accuracy" : @(location.horizontalAccuracy),
            @"latitude" : @(location.coordinate.latitude),
            @"longitude" : @(location.coordinate.longitude),
            @"altitude" : @(location.altitude),
            @"speed" : @(location.speed),
            @"heading" : @(location.course),
            @"timestamp" : @(location.timestamp.timeIntervalSince1970 * 1000),
            @"isAvailableCoordinate": @(flag),

            @"address" : reGeocode.formattedAddress ? reGeocode.formattedAddress : @"",
            @"country" : reGeocode.country ? reGeocode.country : @"",
            @"province" : reGeocode.province ? reGeocode.province : @"",
            @"city" : reGeocode.city ? reGeocode.city : @"",
            @"district" : reGeocode.district ? reGeocode.district : @"",
            @"cityCode" : reGeocode.citycode ? reGeocode.citycode : @"",
            @"adCode" : reGeocode.adcode ? reGeocode.adcode : @"",
            @"street" : reGeocode.street ? reGeocode.street : @"",
            @"streetNumber" : reGeocode.number ? reGeocode.number : @"",
            @"poiName" : reGeocode.POIName ? reGeocode.POIName : @"",
            @"aoiName" : reGeocode.AOIName ? reGeocode.AOIName : @"",
        };
       
    } else {
        // 定位信息
        return @{
             @"accuracy": @(location.horizontalAccuracy),
             @"latitude": @(location.coordinate.latitude),
             @"longitude": @(location.coordinate.longitude),
             @"isAvailableCoordinate": @(flag),
             @"altitude": @(location.altitude),
             @"speed": @(location.speed),
             @"heading": @(location.course),
             @"timestamp": @(location.timestamp.timeIntervalSince1970 * 1000),
        };
    }
}

/**
 *  @brief 当plist配置NSLocationAlwaysUsageDescription或者NSLocationAlwaysAndWhenInUseUsageDescription，
 *  并且[CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined，会调用代理的此方法。
 *  此方法实现申请后台权限API即可：[locationManager requestAlwaysAuthorization](必须调用,不然无法正常获取定位权限)
 *  @param manager 地理围栏管理类。
 *  @param locationManager  需要申请后台定位权限的locationManager。
 *  @since 2.6.2
 */
- (void)amapLocationManager:(AMapLocationManager *)manager
      doRequireLocationAuth:(CLLocationManager *)locationManager {
  [locationManager requestAlwaysAuthorization];
}

- (NSArray<NSString *> *)supportedEvents {
  return @[ @"AMapGeolocation" ];
}

/**
 *  @brief 连续定位回调函数.注意：如果实现了本方法，则定位信息不会通过amapLocationManager:didUpdateLocation:方法回调。
 *  @param manager 定位 AMapLocationManager 类。
 *  @param location 定位结果。
 *  @param reGeocode 逆地理信息。
 */
- (void)amapLocationManager:(AMapLocationManager *)manager
    didUpdateLocation:(CLLocation *)location
    reGeocode:(AMapLocationReGeocode *)reGeocode
{
    id json = [self json:location reGeocode:reGeocode];
    [self sendEventWithName: @"AMapGeolocation" body:json];
}

/**
 *  @brief 当定位发生错误时，会调用代理的此方法。
 *  @param manager 定位 AMapLocationManager 类。
 *  @param error 返回的错误，参考 CLError 。
 */
 - (void)amapLocationManager:(AMapLocationManager *)manager didFailWithError:(NSError *)error {
   [self sendEventWithName: @"AMapGeolocation"
    body: @{
        @"errorCode": @(error.code),
        @"errorInfo": error.localizedDescription,
    }];
 }

@end
