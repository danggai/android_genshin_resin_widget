package danggai.domain.network.character.entity

data class Reliquary(
    val id: Int,
    val name: String,
    val level: Int,
    val rarity: Int,
    val pos: Int,               // 부위  1꽃 2깃털 3시계 4성배 5왕관
    val pos_name: String,       // 부위 명
    val set: ReliquarySet,               // 세트
    val icon: String
)
