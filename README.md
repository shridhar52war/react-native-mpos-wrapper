# react-native-mpos-wrapper

React-Native wrapper for softPos SDK. This module is built using the instructions mentioned [here](https://reactnative.dev/docs/native-modules-setup)

## Pre-requisites

The wrapper includes all the nesessary libraries which are listed in the MPOS SDK v3.4.0 specification. Contact `shridhar.b@razorpay.com` for SDK specification.

## Installation

```sh
npm install react-native-mpos-wrapper
```

## Usage

The API is still WIP. This section will be updated once testing is done e2e.

```js
import {
  init,
  initializeTransaction,
  refreshToken,
  MposWrapperEmitter,
} from 'react-native-mpos-wrapper';

// inside React Component
useEffect(() => {
  MposWrapperEmitter.addListener('RefreshToken', (data) => {
    // Handle data
  });
  MposWrapperEmitter.addListener('TransactionResult', (data) => {
    // Handle data
  });
  MposWrapperEmitter.addListener('TransactionUIEvent', (data) => {
    // Handle data
  });
}, []);

// on CTA click(s)
init(SdkConfig);
refreshToken();
initializeTransaction(amount);
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
