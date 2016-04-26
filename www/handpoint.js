 /*global cordova*/
module.exports = {

    connect: function (name, address, port, method, success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "connect", [{"name": name, "address": address, "port": port, "method": method}]);
    },
    disconnect: function (success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "disconnect", []);
    },
    connectWithCurrentDevice: function (success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "connectWithCurrentDevice", []);
    },
	pay: function (price, currency, budget, customerReference, success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "pay", [{"price": price, "currency": currency, "budget": budget, "customerReference": customerReference}]);
    },
    SetMerchantKey: function (key, success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "SetMerchantKey", [{"key":key}]);
    },
    SetDeviceName: function (name, success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "SetDeviceName", [{"name":name}]);
    },
    ListDevices: function (success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "ListDevices", []);
    },
    init: function (success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "init", []);
    },
    TransactionStatusTrigger: function (success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "TransactionStatusTrigger", []);
    },
    searchDevices: function (success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "searchDevices", []);
    },
    connectionStatusStream: function (success, failure) {
        cordova.exec(success, failure, "HandPointPlugin", "connectionStatusStream", []);
    }
 
    
	
  };

