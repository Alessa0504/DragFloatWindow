package com.example.dragfloatwindowdemo

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dragfloatwindowdemo.databinding.ActivityMainBinding
import com.example.dragfloatwindowdemo.util.ScreenUntil

class MainActivity : AppCompatActivity() {
    private lateinit var floatView: View
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        floatView = layoutInflater.inflate(R.layout.layout_float_view, null, false)
        imageView = floatView.findViewById<ImageView>(R.id.iv_image)
        imageView.setOnClickListener {
            Toast.makeText(this@MainActivity, "点击了一下", Toast.LENGTH_SHORT).show()
        }
        binding.floatView.setView(
            floatView,
            ScreenUntil.widthPixels - ScreenUntil.dip2px(this@MainActivity, 100f),
            ScreenUntil.heightPixels - ScreenUntil.dip2px(this@MainActivity, 400f)
        )
    }
}