
import 'flutter_urovo_pos_printer_plugin_platform_interface.dart';

class FlutterUrovoPosPrinterPlugin {
  Future<String?> getPlatformVersion() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.getPlatformVersion();
  }

  Future<bool?> initPrinter(){
    return FlutterUrovoPosPrinterPluginPlatform.instance.initPrinter();
  }

  Future<bool?> printTest() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.printTest();
  }

  Future<String?> getStatus() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.getStatus();
  }

  Future<bool?> dispose() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.dispose();
  }

  Future<bool?> setupPage({required int height, required int width}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.setupPage(height: height, width: width);
  }

  Future<bool?> setGrayLevel({required int level}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.setGrayLevel(level:level);
  }

  Future<bool?> paperFeed({required int length}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.paperFeed(length:length);
  }

  Future<bool?> setSpeedLevel(int level) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.setSpeedLevel(level:level);
  }

  Future<bool?> clearPage() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.clearPage();
  }

  Future<String?> printPage({required int rotate}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.printPage(rotate:rotate);
  }

  Future<bool?> drawLine(
      {required int x0,
        required int y0,
        required int x1,
        required int y1,
        required int lineWidth}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.drawLine(
      x0: x0,
      y0: y0,
      x1: x1,
      y1: y1,
      lineWidth: lineWidth,
    );
  }

  Future<bool?> drawText(
      {required String data,
        required int x,
        required int y,
        required String fontName,
        required int fontSize,
        required bool isBold,
        required bool isItalic,
        required int rotate}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.drawText(
      data: data,
      x: x,
      y: y,
      fontName: fontName,
      fontSize: fontSize,
      isBold: isBold,
      isItalic: isItalic,
      rotate: rotate,
    );
  }

  Future<bool?> drawTextEx(
      {required String data,
        required int x,
        required int y,
        required int width,
        required int height,
        required String fontName,
        required int fontSize,
        required int rotate,
        required int style,
        required int format
      }) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.drawTextEx(
      data: data,
      x: x,
      y: y,
      width: width,
      height: height,
      fontName: fontName,
      fontSize: fontSize,
      rotate: rotate,
      style: style,
      format: format,
    );
  }

  Future<bool?> drawBarcode(
      {required String data,
        required int x,
        required int y,
        required int barcodeType,
        required int width,
        required int height,
        required int rotate}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.drawBarcode(
      data: data,
      x: x,
      y: y,
      barcodeType: barcodeType,
      width: width,
      height: height,
      rotate: rotate,
    );
  }

  Future<bool?> drawBitmap(
      {required String image, required int xDest, required int yDest}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.drawBitmap(
      image: image,
      xDest: xDest,
      yDest: yDest,
    );
  }

  Future<bool?> drawBitmapEx(
      {required List<int> byteBitmap,
        required int xDest,
        required int yDest,
        required int widthDest,
        required int heightDest}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.drawBitmapEx(
      byteBitmap: byteBitmap,
      xDest: xDest,
      yDest: yDest,
      widthDest: widthDest,
      heightDest: heightDest,
    );
  }
}
