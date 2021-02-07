package com.kosmo.shooong.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kosmo.shooong.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


//1]Fragement상속
//※androidx.fragment.app.Fragment 상속
public class Fragment_4 extends Fragment {
    //아이디 비번 저장용
    private String id;
    private String pwd;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("com.kosmo.kosmoapp","onAttach:4");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getContext().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);

        id=preferences.getString("id",null);
        pwd=preferences.getString("pwd",null);
        Log.i("com.kosmo.kosmoapp",id+":"+pwd);
    }

    //2]onCreateView()오버 라이딩
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("com.kosmo.kosmoapp","onCreateView:4");



        //프래그먼트 레이아웃 전개
        View view=inflater.inflate(R.layout.tablayout_4,null,false);
        //웹뷰 얻기]
        WebView webView = view.findViewById(R.id.webview);
        //WebView설정]
        //1]WebView의 getSettings()메소드로 WebSettings객체
        WebSettings settings=webView.getSettings();
        //자스가 실행되도록 설정- 기본적으로 웹뷰는 자스를 지원하지 않음]
        settings.setJavaScriptEnabled(true);//필수 설정
        // 아래부분 생략시 웹뷰가 전체 레이아웃을 차지함(사이트 로드시)]
        webView.setWebViewClient(new WebViewClient());
        //자스의 alert()모양을 Toast 로 변경
        webView.setWebChromeClient(new CustomWebChromeClient());
        //get요청
        webView.loadUrl("http://192.168.75.103:8080/shoong/");
        //post요청
        /*
        try {
            String params = "id=" + URLEncoder.encode(id, "UTF-8") + "&pwd=" + URLEncoder.encode(pwd, "UTF-8");
            webView.postUrl("http://192.168.0.15:8080/shoong/android/login", params.getBytes());
        }
        catch(UnsupportedEncodingException e){e.printStackTrace();}*/

        return view;
    }/////////

    private class CustomWebChromeClient extends WebChromeClient{
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            //경고 메시지를 Toast로 보여주기
            Toast.makeText(view.getContext(),message,Toast.LENGTH_SHORT).show();
            //자바스크립트 경고창의 확인버튼을 클릭한것으로 처리하도록 호출
            //해야한다 alert()는 모달이라 클릭한 것으로 처리안하면
            //다른 메뉴를 클릭 할 수 없다
            result.confirm();
            return true;
        }
    }



}
