package com.ftc5466.citrusscouter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ImportFromQRActivity extends Activity implements ZXingScannerView.ResultHandler {
    public static final String INTENT_PURPOSE_KEY = "ImportFromQRPurpose";
    public static final String INTENT_PURPOSE_TEAMS = "ImportFromQRTeamsPurpose";
    public static final String INTENT_PURPOSE_MATCHLIST = "ImportFromQRMatchlistPurpose";

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_import_from_qr);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        contentFrame.addView(mScannerView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        String string = rawResult.getText();
        if (string.isEmpty()) { return; }

        switch (getIntent().getStringExtra(INTENT_PURPOSE_KEY)) {
            case INTENT_PURPOSE_TEAMS:
                CitrusDb.getInstance().importTeamsFromString(string);
                break;
            case INTENT_PURPOSE_MATCHLIST:
                CitrusDb.getInstance().importMatchlistFromString(this, string);
                break;
            default:
                Toast.makeText(this, "THE DEV NEEDS TO ADD INTENT_PURPOSE_KEY to this activity", Toast.LENGTH_SHORT).show();
                MainActivity.logE("THE DEV NEEDS TO ADD INTENT_PURPOSE_KEY to this activity");
                break;
        }

        setResult(0);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mScannerView.stopCamera();
            mScannerView.startCamera();
        }
    }
}