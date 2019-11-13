package tech.levanter.anyvision.models

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos_table")
data class Photo(
    @PrimaryKey@ColumnInfo(name = "uri") var uri: String,
    @ColumnInfo(name = "joyLevel") var joyLevel: Float,
    @ColumnInfo(name = "hasFaces") var hasFaces: Int
) {
//    @PrimaryKey(autoGenerate = true)
//    var id: Int = 0
}