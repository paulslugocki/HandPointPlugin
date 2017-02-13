#import <Cordova/CDVPlugin.h>
#import "HeftClient.h"
#import "HeftManager.h"
#import "HeftStatusReportPublic.h"

@interface HandPointPlugin : CDVPlugin

  - (void)connect: (CDVInvokedUrlCommand*)command;
  - (void)connectWithCurrentDevice:(CDVInvokedUrlCommand*)command;
  - (void)pay: (CDVInvokedUrlCommand*)command;
  - (void)SetMerchantKey:(CDVInvokedUrlCommand*)command;
  - (void)SetDeviceName: (CDVInvokedUrlCommand*)command;
  - (void)ListDevices:(CDVInvokedUrlCommand*)command;
  - (void)init: (CDVInvokedUrlCommand*)command;

  @property(nonatomic, strong) id<HeftClient> heftClient;
  @property(nonatomic, strong) HeftManager* manager;
  @property(nonatomic, strong) NSString* transactionStatus;
  @property(nonatomic, strong) NSString* cardReaderSerial;

@end
