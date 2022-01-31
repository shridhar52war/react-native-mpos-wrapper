import { NativeModules, Platform, NativeEventEmitter } from 'react-native';
import type {
  ConfigObect,
  TransactionResult,
  TransactionOutcome,
} from './types';

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

const MposWrapperEmitter = NativeModules.MposWrapper
  ? new NativeEventEmitter(NativeModules.MposWrapper)
  : null;

export function init(configObect: ConfigObect): Promise<void> {
  return MposWrapper.init(configObect);
}

export function refreshToken(): void {
  return MposWrapper.refreshToken();
}

export function initializeTransaction(amount: string): void {
  return MposWrapper.initializeTransaction(amount);
}

export function voidTransaction(transactionId: string): void {
  return MposWrapper.voidTransaction(transactionId);
}

export function refundTransaction(transactionId: string): void {
  return MposWrapper.refundTransaction(transactionId);
}

export {
  MposWrapperEmitter,
  TransactionResult,
  ConfigObect,
  TransactionOutcome,
};
