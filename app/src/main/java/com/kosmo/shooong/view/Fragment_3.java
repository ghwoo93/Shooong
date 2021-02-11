package com.kosmo.shooong.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kosmo.shooong.R;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//1]Fragement상속
//※androidx.fragment.app.Fragment 상속
public class Fragment_3 extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE=1;
    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE=2;
    public static Button btnCamera;
    private Button btnGallery;
    private ImageView imageView;

    String photoImagePath;

    private Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        Log.i("com.kosmo.kosmoapp","onAttach:3");
    }

    //2]onCreateView()오버 라이딩
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return null;
    }/////////

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }///////////////

    private void sendImageToServer(File file){

    }


}
