package com.example.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class MainActivity : AppCompatActivity() {

    val TAG = "확인용"

    private lateinit var binding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        // IO의 경우 대기시간이 있는 네트워크 입출력 등의 작업에 적합
        // IO Dispathcher는 필요에 따라 추가적으로 스레드를 더 생성하거나 줄일 수 있으며 최대 64개까지 생성이 가능
        // Default Dispatcher와 스레드를 공유하기 때문에 switching으로 인한 오버헤드를 일으키지 않는다

        // Default의 경우 대기시간이 없고 지속적으로 CPU의 작업을 필요로 하는 무거운 작업에 적합
        // 해당 Dispatcher는 JVM의 공유 스레드풀을 사용하고 동시 작업 가능한 최대 갯수는 CPU의 코어 수와 같다(최소 2개)
        // 연속적인 CPU 연산작업을 수행하는 경우 Default를 사용하는 것이 더 적합

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
            withContext(Dispatchers.Main) {
                Log.d(TAG, "test: ")
                answer;
            }
        }
    };

    suspend fun doNetworkCall(): String {
        delay(3000L);
        return "answer";
    };

    fun test2() {
        runBlocking {
            Log.d(TAG, "test3: 1");
            delay(1000L);
            Log.d(TAG, "test3: 2");
            Log.d(TAG, "test3: 3");
        };
    };

    fun test3() {
        runBlocking {
            launch(Dispatchers.IO) {
                delay(3000L);
                Log.d(TAG, "test4: 1");
            };
            launch(Dispatchers.IO) {
                delay(3000L);
                Log.d(TAG, "test4: 2");
            };
        };
    };

    fun test4() {

        val job = GlobalScope.launch(Dispatchers.Default) {
            repeat(5) {
                Log.d(TAG, "test5: 1");
                delay(1000L);
            };
        };

        runBlocking {
            job.join(); // join이 끝날때까지 runblocking이 지속, suspend에서만 작동하기때문에 runblocking안에 넣어줌
            Log.d(TAG, "test5: 1");
        };
        Log.d(TAG, "test5:2 ");

        runBlocking {
            delay(3000L);
            job.cancel();// 3초후 job을 멈추게함
            Log.d(TAG, "test6: 1");
        };
    };

    fun test5() {
        val job = GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG, "test7: 1");
            for (i in 30..40) {
                Log.d(TAG, "Result for i = $i : ${fib(i)}");
            };
            Log.d(TAG, "test7: 2");
        };
        runBlocking {
            // globalScope이 default이기 때문에 runblocking에 delay가 들어가도 멈추지 않음
            delay(2000L);
            job.cancel();
            //  coroutine안에 계산하는데 복잡하고 오래 걸리는 작업이 있다면 그걸 처리하느라
            //  cancel이 되었다는 정보를 corutine의 계산이 끝날 때까지 못 들을 수 있음
            Log.d(TAG, "test7: 3");
        };
    };

    fun fib(n: Int): Long { // 계산이 오래 걸리게 하도록 만든 함수
        return if (n == 0) 0;
        else if (n == 1) 1;
        else fib(n - 1) + fib(n - 2);
    };

    fun test6() {
        val job = GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG, "test8: 1");
            withTimeout(3000L) {
                // withTimeout은 안에 있는 함수가 정해진 시간보다 오래 걸릴 경우 cancel 시키는 함수
                // withTimeout이 설정되었기 때문에 아래 있는 for Loop가 3초 이상 걸릴 경우 취소
                for (i in 30..40) {
                    if (isActive) {
                        Log.d(TAG, "Result for i = $i : ${fib(i)}");
                    };
                };
            };
            Log.d(TAG, "test8: 2");
        };

        runBlocking {
          delay(2000L);
          job.cancel();
            Log.d(TAG, "test8: 3");
        };
    };
};
