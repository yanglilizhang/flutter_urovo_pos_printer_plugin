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
  Future<String?> printTest() async {
    final print = await methodChannel.invokeMethod<String>('printTest');
    return print;
  }

  @override
  Future<int?> getStatus() async {
    final status = await methodChannel.invokeMethod<int>('getStatus');
    return status;
  }

  @override
  Future<int?> dispose() async {
    final dispose = await methodChannel.invokeMethod<int>('dispose');
    return dispose;
  }

  @override
  Future<int?> setupPage({required int height, required int width}) async {
    final setup = await methodChannel.invokeMethod<int>('setupPage', {
      'height': height,
      'width': width,
    });
    return setup;
  }

  //* set gray level
  @override
  Future<int?> setGrayLevel(int level) async {
    final setLevel = await methodChannel.invokeMethod("setGrayLevel", [level]);
    return setLevel;
  }

  //* set paper feed
  @override
  Future<int?> paperFeed(int length) async {
    final length = await methodChannel.invokeMethod('paperFeed');
    return length;
  }

//* set speed level [0-9]
  @override
  Future<int?> setSpeedLevel(int level) async {
    final level = await methodChannel.invokeMethod('setSpeedLevel');
    return level;
  }

  // * clear page setup
  @override
  Future<int?> clearPage() async {
    return await methodChannel.invokeMethod('clearPage');
  }

  //* print page
  @override
  Future<int?> printPage(int rotate) async {
    return await methodChannel.invokeMethod('printPage', [rotate]);
  }

  //* draw
  @override
  Future<int?> drawLine({required int x0,
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
  Future<int?> drawText({required String data,
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
  Future<int?> drawTextEx({required String data,
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
  Future<int?> drawBarcode({
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
  Future<int?> drawBitmap({
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
  Future<int?> drawBitmapEx({
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
