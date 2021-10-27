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



    /* FORMAT, REGEX */

    const val PATTERN_ENG_NUM_ONLY = "^[a-zA-Z0-9]+$"
    const val PATTERN_NUM_ONLY = "[^\\d]"

    const val DATE_FORMAT_BEFORE = "yyyy-MM-dd'T'HH:mm:ssX"
    const val DATE_FORMAT_AFTER = "yyyy-MM-dd HH:mm"



    /* PREFERENCE */

    const val PREF_COOKIE = "PREF_COOKIE"
    const val PREF_UID = "PREF_UID"



    /* ACTION */

    const val ACTION_BUTTON_REFRESH = "danggai.app.resinwidget.btn_refresh"

    const val BACK_BUTTON_INTERVAL: Long = 2000

}