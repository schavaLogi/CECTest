package com.logitech.lip;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.ui.login.LoginSelectorActivity;
import com.logitech.lip.utility.KeyUtility;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class KeyUtilityTest {

    private Activity activity;

    @Rule
    public ActivityTestRule<LoginSelectorActivity> activityTestRule =
            new ActivityTestRule<>(LoginSelectorActivity.class);

    @Before
    public void setUp() throws Exception {
        activity = activityTestRule.getActivity();
        LIPSdk.initialize(activity.getApplication(),LipSdkTest.getConfiguration());
    }

    @After
    public void tearDown() throws Exception {
        activity.finish();
    }

    @Test
    public void testAllPublicMethods() throws Exception {

        assertNotNull(KeyUtility.class.getMethod("createNewKeys", Context.class));

        assertNotNull(KeyUtility.class.getMethod("encryptString", String.class));
        assertNotNull(KeyUtility.class.getMethod("decryptString", String.class));

        assertNotNull(KeyUtility.class.getMethod("simpleAesEncrypt", String.class, String.class));
        assertNotNull(KeyUtility.class.getMethod("simpleAesDecrypt", String.class, String.class));
    }

    @Test
    public void testBigDataRSAEncrypt() {
        String actualData = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImE0ODhkOWRkNGJjOTg1MmQ1YzkwZGFhMTY4N2FkMTNiYWRlNGJiZmMifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXRfaGFzaCI6InJubFFnVXFtMXZVcDM4bng0RGR3bHciLCJhdWQiOiI1OTYxNzcwNTUwNS1lYzd1OHRvbTgwcjN1ZnNtajBuMW5xZGxsNnFvOWRzYS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExNTA3MzQ2Njg5NzMxNDAzNTA3MiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI1OTYxNzcwNTUwNS1lYzd1OHRvbTgwcjN1ZnNtajBuMW5xZGxsNnFvOWRzYS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImVtYWlsIjoidGhlYm91bmRsZXNzc0BnbWFpbC5jb20iLCJpYXQiOjE0NDY1NTI0ODYsImV4cCI6MTQ0NjU1NjA4Nn0.YEBztRVDdh-w1rF5bogoXESGWW_3DT5SvcGSYyi_6bXeJIgcAiJsgxceKRmcNkocm2cRaAqKh9rm7sCkeL_ZSYkxh4w6LCEHTdPU6HpxAJx6iLXB1ILyrHHCJ4AA-VfFe2mfi0f8J12v6EOkLV2COmjBY5PTpUUe9dJbKuq_VEtDVplZynwO4UVOJJ0fHtdORwieDsif0zUIAQXxVeuTYIMhMElkeY7tFE7Gfy8ZzR32jlTxEpQqQd_15IK34XjJfYC592nXCxnyxSMLUhjd-rKzVG3wuJYR8v-ZVphu85mZ8i9m599qpMQRbKEAggWxv3sLOvevX4QU9q7Q6P1eGQ";
        String encData = KeyUtility.encryptString(actualData);
        assertNotNull(encData);
        assertFalse(actualData.equals(encData));

        String decData = KeyUtility.decryptString(encData);
        assertNotNull(decData);

        assertFalse(decData.equals(encData));

        assertTrue(actualData.equals(decData));
    }

    @Test
    public void testSmallDataRSAEncrypt() {
        String actualData = "Simple test data....  ";
        String encData = KeyUtility.encryptString(actualData);
        assertNotNull(encData);
        assertFalse(actualData.equals(encData));

        String decData = KeyUtility.decryptString(encData);
        assertNotNull(decData);

        assertFalse(decData.equals(encData));

        assertTrue(actualData.equals(decData));
    }

    @Test
    public void testBigDataAESEncrypt() throws Exception {
        final String SEED = "sadoiasiodw21083021@#1312637";
        String actualData = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImE0ODhkOWRkNGJjOTg1MmQ1YzkwZGFhMTY4N2FkMTNiYWRlNGJiZmMifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXRfaGFzaCI6InJubFFnVXFtMXZVcDM4bng0RGR3bHciLCJhdWQiOiI1OTYxNzcwNTUwNS1lYzd1OHRvbTgwcjN1ZnNtajBuMW5xZGxsNnFvOWRzYS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExNTA3MzQ2Njg5NzMxNDAzNTA3MiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI1OTYxNzcwNTUwNS1lYzd1OHRvbTgwcjN1ZnNtajBuMW5xZGxsNnFvOWRzYS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImVtYWlsIjoidGhlYm91bmRsZXNzc0BnbWFpbC5jb20iLCJpYXQiOjE0NDY1NTI0ODYsImV4cCI6MTQ0NjU1NjA4Nn0.YEBztRVDdh-w1rF5bogoXESGWW_3DT5SvcGSYyi_6bXeJIgcAiJsgxceKRmcNkocm2cRaAqKh9rm7sCkeL_ZSYkxh4w6LCEHTdPU6HpxAJx6iLXB1ILyrHHCJ4AA-VfFe2mfi0f8J12v6EOkLV2COmjBY5PTpUUe9dJbKuq_VEtDVplZynwO4UVOJJ0fHtdORwieDsif0zUIAQXxVeuTYIMhMElkeY7tFE7Gfy8ZzR32jlTxEpQqQd_15IK34XjJfYC592nXCxnyxSMLUhjd-rKzVG3wuJYR8v-ZVphu85mZ8i9m599qpMQRbKEAggWxv3sLOvevX4QU9q7Q6P1eGQ";

        String encData = KeyUtility.simpleAesEncrypt(SEED, actualData);
        assertNotNull(encData);
        assertFalse(actualData.equals(encData));

        String decData = KeyUtility.simpleAesDecrypt(SEED, encData);
        assertNotNull(decData);

        assertFalse(decData.equals(encData));

        assertTrue(actualData.equals(decData));
    }

    @Test
    public void testSmallDataAESEncrypt() throws Exception {
        final String SEED = "sadoiasiodw21083021@#1312637";
        String actualData = "Simple test data....  ";
        String encData = KeyUtility.simpleAesEncrypt(SEED, actualData);
        assertNotNull(encData);
        assertFalse(actualData.equals(encData));

        String decData = KeyUtility.simpleAesDecrypt(SEED, encData);
        assertNotNull(decData);

        assertFalse(decData.equals(encData));

        assertTrue(actualData.equals(decData));
    }
}
