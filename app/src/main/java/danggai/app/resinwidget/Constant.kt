package danggai.app.resinwidget

object Constant {

    /* URL */

    const val BASE_URL = ""



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



    /* ACTION */

    const val ACTION_BUTTON_REFRESH = "danggai.app.resinwidget.btn_refresh"
}