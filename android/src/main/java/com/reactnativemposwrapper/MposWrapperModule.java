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

import my.com.softspace.ssmpossdk.Environment;
import my.com.softspace.ssmpossdk.SSMPOSSDK;
import my.com.softspace.ssmpossdk.SSMPOSSDKConfiguration;

@ReactModule(name = MposWrapperModule.NAME)
public class MposWrapperModule extends ReactContextBaseJavaModule {
    public static final String NAME = "MposWrapper";
    private  Application application;

    private FasstapSDKModule fasstapSDKModule;
    private ReactApplicationContext reactContext;
    private Context _context;
    private Activity _activity;

    public MposWrapperModule(ReactApplicationContext reactContext) {
      super(reactContext);
      if (SSMPOSSDK.isRunningOnRemoteProcess(reactContext))
      {
        System.out.println("isRunningOnRemoteProcess....");
        return;
      }
      this.reactContext= reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }


    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    @ReactMethod
    public void multiply(int a, int b, Promise promise) {
        promise.resolve(a * b);
    }


    @ReactMethod
    public void init(ReadableMap initConfig, Promise promise){
      this._activity = getCurrentActivity();
      System.out.println("Inside initFasstapSDK--------");
      System.out.println(initConfig.getString("attestationHost"));
      System.out.println(initConfig.getString("attestationHostCertPinning"));

      try{
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
          .setEnvironment(Environment.UAT)
          .build();

        ReactApplicationContext dd = this.reactContext;
        Activity aa = this._activity;

        SSMPOSSDK.init(this.reactContext, config);
        System.out.println("SDK Version: " + SSMPOSSDK.getInstance().getSdkVersion());
        System.out.println("COTS ID: " + SSMPOSSDK.getInstance().getCotsId());

        if(!SSMPOSSDK.hasRequiredPermission(dd)){
          SSMPOSSDK.requestPermissionIfRequired( this._activity, 1000);
        }
        promise.resolve("Successfully Initiated");
//        this._activity.runOnUiThread(new Runnable() {
//          @Override
//          public void run() {
//            SSMPOSSDK.init(dd, config);
//            System.out.println("SDK Version: " + SSMPOSSDK.getInstance().getSdkVersion());
//            System.out.println("COTS ID: " + SSMPOSSDK.getInstance().getCotsId());
//
//            if(!SSMPOSSDK.hasRequiredPermission(dd)){
//              SSMPOSSDK.requestPermissionIfRequired( aa, 1000);
//            }
//            promise.resolve("Successfully Initiated");
//          }
//        });
      }catch (Exception e){
        Logger logger = Logger.getAnonymousLogger();
        logger.log(Level.SEVERE, "Catch Error", e);
        promise.reject(e);
      }
    }

    @ReactMethod
    public void initializeTransaction(Callback callback){
        // Accept config as param to set amount and other transactional related data.
      fasstapSDKModule.initializeTransaction(this.reactContext, callback);
    }

    public static native int nativeMultiply(int a, int b);
}
