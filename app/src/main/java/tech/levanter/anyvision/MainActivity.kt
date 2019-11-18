package tech.levanter.anyvision

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.navigation_bar_layout.*
import tech.levanter.anyvision.fragments.AllPhotosFragment
import tech.levanter.anyvision.fragments.FacesPhotosFragment
import tech.levanter.anyvision.fragments.NoFacesPhotosFragment
import tech.levanter.anyvision.interfaces.Methods


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

    private fun setUpFragments() {
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
        }
    }
}
