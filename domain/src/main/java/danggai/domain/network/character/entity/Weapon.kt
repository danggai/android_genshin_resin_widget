package danggai.domain.network.character.entity

data class Weapon(
    val id: Int,
    val rarity: Int,            // 3,4,5성
    val affix_level: Int,       // 재련 수
    val name: String,
    val level: Int,
    val desc: String,
    val promote_level: Int,     // 재련 한정 등등?
    val type: Int,              // 1한손검 10법구 11양손검 12활 13장병기
    val type_name: String,      // 무기 종류 명
    val icon: String,
)