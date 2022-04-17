package com.example.now_word;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.dao.WordDao;
import com.example.model.Word;
import com.example.utils.BaiduOCR;
import com.example.utils.OWLoadingView;
import com.example.utils.Tran_CN_split;
import com.example.utils.TransApi;
import com.soundcloud.android.crop.Crop;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TakePictureActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private Button mTakePhoto, mChooseFromAlbum;
    private ImageView mPicture;
    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_PERMISSION_CODE = 267;
    private static final int TAKE_PHOTO = 189;
    private static final int CHOOSE_PHOTO = 385;
    private static final int CROP_PHOTO=6709;
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.now_word.provider";
    private Uri mImageUri;//图片URI
    private File imageFile;String storageDir;
    private String imagePath;//图片路径
        View dialog;//进度条
        OWLoadingView owLoadingView;

        private WordDao wordDao;//数据库操作类
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wordDao=new WordDao(this);

        setContentView(R.layout.activity_take_picture);
        mTakePhoto=findViewById(R.id.take_photo);
        mChooseFromAlbum=findViewById(R.id.choose_photo);
        mPicture=findViewById(R.id.picture);
        owLoadingView=findViewById(R.id.owloading);


        /*申请读取存储的权限和相机权限*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (checkSelfPermission(PERMISSION_WRITE_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{PERMISSION_WRITE_STORAGE,Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);//这里添加申请权限的参数
        }
        }


        mTakePhoto.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                takePhoto();
                }
        });

        mChooseFromAlbum.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                openAlbum();
                }
        });
        }

        @Override
        protected void onResume() {

                super.onResume();
        }

        /**
 * 打开相册
 */
private void openAlbum() {
//        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        openAlbumIntent.setType("image/*");
//        startActivityForResult(openAlbumIntent, CHOOSE_PHOTO);//打开相册
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

}

/**
 * 拍照
 */
private void takePhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//打开相机的Intent
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
        imageFile = createImageFile();//创建用来保存照片的文件
//      if(imageFile==null){Log.i("hahaha","-----------------空");}
        //Log.i(TAG, "takePhoto: uriFromFile " + mImageUriFromFile);
        if (imageFile != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        /*7.0以上要通过FileProvider将File转化为Uri*/
        mImageUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, imageFile);
        } else {
        /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
        mImageUri = Uri.fromFile(imageFile);
        }
        takePhotoIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.addCategory(Intent.CATEGORY_DEFAULT);              // 根据文件地址创建文件
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);//将用于输出的文件Uri传递给相机
        startActivityForResult(takePhotoIntent, TAKE_PHOTO);//打开相机
        }
        }
        }

