package iBeaconServer;

public class JSON {

    public static final String KEY_USER_NAME = "USER_NAME" ;
    public static final String KEY_USER_PWD = "USER_PWD" ;
    public static final String KEY_RSSI = "RSSI" ;
    public static final String KEY_UUID = "UUID" ;
    public static final String KEY_MAJOR = "MAJOR" ;
    public static final String KEY_MINOR = "MINOR" ;
    public static final String KEY_RESULT = "result";
    public static final String KEY_RESULT_MESSAGE = "resultMessage";
    public static final String KEY_LOCATION = "LOCATION";
    public static final String KEY_USER_LIST = "用戶清單";
    public static final String KEY_BINDING = "BINDING";
    public static final String KEY_TARGET_LOCATION = "targetLocation";
    public static final String KEY_ITEM_NAME = "itemName";
    public static final String KEY_ITEM_LIST = "itemList";


    public static final String KEY_LOGIN_SUCCESS = "登入成功";
    public static final String KEY_LOGOUT_SUCCESS = "登出成功";
    public static final String KEY_LOGIN_FAIL = "帳號密碼有誤";
    public static final String KEY_LOGOUT_FAIL = "登出失敗，請檢查網路連線並再嘗試一次";


    public static final String KEY_STATE = "STATE" ;
    public static final int STATE_LOGOUT = 0 ;
    public static final int STATE_SEND_IBEACON = 1 ;
    public static final int STATE_LOGIN = 2 ;
    public static final int STATE_WHOAMI = 3 ;
    public static final int STATE_FIND_FRIEND = 4 ;
    public static final int STATE_CAR_BINDING = 5 ;
    public static final int STATE_USER_MOVE = 6 ;
    public static final int STATE_FIND_TARGET_LOCATION = 7 ;
    public static final int STATE_FIND_ITEM_LIST = 8 ;
    public static final int STATE_FIND_ITEM = 9 ;
    public static final int STATE_ASK_LOCATION_PERMISSION  = 10 ;

}
