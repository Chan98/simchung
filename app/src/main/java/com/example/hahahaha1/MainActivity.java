package com.example.hahahaha1;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
//    TextToSpeech tts;
    Button make;
    Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //버튼 아이디 가져오기
        make = (Button)findViewById(R.id.make);
        scan = (Button)findViewById(R.id.scan);
        //tts 세팅
//        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR){
//                    tts.setLanguage(Locale.KOREAN);
//                }
//            }
//        });

        //생성화면으로 가는 버튼 누르면, 생성화면으로 이동
        make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MakeActivity.class);
                startActivityForResult(intent, 101);
            }
        });
        //스캔화면으로 가는 버튼 누르면, 스캔화면으로 이동
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivityForResult(intent, 102);
                //tts.speak("화면에 큐알코드를 비춰주세요.", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //생성화면
        if(requestCode == 101){
            String check = data.getStringExtra("check");
            //Toast.makeText(getApplicationContext(), "생성화면으로부터 응답 : " + check, Toast.LENGTH_LONG).show();
        }
        //스캔화면
        if(requestCode == 102){
            String check = data.getStringExtra("check");
            //Toast.makeText(getApplicationContext(), "스캔화면으로부터 응답 : " + check, Toast.LENGTH_LONG).show();
        }
        //녹음화면
        if(requestCode == 103){
            String check = data.getStringExtra("check");
            //Toast.makeText(getApplicationContext(), "녹음화면으로부터 응답 : " + check, Toast.LENGTH_LONG).show();
        }
    }
    //tts 디스토리
//    @Override
//    protected  void onDestroy(){
//        super.onDestroy();
//        if(tts != null){
//            tts.stop();
//            tts.shutdown();
//        }
//    }
}
