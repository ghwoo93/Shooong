package com.kosmo.shooong.item;

//리스트뷰의 하나의 아이템에 뿌려질 데이타를 저장 하는 자료구조]
public class FragmentCourseItem {
    //이미지 URL주소 저장용]
    private String itemImageUrl;
    //텍스트 저장용]
    private String itemText;
    //인자 생성자]
    public FragmentCourseItem(String itemImageUrl, String itemText) {
        this.itemImageUrl = itemImageUrl;
        this.itemText = itemText;
    }
    //게터]
    public String getItemImageUrl() {
        return itemImageUrl;
    }
    public String getItemText() {
        return itemText;
    }
}
