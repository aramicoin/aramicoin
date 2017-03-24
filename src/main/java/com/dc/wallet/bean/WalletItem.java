package com.dc.wallet.bean;

import com.dc.core.base.BaseModel;

import java.io.Serializable;
import java.util.Date;


public class WalletItem extends BaseModel implements Serializable, Cloneable {


    private String address;


    private String label;


    private String filePath;


    private Date addTime;

    public String getAddress() {
        return address;
    }

    public WalletItem setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public WalletItem setLabel(String label) {
        this.label = label;
        return this;
    }

    public Date getAddTime() {
        return addTime;
    }

    public WalletItem setAddTime(Date addTime) {
        this.addTime = addTime;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Class<?> getModelClazz() {
        return this.getClass();
    }


}
