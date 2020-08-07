package com.uiwjs.react.geolocation;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.CoordinateConverter.CoordType;
import com.amap.api.location.DPoint;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReadableMap;

public class RNAMapGeolocationModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;
    // 声明AMapLocationClient类对象
    private AMapLocationClient client;
    // 初始化 AMapLocationClientOption 对象
    private AMapLocationClientOption option = new AMapLocationClientOption();
    private AMapLocation mLastAMapLocation;

    public RNAMapGeolocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public void setApiKey(String key) {
        if (client != null) {
            client = null;
        }
        // 通过SDK提供的 `setApiKey(String key);` 接口设置Key，注意Key设置要在SDK业务初始化之前。
        // 需要在初始化的额前面设置 key
        AMapLocationClient.setApiKey(key);
        // 初始化定位
        client = new AMapLocationClient(reactContext.getApplicationContext());
        //设置定位参数
        client.setLocationOption(option);
        // 设置定位监听
        client.setLocationListener(locationListener);
        // client.startLocation();
    }

    @Override
    public String getName() {
        return "RNAMapGeolocation";
    }

    @ReactMethod
    public void getCurrentLocation(final Promise promise) {
        try {
            if (client == null) {
                promise.reject("-1", "尚未调用 setApiKey() 进行初始化");
                return;
            }
            if (!client.isStarted()) {
                client.startLocation();
            }
            // System.out.println(mLastAMapLocation);
            promise.resolve(toJSON(mLastAMapLocation));
            client.stopLocation();
        } catch (Exception e) {
            promise.reject("-110", e.getMessage());
        }
    }

    @ReactMethod
    public void getLastKnownLocation(Promise promise) {
        if (client == null) {
            promise.reject("-1", "尚未调用 setApiKey() 进行初始化");
            return;
        }
        AMapLocation location = client.getLastKnownLocation();
        promise.resolve(toJSON(location));
    }

    @ReactMethod
    public void start() {
        if (client == null) {
            return;
        }
        client.startLocation();
    }

    @ReactMethod
    public void stop() {
        if (client == null) {
            return;
        }
        client.stopLocation();
    }
    @ReactMethod
    public void isStarted(Promise promise) {
        if (client == null) {
            promise.reject("-1", "尚未调用 setApiKey() 进行初始化");
            return;
        }
        promise.resolve(client.isStarted());
    }
	/**
	 * 默认的定位参数
	 */
	// private AMapLocationClientOption getDefaultOption(){
	// 	AMapLocationClientOption mOption = new AMapLocationClientOption();
	// 	mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
	// 	mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
	// 	mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
	// 	mOption.setInterval(2000); //可选，设置定位间隔。默认为2秒
	// 	mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
	// 	mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
	// 	mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
	// 	AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
	// 	mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
	// 	mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
	// 	mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
	// 	mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
	// 	return mOption;
	// }

    /**
     * 设置网络请求的协议, 默认值：HTTP
     * @param amapLocationProtocol 可选协议类型:HTTP, HTTPS -wx
     */
    public void setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol
    amapLocationProtocol) {
        if (client == null) {
            return;
        }
        option.setLocationProtocol(amapLocationProtocol);
        client.setLocationOption(option);
    }

    /**
     * 设置网络请求超时时间,默认:30秒-wx
     */
    @ReactMethod
    public void setHttpTimeOut(int httpTimeOut) {
        if (client == null) {
            return;
        }
        option.setHttpTimeOut(httpTimeOut);
        client.setLocationOption(option);
    }
    /**
     * 设置是否使用缓存策略-wx
     */
    @ReactMethod
    public void setLocationCacheEnable(boolean isLocationCacheEnable) {
        if (client == null) {
            return;
        }
        option.setLocationCacheEnable(isLocationCacheEnable);
        client.setLocationOption(option);
    }
    /**
     * 设置是否等待wifi刷新-wx
     */
    @ReactMethod
    public void setOnceLocationLatest(boolean isOnceLocationLatest) {
        if (client == null) {
            return;
        }
        option.setOnceLocationLatest(isOnceLocationLatest);
        client.setLocationOption(option);
    }
    /**
     * 设置是否gps优先-wx
     */
    @ReactMethod
    public void setGpsFirst(boolean isSetGpsFirst) {
        if (client == null) {
            return;
        }
        option.setGpsFirst(isSetGpsFirst);
        client.setLocationOption(option);
    }
    /**
     * 设置发起定位请求的时间间隔，单位：毫秒，默认值：2000毫秒
     */
    @ReactMethod
    public void setInterval(int interval) {
        if (client == null) {
            return;
        }
        option.setInterval(interval);
        client.setLocationOption(option);
    }

    /**
     * 设置是否单次定位
     */
    @ReactMethod
    public void setOnceLocation(boolean isOnceLocation) {
        if (client == null) {
            return;
        }
        option.setOnceLocation(isOnceLocation);
        client.setLocationOption(option);
    }
    /**
     * 设置逆地理信息的语言,目前之中中文和英文
     */
    @ReactMethod
    public void setGeoLanguage(String language) {
        if (client == null) {
            return;
        }
        option.setGeoLanguage(AMapLocationClientOption.GeoLanguage.valueOf(language));
        client.setLocationOption(option);
    }

    /**
     * 设置定位模式。默认值：Hight_Accuracy 高精度模式
     * android 默认定位模式，目前支持三种定位模式
     * - `Hight_Accuracy` 高精度定位模式：在这种定位模式下，将同时使用高德网络定位和卫星定位,优先返回精度高的定位
     * - `Battery_Saving` 低功耗定位模式：在这种模式下，将只使用高德网络定位
     * - `Device_Sensors` 仅设备定位模式：在这种模式下，将只使用卫星定位。
     */
    @ReactMethod
    public void setLocationMode(String mode) {
        if (client == null) {
            return;
        }
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.valueOf(mode));
        client.setLocationOption(option);
    }
    /**
     * 设置是否返回地址信息，默认返回地址信息
     * 默认值：true, 返回地址信息
     */
    @ReactMethod
    public void setNeedAddress(boolean value) {
        if (client == null) {
            return;
        }
        option.setNeedAddress(value);
        client.setLocationOption(option);
    }
    @ReactMethod
    public void coordinateConvert(ReadableMap point, int typer, final Promise promise) {
        if (client == null) {
            return;
        }
        try {
            // { latitude: 40.002172, longitude: 116.467357 }
            // 构造一个示例坐标，第一个参数是纬度，第二个参数是经度
            DPoint pointer = new DPoint(point.getDouble("latitude"), point.getDouble("longitude"));
            // DPoint pointer = new DPoint(39.911127, 116.433608);
            // 初始化坐标转换类
            CoordinateConverter converter = new CoordinateConverter(reactContext.getApplicationContext());
            /**
            * 设置坐标来源,这里使用百度坐标作为示例
            * 可选的来源包括：
            * - CoordType.BAIDU: 百度坐标
            * - CoordType.MAPBAR: 图吧坐标
            * - CoordType.MAPABC: 图盟坐标
            * - CoordType.SOSOMAP: 搜搜坐标
            * - CoordType.ALIYUN: 阿里云坐标
            * - CoordType.GOOGLE: 谷歌坐标
            * - CoordType.GPS: GPS坐标
            */
            switch (typer) {
                case 0:
                    converter.from(CoordType.BAIDU);
                    break;
                case 1:
                    converter.from(CoordType.MAPBAR);
                    break;
                case 2:
                    converter.from(CoordType.MAPABC);
                    break;
                case 3:
                    converter.from(CoordType.SOSOMAP);
                    break;
                case 4:
                    converter.from(CoordType.ALIYUN);
                    break;
                case 5:
                    converter.from(CoordType.GOOGLE);
                    break;
                case 6:
                    converter.from(CoordType.GPS);
                    break;
                    default: break;
            }
            converter.coord(pointer);
            // 转换成高德坐标
            DPoint destPoint = converter.convert();
            WritableMap map = Arguments.createMap();
            map.putDouble("latitude", destPoint.getLatitude());
            map.putDouble("longitude", destPoint.getLongitude());
            promise.resolve(map);
        } catch (Exception e) {
            promise.reject("-11", e.getMessage());
        }
    }

    /**
     * 设置是否允许调用WIFI刷新 默认值为true，当设置为false时会停止主动调用WIFI刷新，将会极大程度影响定位精度，但可以有效的降低定位耗电
     * 默认值：true, 返回地址信息
     */
    @ReactMethod
    public void setWifiScan(boolean isWifiPassiveScan) {
        if (client == null) {
            return;
        }
        option.setWifiScan(isWifiPassiveScan);
        client.setLocationOption(option);
    }

    /**
     * 设置是否使用设备传感器。是否开启设备传感器，当设置为true时，网络定位可以返回海拔、角度和速度。
     * [高德地图 setSensorEnable 文档](http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Location_Doc/index.html)
     * @default false
     * @platform android
     */
    @ReactMethod
    public void setSensorEnable(boolean sensorEnable) {
        if (client == null) {
            return;
        }
        option.setSensorEnable(sensorEnable);
        client.setLocationOption(option);
    }

    private DeviceEventManagerModule.RCTDeviceEventEmitter getDeviceEventEmitter() {
        if (eventEmitter == null) {
            eventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        }
        return eventEmitter;
    };
    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            mLastAMapLocation = location;
            getDeviceEventEmitter().emit("AMapGeolocation", toJSON(location));
        }
    };
    private WritableMap toJSON(AMapLocation location) {
        WritableMap map = Arguments.createMap();
        if (location == null) {
            return null;
        }
        map.putInt("errorCode", location.getErrorCode());
        map.putString("errorInfo", location.getErrorInfo());
        if (location.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            map.putDouble("accuracy", location.getAccuracy());
            map.putDouble("latitude", location.getLatitude());
            map.putDouble("longitude", location.getLongitude());
            map.putDouble("altitude", location.getAltitude());
            map.putDouble("speed", location.getSpeed());
            // 获取方向角(单位：度）
            map.putDouble("heading", location.getBearing());
            map.putDouble("timestamp", location.getTime());
            if (!location.getAddress().isEmpty()) {
                map.putString("address", location.getAddress());
                map.putString("country", location.getCountry());
                map.putString("province", location.getProvince());
                map.putString("city", location.getCity());
                map.putString("district", location.getDistrict());
                map.putString("cityCode", location.getCityCode());
                map.putString("adCode", location.getAdCode());
                map.putString("street", location.getStreet());
                map.putString("streetNumber", location.getStreetNum());
                map.putString("poiName", location.getPoiName());
                map.putString("aoiName", location.getAoiName());
            }
            // --------------------
            // 以上与 iOS 相同字段


            // 获取坐标系类型 高德定位sdk会返回两种坐标系：
            // 坐标系 AMapLocation.COORD_TYPE_GCJ02 -- GCJ02
            // 坐标系 AMapLocation.COORD_TYPE_WGS84 -- WGS84
            // 国外定位时返回的是WGS84坐标系
            map.putString("coordType", location.getCoordType());
            // 获取位置语义信息
            map.putString("description", location.getDescription());

            // 返回支持室内定位的建筑物ID信息
            map.putString("buildingId", location.getBuildingId());
            // 室内外置信度
            // 室内：且置信度取值在[1 ～ 100]，值越大在在室内的可能性越大
            // 室外：且置信度取值在[-100 ～ -1] ,值越小在在室内的可能性越大
            // 无法识别室内外：置信度返回值为 0
            map.putInt("conScenario", location.getConScenario());
            // 获取室内定位的楼层信息
            map.putString("floor", location.getFloor());
            // 获取卫星信号强度，仅在卫星定位时有效,值为：
            // #GPS_ACCURACY_BAD，#GPS_ACCURACY_GOOD，#GPS_ACCURACY_UNKNOWN
            map.putInt("gpsAccuracyStatus", location.getGpsAccuracyStatus());
            // 获取定位信息描述
            map.putString("locationDetail", location.getLocationDetail());

            map.putInt("locationType", location.getLocationType());
            map.putString("provider", location.getProvider());
            map.putInt("satellites", location.getSatellites());
            map.putInt("trustedLevel", location.getTrustedLevel());


            WritableMap qualityReportMap = Arguments.createMap();
            // 获取定位质量
            AMapLocationQualityReport aMapLocationQualityReport = location.getLocationQualityReport();
            String adviseMessage = aMapLocationQualityReport.getAdviseMessage();
            if (adviseMessage != null) {
                adviseMessage = adviseMessage.replaceAll("[\\t\\n\\r/]", "");
            }
            qualityReportMap.putString("adviseMessage", adviseMessage);
            qualityReportMap.putInt("gpsSatellites", aMapLocationQualityReport.getGPSSatellites());
            qualityReportMap.putInt("gpsStatus", aMapLocationQualityReport.getGPSStatus());
            qualityReportMap.putDouble("netUseTime", aMapLocationQualityReport.getNetUseTime());
            qualityReportMap.putString("networkType", aMapLocationQualityReport.getNetworkType());
            qualityReportMap.putBoolean("isInstalledHighDangerMockApp", aMapLocationQualityReport.isInstalledHighDangerMockApp());
            qualityReportMap.putBoolean("isWifiAble", aMapLocationQualityReport.isWifiAble());
            map.putMap("locationQualityReport", qualityReportMap);

        }
        return map;
    }
}
