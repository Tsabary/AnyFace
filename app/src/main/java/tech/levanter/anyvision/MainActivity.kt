package tech.levanter.anyvision

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import tech.levanter.anyvision.fragments.AllPhotosFragment
import tech.levanter.anyvision.fragments.FacesPhotosFragment
import tech.levanter.anyvision.fragments.NoFacesPhotosFragment
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import tech.levanter.anyvision.databinding.ActivityMainBinding
import tech.levanter.anyvision.interfaces.Methods
import tech.levanter.anyvision.viewModels.AllPhotosViewModel
import java.io.File


class MainActivity : AppCompatActivity(), Methods {


    private val fm = supportFragmentManager
    private var active: Fragment? = null

    lateinit var allPhotosViewModel: AllPhotosViewModel

    lateinit var allPhotosFragment: AllPhotosFragment
    lateinit var facesPhotosFragment: FacesPhotosFragment
    lateinit var noFacesPhotosFragment: NoFacesPhotosFragment


    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        allPhotosFragment = AllPhotosFragment()
        facesPhotosFragment = FacesPhotosFragment()
        noFacesPhotosFragment = NoFacesPhotosFragment()

        fm.beginTransaction()
            .add(R.id.main_frame_container, allPhotosFragment, "allPhotosFragment")
            .commitAllowingStateLoss()
        fm.beginTransaction()
            .add(R.id.main_frame_container, facesPhotosFragment, "facesPhotosFragment")
            .hide(facesPhotosFragment)
            .commitAllowingStateLoss()
        fm.beginTransaction()
            .add(R.id.main_frame_container, noFacesPhotosFragment, "noFacesPhotosFragment")
            .hide(noFacesPhotosFragment)
            .commitAllowingStateLoss()

        active = allPhotosFragment

        allPhotosViewModel = ViewModelProviders.of(this).get(AllPhotosViewModel::class.java)

        val bottomNavigation = home_bottom_nav
        bottomNavigation.setNavigationChangeListener { _, position ->
            when (position) {
                0 -> navigateToAll()
                1 -> navigateToFaces()
                2 -> navigateToNoFaces()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (hasNoPermissions(this)) {
            requestPermission(this, permissions)
        } else {
            loadAllPhotos(this)
        }
//        else {
//
////            if (isExternalStorageReadable()) {
////                runOnUiThread {
////                    loadImages(File(Environment.getExternalStorageDirectory().path + "/download/anyvision"))
////                }
////            }
//        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (grantResults.isNotEmpty()
//            && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 17
//        ) {
//            if (isExternalStorageReadable()) {
//                runOnUiThread {
//                    loadImages(File(Environment.getExternalStorageDirectory().path + "/download/anyvision"))
//                }
//            }
//        }
//    }

//    private fun loadImages(dir: File?) {
//        if (dir != null) {
//            if (dir.exists()) {
//
//                val files = dir.listFiles()
//                if (files != null) {
//                    val allPhotos = mutableListOf<File>()
//
//                    for (file in files) {
//                        val absolutePath = file.absolutePath
//
//                        if (absolutePath.contains(".")) {
//                            val extension = absolutePath.substring(absolutePath.lastIndexOf("."))
//                            if (acceptedExtensions.contains(extension)) {
//                                allPhotos.add(file)
//                            }
//                        }
//                    }
//                    allPhotosViewModel.photosList.postValue(allPhotos)
//                }
//            }
//        }
//    }


    private fun navigateToAll() {
        fm.beginTransaction().hide(active!!).show(allPhotosFragment).commit()
        active = allPhotosFragment
        shake(allPhotosFragment.emptyIllustration)
    }

    private fun navigateToFaces() {
        fm.beginTransaction().hide(active!!).show(facesPhotosFragment).commit()
        active = facesPhotosFragment
        shake(facesPhotosFragment.emptyIllustration)
    }

    private fun navigateToNoFaces() {
        fm.beginTransaction().hide(active!!).show(noFacesPhotosFragment).commit()
        active = noFacesPhotosFragment
        shake(noFacesPhotosFragment.emptyIllustration)
    }

    private fun shake(view: View) {
        YoYo.with(Techniques.Shake)
            .duration(700)
            .playOn(view)
    }
}