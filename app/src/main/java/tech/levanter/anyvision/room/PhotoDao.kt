package tech.levanter.anyvision.room

import androidx.lifecycle.LiveData
import androidx.room.*
import tech.levanter.anyvision.models.Photo

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(photo: Photo)

    @Update
    suspend fun update(photo: Photo)

    @Query("SELECT * FROM photos_table ORDER BY joyLevel DESC")
     fun getAllPhotos(): LiveData<List<Photo>>

    @Query("SELECT * FROM photos_table WHERE hasFaces=1")
     fun getPhotosWithFaces(): LiveData<List<Photo>>

    @Query("SELECT * FROM photos_table WHERE hasFaces=2")
     fun getPhotosWithoutFaces(): LiveData<List<Photo>>
}