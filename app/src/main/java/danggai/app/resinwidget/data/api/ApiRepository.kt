package danggai.app.resinwidget.data.api

import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.data.res.Meta
import danggai.app.resinwidget.data.res.ResDailyNote
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.log
import io.reactivex.Observable

class ApiRepository(private val api: ApiInterface) {

    fun dailyNote(uid: String, cookie: String): Observable<ResDailyNote> {
        val emptyData = DailyNote(-1,-1,"",-1, -1, listOf())

        return Observable.just(true)
            .switchMap {
                api.dailyNote(uid, cookie, CommonFunction.getGenshinDS())
            }
            .map { res ->
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