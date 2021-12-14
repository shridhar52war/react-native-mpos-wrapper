package com.reactnativemposwrapper;

import static my.com.softspace.ssmpossdk.transaction.MPOSTransaction.TransactionEvents.TransactionResult.TransactionSuccessful;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.telecom.Call;

import java.util.logging.Logger;
import java.util.logging.Level;


import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.UiThreadUtil;

import my.com.softspace.ssmpossdk.Environment;
import my.com.softspace.ssmpossdk.SSMPOSSDK;
import my.com.softspace.ssmpossdk.SSMPOSSDKConfiguration;
import my.com.softspace.ssmpossdk.transaction.MPOSTransaction;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionOutcome;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionParams;

@ReactModule(name = MposWrapperModule.NAME)
public class MposWrapperModule extends ReactContextBaseJavaModule {
  public static final String NAME = "MposWrapper";
  private Application application;

  private FasstapSDKModule fasstapSDKModule;
  private ReactApplicationContext reactContext;
  private Context _context;
  private Activity _activity;

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
//    System.out.println("attestationHost : " + initConfig.getString("attestationHost"));
//    System.out.println("attestationHostCertPinning : " + initConfig.getString("attestationHostCertPinning"));
//    System.out.println("googleApiKey : " + initConfig.getString("googleApiKey"));
//    System.out.println("accessKey : " + initConfig.getString("accessKey"));
//    System.out.println("secretKey : " + initConfig.getString("secretKey"));
//    System.out.println("uniqueId : " + initConfig.getString("uniqueId"));
//    System.out.println("developerId : " + initConfig.getString("developerId"));
//    System.out.println("Environment : " + Environment.UAT);


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

  private void refreshToken() {
    Activity _currentActivity = getCurrentActivity();
    System.out.println("Inside refreshToken method......" + _currentActivity);
    SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().uniqueID = "rzp01";
    SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().developerID = "9nD9hrW8EMWB375";
    SSMPOSSDK.getInstance().getTransaction().refreshToken(_currentActivity, new MPOSTransaction.TransactionEvents() {
      @Override
      public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
        System.out.println("onTransactionResult in refreshToken:: " + result);

        if (result == TransactionSuccessful) {
          System.out.println("refreshToken TransactionSuccessful" + result);
        } else {
          if (transactionOutcome != null) {
            System.out.println(transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());
          }
        }
      }

      @Override
      public void onTransactionUIEvent(int event) {
        System.out.println("onTransactionUIEvent refreshToken :: " + event);
      }
    });
  }


  @ReactMethod
  public void initializeTransaction(Callback callback) {
    // Accept config as param to set amount and other transactional related data.
    //fasstapSDKModule.initializeTransaction(this.reactContext, callback);
    Activity _activityContext = getCurrentActivity();
    try {
      MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
        .setAmount("100")
        .build();
      System.out.println("Initialising transaction........");
      refreshToken();
      uploadSignature();
      SSMPOSSDK.getInstance().getTransaction().startTransaction(_activityContext, transactionalParams, new MPOSTransaction.TransactionEvents() {
        @Override
        public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
          System.out.println(" onTransactionResult result : " + result);
          if (result == TransactionSuccessful) {
            String outcome = "Transaction ID :: " + transactionOutcome.getTransactionID() + "\n";
            outcome += "Approval code :: " + transactionOutcome.getApprovalCode() + "\n";
            outcome += "Card number :: " + transactionOutcome.getCardNo() + "\n";
            outcome += "Cardholder name :: " + transactionOutcome.getCardHolderName();
            System.out.println(outcome);

            callback.invoke("transactionOutcome.getTransactionID()" + transactionOutcome.getTransactionID());
          }
        }

        @Override
        public void onTransactionUIEvent(int event) {
          System.out.println("onTransactionUIEvent" + event);
          callback.invoke("onTransactionUIEvent" + event);
        }
      });
    } catch (Exception e) {
      Logger logger = Logger.getAnonymousLogger();
      logger.log(Level.SEVERE, "Catch Error Transaction", e);
      callback.invoke("Error in transaction");
    }
  }
}
