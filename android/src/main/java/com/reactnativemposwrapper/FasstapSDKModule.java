package com.reactnativemposwrapper;


import static my.com.softspace.reader.internal.kernelconfig.HexUtil.byteArrayToHexString;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import java.util.Map;

import my.com.softspace.ssmpossdk.Environment;
import my.com.softspace.ssmpossdk.SSMPOSSDK;
import my.com.softspace.ssmpossdk.SSMPOSSDKConfiguration;
import my.com.softspace.ssmpossdk.transaction.MPOSTransaction;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionOutcome;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionParams;
import static my.com.softspace.ssmpossdk.transaction.MPOSTransaction.TransactionEvents.TransactionResult.TransactionSuccessful;

public class FasstapSDKModule {

  private final static String TAG = "FasstapSDKModule";


  public void initFasstapSDK(ReadableMap initConfig, Context context, Promise promise){
      System.out.println("Inside initFasstapSDK--------");
      System.out.println(initConfig.getString("attestationHost"));
      System.out.println(initConfig.getString("attestationHostCertPinning"));
    // should it be wrapped within try catch?
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
        .setEnvironment(Environment.UAT)
        .build();
      SSMPOSSDK.init(context, config);
      System.out.println("SDK Version: " + SSMPOSSDK.getInstance().getSdkVersion());
      System.out.println("COTS ID: " + SSMPOSSDK.getInstance().getCotsId());

//      if(!SSMPOSSDK.hasRequiredPermission(context)){
//          SSMPOSSDK.requestPermissionIfRequired((Activity) context, 1000);
//      }
      //promise.resolve("Successfully Initiated");
    }catch (Exception e){
      //System.out.println(e);
      promise.reject(e);
    }
  }

  public void initializeTransaction(Context context, Callback callback){
    //callback.invoke("Success");
    try{
      MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
        .setAmount("100")
        .build();

      SSMPOSSDK.getInstance().getTransaction().startTransaction((Activity) context, transactionalParams, new MPOSTransaction.TransactionEvents() {
        @Override
        public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
          if(result == TransactionSuccessful)
          {
            String outcome = "Transaction ID :: " + transactionOutcome.getTransactionID() + "\n";
            outcome += "Approval code :: " + transactionOutcome.getApprovalCode() + "\n";
            outcome += "Card number :: " + transactionOutcome.getCardNo() + "\n";
            outcome += "Cardholder name :: " + transactionOutcome.getCardHolderName();
            System.out.println(outcome);

           callback.invoke("transactionOutcome.getTransactionID()"+ transactionOutcome.getTransactionID());
          }

        }

        @Override
        public void onTransactionUIEvent(int event) {
          System.out.println("onTransactionUIEvent"+event);
            callback.invoke("onTransactionUIEvent"+event);
        }
      });
    }catch (Exception e){
      System.out.println(e.getMessage());
    }
  }
}
