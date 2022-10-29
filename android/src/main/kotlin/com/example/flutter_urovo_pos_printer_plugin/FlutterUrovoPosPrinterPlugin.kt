package com.example.flutter_urovo_pos_printer_plugin

import android.device.PrinterManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.FileInputStream


/** FlutterUrovoPosPrinterPlugin */
class FlutterUrovoPosPrinterPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  // Printer status
  private val PRNSTS_OK = 0 //OK
  private val PRNSTS_OUT_OF_PAPER = -1 //Out of paper
  private val PRNSTS_OVER_HEAT = -2 //Over heat
  private val PRNSTS_UNDER_VOLTAGE = -3 //under voltage
  private val PRNSTS_BUSY = -4 //Device is busy
  private val PRNSTS_ERR = -256 //Printing error
  private val PRNSTS_ERR_DRIVER = -257 //Driver error


  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_urovo_pos_printer_plugin")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "initPrinter") {
      result.success(initPrinter())
    } else if(call.method.equals("getStatus")){
      getPrinterStatus(result)
    } else if(call.method.equals("printTest")){
      printTest(result)
    } else if (call.method == "dispose") {
      closePrinter(result)
    } else if (call.method == "paperFeed") {
      val arguments = call.arguments as HashMap<*, *>
      val level = arguments["length"] as Int
      //level - Number of steps required. The range of Gray level is 0 to 100, 1mm for every 1 units.
      if (level>=0||level<=100) {
        paperFeed(level,result)
      } else {
        result.error("out of level","paperFeed",null)
      }
    } else if (call.method == "setSpeedLevel") {
      val arguments = call.arguments as HashMap<*, *>
      val level = arguments["level"] as Int
      //level - The range of Gray level is 0 to 9. The default value is 9.
      if (level>=0||level<=9) {
        setSpeedLevel(level,result)
      } else {
        result.error("out of level","setSpeedLevel",null)
      }
    } else if (call.method == "setGrayLevel") {
      val arguments = call.arguments as HashMap<*, *>
      val level = arguments["level"] as Int
      //level - The range of Gray level is 0 to 4. The default value is 0.
      if (level>=0||level<=4) {
        setGrayLevel(level,result)
      } else {
        result.error("out of level","setGrayLevel",null)
      }
    } else if (call.method == "setupPage") {
      //width - Page width. -1 means largest possible width (width = 384).
      //height - Page height. -1 means printer driver to manage the page height.
      val arguments = call.arguments as HashMap<*, *>
      val height = arguments["height"] as Int
      val width = arguments["width"] as Int
      setupPage(result,height, width)
    } else if (call.method == "clearPage") {
      //0 if successful, -1 if failed.
      clearPage(result)
    } else if (call.method == "printPage") {
      //rotate - The rotation angle, currently supports only 0 (non-rotating)
      val arguments = call.arguments as HashMap<*, *>
      val rotate = arguments["rotate"] as Int
      printPage(rotate,result)
    } else if (call.method == "drawLine") {
      //Draw a line in the current page. The (0,0) point axis is on the upper left corner of the screen.
      //Parameters:
      //x0 - Start point X axis.
      //y0 - Start point Y axis.
      //x1 - End point X axis.
      //y1 - End point Y axis.
      //lineWidth - In pixel. 8 pixels is equivalent to 1 mm.
      val arguments = call.arguments as HashMap<*, *>
      val x0 = arguments["x0"] as Int
      val y0 = arguments["y0"] as Int
      val x1 = arguments["x1"] as Int
      val y1 = arguments["y1"] as Int
      val lineWidth = arguments["lineWidth"] as Int
      drawLine(x0,y0,x1,y1,lineWidth,result)
    } else if (call.method == "drawText"){
      //data - The string to draw.
      //x - Start point X axis.
      //y - Start point Y axis.
      //fontName - Font to use, otherwise, default system font will be used. Custom fonts can be specified, for example, specifying the full path /mnt/sdcard/xxx.ttf.
      //fontSize - The font size, in pixel.
      //bold - Set font style to bold.
      //italic - Set font style to italic.
      //rotate - The text direction. Value:
      //            0 : no rotation
      //            1 : rotate 90 degree
      //            2 : rotate 180 degree
      //            3 : rotate 270 degree
      val arguments = call.arguments as HashMap<*, *>
      val data = arguments.get("data") as String
      val x = arguments.get("x") as Int
      val y = arguments.get("y") as Int
      val fontName = arguments.get("fontName") as String
      val fontSize = arguments.get("fontSize") as Int
      val bold = arguments.get("isBold") as Boolean
      val italic = arguments.get("isItalic") as Boolean
      val rotate = arguments.get("rotate") as Int
      drawText(data,x,y,fontName,fontSize,bold,italic,rotate,result)
    } else if (call.method == "drawTextEx"){
      //data - The string to draw.
      //x - Start point X axis.
      //y - Start point Y axis.
      //width - Text is printed to the width of a rectangle on the page.
      //height - Text is printed to the height of a rectangle on the page.
      //fontName - Font to use, otherwise, default system font will be used. Or custom fonts i.e. /mnt/sdcard/xxx.ttf the path.
      //fontSize - The font size, in pixel.
      //rotate - Print the text rotated to requested degress. Value:
      //            0 : no rotation
      //            1 : rotate 90 degree
      //            2 : rotate 180 degree
      //            3 : rotate 270 degree
      //style - Font style of value:
      //            0x0001 : underline
      //            0x0002 : italic
      //            0x0004 : bold
      //            0x0008 : reverse effect
      //            0x0010 : strike out
      //    you can mix the style by using the or operator, style= 0x0002|0x0004.
      //format - Set to 0 means word wrap at the specified width range 0 to 384, set to 1 means no word wrap.
      val arguments = call.arguments as HashMap<*, *>
      val data = arguments.get("data") as String
      val x = arguments["x"] as Int
      val y = arguments["y"] as Int
      val width = arguments["width"] as Int
      val height = arguments["height"] as Int
      val fontName = arguments["fontName"] as String?
      val fontSize = arguments["fontSize"] as Int
      val rotate = arguments["rotate"] as Int
      val style = arguments["style"] as Int
      val format = arguments["format"] as Int
      drawTextEx(data, x,y,width,height,fontName,fontSize,rotate,style,format,result)
    } else if (call.method == "drawBarcode"){
      //data - The barcode text.
      //x - Start point at X axis.
      //y - Start point at Y axis.
      //barcodeType some type
      //width - There are four thickness level to the lines, 1 being the thinnest and 4 being the thickest.
      //height - The barcode height in pixel.
      //rotate - The barcode rotation, Value:
      //            0 : no rotation
      //            1 : rotate 90 degree
      //            2 : rotate 180 degree
      //            3 : rotate 270 degree
      val arguments = call.arguments as HashMap<*, *>
      val data = arguments["data"] as String?
      val x = arguments["x"] as Int
      val y = arguments["y"] as Int
      val barcodeType = arguments["barcodeType"] as Int
      val width = arguments["width"] as Int
      val height = arguments["height"] as Int
      val rotate = arguments["rotate"] as Int
      drawBarcode(data, x,y,barcodeType,width,height,rotate,result)
    } else if (call.method == "drawBitmap") {
      //bmp - The bitmap to drawn.
      //xDest - Start point at X axis.
      //yDest - Start point at Y axis.
      val arguments = call.arguments as HashMap<*, *>
      val image = arguments["image"] as String?
      val xDest = arguments["xDest"] as Int
      val yDest = arguments["yDest"] as Int
      //      val opts = BitmapFactory.Options()
//      val img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_camera, opts)
      val input = FileInputStream(image)
      val buffer = BufferedInputStream(input)
      val bMapArray = ByteArray(buffer.available())
      buffer.read(bMapArray)
      val bitmap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.size)
      drawBitmap(bitmap,xDest,yDest,result)
    } else if (call.method == "drawBitmapEx") {
      //bmp - ByteArray data for mono-bitmaps.
      //xDest - Start point at X axis.
      //yDest - Start point at Y axis.
      //widthDest - Horizontal width bytes.
      //heightDest - Vertical height point.
      //Draw a mono-bitmaps on the current page.
        val arguments = call.arguments as java.util.HashMap<*, *>
        val byteBitmap = arguments["byteBitmap"] as ByteArray?
        val xDest = arguments["xDest"] as Int
        val yDest = arguments["yDest"] as Int
        val widthDest = arguments["widthDest"] as Int
        val heightDest = arguments["heightDest"] as Int
        drawBitmapEx(byteBitmap, xDest, yDest, widthDest, heightDest,result)
    } else {
      result.notImplemented()
    }
  }

  private fun drawBitmapEx(bitmap: ByteArray?, xDest: Int, yDest: Int, widthDest: Int, heightDest: Int, result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            if (async { printerManager?.drawBitmapEx(bitmap,xDest,yDest,widthDest,heightDest) }.await()!!>=PRNSTS_OK){
              result.success(true)
            } else{
              result.success(false)
            }
          } else{
            result.success(false)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun drawBitmap(bitmap: Bitmap?, xDest: Int, yDest: Int, result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            if (async { printerManager?.drawBitmap(bitmap,xDest,yDest) }.await()!!>=PRNSTS_OK){
              result.success(true)
            } else{
              result.success(false)
            }
          } else{
            result.success(false)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun drawBarcode(
    data: String?,
    x: Int,
    y: Int,
    barcodeType: Int,
    width: Int,
    height: Int,
    rotate: Int,
    result: Result
  ) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            if (async { printerManager?.drawBarcode(data, x, y, barcodeType, width, height, rotate) }.await()!!>=PRNSTS_OK){
              result.success(true)
            } else{
              result.success(false)
            }
          } else{
            result.success(false)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun drawTextEx(
    data: String,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    fontName: String?,
    fontSize: Int,
    rotate: Int,
    style: Int,
    format: Int,
    result: Result
  ) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            if (async { printerManager?.drawTextEx(data, x,y,width,height,fontName,fontSize,rotate,style,format) }.await()!!>=PRNSTS_OK){
              result.success(true)
            } else{
              result.success(false)
            }
          } else{
            result.success(false)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun drawText(
    data: String,
    x: Int,
    y: Int,
    fontName: String,
    fontSize: Int,
    bold: Boolean,
    italic: Boolean,
    rotate: Int,
    result: Result
  ) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            if (async { printerManager?.drawText(data,x,y,fontName,fontSize,bold,italic,rotate) }.await()!! >=PRNSTS_OK){
              result.success(true)
            } else{
              result.success(false)
            }
          } else{
            result.success(false)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun drawLine(x0: Int, y0: Int, x1: Int, y1: Int, lineWidth: Int, result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            if (async { printerManager?.drawLine(x0, y0, x1, y1, lineWidth) }.await()==PRNSTS_OK){
              result.success(true)
            } else{
              //drawLine failed
              result.success(false)
            }
          } else{
            result.success(false)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun printPage(rotate: Int,result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            when (async { printerManager?.printPage(rotate) }.await()) {
              PRNSTS_OK -> {
                result.success("Printer OK")
              }
              PRNSTS_OUT_OF_PAPER -> {
                result.success("Out of paper")
              }
              PRNSTS_OVER_HEAT -> {
                result.success("Over heat")
              }
              PRNSTS_UNDER_VOLTAGE -> {
                result.success("Under voltage")
              }
              PRNSTS_BUSY -> {
                result.success("Device is busy")
              }
              PRNSTS_ERR -> {
                result.success("Printing error")
              }
              PRNSTS_ERR_DRIVER -> {
                result.success("Driver error")
              }
            }
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success("error")
    }
  }

  private fun clearPage(result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            val state = async { printerManager?.clearPage() }.await()
            if(state==PRNSTS_OK) {
              result.success(true)
            }else{
              result.success(false)
            }
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          ex.printStackTrace()
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun setSpeedLevel(level: Int, result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            printerManager?.setSpeedLevel(level)
            result.success(true)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          ex.printStackTrace()
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun paperFeed(level: Int, result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            printerManager?.paperFeed(level)
            result.success(true)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          ex.printStackTrace()
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun setGrayLevel(level: Int, result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            printerManager?.setGrayLevel(level)
            result.success(true)
          } else {
            result.success(false)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          ex.printStackTrace()
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager!!.close()
    } else {
      result.success(false)
    }
  }

  private fun closePrinter(result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret==PRNSTS_OK){
            printerManager?.close()
            result.success(true)
          } else {
            result.success(false)
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          ex.printStackTrace()
          result.error("1", ex.message, ex.stackTrace)
        }
      }
    } else {
      result.success(false)
    }
  }

  private fun setupPage(result: Result, height: Int, width: Int) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret == PRNSTS_OK) {
            //open success
            val re = async { printerManager!!.setupPage(width, height) }.await()
            if (re == PRNSTS_OK) {
              result.success(true)
            } else {
              result.success(false)
            }
          } else {
            result.success(false)//
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          ex.printStackTrace()
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager?.close()
    } else {
      result.success(-1)
    }
  }

  private fun getPrinterStatus(result: Result) {
    if (initPrinter()) {
      var printerManager: PrinterManager? = null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          val ret = async { printerManager?.open() }.await()
          if (ret == PRNSTS_OK) {
            //open success
            when (async { printerManager?.status }.await()) {
              PRNSTS_OK -> {
                result.success("Printer OK")
              }
              PRNSTS_OUT_OF_PAPER -> {
                result.success("Out of paper")
              }
              PRNSTS_OVER_HEAT -> {
                result.success("Over heat")
              }
              PRNSTS_UNDER_VOLTAGE -> {
                result.success("Under voltage")
              }
              PRNSTS_BUSY -> {
                result.success("Device is busy")
              }
              PRNSTS_ERR -> {
                result.success("Printing error")
              }
              PRNSTS_ERR_DRIVER -> {
                result.success("Driver error")
              }
            }
          } else {
            result.success("Printer open failed")//
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          ex.printStackTrace()
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager?.close()
    } else {
      result.success(false)
    }
  }
  var mPrinterManager: PrinterManager? = null

  open fun getPrinterManager(): PrinterManager? {
    if (mPrinterManager == null) {
      mPrinterManager = PrinterManager()
      mPrinterManager!!.open()
    }
    return mPrinterManager
  }

  private fun initPrinter(): Boolean {
    return try {
      val className = "android.device.PrinterManager"
      Class.forName(className).newInstance()
      val ret = getPrinterManager()?.status
      ret!! >=PRNSTS_OK
    } catch (ex: ClassNotFoundException) {
      println("catch : $ex");
      false
    }
  }

  private fun printTest(result: Result) {
    if (initPrinter()) {
      var printerManager:PrinterManager?=null
      try {
        runBlocking {
          printerManager = async { connectPrinterManager() }.await()
          if (printerManager != null) {
            val ret = async { printerManager?.open() }.await()
            if (ret == PRNSTS_OK) {
              //open success
              runBlocking {
                val status = async { printTestPage(printerManager!!)}.await()
                result.success(status)
              }
            } else {
              result.success("open failed")
            }
          }
        }
      } catch (ex: Exception) {
        printerManager?.close()
        result.error("1", ex.message, ex.stackTrace)
      } finally {
        try {
          if (printerManager != null) {
            printerManager!!.close()
          }
        } catch (ex: ClassNotFoundException) {
          ex.printStackTrace()
          result.error("1", ex.message, ex.stackTrace)
        }
      }
      printerManager?.close()
    } else {
      result.success(false)
    }
  }

  private fun printTestPage(printerManager: PrinterManager): Boolean {
    val fontName = "simsun"
    return try {
      printerManager.setupPage(384, -1)
      printerManager.prn_drawText("Hello", 200, 200, fontName, 32, false, false, 0)
      val fontSize = 24
      val fontStyle = 0x0000
      val height = 0

      printerManager.prn_drawLine(32, 8, 136, 8, 8)
      printerManager.prn_drawLine(32, 12, 136, 12, 8)
      printerManager.prn_drawLine(32, 18, 136, 18, 8)
      printerManager.prn_drawLine(32, 24, 136, 24, 8)
      printerManager.prn_drawLine(32, 32, 136, 32, 32)

      printerManager.prn_drawLine(136, 56, 240, 56, 8)
      printerManager.prn_drawLine(136, 62, 240, 62, 8)
      printerManager.prn_drawLine(136, 68, 240, 68, 8)
      printerManager.prn_drawLine(136, 74, 240, 74, 8)
      printerManager.prn_drawLine(136, 80, 240, 80, 32)

      printerManager.prn_drawLine(240, 104, 344, 104, 8)
      printerManager.prn_drawLine(240, 110, 344, 110, 8)
      printerManager.prn_drawLine(240, 116, 344, 116, 8)
      printerManager.prn_drawLine(240, 122, 344, 122, 8)
      printerManager.prn_drawLine(240, 128, 344, 128, 32)

      printerManager.prn_drawLine(136, 152, 240, 152, 8)
      printerManager.prn_drawLine(136, 158, 240, 158, 8)
      printerManager.prn_drawLine(136, 164, 240, 164, 8)
      printerManager.prn_drawLine(136, 170, 240, 170, 8)
      printerManager.prn_drawLine(136, 176, 240, 176, 32)

      printerManager.prn_drawLine(32, 200, 136, 200, 8)
      printerManager.prn_drawLine(32, 206, 136, 206, 8)
      printerManager.prn_drawLine(32, 212, 136, 212, 8)
      printerManager.prn_drawLine(32, 218, 136, 218, 8)
      printerManager.prn_drawLine(32, 224, 136, 224, 32)
      printerManager.printPage(0)
      true
    }catch (ex: Exception){
      false
    }
  }

  private suspend fun connectPrinterManager(): PrinterManager? {
    return GlobalScope.async {
      return@async PrinterManager()
    }.await();
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
