package com.logitech.lip;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.ui.login.LoginSelectorActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SecPrefTest {
    private SecurePrefManager pref;
    private Activity activity;

    @Rule
    public ActivityTestRule<LoginSelectorActivity> activityTestRule =
            new ActivityTestRule<>(LoginSelectorActivity.class);

    @Before
    public void setUp() throws Exception {
        activity = activityTestRule.getActivity();
        pref = new SecurePrefManager(activity);
    }

    @After
    public void tearDown() throws Exception {
        activity.finish();
    }

    @Test
    public void testAllPublicMethods() throws Exception {
        SecurePrefManager.class.getConstructor(Context.class);
        assertNotNull(SecurePrefManager.class.getMethod("saveData", String.class, String.class, boolean.class));
        assertNotNull(SecurePrefManager.class.getMethod("getData",String.class, String.class, boolean.class));
        assertNotNull(SecurePrefManager.class.getMethod("clearKey",String.class));
        assertNotNull(SecurePrefManager.class.getMethod("clearAll"));
    }

    @Test
    public void testSmallSizeDataEncryption(){
        String actualData = "Hello";
        pref.saveData("KEY1", actualData, true);

        String readData = pref.getData("KEY1",null,false);
        boolean result = readData.equals(actualData);
        assertFalse(result);

        readData = pref.getData("KEY1",null,true);
        result = readData.equals(actualData);
        assertTrue(result);
    }

    @Test
    public void testBigSizeDataEncryption(){
        String actualData = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImE0ODhkOWRkNGJjOTg1MmQ1YzkwZGFhMTY4N2FkMTNiYWRlNGJiZmMifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXRfaGFzaCI6InJubFFnVXFtMXZVcDM4bng0RGR3bHciLCJhdWQiOiI1OTYxNzcwNTUwNS1lYzd1OHRvbTgwcjN1ZnNtajBuMW5xZGxsNnFvOWRzYS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExNTA3MzQ2Njg5NzMxNDAzNTA3MiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI1OTYxNzcwNTUwNS1lYzd1OHRvbTgwcjN1ZnNtajBuMW5xZGxsNnFvOWRzYS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImVtYWlsIjoidGhlYm91bmRsZXNzc0BnbWFpbC5jb20iLCJpYXQiOjE0NDY1NTI0ODYsImV4cCI6MTQ0NjU1NjA4Nn0.YEBztRVDdh-w1rF5bogoXESGWW_3DT5SvcGSYyi_6bXeJIgcAiJsgxceKRmcNkocm2cRaAqKh9rm7sCkeL_ZSYkxh4w6LCEHTdPU6HpxAJx6iLXB1ILyrHHCJ4AA-VfFe2mfi0f8J12v6EOkLV2COmjBY5PTpUUe9dJbKuq_VEtDVplZynwO4UVOJJ0fHtdORwieDsif0zUIAQXxVeuTYIMhMElkeY7tFE7Gfy8ZzR32jlTxEpQqQd_15IK34XjJfYC592nXCxnyxSMLUhjd-rKzVG3wuJYR8v-ZVphu85mZ8i9m599qpMQRbKEAggWxv3sLOvevX4QU9q7Q6P1eGQ";
        pref.saveData("KEY1", actualData, true);

        String readData = pref.getData("KEY1",null,false);
        boolean result = readData.equals(actualData);
        assertFalse(result);

        readData = pref.getData("KEY1",null,true);
        result = readData.equals(actualData);
        assertTrue(result);
    }
}
