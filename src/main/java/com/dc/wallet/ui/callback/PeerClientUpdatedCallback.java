package com.dc.wallet.ui.callback;

import com.dc.core.global.DCCoreConfig;
import com.dc.core.net.callback.PeerClientUpdatedCallBackFactory;
import com.dc.wallet.ui.MainForm;
import com.ms.libs.log.Log;
import com.ms.libs.log.Logger;

public class PeerClientUpdatedCallback implements PeerClientUpdatedCallBackFactory {

    private static Log log = Logger.getLog(DCCoreConfig.LogName);

    private MainForm mainForm;

    public PeerClientUpdatedCallback(Object formObject) {
        mainForm = (MainForm) formObject;
    }

    @Override
    public void refreshData() {


        mainForm.refreshData();

    }

}
