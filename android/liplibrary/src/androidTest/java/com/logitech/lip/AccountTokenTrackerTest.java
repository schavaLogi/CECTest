package com.logitech.lip;

import static junit.framework.TestCase.assertNotNull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.account.AccountManager;
import com.logitech.lip.account.AccountTokenTracker;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.SignInResponse;
import com.logitech.lip.ui.login.LoginSelectorActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AccountTokenTrackerTest {
    protected CountDownLatch latch;
    private TokenTracker tracker;
    private AccountToken token;
    private Activity activity;

    @Rule
    public ActivityTestRule<LoginSelectorActivity> activityTestRule =
            new ActivityTestRule<>(LoginSelectorActivity.class);

    @Before
    public void setUp() throws Exception {
        activity = activityTestRule.getActivity();
        LIPSdk.initialize(activity.getApplication(),LipSdkTest.getConfiguration());
        latch = new CountDownLatch(1);
        tracker = new TokenTracker(latch);
        tracker.startTracking();
        token = null;
    }

    @After
    public void tearDown() throws Exception {
        tracker.stopTracking();
        activity.finish();
    }

    private class TokenTracker extends AccountTokenTracker {
        private CountDownLatch countLatch;
        public TokenTracker(CountDownLatch latch) {
            this.countLatch = latch;
        }
        @Override
        protected void onAccountTokenChanged(AccountToken accountToken) {
            token = accountToken;
            countLatch.countDown();
        }
    }

    @Test
    public void testAccountTokenTracker() throws Exception {
        final SignInResponse response = new SignInResponse();
        response.setAccessToken("asfasfasd");
        response.setIdToken("adasfsdjflkjdf");
        response.setRefreshToken("dasdksahdksadsa");
        response.setExpireTime("3600");

        assertNull(token);

        AccountManager.setCurrentAccountToken(response, null);

        latch.await(10000, TimeUnit.MILLISECONDS);
        assertNotNull(token);
        assertEquals(token.getAccessToken(), response.getAccessToken());
        assertEquals(token.getIdToken(), response.getIdToken());
    }
}