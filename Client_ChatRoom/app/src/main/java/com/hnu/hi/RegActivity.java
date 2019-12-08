package com.hnu.hi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hnu.hi.client.Client_ChatRoom;
import com.hnu.hi.ui.login.LoginActivity;
import com.hnu.hi.ui.login.LoginFormState;

public class RegActivity extends AppCompatActivity {
    private static final String TAG = "RegActivity";
    private MutableLiveData<LoginFormState> regFormState = new MutableLiveData<>();
    private Client_ChatRoom client_chatRoom = Client_ChatRoom.getClient_chatRoom();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        final EditText reg_usernameEditText = findViewById(R.id.reg_username);
        final EditText reg_passwordEditText = findViewById(R.id.reg_password);
        final Button regButton = findViewById(R.id.reg_button);
        final ProgressBar loadingProgressBar = findViewById(R.id.reg_loading);

        regFormState.observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState regFormState) {
                if (regFormState == null) {
                    return;
                }
                regButton.setEnabled(regFormState.isDataValid());
                if (regFormState.getUsernameError() != null) {
                    reg_usernameEditText.setError(getString(regFormState.getUsernameError()));
                }
                if (regFormState.getPasswordError() != null) {
                    reg_passwordEditText.setError(getString(regFormState.getPasswordError()));
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            Integer is = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                regDataChanged(reg_usernameEditText.getText().toString(),
                        reg_passwordEditText.getText().toString());
            }
        };
        reg_usernameEditText.addTextChangedListener(afterTextChangedListener);
        reg_passwordEditText.addTextChangedListener(afterTextChangedListener);


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: click");
                if(client_chatRoom.ConnectServer()){
                    if(client_chatRoom.Reg(reg_usernameEditText.getText().toString(),reg_passwordEditText.getText().toString())){
                        Toast.makeText(RegActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                        intent.putExtra("uid","123457");
                        Log.d(TAG, "onClick: 返回登陆界面列表");
                        startActivity(intent);
                    }
                }
                else {
                    Log.d(TAG, "onClick: 服务器连接失败");
                }

            }
        });


    }


    public void regDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            regFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            regFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            regFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
