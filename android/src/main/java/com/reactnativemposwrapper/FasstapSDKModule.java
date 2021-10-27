package com.reactnativemposwrapper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;

//import my.com.softspace.ssfasstapsdk.FasstapSDKConfiguration;
//import my.com.softspace.ssfasstapsdk.FasstapSDKInfo;
//import my.com.softspace.ssfasstapsdk.SSFasstapSDK;
//import my.com.softspace.ssfasstapsdk.pog.AttestationPOG;
//import my.com.softspace.ssfasstapsdk.transaction.KernelConfigurationParams;
//import my.com.softspace.ssfasstapsdk.transaction.Transaction;
//import my.com.softspace.ssfasstapsdk.transaction.TransactionalParams;

public class FasstapSDKModule extends AppCompatActivity {

  public void initFasstapSDK(ReadableMap initConfig){
    Context context = getApplicationContext();
    Log.i("Init", initConfig.toString());
  }
}
