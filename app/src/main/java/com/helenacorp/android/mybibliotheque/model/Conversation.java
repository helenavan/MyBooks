package com.helenacorp.android.mybibliotheque.model;

import com.helenacorp.android.mybibliotheque.UserList;

import java.util.Date;

/**
 * Created by helena on 21/03/2018.
 */

public class Conversation {
    /** The Constant STATUS_SENDING. */
    public static final int STATUS_SENDING = 0;

    /** The Constant STATUS_SENT. */
    public static final int STATUS_SENT = 1;

    /** The Constant STATUS_FAILED. */
    public static final int STATUS_FAILED = 2;

    /** The msg. */
    private String msg;

    /** The status. */
    private int status = STATUS_SENT;

    /** The date. */
    private Date date;

    /** The sender. */
    private String sender;

    /** The receiver */
    private String receiver;

    /** The photo url. */
    private String photoUrl;

    public Conversation(String msg, Date date, String sender, String receiver, String photoUrl) {
        this.msg = msg;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.photoUrl = photoUrl;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public boolean isSent()
    {
        return UserList.user.getIdUser().contentEquals(sender);
    }

    public Date getDate() {

        return date;
    }


    public void setDate(Date date)
    {
        this.date = date;
    }


    public String getReceiver()
    {
        return receiver;
    }

    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getPhotoUrl() { return this.photoUrl; }


}
