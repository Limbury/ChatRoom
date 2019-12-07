package com.hnu.hi.data.model;

import com.hnu.hi.client.Client_ChatRoom;
import com.hnu.hi.data.ListInfo;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    //private Client_ChatRoom client_chatRoom;
    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
        //this.client_chatRoom = client_chatRoom;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
//    public ListInfo getListInfo(){return listInfo;}
//    public Client_ChatRoom getClient_chatRoom() {return client_chatRoom;}
}
