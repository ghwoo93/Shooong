package com.kosmo.shooong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {


    private EditText id;
    private EditText pwd;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        //타이틀바 색상 변경-자바코드
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0x99FF0000));

        //서버 연동전 화면 전환을 위한 테스트 코드
        /*
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                //전환된 화면(MainActivity)Destroy시 로그인 화면도 Destroy하기
                //XML에서 android:noHistory="true"처리
            }
        };
        //1초 지연후 LoginActivity로 전환
        handler.sendEmptyMessageDelayed(0,1000);
        */
        //위젯 얻기]
        initView();
        //버튼 배경 투명처리
        btnLogin.getBackground().setAlpha(170);
        //버튼에 리스너 부착
        btnLogin.setOnClickListener(listener);
    }///////////////onCreat
    //버튼 이벤트 처리]
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new LoginAsyncTask().execute(
                    "http://192.168.0.8:8080/shoong/android/member/json",
                    id.getText().toString(),
                    pwd.getText().toString());
        }
    };//////////////////OnClickListener
    //서버로 데이타 전송 및 응답을 받기 위한 스레드 정의
    private class LoginAsyncTask extends AsyncTask<String,Void,String>{

        private AlertDialog progressDialog;
        @Override
        protected void onPreExecute() {
            //프로그래스바용 다이얼로그 생성]
            //빌더 생성 및 다이얼로그창 설정
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress);
            builder.setIcon(android.R.drawable.ic_menu_compass);
            builder.setTitle("로그인");

            //빌더로 다이얼로그창 생성
            progressDialog = builder.create();
            progressDialog.show();
        }///////////onPreExecute

        @Override
        protected String doInBackground(String... params) {
            StringBuffer buf = new StringBuffer();
            try {
                URL url = new URL(String.format("%s?id=%s&pwd=%s",params[0],params[1],params[2]));
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                //서버에 요청 및 응답코드 받기
                int responseCode=conn.getResponseCode();
                if(responseCode ==HttpURLConnection.HTTP_OK){
                    //연결된 커넥션에서 서버에서 보낸 데이타 읽기
                    BufferedReader br =
                            new BufferedReader(
                                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    String line;
                    while((line=br.readLine())!=null){
                        buf.append(line);
                    }
                    br.close();
                }
            }
            catch(Exception e){e.printStackTrace();}

            SystemClock.sleep(2000);
            return buf.toString();
        }///////////doInBackground

        @Override
        protected void onPostExecute(String result) {
            //서버로부터 받은 데이타(JSON형식) 파싱
            //회원이 아닌 경우 빈 문자열
            Log.i("com.kosmo.shooong","result:"+result);
            if(result !=null && result.length()!=0) {//회원인 경우
                try {
                    JSONObject json = new JSONObject(result);
                    String name = json.getString("userName");
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                    //finish()불필요-NO_HISTORY로 설정했기때문에(매니페스트에서)
                    //아이디 비번저장
                    SharedPreferences preferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor =preferences.edit();
                    editor.putString("name",name);
                    editor.putString("id",json.getString("userId"));
                    editor.putString("pwd",json.getString("userPWD"));
                    Log.i("com.kosmo.shooong","id:"+id+"pwd:"+pwd);
                    editor.commit();
                }
                catch(Exception e){e.printStackTrace();}

            }
            else{//회원이 아닌 경우
                Toast.makeText(LoginActivity.this,"아이디와 비번이 일치하지 않아요",Toast.LENGTH_SHORT).show();
            }

            //다이얼로그 닫기
            if(progressDialog!=null && progressDialog.isShowing())
               progressDialog.dismiss();
        }
    }///////////////LoginAsyncTask

    private void initView() {
        id = (EditText) findViewById(R.id.id);
        pwd = (EditText) findViewById(R.id.pwd);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }
}//////////class
