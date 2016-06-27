package wepoy;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.device.PrinterManager;
/**
 * This class echoes a string called from JavaScript.
 */
public class Wepoy extends CordovaPlugin {

    private PrinterManager printer;
    private ScanManager mScanManager;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        printer = new PrinterManager();
        if (action.equals("printSomething")) {
            String message = args.getString(0);
            int printerOpened = printer.open();

            this.printSomething(message + " " + printerOpened, callbackContext);
            return true;
        }

        return false;
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

}