/**
 * 创建用来存储图片的文件，以时间来命名就不会产生命名冲突
 *
 * @return 创建的图片文件
 */
        private File createImageFile() {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + ".jpg";
                storageDir = Environment.getExternalStorageDirectory() + File.separator + getPackageName() + File.separator + "IMAGE/";//存储路径
                File out = new File(storageDir);
                if (!out.exists()) {
                        out.mkdirs();
                }
                imagePath=storageDir+imageFileName;
                imageFile = new File(storageDir,imageFileName);
                //Log.i("hahaha",storageDir+imageFileName);
                return imageFile;
                }

        /*申请权限的回调*/
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Log.i(TAG, "onRequestPermissionsResult: permission granted");
                } else {
                //Log.i(TAG, "onRequestPermissionsResult: permission denied");
                Toast.makeText(this, "You Denied Permission", Toast.LENGTH_SHORT).show();
                }
                }

        /*相机或者相册返回来的数据*/
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                switch (requestCode) {
                        case TAKE_PHOTO:
                                if (resultCode == RESULT_OK) {
//                                        try {

                                                Crop.of(mImageUri, mImageUri).asSquare().start(this);
                                                /*如果拍照成功，将Uri用BitmapFactory的decodeStream方法转为Bitmap*/
//                                                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));
                                                //Log.i(TAG, "onActivityResult: imageUri " + mImageUri);
//                                                galleryAddPic(mImageUri);//更新图库
//                                                mPicture.setImageBitmap(bitmap);//显示到ImageView上

//                                        } catch (FileNotFoundException e) {
//                                                e.printStackTrace();
//                                        }
                                }
                                break;
//                        case CHOOSE_PHOTO:
//                                if (data == null) {//如果没有选取照片，则直接返回
//                                        return;
//                                }
//                                //Log.i("hahaha", "onActivityResult: ImageUriFromAlbum: " + data.getData());
//                                if (resultCode == RESULT_OK) {
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                                //Log.i("hahaha", ">4.4 " + data.getData());
//                                                handleImageOnKitKat(data);//4.4之后图片解析
//                                                Crop.of(mImageUri, mImageUri).asSquare().start(this);
//
//                                        } else {
//                                                //Log.i("hahaha", "4.4" + data.getData());
//                                                handleImageBeforeKitKat(data);//4.4之前图片解析
//                                                Crop.of(mImageUri, mImageUri).asSquare().start(this);
//
//                                        }
//
//                                }
//                                break;
                        case CROP_PHOTO:
                                if (resultCode == RESULT_OK) {
                                        //调用图片识别
                                        Log.i("hahaha", "图片路径URI： " + mImageUri);
                                        //imagePath=mImageUri.getPath();
                                        Log.i("hahaha", "图片路径： " + imagePath);
                                        galleryAddPic(mImageUri);//更新图库
                                        displayImage(imagePath);
                                        startOCR();//开始上传图片
                                }
                                break;
                        case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                                if (resultCode == RESULT_OK) {
                                        mImageUri = result.getUri();
                                        imagePath=mImageUri.getPath();
                                        galleryAddPic(mImageUri);//更新图库
                                        displayImage(imagePath);
                                        startOCR();//开始上传图片
                                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                                        Exception error = result.getError();
                                }

                                break;
                        default:
                                break;
                }
        }

        /**
         * 4.4版本以下对返回的图片Uri的处理：
         * 就是从返回的Intent中取出图片Uri，直接显示就好
         * @param data 调用系统相册之后返回的Uri
         */
        private void handleImageBeforeKitKat(Intent data) {
                mImageUri = data.getData();
                Log.i("hahaha", "选择图库图片路径URI： " +mImageUri);
                imagePath = getImagePath(mImageUri, null);
                }

        /**
         * 4.4版本以上对返回的图片Uri的处理：
         * 返回的Uri是经过封装的，要进行处理才能得到真实路径
         * @param data 调用系统相册之后返回的Uri
         */
        @TargetApi(19)
        private void handleImageOnKitKat(Intent data) {
                //Log.i("hahaha", "解析数据URI");
                mImageUri = data.getData();
                if (DocumentsContract.isDocumentUri(this, mImageUri)) {
                //如果是document类型的Uri，则提供document id处理
                String docId = DocumentsContract.getDocumentId(mImageUri);
                if ("com.android.providers.media.documents".equals(mImageUri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(mImageUri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
                }
                } else if ("content".equalsIgnoreCase(mImageUri.getScheme())) {
                //如果是content类型的uri，则进行普通处理
                imagePath = getImagePath(mImageUri, null);
                } else if ("file".equalsIgnoreCase(mImageUri.getScheme())) {
                //如果是file类型的uri，则直接获取路径
                imagePath = mImageUri.getPath();
                }
                }

        /**
         * 将imagePath指定的图片显示到ImageView上
         */
        private void displayImage(String imagePath) {
                if (imagePath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                mPicture.setImageBitmap(bitmap);
                } else {
                Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
                }
                }

        /**
         * 将Uri转化为路径
         * @param uri 要转化的Uri
         * @param selection 4.4之后需要解析Uri，因此需要该参数
         * @return 转化之后的路径
         */
        private String getImagePath(Uri uri, String selection) {
                String path = null;
                Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
                if (cursor != null) {
                if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
                cursor.close();
                }
                return path;
                }

        @Override
        protected void onPause() {
                owLoadingView.setVisibility(View.INVISIBLE);
                super.onPause();
        }

        /**
         * 将拍的照片添加到相册
         *
         * @param mImageUri0 拍的照片的Uri
         */
        private void galleryAddPic(Uri mImageUri0) {
//                // 其次把文件插入到系统图库,因为这里使用的是自定义保存位置，不需要，否则会保存两张一样的
//                try {
//                        MediaStore.Images.Media.insertImage(this.getContentResolver(),
//                                imageFile .getAbsolutePath(),  storageDir , null);
//                } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                }


                // 最后通知图库更新
//              context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mImageUri0.getPath()))));
//              Log.i("hahaha", "更新图库 " + mImageUri);
                }

        //开始上传图片和解析
        private void startOCR(){

                owLoadingView.setVisibility(View.VISIBLE);
                owLoadingView.startAnim();
                owLoadingView.setAutoStartAnim(true);

                //调用图片识别
                Toast.makeText(this,"识别中，请稍等.....",Toast.LENGTH_LONG).show();

                new Thread(new Runnable() {
                        @Override
                        public void run() {
                                //获取拍照取词的数据
                                String resultstr= BaiduOCR.accurateBasic(imagePath);

                                //处理结果，返回单词列表
                                List<Word> wordList = resultToWords(resultstr);

                                //将处理结果传到下一个页面
                                Intent intent=new Intent(TakePictureActivity.this,PictureResultActivity.class);
                                intent.putExtra("result",(Serializable)wordList);
                                startActivity(intent);
                        }
                }).start();


        }

        //处理结果，返回单词列表
        private List<Word> resultToWords(String result){
                //首先从拍照取词中获取英文单词，然后再传给百度翻译进行翻译获取中文意思
                List<Word> wordList = null;
                List<Word> words=new ArrayList<>();
//        Gson gson=new Gson();
//        ResultJson resultJson=gson.fromJson(result,ResultJson.class);
                try {  //获取英文单词字符串
                        JSONObject jsonObject_1=new JSONObject(result);
                        JSONArray jsonArray_1=jsonObject_1.getJSONArray("words_result");
                        String en_str="";
                        for (int i=0; i < jsonArray_1.length(); i++)    {
                                JSONObject jsonObject = jsonArray_1.getJSONObject(i);
                                String wordheadAndChina = jsonObject.getString("words");
                                if((Tran_CN_split.getWordHead(wordheadAndChina)!=null)&&(Tran_CN_split.getWordHead(wordheadAndChina)!="")){
                                        en_str=en_str+Tran_CN_split.getWordHead(wordheadAndChina)+"\n";
                                }
                        }
                        //Log.i("hahaha","英文单词"+en_str);
                        //调用百度翻译接口，传入英文单词获取中文意思
                        TransApi transApi=new TransApi("20201227000657117","alLQymLvspjYV98dsrSa");
                        String stringResult=transApi.getTransResult(en_str,"auto","zh");
                        String stringResult_utf_8 = URLDecoder.decode(stringResult, "UTF-8");
                        //Log.i("hahaha", "中文意思返回结果"+stringResult_utf_8);

                        //处理百度翻译的数据构成单词列表
                        JSONObject jsonObject_2=new JSONObject(stringResult_utf_8);
                        JSONArray jsonArray_2=jsonObject_2.getJSONArray("trans_result");
                        for (int i=0; i < jsonArray_2.length(); i++)    {
                                JSONObject jsonObject = jsonArray_2.getJSONObject(i);
                                String wordhead = jsonObject.getString("src");//单词
                                String tran_cn = jsonObject.getString("dst");//中文意思
                                Word word=new Word(wordhead,tran_cn);
                                words.add(word);
                        }

                        //数据库同步然后返回，若数据库中已有就替换成已有数据
                        wordList=wordDao.UpdateHaveWord(words);
                } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
                return wordList;
        }
}