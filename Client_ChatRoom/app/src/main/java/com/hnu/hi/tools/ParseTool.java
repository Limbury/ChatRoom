package com.hnu.hi.tools;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.hnu.hi.msg.MsgAddFriend;
import com.hnu.hi.msg.MsgAddFriendResp;
import com.hnu.hi.msg.MsgChatText;
import com.hnu.hi.msg.MsgHead;
import com.hnu.hi.msg.MsgLogin;
import com.hnu.hi.msg.MsgLoginResp;
import com.hnu.hi.msg.MsgReg;
import com.hnu.hi.msg.MsgRegResp;
import com.hnu.hi.msg.MsgTeamList;

public class ParseTool {

    private static String readString(DataInputStream dins, int len) throws IOException {
        byte[] data = new byte[len];
        dins.readFully(data);
        return new String(data).trim();
    }


    public static MsgHead parseMsg(byte[] data) throws IOException {
        int totalLen = data.length + 4;
        ByteArrayInputStream bins = new ByteArrayInputStream(data);
        DataInputStream dins = new DataInputStream(bins);
        byte msgtype = dins.readByte();
        int dest = dins.readInt();
        int src = dins.readInt();
        if (msgtype == 0x01) {
            String nikeName = readString(dins, 10);
            String pwd = readString(dins, 10);
            MsgReg mr = new MsgReg();
            mr.setTotalLen(totalLen);
            mr.setType(msgtype);
            mr.setDest(dest);
            mr.setSrc(src);
            mr.setNikeName(nikeName);
            mr.setPwd(pwd);
            return mr;
        }

        else if (msgtype == 0x11) {
            byte state = dins.readByte();
            MsgRegResp mrr = new MsgRegResp();
            mrr.setTotalLen(totalLen);
            mrr.setType(msgtype);
            mrr.setDest(dest);
            mrr.setSrc(src);
            mrr.setState(state);
            return mrr;
        }

        else if (msgtype == 0x02) {
            String pwd = readString(dins, 10);
            MsgLogin mli = new MsgLogin();
            mli.setTotalLen(totalLen);
            mli.setType(msgtype);
            mli.setDest(dest);
            mli.setSrc(src);
            mli.setPwd(pwd);
            return mli;
        }

        else if (msgtype == 0x22) {
            byte state = dins.readByte();
            MsgLoginResp mlr = new MsgLoginResp();
            mlr.setTotalLen(totalLen);
            mlr.setType(msgtype);
            mlr.setDest(dest);
            mlr.setSrc(src);
            mlr.setState(state);
            return mlr;
        }

        else if (msgtype == 0x03) {
            int i, j;

            String UserName = readString(dins, 10);
            int pic = dins.readInt();
            byte listCount = dins.readByte();
            String listName[] = new String[listCount];
            byte bodyCount[] = new byte[listCount];

            int bodyNum[][];
            bodyNum = new int[listCount][];

            int bodyPic[][];
            bodyPic = new int[listCount][];

            String nikeName[][];
            nikeName = new String[listCount][];

            byte bodyState[][];
            bodyState = new byte[listCount][];

            for (i = 0; i < listCount; i++) {
                listName[i] = readString(dins, 10);
                bodyCount[i] = dins.readByte();

                bodyNum[i] = new int[bodyCount[i]];
                bodyPic[i] = new int[bodyCount[i]];
                nikeName[i] = new String[bodyCount[i]];
                bodyState[i] = new byte[bodyCount[i]];

                for (j = 0; j < bodyCount[i]; j++) {
                    bodyNum[i][j] = dins.readInt();
                    bodyPic[i][j] = dins.readInt();
                    nikeName[i][j] = readString(dins, 10);
                    bodyState[i][j] = dins.readByte();
                    System.out.println(bodyNum[i][j]+" "+bodyPic[i][j]+" "+nikeName[i][j]+" "+bodyState[i][j]);
                }

            }


            MsgTeamList mtl = new MsgTeamList();
            mtl.setUserName(UserName);
            mtl.setPic(pic);
            mtl.setTotalLen(totalLen);
            mtl.setType(msgtype);
            mtl.setDest(dest);
            mtl.setSrc(src);
            mtl.setListCount(listCount);
            mtl.setListName(listName);
            mtl.setBodyCount(bodyCount);
            mtl.setBodyNum(bodyNum);
            mtl.setBodyPic(bodyPic);
            mtl.setNikeName(nikeName);
            mtl.setBodyState(bodyState);

            return mtl;

        }

        else if (msgtype == 0x04) {
            MsgChatText mct = new MsgChatText();
            String msgText = readString(dins, totalLen-13);
            mct.setTotalLen(totalLen);
            mct.setType(msgtype);
            mct.setDest(dest);
            mct.setSrc(src);
            mct.setMsgText(msgText);

            return mct;
        }

        else if (msgtype == 0x05){
            MsgAddFriend maf = new MsgAddFriend();
            int add_id = dins.readInt();
            String list_name = readString(dins, totalLen - 17);
            maf.setTotalLen(totalLen);
            maf.setType(msgtype);
            maf.setDest(dest);
            maf.setSrc(src);
            maf.setAdd_ID(add_id);
            maf.setList_name(list_name);
            return maf;
        }

        else if (msgtype == 0x55){
            MsgAddFriendResp mafr = new MsgAddFriendResp();
            byte state = dins.readByte();
            mafr.setTotalLen(totalLen);
            mafr.setType(msgtype);
            mafr.setDest(dest);
            mafr.setSrc(src);
            mafr.setState(state);
            return mafr;
        }

        return null;

    }
}
