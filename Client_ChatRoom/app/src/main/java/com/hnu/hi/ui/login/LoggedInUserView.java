package com.hnu.hi.ui.login;

import com.hnu.hi.client.Client_ChatRoom;
import com.hnu.hi.data.ListInfo;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
//    private ListInfo listInfo;
    //... other data fields that may be accessible to the UI
    //private Client_ChatRoom client_chatRoom;
    LoggedInUserView(String displayName) {
        this.displayName = displayName;
        //this.client_chatRoom = client_chatRoom;
    }

    String getDisplayName() {
        return displayName;
    }

//    ListInfo getListInfo(){return listInfo;}
//
//    Client_ChatRoom getClient_chatRoom(){return client_chatRoom;}
}
