package com.dc.wallet.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


public class Alerts {


    public static void info(Shell shell, String msg) {
        MessageBox msgBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
        msgBox.setText(Messages.getString("Alerts.Tip"));
        msgBox.setMessage(msg);
        msgBox.open();
    }

    public static void info(String msg) {
        info(new Shell(), msg);
    }

    public static void error(Shell shell, String msg) {
        MessageBox msgBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
        msgBox.setText(Messages.getString("Alerts.Error"));
        msgBox.setMessage(msg);
        msgBox.open();
    }

    public static void error(String msg) {
        error(new Shell(), msg);
    }

}
