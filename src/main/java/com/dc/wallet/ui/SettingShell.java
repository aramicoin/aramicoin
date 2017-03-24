package com.dc.wallet.ui;

import com.dc.core.global.DCCoreConfig;
import com.dc.wallet.config.WalletConfig;
import com.ms.libs.beans.Field;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;

import java.io.File;

public class SettingShell extends Shell {


    public static void main(String args[]) {

        try {
            Display display = Display.getDefault();
            SettingShell shell = new SettingShell(display);
            shell.open();
            shell.layout();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public SettingShell(Display display) {
        super(display, SWT.SHELL_TRIM);

        TabFolder tabFolder = new TabFolder(this, SWT.NONE);
        tabFolder.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        tabFolder.setBounds(10, 10, 534, 279);

        TabItem tbtmm = new TabItem(tabFolder, SWT.NONE);
        tbtmm.setText(Messages.getString("SettingShell.a"));

        Composite composite = new Composite(tabFolder, SWT.NONE);
        tbtmm.setControl(composite);

        Label lblLanguage = new Label(composite, SWT.NONE);
        lblLanguage.setBounds(10, 13, 76, 17);
        lblLanguage.setText(Messages.getString("SettingShell.b"));

        Combo combo = new Combo(composite, SWT.NONE);
        combo.setBounds(92, 10, 215, 25);


        for (Field langField : WalletConfig.LanguageList) {
            combo.add(langField.getStringName());
        }


        int currLangIndex = WalletConfig.getLanguageIndex(WalletConfig.LangaugeName);
        combo.select(currLangIndex);

        System.out.println(WalletConfig.LangaugeName);
        System.out.println(currLangIndex);


        Button btnNewButton = new Button(this, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {

                int sIndex = combo.getSelectionIndex();
                Field langField = WalletConfig.LanguageList.get(sIndex);
                String newLangValue = langField.getStringValue();

                if (!WalletConfig.LangaugeName.equals(newLangValue)) {

                    DCCoreConfig.saveConfigValue("LangaugeName", newLangValue);


                    WalletConfig.isRestartApp = true;


                    String appPath = DCCoreConfig.RootRealPath + "Wallet.exe";
                    boolean appExist = new File(appPath).exists();


                    if (appExist) {
                        System.exit(0);
                    } else {
                        getShell().dispose();
                    }
                }
            }
        });
        btnNewButton.setBounds(378, 295, 80, 27);
        btnNewButton.setText(Messages.getString("SettingShell.c"));

        Button btnc = new Button(this, SWT.NONE);
        btnc.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                getShell().dispose();
            }
        });
        btnc.setText(Messages.getString("SettingShell.d"));
        btnc.setBounds(464, 295, 80, 27);
        createContents();
    }


    protected void createContents() {
        setText(Messages.getString("SettingShell.e"));
        setSize(570, 370);
        setImage(SWTResourceManager.getImage(MainForm.class, DCCoreConfig.LogoFile));

        setLocation(Display.getCurrent().getClientArea().width / 2 - getShell().getSize().x / 2, Display.getCurrent().getClientArea().height / 2 - getSize().y / 2);
    }

    @Override
    protected void checkSubclass() {

    }
}
