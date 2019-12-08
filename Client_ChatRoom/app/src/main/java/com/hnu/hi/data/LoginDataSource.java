package com.hnu.hi.data;

import android.util.Log;

import com.hnu.hi.client.Client_ChatRoom;
import com.hnu.hi.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private static final String TAG = "LoginDataSource";
    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
//            要从Android模拟器访问PC localhost，请使用10.0.2.2而不是127.0.0.1。 localhost或127.0.0.1是指模拟设备本身，而不是运行模拟器的主机。
//            参考：http://developer.android.com/tools/devices/emulator.html#networkaddresses
//
//            对于Genymotion使用：10.0.3.2而不是10.0.2.2
            Client_ChatRoom client_chatRoom = Client_ChatRoom.getClient_chatRoom();
            if(client_chatRoom.ConnectServer()){
                if(client_chatRoom.Login(Integer.parseInt(username),password) == 0){//登录成功
                    Log.d(TAG, "login: client_chatRoom.Login(Integer.parseInt(username),password) == 0");
                    //listInfo = client_chatRoom.getlist();
                    //username = listInfo.getNickName();
                }

                else {
                    Log.d(TAG, "login: 登录失败");
                    throw new LoginException("登录失败");
                }
            }
            else {
                Log.d(TAG, "login: 服务器连接失败");
                throw new LoginException("服务器连接失败");
            }
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            username);
//            if (!username.equals("Gol_Light")){
//                throw new LoginException("用户不是Gol_Light");
//            }
            Log.d(TAG, "login: 验证成功");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
