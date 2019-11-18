package tech.levanter.anyvision.room

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import tech.levanter.anyvision.models.Photo

class PhotoRepository(application: Application) {

    private var photoDao: PhotoDao

    private var allPhotos: LiveData<List<Photo>>
    private var facesPhotos: LiveData<List<Photo>>
    private var noFacesPhotos: LiveData<List<Photo>>

    init {
        val database: PhotoDatabase = PhotoDatabase.getInstance(
            application.applicationContext
        )!!

        photoDao = database.photoDao()

        allPhotos = photoDao.getAllPhotos()
        facesPhotos = photoDao.getPhotosWithFaces()
        noFacesPhotos = photoDao.getPhotosWithoutFaces()
    }

    fun insert(photo: Photo) {
        CoroutineScope(IO).launch {
            photoDao.insert(photo)
        }
    }

    fun update(photo: Photo) {
        CoroutineScope(IO).launch {
            photoDao.update(photo)
        }
    }

    fun getAllPhotos(): LiveData<List<Photo>> {
        return allPhotos
    }

    fun getFacesPhotos(): LiveData<List<Photo>> {
        return facesPhotos
    }

    fun getNoFacesPhotos(): LiveData<List<Photo>> {
        return noFacesPhotos
    }
}