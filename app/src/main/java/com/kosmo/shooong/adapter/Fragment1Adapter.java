package com.kosmo.shooong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosmo.shooong.item.FragmentItem;
import com.squareup.picasso.Picasso;

import java.util.List;
import com.kosmo.shooong.R;

public class Fragment1Adapter extends BaseAdapter {
    //생성자를 통해서 초기화 할 멤버 변수들]

    //리스트뷰가 실행되는 컨텍스트
    private Context context;
    //리스트뷰에 뿌릴 데이타
    private List<FragmentItem> items;
    //레이아웃 리소스 아이디(선택사항)
    private int layoutResId;
    //2]생성자 정의:생성자로 Context와 리스트뷰에 뿌려줄 데이타를 받는다.
    //             리소스 레이아웃 아이디(int)는 선택사항
    //인자생성자2]컨텍스트와 레이아웃 리소스 아이디 그리고 데이타
    public Fragment1Adapter(Context context,int layoutResId,List<FragmentItem> items) {
        this.context = context;
        this.items = items;
        this.layoutResId = layoutResId;
    }

    @Override
    public int getCount() {
        return items.size();
    }
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView=View.inflate(context,layoutResId,null);
        }
        //리스트 뷰에서 하나의 아이템항목을 구성하는 각 위젯의 데이타 설정]
        //텍스트뷰 위젯 얻고 데이터 설정]
        ((TextView)convertView.findViewById(R.id.itemtext)).setText(items.get(position).getItemText());
        //이미지뷰 위젯 얻고 데이타 설정]
        ImageView imageView = convertView.findViewById(R.id.itemicon);
        //Picasso라이브러리 참조 사이트:http://dwfox.tistory.com/31
        //                            https://github.com/square/picasso
        //외부(원격)로부터 이미지를 불러와야 할 경우 유용하게 사용할 수 있는 라이브러리이다.
        //매우 간단한 코드 몇 줄로 이미지 로딩
        //라이브러리 등록 방법
        // build.gradle (Module:app)파일에 implementation 'com.squareup.picasso:picasso:(insert latest version)' 추가
        //마지막으로 아래처럼 코딩
        Picasso.get().load(items.get(position).getItemImageUrl()).into(imageView);

        if(position % 2==0)
            convertView.setBackgroundColor(0xFFFFFFFF);
        else
            convertView.setBackgroundColor(0xFFC0C0C0);
        //아이템 이벤트 처리]
        //아이템 항목(이미지뷰하나,텍스트뷰 하나구성) 클릭시
        final int index = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,items.get(index).getItemText(),Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
