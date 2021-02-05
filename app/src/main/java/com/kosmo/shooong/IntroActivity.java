package com.kosmo.shooong;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

//http://ajaxload.info/ 로딩 gif이미지 만드는 사이트
//gif 이미지를 ImageView에 로드 하려면
//1. gradle파일에 implementation 'com.github.bumptech.glide:glide:4.10.0' 추가
//https://github.com/bumptech/glide
//2. 다음 코드 작성
//ImageView loading=findViewById(R.id.loading);
//Glide.with(this).load(R.drawable.ajax_loader).into(loading);
public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        ImageView imageView = findViewById(R.id.loading);
        Glide.with(this).load(R.drawable.ajax_loader).into(imageView);
        imageView.setImageAlpha(178);

        //액션바 색상 변경-자바코드
        ActionBar actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x5500ff00));
        //몇초 지연후 로그인 화면으로 이동시키기
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                //전환된 화면(LoginActivity)Destroy시 인트로 화면도 Destroy하기
                finish();
            }
        };
        //2초 지연후 LoginActivity로 전환
        handler.sendEmptyMessageDelayed(0,3000);
    }/////////////onCreate
}//////////////class
