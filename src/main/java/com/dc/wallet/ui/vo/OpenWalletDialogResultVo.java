package com.dc.wallet.ui.vo;


public class OpenWalletDialogResultVo {

    
    private String password;

    
    private String label;

    public OpenWalletDialogResultVo() {
		super();
	}

	public OpenWalletDialogResultVo(String label, String password) {
        this.label = label;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public OpenWalletDialogResultVo setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public OpenWalletDialogResultVo setLabel(String label) {
        this.label = label;
        return this;
    }
}
