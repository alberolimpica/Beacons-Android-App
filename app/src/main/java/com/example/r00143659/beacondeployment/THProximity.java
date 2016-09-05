package com.example.r00143659.beacondeployment;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class THProximity extends RealmObject {

    // Constants

    public static final int NONE = -1;
    public static final int GREEN = 2;
    public static final int YELLOW = 1;
    public static final int RED = 0;

    // Properties

    @PrimaryKey
    private String id;
    private @Status int status;

    // Constructor

    public THProximity(){
        status = NONE;
    }

    // Getters and Setters

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

    // Interface

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE, GREEN, YELLOW, RED})
    public @interface Status{}
}
