package com.dc.wallet.ui.service;

import com.dc.core.global.DCCoreConfig;
import com.dc.core.transaction.TransactionType;
import com.dc.core.transaction.bean.Tx;
import com.dc.core.util.Util;
import com.dc.core.wallet.WalletService;
import com.dc.core.wallet.bean.Wallet;
import com.dc.wallet.bean.AddressBook;
import com.dc.wallet.bean.WalletItem;
import com.dc.wallet.config.WalletConfig;
import com.dc.wallet.dao.AddressBookDAO;
import com.dc.wallet.dao.WalletItemDAO;
import com.dc.wallet.ui.vo.TxTableItemVo;
import com.google.common.collect.Lists;
import com.ms.libs.log.Log;
import com.ms.libs.log.Logger;
import com.ms.libs.util.DateUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class DataService {

    private static Log log = Logger.getLog(WalletConfig.LogName);


    private static DataService me;
    private static Object lock = new Object();

    private DataService() {
    }

    public static DataService me() {
        if (me == null) {
            synchronized (lock) {
                if (me == null) {
                    me = new DataService();
                }
            }
        }
        return me;
    }


    private List<Wallet> walletCacheList = Lists.newArrayList();


    public int getWalletTotal() {
        return walletCacheList.size();
    }

    public List<Wallet> getWalletCacheList() {
        return walletCacheList;
    }


    public boolean isExist(Wallet w) {
        String walletAddress = w.getWalletAddress();

        return isExist(walletAddress);
    }


    public boolean isExist(String walletAddress) {
        for (Wallet w : walletCacheList) {
            if (w.getWalletAddress().equals(walletAddress)) {
                return true;
            }
        }

        return false;
    }


    public void addToWalletCache(Wallet w) {

        if (isExist(w)) {
            return;
        }

        walletCacheList.add(w);
    }


    public void addToWalletCache(List walletList) {
        walletCacheList.addAll(walletList);
    }


    public void removeFromWalletCache(String walletAddress) {

        walletAddress = walletAddress.trim();

        if (!isExist(walletAddress)) {
            return;
        }


        Iterator<Wallet> it = walletCacheList.iterator();
        while (it.hasNext()) {
            Wallet w = it.next();
            if (w.getWalletAddress().equals(walletAddress))
                it.remove();
        }
    }


    public Wallet getWalletObjByWalletAddress(String walletAddress) {

        Wallet result = null;


        Iterator<Wallet> it = walletCacheList.iterator();
        while (it.hasNext()) {
            Wallet w = it.next();
            if (w.getWalletAddress().equals(walletAddress)) {
                result = w;
            }
        }

        return result;
    }


    public void addWalletItemRow(Table table, WalletItem item) {

        Wallet w = getWalletObjByWalletAddress(item.getAddress());
        if (w != null) {
            long DCTotal = WalletService.getCanUseDCTotal(w.getWalletAddress());
            TableItem ti = new TableItem(table, SWT.NONE);
            int i = 0;
            ti.setText(i++, item.getLabel());
            ti.setText(i++, item.getAddress());
            ti.setText(i++, Util.smartFormatMoney(DCTotal));
            ti.setText(i++, DateUtil.formatByDateTime(item.getAddTime()));
        }
    }


    public void addAddressBookRow(Table table, AddressBook book) {
        TableItem ti = new TableItem(table, SWT.NONE);
        int i = 0;
        ti.setText(i++, book.getLabel());
        ti.setText(i++, book.getAddress());
    }


    public void addWalletItemRowList(Table table, List<WalletItem> itemList) {
        for (WalletItem item : itemList) {
            if (item != null)
                addWalletItemRow(table, item);
        }
    }


    public void addAddressBookRowList(Table table, List<AddressBook> itemList) {
        for (AddressBook item : itemList) {
            addAddressBookRow(table, item);
        }
    }


    public void addTxTableItemRow(Table table, TxTableItemVo vo, boolean hasAddress) {
        TableItem ti = new TableItem(table, SWT.NONE);
        int i = 0;
        ti.setText(i++, vo.getId());
        ti.setText(i++, vo.getTxTime());
        if (hasAddress) {

            int TxType = vo.getTx().getTxType();

            if (TxType == TransactionType.TYPE_COINBASE) {
                ti.setText(i++, "Miner Block");
            } else if (TxType == TransactionType.TYPE_COINBASE_HM) {
                ti.setText(i++, "Miner HMBlock");
            } else {
                ti.setText(i++, vo.getSendAddress());
            }
            ti.setText(i++, vo.getReceiveAddress());
        }
        ti.setText(i++, vo.getMoney());
    }


    public void addTxTableItemRowList(Table table, List<Tx> txList, boolean hasAddress) {

        WalletItemDAO dao = WalletItemDAO.me();
        List<String> myAddrList = dao.findAllAddress();

        int i = 1;
        List<TxTableItemVo> txTableItemVoList = Lists.newArrayList();


        for (Tx tx : txList) {

            TxTableItemVo txTableItemVo = new TxTableItemVo();


            String senderWA = tx.getSenderWA();

            String recipientWA = tx.getRecipientWA();


            boolean isSender = false;

            if (myAddrList.contains(senderWA)) {
                isSender = true;
            }


            String symbol = "";

            if (tx.getTxType() == TransactionType.TYPE_PAYMENT_CHANGE) {
                symbol = "+";
            } else if (tx.getTxType() == TransactionType.TYPE_COINBASE || tx.getTxType() == TransactionType.TYPE_COINBASE_HM) {
                symbol = "+";
            } else if (tx.getTxType() == TransactionType.TYPE_PAYMENT) {
                if (isSender) {
                    symbol = "-";
                } else {
                    symbol = "+";
                }
            }

            txTableItemVo.setId(String.valueOf(i++));
            txTableItemVo.setSendAddress(senderWA);
            txTableItemVo.setReceiveAddress(recipientWA);
            txTableItemVo.setMoney(symbol + " " + String.valueOf(Util.smartFormatMoney(tx.getAmountDCT())));
            txTableItemVo.setTxTime(DateUtil.formatByDateTime(new Date(tx.getTimestamp() * 1000)));
            txTableItemVo.setTx(tx);


            txTableItemVoList.add(txTableItemVo);
        }

        for (TxTableItemVo vo : txTableItemVoList) {
            addTxTableItemRow(table, vo, hasAddress);
        }
    }


    public void randomWalletItemRow(Table table) {
        String dat = RandomStringUtils.randomAlphabetic(6);

        TableItem ti = new TableItem(table, SWT.NONE);
        ti.setText(0, dat);
        ti.setText(1, dat);
        ti.setText(2, DateUtil.formatByDateTime(new Date()));
    }


    public String moneyAndUnit(long money) {

        return Util.smartFormatMoney(money) + " " + DCCoreConfig.UnitName;
    }


    public long countCanUseMoneyVal() {
        long rtv = 0;

        for (Wallet item : walletCacheList) {
            String walletAddress = item.getWalletAddress();

            rtv += WalletService.getCanUseDCTotal(walletAddress);
        }

        return rtv;
    }


    public long countWaitingMoneyVal() {

        long refreshFundsTotal = 0;
        for (Wallet item : walletCacheList) {
            refreshFundsTotal += WalletService.countUnBlockAndNotChangeDCTotal(item.getWalletAddress());
        }

        return refreshFundsTotal;
    }


    public long countTotalMoney(long countCanUseMoneyVal, long countWaitingMoneyVal) {
        return countCanUseMoneyVal + countWaitingMoneyVal;
    }


    public void refreshWalletAddressComboOfSendPanel(Combo walletAddressCombo) {
        if (walletAddressCombo == null) {
            return;
        }
        walletAddressCombo.removeAll();
        for (Wallet item : walletCacheList) {
            walletAddressCombo.add(item.getWalletAddress());
        }


        walletAddressCombo.select(walletCacheList.size() - 1);
    }


    public boolean saveAddressBookIfNecessary(String address, String label) {
        if (StringUtils.isBlank(label) || StringUtils.isBlank(address)) {
            return false;
        }

        AddressBookDAO dao = new AddressBookDAO();


        if (dao.isExistByAddress(address)) {
            AddressBook addressBook = new AddressBook();
            addressBook.setAddress(address);
            addressBook.setLabel(label);
            addressBook.setAddTime(new Date());
            return dao.add(addressBook);
        }

        return true;
    }



}
