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
import kotlinx.android.synthetic.main.navigation_bar_layout.*
import tech.levanter.anyvision.databinding.ActivityMainBinding
import tech.levanter.anyvision.interfaces.Methods
import tech.levanter.anyvision.viewModels.AllPhotosViewModel
import java.io.File


class MainActivity : AppCompatActivity(), Methods {

    private val fm = supportFragmentManager
    var active: Fragment? = null

    lateinit var allPhotosFragment: AllPhotosFragment
    lateinit var facesPhotosFragment: FacesPhotosFragment
    lateinit var noFacesPhotosFragment: NoFacesPhotosFragment

    var isAppInForeground = false
    var isDetecting = false
    var operationResultText = ""


    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpFragments()
        setUpBottomNav()
    }

    override fun onResume() {
        super.onResume()

        isAppInForeground = true

        if (hasNoPermissions(this)) {
            requestPermission(this, permissions)
        } else {
            loadAllPhotos(this)
        }
    }

    override fun onPause() {
        super.onPause()
        isAppInForeground = false
    }

    private fun setUpFragments(){
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
    }

    private fun setUpBottomNav() {
        val bottomNavigation = bottom_nav
        bottomNavigation.setNavigationChangeListener { _, position ->
            when (position) {
                0 -> navigateToFragment(this, fm, active!!, allPhotosFragment)
                1 -> navigateToFragment(this, fm, active!!, facesPhotosFragment)
                2 -> navigateToFragment(this, fm, active!!, noFacesPhotosFragment)
            }
        }    }

    /*
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
*/
}
