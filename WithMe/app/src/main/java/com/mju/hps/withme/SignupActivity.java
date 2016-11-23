package com.mju.hps.withme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.jni.WithMeJni;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.regex.Pattern;

import static com.mju.hps.withme.constants.Constants.CROP_FROM_CAMERA;
import static com.mju.hps.withme.constants.Constants.PICK_FROM_ALBUM;
import static com.mju.hps.withme.constants.Constants.PICK_FROM_CAMERA;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mail;
    EditText password;
    EditText passwordConfirm;
    EditText name;
    EditText birth;
    EditText phone;
    RadioGroup genderGroup;

    ImageView profileImage;
    private Uri mImageCaptureUri;
    private Bitmap photo;

    private String imageRealPath;

    protected InputFilter filterPhone = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mail = (EditText)findViewById(R.id.signup_input_mail);
        password = (EditText)findViewById(R.id.signup_input_password);
        passwordConfirm = (EditText)findViewById(R.id.signup_input_password_confirm);
        name = (EditText)findViewById(R.id.signup_input_name);
        birth = (EditText)findViewById(R.id.signup_input_birth);
        phone = (EditText)findViewById(R.id.signup_input_phone);
        genderGroup = (RadioGroup)findViewById(R.id.signup_group_gender);
        phone.setFilters(new InputFilter[] {filterPhone});

        profileImage = (ImageView) findViewById(R.id.signup_input_profileImage);
        profileImage.setOnClickListener(this);

    }

    public void createUser(View view) {
        WithMeJni jni = new WithMeJni();
        if(mail.getText().toString().equals("")){
            Toast.makeText(this, "메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.getText().toString().equals("")){
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwordConfirm.getText().toString().equals("")){
            Toast.makeText(this, "비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.getText().toString().equals("")){
            Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(birth.getText().toString().equals("")){
            Toast.makeText(this, "생년월일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(jni.isValidMail(mail.getText().toString()) == 0){
            Toast.makeText(this, "메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(jni.isValidPhone(phone.getText().toString()) == 0){
            Toast.makeText(this, "핸드폰 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(jni.isSamePassword(password.getText().toString(), passwordConfirm.getText().toString()) == 0){
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton gender = (RadioButton)findViewById(genderGroup.getCheckedRadioButtonId());

        final String json = "{" +
            "\"mail\" : \""  + mail.getText().toString() + "\", " +
            "\"password\" : \""  + password.getText().toString() + "\", " +
            "\"token\" : \""  + FirebaseInstanceId.getInstance().getToken() + "\", " +
            "\"name\" : \""  + name.getText().toString() + "\", " +
            "\"birth\" : \""  + birth.getText().toString() + "\", " +
            "\"phone\" : \""  + phone.getText().toString() + "\", " +
            "\"gender\" : \""  + gender.getText().toString() + "\"}";

        if (photo != null){
            imageRealPath = DatabaseLab.getInstance().getRealPathFromURI(mImageCaptureUri);
//            Log.e("imageRealPath", imageRealPath);
        }

        final Activity activity = this;
        new Thread() {
            public void run() {
                if(photo == null){
                    String responseStr = ServerManager.getInstance().post(Constants.SERVER_URL + "/user", json);
                    Log.d("createUserResult", responseStr);
                    try {
                        JSONObject response = new JSONObject(responseStr);
                        String result = response.getString("result");
                        if(result.equals("fail")){
                            activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.createUserFail"));
                        }
                        else {
                            activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.createUserSuccess"));
                        }

                    } catch (Throwable t) {
                        Log.e("createUser", t.toString());
                    }
                }

                //c
                else {
                    Log.e("RealPath", imageRealPath);
                    String responseStr = ServerManager.getInstance().userSignup(Constants.SERVER_URL + "/user/image", json, new File(imageRealPath), mail.getText().toString());
                    Log.d("createUserResult", responseStr);
                    try {
                        JSONObject response = new JSONObject(responseStr);
                        String result = response.getString("result");
                        if(result.equals("fail")){
                            activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.createUserFail"));
                        }
                        else {
                            activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.createUserSuccess"));
                        }

                    } catch (Throwable t) {
                        Log.e("createUser", t.toString());
                    }
                }

            }
        }.start();
    }

    public void onBirthClicked (View v){
        new DatePickerDialog(this, dateSetListener, 2016, 12, 25).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            birth.setText("" + year + "." + monthOfYear+1 +"." + dayOfMonth);
        }
    };



    /**
     * 카메라에서 이미지 가져오기
     */
    private void doTakePhotoAction()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        Log.e("doTake", mImageCaptureUri.getPath());

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction()
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onClick(View v) {

        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakePhotoAction();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();
    } // end onClick

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받음.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에 임시 파일을 삭제.
                final Bundle extras = data.getExtras();


                if(extras != null)
                {
                    photo = extras.getParcelable("data");
                    profileImage.setImageBitmap(photo);

                    mImageCaptureUri = getImageUri(getApplicationContext(), photo);
                }

//                Log.e("image!", mImageCaptureUri.getPath());
                // 임시 파일 삭제
                /*
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    Log.e("Exist!", "exist!!");
                    f.delete();
                }
                */
                break;
            }

            case PICK_FROM_ALBUM:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행

                mImageCaptureUri = data.getData();

                /*
                File original_file = getImageFile(mImageCaptureUri);
                mImageCaptureUri = createSaveCropFile();
                File cpoy_file = new File(mImageCaptureUri.getPath());
                // SD카드에 저장된 파일을 이미지 Crop을 위해 복사한다.
                copyFile(original_file , cpoy_file);
                */
            }

            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정.
                // 이후에 이미지 크롭 어플리케이션을 호출

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
        }
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

//    public void hideKeyboard(){
//        View currentFocus = SignupActivity.this.getCurrentFocus();    // Change the name according to your activity's name.
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(SignupActivity.this.INPUT_METHOD_SERVICE);
//        if(currentFocus != null){
//            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(),0);
//            currentFocus.clearFocus();
//        }
//    }



}
