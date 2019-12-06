package com.hnu.hi.data;

public class UserInfo {
    /*
     * 用户基本信息
     */

    private int JKNum;// 保存该用户的JK号
    private String NickName;// 保存该用户的昵称
    private String PassWord;// 保存该用户的IP
    private int Pic;

    /*
     * 用户好友信息
     */

    private byte listCount;// 保存有多少组好友
    private String ListName[];// 保存每个分组的名称
    private byte[] bodyCount;// 每组有多少个人
    private int bodyNum[][];// 每个好友的JK号
    private int bodypic[][];//好友头像
    private String nikeName[][];// 每个好友的昵称

    public byte getListCount() {
        return listCount;
    }

    public void setListCount(byte listCount) {
        this.listCount = listCount;
    }

    public String[] getListName() {
        return ListName;
    }

    public void setListName(String[] listName) {
        ListName = listName;
    }

    public byte[] getBodyCount() {
        return bodyCount;
    }

    public void setBodyCount(byte[] bodyCount) {
        this.bodyCount = bodyCount;
    }

    public int[][] getBodyNum() {
        return bodyNum;
    }

    public void setBodyNum(int[][] bodyNum) {
        this.bodyNum = bodyNum;
    }

    public String[][] getNikeName() {
        return nikeName;
    }

    public void setNikeName(String[][] nikeName) {
        this.nikeName = nikeName;
    }

    public int getJKNum() {
        return JKNum;
    }

    public void setJKNum(int jKNum) {
        JKNum = jKNum;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public boolean equals(UserInfo compare) {
        if (compare.getJKNum() == JKNum && compare.getPassWord().equals(PassWord)) {
            return true;
        }
        return false;
    }

    public int[][] getBodypic() {
        return bodypic;
    }

    public void setBodypic(int bodypic[][]) {
        this.bodypic = bodypic;
    }

    public int getPic() {
        return Pic;
    }

    public void setPic(int pic) {
        Pic = pic;
    }

}
