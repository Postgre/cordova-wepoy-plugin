package wepoy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.json.JSONObject;
import org.json.JSONException;
import android.support.v4.content.LocalBroadcastManager;


public class BarcodeReceiver extends BroadcastReceiver {
  private String barcodeStr;

  @Override
  public void onReceive(Context context, Intent intent) {
      byte[] barcode = intent.getByteArrayExtra("barcode");
      int barocodelen = intent.getIntExtra("length", 0);
      barcodeStr = new String(barcode, 0, barocodelen);

      byte temp = intent.getByteExtra("barcodeType", (byte) 0);
      android.util.Log.i("debug", "----codetype--" + temp);
      android.util.Log.i("debug", "----barcodeStr--" + barcodeStr);

      this.sendBarcode(context, barcodeStr);
  }

  public void sendBarcode(Context context, String barcode) {
    android.util.Log.i("debug", "Sending barcode via didScanBarcode");
    final Intent intentb = new Intent("didScanBarcode");
    Bundle b = new Bundle();
    b.putString( "userdata", "{ barcode: '"+barcode+"'}" );
    intentb.putExtras( b);
    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.ionicframework.ferryticketmachine577699");
    if (launchIntent != null) {
        context.startActivity(launchIntent);//null pointer check in case package name was not found
    }
    LocalBroadcastManager.getInstance(context).sendBroadcastSync(intentb);
  }
}
