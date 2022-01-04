package com.reactnativemposwrapper;

import static my.com.softspace.ssmpossdk.transaction.MPOSTransaction.TransactionEvents.TransactionResult;


//int TransactionDeclined = 7004;
//  int TransactionFailed = 7005;
//  int TransactionNoAppError = 7006;
//  int TransactionFailedAllowFallback = 7007;
//  int TransactionCardExpired = 7008;
//  int TransactionOnlineFail = 7020;
//  int TransactionCancel = 7024;
//  int TransactionTimeout = 7028;
//  int TransactionCardError = 7030;
//  int TransactionRequireCDCVM = 7054;

import android.app.Activity;
import android.app.Application;

import java.util.logging.Logger;
import java.util.logging.Level;


import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.UiThreadUtil;

import my.com.softspace.ssmpossdk.Environment;
import my.com.softspace.ssmpossdk.SSMPOSSDK;
import my.com.softspace.ssmpossdk.SSMPOSSDKConfiguration;
import my.com.softspace.ssmpossdk.transaction.MPOSTransaction;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionOutcome;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionParams;

import com.facebook.react.modules.core.DeviceEventManagerModule;

@ReactModule(name = MposWrapperModule.NAME)
public class MposWrapperModule extends ReactContextBaseJavaModule {
  public static final String NAME = "MposWrapper";
  public static final String ERROR = "ERROR";
  private static final String REFRESH_TOKEN_ACTION = "REFRESH_TOKEN_ACTION";
  private static final String INITIALIZE_TRANSACTION_ACTION = "INITIALIZE_TRANSACTION_ACTION";
  private static final String VOID_TRANSACTION_ACTION = "VOID_TRANSACTION_ACTION";
  private static final String REFUND_TRANSACTION_ACTION = "REFUND_TRANSACTION_ACTION";
  private static final String TRANSACTION_UI_EVENT = "TRANSACTION_UI_EVENT";
  private static final String TRANSACTION_RESULT_EVENT = "TRANSACTION_RESULT_EVENT";


  private Application application;
  private ReactApplicationContext reactContext;
  private Activity _activity;
  private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter = null;

