# react-native-mpos-wrapper

React-Native wrapper for softPos SDK

## Installation

```sh
npm install react-native-mpos-wrapper
```

## Usage

API design is work in progress. Idea is to have something similar implemented.

```js
import { init } from 'react-native-mpos-wrapper';

try {
  const { status } = await init(sdkConfig);
} catch (e) {
  //handle initialisation and attestation error
}
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
