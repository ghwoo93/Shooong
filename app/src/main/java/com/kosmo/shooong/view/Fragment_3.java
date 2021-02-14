package com.kosmo.shooong.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosmo.shooong.R;
import com.kosmo.shooong.adapter.Fragment3CourseAdapter;
import com.kosmo.shooong.item.FragmentCourseItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

//1]Fragement상속
//※androidx.fragment.app.Fragment 상속
public class Fragment_3 extends Fragment {

    //리스트뷰에 뿌려질 데이타  선언]
    private List<FragmentCourseItem> items = new Vector<FragmentCourseItem>();
    private ListView listView;
    private Fragment3CourseAdapter adapter;
    private View view;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("com.kosmo.kosmoapp","onAttach:3");
    }

    //2]onCreateView()오버 라이딩
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tablayout_3,null,false);
        //아이디로 리소스 가져올때:view.findViewById()
        //어댑터 생성]
        adapter=new Fragment3CourseAdapter(getContext(),R.layout.tabmenu3_item_layout,items);
        //리스트 뷰 얻기]
        listView=view.findViewById(R.id.interCourseView);
        //리스트뷰와 어댑터 연결]
        listView.setAdapter(adapter);

        //데이타는 스레드로 원격 서버에서 받아 온다
        new ItemsAsyncTask().execute("http://192.168.0.15:8080/shoong/android/course/json");
        return view;
    }/////////

    private class ItemsAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuffer buf = new StringBuffer();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                //서버에 요청 및 응답코드 받기
                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
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

            return buf.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("com.kosmo.shooong", "courseResult:" + result);
            Gson gson = new Gson();
            Vector<FragmentCourseItem> list =
                    gson.fromJson(result,new TypeToken<Vector<FragmentCourseItem>>(){}.getType());

            for(FragmentCourseItem item:items) Log.i("com.kosmo.shooong", "courseItem:" + item.toString());
            items.addAll(list);
            //어댑터에게 데이터 변경 통지
            adapter.notifyDataSetChanged();
        }
    }
}
