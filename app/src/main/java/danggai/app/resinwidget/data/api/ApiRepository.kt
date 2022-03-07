package danggai.app.resinwidget.data.api

import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.data.local.CheckIn
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.data.res.*
import danggai.app.resinwidget.util.CommonFunction
import io.reactivex.Observable

class ApiRepository(private val api: ApiInterface) {

    fun dailyNote(uid: String, server: String, cookie: String): Observable<ResDailyNote> {
        val emptyData = ResDailyNote.Data("","", DailyNote( -1,-1,"-1", -1, -1, false, -1, -1, -1, -1, "-1", -1, -1, listOf()))

        return Observable.just(true)
            .switchMap {
                api.dailyNote(uid, server, cookie, CommonFunction.getGenshinDS())
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

    fun changeDataSwitch(gameId: Int, switchId: Int, isPublic: Boolean, cookie: String): Observable<ResDefault> {
        val emptyData = ResDefault.Data("","")

        return Observable.just(true)
            .switchMap {
                api.changeDataSwitch(gameId, switchId, isPublic, cookie, CommonFunction.getGenshinDS())
            }
            .map { res ->
                when {
                    res.isSuccessful -> {
                        ResDefault(Meta(res.code(), res.message()), res.body()?:emptyData)
                    } else -> {
                        ResDefault(Meta(res.code(), res.message()), emptyData)
                    }
                }
            }
    }

    fun getCameRecordCard(hoyolabUid: String, cookie: String): Observable<ResGameRecordCard> {
        val emptyData = ResGameRecordCard.Data("","", ResGameRecordCard.GameRecordCardList(listOf()))
//        GameRecordCard("", listOf(), listOf(), "", "", listOf(), false, false, -1, "", "", "", "")

        return Observable.just(true)
            .switchMap {
                api.getGameRecordCard(hoyolabUid, cookie, CommonFunction.getGenshinDS())
            }
            .map { res ->
                when {
                    res.isSuccessful -> {
                        ResGameRecordCard(Meta(res.code(), res.message()), res.body()?:emptyData)
                    } else -> {
                        ResGameRecordCard(Meta(res.code(), res.message()), emptyData)
                    }
                }
            }
    }

    fun checkIn(region: String, cookie: String): Observable<ResCheckIn> {
        val emptyData = ResCheckIn.Data("","", CheckIn(""))

        val actId = Constant.OS_ACT_ID

        return Observable.just(true)
            .switchMap {
                api.checkIn(region, actId, cookie, CommonFunction.getGenshinDS())
            }
            .map { res ->
                when {
                    res.isSuccessful -> {
                        ResCheckIn(Meta(res.code(), res.message()), res.body()?:emptyData)
                    } else -> {
                        ResCheckIn(Meta(res.code(), res.message()), emptyData)
                    }
                }
            }
    }


}