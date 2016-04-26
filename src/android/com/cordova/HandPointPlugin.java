package com.cordova.handpointplugin;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;


import org.apache.cordova.*; // Cordova 3.x

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import android.content.Context;


import android.util.Log;

import com.handpoint.api.*;

//adb logcat HandPoint:D *:S


public class HandPointPlugin extends CordovaPlugin implements Events.Required, Events.Status {

 Hapi api;
 Device device;
 @SuppressWarnings("unused")
 private CallbackContext callbackContext;

 private CallbackContext List_callbackContext;
 private CallbackContext Status_callbackContext;
 private CallbackContext Connection_callbackContext;

 // Debugging
 private static final String TAG = "HandPoint";
 private static final boolean D = true;

 private String[] ConnectioMethod = {
  "USB",
  "SERIAL",
  "BLUETOOTH",
  "HTTPS",
  "WIFI",
  "ETHERNET",
  "SIMULATOR"
 };


 //Receiving a list of connectable devices
 List < Device > myListOfDevices;

 private String sharedSecret_key;
 private String deviceName;
 private Context appContext;

 private boolean connectCalled;
 private boolean isConnected;

 public HandPointPlugin() {
  super();
  sharedSecret_key = "0102030405060708091011121314151617181920212223242526272829303132"; //this is test key.
  deviceName = "test";
 }

 public HandPointPlugin(Context context) {
  //initApi(context);
 }

 @Override
 public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
  Log.d(TAG, "action = " + action);




  appContext = this.cordova.getActivity().getApplicationContext();

  this.callbackContext = callbackContext;

  boolean retValue = true;
  if (action.equals("init")) {
   //Initialize  API
   initApi(appContext, callbackContext);
   retValue = true;

  } else if (action.equals("pay")) {
   //Pay
   pay(args, callbackContext);
   retValue = true;

  } else if (action.equals("connect")) {
   //Connect Device
   connect(args, callbackContext);
   retValue = true;

  } else if (action.equals("ListDevices")) {
   //List devices Device
   ListDevices(callbackContext);
   retValue = true;

  } else if (action.equals("SetMerchantKey")) {
   //Connect Device
   SetMerchantKey(args, callbackContext);
   retValue = true;

  } else if (action.equals("SetDeviceName")) {
   //Connect Device
   SetDeviceName(args, callbackContext);
   retValue = true;

  } else if (action.equals("disconnect")) {
   //disonnect Device
   disconnect(callbackContext);
   retValue = true;

  } else if (action.equals("TransactionStatusTrigger")) {
   //register a callback for recieveing Transaction status messages
   TransactionStatusTrigger(callbackContext);
   retValue = true;

  } else if (action.equals("searchDevices")) {
   //search for wifi devices.
   searchDevices(callbackContext);
   retValue = true;

  } else {
   retValue = false;
  }

