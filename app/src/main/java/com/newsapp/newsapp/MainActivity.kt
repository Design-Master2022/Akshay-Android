package com.newsapp.newsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.newsapp.newsapp.databinding.ActivityMainBinding
import com.newsapp.newsapp.databinding.ActivityProfileBinding
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.home.ProfileActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivAdd.setOnClickListener {  startActivity(Intent(this, ProfileActivity::class.java)) }
    }
}