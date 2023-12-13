/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, {useCallback, useEffect, useState} from 'react';
import {
  Alert,
  NativeModules,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
} from 'react-native';
import Braze from '@braze/react-native-sdk';

import {Colors, Header} from 'react-native/Libraries/NewAppScreen';
import AsyncStorage from '@react-native-async-storage/async-storage';

const ASYNC_STORAGE_KEY = 'notification-permission';

const storeData = async (value: any) => {
  try {
    await AsyncStorage.setItem(ASYNC_STORAGE_KEY, JSON.stringify(value));
  } catch (e) {
    // saving error
  }
};

function App(): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const [cards, setCards] = useState([]);

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  const notificationAlert = useCallback(async () => {
    const value = await AsyncStorage.getItem(ASYNC_STORAGE_KEY);
    const notificationEnabled =
      await NativeModules.Notification.isNotificationPermissionGranted();

    if (notificationEnabled === 2 || value === 'true') {
      return;
    }

    Alert.alert(
      'Request Permission',
      'Please allow notifications to receive updates from Braze.',
      [
        {
          text: 'Enable Notification',
          onPress: () => {
            storeData(true);
            NativeModules.Notification.requestPermission();
          },
        },
        {
          text: 'Cancel',
          onPress: () => {
            storeData(true);
          },
          style: 'cancel',
        },
      ],
      {cancelable: false},
    );
  }, []);

  useEffect(() => {
    Braze.changeUser('test-braze-user-013');
    Braze.setEmail('braze-test-user@braze.com');

    notificationAlert();

    // listen for updates as a result of card refreshes
    Braze.addListener(
      Braze.Events.CONTENT_CARDS_UPDATED,
      async (update: any) => {
        setCards(update.cards);
      },
    );
    // trigger a refresh of cards
    Braze.requestContentCardsRefresh();
  }, [notificationAlert]);

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <Header />
        <View
          style={{
            backgroundColor: isDarkMode ? Colors.black : Colors.white,
          }}>
          <Text style={styles.highlight}>RN Braze SDK Version 8.2.0</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});

export default App;
