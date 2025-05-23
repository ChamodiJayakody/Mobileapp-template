import React, { useEffect, useState } from 'react';
import { NativeEventEmitter, NativeModules, StyleSheet, View, Text } from 'react-native';

const { BarcodeScanner } = NativeModules;

const App = () => {
  const [scannedCode, setScannedCode] = useState<string>('');
  const [isScannerAvailable, setIsScannerAvailable] = useState<boolean>(false);

  useEffect(() => {
    if (!BarcodeScanner) {
      console.warn('BarcodeScanner module is not available');
      setIsScannerAvailable(false);
      return;
    }

    setIsScannerAvailable(true);
    const eventEmitter = new NativeEventEmitter(BarcodeScanner);
    
    const subscription = eventEmitter.addListener(
      'onBarcodeScanned',
      (event) => {
        console.log('Scanned barcode:', event.barcode);
        setScannedCode(event.barcode);
      }
    );

    // Start scanning when component mounts
    BarcodeScanner.startScanning();

    // Cleanup
    return () => {
      subscription.remove();
      BarcodeScanner.stopScanning();
    };
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.text}>
        {BarcodeScanner ? 'Scanning for barcodes...' : 'Scanner not available'}
      </Text>
      {scannedCode ? (
        <Text style={styles.scannedText}>Last scanned: {scannedCode}</Text>
      ) : null}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  text: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  scannedText: {
    fontSize: 16,
    textAlign: 'center',
    margin: 10,
    color: '#2196F3',
  },
});

export default App;