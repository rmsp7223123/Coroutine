package com.example.coroutine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.coroutine.databinding.ActivityMain2Binding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
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
                while (true) { //button눌린게 true면 still running로그 반복 프린트
                    delay(1000L); //1초 간격으로 printing
                    Log.d(TAG, "Still running");
                };
            };
            GlobalScope.launch {
                delay(5000L) //5초후 intent시작
                Intent(this@MainActivity2, MainActivity::class.java).also {
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

    fun test() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception"); // 예외처리
        };

        val job = GlobalScope.launch(exceptionHandler) {
            launch { // 새로운 코루틴을 하나 더 시작
                delay(100);
                throw ArithmeticException("Divide by zero"); // ArithmeticException 발생
            };
        };

        runBlocking {
            job.join(); // job 코루틴이 완료될 때까지 기다림
        };
        // CoroutineExceptionHandler에 의해 처리되어 해당 예외가 발생했음을 알려주고, "Caught" 메시지와 함께 예외 내용이 출력
        // 이후 runBlocking 블록에서 job.join()을 통해 외부 코루틴이 종료
    };

    suspend fun doTask1(): Int {
        delay(1000);
        return 42;
    };

    suspend fun doTask2(): String {
        delay(1500);
        return "Task completed";
    };

    fun test2() = runBlocking {
        val deferred1 = async { doTask1() };
        val deferred2 = async { doTask2() };

        // async 함수를 사용하여 두 가지 다른 작업(doTask1 및 doTask2)을 동시에 시작
        // 각각의 작업은 Deferred를 반환
        // Deferred는 비동기 계산의 결과
        // await를 사용하여 결과를 기다릴 수 있음

        println("Result 1: ${deferred1.await()}");
        println("Result 2: ${deferred2.await()}")  ;
    };

    fun test3() {
        val job = GlobalScope.launch(Dispatchers.Default) {
            println("Coroutine running on ${Thread.currentThread().name}");
        };

        runBlocking {
            job.join();
            // runBlocking 블록 내에서 job.join()이 호출되어 해당 코루틴 job이 완료될 때까지 메인 스레드가 차단
            // join()은 해당 코루틴이 완료될 때까지 대기하고, 여기서는 해당 코루틴이 지정된 Dispatchers.Default에서 실행된 후에 완료
        };

        // 결과적으로 runBlocking 블록 내에서 job.join()이 실행되면 메인 스레드가 해당 코루틴이 완료될 때까지 대기
        // 해당 코루틴은 백그라운드의 디폴트 디스패처에서 실행되며, 이 디스패처에 할당된 스레드에서 동작
    };

    fun simpleFlow(): Flow<Int> = flow {
        // Flow는 Kotlin의 코루틴을 기반으로 하는 비동기적인 스트림 처리를 지원하는 라이브러리
        for (i in 1..5) {
            kotlinx.coroutines.delay(1000); // 1초마다 값 발생
            emit(i); // 값을 방출
        };
    };

    fun main() = runBlocking<Unit> {
        val flow = simpleFlow();

        launch {
            flow.collect { value -> //  Flow에서 값을 수집
                println(value); // 수집된 값 출력
            };
        };

        println("Collecting values...");
    };

    fun simpleFlow2(): Flow<Int> = flow {
        for (i in 1..5) {
            kotlinx.coroutines.delay(1000);
            emit(i);
        };
    };

    fun main2() = runBlocking<Unit> {
        val flow = simpleFlow();

        // flow.map, flow.filter 등의 연산자를 사용하여 Flow에서 값을 변형하고 조작
        flow.map { it * 2 } // 각 값에 * 2 연산
            .filter { it % 3 == 0 } // 3의 배수인 값만 필터링
            .collect { value ->
                println(value);
            };
    };

    suspend fun performTask(taskName: String): Int {
        delay(1000);
        println("$taskName 완료");
        return taskName.length;
    };

    fun main3() = runBlocking {
        val job = launch {
            val result = supervisorScope {
                val deferredList = mutableListOf<Deferred<Int>>();

                for (i in 1..5) {
                    val deferred = async { performTask("Task $i"); };
                    deferredList.add(deferred);
                };

                // 예외가 발생하더라도 계속 진행하기 위해 awaitAll 대신 Deferred들을 수동으로 기다림
                val results = mutableListOf<Int>();
                for (deferred in deferredList) {
                    try {
                        results.add(deferred.await());
                    } catch (e: Exception) {
                        println("작업 실패: ${e.message}");
                    };
                };
                results;
            };

            println("작업 결과: $result");
        };

        delay(2500); // 일정 시간이 지난 후 작업 취소
        job.cancelAndJoin(); // 작업 취소
    };
};

