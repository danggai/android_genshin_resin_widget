package danggai.app.resinwidget.data.local

data class GameRecordCard (
    val background_image: String,
    val data: List<InnerData>,
    val data_switches: List<DataSwitch>,
    val game_id: Int,
    val game_role_id: String,
    val h5_data_switches: List<Any>,
    val has_role: Boolean,
    val is_public: Boolean,
    val level: Int,
    val nickname: String,
    val region: String,
    val region_name: String,
    val url: String
) {

    data class DataSwitch(
        val is_public: Boolean,
        val switch_id: Int,
        val switch_name: String
    )

    data class InnerData(
        val name: String,
        val type: Int,
        val value: String
    )
}

//{
//    "retcode":0,
//    "message":"OK",
//    "data":{
//        "list":[{
//            "has_role":true,
//            "game_id":2,
//            "game_role_id":"825197716",
//            "nickname":"DangGai",
//            "region":"os_asia",
//            "level":56,
//            "background_image":"https://upload-os-bbs.hoyolab.com/game_record/game_record_ys_background.png",
//            "is_public":true,
//            "data":[
//                {"name":"활동 일수","type":1,"value":"265"},
//                {"name":"획득한 캐릭터 개수","type":1,"value":"38"},
//                {"name":"업적 달성 개수","type":1,"value":"446"},
//                {"name":"나선 비경","type":1,"value":"12-3"}
//                   ],
//            "region_name":"Asia Server",
//            "url":"https://webstatic-sea.mihoyo.com/app/community-game-records-sea/m.html?bbs_presentation_style=fullscreen\u0026bbs_auth_required=true\u0026v=101\u0026gid=2\u0026user_id=37037160",
//            "data_switches":[
//                {"switch_id":1,"is_public":true,"switch_name":"개인센터에 캐릭터 전적 전시"},
//                {"switch_id":2,"is_public":true,"switch_name":"전적 페이지에 캐릭터 정보 공개 여부"},
//                {"switch_id":3,"is_public":true,"switch_name":"'실시간 메모' 페이지를 열어 관련 게임 데이터를 확인하시겠나요?"}
//                            ],
//            "h5_data_switches":[]
//        },
//        {
//            "has_role":true,
//            "game_id":1,
//            "game_role_id":"12357877",
//            "nickname":"당가이",
//            "region":"kr01",
//            "level":63,
//            "background_image":"https://upload-os-bbs.mihoyo.com/game_record/honkai3rd/background.png",
//            "is_public":true,
//            "data":[
//                {"name":"누적 로그인","type":1,"value":"852"},
//                {"name":"보유 중인 성흔","type":1,"value":"205"},
//                {"name":"보유 중인 슈트","type":1,"value":"33"},
//                {"name":"코스튬 수","type":1,"value":"45"}
//            ],
//            "region_name":"KR Server",
//            "url":"https://webstatic-sea.mihoyo.com/app/community-game-records-sea/m.html?bbs_presentation_style=fullscreen\u0026bbs_auth_required=true\u0026v=101\u0026gid=1\u0026user_id=37037160",
//            "data_switches":[
//                {"switch_id":1,"is_public":true,"switch_name":"개인센터에 캐릭터 전적 전시"},
//                {"switch_id":2,"is_public":true,"switch_name":"전적 페이지에 캐릭터 정보 공개 여부"}
//            ],
//            "h5_data_switches":[]
//        }]
//    }
//}