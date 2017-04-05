package wepoy;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// printer
import android.device.PrinterManager;

// scanner
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.device.ScanManager;
import android.device.scanner.configuration.Triggering;

// card reader
import android.device.MagManager;

/**
 * This class echoes a string called from JavaScript.
 */
// onDecodeComplete barcodeType = 28
// BARCODE_FLAT	28 in printer
public class Wepoy extends CordovaPlugin {

    private PrinterManager printer = new PrinterManager();

    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;
    private ScanManager mScanManager;
    private String barcodeStr;
    private CallbackContext scanSuccessCallback;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra("barcode");
            int barocodelen = intent.getIntExtra("length", 0);
            barcodeStr = new String(barcode, 0, barocodelen);

            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);

            try {
              JSONObject json = new JSONObject();
              json.put("barcode_type", temp);
              json.put("barcode_str", barcodeStr);

              scanSuccessCallback.sendPluginResult(new PluginResult(PluginResult.Status.OK, json));
              mScanManager.stopDecode();
              mScanManager.closeScanner();
            } catch (JSONException e) {
                //some exception handler code.
            }
        }
    };

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        android.util.Log.i("debug", "WepoyPlugin execute: " + action);

        if(action.equals("printerStatus")) {
            this.printerStatus(callbackContext);
            return true;
        }

        if (action.equals("printLine")) {
            String message = args.getString(0);
            int printerOpened = printer.open();
            
            if(printerOpened == 0) {
                this.printLine(message, callbackContext);
            }
            else {
                // printer could not be opened
                callbackContext.error("Error opening printer");
            }
            return true;
        }

        if (action.equals("printCode")) {
            String message = args.getString(0);
            int codeType = args.getInt(1);

            int printerOpened = printer.open();

            if(printerOpened == 0) {
                this.printCode(message, codeType, callbackContext);
            }
            else {
                // printer could not be opened
                callbackContext.error("Error opening printer");
            }
            return true;
        }

        if (action.equals("paperFeed")) {
            int amount = args.getInt(0);
            int printerOpened = printer.open();

            if(printerOpened == 0) {
                this.paperFeed(amount, callbackContext);
            } else {
                // printer could not be opened
                callbackContext.error("Error opening printer");
            }
            return true;
        }

        if (action.equals("scanBarcode")) {
            mScanManager = new ScanManager();
            mScanManager.openScanner();
            mScanManager.switchOutputMode(0);
            if(mScanManager.getTriggerMode() != Triggering.CONTINUOUS)
                mScanManager.setTriggerMode(Triggering.CONTINUOUS);

            this.scanBarcode(callbackContext);
            return true;
        }

        if (action.equals("enableScanner")) {
            mScanManager = new ScanManager();
            boolean res = mScanManager.openScanner();
            mScanManager.switchOutputMode(0);
            if(mScanManager.getTriggerMode() != Triggering.CONTINUOUS)
                mScanManager.setTriggerMode(Triggering.CONTINUOUS);

            this.enableScanner(callbackContext);
            return true;
        }

        if (action.equals("listenToScan")) {
            mScanManager = new ScanManager();
            mScanManager.openScanner();
            mScanManager.switchOutputMode( 0);
            if(mScanManager.getTriggerMode() != Triggering.CONTINUOUS)
                mScanManager.setTriggerMode(Triggering.CONTINUOUS);

            this.listenToScan(callbackContext);
            return true;
        }

        if (action.equals("scanMagneticStripe")) {
            this.scanMagneticStripe(callbackContext);
            return true;
        }

        return false;
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    private void printerStatus(CallbackContext callbackContext) {

        if(printer.open() == 0) {

            int status = printer.getStatus();
            android.util.Log.i("debug", "printerStatus: " + status);
            if(status == 0) {
                callbackContext.success("Ok");
            } else if(status == -1) {
                callbackContext.error("PRINTER_OUT_OF_PAPER");
            } else {
                callbackContext.error("PRINTER_OVERHEATED");
            }
        }
        else {
            // printer could not be opened
                callbackContext.error("PRINTER_ERROR_OPENING_PRINTER");
        }
    }

    private void printLine(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {

            // first lets check the printer status
            int status = printer.getStatus();
            if(status != 0) {
                if(status == -1) {
                    callbackContext.error("PRINTER_OUT_OF_PAPER");
                } else {
                    callbackContext.error("PRINTER_OVERHEATED");
                }
                // abort, as printer is not ready
                return;
            }

            int res = printer.setupPage(384, -1);
            if(res == 0) {

                int ret = printer.drawTextEx(message, 0, 0, 384, -1, "courier", 21, 0, 0, 0);
                if(ret > -1) {
                    ret = printer.printPage(0);
                    if(ret == 0) {
                        callbackContext.success(message + " printPage: " + ret + " status: " + status);
                    } else {
                        callbackContext.error("Error while printing page");
                    }
                } else {
                    callbackContext.error("Error drawTextEx in print: ret = " + ret);
                }
            } else {
                callbackContext.error("Error setting up printer page");
            }
        } else {
            callbackContext.error("Expected one non-empty string argument");
        }
    }

    private void printCode(String message, int codeType, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {

            // first lets check the printer status
            int status = printer.getStatus();
            if(status != 0) {
                if(status == -1) {
                    callbackContext.error("PRINTER_OUT_OF_PAPER");
                } else {
                    callbackContext.error("PRINTER_OVERHEATED");
                }
                // abort, as printer is not ready
                return;
            }

            int res = printer.setupPage(384, -1);
            if(res == 0) {
                int bcret = printer.drawBarcode(message, 100, 10, codeType, 8, 240, 0);
                if (bcret > -1 ) {

                    int ret = printer.printPage(0);
                    if(ret == 0) {
                        callbackContext.success(message + " printPage: " + ret + " status: " + status);
                    } else {
                        callbackContext.error("Error printing page");
                    }
                } else {
                    callbackContext.error("Drawbarcode printing error");
                }
            }
            else {
                callbackContext.error("Error setting up printer page");
            }
        } else {
            callbackContext.error("Expected one non-empty string argument");
        }
    }

    private void paperFeed(int amount,CallbackContext callbackContext) {
        
        // first lets check the printer status
        int status = printer.getStatus();
        if(status != 0) {
            if(status == -1) {
                callbackContext.error("PRINTER_OUT_OF_PAPER");
            } else {
                callbackContext.error("PRINTER_OVERHEATED");
            }
            // abort, as printer is not ready
            return;
        }

        printer.paperFeed(amount);
        callbackContext.success("paperFeed successful");
    }



    private void scanBarcode(CallbackContext callbackContext) {
        listenToScan(callbackContext);
        // mScanManager.startDecode();
    }

    private void enableScanner(CallbackContext callbackContext) {
        mScanManager.startDecode();
        callbackContext.success("OK");
    }

    private void listenToScan(CallbackContext callbackContext) {
      IntentFilter filter = new IntentFilter();
      filter.addAction(SCAN_ACTION);
      webView.getContext().registerReceiver(mScanReceiver, filter);
      scanSuccessCallback = callbackContext;
    }

    private void scanMagneticStripe(CallbackContext callbackContext) {
      MagReadService mReadService = new MagReadService(webView.getContext(), callbackContext);
      mReadService.start();
    }

}
