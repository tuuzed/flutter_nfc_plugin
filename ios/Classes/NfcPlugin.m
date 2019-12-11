#import "NfcPlugin.h"
#import <nfc_plugin/nfc_plugin-Swift.h>

@implementation NfcPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftNfcPlugin registerWithRegistrar:registrar];
}
@end
