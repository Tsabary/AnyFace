package tech.levanter.anyvision.room

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import tech.levanter.anyvision.models.Photo
import kotlin.coroutines.CoroutineContext

class PhotoRepository (application: Application){

    private var photoDao: PhotoDao

//    private var allPhotosStatic: MutableList<Photo>
//    lateinit var allPhotosStatic : List<Photo>
    private var allPhotos: LiveData<MutableList<Photo>>
    private var facesPhotos: LiveData<MutableList<Photo>>
    private var noFacesPhotos: LiveData<MutableList<Photo>>


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


//    fun delete(photo: Photo) {
//        val deletePhotoAsyncTask = DeletePhotoAsyncTask(
//            photoDao
//        ).execute(photo)
//    }
//
//    fun deleteAllPhotos() {
//        val deleteAllPhotosAsyncTask = DeleteAllPhotosAsyncTask(
//            photoDao
//        ).execute()
//    }



//    fun getAllPhotosStatic(): MutableList<Photo> {
//        return allPhotosStatic
//    }

    fun getAllPhotos(): LiveData<MutableList<Photo>> {
        return allPhotos
    }

    fun getFacesPhotos(): LiveData<MutableList<Photo>> {
        return facesPhotos
    }

    fun getNoFacesPhotos(): LiveData<MutableList<Photo>> {
        return noFacesPhotos
    }


    companion object {



        private class InsertPhotoAsyncTask(photoDao: PhotoDao) : AsyncTask<Photo, Unit, Unit>() {
            var mPhotoDao = photoDao

            override fun doInBackground(vararg p0: Photo) {
                mPhotoDao.insert(p0[0])
            }
        }



        private class UpdatePhotoAsyncTask(photoDao: PhotoDao) : AsyncTask<Photo, Unit, Unit>() {
            val mPhotoDao = photoDao

            override fun doInBackground(vararg p0: Photo?) {
                mPhotoDao.update(p0[0]!!)
            }
        }

//        private class DeletePhotoAsyncTask(photoDao: PhotoDao) : AsyncTask<Photo, Unit, Unit>() {
//            val mPhotoDao = photoDao
//
//            override fun doInBackground(vararg p0: Photo?) {
//                mPhotoDao.delete(p0[0]!!)
//            }
//        }
//
//        private class DeleteAllPhotosAsyncTask(photoDao: PhotoDao) : AsyncTask<Unit, Unit, Unit>() {
//            val mPhotoDao = photoDao
//
//            override fun doInBackground(vararg p0: Unit?) {
//                mPhotoDao.deleteAllPhotos()
//            }
//        }
    }

}