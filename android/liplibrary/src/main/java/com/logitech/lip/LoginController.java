package com.logitech.lip;

import android.app.Activity;
import android.content.Intent;

import com.logitech.lip.account.AccountManager;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.ui.login.LoginSelectorActivity;
import com.logitech.lip.ui.login.LoginSelectorTabletActivity;

/**
 * This class manages login & logout for LIP \n.
 * <p>
 *     Designed as single point of contact in case we are accessing in multiple places make sure the listener should reset
 *     Mainly when accessed from Activity or Fragment scope reset listener when going out of scope
 * </p>
 */
public final class LoginController {

    private static final String TAG = LoginController.class.getSimpleName();

    private static LoginController INSTANCE;

    private LoginListener refListener;

    private LoginController(){
    }

    public static LoginController getInstance(){
        LIPSdk.isInitialized();
        if( INSTANCE == null ) {
            synchronized (LoginController.class) {
                if( INSTANCE == null ) {
                    INSTANCE = new LoginController();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Returns the login listener
     * @return LoginListener
     */
    public LoginListener getListener(){
        return refListener;
    }

    /**
     * Registers the login listener, In case listener is null it will act as Un-register
     * Will hold strong reference of Object. Unregister once we are done and
     * @param listener LoginListener
     * <p>
     *  Note: To avoid memory leak use registerLoginListener(null) this acts as unregister.
     *
     * </p>
     */
    public void registerLoginListener(LoginListener listener ){
        this.refListener = listener;
    }


    /**
     * This will provide available login options activity
     * @param activity parent activity
     * @param loginOptions Login options
     */
    public void requestLogin(Activity activity, LoginOptions loginOptions){
        if(activity != null) {
            Logger.info(TAG, "requestLogin", "requestLogin");
            boolean isTablet = activity.getResources().getBoolean(R.bool.isTablet);
            Intent intent;
            if(isTablet) {
                intent = new Intent(activity, LoginSelectorTabletActivity.class);
            } else {
                intent = new Intent(activity, LoginSelectorActivity.class);
            }
            intent.putExtra(LoginSelectorActivity.LOGIN_OPTIONS, loginOptions);
            activity.startActivity(intent);
        }
    }

    /**
     * This will provide available login options activity
     * Will launch default Login Mode, Validated details and Tokens will be saved
     * @param activity parent activity
     */
    public void requestLogin(Activity activity){
        LoginOptions loginOptions = new LoginOptions.Builder()
                .setIsCreate(false)
                .setIsPersistToken(true)
                .setIsValidate(true)
                .build();
        requestLogin(activity, loginOptions);
        //requestLogin(activity, false, true, true);
    }

    /**
     * This will provide available login options activity
     * Will launch default Login Mode, Validation & Tokens save based on params
     * @param activity parent activity
     * @param isPersistTokens  to indicate tokens should be persisted or not
     *                         This option will effect the refresh token mechanism when tokens not saved
     *                         TRUE - To save tokens locally for future purpose
     *                         FALSE - Not to save tokens
     * @param isValidate  to show failure message or not
     *                         TRUE - Will show failure reasons in screen
     *                         FALSE - Will not show failure reasons in screen
     */
   /* public void requestLogin(Activity activity,
                             boolean isPersistTokens,
                             boolean isValidate){
        requestLogin(activity, false, isPersistTokens, isValidate);
    }*/

    /**
     * This will provide available login options activity
     * Will launch default Login Mode and Tokens will be saved
     * @param activity parent activity
     * @param isPersistTokens  to indicate tokens should be persisted or not
     *                         This option will effect the refresh token mechanism when tokens not saved
     *                         TRUE - To save tokens locally for future purpose
     *                         FALSE - Not to save tokens
     * @param isValidate  to show failure message or not
     *                         TRUE - Will show failure reasons in screen
     *                         FALSE - Will not show failure reasons in screen
     *
     */
   /* private void requestLogin(Activity activity,
                              boolean createMode,
                              boolean isPersistTokens,
                              boolean isValidate){

        Log.d(TAG, "requestLogin createMode =" + createMode +
                "isPersistTokens=" + isPersistTokens +
                "isValidate=" + isValidate);

        if(activity != null) {
            Intent intent = new Intent(activity, LoginSelectorActivity.class);
            intent.putExtra(LoginSelectorActivity.LOGIN_INIT_MODE, createMode);
            intent.putExtra(LoginSelectorActivity.LOGIN_TOKEN_PERSIST, isPersistTokens);
            intent.putExtra(LoginSelectorActivity.LOGIN_VALIDATE, isValidate);
            activity.startActivity(intent);
        }
    }*/

    /**
     * This will provide available login options activity
     * Will launch Create or login based on create Mode.
     * Will validate failure reasons and Tokens will be saved
     * @param activity parent activity
     * @param createMode  launch mode
     *                    TRUE - create
     *                    FALSE - Login
     */
    public void requestLogin(Activity activity, boolean createMode){
        LoginOptions loginOptions = new LoginOptions.Builder()
                .setIsCreate(createMode)
                .setIsPersistToken(true)
                .setIsValidate(true)
                .build();
        requestLogin(activity, loginOptions);
        //requestLogin(activity, createMode, true, true);
    }

    /**
     * This will provide available login options activity
     * Will launch Create or login based on Mode
     * Tokens persist or not will be based on isPersistTokens
     * @param activity parent activity
     * @param createMode  launch mode
     *                    TRUE - create
     *                    FALSE - Login
     * @param isPersistTokens  to indicate tokens should be persisted or not
     *                         This option will effect the refresh token mechanism when tokens not saved
     *                         TRUE - To save tokens locally for future purpose
     *                         FALSE - Not to save tokens
     */


    /**
     * Will clear all saved tokens, Should not use previous token any more
     * @param bAllSessions  TRUE - to clear All sessions
     */
    public void requestLogout(final boolean bAllSessions){
        Logger.debug(TAG, "requestLogout","bAllSessions =" + bAllSessions);
        AccountManager.getCurrentAccountToken(false,
                new ResponseListener<AccountToken>() {
                    @Override
                    public void onSuccess(AccountToken result) {
                        AccountManager.signOut(bAllSessions, result.getAccessToken(), null);
                        AccountManager.setCurrentAccountToken(null, null);
                    }

                    @Override
                    public void onError(ErrorCode errorCode, String errorMessage) {
                        AccountManager.setCurrentAccountToken(null, null);
                    }
                });

    }
}
