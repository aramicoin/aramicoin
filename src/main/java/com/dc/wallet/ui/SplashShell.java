package com.dc.wallet.ui;

import com.dc.core.global.DCCoreConfig;
import com.dc.core.ui.FormsUtil;
import com.dc.wallet.config.WalletConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class SplashShell extends Shell {


    public SplashShell(Display display) {
        super(display, SWT.NONE);
        createContents();
    }


    protected void createContents() {
        setImage(SWTResourceManager.getImage(MainForm.class, DCCoreConfig.LogoFile));


        setText(DCCoreConfig.AppName);


        FormsUtil.addForm(SplashShell.class, this);

        Image image = SWTResourceManager.getImage(SplashShell.class, "/com/dc/wallet/ui/assets/splash.png");


        Label lblNewLabel = new Label(this, SWT.NONE);
        lblNewLabel.setImage(image);
        lblNewLabel.pack();


        this.pack();


        setLocation(Display.getCurrent().getClientArea().width / 2 - getShell().getSize().x / 2, Display.getCurrent().getClientArea().height / 2 - getSize().y / 2);

    }

    @Override
    protected void checkSubclass() {

    }


    public void exitBydelayed(int delayed, Shell showShell) {
        if (this != null && !this.isDisposed()) {


            new Thread() {
                public void run() {

                    while (true) {
                        if (System.currentTimeMillis() - WalletConfig.AppStartTime >= delayed) {
                            Display.getDefault().syncExec(new Runnable() {
                                @Override
                                public void run() {

                                    showShell.open();
                                    showShell.layout();


                                    dispose();
                                }
                            });


                            break;
                        }

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }.start();

        }
    }


    public void exit() {
        if (this != null && !this.isDisposed()) {

            dispose();
        }
    }


}
