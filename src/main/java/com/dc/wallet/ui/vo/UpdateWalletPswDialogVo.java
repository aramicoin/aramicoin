package com.dc.wallet.ui.vo;


public class UpdateWalletPswDialogVo {

    
    private String oldPassword;

    
    private String newPassword;

    public UpdateWalletPswDialogVo() {
		super();
	}

    public UpdateWalletPswDialogVo(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public UpdateWalletPswDialogVo setNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public UpdateWalletPswDialogVo setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        return this;
    }
}
