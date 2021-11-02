package com.reactnativemposwrapper;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import my.com.softspace.ssfasstapsdk.FasstapSDKConfiguration;
import my.com.softspace.ssfasstapsdk.FasstapSDKInfo;
import my.com.softspace.ssfasstapsdk.SSFasstapSDK;
import my.com.softspace.ssfasstapsdk.pog.AttestationPOG;
import my.com.softspace.ssfasstapsdk.transaction.KernelConfigurationParams;
import my.com.softspace.ssfasstapsdk.transaction.Transaction;
import my.com.softspace.ssfasstapsdk.transaction.TransactionalParams;

public class FasstapSDKModule {

  public void initFasstapSDK(ReadableMap initConfig, Context context, Promise promise){

    FasstapSDKConfiguration config = FasstapSDKConfiguration.Builder.create()
      .setAttestationHost(initConfig.getString("attestationHost"))
      .setAttestationHostCertPinning(initConfig.getString("attestationHostCertPinning"))
      .setAttestationHostReadTimeout(10000L)
      .setAttestationRefreshInterval(300000L)
      .setAttestationStrictHttp(true)
      .setAttestationConnectionTimeout(30000L)
      .setLibGoogleApiKey(initConfig.getString("googleApiKey"))
      .setLibAccessKey(initConfig.getString("accessKey"))
      .setLibSecretKey(initConfig.getString("secretKey"))
      .build();

    SSFasstapSDK.init(context, config, new SSFasstapSDK.EncryptionModel() {
      @Override
      public Object[] cipherOperation(boolean isEncrypt, byte[] data, boolean increaseKSN) {
        if (isEncrypt)
        {
          return SSFasstapSDK.getInstance().getAttestationPog().cryptoOperation(context, isEncrypt, data, increaseKSN);
        }
        // to simulate host response
        return new Object[] {0, data};
      }
    });
    FasstapSDKInfo fasstapSDKInfo = SSFasstapSDK.getInstance().getFasstapSDKInfo(context);
    Log.i("SDK Version: " , fasstapSDKInfo.getSdkVersion());
    Log.i("COTS ID: " , fasstapSDKInfo.getCotsId());

    // Please perform login your user before attestation
    //TODO: Check what is this for
    //SSFasstapSDK.getInstance().getAttestationPog().login(this, "user1");

    if (!SSFasstapSDK.hasRequiredPermission(context)) {
      SSFasstapSDK.requestPermissionIfRequired((Activity) context, 1000);
    }
    else {
      // add attestation logic here
      SSFasstapSDK.getInstance().getAttestationPog().attest(context, (status, recommendedActions) -> {
        boolean isSuccess = false;

        if ((recommendedActions == null || recommendedActions.size() == 0) && status == AttestationPOG.POGStatusCodes.RST_OK)
        {
          isSuccess = true;
        }
        else
        {
          Log.i("Attestation FAILED", "" +status + "");
          // Promise.reject()
        }

        if (isSuccess)
        {
          Log.i("Attestation SUCCESS", "" +status + "");
          // Promise.resolve()
        }
      });
    }
  }
}
