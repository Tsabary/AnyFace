package tech.levanter.anyvision.room

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tech.levanter.anyvision.models.Photo
import kotlin.coroutines.CoroutineContext

class PhotoRepository(application: Application) {

    private var photoDao: PhotoDao

    private var allPhotos: LiveData<List<Photo>>
    private var facesPhotos: LiveData<List<Photo>>
    private var noFacesPhotos: LiveData<List<Photo>>


//    private var parentJob = Job()
//
//    private val coroutineContext: CoroutineContext
//        get() = parentJob + Dispatchers.Main

//    private val scope = CoroutineScope(coroutineContext)

    init {

        val database: PhotoDatabase = PhotoDatabase.getInstance(
            application.applicationContext
        )!!

        photoDao = database.photoDao()

//        allPhotosStatic = photoDao.getAllPhotosStatic()
        allPhotos = photoDao.getAllPhotos()
        facesPhotos = photoDao.getPhotosWithFaces()
        noFacesPhotos = photoDao.getPhotosWithoutFaces()

    }

    fun insert(photo: Photo) {
        val insertPhotoAsyncTask = InsertPhotoAsyncTask(
            photoDao
        ).execute(photo)
    }

    fun update(photo: Photo) {
        val updatePhotoAsyncTask = UpdatePhotoAsyncTask(
            photoDao
        ).execute(photo)
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


    companion object {

        private class InsertPhotoAsyncTask(photoDao: PhotoDao) : AsyncTask<Photo, Unit, Unit>() {
            var mPhotoDao = photoDao

            override fun doInBackground(vararg p0: Photo) {
                CoroutineScope(IO).launch {
                    mPhotoDao.insert(p0[0])
                }
            }
        }


        private class UpdatePhotoAsyncTask(photoDao: PhotoDao) : AsyncTask<Photo, Unit, Unit>() {
            val mPhotoDao = photoDao

            override fun doInBackground(vararg p0: Photo?) {
                CoroutineScope(IO).launch {

                    mPhotoDao.update(p0[0]!!)
                }
            }
        }
    }

}