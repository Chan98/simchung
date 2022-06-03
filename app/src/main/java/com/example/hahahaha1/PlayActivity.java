package com.example.hahahaha1;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.util.Locale;

public class PlayActivity extends AppCompatActivity {
    TextToSpeech tts;

    Button replay;
    ToggleButton play_p;
    ToggleButton record_p;
    String QR;

    //녹음을 위한 장치, 재생도 포함
    MediaRecorder recorder;
    String filename;
    MediaPlayer player;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        replay = (Button) findViewById(R.id.replay);
        play_p = (ToggleButton) findViewById(R.id.play_p);
        record_p = (ToggleButton) findViewById(R.id.record_p);

        play_p.setVisibility(View.INVISIBLE);
        record_p.setVisibility(View.VISIBLE);

        QR = getIntent().getStringExtra("QR");
        File sdcard = Environment.getExternalStorageDirectory();
        file = new File(sdcard, QR) ;
        filename = file.getAbsolutePath();
        //playAudio();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playAudio();
            }
        }, 2000);

        //녹음을 위한 버튼
        record_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼 활성화/비활성화
                if(record_p.isChecked()){//걍 xml로 글자 보여줘?아냐아냐 안돼
                    tts.speak("녹음이 시작됩니다.", TextToSpeech.QUEUE_FLUSH, null);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            QR = getIntent().getStringExtra("QR");
                            File sdcard = Environment.getExternalStorageDirectory();
                            file = new File(sdcard, QR) ;
                            filename = file.getAbsolutePath();
                            recordAudio();
                        }
                    }, 1500);

                }
                else{
                    stopRecording();
                    tts.speak("녹음이 중지되었습니다.", TextToSpeech.QUEUE_FLUSH, null);//확인해주세요 tts
                    record_p.setVisibility(View.INVISIBLE);
                    play_p.setVisibility(View.VISIBLE);
                }
            }
        });
        //재생하기 위한 버튼
        play_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play_p.isChecked()){
                    playAudio();
                }
                else{
                    stopAudio();
                    //tts로 뒤로가기, 녹음하기, 출력하기 버튼
                }
//                save.setVisibility(View.VISIBLE);
//                play_m.setVisibility(View.INVISIBLE);
//                record_m.setVisibility(View.VISIBLE);
                //위 버튼들은 play_m버튼이 재생하고 중지하거나 끝났을 경우 나오도록 만드는 법 강구
            }
        });
    }

    //녹음
    public void recordAudio(){
        recorder = new MediaRecorder();
        //녹음하기 위한 전처리
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        recorder.setOutputFile(filename);
        try{
            recorder.prepare();
            recorder.start();
            //Toast.makeText(this, "녹음 시작", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //녹음 중지
    public void stopRecording(){
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;

            //Toast.makeText(this, "녹음 중지", Toast.LENGTH_LONG).show();
        }
    }
    //재생
    public void playAudio(){
        try{
            closePlayer();

            player = new MediaPlayer();
            player.setDataSource(filename);
            Log.d("D", "플레이 들어옴 파일네임 "  + filename);
            player.prepare();
            Log.d("D", "프리페어 다음 " );
            player.start();
            Log.d("D", "스타트 다음 " );
            //Toast.makeText(this, "재생 시작", Toast.LENGTH_LONG).show();
            Log.d("D", "플레이 들어옴 파일네임 "  + file.exists());

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //재생 중지
    public void stopAudio(){
        if(player != null && player.isPlaying()){
            player.stop();

            //Toast.makeText(this, "재생 중지", Toast.LENGTH_LONG).show();
        }
    }
    //객체 비우기인듯
    public void closePlayer(){
        if(player != null){
            player.release();
            player = null;
        }
    }
}
