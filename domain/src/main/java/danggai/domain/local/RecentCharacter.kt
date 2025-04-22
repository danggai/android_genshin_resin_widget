package danggai.domain.local

data class RecentCharacter(
    val name_ko: String,                    // 이름
    val name_en: String,                    // 이름
    val rarity: Int,                        // 4~5성
    val element: Elements,
    val talentArea: TalentArea,
    val talentDay: TalentDate,
    val icon: String,                          // 프로필 사진 주소
) {
}