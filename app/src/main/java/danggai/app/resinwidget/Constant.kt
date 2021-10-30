package danggai.app.resinwidget

object Constant {

    /* URL */

    const val OS_TAKUMI_URL = "https://api-os-takumi.mihoyo.com"
    const val CN_TAKUMI_URL = "https://api-takumi.mihoyo.com"

    const val OS_SALT = "6cqshh5dhw73bzxn20oexa9k516chk7s"
    const val CN_SALT = "xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs"



    /* HTTP STATUS CODE */

    const val META_CODE_SUCCESS = 200
    const val META_CODE_BAD_REQUEST = 400
    const val META_CODE_NOT_FOUND = 404
    const val META_CODE_SERVER_ERROR = 500


    /* RETCODE */

    const val RETCODE_SUCCESS = "0"
    const val RETCODE_ERROR_CHARACTOR_INFO = "1009"       // 角色信息错误
    const val RETCODE_ERROR_INVALID_REQUEST = "-1"



    /* WORKER */

    const val WORKER_UNIQUE_NAME_AUTO_REFRESH = "AutoRefreshWork"



    /* FORMAT, REGEX */

    const val PATTERN_ENG_NUM_ONLY = "^[a-zA-Z0-9]+$"
    const val PATTERN_NUM_ONLY = "[^\\d]"

    const val DATE_FORMAT_SYNC_TIME = "MM/dd HH:mm"



    /* PREFERENCE */

    const val PREF_COOKIE = "PREF_COOKIE"
    const val PREF_UID = "PREF_UID"

    const val PREF_IS_VALID_USERDATA = "PREF_IS_VALID_USERDATA"

    const val PREF_AUTO_REFRESH_PERIOD = "PREF_AUTO_REFRESH_PERIOD"

    const val PREF_CURRENT_RESIN = "PREF_CURRENT_RESIN"
    const val PREF_MAX_RESIN = "PREF_MAX_RESIN"
    const val PREF_RESIN_RECOVERY_TIME = "PREF_RESIN_RECOVERY_TIME"
    const val PREF_RECENT_SYNC_TIME = "PREF_RECENT_SYNC_TIME"

    const val PREF_DEFAULT_REFRESH_PERIOD = 15L


    /* ACTION */

    const val ACTION_BUTTON_REFRESH = "danggai.app.resinwidget.btn_refresh"
    const val ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"

    const val REFRESH_UI = "REFRESH_UI_ONLY"
    const val REFRESH_DATA = "REFRESH_DATA"

    const val BACK_BUTTON_INTERVAL: Long = 2000

}