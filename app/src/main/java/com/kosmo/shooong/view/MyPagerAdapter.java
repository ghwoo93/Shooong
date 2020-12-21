package com.kosmo.shooong.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

//ViewPager와 연결해서 Fragment로 화면을 전환하기 위한 어댑터
//1]FragmentStatePagerAdapter 상속
public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private int numbersOfFragment;
        //생성자]
    public MyPagerAdapter(FragmentManager fm,int numbersOfFragment){
        super(fm,FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numbersOfFragment = numbersOfFragment;
    }////////////MyPagerAdapter

    //탭 메뉴의 position에 해당하는 프래그먼트를 반환
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:return new Fragment_1();
            case 1:return new Fragment_2();
            //case 2:return new Fragment_3();
            default:return new Fragment_4();
        }

    }
    //총 Fragment의 갯수 즉 paging의 수
    @Override
    public int getCount() {
        return numbersOfFragment;
    }


}
