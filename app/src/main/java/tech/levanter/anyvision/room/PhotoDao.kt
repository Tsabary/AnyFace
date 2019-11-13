package tech.levanter.anyvision.room

import androidx.lifecycle.LiveData
import androidx.room.*
import tech.levanter.anyvision.models.Photo

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(photo: Photo)

    @Update
    fun update(photo: Photo)

    @Delete
    fun delete(photo: Photo)

    @Query("DELETE FROM photos_table")
    fun deleteAllPhotos()

    @Query("SELECT * FROM photos_table ORDER BY joyLevel DESC")
    fun getAllPhotos(): LiveData<MutableList<Photo>>

    @Query("SELECT * FROM photos_table WHERE hasFaces=1")
    fun getPhotosWithFaces(): LiveData<MutableList<Photo>>

    @Query("SELECT * FROM photos_table WHERE hasFaces=2")
    fun getPhotosWithoutFaces(): LiveData<MutableList<Photo>>
}