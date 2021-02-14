package com.kosmo.shooong.item;
//카테고리 코스명 길이(km) 시간(분) 등록일

import java.text.SimpleDateFormat;

//리스트뷰의 하나의 아이템에 뿌려질 데이타를 저장 하는 자료구조]
public class FragmentCourseItem {
    //코스 카테
    private String courseCateName;
    //코스 이름
    private String courseName;
    //코스 길이
    private String courseLength;
    //코스 시간
    private String courseTime;
    //코스 등록일
    private String courseRegiDate;
    //코스 파일 이름
    private String courseId;

    public FragmentCourseItem(
            String courseCateName, String courseName, String courseLength,
            String courseTime, String courseRegiDate, String courseId) {
        this.courseCateName = courseCateName;
        this.courseName = courseName;
        this.courseLength = courseLength;
        this.courseTime = courseTime;
        this.courseRegiDate = courseRegiDate;
        this.courseId = courseId;
    }

    public String getCourseCateName() {
        return courseCateName;
    }

    public void setCourseCateName(String courseCateName) {
        this.courseCateName = courseCateName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseLength() {
        return courseLength;
    }

    public void setCourseLength(String courseLength) {
        this.courseLength = courseLength;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public String getCourseRegiDate() {
        return courseRegiDate;
    }

    public void setCourseRegiDate(String courseRegiDate) {
        this.courseRegiDate = courseRegiDate;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "FragmentCourseItem{" +
                "courseCateName='" + courseCateName + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseLength='" + courseLength + '\'' +
                ", courseTime='" + courseTime + '\'' +
                ", courseRegiDate='" + courseRegiDate + '\'' +
                ", courseId='" + courseId + '\'' +
                '}';
    }
}
