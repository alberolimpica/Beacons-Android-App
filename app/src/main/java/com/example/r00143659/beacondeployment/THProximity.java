package com.example.r00143659.beacondeployment;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class THProximity extends RealmObject {

    public static final int NONE = -1;
    public static final int GREEN = 2;
    public static final int YELLOW = 1;
    public static final int RED = 0;

    @PrimaryKey
    private String id;
    private @Status int status;

    public THProximity(){
        status = NONE;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        if(this.status > status)
            return;

        this.status = status;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE, GREEN, YELLOW, RED})
    public @interface Status{}
}
