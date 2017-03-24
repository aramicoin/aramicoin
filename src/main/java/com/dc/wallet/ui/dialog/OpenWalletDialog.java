package com.dc.wallet.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.dc.core.util.Jsons;
import com.dc.wallet.ui.Messages;
import com.dc.wallet.ui.vo.OpenWalletDialogResultVo;


public class OpenWalletDialog extends Dialog {

	protected Object result;
	protected String pswTextVal;
	protected String labelTextVal;
	protected Shell shell;
	private Text pswText;
	private Label label_1;
	private Text labelText;
	private Button canelBtn;

	
	private String defaultDialogTitle = Messages.getString("OpenWalletDialog.Open...");
	private String defaultLabelText = ""; 
	private boolean defaultLabelEnabled = true;

	public OpenWalletDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	
	public OpenWalletDialog(Shell parent, int style) {
		super(parent, style);
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

		
		return Jsons.toJson(new OpenWalletDialogResultVo(labelTextVal, pswTextVal));
	}

	public String getDefaultDialogTitle() {
		return defaultDialogTitle;
	}

	public void setDefaultDialogTitle(String defaultDialogTitle) {
		this.defaultDialogTitle = defaultDialogTitle;
	}

	public void setDefaultLabelText(String text) {
		defaultLabelText = text;
	}

	public void setDefaultLabelEnabled(boolean enabled) {
		defaultLabelEnabled = enabled;
	}

	
	private void createContents() {

		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE);
		shell.setSize(300, 220);
		shell.setText(defaultDialogTitle);

		
		shell.setLocation(Display.getCurrent().getClientArea().width / 2 - shell.getShell().getSize().x / 2, Display.getCurrent().getClientArea().height / 2 - shell.getSize().y / 2);

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont(Messages.getString("OpenWalletDialog.Song"), 9, SWT.NORMAL));
		label.setBounds(10, 20, 274, 17);

		pswText = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		pswText.setBounds(10, 43, 274, 23);
		pswText.setEchoChar('*');

		label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont(Messages.getString("OpenWalletDialog.宋Song体"), 9, SWT.NORMAL));
		label_1.setBounds(10, 80, 274, 17);

		labelText = new Text(shell, SWT.BORDER);
		labelText.setBounds(10, 103, 274, 23);
		labelText.setText(defaultLabelText);
		labelText.setEnabled(defaultLabelEnabled);

		Button okBtn = new Button(shell, SWT.NONE);
		okBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				pswTextVal = pswText.getText();
				labelTextVal = labelText.getText();

				

				shell.close();

			}
		});
		okBtn.setBounds(57, 142, 80, 27);
		okBtn.setText(Messages.getString("OpenWalletDialog.OK"));

		canelBtn = new Button(shell, SWT.NONE);
		canelBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				pswTextVal = ""; 
				labelTextVal = ""; 
				shell.close();
			}
		});
		canelBtn.setText(Messages.getString("OpenWalletDialog.Cancel"));
		canelBtn.setBounds(158, 142, 80, 27);
	}
}
