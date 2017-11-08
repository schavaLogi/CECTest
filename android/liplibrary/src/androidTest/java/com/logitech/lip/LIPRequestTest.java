package com.logitech.lip;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.network.IListener;
import com.logitech.lip.network.LIPRequest;
import com.logitech.lip.network.MockNetwork;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.network.WaitableQueue;
import com.logitech.lip.ui.login.LoginSelectorActivity;
import com.logitech.lip.volley.ExecutorDelivery;
import com.logitech.lip.volley.NetworkDispatcher;
import com.logitech.lip.volley.NetworkError;
import com.logitech.lip.volley.NetworkResponse;
import com.logitech.lip.volley.ServerError;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;


@RunWith(AndroidJUnit4.class)
public class LIPRequestTest {

    private Activity activity;
    private CountDownLatch latch;
    private boolean isRequestSuccess = false;

    @Rule
    public ActivityTestRule<LoginSelectorActivity> activityTestRule =
            new ActivityTestRule<>(LoginSelectorActivity.class);

    @Before
    public void setUp() throws Exception {
        activity = activityTestRule.getActivity();
        LIPSdk.initialize(activity.getApplication(),LipSdkTest.getConfiguration());
        latch = new CountDownLatch(1);
        isRequestSuccess = false;
    }

    @After
    public void tearDown() throws Exception {
        activity.finish();
    }

    @Test
    public void testSimpleSuccessNetworkRequest() throws Exception {
        WaitableQueue queue = new WaitableQueue();
        MockNetwork network = new MockNetwork();
        NetworkDispatcher mDispatcher = new NetworkDispatcher(queue, network , null,
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
        mDispatcher.start();
        network.setDataToReturn("{\"key\": \"value\"}".getBytes());
        final LIPRequest<Object> request = new LIPRequest<>("http://foo",
                Object.class, LIPRequest.Method.GET, null,
                new ServiceListener<>(latch, IListener.ErrorCode.ERROR_CODE_INTERNAL));
        request.setShouldCache(false);
        queue.add(request);
        latch.await();
        assertTrue(isRequestSuccess);
    }

    @Test
    public void testSimpleNetworkErrorRequest() throws Exception {
        WaitableQueue queue = new WaitableQueue();
        MockNetwork network = new MockNetwork();
        NetworkDispatcher mDispatcher = new NetworkDispatcher(queue, network , null,
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
        mDispatcher.start();
        network.setDataToReturn("{\"key\": \"value\"}".getBytes());
        network.setExceptionToThrow(new NetworkError(
                new NetworkResponse(400, "No Network".getBytes(),
                        Collections.<String, String>emptyMap(), false, 0)
        ));

        final LIPRequest<Object> request = new LIPRequest<>("http://foo",
                Object.class, LIPRequest.Method.GET, null,
                new ServiceListener<>(latch, IListener.ErrorCode.NETWORK_ERROR));
        request.setShouldCache(false);
        queue.add(request);
        latch.await();
        assertFalse(isRequestSuccess);
    }

    @Test
    public void testSimpleServerErrorRequest() throws Exception {
        WaitableQueue queue = new WaitableQueue();
        MockNetwork network = new MockNetwork();
        NetworkDispatcher mDispatcher = new NetworkDispatcher(queue, network , null,
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
        mDispatcher.start();
        network.setDataToReturn("{\"key\": \"value\"}".getBytes());
        network.setExceptionToThrow(new ServerError(
                new NetworkResponse(409,"duplicated token".getBytes(),
                        Collections.<String, String>emptyMap(), false, 0)
        ));

        final LIPRequest<Object> request = new LIPRequest<>("http://foo",
                Object.class, LIPRequest.Method.GET, null,
                new ServiceListener<>(latch, IListener.ErrorCode.SERVER_ACCOUNT_ALREADY_EXISTS));
        request.setShouldCache(false);
        queue.add(request);
        latch.await();
        assertFalse(isRequestSuccess);
    }

    @Test
    public void testSimpleSuccessPostNetworkRequest() throws Exception {
        WaitableQueue queue = new WaitableQueue();
        MockNetwork network = new MockNetwork();
        NetworkDispatcher mDispatcher = new NetworkDispatcher(queue, network , null,
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
        mDispatcher.start();
        network.setDataToReturn("{\"key\": \"value\"}".getBytes());
        final LIPRequest<Object> request = new LIPRequest<>("http://foo",
                Object.class, LIPRequest.Method.POST, null,new ServiceListener<>(latch,
                IListener.ErrorCode.ERROR_CODE_INTERNAL));
        request.setShouldCache(false);
        queue.add(request);
        latch.await();
        assertTrue(isRequestSuccess);
    }


    private class ServiceListener<T> extends ResponseListener<T> {
        private CountDownLatch latch;
        private ErrorCode errorCode;
        public ServiceListener(CountDownLatch latch, ErrorCode errorCode){
            this.latch = latch;
            this.errorCode = errorCode;
        }
        @Override
        public void onSuccess(T result) {
            isRequestSuccess = true;
            latch.countDown();
        }

        @Override
        public void onError(ErrorCode errorCode, String errorMessage) {
            isRequestSuccess = false;
            latch.countDown();
            assertEquals(this.errorCode, errorCode);
        }
    }
}
