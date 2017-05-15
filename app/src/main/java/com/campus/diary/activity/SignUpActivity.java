package com.campus.diary.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.campus.diary.R;
import com.campus.diary.mvp.contract.SignUpContract;
import com.campus.diary.mvp.presenter.SignUpPresenter;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BaseActivity implements SignUpContract.View {
    private EditText mUsername;
    private EditText mUserPassword1;
    private EditText mUserPassword2;
    private EditText mNickName;
    private ProgressDialog progressDialog;

    private SignUpContract.Presenter signUpLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpLogic = new SignUpPresenter(SignUpActivity.this);
        mUsername = (EditText) findViewById(R.id.register_name);
        mUserPassword1 = (EditText) findViewById(R.id.register_password1);
        mUserPassword2 = (EditText) findViewById(R.id.register_password2);
        mNickName = (EditText) findViewById(R.id.nick_name);
        addTitle(R.string.login_account);
        setRightButton(R.string.sign_up, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpLogic.authority(getSignUpInfo());
            }
        });
        progressDialog = new ProgressDialog(SignUpActivity.this);
    }

    @Override
    public void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public Map<String, Object> getSignUpInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mUserName", mUsername.getText().toString());
        map.put("mUserPassword1", mUserPassword1.getText().toString());
        map.put("mUserPassword2", mUserPassword2.getText().toString());
        map.put("mNickName", mNickName.getText().toString());
        return map;
    }

    @Override
    public void showToast(String result) {
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(String msg) {
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }

    @Override
    public String getResString(@StringRes int resId) {
        return getString(resId);
    }

    @Override
    public void backSignUp() {
        setResult(RESULT_OK);
        finish();
    }
}
