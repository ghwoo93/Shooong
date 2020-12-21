package com.kosmo.shooong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.kosmo.shooong.view.MyPagerAdapter;

import java.util.List;
import java.util.Vector;

/*
TabLayout 사용하기
1.activity_main.xml에서 design모드 연후
2.design팔레트의 돋보기 클릭후 TabLayout으로 검색후 다운로드 버튼 클릭
3.build.gradle(app레벨)에 아래 코드 자동 추가됨
implementation 'com.google.android.material:material:1.1.0'
 */
public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    //Fragment객체 저장용
    private List<Fragment> fragments = new Vector<Fragment>();

    //탭메뉴 인덱스 저장용]
    private int tabCurrentIndex=0;
    private int tabPreviousindex=0;
    //Volley라이브러리 사용시
    public static Context APP_CONTEXT;

    public static final int MY_REQUEST_PERMISSION_EXTERNAL=1;

    //현재 앱에서 필요한 권한들
    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private Button mapButton;
    //허용이 안된 권한들을 저장할 컬렉션
    final List<String> denyPermissions = new Vector<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Log.i("com.kosmo.kosmoapp",getIntent().getStringExtra("name")+"님 즐앱 하세요");

        APP_CONTEXT = getApplicationContext();

        //위젯 얻기
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        mapButton = findViewById(R.id.btnAppChange);
        //탭메뉴 구성
        //방법1-setText() 및 setIcon()메소드 사용
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.perm_group_app_info).setText("PICASSO"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.perm_group_audio_settings).setText("VOLLEY"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.perm_group_bluetooth).setText("카메라"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.perm_group_device_alarms).setText("웹뷰"));



        //방법2-하나의 탭메뉴를 위한 사용자 정의 레이아웃 사용
        //tabLayout.addTab(tabLayout.newTab().setCustomView(customTabview("PICCASO",R.drawable.perm_group_app_info)));
        //tabLayout.addTab(tabLayout.newTab().setCustomView(customTabview("VOLLEY",R.drawable.perm_group_audio_settings)));
        //tabLayout.addTab(tabLayout.newTab().setCustomView(customTabview("카메라",R.drawable.perm_group_bluetooth)));
        //tabLayout.addTab(tabLayout.newTab().setCustomView(customTabview("웹뷰",R.drawable.perm_group_device_alarms)));
        //어댑터 생성
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        //ViewPager와 MyPageAdapter를 연결
        viewPager.setAdapter(myPagerAdapter);



        //리스너 설정
        //ViewPager 와 TabLayout의  pageChange 이벤트 연결
        //아래 생략시 마우스로 페이지 전환시 탭 메뉴가 같이 연동이 안된다
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            //탭이 선택되었을 때 호출
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
                //이전 및 현재 탭 인덱스 저장
                //1.현재 인덱스를 이전 인덱스로 설정
                tabPreviousindex=tabCurrentIndex;
                //2.현재 인덱스 설정
                tabCurrentIndex = tab.getPosition();
                //((TextView)tab.getCustomView().findViewById(R.id.tabTitle)).setTextColor(Color.RED);
                if(tab.getPosition()==2){
                    //권한 요청]
                    //3]권한 요청하기
                    //마쉬멜로우 이후 버전부터 사용자에게 권한 요청보낸다
                    if(Build.VERSION.SDK_INT >=23) {
                        requestUserPermissions();
                    }//if
                }
            }
            //탭이 선택되지 않았을 때 호출
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //((TextView)tab.getCustomView().findViewById(R.id.tabTitle)).setTextColor(Color.GRAY);
            }
            //탭이 다시 선택되었을 때 호출
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        Log.i("com.kosmo.kosmoapp","onCreate:MainActivity");
    }//////////onCreate
    //사용자 정의 탭 메뉴 레이아웃 전개 메소드
    private View customTabview(String menuTitle, int tabIconRes) {
        View view=View.inflate(this,R.layout.custom_tab_layout,null);
        //아이콘 설정
        ImageView tabIcon = view.findViewById(R.id.tabIcon);
        tabIcon.setImageResource(tabIconRes);
        //탭메뉴 타이틀 설정
        TextView tabMenuTitle = view.findViewById(R.id.tabTitle);
        tabMenuTitle.setText(menuTitle);
        return view;
    }/////////////////////

    //백키를 누를때 자동으로 호출되는 콜백 메소드]
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(tabCurrentIndex ==0){
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_menu_compass)
                    .setTitle("어플리케이션 종료")
                    .setMessage("프로그램을 종료 하시게씁니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //앱 종료
                            finish();
                        }
                    })
                    .setNegativeButton("아니오",null).show();
        }
        else{//백키 누를때 이전 탭메뉴로 이동시키기
            viewPager.setCurrentItem(tabPreviousindex);

        }
    }////////////onBackPressed

    //사용자에게 권한을 요청하는 메소드(안드로이드 6.0이상부터 추가됨)
    private boolean requestUserPermissions() {
        Log.i("com.kosmo.kosmoapp","denyPermissions수:"+denyPermissions.size());
        for (String permission : permissions) {
            Log.i("com.kosmo.kosmoapp","권한 요청들:"+permission);
            //권한 획득시 0,없을시 -1
            int checkSelfPermission = ActivityCompat.checkSelfPermission(this, permission);
            //권한이 없는 경우
            Log.i("com.kosmo.kosmoapp","군한이 없는 요청들:"+checkSelfPermission);
            if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                denyPermissions.add(permission);
            }
        }
        //권한이 없는게 있다면
        if (denyPermissions.size()!=0) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_compass)
                    .setCancelable(false)
                    .setTitle("권한 요청")
                    .setMessage("권한을 허용해야만 이 앱을 정상적으로 사용할 수 있습니다")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //사용자에게 없는 권한 요청
                            ActivityCompat.requestPermissions(MainActivity.this, denyPermissions.toArray(new String[denyPermissions.size()]), MainActivity.MY_REQUEST_PERMISSION_EXTERNAL);
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           //카메라 탭 메뉴 비활성화
                            LinearLayout tabCamera=(LinearLayout)tabLayout.getChildAt(0);
                            tabCamera.getChildAt(2).setClickable(false);
                            //처음 탭 메뉴로 이동
                            viewPager.setCurrentItem(0);
                            Log.i("com.kosmo.kosmoapp","카메라 비활성화 하기");
                        }
                    }).show();

            return false;
        }
        return true;//모든 권한이 있는 경우
        //onRequestPermissionsResult오버라이딩 하자

    }////////////requestUserPermission

    //아래 메소드는 프래그먼트에서 오버라이딩 하지말고
    //프래그 먼트를 붙인 액티비티에서 오버라이딩 해라
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case MainActivity.MY_REQUEST_PERMISSION_EXTERNAL:
                //사용자가  allow(허용)나 deny를 누른 경우
                if(grantResults.length > 0 ){
                    for (int i = 0; i < permissions.length; i++) {

                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {//허용한 경우
                            Log.i("com.kosmo.kosmoapp",":허용");
                            //허용시 해당 구너한 삭제
                            denyPermissions.remove(permissions[i]);

                        }
                        else{//사용자가  deny(거부)를 누른 경우
                            LinearLayout tabCamera=(LinearLayout)tabLayout.getChildAt(0);
                            tabCamera.getChildAt(2).setClickable(false);
                            viewPager.setCurrentItem(0);
                            Log.i("com.kosmo.kosmoapp",":거부");
                            Toast.makeText(this, "권한을 허용해야만 카메라를 사용하실수 있습니다", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        }
    }////////////////onRequestPermissionsResult
    //아이디 비번 삭제
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor =preferences.edit();
        editor.remove("id");
        editor.remove("pwd");
        editor.commit();
    }
}/////////////class
