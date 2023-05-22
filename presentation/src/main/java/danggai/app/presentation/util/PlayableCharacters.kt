package danggai.app.presentation.util

import danggai.app.presentation.R
import danggai.domain.local.Elements
import danggai.domain.local.LocalCharacter
import danggai.domain.util.Constant

val PlayableCharacters: List<LocalCharacter> = listOf(
    LocalCharacter(
        Constant.ID_AYAKA,
        "카미사토 아야카",
        "Kamisato Ayaka",
        5,
        Elements.CYRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_ayaka
    ),

    LocalCharacter(
        Constant.ID_JEAN,
        "진",
        "Jean",
        5,
        Elements.ANEMO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_jean
    ),

//    LocalCharacter(
//        Constant.ID_AITHER,
//        "아이테르",
//        "Aither",
//        5,
//        Elements.ANEMO,
//        Constant.TALENT_AREA_MONDSTADT,
//        Constant.TALENT_DATE_ALL,
//        R.drawable.icon_aither
//    ),
//
//    LocalCharacter(
//        Constant.ID_AITHER,
//        "아이테르",
//        "Aither",
//        5,
//        Elements.GEO,
//        Constant.TALENT_AREA_LIYUE,
//        Constant.TALENT_DATE_ALL,
//        R.drawable.icon_aither
//    ),
//
//    LocalCharacter(
//        Constant.ID_AITHER,
//        "아이테르",
//        "Aither",
//        5,
//        Elements.ELECTRO,
//        Constant.TALENT_AREA_INAZUMA,
//        Constant.TALENT_DATE_ALL,
//        R.drawable.icon_aither
//    ),

    LocalCharacter(
        Constant.ID_LISA,
        "리사",
        "Lisa",
        4,
        Elements.ELECTRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_lisa
    ),

//    LocalCharacter(
//        Constant.ID_LUMINE,
//        "루미네",
//        "Lumine",
//        5,
//        Elements.ANEMO,
//        Constant.TALENT_AREA_MONDSTADT,
//        Constant.TALENT_DATE_ALL,
//        R.drawable.icon_lumine
//    ),
//
//    LocalCharacter(
//        Constant.ID_LUMINE,
//        "루미네",
//        "Lumine",
//        5,
//        Elements.GEO,
//        Constant.TALENT_AREA_LIYUE,
//        Constant.TALENT_DATE_ALL,
//        R.drawable.icon_lumine
//    ),
//
//    LocalCharacter(
//        Constant.ID_LUMINE,
//        "루미네",
//        "Lumine",
//        5,
//        Elements.ELECTRO,
//        Constant.TALENT_AREA_INAZUMA,
//        Constant.TALENT_DATE_ALL,
//        R.drawable.icon_lumine
//    ),

    LocalCharacter(
        Constant.ID_BARBARA,
        "바바라",
        "Barbara",
        4,
        Elements.HYDRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_barbara
    ),

    LocalCharacter(
        Constant.ID_KEAYA,
        "케이아",
        "Keaya",
        4,
        Elements.CYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_keaya
    ),

    LocalCharacter(
        Constant.ID_DILUC,
        "다이루크",
        "Diluc",
        5,
        Elements.PYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_diluc
    ),

    LocalCharacter(
        Constant.ID_RAZOR,
        "레이저",
        "Razor",
        4,
        Elements.ELECTRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_razor
    ),

    LocalCharacter(
        Constant.ID_AMBER,
        "엠버",
        "Amber",
        4,
        Elements.PYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_amber
    ),

    LocalCharacter(
        Constant.ID_VENTI,
        "벤티",
        "Venti",
        5,
        Elements.ANEMO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_venti
    ),

    LocalCharacter(
        Constant.ID_XIANGLING,
        "향릉",
        "Xiangling",
        4,
        Elements.PYRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_xiangling
    ),

    LocalCharacter(
        Constant.ID_BEIDOU,
        "북두",
        "Beidou",
        4,
        Elements.ELECTRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_beidou
    ),

    LocalCharacter(
        Constant.ID_XINGQIU,
        "행추",
        "Xingqiu",
        4,
        Elements.HYDRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_xingqiu
    ),

    LocalCharacter(
        Constant.ID_XIAO,
        "소",
        "Xiao",
        5,
        Elements.ANEMO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_xiao
    ),

    LocalCharacter(
        Constant.ID_NINGGUANG,
        "응광",
        "Ningguang",
        4,
        Elements.GEO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_ningguang
    ),

    LocalCharacter(
        Constant.ID_KLEE,
        "클레",
        "Klee",
        5,
        Elements.PYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_klee
    ),

    LocalCharacter(
        Constant.ID_ZHONGLI,
        "종려",
        "Zhongli",
        5,
        Elements.GEO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_zhongli
    ),

    LocalCharacter(
        Constant.ID_FISCHL,
        "피슬",
        "Fischl",
        4,
        Elements.ELECTRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_fischl
    ),
    
    LocalCharacter(
        Constant.ID_BENNETT,
        "베넷",
        "Bennett",
        4,
        Elements.PYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_bennett
    ),
    
    LocalCharacter(
        Constant.ID_CHILDE,
        "타르탈리아",
        "Childe",
        5,
        Elements.HYDRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_childe
    ),
    
    LocalCharacter(
        Constant.ID_NOELLE,
        "노엘",
        "Noelle",
        4,
        Elements.GEO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_noelle
    ),
    
    LocalCharacter(
        Constant.ID_QIQI,
        "치치",
        "Qiqi",
        5,
        Elements.CYRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_qiqi
    ),
    
    LocalCharacter(
        Constant.ID_CHONGYUN,
        "중운",
        "Chongyun",
        4,
        Elements.CYRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_chongyun
    ),
    
    LocalCharacter(
        Constant.ID_GANYU,
        "감우",
        "Ganyu",
        5,
        Elements.CYRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_ganyu
    ),
    
    LocalCharacter(
        Constant.ID_ALBEDO,
        "알베도",
        "Albedo",
        5,
        Elements.GEO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_albedo
    ),

    LocalCharacter(
        Constant.ID_DIONA,
        "디오나",
        "Diona",
        4,
        Elements.CYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_diona
    ),

    LocalCharacter(
        Constant.ID_MONA,
        "모나",
        "Mona",
        5,
        Elements.HYDRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_mona
    ),

    LocalCharacter(
        Constant.ID_KEQING,
        "각청",
        "Keqing",
        5,
        Elements.ELECTRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_keqing
    ),

    LocalCharacter(
        Constant.ID_SUCROSE,
        "설탕",
        "Sucrose",
        4,
        Elements.ANEMO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_sucrose
    ),

    LocalCharacter(
        Constant.ID_XINYAN,
        "신염",
        "Xinyan",
        4,
        Elements.PYRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_xinyan
    ),

    LocalCharacter(
        Constant.ID_ROSARIA,
        "로자리아",
        "Rosaria",
        4,
        Elements.CYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_rosaria
    ),

    LocalCharacter(
        Constant.ID_HUTAO,
        "호두",
        "Hu Tao",
        5,
        Elements.PYRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_hutao
    ),

    LocalCharacter(
        Constant.ID_KAZUHA,
        "카에데하라 카즈하",
        "Kaedehara Kazuha",
        5,
        Elements.ANEMO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_kazuha
    ),

    LocalCharacter(
        Constant.ID_YANFEI,
        "연비",
        "Yanfei",
        4,
        Elements.PYRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_yanfei
    ),

    LocalCharacter(
        Constant.ID_YOIMIYA,
        "요이미야",
        "Yoimiya",
        5,
        Elements.PYRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_yoimiya
    ),

    LocalCharacter(
        Constant.ID_THOMA,
        "토마",
        "Thoma",
        4,
        Elements.PYRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_thoma
    ),

    LocalCharacter(
        Constant.ID_EULA,
        "유라",
        "Eula",
        5,
        Elements.CYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_eula
    ),

    LocalCharacter(
        Constant.ID_RAIDEN,
        "라이덴 쇼군",
        "Radien Shogun",
        5,
        Elements.ELECTRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_raiden
    ),

    LocalCharacter(
        Constant.ID_SAYU,
        "사유",
        "Sayu",
        4,
        Elements.ANEMO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_sayu
    ),

    LocalCharacter(
        Constant.ID_KOKOMI,
        "산고노미야 코코미",
        "Sangonomiya Kokomi",
        5,
        Elements.HYDRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_kokomi
    ),

    LocalCharacter(
        Constant.ID_GOROU,
        "고로",
        "Gorou",
        4,
        Elements.GEO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_gorou
    ),

    LocalCharacter(
        Constant.ID_SARA,
        "쿠죠 사라",
        "Kujou Sara",
        4,
        Elements.ELECTRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_sara
    ),

    LocalCharacter(
        Constant.ID_ITTO,
        "아라타키 이토",
        "Arataki Itto",
        5,
        Elements.GEO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_itto
    ),

    LocalCharacter(
        Constant.ID_YAE,
        "야에 미코",
        "Yae Miko",
        5,
        Elements.ELECTRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_yae
    ),

    LocalCharacter(
        Constant.ID_HEIZOU,
        "시카노인 헤이조",
        "Shikanoin Heizou",
        4,
        Elements.ANEMO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_heizou
    ),

    LocalCharacter(
        Constant.ID_YERAN,
        "야란",
        "Yeran",
        5,
        Elements.HYDRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_yeran
    ),

    LocalCharacter(
        Constant.ID_ALOY,
        "에일로이",
        "Aloy",
        105,
        Elements.CYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_aloy
    ),

    LocalCharacter(
        Constant.ID_SHENHE,
        "신학",
        "Shenhe",
        5,
        Elements.CYRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_shenhe
    ),

    LocalCharacter(
        Constant.ID_YUNJIN,
        "운근",
        "Yun Jin",
        4,
        Elements.GEO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_yunjin
    ),

    LocalCharacter(
        Constant.ID_KUKI,
        "쿠키 시노부",
        "Kuki Shinobu",
        4,
        Elements.ELECTRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_kuki
    ),

    LocalCharacter(
        Constant.ID_AYATO,
        "카미사토 아야토",
        "Kamisato Ayato",
        5,
        Elements.HYDRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_ayato
    ),

    LocalCharacter(
        Constant.ID_COLLEI,
        "콜레이",
        "Collei",
        4,
        Elements.DENDRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_collei
    ),

    LocalCharacter(
        Constant.ID_DORI,
        "도리",
        "Dori",
        4,
        Elements.ELECTRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_dori
    ),

    LocalCharacter(
        Constant.ID_TIGHNARI,
        "타이나리",
        "Tighnari",
        5,
        Elements.DENDRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_tighnari
    ),

    LocalCharacter(
        Constant.ID_NILOU,
        "닐루",
        "Nilou",
        5,
        Elements.HYDRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_nilou
    ),

    LocalCharacter(
        Constant.ID_CYNO,
        "사이노",
        "Cyno",
        5,
        Elements.ELECTRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_cyno
    ),

    LocalCharacter(
        Constant.ID_CANDACE,
        "캔디스",
        "Candace",
        4,
        Elements.HYDRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_candace
    ),

    LocalCharacter(
        Constant.ID_NAHIDA,
        "나히다",
        "Nahida",
        5,
        Elements.DENDRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_nahida
    ),

    LocalCharacter(
        Constant.ID_LAYLA,
        "레일라",
        "Layla",
        4,
        Elements.CYRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_layla
    ),

    LocalCharacter(
        Constant.ID_WANDERER,
        "방랑자",
        "The Wanderer",
        5,
        Elements.ANEMO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_wanderer
    ),

    LocalCharacter(
        Constant.ID_FARUZAN,
        "파루잔",
        "Faruzan",
        4,
        Elements.ANEMO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_faruzan
    ),

    LocalCharacter(
        Constant.ID_YAOYAO,
        "요요",
        "Yaoyao",
        4,
        Elements.DENDRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_yaoyao
    ),

    LocalCharacter(
        Constant.ID_ALHAITHAM,
        "알하이탐",
        "Alhaitham",
        5,
        Elements.DENDRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_alhaitham
    ),

    LocalCharacter(
        Constant.ID_DEHYA,
        "데히야",
        "Dehya",
        5,
        Elements.PYRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_dehya
    ),
    
    LocalCharacter(
        Constant.ID_MIKA,
        "미카",
        "Mika",
        4,
        Elements.CYRO,
        Constant.TALENT_AREA_MONDSTADT,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_mika
    ),

    LocalCharacter(
        Constant.ID_KAVEH,
        "카베",
        "Kaveh",
        4,
        Elements.DENDRO,
        Constant.TALENT_AREA_SUMERU,
        Constant.TALENT_DATE_TUEFRI,
        R.drawable.icon_kaveh
    ),

    LocalCharacter(
        Constant.ID_BAIZHUER,
        "백출",
        "Baizhuer",
        5,
        Elements.DENDRO,
        Constant.TALENT_AREA_LIYUE,
        Constant.TALENT_DATE_WEDSAT,
        R.drawable.icon_baizhuer
    ),

    LocalCharacter(
        Constant.ID_KIRARA,
        "키라라",
        "Kirara",
        4,
        Elements.DENDRO,
        Constant.TALENT_AREA_INAZUMA,
        Constant.TALENT_DATE_MONTHU,
        R.drawable.icon_kirara
    ),

)