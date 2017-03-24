package com.dc.wallet.ui.dialog;

import com.dc.core.util.Jsons;
import com.dc.wallet.ui.Messages;
import com.dc.wallet.ui.vo.UpdateWalletPswDialogVo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;


public class UpdateWalletPswDialog extends Dialog {

	protected Object result;
	protected String oldPswVal;
	protected String newPswVal;
	protected Shell shell;
	private Text oldPswText;
	private Label label_1;
	private Text newPswText;
	private Button canelBtn;

	private Text confirmPswText;
	private Label label_2;

	public UpdateWalletPswDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	
	public UpdateWalletPswDialog(Shell parent, int style) {
		super(parent, style);
		setText(Messages.getString("UpdateWalletPswDialog.WalletPsw"));
	}

	
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		
		return Jsons.toJson(new UpdateWalletPswDialogVo(oldPswVal, newPswVal));
	}

	
	private void createContents() {

		shell = new Shell(getParent(), getStyle());
		shell.setSize(300, 264);

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont(Messages.getString("UpdateWalletPswDialog.Song"), 9, SWT.NORMAL));
		label.setBounds(10, 20, 274, 17);

		oldPswText = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		oldPswText.setBounds(10, 43, 274, 23);
		oldPswText.setEchoChar('*');

		label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont(Messages.getString("UpdateWalletPswDialog.Song"), 9, SWT.NORMAL));
		label_1.setBounds(10, 80, 274, 17);

		newPswText = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		newPswText.setBounds(10, 103, 274, 23);
		newPswText.setEchoChar('*');

		Button okBtn = new Button(shell, SWT.NONE);
		okBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				oldPswVal = oldPswText.getText();
				newPswVal = newPswText.getText();
				shell.close();
			}
		});
		okBtn.setBounds(64, 200, 80, 27);
		okBtn.setText(Messages.getString("UpdateWalletPswDialog.Sure"));

		canelBtn = new Button(shell, SWT.NONE);
		canelBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				oldPswVal = ""; 
				newPswVal = ""; 
				shell.close();
			}
		});
		canelBtn.setText(Messages.getString("UpdateWalletPswDialog.Sure"));
		canelBtn.setBounds(150, 200, 80, 27);

		confirmPswText = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		confirmPswText.setBounds(10, 164, 274, 23);
		newPswText.setEchoChar('*');

		label_2 = new Label(shell, SWT.NONE);
		label_2.setText(Messages.getString("UpdateWalletPswDialog.ConfirmNewPsw："));
		label_2.setFont(SWTResourceManager.getFont(Messages.getString("UpdateWalletPswDialog.Song"), 9, SWT.NORMAL));
		label_2.setBounds(10, 141, 274, 17);
	}
}
