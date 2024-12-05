package danggai.domain.local

import danggai.domain.network.character.entity.Avatar

data class LocalCharacter(
    val id: Int,                            // 고유 id
    val name_ko: String,                    // 이름
    val name_en: String,                    // 이름
    val rarity: Int,                        // 4~5성
    val element: Elements,
    val talentArea: TalentArea,
    val talentDay: TalentDate,
    val icon: Int,                          // 프로필 사진 res ID
    var isSelected: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Int -> {
                id == other
            }

            is Avatar -> {
                id == other.id
            }

            is LocalCharacter -> {
                id == other.id
            }

            else -> {
                false
            }
        }
    }

    override fun hashCode(): Int {
        return id
    }
}