  public MposWrapperModule(ReactApplicationContext reactContext) {
    super(reactContext);
    if (SSMPOSSDK.isRunningOnRemoteProcess(reactContext)) {
      System.out.println("isRunningOnRemoteProcess....");
      return;
    }
    this.reactContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void init(ReadableMap initConfig, Promise promise) {
    this._activity = getCurrentActivity();
    System.out.println("Inside initFasstapSDK--------");
    try {
      SSMPOSSDKConfiguration config = SSMPOSSDKConfiguration.Builder.create()
        .setAttestationHost(initConfig.getString("attestationHost"))
        .setAttestationHostCertPinning(initConfig.getString("attestationHostCertPinning"))
        .setAttestationHostReadTimeout(10000L)
        .setAttestationRefreshInterval(300000L)
        .setAttestationStrictHttp(true)
        .setAttestationConnectionTimeout(30000L)
        .setLibGoogleApiKey(initConfig.getString("googleApiKey"))
        .setLibAccessKey(initConfig.getString("accessKey"))
        .setLibSecretKey(initConfig.getString("secretKey"))
        .setUniqueID(initConfig.getString("uniqueId"))
        .setDeveloperID(initConfig.getString("developerId"))
        .setEnvironment(Environment.UAT)
        .build();

      ReactApplicationContext currentReactContext = this.reactContext;
      Activity currentActivity = this._activity;

      UiThreadUtil.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          SSMPOSSDK.init(currentReactContext, config);
          System.out.println("SDK Version: " + SSMPOSSDK.getInstance().getSdkVersion());
          System.out.println("COTS ID: " + SSMPOSSDK.getInstance().getCotsId());

          if (!SSMPOSSDK.hasRequiredPermission(currentReactContext)) {
            SSMPOSSDK.requestPermissionIfRequired(currentActivity, 1000);
          }
          promise.resolve("Successfully Initialised the SDK");
        }
      });

    } catch (Exception e) {
      Logger logger = Logger.getAnonymousLogger();
      logger.log(Level.SEVERE, "Catch Error", e);
      promise.reject(e);
    }
  }

  public void sendEvent(String eventName, ReadableMap params) {
    if (eventEmitter == null) {
      eventEmitter = getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    }
    if (eventEmitter != null) {
      eventEmitter.emit(eventName, params);
    }
  }

  private void sendTransactionResultEvent(String action, Integer result, ReadableMap transactionOutcome) {
    WritableMap params = Arguments.createMap();
    params.putString("action", action);
    params.putString("eventName", TRANSACTION_RESULT_EVENT);
    params.putInt("transactionResult", result);
    params.putMap("transactionOutcome", transactionOutcome);
    sendEvent(action, params);
  }

  private void sendTransactionUiEvent(String action, Integer eventValue) {
    WritableMap params = Arguments.createMap();
    params.putString("action", action);
    params.putString("eventName", TRANSACTION_UI_EVENT);
    params.putInt("transactionResult", eventValue);
    sendEvent(action, params);
  }

  private void sendErrorEvent(String action, Exception exception) {
    WritableMap params = Arguments.createMap();
    params.putString("action", action);
    params.putString("eventName", ERROR);
    params.putMap("exception", (ReadableMap) exception);
    sendEvent(action, params);
  }

  private void uploadSignature() {
    //writeLog("uploadSignature()");
    System.out.println("uploadSignature method......");
    String base64SignatureString = "shri_=="; // your signature image base64 string

    try {
      MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
        .setSignature(base64SignatureString)
        .build();

      SSMPOSSDK.getInstance().getTransaction().uploadSignature(transactionalParams);

    } catch (Exception e) {
      Logger logger = Logger.getAnonymousLogger();
      logger.log(Level.SEVERE, "Catch Error uploadSignature", e);
    }
  }

  @ReactMethod
  public void refreshToken() {
    Activity _currentActivity = getCurrentActivity();
    System.out.println("Inside refreshToken method......");
    //SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().uniqueID = "rzp01";
    //SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().developerID = "9nD9hrW8EMWB375";
    // System.out.println("XXXXXXXXXXXXXXX" + SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().uniqueID + SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().developerID);
    SSMPOSSDK.getInstance().getTransaction().refreshToken(_currentActivity, new MPOSTransaction.TransactionEvents() {
      @Override
      public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
        System.out.println("onTransactionResult in refreshToken:: " + result);
        WritableMap writableMap = Arguments.createMap();
        if (transactionOutcome != null) {
          writableMap.putString("statusCode", transactionOutcome.getStatusCode());
          writableMap.putString("statusMessage", transactionOutcome.getStatusMessage());
        }
        sendTransactionResultEvent(REFRESH_TOKEN_ACTION, result, writableMap);
      }

      @Override
      public void onTransactionUIEvent(int event) {
        System.out.println("onTransactionUIEvent refreshToken :: " + event);
        sendTransactionUiEvent(REFRESH_TOKEN_ACTION, event);
      }
    });
  }


  @ReactMethod
  public void initializeTransaction(String amount) {
    // Accept config as param to set amount and other transactional related data.
    Activity _activityContext = getCurrentActivity();
    try {
      MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
        .setAmount(amount)
        .build();
      System.out.println("Initialising transaction........");
      SSMPOSSDK.getInstance().getTransaction().startTransaction(_activityContext, transactionalParams, new MPOSTransaction.TransactionEvents() {
        @Override
        public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
          System.out.println(" onTransactionResult -----result : " + result);
          WritableMap writableMap = Arguments.createMap();
          if (result == 0) {
            if (transactionOutcome != null) {
              writableMap.putString("transactionID", transactionOutcome.getTransactionID());
              writableMap.putString("approvalCode", transactionOutcome.getApprovalCode());
              writableMap.putString("cardNo", transactionOutcome.getCardNo());
              writableMap.putString("cardHolderName", transactionOutcome.getCardHolderName());
              writableMap.putString("transactionId", transactionOutcome.getTransactionID());
              writableMap.putString("approvalCode", transactionOutcome.getApprovalCode());
            }
          }
          sendTransactionResultEvent(INITIALIZE_TRANSACTION_ACTION, result, writableMap);
        }

        @Override
        public void onTransactionUIEvent(int event) {
          System.out.println("onTransactionUIEvent" + event);
          sendTransactionUiEvent(INITIALIZE_TRANSACTION_ACTION, event);
        }
      });
    } catch (Exception e) {
      Logger logger = Logger.getAnonymousLogger();
      logger.log(Level.SEVERE, "Catch Error Transaction", e);
      sendErrorEvent(INITIALIZE_TRANSACTION_ACTION, e);
    }
  }

  @ReactMethod
  public void voidTransaction(String transactionId) {
    MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
      .setMPOSTransactionID(transactionId)
      .build();
    Activity _activityContext = getCurrentActivity();
    try {

      SSMPOSSDK.getInstance().getTransaction().voidTransaction(_activityContext, transactionalParams, new MPOSTransaction.TransactionEvents() {
        @Override
        public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
          System.out.println("voidTransaction -> onTransactionResult result : " + result);
          WritableMap writableMap = Arguments.createMap();
          if (transactionOutcome != null) {
            writableMap.putString("statusCode", transactionOutcome.getStatusCode());
            writableMap.putString("statusMessage", transactionOutcome.getStatusMessage());
          }
          sendTransactionResultEvent(VOID_TRANSACTION_ACTION, result, writableMap);
        }

        @Override
        public void onTransactionUIEvent(int event) {
          System.out.println("voidTransaction -> onTransactionUIEvent result : " + event);
          sendTransactionUiEvent(VOID_TRANSACTION_ACTION, event);
        }
      });
    } catch (Exception e) {
      Logger logger = Logger.getAnonymousLogger();
      logger.log(Level.SEVERE, "voidTransaction -> Catch Error Transaction", e);
      sendErrorEvent(VOID_TRANSACTION_ACTION, e);
    }
  }

  @ReactMethod
  public void refundTransaction(String transactionId) {
    MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
      .setMPOSTransactionID(transactionId)
      .build();
    Activity _activityContext = getCurrentActivity();
    try {
      SSMPOSSDK.getInstance().getTransaction().refundTransaction(_activityContext, transactionalParams, new MPOSTransaction.TransactionEvents() {
        @Override
        public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
          System.out.println("refundTransaction -> onTransactionResult result : " + result);
          WritableMap writableMap = Arguments.createMap();
          if (transactionOutcome != null) {
            writableMap.putString("statusCode", transactionOutcome.getStatusCode());
            writableMap.putString("statusMessage", transactionOutcome.getStatusMessage());
          }
          sendTransactionResultEvent(REFUND_TRANSACTION_ACTION, result, writableMap);
        }

        @Override
        public void onTransactionUIEvent(int event) {
          System.out.println("refundTransaction -> onTransactionUIEvent result : " + event);
          sendTransactionUiEvent(REFUND_TRANSACTION_ACTION, event);
        }
      });
    } catch (Exception e) {
      Logger logger = Logger.getAnonymousLogger();
      logger.log(Level.SEVERE, "refundTransaction -> Catch Error Transaction", e);
      sendErrorEvent(REFUND_TRANSACTION_ACTION, e);
    }
  }
}
