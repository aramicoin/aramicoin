package com.dc.wallet.config;

import com.dc.core.global.DCCoreConfig;
import com.dc.wallet.ui.Messages;
import com.dc.wallet.ui.SplashShell;
import com.ms.libs.beans.Field;
import com.ms.libs.config.ConfigFile;
import com.ms.libs.encrypt.CryptoFile;
import com.ms.libs.util.CommUtil;
import com.ms.libs.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class WalletConfig {


    public static final String LogName = "ARAMI-Wallet";

    public static String AppName;

    public static String AppFullName;

    public static String AppVersion;


    public static String AppSite;


    public static String CheckVersionURL;


    public static String DefaultFontName;


    public static boolean IsMultipleLanguages;


    private static String walletConfigFilePath;


    private static ConfigFile walletConfigFile = new ConfigFile();


    public static String LangaugeName;


    public static List<Field> LanguageList;


    public static SplashShell splashShell;


    public static final String logoFile = "/com/dc/wallet/ui/assets/logo.png";


    public static long AppStartTime = System.currentTimeMillis();


    public static boolean isRestartApp = false;

    static {

        try {

            LangaugeName = CommUtil.null2String(DCCoreConfig.getConfigValue("LangaugeName"), "");


            String newBundleName = "com.dc.wallet.ui.messages";
            if (!LangaugeName.equals("")) {
                newBundleName += "_" + LangaugeName;
            }

            Messages.setBUNDLE_NAME(newBundleName);


            LanguageList = new ArrayList<Field>();
            LanguageList.add(new Field("English(English)", "en"));
            LanguageList.add(new Field("Deutsch(German)", "de"));
            LanguageList.add(new Field("Fran ç ais(French)", "fr"));
            LanguageList.add(new Field("У Екатерины с Каппа с р.(Russian)", "ru"));
            LanguageList.add(new Field("ع ر ب ي(Arabic)", "ar"));
            LanguageList.add(new Field("简体中文(Chinese)", "zh_CN"));
            LanguageList.add(new Field("繁體中文(Traditional)", "zh_TW"));
            LanguageList.add(new Field("한 국 어(Korean)", "ko"));
            LanguageList.add(new Field("日本語(Japanese)", "ja"));


            {
                walletConfigFilePath = DCCoreConfig.RootRealPath + "wallet.conf";

                if (!new File(walletConfigFilePath).exists()) {
                    FileUtil.saveFile("", walletConfigFilePath);
                }

                byte[] decodeData = CryptoFile.getConfigFileData(walletConfigFilePath, DCCoreConfig.CORE_SECRET_KEY);


                walletConfigFile.setData(decodeData);


                AppName = walletConfigFile.getValue("AppName");

                AppFullName = walletConfigFile.getValue("AppFullName");

                AppVersion = walletConfigFile.getValue("AppVersion");

                AppSite = walletConfigFile.getValue("AppSite");

                CheckVersionURL = walletConfigFile.getValue("CheckVersionURL");

                DefaultFontName = walletConfigFile.getValue("DefaultFontName");

                IsMultipleLanguages = CommUtil.null2Boolean(walletConfigFile.getValue("IsMultipleLanguages"));

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String getLanguageValue(String langName) {
        String rtv = "";

        for (Field langField : WalletConfig.LanguageList) {
            if (WalletConfig.LangaugeName.equals(langField.getName())) {
                rtv = langField.getStringValue();
                break;
            }
        }

        return rtv;
    }


    public static int getLanguageIndex(String langValue) {
        int currLangIndex = -1;

        int count = 0;
        for (Field langField : WalletConfig.LanguageList) {
            if (langValue.equals(langField.getStringValue())) {
                currLangIndex = count;
                break;
            }
            count++;
        }

        return currLangIndex;
    }

}
