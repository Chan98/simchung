package com.example.hahahaha1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MakeActivity extends AppCompatActivity {
 //   TextToSpeech tts;
    private BluetoothSPP bt;

    Button remake;
    ToggleButton play_m;
    ToggleButton record_m;
    Button print;

    //녹음을 위한 장치, 재생도 포함
    MediaRecorder recorder;
    String filename;
    MediaPlayer player;
    File file;

    //날짜
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss");
    String time;
    String QR_String;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make);

//        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != TextToSpeech.ERROR) {
//                    tts.setLanguage(Locale.KOREAN);
//                }
//            }
//        });
        bt = new BluetoothSPP(this); //Initializing
        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "블루투스 사용 불가"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MakeActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , name + "이 연결되었습니다."
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "연결해제", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "연결실패, 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
//        print = findViewById(R.id.print); //연결시도
//        print.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
//                    bt.disconnect();
//                } else {
//                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
//                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
//                }
//                bt.send("Text", true);
//            }
//        });

        remake = (Button) findViewById(R.id.remake);
        //play_m = (ToggleButton) findViewById(R.id.play_m);
        record_m = (ToggleButton) findViewById(R.id.record_m);
        print = (Button)findViewById(R.id.print);

        //play_m.setVisibility(View.INVISIBLE);
        remake.setVisibility(View.VISIBLE);
        record_m.setVisibility(View.VISIBLE);
        print.setVisibility(View.INVISIBLE);

        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.disconnect();
        } else {
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }

        //뒤로 돌아가기 위한 버튼
        remake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("check", "make");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        //녹음을 위한 버튼
        record_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼 활성화/비활성화
                if(record_m.isChecked()){//걍 xml로 글자 보여줘?아냐아냐 안돼
                    record_m.setBackgroundDrawable(getResources().getDrawable(R.drawable.stopicon));
                    //tts.speak("녹음이 시작됩니다.", TextToSpeech.QUEUE_FLUSH, null);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            time = getTime();
                            File sdcard = Environment.getExternalStorageDirectory();
                            //file = new File(sdcard, "*SimCheong-" + mFormat.format(mDate) + ".mp4");//내 생각엔 recorded.mp4라고만 해도 될 듯 어차피 새로 생기는 애니까
                            file = new File(sdcard, "*SimCheong-" + time + ".mp3") ;
                            QR_String = "*SimCheong-" + time + ".mp3";
                            filename = file.getAbsolutePath();
                            Log.d("D", "------------------" + filename + file.getAbsolutePath());
                            recordAudio();
                        }
                    }, 500);
//                    record_m.setBackgroundDrawable(getResources().getDrawable(R.drawable.stopicon));
//                    time = getTime();
//                    File sdcard = Environment.getExternalStorageDirectory();
//                    //file = new File(sdcard, "*SimCheong-" + mFormat.format(mDate) + ".mp4");//내 생각엔 recorded.mp4라고만 해도 될 듯 어차피 새로 생기는 애니까
//                    file = new File(sdcard, "*SimCheong-" + time + ".mp3") ;
//                    QR_String = "*SimCheong-" + time + ".mp3";
//                    filename = file.getAbsolutePath();
//                    Log.d("D", "------------------" + filename + file.getAbsolutePath());
//                    recordAudio();

                }
                else{
                    record_m.setBackgroundDrawable(getResources().getDrawable(R.drawable.recordicon));
                    stopRecording();
                    //tts.speak("녹음이 중지되었습니다.", TextToSpeech.QUEUE_FLUSH, null);//확인해주세요 tts
                    print.setVisibility(View.VISIBLE);
                    //play_m.setVisibility(View.VISIBLE);
                    playAudio();
                }
            }
        });
//        //재생하기 위한 버튼
//        play_m.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(play_m.isChecked()){
//                    playAudio();
//                }
//                else{
//                    stopAudio();
//                    //tts로 뒤로가기, 녹음하기, 출력하기 버튼
//                }
//                print.setVisibility(View.VISIBLE);
////                play_m.setVisibility(View.INVISIBLE);
////                record_m.setVisibility(View.VISIBLE);
//                //위 버튼들은 play_m버튼이 재생하고 중지하거나 끝났을 경우 나오도록 만드는 법 강구
//            }
//        });
//        print.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
//                    bt.disconnect();
//                } else {
//                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
//                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
//                }
//                generateQRcode(QR_String);
//                byte[] byteArray = bitmapToByteArray(bitmap);
//                bt.send("text", true);
//                try{
//                    String str = new String(byteArray, "ASCII");
//                    Log.d("D", "=========================바이트 어레이" + str);
//                }catch (UnsupportedEncodingException e){
//                    e.printStackTrace();
//                }
//
//            }
//        });
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

            //Toast.makeText(this, "녹음 종료", Toast.LENGTH_LONG).show();
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
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {
        Button btnSend = findViewById(R.id.print); //데이터 전송
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                generateQRcode(QR_String);
                byte[] byteArray = bitmapToByteArray(bitmap);
//                try{
//                    String string = new String(byteArray, "UTF-8");
//                    bt.send(string, true);
//                }catch (UnsupportedEncodingException e){
//                    e.printStackTrace();
//                }

                //bt.send(byteArrayToHexString(byteArray), true);
                bt.send(byteArray.toString(), true);
                //bt.send("Text", true);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "블루투스 사용 불가"
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void generateQRcode(String contents){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try{
            bitmap = toBitmap(qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, 200, 200));
            //((ImageView)findViewById(R.id.iv_generated_qrcode)).setImageBitmap(bitmap);
        }catch(WriterException e){
            e.printStackTrace();
        }
    }
    public static Bitmap toBitmap(BitMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for(int x = 0; x < height; x++){
            for(int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }

        }
        return bmp;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return byteArray;
    }

    public static String byteArrayToHexString(byte[] bytes){

        StringBuilder sb = new StringBuilder();

        for(byte b : bytes){

            sb.append(String.format("%02X", b&0xff));
        }

        return sb.toString();
    }
}
