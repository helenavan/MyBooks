package com.helenacorp.android.mybibliotheque

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.zxing.Result
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AddBookFragment
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * Created by helena on 04/09/2017.
 */
class SimpleScannerActivity : BaseScannerActivity(), ZXingScannerView.ResultHandler {
    private var mScannerView: ZXingScannerView? = null
    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_simple_scanner)
        mScannerView = ZXingScannerView(this) // Programmatically initialize the scanner view
        setContentView(mScannerView) // Set the scanner view as the content view
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera() // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        //  Toast.makeText(this, "Contents = " + rawResult.getText() +
        //        ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_LONG).show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        val handler = Handler()
        handler.postDelayed({ mScannerView!!.resumeCameraPreview(this@SimpleScannerActivity) }, 2000)
        returnIsbn(rawResult)
    }

    fun returnIsbn(raw: Result) {
        val returnI = Intent(this@SimpleScannerActivity, AddBookFragment::class.java)
        returnI.putExtra("barcode", raw.text.toString())
        setResult(Activity.RESULT_OK, returnI)
        finish()
    }
}