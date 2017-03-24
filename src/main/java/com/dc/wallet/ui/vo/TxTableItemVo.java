package com.dc.wallet.ui.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.dc.core.transaction.bean.Tx;


public class TxTableItemVo {

	
	private String id;

	
	private String direct;

	
	private String txTime;

	
	private String money;

	
	private String sendAddress;

	
	private String receiveAddress;

	private Tx tx;

	public TxTableItemVo() {
	}

	public String getId() {
		return id;
	}

	public TxTableItemVo setId(String id) {
		this.id = id;
		return this;
	}

	public String getDirect() {
		return direct;
	}

	public TxTableItemVo setDirect(String direct) {
		this.direct = direct;
		return this;
	}

	public String getTxTime() {
		return txTime;
	}

	public TxTableItemVo setTxTime(String txTime) {
		this.txTime = txTime;
		return this;
	}

	public String getMoney() {
		return money;
	}

	public TxTableItemVo setMoney(String money) {
		this.money = money;
		return this;
	}

	public String getSendAddress() {
		return sendAddress;
	}

	public TxTableItemVo setSendAddress(String SendAddress) {
		this.sendAddress = SendAddress;
		return this;
	}

	public String getReceiveAddress() {
		return receiveAddress;
	}

	public TxTableItemVo setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
		return this;
	}

	public Tx getTx() {
		return tx;
	}

	public void setTx(Tx tx) {
		this.tx = tx;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
