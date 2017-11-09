package com.cectest;

import android.app.Application;

import com.babisoft.ReactNativeLocalization.ReactNativeLocalizationPackage;
import com.burnweb.rnsendintent.RNSendIntentPackage;
import com.facebook.react.ReactApplication;
import com.oblador.vectoricons.VectorIconsPackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.logitech.lip.LIPSdk;
import com.logitech.lip.LipConfiguration;
import com.microsoft.codepush.react.CodePush;
import com.cectest.lipmodule.LipPackage;

import java.util.Arrays;
import java.util.List;

import it.innove.BleManagerPackage;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected String getJSBundleFile() {
        return CodePush.getJSBundleFile();
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
            new ReactNativeLocalizationPackage(),
            new VectorIconsPackage(),
            new RNSendIntentPackage(),
            new BleManagerPackage(),
            new CodePush("ELm2xK2uR0avLYjsA80QOSPvO_Sd6f649b19-1890-4f5d-bc47-204ddce4effd",
            getApplicationContext(), BuildConfig.DEBUG),
            new LipPackage()
      );
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);

    LipConfiguration lipConfiguration = new LipConfiguration.Builder()
            .setIsVerifyEmail(false)
            .setAnalyticCallback(null)
            .setTermsUseUrl(getString(R.string.lip_signup_tou_url))
            .setPrivacyPolicyUrl(getString(R.string.lip_signup_privacy_policy_url))
            .setServerUrl(/*"http://panneer.int.myharmony.com:8084"*/
                    "https://int-lip.dynamite.logi.com/"/*"https://stable-lip.dynamite.logi.com"*/)
            .build();

    LIPSdk.initialize(this, lipConfiguration);
  }
}
