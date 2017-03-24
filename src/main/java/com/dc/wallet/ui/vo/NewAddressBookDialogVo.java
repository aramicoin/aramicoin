package com.dc.wallet.ui.vo;


public class NewAddressBookDialogVo {

    
    private String address;

    
    private String label;

    public NewAddressBookDialogVo(String address, String label) {
        this.address = address;
        this.label = label;
    }

    public NewAddressBookDialogVo() {
		super();
	}

    public String getAddress() {
        return address;
    }

    public NewAddressBookDialogVo setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public NewAddressBookDialogVo setLabel(String label) {
        this.label = label;
        return this;
    }
}
