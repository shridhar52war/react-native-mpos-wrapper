package com.reactnativemposwrapper;

import android.telecom.Call;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = MposWrapperModule.NAME)
public class MposWrapperModule extends ReactContextBaseJavaModule {
    public static final String NAME = "MposWrapper";
    private ReactContext mReactContext;
    private FasstapSDKModule fasstapSDKModule;

    public MposWrapperModule(ReactApplicationContext reactContext) {
      super(reactContext);
      mReactContext = reactContext;
      fasstapSDKModule = new FasstapSDKModule();
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
    public void init(ReadableMap config, Promise promise){
      fasstapSDKModule.initFasstapSDK(config, mReactContext, promise);
    }

    @ReactMethod
    public void initializeTransaction(Callback callback){
        // Accept config as param to set amount and other transactional related data.
      fasstapSDKModule.initializeTransaction(mReactContext, callback);
    }

    public static native int nativeMultiply(int a, int b);
}
