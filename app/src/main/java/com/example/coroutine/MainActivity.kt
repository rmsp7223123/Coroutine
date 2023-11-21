package com.example.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    val TAG = "확인용"

    private lateinit var binding : ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        GlobalScope.launch {
            delay(3000L)//3초
            Log.d(TAG, "onCreate1:${Thread.currentThread().name} ");
        };
        Log.d(TAG, "onCreate2:${Thread.currentThread().name} ");
    }

    fun test() {
      GlobalScope.launch(Dispatchers.IO) {// 네트워크에서 불러올 데이터
          Log.d(TAG, "test: 1");
          val answer = doNetworkCall();
          withContext(Dispatchers.Main){
              Log.d(TAG, "test: ")
              answer;
          }
      }
    };

    suspend fun doNetworkCall():String{
        delay(3000L);
        return "answer";
    };
}