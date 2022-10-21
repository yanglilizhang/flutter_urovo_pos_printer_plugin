import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_urovo_pos_printer_plugin_method_channel.dart';

abstract class FlutterUrovoPosPrinterPluginPlatform extends PlatformInterface {
  /// Constructs a FlutterUrovoPosPrinterPluginPlatform.
  FlutterUrovoPosPrinterPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterUrovoPosPrinterPluginPlatform _instance = MethodChannelFlutterUrovoPosPrinterPlugin();

  /// The default instance of [FlutterUrovoPosPrinterPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterUrovoPosPrinterPlugin].
  static FlutterUrovoPosPrinterPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterUrovoPosPrinterPluginPlatform] when
  /// they register themselves.
  static set instance(FlutterUrovoPosPrinterPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool?> initPrinter() {
    throw UnimplementedError('initPrinter() has not been implemented.');
  }

  Future<String?> print() {
    throw UnimplementedError('print() has not been implemented.');
  }

  Future<int?> getStatus() {
    throw UnimplementedError('getStatus() has not been implemented.');
  }

  Future<int?> dispose() {
    throw UnimplementedError('dispose() has not been implemented.');
  }

  Future<int?> setupPage({required int height, required int width}) {
    throw UnimplementedError('setupPage() has not been implemented.');
  }

  Future<int?> setGrayLevel(int level) {
    throw UnimplementedError('setGrayLevel() has not been implemented.');
  }

  Future<int?> paperFeed(int length) {
    throw UnimplementedError('paperFeed() has not been implemented.');
  }

  Future<int?> setSpeedLevel(int level) {
    throw UnimplementedError('setSpeedLevel() has not been implemented.');
  }

  Future<int?> clearPage() {
    throw UnimplementedError('clearPage() has not been implemented.');
  }

  Future<int?> printPage(int rotate) {
    throw UnimplementedError('printPage() has not been implemented.');
  }

  Future<int?> drawLine(
      {required int x0, required int y0, required int x1, required int y1, required int lineWidth}) {
    throw UnimplementedError('drawLine() has not been implemented.');
  }

  Future<int?> drawText(
      {required String data, required int x, required int y, required String fontName, required int fontSize, required bool isBold, required bool isItalic, required int rotate}) {
    throw UnimplementedError('drawText() has not been implemented.');
  }

  Future<int?> drawTextEx({required String data,
    required int x,
    required int y,
    required int width,
    required int height,
    required String fontName,
    required int fontSize,
    required int rotate,
    required int style,
    required int format}) {
    throw UnimplementedError('drawTextEx() has not been implemented.');
  }

  Future<int?> drawBarcode(
      {required String data, required int x, required int y, required int barcodeType, required int width, required int height, required int rotate}) {
    throw UnimplementedError('drawBarcode() has not been implemented.');
  }

  Future<int?> drawBitmap(
      {required String image, required int xDest, required int yDest}) {
    throw UnimplementedError('drawBitmap() has not been implemented.');
  }

  Future<int?> drawBitmapEx({required List<int> byteBitmap,
    required int xDest, required int yDest, required int widthDest, required int heightDest}) {
    throw UnimplementedError('drawBitmapEx() has not been implemented.');
  }

}
