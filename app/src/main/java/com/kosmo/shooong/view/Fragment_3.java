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
        Log.i("com.kosmo.kosmoapp","onCreateView:3");
        View view=inflater.inflate(R.layout.tablayout_3,null,false);
        btnCamera = view.findViewById(R.id.btnCamera);
        btnGallery= view.findViewById(R.id.btnGallery);
        imageView = view.findViewById(R.id.imageView);
        //카메라 버튼
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        //갤러리 버튼
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        return view;
    }/////////

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                if(data== null){
                    Toast.makeText(context,"카메라로 사진 찍기 실패",Toast.LENGTH_SHORT).show();
                    return;
                }
                Bitmap bmp=(Bitmap)data.getExtras().get("data");
                /*
                아래는 용량이 클 경우 OutOfMemoryException 발생이 예상되어 압축
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                //압축된 이미지를 이미지뷰에표시
                imageView.setImageBitmap(bitmap);
                 */
                //압축이 안된 이미지를 이미지뷰에 표시
                imageView.setImageBitmap(bmp);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                photoImagePath=file.getAbsolutePath()+File.separator+dateFormat.format(new Date())+"_camera.png";

                file = new File(photoImagePath);

                ///갤러리에 촬영한 사진 추가하기
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(
                            new FileOutputStream(file));

                    bmp.compress(Bitmap.CompressFormat.PNG,100,bos);//이미지가 용량이 클 경우
                                                                          //OutOfMemoryException 발생할수 있음.그래서 압축
                    //사진을 앨범에 보이도록 갤러리앱에 방송을 보내기
                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    bos.flush();
                    bos.close();
                    //https://square.github.io/okhttp/
                    //1.그레이들에 okhttp3라이브러리 추가
                    //서버로 전송하기
                    sendImageToServer(file);

                }
                catch(Exception e){e.printStackTrace();}
                Log.i("com.kosmo.kosmoapp",photoImagePath);
            }
        }
        else if(requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE){
            //갤러리에 있는 이미지를 이미지뷰에 표시하기
            if(resultCode== Activity.RESULT_OK){
                if(data== null){
                    Toast.makeText(context,"이미지를 가져올수 없어요",Toast.LENGTH_SHORT).show();
                    return;
                }
                //갤러리에 있는 이미지 선택
                Uri selectedImageUri=data.getData();
                //선택된 이미지의 Uri로 이미지뷰에 표시
                imageView.setImageURI(selectedImageUri);
            }
        }
    }///////////////
    //서버로 이미지를 전송하는 메소드(AsyncTask+HttpURLConnection(doInBackground안에서))
    private void sendImageToServer(File file){

        //요청바디 설정
        RequestBody requestBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //파라미터명은 picture
                .addFormDataPart("picture",file.getName(),RequestBody.create(file,MediaType.parse("image/png")))
                .build();
        //요청 객체 생성
        Request request=new Request.Builder()
                .url("http://192.168.0.20:8080/rest/upload")
                .post(requestBody)
                .build();
        OkHttpClient client= new OkHttpClient();
        //비동기로 요청 보내기
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
            //서버로부터 응답받는 경우
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("com.kosmo.kosmoapp", response.body().string());

            }
        });

    }


}
