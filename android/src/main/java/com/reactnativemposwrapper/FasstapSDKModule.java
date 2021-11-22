package com.reactnativemposwrapper;


import static my.com.softspace.reader.internal.kernelconfig.HexUtil.byteArrayToHexString;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import java.util.Map;

import my.com.softspace.ssfasstapsdk.FasstapSDKConfiguration;
import my.com.softspace.ssfasstapsdk.FasstapSDKInfo;
import my.com.softspace.ssfasstapsdk.SSFasstapSDK;
import my.com.softspace.ssfasstapsdk.pog.AttestationPOG;
import my.com.softspace.ssfasstapsdk.transaction.KernelConfigurationParams;
import my.com.softspace.ssfasstapsdk.transaction.Transaction;
import my.com.softspace.ssfasstapsdk.transaction.TransactionalParams;

public class FasstapSDKModule {

  private final static String TAG = "FasstapSDKModule";
  private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  public void initFasstapSDK(ReadableMap initConfig, Context context, Promise promise){


      System.out.println("Inside initFasstapSDK--------");
      System.out.println(initConfig.getString("attestationHost"));
      System.out.println(initConfig.getString("attestationHostCertPinning"));
    // should it be wrapped within try catch?
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

    promise.resolve("Success");

    /*SSFasstapSDK.init(context, config, new SSFasstapSDK.EncryptionModel() {
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
    Log.i("COTS ID: " , fasstapSDKInfo.getCotsId());*/

    // Please perform login your user before attestation
    //TODO: Check what is this for
    //SSFasstapSDK.getInstance().getAttestationPog().login(this, "user1");

   /* if (!SSFasstapSDK.hasRequiredPermission(context)) {
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
          promise.reject("FAILED", "something is wrong");
        }

        if (isSuccess)
        {
          Log.i("Attestation SUCCESS", "" +status + "");
          promise.resolve(status);
        }
      });
    }*/
  }

  public void initializeTransaction(Context context, Callback callback){
    // add kernel configurations params if required
    try {
      TransactionalParams transactionalParams = TransactionalParams.Builder.create().setAmount("2000").setDebitOptIn(true).setWaitForUserInputTimeout(15000).build();
      SSFasstapSDK.getInstance().getTransaction().startTransaction((Activity) context, transactionalParams, new Transaction.TransactionEvents()
      {
        @Override
        public void onCardEvent(int i)
        {
          Log.i("onCardEvent: " , "Code :"+ i);
          // instead of i, we can pass the enum
          callback.invoke(null, i);
        }

        @Override
        public void onTransactionResult(int i, byte[] bytes)
        {
          // refer the documentation for the value <-> description
          Log.i("onTransactionResult: ", "Code :"+i);
          callback.invoke(null,i);
        }

        @Override
        public void onTransactionUIEvent(int i)
        {
          Log.i("onTransactionUIEvent: " , "Code :"+i);
          callback.invoke(null, i);
        }


        public byte[] onTransactionRequestOnlineAuthentication(byte[] bytes, Map<String, byte[]> map) {
         // Log.i("onTransactionRequestOnlineAuthentication: " ,"");
          Log.i("ksn: " , byteArrayToHexString(bytes, 0, bytes.length, false));

          if (map != null)
          {
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
              System.out.println(entry.getKey() + " : " + byteArrayToHexString(entry.getValue(), 0 , entry.getValue().length, false));
            }
          }

          // simulate approved response from host.
          return new byte[]{(byte)0x00, (byte)0x00, (byte)0x8A, (byte)0x00, (byte)0x02, (byte)0x30, (byte)0x30};
        }

      });
    }catch (Exception e){
      Log.e(TAG, e.getMessage(), e);
      callback.invoke(e, null);
    }

  }

  public static String byteArrayToHexString(byte[] scr, int off, int len, boolean noSpace) {
    StringBuffer buf = new StringBuffer();

    for (int i = 0, j = 0; i < len; i++)
    {
      buf.append(HEX[((scr[off + i] >> 4) & 0xf)]);
      buf.append(HEX[((scr[off + i]) & 0xf)]);
      if (!noSpace)
        buf.append(' ');
    }

    return buf.toString();
  }
}
