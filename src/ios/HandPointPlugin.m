#import <Cordova/CDVPlugin.h>
#import "HandPointPlugin.h"
#import "HeftClient.h"
#import "HeftManager.h"
#import "HeftStatusReportPublic.h"

@implementation HandPointPlugin

@synthesize heftClient, manager, transactionStatus;
//Set your unique shared secret here, provided by Handpoint with the dev kit
NSString* sharedSecret = @"0102030405060708091011121314151617181920212223242526272829303132";

- (void)connect: (CDVInvokedUrlCommand*)command {

}
- (void)connectWithCurrentDevice:(CDVInvokedUrlCommand*)command {

}
- (void)pay: (CDVInvokedUrlCommand*)command {

}
- (void)SetMerchantKey:(CDVInvokedUrlCommand*)command {

}
- (void)SetDeviceName: (CDVInvokedUrlCommand*)command {

}
- (void)ListDevices:(CDVInvokedUrlCommand*)command {

}
- (void)init: (CDVInvokedUrlCommand*)command {

}

#pragma mark Other functions

//Creating a heftclient
-(void)createClient:(HeftRemoteDevice *)newDevice{

    heftClient = nil;
    [manager clientForDevice:newDevice sharedSecret:[self SharedSecretDataFromString:sharedSecret] delegate:self];
}

-(void)setTransactionStatus:(NSString *)tStatus{
  transactionStatus = transactionStatus
}

#pragma mark HeftLibrary implementation

-(void)didConnect:(id<HeftClient>)client {
    NSLog(@"didConnect");
    heftClient = client;
    NSLog(@"Connected to %@", [[heftClient mpedInfo] objectForKey:kSerialNumberInfoKey]);
    [cardReaderSerial setText:[[heftClient mpedInfo] objectForKey:kSerialNumberInfoKey]];

    //clear status message
    [self setTransactionStatus:@""];
}

-(void)didFindAccessoryDevice:(HeftRemoteDevice *)newDevice{
    NSLog(@"didFindAccessoryDevice");
    [self createClient:newDevice];
}

-(void)responseStatus:(id<ResponseInfo>)info{
    NSLog(@"responseStatus:");
    NSLog(@"%@",info.status);
    NSLog(@"%@",info.xml.description);

    //[transactionStatus setText:[info status]];

    [self setTransactionStatus:info.status];
}

-(void)responseScannerEvent:(id<ScannerEventResponseInfo>)info{
    NSLog(@"responceScannerEvent");
}

-(void)responseEnableScanner:(id<EnableScannerResponseInfo>)info{
    NSLog(@"responceEnableScanner");
}

-(void)responseFinanceStatus:(id<FinanceResponseInfo>)info{
    NSLog(@"responseFinanceStatus:");
    NSLog(@"%@",info.status);
    NSLog(@"%@",info.customerReceipt);
    NSLog(@"%@",info.xml.description);

    //[transactionStatus setText:[info status]];

    [self setTransactionStatus:info.status];

  }

-(void)responseError:(id<ResponseInfo>)info{
    NSLog(@"responceError");
    //[transactionStatus setText:[info status]];
    [self setTransactionStatus:info.status];
}

-(void)requestSignature:(NSString *)receipt{
    NSLog(@"requestSignature");
}

-(void)cancelSignature{
    NSLog(@"cancelSignature");
}

-(void)didDiscoverFinished{
    NSLog(@"didDiscoverFinished");
    if ([[manager devicesCopy] count] > 0 ){
        NSArray* devices = [manager devicesCopy];
        [self createClient:devices[0]];
    }
}

-(void)didDiscoverDevice:(HeftRemoteDevice *)newDevice{
    NSLog(@"didDiscoverDevice");
}

-(void)didLostAccessoryDevice:(HeftRemoteDevice *)oldDevice{
    NSLog(@"didLostAccessoryDevice");
    [cardReaderSerial setText:@"None"];
    heftClient = nil;
}

-(void)hasSources{
    NSLog(@"hasSources");
}

-(void)noSources{
    NSLog(@"noSources");
}

-(void)responseLogInfo:(id<LogInfo>)info{
    NSLog(@"responceLogInfo");
}

@end
