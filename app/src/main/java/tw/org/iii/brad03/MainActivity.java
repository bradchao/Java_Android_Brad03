package tw.org.iii.brad03;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private LocationManager lmgr;
    private MyListener listener;
    private UIHandler handler;
    private String mesg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new UIHandler();

        // 檢查是否取的授權
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 沒有授權, 所以要要求詢問授權
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);   // 123 => requestCode
        } else {
            initLocation();
        }

        webView = (WebView) findViewById(R.id.webview);
        initWebView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // get a grant
                initLocation();
            } else {
                finish();
            }
        }
    }

    private void initLocation() {
        lmgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new MyListener();
        lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lmgr != null) {
            lmgr.removeUpdates(listener);
        }
    }

    private class MyListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            webView.loadUrl("javascript:goto(" + lat + ", " + lng + ")");
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
    }


    private void initWebView(){
        webView.setWebViewClient(new WebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new BradJS(), "brad");

        webView.loadUrl("file:///android_asset/map.html");
    }

    public void gotoWhere(View v){
        webView.loadUrl("javascript:goto(24.150947, 120.652709)");
    }
    public void gotoBrad(View v){
        webView.loadUrl("file:///android_asset/brad.html");
    }

    public class BradJS {
        @JavascriptInterface
        public boolean showDialog(String mesg){
            MainActivity.this.mesg = mesg;
            handler.sendEmptyMessage(0);
            return true;
        }
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //Log.d("brad", mesg);
                    //showAlert();
                    showConfirm();
                    break;
            }
        }
    }

    private void showAlert(){
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warn");
        builder.setMessage(mesg);

        dialog = builder.create();
        dialog.show();
    }
    private void showConfirm(){
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warn");
        builder.setMessage(mesg);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("brad", "OK");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("brad", "Cancel");
            }
        });
        builder.setNeutralButton("later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("brad", "later");
            }
        });


        dialog = builder.create();
        dialog.show();
    }
    private void showPrompt(){
    }



}
