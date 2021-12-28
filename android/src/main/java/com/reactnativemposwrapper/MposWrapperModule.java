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
import android.content.Context;
import android.os.Looper;
import android.telecom.Call;

import java.util.logging.Logger;
import java.util.logging.Level;


import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
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
  public static final String TRANSACTION_UI_EVENT_NAME = "TransactionUIEvent";
  public  static final String TRANSACTION_RESULT_EVENT_NAME = "TransactionResult";
  public static final String REFRESH_TOKEN_EVENT_NAME = "RefreshToken";
  public static final String ERROR = "Error";
  private Application application;

  private FasstapSDKModule fasstapSDKModule;
  private ReactApplicationContext reactContext;
  private Context _context;
  private Activity _activity;
  private Callback jsCallback;
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

  public void sendEvent(String eventName, Integer eventCode) {
    WritableMap params = Arguments.createMap();
    params.putInt("eventCode", eventCode);
    if (eventEmitter == null) {
      eventEmitter = getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    }
    if (eventEmitter != null) {
      System.out.println("Emitting event" + "Event Code :" + eventName + "Event Code :" + eventCode);
      eventEmitter.emit(eventName, params);
    }
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
  public void refreshToken(Callback callback) {
    Activity _currentActivity = getCurrentActivity();
    jsCallback = callback;
    System.out.println("Inside refreshToken method......");
    //SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().uniqueID = "rzp01";
    //SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().developerID = "9nD9hrW8EMWB375";
    // System.out.println("XXXXXXXXXXXXXXX" + SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().uniqueID + SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().developerID);
    SSMPOSSDK.getInstance().getTransaction().refreshToken(_currentActivity, new MPOSTransaction.TransactionEvents() {
      @Override
      public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
        System.out.println("onTransactionResult in refreshToken:: " + result);

        if (result == TransactionResult.TransactionSuccessful) {
          System.out.println("refreshToken TransactionSuccessful" + result);
          sendEvent(REFRESH_TOKEN_EVENT_NAME, result);
        } else {
          if (transactionOutcome != null) {
            System.out.println("refreshToken :" + transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());
          }
        }
      }

      @Override
      public void onTransactionUIEvent(int event) {
        System.out.println("onTransactionUIEvent refreshToken :: " + event);
        sendEvent(REFRESH_TOKEN_EVENT_NAME, event);
      }
    });
  }


  @ReactMethod
  public void initializeTransaction(Callback callback) {
    // Accept config as param to set amount and other transactional related data.
    //fasstapSDKModule.initializeTransaction(this.reactContext, callback);
    jsCallback = callback;
    Activity _activityContext = getCurrentActivity();
    try {
      MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
        .setAmount("100")
        .build();
      System.out.println("Initialising transaction........");
      // uploadSignature();
      SSMPOSSDK.getInstance().getTransaction().startTransaction(_activityContext, transactionalParams, new MPOSTransaction.TransactionEvents() {
        @Override
        public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
          System.out.println(" onTransactionResult result : " + result);
          if (result == TransactionResult.TransactionSuccessful) {
            if (transactionOutcome != null) {
              String outcome = "Transaction ID :: " + transactionOutcome.getTransactionID() + "\n";
              outcome += "Approval code :: " + transactionOutcome.getApprovalCode() + "\n";
              outcome += "Card number :: " + transactionOutcome.getCardNo() + "\n";
              outcome += "Cardholder name :: " + transactionOutcome.getCardHolderName();
              System.out.println(outcome);
            }
            //jsCallback.invoke("transactionOutcome.getTransactionID()");
            sendEvent(TRANSACTION_RESULT_EVENT_NAME, result);
          }
        }

        @Override
        public void onTransactionUIEvent(int event) {
          System.out.println("onTransactionUIEvent" + event);
          // jsCallback.invoke("onTransactionUIEvent" + event);
          sendEvent(TRANSACTION_UI_EVENT_NAME, event);
        }
      });
    } catch (Exception e) {
      Logger logger = Logger.getAnonymousLogger();
      logger.log(Level.SEVERE, "Catch Error Transaction", e);
      //jsCallback.invoke("Error in transaction");
      sendEvent(ERROR, 000);
    }
  }
}
