package com.dc.wallet.ui;

import com.dc.core.exception.WrongPasswordException;
import com.dc.core.global.DCCoreConfig;
import com.dc.core.net.PeerClient;
import com.dc.core.transaction.dao.TxDAO;
import com.dc.core.ui.CoreUtil;
import com.dc.core.ui.FormsUtil;
import com.dc.core.util.Jsons;
import com.dc.core.util.Util;
import com.dc.core.wallet.WalletService;
import com.dc.core.wallet.bean.Wallet;
import com.dc.wallet.bean.WalletItem;
import com.dc.wallet.config.WalletConfig;
import com.dc.wallet.dao.WalletItemDAO;
import com.dc.wallet.ui.callback.PeerClientUpdatedCallback;
import com.dc.wallet.ui.dialog.AddressBookListDialog;
import com.dc.wallet.ui.dialog.OpenWalletDialog;
import com.dc.wallet.ui.dialog.UpdateWalletPswDialog;
import com.dc.wallet.ui.service.DataService;
import com.dc.wallet.ui.service.TxService;
import com.dc.wallet.ui.vo.OpenWalletDialogResultVo;
import com.dc.wallet.ui.vo.UpdateWalletPswDialogVo;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.ms.libs.log.Log;
import com.ms.libs.log.Logger;
import com.ms.libs.util.CommUtil;
import com.ms.libs.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.prefs.Preferences;

public class MainForm {

    private static Log log = Logger.getLog(WalletConfig.LogName);

    protected Display display;
    protected Shell shell;
    protected SettingShell settingShell;


    protected Label canUseMoneyValLabel;
    protected Label waitingMoneyValLabel;
    protected Label totalMoneyValLabel;


    protected ToolItem sendTBI;
    protected ToolItem receiveTBI;
    protected ToolItem txListTBI;
    protected ToolItem summaryTBI;


    protected Combo walletAddressCombo;

    protected Composite compContainer;
    protected Composite summaryComp;
    protected Composite sendComp;
    protected Composite receiveComp;
    protected Composite tradeListComp;
    private Text toWalletAddressText;
    private Table walletItemTable;
    private Table txListTable;


    protected WalletItemDAO walletItemDAO = new WalletItemDAO();
    protected TxDAO txDAO = new TxDAO();
    private Text labelText;
    private Text moneyText;
    private Table recentTxListTable;


    List<WalletItem> walletItemList;

    public MainForm() {

        PeerClient.me().setUpdatedCallBack(new PeerClientUpdatedCallback(this));
    }


    public void open() {
        display = Display.getDefault();
        createContents();


        WalletConfig.splashShell.exitBydelayed(1000 * 2, shell);


        openWallets();


        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }


    private void refreshMoneyValOfSummaryPanel() {

        long countCanUseMoneyVal = DataService.me().countCanUseMoneyVal();

        long countWaitingMoneyVal = DataService.me().countWaitingMoneyVal();

        long countTotalMoneyVal = DataService.me().countTotalMoney(countCanUseMoneyVal, countWaitingMoneyVal);

        DataService ds = DataService.me();


        display.syncExec(new Runnable() {
            @Override
            public void run() {

                canUseMoneyValLabel.setText(ds.moneyAndUnit(countCanUseMoneyVal));


                waitingMoneyValLabel.setText(ds.moneyAndUnit(countWaitingMoneyVal));


                totalMoneyValLabel.setText(ds.moneyAndUnit(countTotalMoneyVal));
            }
        });

    }


