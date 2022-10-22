import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_urovo_pos_printer_plugin_platform_interface.dart';

/// An implementation of [FlutterUrovoPosPrinterPluginPlatform] that uses method channels.
class MethodChannelFlutterUrovoPosPrinterPlugin extends FlutterUrovoPosPrinterPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_urovo_pos_printer_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
        'getPlatformVersion');
    return version;
  }

  @override
  Future<bool?> initPrinter() async {
    final status = await methodChannel.invokeMethod<bool>('initPrinter');
    return status;
  }

  @override
  Future<bool?> printTest() async {
    final print = await methodChannel.invokeMethod<bool>('printTest');
    return print;
  }

  @override
  Future<String?> getStatus() async {
    final status = await methodChannel.invokeMethod<String>('getStatus');
    return status;
  }

  @override
  Future<bool?> dispose() async {
    final dispose = await methodChannel.invokeMethod<bool>('dispose');
    return dispose;
  }

  @override
  Future<bool?> setupPage({required int height, required int width}) async {
    final setup = await methodChannel.invokeMethod<bool>('setupPage', {
      'height': height,
      'width': width,
    });
    return setup;
  }

  //* set gray level
  @override
  Future<bool?> setGrayLevel({required int level}) async {
    final setLevel = await methodChannel.invokeMethod("setGrayLevel", {level:level});
    return setLevel;
  }

  //* set paper feed
  @override
  Future<bool?> paperFeed({required int length}) async {
    final setLength = await methodChannel.invokeMethod('paperFeed',{'length':length});
    return setLength;
  }

//* set speed level [0-9]
  @override
  Future<bool?> setSpeedLevel({required int level}) async {
    final setLevel = await methodChannel.invokeMethod('setSpeedLevel',{'level':level});
    return setLevel;
  }

  // * clear page setup
  @override
  Future<bool?> clearPage() async {
    return await methodChannel.invokeMethod('clearPage');
  }

  //* print page
  @override
  Future<String?> printPage({required int rotate}) async {
    return await methodChannel.invokeMethod('printPage', {'rotate':rotate});
  }

  //* draw
  @override
  Future<bool?> drawLine({required int x0,
    required int y0,
    required int x1,
    required int y1,
    required int lineWidth}) async {
    return await methodChannel.invokeMethod('drawLine', {
      'x0': x0,
      'y0': y0,
      'x1': x1,
      'y1': y1,
      'lineWidth': lineWidth,
    });
  }

  //* draw text
  @override
  Future<bool?> drawText({required String data,
    required int x,
    required int y,
    required String fontName,
    required int fontSize,
    required bool isBold,
    required bool isItalic,
    required int rotate}) async {
    return await methodChannel.invokeMethod('drawText', {
      'data': data,
      'x': x,
      'y': y,
      'fontName': fontName,
      'fontSize': fontSize,
      'isBold': isBold,
      'isItalic': isItalic,
      'rotate': rotate,
    });
  }

  //* draw text ex
  @override
  Future<bool?> drawTextEx({required String data,
    required int x,
    required int y,
    required int width,
    required int height,
    required String fontName,
    required int fontSize,
    required int rotate,
    required int style,
    required int format}) async {
    return await methodChannel.invokeMethod('drawTextEx', {
      'data': data,
      'x': x,
      'y': y,
      'width': width,
      'height': height,
      'fontName': fontName,
      'fontSize': fontSize,
      'rotate': rotate,
      'style': style,
      'format': format,
    });
  }

  //* draw barcode
  @override
  Future<bool?> drawBarcode({
    required String data,
    required int x,
    required int y,
    required int barcodeType,
    required int width,
    required int height,
    required int rotate,
  }) async {
    return await methodChannel.invokeMethod('drawBarcode', {
      'data': data,
      'x': x,
      'y': y,
      'barcodeType': barcodeType,
      'width': width,
      'height': height,
      'rotate': rotate,
    });
  }

  // * draw bitmap
  @override
  Future<bool?> drawBitmap({
    required String image,
    required int xDest,
    required int yDest,
  }) async {
    return await methodChannel.invokeMethod('drawBitmap', {
      'image': image,
      'xDest': xDest,
      'yDest': yDest,
    });
  }

  // * draw bitmap ex
  @override
  Future<bool?> drawBitmapEx({
    required List<int> byteBitmap,
    required int xDest,
    required int yDest,
    required int widthDest,
    required int heightDest,
  }) async {
    return await methodChannel.invokeMethod('drawBitmapEx', {
      'byteBitmap': byteBitmap,
      'xDest': xDest,
      'yDest': yDest,
      'widthDest': widthDest,
      'heightDest': heightDest,
    });
  }
}
