
import 'flutter_urovo_pos_printer_plugin_platform_interface.dart';

class FlutterUrovoPosPrinterPlugin {
  Future<String?> getPlatformVersion() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.getPlatformVersion();
  }

  Future<bool?> initPrinter(){
    return FlutterUrovoPosPrinterPluginPlatform.instance.initPrinter();
  }

  Future<String?> print() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.print();
  }

  Future<int?> getStatus() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.getStatus();
  }

  Future<int?> dispose() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.dispose();
  }

  Future<int?> setupPage({required int height, required int width}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.setupPage(height: height, width: width);
  }

  Future<int?> setGrayLevel(int level) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.setGrayLevel(level);
  }

  Future<int?> paperFeed(int length) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.paperFeed(length);
  }

  Future<int?> setSpeedLevel(int level) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.setSpeedLevel(level);
  }

  Future<int?> clearPage() {
    return FlutterUrovoPosPrinterPluginPlatform.instance.clearPage();
  }

  Future<int?> printPage(int rotate) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.printPage(rotate);
  }

  Future<int?> drawLine(
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

  Future<int?> drawText(
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

  Future<int?> drawTextEx(
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

  Future<int?> drawBarcode(
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

  Future<int?> drawBitmap(
      {required String image, required int xDest, required int yDest}) {
    return FlutterUrovoPosPrinterPluginPlatform.instance.drawBitmap(
      image: image,
      xDest: xDest,
      yDest: yDest,
    );
  }

  Future<int?> drawBitmapEx(
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
