package com.example.hahahaha1;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

public class ScanActivity extends AppCompatActivity {
    TextToSpeech tts;

    Button rescan;
    TextView textView;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        //나중에 사용을 위해 이름 찾아놓기
        textView = (TextView)findViewById(R.id.textView);
        rescan = (Button)findViewById(R.id.rescan);
        //뒤로가기
        rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("check", "scan");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        //화면에 큐알코드 붙이기
        qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("QR 코드를 스캔해주세요.");
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String check;

        if(result != null){
            if(result.getContents() == null){//큐알코드 안에 내용이 없다면 전 화면으로 돌아감
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
                tts.speak("스캔이 취소되었습니다. 처음화면으로 돌아갑니다.", TextToSpeech.QUEUE_FLUSH, null);
            }
            else{
                try{
                    boolean isMatch = Patterns.WEB_URL.matcher(result.getContents()).matches();
                    if(result.getContents().contains("*SimCheong-")){
                        //Log.d("D", "=========" + result.getContents());
                        check = check_QR(result.getContents());

                        if(check.equals("YES")){
                            Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                            intent.putExtra("QR", result.getContents());
                            startActivityForResult(intent, 104);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                            //tts.speak("스캔이 완료되었습니다 녹음파일을 재생합니다", TextToSpeech.QUEUE_FLUSH, null);
                            //Toast.makeText(getApplicationContext(), "스캔이 완료되었습니다", Toast.LENGTH_LONG).show();
                        }
                        else if(check.equals("NO")){
                            Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                            intent.putExtra("QR", result.getContents());
                            Log.d("d", "-----------------" + result.getContents().toString());
                            startActivityForResult(intent, 103);
                            setResult(Activity.RESULT_OK);
                            finish();
                            //tts.speak("스캔이 완료되었습니다", TextToSpeech.QUEUE_FLUSH, null);
                            Toast.makeText(getApplicationContext(), "스캔이 완료되었습니다", Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(isMatch){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                        startActivity(intent);
                        //tts.speak("스캔이 완료되었습니다. URL 주소로 화면을 이동합니다.", TextToSpeech.QUEUE_FLUSH, null);
                        Toast.makeText(getApplicationContext(), "스캔이 완료되었습니다. URL 주소로 화면을 이동합니다.", Toast.LENGTH_LONG).show();
                        Intent intent1 = new Intent();
                        setResult(Activity.RESULT_OK, intent1);
                        finish();
                    }
                    else{
                        String text = new String(result.getContents());
                        textView.setText(text);
                        tts.speak("스캔이 완료되었습니다.,   " + text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public String check_QR(String result){
//        File sdcard = Environment.getExternalStorageDirectory();
//        File file = new File(sdcard, result);
//        String filename = file.getAbsolutePath();
        String check = "";
        File file = new File("/storage/emulated/0/" + result);
        //Log.d("D", "==================" + file.getAbsolutePath());
        if(file.exists()){
            check = "YES";
        }
        else{
            check = "NO";
        }
        //Log.d("D", "============================" + file.getAbsolutePath() +"/" + check);
        return check;
    }
}
