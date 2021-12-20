package danggai.app.resinwidget

object Constant {

    /* URL */

    const val OS_TAKUMI_URL = "https://bbs-api-os.mihoyo.com"
    const val CN_TAKUMI_URL = "https://api-takumi.mihoyo.com"

    const val OS_SALT = "6cqshh5dhw73bzxn20oexa9k516chk7s"
    const val CN_SALT = "xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs"

    const val OS_CHECK_IN_URL = "https://hk4e-api-os.mihoyo.com/event/sol/sign"
    const val OS_ACT_ID = "e202102251931481"
    const val CN_CHECK_IN_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/"
    const val CN_ACT_ID = "e202009291139501"

    const val SERVER_CN_GF_01 = "cn_gf01"
    const val SERVER_CN_QD_01 = "cn_qd01"
    const val SERVER_OS_USA = "os_usa"
    const val SERVER_OS_EURO = "os_euro"
    const val SERVER_OS_ASIA = "os_asia"
    const val SERVER_OS_CHT = "os_cht"

    /* HTTP STATUS CODE */

    const val META_CODE_SUCCESS = 200
    const val META_CODE_BAD_REQUEST = 400
    const val META_CODE_NOT_FOUND = 404
    const val META_CODE_SERVER_ERROR = 500


    /* RETCODE */

    const val RETCODE_SUCCESS = "0"
    const val RETCODE_ERROR_CHARACTOR_INFO = "1009"
    const val RETCODE_ERROR_INTERNAL_DATABASE_ERROR = "-1"
    const val RETCODE_ERROR_TOO_MANY_REQUESTS = "10101"
    const val RETCODE_ERROR_NOT_LOGGED_IN = "-100"
    const val RETCODE_ERROR_NOT_LOGGED_IN_2 = "10001"
    const val RETCODE_ERROR_NOT_LOGGED_IN_3 = "10103"
    const val RETCODE_ERROR_DATA_NOT_PUBLIC = "10102"
    const val RETCODE_ERROR_ACCOUNT_NOT_FOUND = "-10002"
    const val RETCODE_ERROR_INVALID_LANGUAGE = "-108"

    const val RETCODE_ERROR_CLAIMED_DAILY_REWARD = "-5003"
    const val RETCODE_ERROR_CHECKED_INTO_HOYOLAB = "2001"
    const val RETCODE_ERROR_TOO_FAST = "-1004"


    /* API NAME */

    const val API_NAME_DAILY_NOTE = "Daily Note"
    const val API_NAME_CHANGE_DATA_SWITCH = "Change Data Switch"
    const val API_NAME_CHECK_IN = "Check In"



    /* WORKER */

    const val WORKER_UNIQUE_NAME_AUTO_REFRESH = "AutoRefreshWork"
    const val WORKER_UNIQUE_NAME_AUTO_CHECK_IN = "AutoCheckInWork"



    /* FORMAT, REGEX */

    const val PATTERN_ENG_NUM_ONLY = "^[a-zA-Z0-9]+$"
    const val PATTERN_NUM_ONLY = "[^\\d]"

    const val DATE_FORMAT_SYNC_TIME = "MM/dd HH:mm"



    /* PREFERENCE */

    const val PREF_COOKIE = "PREF_COOKIE"
    const val PREF_UID = "PREF_UID"

    const val PREF_FIRST_LAUNCH = "PREF_FIRST_LAUNCH"

    const val PREF_IS_VALID_USERDATA = "PREF_IS_VALID_USERDATA"

    const val PREF_AUTO_REFRESH_PERIOD = "PREF_AUTO_REFRESH_PERIOD"
    const val PREF_TIME_NOTATION = "PREF_TIME_NOTATION"
    const val PREF_WIDGET_THEME = "PREF_WIDGET_THEME"
    const val PREF_WIDGET_BACKGROUND_TRANSPARENCY = "PREF_WIDGET_BACKGR OUND_TRANSPARENCY"

    const val PREF_CURRENT_RESIN = "PREF_CURRENT_RESIN"
    const val PREF_MAX_RESIN = "PREF_MAX_RESIN"
    const val PREF_RESIN_RECOVERY_TIME = "PREF_RESIN_RECOVERY_TIME"
    const val PREF_RECENT_SYNC_TIME = "PREF_RECENT_SYNC_TIME"

    const val PREF_NOTI_EACH_40_RESIN = "PREF_NOTI_EACH_40_RESIN"
    const val PREF_NOTI_140_RESIN = "PREF_NOTI_140_RESIN"
    const val PREF_NOTI_CUSTOM_RESIN_BOOLEAN = "PREF_NOTI_CUSTOM_RESIN_BOOLEAN"
    const val PREF_NOTI_CUSTOM_TARGET_RESIN = "PREF_NOTI_CUSTOM_RESIN_INT"

    const val PREF_ENABLE_AUTO_CHECK_IN = "PREF_ENABLE_AUTO_CHECK_IN"
    const val PREF_NOTI_CHECK_IN_SUCCESS = "PREF_NOTI_CHECK_IN_SUCCESS"
    const val PREF_NOTI_CHECK_IN_FAILED = "PREF_NOTI_CHECK_IN_FAILED"

    const val PREF_DEFAULT_REFRESH_PERIOD = 15L

    const val PREF_SERVER_ASIA = 0
    const val PREF_SERVER_USA = 1
    const val PREF_SERVER_EUROPE = 2
    const val PREF_SERVER_CHT = 3

    const val PREF_TIME_NOTATION_REMAIN_TIME = 0
    const val PREF_TIME_NOTATION_FULL_CHARGE_TIME = 1

    const val PREF_WIDGET_THEME_AUTOMATIC = 0
    const val PREF_WIDGET_THEME_LIGHT = 1
    const val PREF_WIDGET_THEME_DARK = 2






    /* ID */

    const val PUSH_CHANNEL_DEFAULT_ID = "DEFAULT"
    const val PUSH_CHANNEL_RESIN_NOTI_ID = "RESIN_NOTIFICATION"
    const val PUSH_CHANNEL_CHECK_IN_ID = "CHECK_IN"



    /* ARGUMENT */

    const val ARG_IS_ONE_TIME = "ARG_IS_ONE_TIME"



    /* TIMEZONE */

    const val CHINA_TIMEZONE = "Asia/Shanghai"
    const val KOREA_TIMEZONE = "Asia/Seoul"



    /* ACTION */

    const val ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"

    const val REFRESH_UI = "REFRESH_UI_ONLY"
    const val REFRESH_DATA = "REFRESH_DATA"

    const val BACK_BUTTON_INTERVAL: Long = 1000

}