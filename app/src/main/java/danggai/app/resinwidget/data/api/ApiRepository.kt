package danggai.app.resinwidget.data.api

import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.data.res.Meta
import danggai.app.resinwidget.data.res.ResChangeDataSwitch
import danggai.app.resinwidget.data.res.ResDailyNote
import danggai.app.resinwidget.util.CommonFunction
import io.reactivex.Observable
import retrofit2.http.Query

class ApiRepository(private val api: ApiInterface) {

    fun dailyNote(uid: String, cookie: String): Observable<ResDailyNote> {
        val emptyData = ResDailyNote.Data("","", DailyNote( -1,-1,"",-1, -1, listOf()))

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

    fun changeDataSwitch(gameId: Int, switchId: Int, isPublic: Boolean, cookie: String): Observable<ResChangeDataSwitch> {
        val emptyData = ResChangeDataSwitch.Data("","")

        return Observable.just(true)
            .switchMap {
                api.changeDataSwitch(gameId, switchId, isPublic, cookie, CommonFunction.getGenshinDS())
            }
            .map { res ->
                when {
                    res.isSuccessful -> {
                        ResChangeDataSwitch(Meta(res.code(), res.message()), res.body()?:emptyData)
                    } else -> {
                        ResChangeDataSwitch(Meta(res.code(), res.message()), emptyData)
                    }
                }
            }
    }

}