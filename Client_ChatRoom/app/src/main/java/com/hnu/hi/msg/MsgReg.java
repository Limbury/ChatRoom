package com.hnu.hi.msg;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
 * MsgRegÎª×¢²áÏûÏ¢Ìå
 */
public class MsgReg extends MsgHead {
    /*
     * |nikeName(10)|pwd(10)|
     */
    private String nikeName;
    private String pwd;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNikeName() {
        return nikeName;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    @Override
    public byte[] packMessage() throws IOException {
        ByteArrayOutputStream bous = new ByteArrayOutputStream();
        DataOutputStream dous = new DataOutputStream(bous);
        packMessageHead(dous);
        writeString(dous, 10, getNikeName());
        writeString(dous, 10, getPwd());
        dous.flush();
        byte[] data = bous.toByteArray();
        return data;
    }
}
