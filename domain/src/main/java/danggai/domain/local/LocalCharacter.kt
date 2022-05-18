package danggai.domain.local

import danggai.domain.network.character.entity.Avatar

data class SelectedCharacter(
    val id: Int,                            // 고유 id
    val name: String,                       // 이름
    val rarity: Int,                        // 3~5성
) {
    override fun equals(any: Any?): Boolean {
        return when (any) {
            is Int -> {
                id == any
            }
            is Avatar -> {
                 id == any.id
            }
            is SelectedCharacter -> {
                id == any.id
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