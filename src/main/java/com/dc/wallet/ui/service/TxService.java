package com.dc.wallet.ui.service;

import com.dc.core.transaction.bean.Tx;
import com.dc.core.transaction.dao.TxDAO;
import com.dc.core.util.Colls;
import com.google.common.collect.Lists;

import java.util.List;


public class TxService {


    private static TxService me;
    private static Object lock = new Object();

    private TxService() {
    }

    public static TxService me() {
        if (me == null) {
            synchronized (lock) {
                if (me == null) {
                    me = new TxService();
                }
            }
        }
        return me;
    }


    public List<Tx> getMyAllTx(List<String> waList, int size) {

        List<Tx> result = Lists.newArrayList();

        for (String address : waList) {
            List<Tx> myTxList = TxDAO.me().getMyTxList(address, size);

            if (Colls.isNotEmpty(myTxList)) {
                result.addAll(myTxList);
            }
        }

        return result;
    }
}
