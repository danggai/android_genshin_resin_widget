package danggai.data.db.account.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("cookie")
    val cookie: String,
    @SerializedName("genshinUid")
    val genshinUid: String,
    @SerializedName("enableGenshinCheckin")
    val enableGenshinCheckin: Boolean,
    @SerializedName("enableHonkai3rdCheckin")
    val enableHonkai3rdCheckin: Boolean,
    @SerializedName("enableTotCheckin")
    val enableTotCheckin: Boolean,
)