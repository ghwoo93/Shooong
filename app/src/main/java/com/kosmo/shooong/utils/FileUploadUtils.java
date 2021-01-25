package com.kosmo.shooong.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploadUtils {
    public static void send2Server(File file,String url) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files",file.getName(),RequestBody.create(file,JSON))
                .build();
        Log.i("com.kosmo.shoong","url:"+url);
        Request request=new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        //비동기로 요청 보내기
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
            //서버로부터 응답받는 경우
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("com.kosmo.shoong", response.body().string());
            }
        });
    }


}
