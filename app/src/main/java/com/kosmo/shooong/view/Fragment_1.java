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

import com.kosmo.shooong.R;
import com.kosmo.shooong.adapter.Fragment1Adapter;
import com.kosmo.shooong.item.FragmentItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;


//1]Fragement상속
//※androidx.fragment.app.Fragment 상속
public class Fragment_1 extends Fragment {

    //리스트뷰에 뿌려질 데이타  선언]
    private List<FragmentItem> items = new Vector<FragmentItem>();
    private ListView listView;
    private Fragment1Adapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("com.kosmo.kosmoapp","onAttach:1");
    }

    //2]onCreateView()오버 라이딩
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("com.kosmo.kosmoapp","onCreateView:1");
        //레이아웃 전개]
        View view = inflater.inflate(R.layout.tablayout_1,null,false);
        //아이디로 리소스 가져올때:view.findViewById()
        //어댑터 생성]
        adapter=new Fragment1Adapter(getContext(),R.layout.tabmenu1_item_layout,items);
        //리스트 뷰 얻기]
        listView=view.findViewById(R.id.picassoListView);
        //리스트뷰와 어댑터 연결]
        listView.setAdapter(adapter);
        //데이타는 스레드로 원격 서버에서 받아 온다
        new ItemsAsyncTask().execute();
        return view;
    }/////////

    //파일로
    private class ItemsAsyncTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            StringBuffer buf = new StringBuffer();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //서버에 요청 및 응답코드 받기
                int responseCode = conn.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    //연결된 커넥션에서 서버에서 보낸 데이타 읽기
                    BufferedReader br =
                            new BufferedReader(
                                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    String line;
                    while((line=br.readLine())!=null){
                        buf.append(line);
                    }
                    br.close();
                    //JSON데이타 파싱
                    JSONArray array= new JSONArray(buf.toString());
                    for(int i=0;i<array.length();i++){
                        JSONObject json = array.getJSONObject(i);
                        String imageUrl = json.getString("imageUrl");
                        String text = json.getString("text");
                        FragmentItem item = new FragmentItem(imageUrl,text);
                        items.add(item);
                    }
                }
            }
            catch(Exception e){e.printStackTrace();}
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            //어댑터에게 데이터 변경 통지
            adapter.notifyDataSetChanged();
        }
    }/////////////////////ItemsAsyncTask
}
