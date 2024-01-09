package com.example.xevivuapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.xevivuapp.databinding.ActivityPermissionBinding

const val REQUEST_CODE = 300

class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(Intent(this@PermissionActivity, HomeActivity::class.java))
        }

        val guideContent1 = getColoredSpanned(
            "Để có thể sử dụng những tính năng của ứng dụng, bạn vui lòng ",
            "#979EB6"
        )
        val guideContent2 = getColoredSpanned(" Cấp quyền truy cập vị trí ", "#1152FD")
        val guideContent3 = getColoredSpanned("cho ứng dụng bằng cách ấn vào tùy chọn ", "#979EB6")
        val guideContent4 = getColoredSpanned("Cho phép Trong khi dùng ứng dụng ", "#1152FD")
        val guideContent5 = getColoredSpanned("nhé !", "#979EB6")
        binding.guideContent.text = Html.fromHtml(
            guideContent1 + guideContent2 + guideContent3 + guideContent4 + guideContent5,
            FROM_HTML_MODE_LEGACY
        )

        binding.permissionButton.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE
            )
        }

    }

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@PermissionActivity,
                        "Chào mừng bạn đến với ứng dụng Xe Vivu",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@PermissionActivity, HomeActivity::class.java))
                } else {
                    Toast.makeText(
                        this@PermissionActivity,
                        "Bạn hãy cấp quyền truy cập Vị trí để sử dụng các tính năng của ứng dụng nhé!",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@PermissionActivity, HomeActivity::class.java))
                }
            }
        }
    }
}