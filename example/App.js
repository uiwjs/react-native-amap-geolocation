import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, Button } from 'react-native';
import AMapGeolocation from '@uiw/react-native-amap-geolocation';

export default class App extends Component {
  state = {
    location: '',
    isListener: false,
  };
  componentDidMount() {
    let apiKey = ''
    if (Platform.OS === 'ios') {
      apiKey = '00b74444d56a1f9e036b608a52f0da33';
    }

    if (apiKey) {
      AMapGeolocation.setApiKey(apiKey);
    }
    AMapGeolocation.setDesiredAccuracy(3);
    // 指定所需的精度级别
    AMapGeolocation.setLocatingWithReGeocode(true);
    // 当设备可以正常联网时，还可以返回该定位点的对应的中国境内位置信息（包括：省、市、区/县以及详细地址）。
    AMapGeolocation.addLocationListener((location) => {
      console.log('返回定位信息', location);
      this.setState({
        location: JSON.stringify(location, null, 2),
      });
    });
  }
  getCurrentLocation = async () => {
    try {
      const json = await AMapGeolocation.getCurrentLocation();
      console.log('json:-json-->>>', json);
      this.setState({
        location: JSON.stringify(json, null, 2),
      })
    } catch (error) {
      console.log('json:-error-->>>', error);
    }
  }
  locationListener = () => {
    this.setState({
      isListener: !this.state.isListener
    }, () => {
      console.log('连续定位！', this.state.isListener ? '开启' : '关闭')
      if (this.state.isListener) {
        AMapGeolocation.start();
      } else {
        AMapGeolocation.stop();
      }
    })
  }
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>☆AMapGeolocation Example☆</Text>
        <Button
          onPress={this.getCurrentLocation}
          title="获取当前定位信息"
          color="#841584"
        />
        <Button
          onPress={this.locationListener}
          // title="获取当前定位信息"
          title={`${this.state.isListener ? '关闭' : '开启'}连续监听定位`}
          color="#841584"
        />
        <Text>
          {this.state.location}
        </Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
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
});
