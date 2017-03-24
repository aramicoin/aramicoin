package com.dc.wallet.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {

	private static final String BUNDLE_NAME = "com.dc.wallet.ui.messages"; 

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			
			int Mode = 2;

			
			if (Mode == 1) {
				return RESOURCE_BUNDLE.getString(key);
			}
			
			else if (Mode == 2) {
				return RESOURCE_BUNDLE_EXT.getString(key);
			}
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}

		return RESOURCE_BUNDLE.getString(key);
	}

	
	private static String BUNDLE_NAME_EXT = "com.dc.wallet.ui.messages";

	private static ResourceBundle RESOURCE_BUNDLE_EXT;

	
	public static void setBUNDLE_NAME(String bundleName) {
		
		BUNDLE_NAME_EXT = bundleName;

		
		RESOURCE_BUNDLE_EXT = ResourceBundle.getBundle(BUNDLE_NAME_EXT);
	}

	
	public static String getBUNDLE_NAME() {
		return BUNDLE_NAME_EXT;
	}

}
