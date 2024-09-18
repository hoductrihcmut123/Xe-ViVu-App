package com.example.xevivuapp.common.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng

object Utils {
    fun Context.isCheckLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun decodePolyLine(poly: String): List<LatLng> {
        val len = poly.length
        var index = 0
        val decoded: MutableList<LatLng> = ArrayList()
        var lat = 0
        var lng = 0
        while (index < len) {
            var shift = 0
            var result = 0
            var b: Int
            do {
                b = poly[index++].code - 63
                result = result or (b and 31 shl shift)
                shift += 5
            } while (b >= 32)
            val dLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dLat
            shift = 0
            result = 0
            do {
                b = poly[index++].code - 63
                result = result or (b and 31 shl shift)
                shift += 5
            } while (b >= 32)
            val dLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dLng
            decoded.add(LatLng(lat.toDouble() / 100000.0, lng.toDouble() / 100000.0))
        }
        return decoded
    }
}