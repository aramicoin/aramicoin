package com.dc.wallet;

import com.dc.core.global.DCCoreConfig;
import com.dc.core.net.PeerClient;
import com.dc.core.util.Handle;
import com.dc.wallet.config.WalletConfig;
import com.dc.wallet.ui.Alerts;
import com.dc.wallet.ui.MainForm;
import com.dc.wallet.ui.SplashShell;
import com.ms.libs.log.Log;
import com.ms.libs.log.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.IOException;

public class WalletApp {

    private static Log log = Logger.getLog(WalletConfig.LogName);


    static boolean isRun = true;

    public static void run() {

        Display display = Display.getDefault();

        if (Handle.isInstanceRunning()) {

            MessageBox msgBox = new MessageBox(new Shell(), SWT.OK);

            msgBox.setText("Warn");

            msgBox.setMessage("Program has been running, please don't repeat running software! \nプログラムはすでに実行して、繰り返し実行ソフトを繰り返してください！\n程式已經運行，請不要重複運行軟件！\n تم تشغيل  البرنامج  ،  الرجاء عدم تكرار  تشغيل  البرمجيات! ");
            int btnNum = msgBox.open();

            if (btnNum == SWT.OK) {
                isRun = false;
            }
            System.exit(0);
        }


        String appPath = DCCoreConfig.RootRealPath + "Wallet.exe";
        boolean appExist = new File(appPath).exists();


        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {


                {


                    PeerClient pClient = PeerClient.me().stopRun();


                    long sleep = 200;


                    long timeOut = 1000 * 60;


                    while (!pClient.isEnd()) {

                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException ie) {
                        }


                        timeOut -= sleep;
                        if (timeOut <= 0) {
                            break;
                        }
                    }
                }


                if (WalletConfig.isRestartApp && appExist) {
                    try {
                        Runtime.getRuntime().exec(appPath);
                    } catch (IOException e) {
                    }
                }

            }
        });


        WalletConfig.splashShell = new SplashShell(display);
        WalletConfig.splashShell.open();

        if (!Handle.checkVersion(WalletConfig.CheckVersionURL)) {

            MessageBox msgBox = new MessageBox(new Shell(), SWT.OK);

            msgBox.setText("Warn");

            msgBox.setMessage("This software has the new version, please download and install the latest version of the software! \nこのソフトは新しいバージョンがあります、ダウンロードして最も新版ソフトをインストールして下さい！\n此軟件已有新版本，請下載安裝最新版軟件！\n هناك  نسخة جديدة من  هذا البرنامج  ، يرجى  تحميل وتثبيت  أحدث  برامج  ! ");
            int btnNum = msgBox.open();

            if (btnNum == SWT.OK) {
                isRun = false;
                Handle.openBrowser(WalletConfig.AppSite);
            }
            System.exit(0);
        }


        if (isRun) {

            try {

                PeerClient.me().start();
            } catch (Exception e) {
                String msg = "Start P2P Network Error" + e.getMessage();
                log.error(msg, e);
                Alerts.error(msg);
            }


            try {
                MainForm window = new MainForm();
                window.open();

            } catch (Exception e) {
                log.error("Start MainForm Error", e);
            }

        }

    }


    public static void main(String[] args) {
        WalletApp.run();

    }

}
