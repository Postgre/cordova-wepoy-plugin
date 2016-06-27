package wepoy;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.device.PrinterManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.device.ScanManager;
import android.device.scanner.configuration.Triggering;
/**
 * This class echoes a string called from JavaScript.
 */
// onDecodeComplete barcodeType = 28
// BARCODE_FLAT	28 in printer
public class Wepoy extends CordovaPlugin {

    private PrinterManager printer;

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


            // showScanResult.setText(barcodeStr);
            // fire callback to scanSomething
            // String[] response;
            // response[0] = "" + temp;
            // response[1] = barcodeStr;

            try {
              JSONObject json = new JSONObject();
              json.put("barcode_type", temp);
              json.put("barcode_str", barcodeStr);

              scanSuccessCallback.sendPluginResult(new PluginResult(PluginResult.Status.OK, json));
            } catch (JSONException e) {
                //some exception handler code.
            }  

            // scanSuccessCallback.success(barcodeStr);
            mScanManager.closeScanner();
        }

    };


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        printer = new PrinterManager();
        if (action.equals("printSomething")) {
            String message = args.getString(0);
            int printerOpened = printer.open();

            this.printSomething(message + " " + printerOpened, callbackContext);
            return true;
        }

        if (action.equals("scanSomething")) {
            mScanManager = new ScanManager();
            mScanManager.openScanner();
            mScanManager.switchOutputMode( 0);
            if(mScanManager.getTriggerMode() != Triggering.CONTINUOUS)
                mScanManager.setTriggerMode(Triggering.CONTINUOUS);

            this.scanSomething(callbackContext);
            return true;
        }

        return false;
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    private void printSomething(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            int ret;
            printer.setupPage(384, -1);
            // printer.drawText("hello", 5, 0, "arial", 23, false, false, 0);

            ret = printer.drawTextEx(message, 5, 0,384,-1, "arial", 24, 0, 0, 0);
            ret = printer.printPage(0);

            int status  = printer.getStatus();

            callbackContext.success(message + " printPage: " + ret + " status: " + status);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void scanSomething(CallbackContext callbackContext) {
        mScanManager.startDecode();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        webView.getContext().registerReceiver(mScanReceiver, filter);
        scanSuccessCallback = callbackContext;
    }
}