    public void refreshData() {


        List<WalletItem> walletItemList = walletItemDAO.findAll();


        List<Wallet> walletList = DataService.me().getWalletCacheList();


        ListIterator<WalletItem> listIter = walletItemList.listIterator();
        while (listIter.hasNext()) {
            WalletItem wi = listIter.next();


            boolean isExist = false;
            for (Wallet wallet : walletList) {
                if (wi.getAddress().equals(wallet.getWalletAddress())) {
                    isExist = true;
                    break;
                }
            }

            if (!isExist) {
                listIter.remove();
            }
        }


        refreshMoneyValOfSummaryPanel();

        display.syncExec(new Runnable() {
            @Override
            public void run() {


                recentTxListTable.removeAll();
                DataService.me().addTxTableItemRowList(recentTxListTable, TxService.me().getMyAllTx(Lists.transform(walletItemList, new Function<WalletItem, String>() {
                    @Override
                    public String apply(WalletItem input) {
                        return input.getAddress();
                    }
                }), 17), false);


                walletItemTable.removeAll();
                DataService.me().addWalletItemRowList(walletItemTable, walletItemList);


                txListTable.removeAll();
                DataService.me().addTxTableItemRowList(txListTable, TxService.me().getMyAllTx(Lists.transform(walletItemList, new Function<WalletItem, String>() {
                    @Override
                    public String apply(WalletItem input) {
                        return input.getAddress();
                    }
                }), 1000), true);
            }
        });
    }


    private void openWallets() {


        walletItemList = walletItemDAO.findAll();

        for (WalletItem wi : walletItemList) {
            File wFile = new File(wi.getFilePath());
            if (wFile.exists()) {


                OpenWalletDialog openWalletDialog = new OpenWalletDialog(shell);

                openWalletDialog.setDefaultLabelText(wi.getLabel());

                openWalletDialog.setDefaultLabelEnabled(false);

                String resultJson = String.valueOf(openWalletDialog.open());
                OpenWalletDialogResultVo vo = Jsons.toObject(resultJson, OpenWalletDialogResultVo.class);


                String password = vo.getPassword();

                if (!password.equals("")) {

                    Wallet w = null;
                    try {

                        w = WalletService.readWalletFile(wi.getFilePath(), password);


                        DataService.me().addToWalletCache(w);


                        DataService.me().refreshWalletAddressComboOfSendPanel(walletAddressCombo);


                        refreshData();

                    } catch (WrongPasswordException wpe) {
                        log.error("Error", wpe);
                    }
                }
            }
        }
    }


    public String formatMoney(double money) {
        return money + " " + DCCoreConfig.UnitName;
    }


    private void saveNewWallet(Wallet wallet, String label, String walletPath) {

        DataService.me().addToWalletCache(wallet);


        String walletAddress = wallet.getWalletAddress();


        WalletItem walletItem = new WalletItem();
        walletItem.setAddress(walletAddress);
        walletItem.setAddTime(new Date());
        walletItem.setLabel(label);
        walletItem.setFilePath(walletPath);


        if (!walletItemDAO.isExistByAddress(walletAddress)) {
            boolean add = walletItemDAO.add(walletItem);


            if (add) {

                DataService.me().addWalletItemRow(walletItemTable, walletItem);
            }
        }


        DataService.me().refreshWalletAddressComboOfSendPanel(walletAddressCombo);


        refreshMoneyValOfSummaryPanel();
    }


