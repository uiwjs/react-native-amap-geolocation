package com.uiwjs.react.geolocation;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;

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
        if (client != null) {
            client.onDestroy();
        }
    }

    @Override
    public String getName() {
        return "RNAMapGeolocation";
    }

    @ReactMethod
    public void getCurrentLocation(final Promise promise) {
        try {
            if (!client.isStarted()) {
                client.startLocation();
            }
            // System.out.println(mLastAMapLocation);
            promise.resolve(toJSON(mLastAMapLocation));
        } catch (Exception e) {
            promise.resolve(null);
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
    public void setApiKey(String key) {
        if (client != null) {
            client.onDestroy();
        }
        // 需要在初始化的额前面设置 key
        AMapLocationClient.setApiKey(key);
        if (client == null) {
            // 初始化定位
            client = new AMapLocationClient(reactContext.getApplicationContext());
            //设置定位参数
            client.setLocationOption(option);
            // 设置定位监听
            client.setLocationListener(locationListener);
            client.startLocation();
        }
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
     * 设置发起定位请求的时间间隔，单位：毫秒，默认值：2000毫秒
     */
    @ReactMethod
    public void setInterval(int interval) {
        option.setInterval(interval);
        client.setLocationOption(option);
    }

    /**
     * 设置是否单次定位
     */
    @ReactMethod
    public void setOnceLocation(boolean isOnceLocation) {
        option.setOnceLocation(isOnceLocation);
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
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.valueOf(mode));
        client.setLocationOption(option);
    }
    /**
     * 设置是否返回地址信息，默认返回地址信息
     * 默认值：true, 返回地址信息
     */
    @ReactMethod
    public void setNeedAddress(boolean value) {
        option.setNeedAddress(value);
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
