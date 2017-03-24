package com.dc.wallet.ui.dialog;

import com.dc.core.util.Jsons;
import com.dc.core.util.Util;
import com.dc.wallet.dao.AddressBookDAO;
import com.dc.wallet.ui.Alerts;
import com.dc.wallet.ui.Messages;
import com.dc.wallet.ui.vo.NewAddressBookDialogVo;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;


public class NewAddressBookDialog extends Dialog {

    protected Object result;
    protected String labelVal;
    protected String addressVal;
    protected Shell shell;
    private Text labelText;
    private Label label_1;
    private Text addressText;
    private Button cancelBtn;


    private Alerts alerts;


    private AddressBookDAO addressBookDAO;

    public NewAddressBookDialog(Shell parent) {
        this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }


    public NewAddressBookDialog(Shell parent, int style) {
        super(parent, style);
    }


    public Object open() {

        addressBookDAO = new AddressBookDAO();

        createContents();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }


        return Jsons.toJson(new NewAddressBookDialogVo(addressVal, labelVal));
    }


    private void createContents() {


        shell = new Shell(getParent(), getStyle());
        shell.setText(Messages.getString("NewAddressBookDialog.Add"));
        shell.setSize(350, 213);

        Label label = new Label(shell, SWT.NONE);
        label.setFont(SWTResourceManager.getFont(Messages.getString("NewAddressBookDialog.Song"), 9, SWT.NORMAL));
        label.setBounds(10, 20, 324, 17);
        label.setText(Messages.getString("NewAddressBookDialog.Label："));

        labelText = new Text(shell, SWT.BORDER);
        labelText.setBounds(10, 43, 324, 23);

        label_1 = new Label(shell, SWT.NONE);
        label_1.setText(Messages.getString("NewAddressBookDialog.Address："));
        label_1.setFont(SWTResourceManager.getFont(Messages.getString("NewAddressBookDialog.Song"), 9, SWT.NORMAL));
        label_1.setBounds(10, 80, 324, 17);

        addressText = new Text(shell, SWT.BORDER);
        addressText.setBounds(10, 103, 324, 23);

        Button okBtn = new Button(shell, SWT.NONE);
        okBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {

                labelVal = labelText.getText().trim();
                addressVal = addressText.getText().trim();

                if (StringUtils.isBlank(labelVal)) {
                    return;
                }

                if (StringUtils.isBlank(addressVal)) {
                    return;
                }

                if (!Util.isWalletAddressValid(addressVal)) {
                    return;
                }


                int count = addressBookDAO.countByLabelAndAddress(labelVal, addressVal);
                if (count > 0) {
                    return;
                }

                shell.close();
            }
        });
        okBtn.setBounds(88, 142, 80, 27);
        okBtn.setText(Messages.getString("NewAddressBookDialog.OK"));

        cancelBtn = new Button(shell, SWT.NONE);
        cancelBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                labelVal = "";
                addressVal = "";
                shell.close();
            }
        });
        cancelBtn.setText(Messages.getString("NewAddressBookDialog.Cancel"));
        cancelBtn.setBounds(186, 142, 80, 27);
    }
}
