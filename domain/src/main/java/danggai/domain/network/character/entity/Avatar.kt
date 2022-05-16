package danggai.domain.network.character.entity

data class Avatar(
    val id: Int,                            // 고유 id
    val level: Int,                         // 레벨
    val name: String,                       // 이름
    val rarity: Int,                        // 3~5성
    val weapon: Weapon,                     // 무기
    val reliquaries: List<Reliquary>,       // 성유물
    val constellations: List<Constellation>,// 별자리 설명
    val actived_constellation_num: Int,     // 별자리 활성화 수
    val costumes: List<Costume>,            // 코스튬 목록
    val element: String,                    // 속성
    val fetter: Int,                        // 1 2 3 4 5 6 7 8 9 10
    val icon: String,                       // 명함 사진
    val image: String,                      // 반투명 전신 사진 (배경)
)