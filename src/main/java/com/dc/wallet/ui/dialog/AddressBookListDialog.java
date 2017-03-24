package com.dc.wallet.ui.dialog;

import com.dc.core.util.Jsons;
import com.dc.wallet.bean.AddressBook;
import com.dc.wallet.dao.AddressBookDAO;
import com.dc.wallet.ui.Messages;
import com.dc.wallet.ui.service.DataService;
import com.dc.wallet.ui.vo.NewAddressBookDialogVo;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

import java.util.Date;


public class AddressBookListDialog extends Dialog {

    protected String result;
    protected String pswTextVal;
    protected String labelTextVal;
    protected Shell shell;

    private Table table;
    private TableColumn tblclmnNewColumn;
    private TableColumn tblclmnNewColumn_1;
    private Button addBtn;
    private Button delButton;


    private AddressBookDAO addressBookDAO;

    public AddressBookListDialog(Shell parent) {
        this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }


    public AddressBookListDialog(Shell parent, int style) {
        super(parent, style);
    }


    public Object open() {

        createContents();
        shell.open();
        shell.layout();

        showData();

        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }


        return result;
    }

    private void showData() {
        addressBookDAO = new AddressBookDAO();


        java.util.List<AddressBook> addressBookList = addressBookDAO.findAll();


        DataService.me().addAddressBookRowList(table, addressBookList);

    }


    private void createContents() {

        shell = new Shell(getParent(), getStyle());
        shell.setSize(500, 249);
        shell.setText(Messages.getString("AddressBookListDialog...."));

        Button okBtn = new Button(shell, SWT.NONE);
        okBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {


                int index = table.getSelectionIndex();
                if (index >= 0) {


                    TableItem item = table.getItem(index);


                    result = item.getText(1);

                }

                shell.close();


            }
        });
        okBtn.setBounds(404, 180, 80, 27);
        okBtn.setText(Messages.getString("AddressBookListDialog."));

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent arg0) {


                int index = table.getSelectionIndex();
                if (index >= 0) {


                    TableItem item = table.getItem(index);


                    result = item.getText(1);

                }

                shell.close();


            }
        });
        table.setBounds(10, 10, 474, 159);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        tblclmnNewColumn = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn.setWidth(96);
        tblclmnNewColumn.setText(Messages.getString("AddressBookListDialog.label"));

        tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn_1.setWidth(374);
        tblclmnNewColumn_1.setText(Messages.getString("AddressBookListDialog.address"));

        addBtn = new Button(shell, SWT.NONE);
        addBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {

                NewAddressBookDialog dialog = new NewAddressBookDialog(shell);
                String resultJson = String.valueOf(dialog.open());
                NewAddressBookDialogVo newAddressBookDialogVo = Jsons.toObject(resultJson, NewAddressBookDialogVo.class);


                String address = newAddressBookDialogVo.getAddress();
                String label = newAddressBookDialogVo.getLabel();
                if (StringUtils.isNotBlank(address) && StringUtils.isNotBlank(label)) {


                    AddressBook addressBook = new AddressBook();
                    addressBook.setAddress(address);
                    addressBook.setLabel(label);
                    addressBook.setAddTime(new Date());


                    boolean add = addressBookDAO.add(addressBook);
                    if (add) {
                        DataService.me().addAddressBookRow(table, addressBook);
                    }
                }
            }
        });
        addBtn.setText(Messages.getString("AddressBookListDialog.add"));
        addBtn.setBounds(10, 180, 80, 27);

        delButton = new Button(shell, SWT.NONE);
        delButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {

                int index = table.getSelectionIndex();
                if (index >= 0) {


                    TableItem item = table.getItem(index);


                    result = item.getText(1);


                    boolean delete = addressBookDAO.deleteByAddress(result);


                    if (delete) {
                        table.remove(index);
                    }
                }
            }
        });
        delButton.setText(Messages.getString("AddressBookListDialog.delete"));
        delButton.setBounds(97, 180, 80, 27);
    }
}
