package com.helenacorp.android.mybibliotheque;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by helena on 04/09/2017.
 */

public class SimpleScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner);
        mScannerView = new ZXingScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(com.google.zxing.Result rawResult) {
      //  Toast.makeText(this, "Contents = " + rawResult.getText() +
        //        ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_LONG).show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SimpleScannerActivity.this);
            }
        }, 2000);
        returnIsbn(rawResult);

    }

    public void returnIsbn(com.google.zxing.Result raw) {
        Intent returnI = new Intent(SimpleScannerActivity.this, SubmitBookActivity.class);
        returnI.putExtra("barcode", raw.getText().toString());
        setResult(Activity.RESULT_OK, returnI);
        finish();
    }
}