  return retValue;
 }

 public void initApi(Context context, CallbackContext callbackContext) {

  this.api = HapiFactory.getAsyncInterface(this, context).defaultSharedSecret(sharedSecret_key);

  //Register a listener for required events
  this.api.addRequiredEventHandler(this);
  this.api.addStatusNotificationEventHandler(this);

  //The api is now initialized. Yay! we've even set a default shared secret!
  //But we need to connect to a device to start taking payments.
  callbackContext.success();
 }

 //You should populate this method as is you see fit.
 //Here we assume the name of a device and use it.
 //Connection to the device is handled automatically in the API
 @Override
 public void deviceDiscoveryFinished(List < Device > devices) {

  Log.d(TAG, "Discovery Finished");
  myListOfDevices = devices;
  for (Device device: devices) {
   if (device.getName() != null && device.getName().equals(deviceName)) {
    //We'll remember the device for this session, but is cool that you do too
    this.device = device;
    this.api.useDevice(this.device);
   }
  }


  JSONArray deviceList = new JSONArray();

  for (Device device: myListOfDevices) {
   JSONObject json = new JSONObject();
   try {
    json.put("name", device.getName());
   } catch (JSONException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   try {
    json.put("address", device.getAddress());
   } catch (JSONException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   try {
    json.put("port", device.getPort());
   } catch (JSONException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }

   deviceList.put(json);
  }

  List_callbackContext.success(deviceList);
 }

 public void connect(JSONArray args, CallbackContext callbackContext) throws JSONException {

    if(isConnected) {
        callbackContext.error("Device already connected");
        return;
    }

  JSONObject obj = args.optJSONObject(0);
  String name = obj.optString("name");
  String address = obj.optString("address");
  String port = obj.optString("port");
  String connectionMethod = obj.optString("method");
  ConnectionMethod method;

  method = ConnectionMethod.SIMULATOR;


  if (connectionMethod.equals("USB")) {
   method = ConnectionMethod.USB;
  }
  if (connectionMethod.equals("SERIAL")) {
   method = ConnectionMethod.SERIAL;
  }
  if (connectionMethod.equals("BLUETOOTH")) {
   method = ConnectionMethod.BLUETOOTH;
  }
  if (connectionMethod.equals("HTTPS")) {
   method = ConnectionMethod.HTTPS;
  }
  if (connectionMethod.equals("WIFI")) {
   method = ConnectionMethod.WIFI;
  }
  if (connectionMethod.equals("ETHERNET")) {
   method = ConnectionMethod.ETHERNET;
  }
  if (connectionMethod.equals("SIMULATOR")) {
   method = ConnectionMethod.SIMULATOR;
  }

  Device device = new Device(name, address, port, method);

  Hapi bFlag = api.useDevice(device);
  //Log.d(TAG, bFlag);

  connectCalled = true;
  Connection_callbackContext = callbackContext;  
   //callbackContext.success("success");
  
 }




 public boolean disconnect(CallbackContext callbackContext) throws JSONException {
  //Disconnect from current device

    //already disconnected 
    if(!isConnected){
        callbackContext.error("Device already disconnected.");
        return false;
    }

  boolean bReturn = api.disconnect();


   if (bReturn == true) {
    
       callbackContext.success("Device disconnected.");
      } else {
        
       callbackContext.error("Device not disconnected.");
      }

      return bReturn;
 }

 public void searchDevices(CallbackContext callbackContext) throws JSONException {

  Log.d(TAG, "searching...");
  api.listDevices(ConnectionMethod.BLUETOOTH);
  List_callbackContext = callbackContext;

  //callbackContext.success();
 }

 public boolean pay(JSONArray args, CallbackContext callbackContext) throws JSONException {
  String price;
  String currency;
  String budget;
  String optionalParameters;

  JSONObject obj = args.optJSONObject(0);

  price = obj.optString("price");
  currency = obj.optString("currency");
  
  //we need to check are optional parameters provided, then construct the function call
  //budget = obj.optString("budget");
  //optionalParameters = obj.optString("optionalParameters");

  //Map optionalParameters = new HashMap();
  //optionalParameters.put("Budget", budget);
  //optionalParameters.put("CustomerReference",customerReference);


  Currency _currency;
 
  if (currency.equals("GBP")) {
   _currency = Currency.GBP;
  } else if (currency.equals("ZAR")) {
   _currency = Currency.ZAR;
  } else if (currency.equals("USD")) {
   _currency = Currency.USD;
  } else if (currency.equals("EUR")) {
   _currency = Currency.EUR;
  } else if(currency.equals("CNY")) {
    _currency = Currency.CNY;
  } else if(currency.equals("EGP")) {
    _currency = Currency.EGP;
  } else if(currency.equals("INR")) {
    _currency = Currency.INR;
  } else if(currency.equals("UAH")) {
    _currency = Currency.UAH;
  } else if(currency.equals("TWD")) {
    _currency = Currency.TWD;
  } else if(currency.equals("AUD")) {
    _currency = Currency.AUD;
  } else if(currency.equals("CAD")) {
    _currency = Currency.CAD;
  } else if(currency.equals("SGD")) {
    _currency = Currency.SGD;
  } else if(currency.equals("CHF")) {
    _currency = Currency.CHF;
  } else if(currency.equals("MYR")) {
    _currency = Currency.MYR;
  } else if(currency.equals("JPY")) {
    _currency = Currency.JPY;
  } else {
     callbackContext.error("Currency not supported.");
     return false;
  }
 

  //we should check are parameters provided then call provide it as a parameter
  //boolean bReturn = api.sale(new BigInteger(price), _currency, optionalParameters);

  boolean bReturn = api.sale(new BigInteger(price), _currency);

  if (bReturn == true) {
   callbackContext.success("Payment succesfully initialized.");
  } else {
   callbackContext.error("Payment initialization failed.");
  }

  return bReturn;
 }


 public void SetMerchantKey(JSONArray args, CallbackContext callbackContext) throws JSONException {
  String key;
  JSONObject obj = args.optJSONObject(0);

  key = obj.optString("key");
  sharedSecret_key = key;
 }

 public void SetDeviceName(JSONArray args, CallbackContext callbackContext) throws JSONException {
  String name;
  JSONObject obj = args.optJSONObject(0);

  name = obj.optString("name");
  deviceName = name;
 }


 public void ListDevices(CallbackContext callbackContext) {

  List_callbackContext = callbackContext;

  //callbackContext.success(message);
 }
 public void TransactionStatusTrigger(CallbackContext callbackContext) {

  Status_callbackContext = callbackContext;

  //callbackContext.success(message);
 }

 @Override
 public void signatureRequired(SignatureRequest signatureRequest, Device device) {
  Log.d(TAG, "SIG REQ: " + signatureRequest);
    this.api.signatureResult(true);
  //You'll be notified here if a sale process needs signature verification
  //See documentation
 }

 @Override
 public void endOfTransaction(TransactionResult transactionResult, Device device) {
  Log.d(TAG, "TRAN END: " + transactionResult.getStatusMessage());

    JSONObject json = new JSONObject();

  if (transactionResult.getFinStatus() == FinancialStatus.AUTHORISED) {
    //...
   Log.d(TAG, "TRAN AUTHORISED");
   Log.d(TAG, transactionResult.getCustomerReceipt());

    try {
        json.put("status", "AUTHORISED");
        json.put("message",transactionResult.getStatusMessage());
        json.put("receipt",transactionResult.getCustomerReceipt());
       } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
       }
      

    PluginResult result = new PluginResult(PluginResult.Status.OK, json);
    result.setKeepCallback(true);
    Status_callbackContext.sendPluginResult(result);
   
  } else if (transactionResult.getFinStatus() == FinancialStatus.DECLINED) {
   //...
    Log.d(TAG, transactionResult.getCustomerReceipt());
        try {
        json.put("status", "DECLINED");
        json.put("message",transactionResult.getStatusMessage());
        json.put("receipt",transactionResult.getCustomerReceipt());
       } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
       }
   

    PluginResult result = new PluginResult(PluginResult.Status.OK, json);
    result.setKeepCallback(true);
    Status_callbackContext.sendPluginResult(result);
    Log.d(TAG, transactionResult.getCustomerReceipt());
   Log.d(TAG, "TRAN DECLINED");
  } else if (transactionResult.getFinStatus() == FinancialStatus.PROCESSED) {
   //...
   Log.d(TAG, "TRAN PROCESSED");
       try {
        json.put("status", "PROCESSED");
        json.put("message",transactionResult.getStatusMessage());
         json.put("receipt",transactionResult.getCustomerReceipt());
       } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
       }
    
     

    PluginResult result = new PluginResult(PluginResult.Status.OK, json);
    result.setKeepCallback(true);
    Status_callbackContext.sendPluginResult(result);
    Log.d(TAG, transactionResult.getCustomerReceipt());
  } else if (transactionResult.getFinStatus() == FinancialStatus.FAILED) {
   //...
   Log.d(TAG, "TRAN FAILED");
   Log.d(TAG, transactionResult.getCustomerReceipt());
       try {
        json.put("status", "FAILED");
         json.put("message",transactionResult.getStatusMessage());
          json.put("receipt",transactionResult.getCustomerReceipt());
       } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
       }
    


    PluginResult result = new PluginResult(PluginResult.Status.OK, json);
    result.setKeepCallback(true);
    Status_callbackContext.sendPluginResult(result);
  } else if (transactionResult.getFinStatus() == FinancialStatus.CANCELLED) {
   //...
        try {
        json.put("status", "CANCELLED");
         json.put("message",transactionResult.getStatusMessage());
          json.put("receipt",transactionResult.getCustomerReceipt());
       } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
       }
    


    PluginResult result = new PluginResult(PluginResult.Status.OK, json);
    result.setKeepCallback(true);
    Status_callbackContext.sendPluginResult(result);
    Log.d(TAG, "TRAN CANCELLED");
    Log.d(TAG, transactionResult.getCustomerReceipt());
  }
 }

 @Override
 public void currentTransactionStatus(StatusInfo statusInfo, Device device) {
  /*
UserCancelled WaitingForCard CardInserted ApplicationSelection ApplicationConfirmation AmountValidation PinInput ManualCardInput WaitingForCardRemoval TipInput AuthenticatingPos WaitingForSignature ConnectingToHost SendingToHost ReceivingFromHost DisconnectingFromHost PinInputComplete Undefined


           */
if (statusInfo.getStatus() == StatusInfo.Status.WaitingForCard) {
    Log.d(TAG,"WaitingForCard");
}

  Log.d(TAG, "TRANS STATUS: " + statusInfo.getMessage());




  PluginResult result = new PluginResult(PluginResult.Status.OK, statusInfo.getMessage());
  result.setKeepCallback(true);
  Status_callbackContext.sendPluginResult(result);

 }

 @Override
 public void connectionStatusChanged(ConnectionStatus connectionStatus, Device device) {
    Log.d(TAG, "CONN STATUS: " + connectionStatus.name());

    if(connectionStatus.name() == "Connected") {

        //store conenction state
        isConnected = true;
        
        //if we're attempting to connect, let's call appopriate callback
        if(connectCalled){
            connectCalled = false;
            Connection_callbackContext.success("Device connected");
        }
        
        
    } else if (connectionStatus.name() == "Disconnected") {
         
         //store conenction state
         isConnected = false;

         //if we're attempting to connect, let's call appopriate callback
         if(connectCalled){
            connectCalled = false;
            Connection_callbackContext.error("Device not connected");
         }
        
    }
    
 }

}