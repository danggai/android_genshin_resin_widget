package danggai.app.resinwidget.data.api

import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.data.res.Meta
import danggai.app.resinwidget.data.res.ResDailyNote
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.log
import io.reactivex.Observable
import java.util.*
import kotlin.math.floor

class ApiRepository(private val api: ApiInterface) {

    fun dailyNote(uid: String, cookie: String): Observable<ResDailyNote> {
        val emptyData = DailyNote(-1,"","","")

        val date = Date()
        val epoch = floor((date.time / 1000).toDouble())
        val hash = CommonFunction.encodeToMD5("salt=6cqshh5dhw73bzxn20oexa9k516chk7s&t=${epoch}&r=abcdef")
        val ds = "${epoch},abcdef,${hash}"

        return Observable.just(true)
            .switchMap {
                api.dailyNote(uid, cookie, ds)
            }
            .map { res ->
                log.e(res)
                when {
                    res.isSuccessful -> {
                        ResDailyNote(Meta(res.code(), res.message()), res.body()?:emptyData)
                    } else -> {
                        ResDailyNote(Meta(res.code(), res.message()), emptyData)
                    }
                }
            }
    }

}