package com.logitech.lip.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.LipSdkTest;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.ChangeClaims;
import com.logitech.lip.account.model.ChangePassword;
import com.logitech.lip.account.model.SignInResponse;
import com.logitech.lip.account.model.SocialIdentity;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.ui.login.LoginSelectorActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AccountManagerTest {

    private SignInResponse signInResponse;
    private UserInfo userInfo;

    @Rule
    public ActivityTestRule<LoginSelectorActivity> mActivityTestRule =
            new ActivityTestRule<LoginSelectorActivity>(LoginSelectorActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent intent = new Intent(targetContext, LoginSelectorActivity.class);
                    LoginOptions loginOptions = new LoginOptions.Builder()
                            .setIsCreate(false)
                            .build();
                    intent.putExtra(LoginSelectorActivity.LOGIN_OPTIONS, loginOptions);
                    return intent;
                }
            };


    @Before
    public void setUp() throws Exception {
        LIPSdk.initialize(mActivityTestRule.getActivity().getApplication(), LipSdkTest.getConfiguration());

        signInResponse = new SignInResponse();
        signInResponse.setAccessToken("32dssd");
        signInResponse.setIdToken("sfsegs");
        signInResponse.setRefreshToken("asfewt");
        signInResponse.setExpireTime("3600");

        userInfo = new UserInfo("dummy@dummy.com", "1234");
    }

    @Test
    public void testAllPublicMethods() throws Exception {
        assertNotNull(AccountManager.class.getMethod("getCurrentAccountToken", ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("setCurrentAccountToken", SignInResponse.class, UserInfo.class));

        assertNotNull(AccountManager.class.getMethod("getUserInfo", String.class, ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("signIn", SocialIdentity.class, ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("signIn", String.class, String.class, ResponseListener.class));

        assertNotNull(AccountManager.class.getMethod("create", UserInfo.class, ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("create", UserInfo.class, Map.class, ResponseListener.class));

        assertNotNull(AccountManager.class.getMethod("refreshAccountToken", ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("signOut", boolean.class, String.class, ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("changePassword", String.class, ChangePassword.class, ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("forgotPassword", String.class, ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("resendVerificationMail", String.class, ResponseListener.class));

        assertNotNull(AccountManager.class.getMethod("changeClaims", String.class, ChangeClaims.class,ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("changeClaims", String.class, Map.class,ResponseListener.class));
        assertNotNull(AccountManager.class.getMethod("getClaims", String.class, ResponseListener.class));
    }

    @Test
    public void testReadAccountToken() throws Exception {
        AccountManager.setCurrentAccountToken(null, null);

        AccountToken accountToken = AccountTokenManager.getInstance().getCurrentAccountToken();
        assertNull(accountToken);
        AccountManager.setCurrentAccountToken(signInResponse, userInfo);

        accountToken = AccountTokenManager.getInstance().getCurrentAccountToken();
        assertEquals(accountToken.getAccessToken(), signInResponse.getAccessToken());

        AccountManager.setCurrentAccountToken(null, null);

        accountToken = AccountTokenManager.getInstance().getCurrentAccountToken();
        assertNull(accountToken);
    }

    @Test
    public void testReadAccountTokenCallback() throws Exception {
        AccountManager.setCurrentAccountToken(null, null);

        AccountToken accountToken = AccountTokenManager.getInstance().getCurrentAccountToken();
        assertNull(accountToken);

        AccountManager.setCurrentAccountToken(signInResponse, userInfo);

        // No need to wait , it should return from cache it self
        AccountManager.getCurrentAccountToken(new ResponseListener<AccountToken>() {
            @Override
            public void onSuccess(AccountToken result) {
                assertEquals(result.getAccessToken(), signInResponse.getAccessToken());
            }

            @Override
            public void onError(ErrorCode errorCode, String errorMessage) {
                assertNotNull(null);
            }
        });
        AccountManager.setCurrentAccountToken(null, null);

        accountToken = AccountTokenManager.getInstance().getCurrentAccountToken();
        assertNull(accountToken);
    }
}
