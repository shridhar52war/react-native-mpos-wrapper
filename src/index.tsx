import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-mpos-wrapper' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const MposWrapper = NativeModules.MposWrapper
  ? NativeModules.MposWrapper
  : new Proxy(
    {},
    {
      get() {
        throw new Error(LINKING_ERROR);
      },
    }
  );

export function multiply(a: number, b: number): Promise<number> {
  return MposWrapper.multiply(a, b);
}

const configObect = {
  attestationHost: 'https://mpos-uat.fasspay.com:9001', //To set the attestation server’s URL. This field is mandatory if attestation is enabled.
  attestationHostCertPinning:
    'sha256/BJlJjxY7OHxhAz6yqy2gm58+qlP0AGwnBHDIG6zkhfU=', //  To set the attestation host’s certificate if SDK should verify with certificate pinning method.
  attestationHostReadTimeout: 10000,
  attestationRefreshInterval: 300000,
  attestationConnectionTimeout: 30000,
  /* To set the Google API Key for Google Safetynet Attestation purpose.
  This field is mandatory. This requires developer to register an
  account under Google Developer Console > Android Device
  Verification API. For more information, please visit this link:
  https://developer.android.com/training/safetynet/attestation.htm
  l#obtain-api-key*/
  googleApiKey: 'AIzaSyD9l4ImfUXhDAMz4Df5rdt7gItDy91fXTE',

  accessKey: 'KxNXmOxZmjoP3BHqp+XdQEp6BrRDOKZMCdJL85H9fsEeAg==', //To set the Access Key. This field is mandatory. Soft Space will provide the Access Key.
  secretKey: 'uPVB+RzDDs6R3V+6y+sDglrsK3sWn7lOqC97YsYJr/M=', //To set the Secret Key. This field is mandatory. Soft Space will provide the Secret Key.
  uniqueId: '', // To set the uniqueID provided by Soft Space.
  developerId: '', //To set the developerID provided by Soft Space.
};

export function init(): Promise<void> {
  return MposWrapper.init(configObect);
}

export function initializeTransaction(
  callback: (err?: Error, code?: number) => void //TODO: add config param to accept
): Promise<void> {
  return MposWrapper.initializeTransaction(callback);
}
