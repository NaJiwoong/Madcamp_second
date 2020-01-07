package com.example.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Room extends Activity implements View.OnClickListener {

    String connectUrl = "http://192.168.0.100:3001";
    int mcompare=0;
    int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private ca.uol.aig.fftpack.RealDoubleFFT transformer;
    int blockSize = 256;

    Button startStopButton;
    boolean started = false;
    boolean isMyTurn = false;

    RecordAudio recordTask;

    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    private double sum=0;
    private double myHz=-1;
    private int cnt=0;

    Socket mSocket;
    String username="";

    private Double threshold=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        try
        {
            mSocket= IO.socket(connectUrl);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mSocket.on(Socket.EVENT_CONNECT, (Object... objects) ->{
                    SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                    mSocket.emit("enter", pref.getString("id","")+"/"+pref.getString("myRoom",""));
                });

        mSocket.on("newUser",(Object... objects) ->{
            String text= objects[0].toString();
            //Toast.makeText(getApplicationContext(), "0000000000", Toast.LENGTH_SHORT).show();
            Log.i("socketsocket",text);
              });

        mSocket.on("myMsg", (Object... objects) ->{
            Log.i("myMsg","sdvdavsadvsadvsadvsadv");
        });

        mSocket.on("newMsg", (Object... objects) ->{
            String text= objects[0].toString();
            /*String text= objects[0].toString();
            if(text.equals("start"))
                started=false;*/
            Log.i("mmmmmmmmmmmmmmmmmmmmm",text);

            runOnUiThread(()->{
                threshold = Double.parseDouble(Double.toString((Math.round(Double.parseDouble(text)))));
                Toast.makeText(getApplicationContext(), "You should be higher than " + Double.toString(Math.round(Double.parseDouble(text))) + "Hz", Toast.LENGTH_LONG).show();
            });

        });

        mSocket.on("start", (Object... objects) ->{
            Log.i("startstartstartstart","sssssssssssssssssssssss");
            runOnUiThread(()->{
            startStopButton.setEnabled(true);
                isMyTurn=true;});
        });

        mSocket.on("victory", (Object... objects) ->{
           runOnUiThread(()->{
               String myrest = Double.toString(myHz);
               Toast.makeText(getApplicationContext(), "You win!!! Your score is " + myrest + "Hz!! ^ ^", Toast.LENGTH_LONG).show();
           }) ;
           SharedPreferences pref=getSharedPreferences("login",MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("victory", Integer.toString(Integer.parseInt(pref.getString("victory","0"))+1));
            edit.commit();
            String info_text=pref.getString("id","Anonymous")+
                    "/"+pref.getString("victory","1")
                    +"/"+pref.getString("defeat","0")
                    +"/"+pref.getString("highscore","0");
            mSocket.emit("pushinfo", info_text);
        });


//        mSocket.on("logout", onLogout);
        mSocket.connect();




        startStopButton = (Button) findViewById(R.id.record_button);
        if(isMyTurn)
            startStopButton.setEnabled(true);
        else
            startStopButton.setEnabled(false);
        startStopButton.setOnClickListener(this);

        transformer = new ca.uol.aig.fftpack.RealDoubleFFT(blockSize);

        // ImageView 및 관련 객체 설정 부분
        // 이미지뷰를 참조하고, 비트맵을 생성한다 비트맵의 첫번째 두번째 인수가 가로,세로의 크기이며
        // ARGB_8888가 나타내는 의미는 각 픽셀은 4바이트에 저장된다이다
        imageView = (ImageView) findViewById(R.id.record_image);
        bitmap = Bitmap.createBitmap((int)320, (int)200,
                Bitmap.Config.ARGB_8888);
        // 비트맵 이미지를 불러와 캔버스에 그림을 그림
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        // 비트맵 객체를 이용하여 이미지뷰를 보여줌
        imageView.setImageBitmap(bitmap);

    }

    private class RecordAudio extends AsyncTask<Void, double[], Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                // AudioRecord를 설정하고 사용한다.
                int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);

                AudioRecord audioRecord = new AudioRecord( MediaRecorder.AudioSource.MIC, frequency,
                        channelConfiguration, audioEncoding, bufferSize);

                // short로 이뤄진 배열인 buffer는 원시 PCM 샘플을 AudioRecord 객체에서 받는다.
                // double로 이뤄진 배열인 toTransform은 같은 데이터를 담지만 double 타입인데, FFT
                // 클래스에서는 double타입이 필요해서이다.
                short[] buffer = new short[blockSize]; //blockSize = 256
                double[] toTransform = new double[blockSize]; //blockSize = 256

                audioRecord.startRecording();

                while (started) {
                    int bufferReadResult = audioRecord.read(buffer, 0, blockSize); //blockSize = 256
                    Log.i("bufferReadResult", Integer.toString(bufferReadResult));
                    // AudioRecord 객체에서 데이터를 읽은 다음에는 short 타입의 변수들을 double 타입으로
                    // 바꾸는 루프를 처리한다.
                    // 직접 타입 변환(casting)으로 이 작업을 처리할 수 없다. 값들이 전체 범위가 아니라 -1.0에서
                    // 1.0 사이라서 그렇다
                    // short를 32,767(Short.MAX_VALUE) 으로 나누면 double로 타입이 바뀌는데,
                    // 이 값이 short의 최대값이기 때문이다.
                    for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                        toTransform[i] = (double) buffer[i] / Short.MAX_VALUE; // 부호 있는 16비트
                    }

                    // 이제 double값들의 배열을 FFT 객체로 넘겨준다. FFT 객체는 이 배열을 재사용하여 출력 값을 담는다
                    // 포함된 데이터는 시간 도메인이 아니라
                    // 주파수 도메인에 존재한다. 이 말은 배열의 첫 번째 요소가 시간상으로 첫 번째 샘플이 아니라는 얘기다.
                    // 배열의 첫 번째 요소는 첫 번째 주파수 집합의 레벨을 나타낸다.

                    // 256가지 값(범위)을 사용하고 있고 샘플 비율이 8,000 이므로 배열의 각 요소가 대략
                    // 15.625Hz를 담당하게 된다. 15.625라는 숫자는 샘플 비율을 반으로 나누고(캡쳐할 수 있는
                    // 최대 주파수는 샘플 비율의 반이다. <- 누가 그랬는데...), 다시 256으로 나누어 나온 것이다.
                    // 따라서 배열의 첫 번째 요소로 나타난 데이터는 영(0)과 15.625Hz 사이에
                    // 해당하는 오디오 레벨을 의미한다.

                    transformer.ft(toTransform);
                    // publishProgress를 호출하면 onProgressUpdate가 호출된다.
                    publishProgress(toTransform);
                }

                audioRecord.stop();
            } catch (Throwable t) {
                Log.e("AudioRecord", "Recording Failed");
            }

            return null;
        }
        @Override
        protected void onProgressUpdate(double[]... toTransform) {
            canvas.drawColor(Color.BLACK);

            double local_sum=0;
            double deci_sum=0;

            double local_max=0;
            int max_i=-1;

            for (int i = 0; i < toTransform[0].length; i++) {
                int x = i;
                int downy = (int) (100 - (toTransform[0][i] * 10));
                int upy = 100;
                int test1 = (int) (toTransform[0][i] * 100);

                if(test1 > 2000){
                    Log.e("주파수 : ",test1+"HZ");
                }

                if(test1>mcompare)
                    mcompare=test1;
                if(local_max<Math.abs(toTransform[0][i]))
                {
                    local_max=Math.abs(toTransform[0][i]);
                    max_i=i;
                }

                /*local_sum+=(Math.abs(toTransform[0][i]) * i*15.625);
                deci_sum+=(Math.abs(toTransform[0][i]));*/
                canvas.drawLine(x, downy, x, upy, paint);
            }
            Log.i("local : ",local_sum+"HZ");
            Log.i("deci : ",deci_sum+"HZ");
//            local_sum=local_sum/toTransform[0].length;
            /*if (deci_sum == 0){
                local_sum = 0;
            }else {
                local_sum = local_sum / deci_sum;
            }
            sum+=local_sum;*/

            sum+=((double)max_i*15.625);

            cnt++;
            imageView.invalidate();
        }
    }
    @Override
    public void onClick(View arg0) {
        if (started) {
            started = false;
            startStopButton.setText("Start");
            recordTask.cancel(true);
            myHz=sum/cnt;
            String text=Double.toString(myHz);
            sum=0;
            cnt=0;
            isMyTurn=false;

            SharedPreferences pref=getSharedPreferences("login",MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("highscore", Integer.toString(Math.max(Integer.parseInt(pref.getString("highscore","0")),(int) Math.round(myHz))));
            edit.commit();
            startStopButton.setEnabled(false);
            if (myHz > threshold){
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                mSocket.emit("say", Double.toString(myHz));
            }else{
                mSocket.emit("say", "-1");
                String game = "You are lower than " + Double.toString(threshold) + "Hz, your score is " + text + "Hz... T ^ T";
                Toast.makeText(getApplicationContext(), game, Toast.LENGTH_LONG).show();

                SharedPreferences.Editor edit2 = pref.edit();
                edit2.putString("defeat", Integer.toString(Integer.parseInt(pref.getString("defeat","0"))+1));
                edit2.commit();
                String info_text=pref.getString("id","Anonymous")+
                        "/"+pref.getString("victory","0")
                        +"/"+pref.getString("defeat","1")
                        +"/"+pref.getString("highscore","0");
                mSocket.emit("pushinfo", info_text);
            }

        } else {
            started = true;
            startStopButton.setText("Stop");
            recordTask = new RecordAudio();
            recordTask.execute();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        mSocket.emit("disconn", pref.getString("myRoom",""));
        Log.i("hahahahahaha",pref.getString("myRoom",""));
        mSocket.disconnect();
        mSocket.off();
    }


}
