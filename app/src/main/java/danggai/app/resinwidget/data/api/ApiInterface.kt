package danggai.app.resinwidget.data.api

import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.data.res.ResDailyNote
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @Headers(
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
    )
    @GET("/game_record/genshin/api/dailyNote?server=os_asia")
    fun dailyNote(@Query("role_id") uid: String, @Header("Cookie") cookie: String, @Header("DS") ds: String): Observable<Response<ResDailyNote.Data>>

}