package tech.levanter.anyvision.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import tech.levanter.anyvision.models.Photo
import tech.levanter.anyvision.room.PhotoRepository

class AllPhotosViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: PhotoRepository =
        PhotoRepository(application)

    private var allPhotos: LiveData<MutableList<Photo>> = repository.getAllPhotos()
    private var facePhotos: LiveData<MutableList<Photo>> = repository.getFacesPhotos()
    private var noFacePhotos: LiveData<MutableList<Photo>> = repository.getNoFacesPhotos()

    fun insert(photo: Photo) {
        repository.insert(photo)
    }

    fun update(photo: Photo) {
        repository.update(photo)
    }

//    fun delete(photo: Photo) {
//        repository.delete(photo)
//    }
//
//    fun deleteAllPhotos() {
//        repository.deleteAllPhotos()
//    }

    fun getAllPhotos(): LiveData<MutableList<Photo>> {
        return allPhotos
    }

    fun getFacePhotos(): LiveData<MutableList<Photo>> {
        return facePhotos
    }

    fun getNoFacePhotos(): LiveData<MutableList<Photo>> {
        return noFacePhotos
    }
}