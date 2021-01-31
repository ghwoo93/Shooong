package com.kosmo.shooong.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kosmo.shooong.MoveActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

//https://firebase.google.com/docs/cloud-messaging/android/receive?hl=ko
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG ="fcm_messaging_app";
    //※포그라운드 상태인 앱에서 알림 메시지(FCM에서 자동처리)수신하려면
    // onMessageReceived 콜백 오버라이딩

    //파이어베이스 콘솔에서 알림 메시지 및 데이타 메시지를 보낼때
    //포그라운드 일때:모든 경우 onMessageRecieved가 호출됨
    //백그라운드일때:데이타메시지를 포함한 경우에만 호출된다
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG,"From:"+remoteMessage.getFrom());
        //알림메시지:키값이 정해져 있다 :제목-title,내용-body 예:{"notification":{"title":"알림 제목","body":"알림 텍스트"}
        if(remoteMessage.getNotification() !=null){
            Log.i(TAG,"알림 제목:"+remoteMessage.getNotification().getTitle());
            Log.i(TAG,"알림 텍스트:"+remoteMessage.getNotification().getBody());

        }
        //데이타 메시지(추가 옵션인 키/값항목에 입력한 데이타)
        //getData():Map컬렉션 반환
        //데이타 메시지가 있는 경우
        //포그라운드일때:알림도 데이타 메시지로 변경
        //백그라운드 일때:노티는 알림메시지가 뜨고 데이터 메시지는 MainActivity의 인텐트 부가 정보로 전송
        if(remoteMessage.getData().size() > 0){
            Log.i(TAG,"데이타 메시지(getData()):"+remoteMessage.getData());
            Log.i(TAG,"데이타 메시지 제목):"+remoteMessage.getData().get("dataTitle"));
            Log.i(TAG,"데이타 메시지 텍스트):"+remoteMessage.getData().get("dataBody"));

            showNotification(remoteMessage.getData().get("dataTitle"),remoteMessage.getData().get("dataBody"));
        }
        else{//데이타 메시지가 없는 경우
            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }
    }///////////onMessageReceived

    public void showNotification(String title,String body){
        Intent intent = new Intent(this,MoveActivity.class);
        //인텐트에 부가정보 저장
        intent.putExtra("dataTitle",title);
        intent.putExtra("dataBody",body);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"com.kosmo.shooong.service")
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        //InboxStyle스타일 추가-여러줄의 body 입력시 표시하기 위함
        //한줄 짜리 body 입력시는 생략 가능
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        StringTokenizer tokenizer = new StringTokenizer(body,"\r\n");
        while(tokenizer.hasMoreTokens()){
            inboxStyle.addLine(tokenizer.nextToken());
        }
        builder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //오레오 부터 아래 코드 추가해야 함 시작
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("com.kosmo.shooong.service","CHANEL_NAME",importance);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,500});
        notificationManager.createNotificationChannel(channel);
        //오레오 부터 아래 코드 추가해야 함 끝
        notificationManager.notify(101,builder.build());


    }////////////////showNotification
    //아래는 내가 만든 웹 서비스(UI)와 연동하기 위한 코드들
    //토큰이 변경될때마다 호출됨.
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        //설정에서 앱의 데이타 삭제후 LOGCAT확인
        //I/com.kosmo.iotpush: FCM token: cPBGRr06NwI:APA91bEqbqd9lO4mM_S0qhuRzmp8nMWrxgUnaxBBQ8bwwemdyWBCzUAOiifnJa1XTWJ7qG1JlRXQihqqN54oC2rDeugtfdHpCxDi4sqCGp4oQRTt9IgFJb-F3TAmu96-n5NoGkW0sMMs
        Log.i(TAG,"FCM token is : "+token);
        //생성 등록된 토큰을 내 서버에 보내기.
        sendRegistrationToServer(token);
    }/////////onNewToken
    //토큰을 웹서버에 전송하기 위해 아래 메소드 구현
    private void sendRegistrationToServer(String token){
        // Add custom implementation, as needed.
        new AsyncToServer().execute("http://192.168.0.8:8080/shoong/admin/setting/main.do","token="+token);
    }////////////sendRegistrationToServer

    class AsyncToServer extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            //[POST방식]
            try{
                //요청 주소로 URL객체 생성
                URL url = new URL(params[0]);
                HttpURLConnection conn =(HttpURLConnection)url.openConnection();
                //연결설정
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(3000);
                conn.setDoOutput(true);
                //스프링 서버로 보낼 데이타 설정
                OutputStream out = conn.getOutputStream();
                // Request Body에 Data 셋팅 및 서버로 전송.
                out.write(params[1].getBytes("UTF-8"));
                out.flush();
                out.close();
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    Log.i(TAG,"서버 전송 성공");
                else
                    Log.i(TAG,"서버 전송 실패");

            }
            catch(Exception e){e.printStackTrace();}
            return null;
        }
    }////////////////AsyncToServer

}///////////////MyFirebaseMessagingService
