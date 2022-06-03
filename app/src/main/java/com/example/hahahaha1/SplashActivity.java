package com.example.hahahaha1;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        tts.speak("위의 버튼은 큐알 생성, 아래 버튼은 큐알 스캔버튼입니다", TextToSpeech.QUEUE_FLUSH, null);
        finish();
    }
}
