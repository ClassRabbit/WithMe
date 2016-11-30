package com.mju.hps.withme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.mju.hps.withme.constants.Constants.CROP_FROM_CAMERA;
import static com.mju.hps.withme.constants.Constants.PICK_FROM_ALBUM;
import static com.mju.hps.withme.constants.Constants.PICK_FROM_CAMERA;

public class UserInfoEditActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int MSG_EDIT_SUCCESS = 1;
    private static final int MSG_EDIT_FAIL = 2;
    private static final int MSG_EDIT_ERROR = 3;


    private EditText email, name, password, birth, phone;

    ImageView profileImage;

    private Uri mImageCaptureUri;
    private Bitmap photo;

    private String imageRealPath;
    private Button infoEditBtn;
    private RadioGroup genderGroup;
    //error handler
    private android.support.v7.app.AlertDialog.Builder builder;
    private android.support.v7.app.AlertDialog theAlertDialog;

    private Handler handler;

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

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str;
                switch (msg.what) {
                    case MSG_EDIT_ERROR:
                        infoEditBtn.setClickable(true);
                        Toast.makeText(UserInfoEditActivity.this, "서버에러 입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_EDIT_FAIL:
                        infoEditBtn.setClickable(true);
                        Toast.makeText(UserInfoEditActivity.this, "회원정보 수정에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_EDIT_SUCCESS:
                        Toast.makeText(UserInfoEditActivity.this, "회원정보 수정에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }

            }
        };

        setContentView(R.layout.activity_user_info_edit);

        email = (EditText)findViewById(R.id.user_info_edit_email);
        name = (EditText)findViewById(R.id.user_info_edit_name);
        birth = (EditText)findViewById(R.id.user_info_edit_birth);
        password = (EditText) findViewById(R.id.user_info_edit_password);
        phone = (EditText)findViewById(R.id.user_info_edit_phone);
        phone.setFilters(new InputFilter[] {filterPhone});

        email.setEnabled(false);
        genderGroup = (RadioGroup)findViewById(R.id.user_info_edit_gender);

        infoEditBtn = (Button)findViewById(R.id.user_info_edit_button);

        setupUI(findViewById(R.id.activity_user_info_edit));

        profileImage = (ImageView) findViewById(R.id.user_info_edit_profileImage);
        profileImage.setOnClickListener(this);

        builder = new android.support.v7.app.AlertDialog.Builder(this);
        birth.setFocusable(false);

        ServerManager.getInstance().getUserProfileImage(User.getInstance().getMail(), profileImage);

    }

    @Override
    protected void onStart() {
        super.onStart();
        email.setText(User.getInstance().getMail());
        name.setText(User.getInstance().getName());
        birth.setText(User.getInstance().getBirth());
        password.setText(User.getInstance().getPassword());
        phone.setText("" + User.getInstance().getPhone());
        Log.e("GENDER", (User.getInstance().getGender()));
        if((User.getInstance().getGender()).equals("Man")){
            ((RadioButton) findViewById(R.id.user_info_edit_gender_man)).setChecked(true);
        }
        else {
            ((RadioButton) findViewById(R.id.user_info_edit_gender_woman)).setChecked(true);
        }
    }




    private boolean errorHandlerSignUp(){
        //error 체크 변수 처음엔 false 에러 X.
        boolean errorCheck = false;

        WithMeJni jni = new WithMeJni();
        String errMsg = "회원 정보에 관한 \n <";
        String errValidMsg = "";
        if(email.getText().toString().equals("")){
            errorCheck = true;
            errMsg = errMsg + " 이메일 ";
        }
        if(password.getText().toString().equals("")){
            errorCheck = true;
            errMsg = errMsg + " 비밀번호 ";
        }
        if(name.getText().toString().equals("")){
            errorCheck = true;
            errMsg = errMsg + " 이름 ";
        }
        if(phone.getText().toString().equals("")){
            errorCheck = true;
            errMsg = errMsg + " 휴대폰번호 ";
        }
        if(birth.getText().toString().equals("")){
            errorCheck = true;
            errMsg = errMsg + " 생년월일 ";
        }

        if( !(email.getText().toString().equals("")) && jni.isValidMail(email.getText().toString()) == 0){
            errorCheck = true;
            errValidMsg = errValidMsg + "이메일 형식이 아닙니다. \n";
        }
        if(!(phone.getText().toString().equals("")) && jni.isValidPhone(phone.getText().toString()) == 0){
            errorCheck = true;
            errValidMsg = errValidMsg + "휴대폰 번호 형식이 아닙니다. \n";
        }

        if(errorCheck){
            if(errValidMsg.equals("")){
                errMsg = errMsg + "> \n 정보를 입력 해주세요.";
                showAlert("회원 정보 부족", errMsg);
            }
            else{
                showAlert("재 입력 요청", errValidMsg);
            }
        }
        return errorCheck;
    }

    public void showAlert(String title, String message) {

        // Set alert title
        builder.setTitle(title);

        // The message
        builder.setMessage(message);
        builder.setPositiveButton("확인", null);
        // Create the alert dialog and display it
        theAlertDialog = builder.create();
        theAlertDialog.show();
    }

    public void userInfoChangeInit(){
        RadioButton gender = (RadioButton)findViewById(genderGroup.getCheckedRadioButtonId());
        User.getInstance().setMail(email.getText().toString());
        User.getInstance().setPassword(password.getText().toString());
        User.getInstance().setName(name.getText().toString());
        User.getInstance().setBirth(birth.getText().toString());
        User.getInstance().setPhone(phone.getText().toString());
        User.getInstance().setGender(gender.getText().toString());
    }

    public void editUser(View view) {
        if(errorHandlerSignUp()){
            Log.e("Sign Up Error!", "회원 가입 에러");
        }
        else {
            infoEditBtn.setClickable(false);

            RadioButton gender = (RadioButton)findViewById(genderGroup.getCheckedRadioButtonId());

            final String json = "{" +
                    "\"mail\" : \""  + email.getText().toString() + "\", " +
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
                        String responseStr = ServerManager.getInstance().post(Constants.SERVER_URL + "/user/edit/"+ User.getInstance().getId(), json);
                        if(responseStr == null){
                            handler.sendMessage(Message.obtain(handler, MSG_EDIT_ERROR, ""));
                            return;
                        }
                        Log.d("editUserResult", responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            String result = response.getString("result");
                            if(result.equals("fail")){
                                handler.sendMessage(Message.obtain(handler, MSG_EDIT_FAIL, ""));
                            }
                            else {
                                JSONObject user = new JSONObject(response.getString("user"));
                                User.getInstance().setId(user.getString("id"));
                                User.getInstance().setMail(user.getString("mail"));
                                User.getInstance().setPassword(user.getString("password"));
                                User.getInstance().setToken(user.getString("token"));
                                User.getInstance().setName(user.getString("name"));
                                User.getInstance().setBirth(user.getString("birth"));
                                User.getInstance().setPhone(user.getString("phone"));
                                User.getInstance().setGender(user.getString("gender"));
                                DatabaseLab.getInstance().loginUser();
                                handler.sendMessage(Message.obtain(handler, MSG_EDIT_SUCCESS, ""));
                                Intent resultIntent=new Intent(activity, UserInfoActivity.class);
                                activity.startActivity(resultIntent);
                            }
                        } catch (Throwable t) {
                            Log.e("createUser", t.toString());
                        }
                    }
                    else {
                        Log.e("RealPath", imageRealPath);
                        String responseStr = ServerManager.getInstance().userInfoEdit(Constants.SERVER_URL + "/user/edit/image/" + User.getInstance().getId(), json, new File(imageRealPath), email.getText().toString());
                        if(responseStr == null){
                            handler.sendMessage(Message.obtain(handler, MSG_EDIT_ERROR, ""));
                            return;
                        }
                        Log.d("createUserResult", responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            String result = response.getString("result");
                            if(result.equals("fail")){
                                handler.sendMessage(Message.obtain(handler, MSG_EDIT_FAIL, ""));
                            }
                            else {
                                JSONObject user = new JSONObject(response.getString("user"));
                                User.getInstance().setId(user.getString("id"));
                                User.getInstance().setMail(user.getString("mail"));
                                User.getInstance().setPassword(user.getString("password"));
                                User.getInstance().setToken(user.getString("token"));
                                User.getInstance().setName(user.getString("name"));
                                User.getInstance().setBirth(user.getString("birth"));
                                User.getInstance().setPhone(user.getString("phone"));
                                User.getInstance().setGender(user.getString("gender"));
                                DatabaseLab.getInstance().loginUser();
                                handler.sendMessage(Message.obtain(handler, MSG_EDIT_SUCCESS, ""));
                                Intent resultIntent=new Intent(activity, UserInfoActivity.class);
                                activity.startActivity(resultIntent);
                            }
                        } catch (Throwable t) {
                            Log.e("createUser", t.toString());
                        }
                    }

                }
            }.start();
        }
    }

    public void onBirthClicked (View v){
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        new DatePickerDialog(this, dateSetListener,
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DATE)).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            birth.setText("" + year + "." + (monthOfYear+1) +"." + dayOfMonth);
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



    //
    // 키보드 숨김 함수
    //
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(UserInfoEditActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
