package tech.levanter.anyvision.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import tech.levanter.anyvision.models.Photo
import tech.levanter.anyvision.room.PhotoRepository

class AllPhotosViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: PhotoRepository =
        PhotoRepository(application)

    private var allPhotos: LiveData<List<Photo>> = repository.getAllPhotos()
    private var facePhotos: LiveData<List<Photo>> = repository.getFacesPhotos()
    private var noFacePhotos: LiveData<List<Photo>> = repository.getNoFacesPhotos()

    fun insert(photo: Photo) {
        repository.insert(photo)
    }

    fun update(photo: Photo) {
        repository.update(photo)
    }

    fun getAllPhotos(): LiveData<List<Photo>> {
        return allPhotos
    }

    fun getFacePhotos(): LiveData<List<Photo>> {
        return facePhotos
    }

    fun getNoFacePhotos(): LiveData<List<Photo>> {
        return noFacePhotos
    }
}