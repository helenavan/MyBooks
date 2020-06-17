package com.helenacorp.android.mybibliotheque.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

const val MY_PERMISSION_REQUEST_FINE_LOCATION = 123
const val MY_PERMISSION_REQUEST_INTERNET = 124
const val MY_PERMISSION_REQUEST_BACKGROUNDLOCATION = 125
const val MY_PERMISSION_REQUEST_READ_EXTENAL_STORAGE = 126

fun isPermissionReadFile(act: Activity): Boolean {
    var PERMISSION_READ_EXTERNAL_STORAGE = false
    val Tag = "PermissionManager"
    if (PERMISSION_READ_EXTERNAL_STORAGE) {
        return PERMISSION_READ_EXTERNAL_STORAGE
    }
    if (ActivityCompat.checkSelfPermission(
                    act,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
    ) {

        Log.d(Tag, "PERMISSION_ACCESS_FINE_LOCATION not granted")
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                        act,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
        ) {
            Log.d(Tag, "LOCATION not granted -> show explanation")
            AlertDialog.Builder(act)
                    .setTitle("Autorisation")
                    .setMessage("READ external Storage")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(
                            android.R.string.ok,
                            DialogInterface.OnClickListener { arg0, arg1 ->
                                ActivityCompat.requestPermissions(
                                        act,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                        MY_PERMISSION_REQUEST_READ_EXTENAL_STORAGE
                                )
                                // startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }).create().show()
        } else {
            // No explanation needed, we can request the permission.
            Log.d(
                    "checkLocPermissions",
                    "PERMISSION_READ_EXTERNAL_STORAGE not granted -> no explanation needed - req permission"
            )
            ActivityCompat.requestPermissions(
                    act,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSION_REQUEST_READ_EXTENAL_STORAGE
            )
        }
    } else {
        //Log.e("checkLocPermissions","WRITE_EXTERNAL_STORAGE granted ");
        PERMISSION_READ_EXTERNAL_STORAGE = true
    }

    return PERMISSION_READ_EXTERNAL_STORAGE
}

fun isPermissionLocation(act: Activity): Boolean {

    var PERMISSION_ACCESS_FINE_LOCATION_GRANTED = false
    val Tag = "PermissionManager"

    if (PERMISSION_ACCESS_FINE_LOCATION_GRANTED) {
        return PERMISSION_ACCESS_FINE_LOCATION_GRANTED
    }

    Log.d(Tag, "isPermissionWriteExtStorageGranted called")

    //CHECK ACCESS_FINE_LOCATION
    //check API - Beginning in Android 6.0 (API level 23)

    if (ActivityCompat.checkSelfPermission(
                    act,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
    ) {

        Log.d(Tag, "PERMISSION_ACCESS_FINE_LOCATION not granted")
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                        act,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
        ) {
            Log.d(Tag, "LOCATION not granted -> show explanation")
            AlertDialog.Builder(act)
                    .setTitle("Autorisation")
                    .setMessage("Localisation")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(
                            android.R.string.ok,
                            DialogInterface.OnClickListener { arg0, arg1 ->
                                ActivityCompat.requestPermissions(
                                        act,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                        MY_PERMISSION_REQUEST_FINE_LOCATION
                                )
                                // startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }).create().show()
        } else {
            // No explanation needed, we can request the permission.
            Log.d(
                    "checkLocPermissions",
                    "PERMISSION_ACCESS_FINE_LOCATION not granted -> no explanation needed - req permission"
            )
            ActivityCompat.requestPermissions(
                    act,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_REQUEST_FINE_LOCATION
            )
        }
    } else {
        //Log.e("checkLocPermissions","WRITE_EXTERNAL_STORAGE granted ");
        PERMISSION_ACCESS_FINE_LOCATION_GRANTED = true
    }

    return PERMISSION_ACCESS_FINE_LOCATION_GRANTED
}

fun isPermissionInternet(act: Activity): Boolean {

    var PERMISSION_ACCESS_INTERNET_LOCATION_GRANTED = false
    val Tag = "PermissionManager"

    if (PERMISSION_ACCESS_INTERNET_LOCATION_GRANTED) {
        return PERMISSION_ACCESS_INTERNET_LOCATION_GRANTED
    }

    Log.d(Tag, "isPermissionWriteExtStorageGranted called")

    //CHECK ACCESS_FINE_LOCATION
    //check API - Beginning in Android 6.0 (API level 23)

    if (ActivityCompat.checkSelfPermission(
                    act,
                    Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
    ) {

        Log.d(Tag, "PERMISSION_ACCESS_INTERNET_LOCATION not granted")
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                        act,
                        Manifest.permission.INTERNET
                )
        ) {
            Log.d(Tag, "LOCATION not granted -> show explanation")
            AlertDialog.Builder(act)
                    .setTitle("Autorisation")
                    .setMessage("Localisation")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(
                            android.R.string.ok,
                            DialogInterface.OnClickListener { arg0, arg1 ->
                                ActivityCompat.requestPermissions(
                                        act,
                                        arrayOf(Manifest.permission.INTERNET),
                                        MY_PERMISSION_REQUEST_INTERNET
                                )
                                // startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }).create().show()
        } else {
            // No explanation needed, we can request the permission.
            Log.d(
                    "checkLocPermissions",
                    "PERMISSION_ACCESS_INTERNET_LOCATION not granted -> no explanation needed - req permission"
            )
            ActivityCompat.requestPermissions(
                    act,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_REQUEST_FINE_LOCATION
            )
        }
    } else {
        //Log.e("checkLocPermissions","WRITE_EXTERNAL_STORAGE granted ");
        PERMISSION_ACCESS_INTERNET_LOCATION_GRANTED = true
    }

    return PERMISSION_ACCESS_INTERNET_LOCATION_GRANTED
}

