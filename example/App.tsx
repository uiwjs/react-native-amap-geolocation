/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, {useEffect, useState} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
  Platform,
  Button,
} from 'react-native';
import AMapGeolocation from '@uiw/react-native-amap-geolocation';
import {Colors} from 'react-native/Libraries/NewAppScreen';

function App(): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };
  const [data, setData] = useState({
    location: '',
    isListener: false,
    isStarted: false,
    isGps: false,
    isLocationCacheEnable: true,
  });
  useEffect(() => {
    let apiKey = '';
    if (Platform.OS === 'ios') {
      apiKey = '00b74444d56a1f9e036b608a52f0da33';
    }
    if (Platform.OS === 'android') {
      apiKey = '5084df66535c2663b89c60b11661b212';
    }
    if (apiKey) {
      try {
        AMapGeolocation.setApiKey(apiKey);
      } catch (error) {
        console.log('error:', error);
      }
    }
    // iOS 指定所需的精度级别
    AMapGeolocation.setDesiredAccuracy(3);
    // Android 指定所需的精度级别，可选设置，默认 高精度定位模式
    AMapGeolocation.setLocationMode(1);
    // 定位是否返回逆地理信息
    AMapGeolocation.setLocatingWithReGeocode(true);
    // 当设备可以正常联网时，还可以返回该定位点的对应的中国境内位置信息（包括：省、市、区/县以及详细地址）。
    AMapGeolocation.addLocationListener(location => {
      console.log('返回定位信息', location);
      setData({...data, location: JSON.stringify(location, null, 2)});
    });
    // 开启监听
    AMapGeolocation.start();
    console.log(
      'AMapGeolocation.addLocationListener',
      AMapGeolocation.startUpdatingHeading,
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const getLocationState = async () => {
    const isStarted = await AMapGeolocation.isStarted();
    if (isStarted) {
      setData({...data, isStarted});
    }
  };
  const coordinateConvert = async () => {
    try {
      // 将百度地图转换为 高德地图 经纬度
      const result = await AMapGeolocation.coordinateConvert(
        {
          latitude: 40.002172,
          longitude: 116.467357,
        },
        0,
      );
      console.log('~coordinateConvert~~', result);
    } catch (error) {
      console.log('~coordinateConvert:error~~', error);
    }
  };
  const getCurrentLocation = async () => {
    try {
      console.log('json:-getCurrentLocation-2->>> 获取当前定位信息');
      AMapGeolocation.start();
      console.log('json:-getCurrentLocation-->>> 获取当前定位信息');
      const json = await AMapGeolocation.getCurrentLocation();
      console.log('json:-json-->>>', json);
    } catch (error) {
      console.log('json:-error-->>>', error);
      if (error instanceof Error) {
        console.log('json:-error-->>>', error.message);
      }
    }
  };
  const locationListener = () => {
    setData({...data, isListener: !data.isListener});
  };
  useEffect(() => {
    if (data.isListener) {
      AMapGeolocation.start();
    } else {
      AMapGeolocation.stop();
    }
  }, [data.isListener]);
  /** Android 是否开启 gps 优先 */
  const gpsFirst = () => {
    if (Platform.OS === 'android') {
      setData({...data, isGps: !data.isGps});
    }
  };
  useEffect(() => AMapGeolocation.setGpsFirst(data.isGps), [data.isGps]);
  /** 开启缓存定位 */
  const setLocationCache = () => {
    if (Platform.OS === 'android') {
      setData({...data, isLocationCacheEnable: !data.isLocationCacheEnable});
    }
  };
  useEffect(
    () => AMapGeolocation.setLocationCacheEnable(data.isLocationCacheEnable),
    [data.isLocationCacheEnable],
  );

  const gpsFirstLabel = `${data.isGps ? '关闭:' : '开启:'}androidGPS优先`;

  return (
    <SafeAreaView>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <View
          style={{
            backgroundColor: isDarkMode ? Colors.black : Colors.white,
          }}>
          <Text style={styles.welcome}>☆AMapGeolocation Example☆</Text>
          <Button
            onPress={getLocationState}
            title={`获取连续定位状态: ${data.isStarted.toString()}`}
            color="#841584"
          />
          <Button
            onPress={coordinateConvert}
            title={'百度地图 => 高德地图经纬度'}
            color="#841584"
          />
          <Button
            onPress={getCurrentLocation}
            title="获取当前定位信息"
            color="#841584"
          />
          <Button
            onPress={locationListener}
            // title="获取当前定位信息"
            title={`${data.isListener ? '关闭' : '开启'}连续监听定位`}
            color="#841584"
          />
          <Button
            onPress={gpsFirst}
            // title="是否开启GPS优先"
            title={gpsFirstLabel}
            color="#841584"
          />
          <Button
            onPress={setLocationCache}
            // title="是否使用缓存定位"
            title={`${
              data.isLocationCacheEnable ? '关闭:' : '开启:'
            }android使用缓存定位`}
            color="#841584"
          />
          <ScrollView style={styles.scroll}>
            <Text>{data.location}</Text>
          </ScrollView>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

export default App;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    // justifyContent: 'center',
    // alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  scroll: {
    flex: 1,
  },
});
