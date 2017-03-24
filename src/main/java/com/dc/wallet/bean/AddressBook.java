package com.dc.wallet.bean;

import com.dc.core.base.BaseModel;

import java.io.Serializable;
import java.util.Date;


public class AddressBook extends BaseModel implements Serializable, Cloneable {


    private String address;


    private String label;


    private Date addTime;

    public String getAddress() {
        return address;
    }

    public AddressBook setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public AddressBook setLabel(String label) {
        this.label = label;
        return this;
    }

    public Date getAddTime() {
        return addTime;
    }

    public AddressBook setAddTime(Date addTime) {
        this.addTime = addTime;
        return this;
    }

    @Override
    public Class<?> getModelClazz() {
        return this.getClass();
    }


}