    protected void createContents() {

        final StackLayout stackLayout = new StackLayout();

        shell = new Shell(SWT.CLOSE | SWT.MIN);
        shell.setImage(SWTResourceManager.getImage(MainForm.class, DCCoreConfig.LogoFile));
        shell.setSize(850, 600);
        shell.setText(DCCoreConfig.AppName + " " + Messages.getString("MainForm.Wallet"));
        shell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {

                System.exit(0);
            }
        });


        FormsUtil.addForm(MainForm.class, shell);


        shell.setLocation(Display.getCurrent().getClientArea().width / 2 - shell.getShell().getSize().x / 2, Display.getCurrent().getClientArea().height / 2 - shell.getSize().y / 2);

        Menu menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);

        MenuItem newFileMI = new MenuItem(menu, SWT.CASCADE);
        newFileMI.setText(Messages.getString("MainForm.文件"));

        Menu menu_1 = new Menu(newFileMI);
        newFileMI.setMenu(menu_1);

        MenuItem openWalletMI = new MenuItem(menu_1, SWT.NONE);
        openWalletMI.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {


                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
                dialog.setText(Messages.getString("MainForm.Please Select..."));
                dialog.setFilterExtensions(new String[]{"*.dat"});
                String walletPath = dialog.open();
                if (StringUtils.isNotBlank(walletPath)) {



                    OpenWalletDialog openWalletDialog = new OpenWalletDialog(shell);
                    String resultJson = String.valueOf(openWalletDialog.open());
                    OpenWalletDialogResultVo vo = Jsons.toObject(resultJson, OpenWalletDialogResultVo.class);


                    String password = vo.getPassword();
                    String label = vo.getLabel();

                    Wallet currentWallet = null;
                    try {
                        currentWallet = WalletService.readWalletFile(walletPath, password);
                    } catch (Exception e) {
                        return;
                    }


                    stackLayout.topControl = receiveComp;
                    compContainer.layout();


                    saveNewWallet(currentWallet, label, walletPath);


                    makeReceiveTBISelect();
                }
            }
        });
        openWalletMI.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/open.png"));
        openWalletMI.setText(Messages.getString("MainForm.Open..."));

        MenuItem newWalletMI = new MenuItem(menu_1, SWT.NONE);
        newWalletMI.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {


                OpenWalletDialog openWalletDialog = new OpenWalletDialog(new Shell());
                openWalletDialog.setDefaultDialogTitle(Messages.getString("MainForm.New..."));

                String resultJson = String.valueOf(openWalletDialog.open());
                OpenWalletDialogResultVo vo = Jsons.toObject(resultJson, OpenWalletDialogResultVo.class);


                String password = vo.getPassword();
                String label = vo.getLabel().trim();

                if (!password.equals("")) {

                    String walletPath = DCCoreConfig.RootRealPath + "wallet-" + label + ".dat";


                    if (new File(walletPath).exists()) {
                        walletPath = DCCoreConfig.RootRealPath + "wallet-" + label + "_" + DateUtil.format(new Date(), "yyyy-MM-dd_HH-mm-ss") + ".dat";
                    }


                    Wallet w = null;
                    try {
                        w = WalletService.generateWallet();

                        WalletService.saveWalletFile(w, walletPath, password);


                        saveNewWallet(w, label, walletPath);

                    } catch (WrongPasswordException wpe) {
                    }
                }
            }
        });
        newWalletMI.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/address.png"));

        new MenuItem(menu_1, SWT.SEPARATOR);

        MenuItem exitMI = new MenuItem(menu_1, SWT.NONE);
        exitMI.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {


                System.exit(0);
            }
        });
        exitMI.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/exit.png"));

        MenuItem settingsMI = new MenuItem(menu, SWT.CASCADE);

        Menu menu_2 = new Menu(settingsMI);
        settingsMI.setMenu(menu_2);


        MenuItem changePswMI = new MenuItem(menu_2, SWT.NONE);
        changePswMI.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {

                dialog.setFilterExtensions(new String[]{"*.dat"});
                String walletPath = dialog.open();
                if (StringUtils.isNotBlank(walletPath)) {



                    UpdateWalletPswDialog openWalletDialog = new UpdateWalletPswDialog(shell);
                    String resultJson = String.valueOf(openWalletDialog.open());
                    UpdateWalletPswDialogVo vo = Jsons.toObject(resultJson, UpdateWalletPswDialogVo.class);


                    String oldPassword = vo.getOldPassword();
                    String newPassword = vo.getNewPassword();

                    Wallet currentWallet = null;
                    try {
                        currentWallet = WalletService.readWalletFile(walletPath, oldPassword);
                    } catch (Exception e) {


                        return;
                    }


                    try {
                        WalletService.saveWalletFile(currentWallet, walletPath, newPassword);
                    } catch (Exception e) {
                    }

                }
            }
        });
        changePswMI.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/key.png"));
        changePswMI.setText(Messages.getString("MainForm."));


        if (WalletConfig.IsMultipleLanguages) {
            MenuItem menuItem = new MenuItem(menu_2, SWT.SEPARATOR);
            MenuItem optionMI = new MenuItem(menu_2, SWT.NONE);
            optionMI.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    settingShell = new SettingShell(display);
                    settingShell.open();
                    settingShell.layout();
                }
            });
            optionMI.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/setting.png"));
            optionMI.setText(Messages.getString("MainForm."));
        }

        MenuItem helpMI = new MenuItem(menu, SWT.CASCADE);
        helpMI.setText(Messages.getString("MainForm."));

        Menu menu_3 = new Menu(helpMI);
        helpMI.setMenu(menu_3);

        MenuItem menuItem_Console = new MenuItem(menu_3, SWT.NONE);
        menuItem_Console.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {


                CoreUtil.showConsole(shell);

            }
        });
        menuItem_Console.setText(Messages.getString("MainForm."));

        MenuItem menuAboutApp = new MenuItem(menu_3, SWT.NONE);
        menuAboutApp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                StringBuffer msg = new StringBuffer();
                msg.append(WalletConfig.AppFullName);
                msg.append("\n");
                msg.append("\n");
                msg.append("Wallet Version: ").append(WalletConfig.AppName).append(" ").append(WalletConfig.AppVersion);
                msg.append("\n");
                msg.append("Core Version: ").append(DCCoreConfig.AppName).append(" ").append(DCCoreConfig.AppVersion);
                msg.append("\n");

                MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
                dialog.setText("About " + WalletConfig.AppName);
                dialog.setMessage(msg.toString());
                dialog.open();
            }
        });
        menuAboutApp.setText(Messages.getString("MainForm.") + DCCoreConfig.AppName);

        ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
        toolBar.setBounds(5, 5, 839, 38);

        summaryTBI = new ToolItem(toolBar, SWT.RADIO);
        summaryTBI.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/home.png"));
        summaryTBI.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (summaryTBI.getSelection()) {
                    stackLayout.topControl = summaryComp;
                    compContainer.layout();
                }
            }
        });
        summaryTBI.setText(Messages.getString("MainForm."));

        sendTBI = new ToolItem(toolBar, SWT.RADIO);
        sendTBI.setWidth(24);
        sendTBI.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/send.png"));
        sendTBI.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (sendTBI.getSelection()) {
                    stackLayout.topControl = sendComp;
                    compContainer.layout();
                }
            }
        });
        sendTBI.setText(Messages.getString("MainForm."));

        receiveTBI = new ToolItem(toolBar, SWT.RADIO);
        receiveTBI.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/down.png"));
        receiveTBI.setWidth(24);
        receiveTBI.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {

                if (receiveTBI.getSelection()) {
                    stackLayout.topControl = receiveComp;
                    compContainer.layout();
                }

            }
        });
        receiveTBI.setText(Messages.getString("MainForm."));

        txListTBI = new ToolItem(toolBar, SWT.RADIO);
        txListTBI.setWidth(24);
        txListTBI.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/list.png"));
        txListTBI.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (txListTBI.getSelection()) {
                    stackLayout.topControl = tradeListComp;
                    compContainer.layout();
                }
            }
        });
        txListTBI.setText(Messages.getString("MainForm."));

        compContainer = new Composite(shell, SWT.NONE);
        compContainer.setBounds(0, 60, 844, 450);

        compContainer.setLayout(stackLayout);

        summaryComp = new Composite(compContainer, SWT.NONE);
        summaryComp.setLayout(new FillLayout(SWT.HORIZONTAL));

        Composite balanceMoneyComp = new Composite(summaryComp, SWT.NONE);
        balanceMoneyComp.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.BOLD));

        Label lblNewLabel = new Label(balanceMoneyComp, SWT.NONE);
        lblNewLabel.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 11, SWT.BOLD));
        lblNewLabel.setBounds(24, 28, 413, 17);
        lblNewLabel.setText(Messages.getString("MainForm."));

        Label waitingMoneyKey = new Label(balanceMoneyComp, SWT.NONE);
        waitingMoneyKey.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        waitingMoneyKey.setBounds(24, 61, 84, 17);
        waitingMoneyKey.setText(Messages.getString("MainForm.："));

        Label label_2 = new Label(balanceMoneyComp, SWT.NONE);
        label_2.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        label_2.setText(Messages.getString("MainForm.："));
        label_2.setBounds(24, 94, 84, 17);

        canUseMoneyValLabel = new Label(balanceMoneyComp, SWT.NONE);
        canUseMoneyValLabel.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 10, SWT.BOLD));
        canUseMoneyValLabel.setBounds(114, 61, 268, 17);

        waitingMoneyValLabel = new Label(balanceMoneyComp, SWT.NONE);
        waitingMoneyValLabel.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 10, SWT.BOLD));
        waitingMoneyValLabel.setBounds(114, 94, 268, 17);

        Label lblD = new Label(balanceMoneyComp, SWT.SEPARATOR | SWT.HORIZONTAL);
        lblD.setBounds(25, 125, 220, 2);

        Label label_5 = new Label(balanceMoneyComp, SWT.NONE);
        label_5.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        label_5.setText(Messages.getString("MainForm."));
        label_5.setBounds(24, 140, 84, 17);

        totalMoneyValLabel = new Label(balanceMoneyComp, SWT.NONE);
        totalMoneyValLabel.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 10, SWT.BOLD));
        totalMoneyValLabel.setBounds(114, 138, 268, 17);

        Composite composite_1 = new Composite(summaryComp, SWT.NONE);

        Label label_9 = new Label(composite_1, SWT.NONE);
        label_9.setText(Messages.getString("MainForm."));
        label_9.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 11, SWT.BOLD));
        label_9.setBounds(0, 26, 437, 17);

        recentTxListTable = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
        recentTxListTable.setLocation(0, 53);
        recentTxListTable.setSize(412, 387);
        recentTxListTable.setLinesVisible(true);
        recentTxListTable.setHeaderVisible(true);

        TableColumn tableColumn = new TableColumn(recentTxListTable, SWT.NONE);
        tableColumn.setWidth(50);
        tableColumn.setText("   ");

        TableColumn tableColumn_4 = new TableColumn(recentTxListTable, SWT.NONE);
        tableColumn_4.setWidth(215);
        tableColumn_4.setText(Messages.getString("MainForm."));

        TableColumn tableColumn_8 = new TableColumn(recentTxListTable, SWT.NONE);
        tableColumn_8.setWidth(142);
        tableColumn_8.setText(Messages.getString("MainForm.") + "(" + DCCoreConfig.UnitName + ")");

        sendComp = new Composite(compContainer, SWT.NONE);
        receiveComp = new Composite(compContainer, SWT.NONE);

        Label labWalletInfo = new Label(receiveComp, SWT.NONE);
        labWalletInfo.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 10, SWT.NORMAL));
        labWalletInfo.setBounds(10, 0, 784, 17);
        labWalletInfo.setText(Messages.getString("MainForm.："));

        walletItemTable = new Table(receiveComp, SWT.BORDER | SWT.FULL_SELECTION);
        walletItemTable.setBounds(10, 23, 824, 386);
        walletItemTable.setHeaderVisible(true);
        walletItemTable.setLinesVisible(true);

        walletItemTable.showSelection();

        TableColumn labelColumn = new TableColumn(walletItemTable, SWT.NONE);
        labelColumn.setWidth(131);
        labelColumn.setText(Messages.getString("MainForm."));

        TableColumn addressColumn = new TableColumn(walletItemTable, SWT.NONE);
        addressColumn.setWidth(385);
        addressColumn.setText(Messages.getString("MainForm."));

        TableColumn tableColumn_2 = new TableColumn(walletItemTable, SWT.NONE);
        tableColumn_2.setWidth(109);
        tableColumn_2.setText(Messages.getString("MainForm."));

        TableColumn addTimeColumn = new TableColumn(walletItemTable, SWT.NONE);
        addTimeColumn.setWidth(142);
        addTimeColumn.setText(Messages.getString("MainForm."));

        Button btnCopy = new Button(receiveComp, SWT.NONE);
        btnCopy.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {

                final Clipboard cb = new Clipboard(display);


                int index = walletItemTable.getSelectionIndex();
                if (index >= 0) {


                    TableItem item = walletItemTable.getItem(index);


                    String walletAddress = item.getText(1);
                    if (walletAddress.length() == 0)
                        return;

                    Object[] data = new Object[]{walletAddress};
                    Transfer[] types = new Transfer[]{TextTransfer.getInstance()};
                    cb.setContents(data, types);

                    Alerts.info(shell, Messages.getString("MainForm"));

                }

            }
        });
        btnCopy.setText(Messages.getString("MainForm."));
        btnCopy.setImage(null);
        btnCopy.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        btnCopy.setBounds(10, 415, 100, 25);

        Button btnRemoveWalletItem = new Button(receiveComp, SWT.NONE);
        btnRemoveWalletItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                int index = walletItemTable.getSelectionIndex();
                if (index >= 0) {


                    TableItem item = walletItemTable.getItem(index);


                    String text = item.getText(1);


                    walletItemTable.remove(index);


                    boolean state = walletItemDAO.deleteByAddress(text);

                    if (state) {


                        DataService.me().removeFromWalletCache(text);


                        DataService.me().refreshWalletAddressComboOfSendPanel(walletAddressCombo);


                        refreshData();

                        Alerts.info(shell, Messages.getString("MainForm."));
                    }

                }
            }
        });
        btnRemoveWalletItem.setImage(null);
        btnRemoveWalletItem.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        btnRemoveWalletItem.setBounds(126, 415, 100, 25);
        btnRemoveWalletItem.setText(Messages.getString("MainForm."));

        Button btnRefreshMoney = new Button(receiveComp, SWT.NONE);
        btnRefreshMoney.setText(Messages.getString("MainForm."));
        btnRefreshMoney.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {


                refreshData();
            }
        });
        btnRefreshMoney.setImage(null);
        btnRefreshMoney.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        btnRefreshMoney.setBounds(242, 415, 100, 25);

        tradeListComp = new Composite(compContainer, SWT.NONE);

        txListTable = new Table(tradeListComp, SWT.BORDER | SWT.FULL_SELECTION);
        txListTable.setLinesVisible(true);
        txListTable.setHeaderVisible(true);
        txListTable.setBounds(10, 25, 824, 415);

        TableColumn tblclmnId = new TableColumn(txListTable, SWT.NONE);
        tblclmnId.setText(" ");
        tblclmnId.setWidth(50);

        TableColumn tableColumn_1 = new TableColumn(txListTable, SWT.NONE);
        tableColumn_1.setWidth(140);
        tableColumn_1.setText(Messages.getString("MainForm"));

        TableColumn tableColumn_6 = new TableColumn(txListTable, SWT.NONE);
        tableColumn_6.setWidth(250);
        tableColumn_6.setText(Messages.getString("MainForm"));

        TableColumn tableColumn_3 = new TableColumn(txListTable, SWT.NONE);
        tableColumn_3.setWidth(250);
        tableColumn_3.setText(Messages.getString("MainForm"));

        TableColumn tblclmnevc = new TableColumn(txListTable, SWT.NONE);
        tblclmnevc.setWidth(130);
        tblclmnevc.setText(Messages.getString("MainForm"));

        Label lblNewLabel_2 = new Label(tradeListComp, SWT.NONE);
        lblNewLabel_2.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        lblNewLabel_2.setBounds(10, 0, 764, 17);
        lblNewLabel_2.setText(Messages.getString("MainForm"));


        stackLayout.topControl = summaryComp;
        compContainer.layout();

        Composite composite = new Composite(sendComp, SWT.BORDER);
        composite.setBounds(10, 10, 824, 153);

        Label lbll = new Label(composite, SWT.NONE);
        lbll.setBounds(10, 10, 66, 17);
        lbll.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        lbll.setText(Messages.getString("MainForm"));


        String walletAddressComboDefText = Messages.getString("MainForm");

        walletAddressCombo = new Combo(composite, SWT.NONE);
        walletAddressCombo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                String text = walletAddressCombo.getText();
                if (text.equals(walletAddressComboDefText)) {
                    walletAddressCombo.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                String text = walletAddressCombo.getText();
                if (StringUtils.isBlank(text)) {
                    walletAddressCombo.setText(walletAddressComboDefText);
                }
            }
        });
        walletAddressCombo.setBounds(78, 6, 732, 21);
        walletAddressCombo.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 10, SWT.NORMAL));
        walletAddressCombo.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
        walletAddressCombo.setText(Messages.getString("MainForm"));

        Label label = new Label(composite, SWT.NONE);
        label.setBounds(10, 43, 66, 17);
        label.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        label.setText(Messages.getString("MainForm"));


        String toWalletAddressDefText = Messages.getString("MainForm");
        toWalletAddressText = new Text(composite, SWT.BORDER);
        toWalletAddressText.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent arg0) {
                String text = toWalletAddressText.getText();

                if (text.equals(toWalletAddressDefText)) {
                    toWalletAddressText.setText("");
                }

            }

            @Override
            public void focusLost(FocusEvent arg0) {
                String text = toWalletAddressText.getText();
                if (StringUtils.isBlank(text)) {
                    toWalletAddressText.setText(toWalletAddressDefText);
                }
            }
        });
        toWalletAddressText.setBounds(78, 40, 640, 23);
        toWalletAddressText.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        toWalletAddressText.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
        toWalletAddressText.setText(toWalletAddressDefText);

        Label lbll_2 = new Label(composite, SWT.NONE);
        lbll_2.setText(Messages.getString("MainForm"));
        lbll_2.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        lbll_2.setBounds(10, 78, 66, 17);

        String labelDefText = Messages.getString("MainForm");
        labelText = new Text(composite, SWT.BORDER);
        labelText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                String text = labelText.getText();
                if (text.equals(labelDefText)) {
                    labelText.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                String text = labelText.getText();
                if (StringUtils.isBlank(text)) {
                    labelText.setText(labelDefText);
                }
            }
        });
        labelText.setText(labelDefText);
        labelText.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
        labelText.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        labelText.setBounds(78, 75, 732, 23);

        Button clearToWalletAddressBtn = new Button(composite, SWT.NONE);
        clearToWalletAddressBtn.setToolTipText(Messages.getString("MainForm"));
        clearToWalletAddressBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {


                toWalletAddressText.setText(toWalletAddressDefText);
            }
        });
        clearToWalletAddressBtn.setBounds(785, 40, 25, 23);
        clearToWalletAddressBtn.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/remove.png"));

        Button pasteBtn = new Button(composite, SWT.NONE);
        pasteBtn.setToolTipText(Messages.getString("MainForm"));
        pasteBtn.setBounds(754, 40, 25, 23);
        pasteBtn.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/paste.png"));

        Button showAddressBookBtn = new Button(composite, SWT.NONE);
        showAddressBookBtn.setToolTipText(Messages.getString("MainForm"));
        showAddressBookBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {


                AddressBookListDialog dialog = new AddressBookListDialog(shell);
                String address = CommUtil.null2String(dialog.open());


                toWalletAddressText.setText(address);
            }
        });
        showAddressBookBtn.setBounds(724, 40, 25, 23);
        showAddressBookBtn.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/address.png"));

        Label lbll_1 = new Label(composite, SWT.NONE);
        lbll_1.setBounds(10, 115, 66, 17);
        lbll_1.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        lbll_1.setText(Messages.getString("MainForm"));

        Combo combo_1 = new Combo(composite, SWT.READ_ONLY);
        combo_1.setBounds(214, 111, 58, 21);
        combo_1.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 10, SWT.NORMAL));
        combo_1.add(DCCoreConfig.UnitName);
        combo_1.select(0);

        String moneyDefText = "0.00000000";
        moneyText = new Text(composite, SWT.BORDER);
        moneyText.setText("0.00000000");
        moneyText.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
        moneyText.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        moneyText.setBounds(78, 110, 130, 23);
        moneyText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                String text = moneyText.getText();
                if (text.equals(moneyDefText)) {
                    moneyText.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                String text = moneyText.getText();
                if (StringUtils.isBlank(text)) {
                    moneyText.setText(moneyDefText);
                }
            }
        });

        pasteBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                Clipboard clipboard = new Clipboard(display);
                String plainText = (String) clipboard.getContents(TextTransfer.getInstance());

                toWalletAddressText.setText(plainText);
                clipboard.dispose();
            }
        });

        Button doSendBtn = new Button(sendComp, SWT.NONE);
        doSendBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {


                String walletAddress = walletAddressCombo.getText();


                String toWalletAddress = toWalletAddressText.getText();


                String label = labelText.getText();


                String money = moneyText.getText();


                if (!Util.isMoneyValid(money)) {
                    Alerts.info(shell, Messages.getString("MainForm"));
                    return;
                }


                Wallet wallet = DataService.me().getWalletObjByWalletAddress(walletAddress);
                if (wallet == null) {
                    Alerts.info(shell, Messages.getString("MainForm"));
                    return;
                }


                if (!Util.isWalletAddressValid(toWalletAddress)) {
                    Alerts.info(shell, Messages.getString("MainForm"));
                    return;
                }


                long moneyFinal = (long) (CommUtil.null2Double(money) * DCCoreConfig.COIN_DECIMAL);


                if (WalletService.getCanUseDCTotal(walletAddress) < moneyFinal) {
                    Alerts.info(shell, Messages.getString(Messages.getString("MainForm")));
                    return;
                }


                try {

                    DataService.me().saveAddressBookIfNecessary(toWalletAddress, label);


                    WalletService.sendDC(wallet, toWalletAddress, moneyFinal, 0);


                    String msg = Messages.getString("MainForm");
                    Alerts.info(shell, msg);


                    toWalletAddressText.setText("");
                    labelText.setText("");
                    moneyText.setText("");


                    refreshData();

                } catch (Exception e) {
                    String msg = Messages.getString("MainForm") + e.getMessage();
                    log.error(msg, e);
                    Alerts.info(shell, msg);
                }

            }
        });
        doSendBtn.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        doSendBtn.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/send-16.png"));
        doSendBtn.setBounds(89, 175, 130, 25);
        doSendBtn.setText(Messages.getString("MainForm.(E)"));

        Button clearSendBtn = new Button(sendComp, SWT.NONE);
        clearSendBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {


                toWalletAddressText.setText(toWalletAddressDefText);


                labelText.setText(labelDefText);


                moneyText.setSelection(0);
            }
        });
        clearSendBtn.setFont(SWTResourceManager.getFont(WalletConfig.DefaultFontName, 9, SWT.NORMAL));
        clearSendBtn.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/remove-16.png"));
        clearSendBtn.setText(Messages.getString("MainForm.(C)"));
        clearSendBtn.setBounds(225, 175, 130, 25);

        Label label_12 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label_12.setText("");
        label_12.setBounds(0, 49, 840, 2);

        Composite composite_2 = new Composite(shell, SWT.NONE);
        composite_2.setBounds(0, 516, 844, 26);

        Label lblmainformlabeltext = new Label(composite_2, SWT.SEPARATOR | SWT.HORIZONTAL);
        lblmainformlabeltext.setText("");
        lblmainformlabeltext.setSize(840, 2);

        Label lblNewLabel_1 = new Label(composite_2, SWT.NONE);
        lblNewLabel_1.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/lock.png"));
        lblNewLabel_1.setBounds(772, 6, 16, 16);

        Label label_13 = new Label(composite_2, SWT.NONE);
        label_13.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/chart_3.png"));
        label_13.setBounds(795, 6, 16, 16);

        Label label_14 = new Label(composite_2, SWT.NONE);
        label_14.setImage(SWTResourceManager.getImage(MainForm.class, "/com/dc/wallet/ui/assets/ok.png"));
        label_14.setBounds(817, 6, 16, 16);
    }

    private void makeReceiveTBISelect() {
        receiveTBI.setSelection(true);
        sendTBI.setSelection(false);
        txListTBI.setSelection(false);
        summaryTBI.setSelection(false);
    }



}
