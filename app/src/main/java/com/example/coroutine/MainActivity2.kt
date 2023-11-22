package com.example.coroutine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.coroutine.databinding.ActivityMain2Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding;

    val TAG = "확인용"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(layoutInflater);
        setContentView(binding.root);

//        GlobalScope.launch(Dispatchers.IO) {
//            val time = measureTimeMillis {
//                val answer1 = networkCall();
//                val answer2 = networkCall2();
//                Log.d(TAG, "Answer1 is $answer1 ");
//                Log.d(TAG, "Answer1 is $answer2 ");
//            }
//            Log.d(TAG, "$time ms. ") // 6초
//        };



//        GlobalScope.launch(Dispatchers.IO) {
//            val time = measureTimeMillis {
//                var answer1: String? = null
//                var answer2: String? = null
//                val job1 = launch { answer1 = networkCall() };
//                val job2 = launch { answer2 = networkCall2() };
//                job1.join();
//                job2.join();
//                // job1과 job2가 동시에 실행
//                Log.d(TAG, "Answer1 is $answer1");
//                Log.d(TAG, "Answer2 is $answer2");
//            };
//            Log.d(TAG, "Requests took $time ms."); // 3초
//        };


        GlobalScope.launch(Dispatchers.IO) {
            // Async는 새로운 coroutine을 시작하고 GlobalScope.launch과 비슷하지만
            // GlobalScope.launch처럼 job(백그라운드 작업)을 리턴하지않고 Deferred를 리턴
            val time = measureTimeMillis {
                val answer1 = async { networkCall() };
                val answer2 = async { networkCall2() };
                // Async를 사용했기때문에 deferred값을 리턴
                // deferred값을 리턴할 때는 await을 사용해야함
                // await은 스레드를 방해하지 않고 deferred값이 계산될 때까지, 기다리게 하는 함수
                Log.d(TAG, "Answer1 is ${answer1.await()}");
                Log.d(TAG, "Answer2 is ${answer2.await()}");
            };
            Log.d(TAG, "Request took $time ms.");
        };


//        binding.btnClick.setOnClickListener {
//            GlobalScope.launch {
//                while (true){ //button눌린게 true면 still running로그 반복 프린트
//                    delay(1000L); //1초 간격으로 printing
//                    Log.d(TAG,"Still running");
//                };
//            }
//            GlobalScope.launch {
//                delay(5000L) //5초후 새로운 intent시작
//                Intent(this@MainActivity2, MainActivity::class.java).also{
//                    startActivity(it);
//                    finish();
//                };
//            };
        // 새로운 인텐트가 열린 후에도 while문이 멈추지않음
//        };


        binding.btnClick.setOnClickListener {
            lifecycleScope.launch {
                while (true){ //button눌린게 true면 still running로그 반복 프린트
                    delay(1000L); //1초 간격으로 printing
                    Log.d(TAG,"Still running");
                };
            };
            GlobalScope.launch {
                delay(5000L) //5초후 intent시작
                Intent(this@MainActivity2, MainActivity::class.java).also{
                    startActivity(it);
                    finish();
                };
            };
            // Intent로 activity가 바뀌는 시점인 5초후 while문이 멈춤
        };
    };

    suspend fun networkCall(): String {
        delay(3000L);
        return "Answer 1";
    };

    suspend fun networkCall2(): String {
        delay(3000L);
        return "Answer 2";
    };
};

