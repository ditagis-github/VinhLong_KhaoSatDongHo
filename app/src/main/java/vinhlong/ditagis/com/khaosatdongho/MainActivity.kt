package vinhlong.ditagis.com.khaosatdongho

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.CompoundButtonCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.ArcGISRuntimeException
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Callout
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.mapping.view.MapView
import com.google.android.gms.common.api.GoogleApiClient
import vinhlong.ditagis.com.khaosatdongho.adapter.DanhSachDongHoKHAdapter
import vinhlong.ditagis.com.khaosatdongho.async.LoginAsycn
import vinhlong.ditagis.com.khaosatdongho.async.PreparingAsycn
import vinhlong.ditagis.com.khaosatdongho.async.UpdateAttachmentAsync
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.LayerInfoDTG
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.User
import vinhlong.ditagis.com.khaosatdongho.libs.Action
import vinhlong.ditagis.com.khaosatdongho.libs.FeatureLayerDTG
import vinhlong.ditagis.com.khaosatdongho.utities.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private val mUri: Uri? = null
    private var popup: Popup? = null
    private var mMapView: MapView? = null
    private var mMap: ArcGISMap? = null
    private var mCallout: Callout? = null
    private var mMapViewHandler: MapViewHandler? = null
    private var mTxtSearch: SearchView? = null
    private var mListViewSearch: ListView? = null
    private var danhSachDongHoKHAdapter: DanhSachDongHoKHAdapter? = null
    private var taiSanImageLayers: ArcGISMapImageLayer? = null
    private var hanhChinhImageLayers: ArcGISMapImageLayer? = null
    private var mLinnearDisplayLayerTaiSan: LinearLayout? = null
    private var mLinnearDisplayLayerBaseMap: LinearLayout? = null
    private var mFloatButtonLayer: FloatingActionButton? = null
    private var mFloatButtonLocation: FloatingActionButton? = null
    private var cb_Layer_HanhChinh: CheckBox? = null
    private var cb_Layer_TaiSan: CheckBox? = null
    private var states: Array<IntArray>? = null
    private var colors: IntArray? = null
    private var isChangingGeometry = false
    private var mLocationDisplay: LocationDisplay? = null
    private val requestCode = 2
    internal var reqPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private var mLocationHelper: LocationHelper? = null
    private var mLocation: Location? = null
    private var mApplication: DApplication? = null
    private var selectedFeature: ArcGISFeature? = null

    fun setChangingGeometry(changingGeometry: Boolean, feature: ArcGISFeature?) {
        this.isChangingGeometry = changingGeometry
        if (this.isChangingGeometry) {
            showPinToAdd()
            this.selectedFeature = feature
        } else
            dismissPin()
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        setLicense()
        mApplication = application as DApplication
        setUp()
        initListViewSearch()
        initLayerListView()
        setOnClickListener()
        startGPS()
        logIn()
        mApplication!!.mainActivity = this
    }

    private fun setLoginInfos() {
        val displayName = mApplication!!.user!!.displayName
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this@MainActivity)
        val headerLayout = navigationView.getHeaderView(0)
        val namenv = headerLayout.findViewById<TextView>(R.id.namenv)
        namenv.text = displayName
    }

    private fun startGPS() {

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mLocationHelper = LocationHelper(this, object : LocationHelper.AsyncResponse {
            override fun processFinish(longtitude: Double, latitude: Double) {

            }
        })


        if (!mLocationHelper!!.checkPlayServices()) {
            mLocationHelper!!.buildGoogleApiClient()
        }
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                mLocation = location
            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}

            override fun onProviderEnabled(s: String) {

            }

            override fun onProviderDisabled(s: String) {
                //                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //                startActivity(i);
                if (!mLocationHelper!!.checkPlayServices()) {
                    mLocationHelper!!.buildGoogleApiClient()
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locationManager.requestLocationUpdates("gps", 5000, 0f, listener)
    }

    private fun showLogInActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, Constant.REQUEST_LOGIN)
    }

    private fun logIn() {
        //check login before
        Preference.instance.setContext(this)
        val userName = Preference.instance.loadPreference(getString(R.string.preference_username))
        val passWord = Preference.instance.loadPreference(getString(R.string.preference_password))
        if (userName != null && !userName.isEmpty() && passWord != null && !passWord.isEmpty())
            LoginAsycn(this@MainActivity, object : LoginAsycn.AsyncResponse {
                override fun processFinish(output: Any) {

                    if (output is User) {
                        val user = output as User
                        mApplication!!.user = user
                        handleLoginSuccess()
                    } else {
                        showLogInActivity()
                    }
                }

            }).execute(userName, passWord)
        else
            showLogInActivity()
    }

    private fun setOnClickListener() {
        findViewById<View>(R.id.layout_layer_open_street_map).setOnClickListener(this)
        findViewById<View>(R.id.layout_layer_street_map).setOnClickListener(this)
        findViewById<View>(R.id.layout_layer_topo).setOnClickListener(this)
        findViewById<View>(R.id.floatBtnLayer).setOnClickListener(this)
        findViewById<View>(R.id.floatBtnAdd).setOnClickListener(this)
        findViewById<View>(R.id.btn_add_feature_close).setOnClickListener(this)
        findViewById<View>(R.id.btn_layer_close).setOnClickListener(this)
        findViewById<View>(R.id.img_layvitri).setOnClickListener(this)
        findViewById<View>(R.id.floatBtnLocation).setOnClickListener(this)
        findViewById<View>(R.id.floatBtnHome).setOnClickListener(this)
    }

    private fun initListViewSearch() {
        this.mListViewSearch = findViewById(R.id.lstview_search)
        //đưa listview search ra phía sau
        this.mListViewSearch!!.invalidate()
        val items = ArrayList<Feature>()
        this.danhSachDongHoKHAdapter = DanhSachDongHoKHAdapter(this@MainActivity, items)
        this.mListViewSearch!!.adapter = danhSachDongHoKHAdapter
        this.mListViewSearch!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val feature = parent.getItemAtPosition(position) as Feature
            mMapViewHandler!!.showPopup(feature)
            danhSachDongHoKHAdapter!!.clear()
            danhSachDongHoKHAdapter!!.notifyDataSetChanged()
        }
    }

    private fun setUp() {
        states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        colors = intArrayOf(R.color.colorTextColor_1, R.color.colorTextColor_1)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        requestPermisson()
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initMapView() {
        mMapView = findViewById(R.id.mapView)
        mMap = ArcGISMap(Basemap.Type.OPEN_STREET_MAP, LATITUDE, LONGTITUDE, LEVEL_OF_DETAIL)
        mMapView!!.map = mMap
        mCallout = mMapView!!.callout
        val preparingAsycn = PreparingAsycn(this@MainActivity, object : PreparingAsycn.AsyncResponse {
            override fun processFinish(layerInfoDTGs: ArrayList<LayerInfoDTG>?) {
                mApplication?.lstFeatureLayerDTG = layerInfoDTGs
                setFeatureService()
            }

        })
        if (CheckConnectInternet.isOnline(this))
            preparingAsycn.execute()
        val edit_latitude = findViewById<View>(R.id.edit_latitude) as EditText
        val edit_longtitude = findViewById<View>(R.id.edit_longtitude) as EditText
        mMapView!!.onTouchListener = object : DefaultMapViewOnTouchListener(this, mMapView) {


            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                try {
                    if (mMapViewHandler != null)
                        mMapViewHandler!!.onSingleTapMapView(e!!)
                } catch (ex: ArcGISRuntimeException) {
                    Log.d("", ex.toString())
                }

                return super.onSingleTapConfirmed(e)
            }

            @SuppressLint("SetTextI18n")
            override fun onScroll(e1: MotionEvent, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                if (mMapViewHandler != null) {
                    val location = mMapViewHandler!!.onScroll()
                    edit_longtitude.setText(location[0].toString() + "")
                    edit_latitude.setText(location[1].toString() + "")
                }
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                return super.onScale(detector)
            }
        }
        changeStatusOfLocationDataSource()
        mLocationDisplay!!.addLocationChangedListener { locationChangedEvent ->
            val position = locationChangedEvent.location.position
            edit_longtitude.setText(position.x.toString() + "")
            edit_latitude.setText(position.y.toString() + "")
            val geometry = GeometryEngine.project(position, SpatialReferences.getWebMercator())
            mMapView!!.setViewpointCenterAsync(geometry.extent.center)
        }

    }

    private fun initLayerListView() {
        mLinnearDisplayLayerTaiSan = findViewById(R.id.linnearDisplayLayerTaiSan)
        mLinnearDisplayLayerBaseMap = findViewById(R.id.linnearDisplayLayerBaseMap)
        findViewById<View>(R.id.layout_layer_open_street_map).setOnClickListener(this)
        findViewById<View>(R.id.layout_layer_street_map).setOnClickListener(this)
        findViewById<View>(R.id.layout_layer_topo).setOnClickListener(this)
        mFloatButtonLayer = findViewById(R.id.floatBtnLayer)
        mFloatButtonLayer!!.setOnClickListener(this)
        findViewById<View>(R.id.btn_layer_close).setOnClickListener(this)
        mFloatButtonLocation = findViewById(R.id.floatBtnLocation)
        mFloatButtonLocation!!.setOnClickListener(this)

        cb_Layer_HanhChinh = findViewById(R.id.cb_Layer_HanhChinh)
        cb_Layer_TaiSan = findViewById(R.id.cb_Layer_TaiSan)
        cb_Layer_TaiSan!!.setOnCheckedChangeListener { buttonView, isChecked ->
            for (i in 0 until mLinnearDisplayLayerTaiSan!!.childCount) {
                val view = mLinnearDisplayLayerTaiSan!!.getChildAt(i)
                if (view is CheckBox) {
                    if (isChecked)
                        view.isChecked = true
                    else
                        view.isChecked = false
                }
            }
        }
        cb_Layer_HanhChinh!!.setOnCheckedChangeListener { buttonView, isChecked ->
            for (i in 0 until mLinnearDisplayLayerBaseMap!!.childCount) {
                val view = mLinnearDisplayLayerBaseMap!!.getChildAt(i)
                if (view is CheckBox) {
                    if (isChecked)
                        view.isChecked = true
                    else
                        view.isChecked = false
                }
            }
        }
    }

    private fun setFeatureService() {
        if (mApplication?.lstFeatureLayerDTG!!.isEmpty()) return
        popup = Popup(this@MainActivity, mMapView!!, mCallout)
        mMapViewHandler = MapViewHandler(mMapView!!, this, popup!!)

        for (layerInfoDTG in mApplication?.lstFeatureLayerDTG!!) {
            if (!layerInfoDTG.isView) continue
            val url = layerInfoDTG.url
            if (layerInfoDTG.id!!.toUpperCase() == Constant.IDLayer.BASEMAP) {
                hanhChinhImageLayers = ArcGISMapImageLayer(url!!)
                hanhChinhImageLayers!!.id = layerInfoDTG.id!!
                mMapView!!.map.operationalLayers.add(hanhChinhImageLayers)
                hanhChinhImageLayers!!.addDoneLoadingListener {
                    if (hanhChinhImageLayers!!.loadStatus == LoadStatus.LOADED) {
                        val sublayerList = hanhChinhImageLayers!!.sublayers
                        for (sublayer in sublayerList) {
                            addCheckBox_SubLayer(sublayer as ArcGISMapImageSublayer, mLinnearDisplayLayerBaseMap!!)
                        }
                        val url_HanhChinh = "$url/5"
                        val serviceFeatureTable = ServiceFeatureTable(url_HanhChinh)
                        popup!!.setmSFTHanhChinh(serviceFeatureTable)
                    }
                }
                hanhChinhImageLayers!!.loadAsync()
            } else if (layerInfoDTG.id!!.toUpperCase() == Constant.IDLayer.CHUYENDE && layerInfoDTG.isView) {
                taiSanImageLayers = ArcGISMapImageLayer(url!!)
                taiSanImageLayers!!.name = layerInfoDTG.titleLayer
                taiSanImageLayers!!.id = layerInfoDTG.id!!
                mMapView!!.map.operationalLayers.add(taiSanImageLayers)
                taiSanImageLayers!!.addDoneLoadingListener {
                    if (taiSanImageLayers!!.loadStatus == LoadStatus.LOADED) {
                        val sublayerList = taiSanImageLayers!!.sublayers
                        for (sublayer in sublayerList) {
                            if (sublayer.id != Constant.idDongHoKhachHang.toLong()) {
                                addCheckBox_SubLayer(sublayer as ArcGISMapImageSublayer, mLinnearDisplayLayerTaiSan!!)
                            }
                        }
                    }
                }
                taiSanImageLayers!!.loadAsync()
            } else {
                val serviceFeatureTable = ServiceFeatureTable(url!!)
                val featureLayer = FeatureLayer(serviceFeatureTable)

                featureLayer.name = layerInfoDTG.titleLayer
                featureLayer.id = layerInfoDTG.id!!
                val action = Action(layerInfoDTG.isView, layerInfoDTG.isCreate, layerInfoDTG.isEdit, layerInfoDTG.isDelete)
                val featureLayerDTG = FeatureLayerDTG(featureLayer, layerInfoDTG.titleLayer, action)
                featureLayerDTG.outFields = getFieldsDTG(layerInfoDTG.outField)
                featureLayerDTG.queryFields = getFieldsDTG(layerInfoDTG.outField)
                featureLayerDTG.updateFields = getFieldsDTG(layerInfoDTG.outField)
                if (layerInfoDTG.id != null && layerInfoDTG.id == Constant.IDLayer.DHKHLYR) {
                    //                    String userName = mApplication.getUser().getUserName();
                    featureLayer.definitionExpression = mApplication!!.definitionFeature
                    popup!!.setDongHoKHDTG(featureLayerDTG)
                    featureLayer.addDoneLoadingListener {
                        if (featureLayer.loadStatus == LoadStatus.LOADED) {
                            addCheckBox_LayerDHKH(featureLayer)
                            mMapView!!.setViewpointScaleAsync(featureLayer.minScale)
                        } else {
                            Toast.makeText(this@MainActivity, "Không tải được lớp đồng hồ khách hàng, đang thử tải lại",
                                    Toast.LENGTH_SHORT).show()
                            setFeatureService()
                        }
                    }
                    mApplication!!.dongHoKHDTG = featureLayerDTG
                    mMapViewHandler!!.setDongHoKHSFT(serviceFeatureTable)
                    mApplication!!.dongHoKHSFT = serviceFeatureTable

                }
                if (layerInfoDTG.id != null && layerInfoDTG.id == Constant.IDLayer.VATTUDONGHOTBL) {
                    mApplication!!.vatTuDHDTG = featureLayerDTG
                    mApplication!!.vatTuKHSFT = serviceFeatureTable
                }
                if (layerInfoDTG.id != null && layerInfoDTG.id == Constant.IDLayer.DMVATTUTBL) {
                    featureLayer.addDoneLoadingListener {
                        if (featureLayer.loadStatus == LoadStatus.LOADED) {
                            mApplication!!.dmVatTuKHSFT = serviceFeatureTable
                        }
                    }
                }

                mMap!!.operationalLayers.add(featureLayer)
            }
        }
    }

    private fun addCheckBox_LayerDHKH(featureLayer: FeatureLayer) {
        val linearLayout = findViewById<LinearLayout>(R.id.linnearDisplayLayer)
        val checkBox = CheckBox(linearLayout.context)
        checkBox.text = featureLayer.name
        checkBox.isChecked = true
        CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList(states, colors))
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (checkBox.isChecked) {
                if (buttonView.text == featureLayer.name)
                    featureLayer.isVisible = true
            } else {
                if (checkBox.text == featureLayer.name)
                    featureLayer.isVisible = false
            }
        }
        linearLayout.addView(checkBox)
    }

    private fun addCheckBox_SubLayer(layer: ArcGISMapImageSublayer, linearLayout: LinearLayout) {
        val checkBox = CheckBox(linearLayout.context)
        checkBox.text = layer.name
        checkBox.isChecked = false
        layer.isVisible = false
        CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList(states, colors))
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (checkBox.isChecked) {
                if (buttonView.text == layer.name)
                    layer.isVisible = true
            } else {
                if (checkBox.text == layer.name)
                    layer.isVisible = false
            }
        }
        linearLayout.addView(checkBox)
    }

    private fun getFieldsDTG(stringFields: String?): Array<String>? {
        var returnFields: Array<String>? = null
        if (stringFields != null) {
            if (stringFields === "*") {
                returnFields = arrayOf("*")
            } else {
                returnFields = stringFields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            }
        }
        return returnFields
    }

    private fun setLicense() {
        //way 1
        ArcGISRuntimeEnvironment.setLicense(getString(R.string.license))
    }

    private fun changeStatusOfLocationDataSource() {
        mLocationDisplay = mMapView!!.locationDisplay
        //        changeStatusOfLocationDataSource();
        mLocationDisplay!!.addDataSourceStatusChangedListener(LocationDisplay.DataSourceStatusChangedListener { dataSourceStatusChangedEvent ->
            // If LocationDisplay started OK, then continue.
            if (dataSourceStatusChangedEvent.isStarted) return@DataSourceStatusChangedListener

            // No error is reported, then continue.
            if (dataSourceStatusChangedEvent.error == null) return@DataSourceStatusChangedListener

            // If an error is found, handle the failure to start.
            // Check permissions to see if failure may be due to lack of permissions.
            val permissionCheck1 = ContextCompat.checkSelfPermission(this@MainActivity, reqPermissions[0]) == PackageManager.PERMISSION_GRANTED
            val permissionCheck2 = ContextCompat.checkSelfPermission(this@MainActivity, reqPermissions[1]) == PackageManager.PERMISSION_GRANTED

            if (!(permissionCheck1 && permissionCheck2)) {
                // If permissions are not already granted, request permission from the user.
                ActivityCompat.requestPermissions(this@MainActivity, reqPermissions, requestCode)
            }
        })
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.quan_ly_su_co, menu)
        mTxtSearch = menu.findItem(R.id.action_search).actionView as SearchView
        mTxtSearch!!.queryHint = getString(R.string.title_search)
        mTxtSearch!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mMapViewHandler!!.querySearch(query, danhSachDongHoKHAdapter!!)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length == 0) {
                    danhSachDongHoKHAdapter!!.clear()
                    danhSachDongHoKHAdapter!!.notifyDataSetChanged()
                }
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_search) {
            this@MainActivity.mListViewSearch!!.visibility = View.VISIBLE
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_thongke) {
            val intent = Intent(this, CongViecActivity::class.java)
            this.startActivityForResult(intent, requestCode)
        } else if (id == R.id.nav_tracuu) {
        } else if (id == R.id.nav_logOut) {
            logIn()
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun requestPermisson(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE), REQUEST_ID_IMAGE_CAPTURE)
        }
        return if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            false
        } else
            true
    }

    private fun goHome() {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay!!.startAsync()

        } else {
            Toast.makeText(this@MainActivity, resources.getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    fun showPinToAdd() {
        findViewById<View>(R.id.linear_addfeature).visibility = View.VISIBLE
        findViewById<View>(R.id.img_map_pin).visibility = View.VISIBLE
        findViewById<View>(R.id.floatBtnAdd).visibility = View.GONE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.floatBtnLayer -> {
                v.visibility = View.INVISIBLE
                (findViewById<View>(R.id.layout_layer) as LinearLayout).visibility = View.VISIBLE
            }
            R.id.layout_layer_open_street_map -> {
                mMapView!!.map.maxScale = 1128.497175
                mMapView!!.map.basemap = Basemap.createOpenStreetMap()
                handlingColorBackgroundLayerSelected(R.id.layout_layer_open_street_map)
            }
            R.id.layout_layer_street_map -> {
                mMapView!!.map.maxScale = 1128.497176
                mMapView!!.map.basemap = Basemap.createStreets()
                handlingColorBackgroundLayerSelected(R.id.layout_layer_street_map)
            }
            R.id.layout_layer_topo -> {
                mMapView!!.map.maxScale = 5.0
                mMapView!!.map.basemap = Basemap.createImageryWithLabels()
                handlingColorBackgroundLayerSelected(R.id.layout_layer_topo)
            }
            R.id.btn_layer_close -> {
                findViewById<View>(R.id.layout_layer).visibility = View.INVISIBLE
                findViewById<View>(R.id.floatBtnLayer).visibility = View.VISIBLE
            }
            R.id.img_layvitri -> if (this.isChangingGeometry) {
                mMapViewHandler!!.updateGeometry(this.selectedFeature!!)
            } else
                mMapViewHandler!!.addFeature()
            R.id.floatBtnAdd -> showPinToAdd()
            R.id.btn_add_feature_close -> dismissPin()
            R.id.floatBtnLocation -> if (!mLocationDisplay!!.isStarted)
                mLocationDisplay!!.startAsync()
            else
                mLocationDisplay!!.stop()
            R.id.floatBtnHome -> goHome()
        }
    }

    fun dismissPin() {
        findViewById<View>(R.id.linear_addfeature).visibility = View.GONE
        findViewById<View>(R.id.img_map_pin).visibility = View.GONE
        findViewById<View>(R.id.floatBtnAdd).visibility = View.VISIBLE
    }

    @SuppressLint("ResourceAsColor")
    private fun handlingColorBackgroundLayerSelected(id: Int) {
        when (id) {
            R.id.layout_layer_open_street_map -> {
                (findViewById<View>(R.id.img_layer_open_street_map) as ImageView).setBackgroundResource(R.drawable.layout_shape_basemap)
                (findViewById<View>(R.id.txt_layer_open_street_map) as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                (findViewById<View>(R.id.img_layer_street_map) as ImageView).setBackgroundResource(R.drawable.layout_shape_basemap_none)
                (findViewById<View>(R.id.txt_layer_street_map) as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
                (findViewById<View>(R.id.img_layer_topo) as ImageView).setBackgroundResource(R.drawable.layout_shape_basemap_none)
                (findViewById<View>(R.id.txt_layer_topo) as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
            }
            R.id.layout_layer_street_map -> {
                (findViewById<View>(R.id.img_layer_street_map) as ImageView).setBackgroundResource(R.drawable.layout_shape_basemap)
                (findViewById<View>(R.id.txt_layer_street_map) as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                (findViewById<View>(R.id.img_layer_open_street_map) as ImageView).setBackgroundResource(R.drawable.layout_shape_basemap_none)
                (findViewById<View>(R.id.txt_layer_open_street_map) as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
                (findViewById<View>(R.id.img_layer_topo) as ImageView).setBackgroundResource(R.drawable.layout_shape_basemap_none)
                (findViewById<View>(R.id.txt_layer_topo) as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
            }
            R.id.layout_layer_topo -> {
                (findViewById<View>(R.id.img_layer_topo) as ImageView).setBackgroundResource(R.drawable.layout_shape_basemap)
                (findViewById<View>(R.id.txt_layer_topo) as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                (findViewById<View>(R.id.img_layer_open_street_map) as ImageView).setBackgroundResource(R.drawable.layout_shape_basemap_none)
                (findViewById<View>(R.id.txt_layer_open_street_map) as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
                (findViewById<View>(R.id.img_layer_street_map) as ImageView).setBackgroundResource(R.drawable.layout_shape_basemap_none)
                (findViewById<View>(R.id.txt_layer_street_map) as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
            }
        }
    }

    private fun getBitmap(path: String?): Bitmap? {

        val uri = Uri.fromFile(File(path!!))
        var `in`: InputStream?
        try {
            val IMAGE_MAX_SIZE = 120000 // 1.2MP
            `in` = contentResolver.openInputStream(uri)

            // Decode image size
            var o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(`in`, null, o)
            assert(`in` != null)
            `in`!!.close()


            var scale = 1
            while (o.outWidth * o.outHeight * (1 / Math.pow(scale.toDouble(), 2.0)) > IMAGE_MAX_SIZE) {
                scale++
            }
            var bitmap: Bitmap?
            `in` = contentResolver.openInputStream(uri)
            if (scale > 1) {
                scale--
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = BitmapFactory.Options()
                o.inSampleSize = scale
                bitmap = BitmapFactory.decodeStream(`in`, null, o)
                // resize to desired dimensions
                val height = bitmap!!.height
                val width = bitmap.width
                val y = Math.sqrt(IMAGE_MAX_SIZE / (width.toDouble() / height))
                val x = y / height * width

                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, x.toInt(), y.toInt(), true)
                bitmap.recycle()
                bitmap = scaledBitmap

                System.gc()
            } else {
                bitmap = BitmapFactory.decodeStream(`in`)
            }
            assert(`in` != null)
            `in`!!.close()
            return bitmap
        } catch (e: IOException) {
            Log.e("", e.message, e)
            return null
        }

    }

    private fun getBitmap(uri: Uri): Bitmap? {

        val imgFile = File(uri.path!!)
        return if (imgFile.exists()) {
            BitmapFactory.decodeFile(imgFile.absolutePath)
        } else null
    }

    private fun getByteArrayFromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val image = outputStream.toByteArray()
        try {
            outputStream.close()
        } catch (e: IOException) {

        }

        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                mMapViewHandler!!.showPopup(mApplication!!.selectedFeature)
            }
        } catch (e: Exception) {
        }

        when (requestCode) {
            Constant.REQUEST.ID_UPDATE_ATTACHMENT -> if (resultCode == Activity.RESULT_OK) {
                val uri = mApplication!!.uri
                if (uri != null) {
                    val bitmap = getBitmap(uri.path)
                    //                        Bitmap bitmap = getBitmap(uri);
                    try {
                        if (bitmap != null) {


                            val updateAttachmentAsync = UpdateAttachmentAsync(this, mApplication!!.selectedFeature!!, getByteArrayFromBitmap(bitmap),
                                    object : UpdateAttachmentAsync.AsyncResponse {
                                        override fun processFinish(isSuccess: Boolean?) {
                                            if (isSuccess!!) {
                                                Toast.makeText(this@MainActivity, "Đã lưu ảnh", Toast.LENGTH_SHORT).show()
                                            } else
                                                Toast.makeText(this@MainActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            updateAttachmentAsync.execute()
                        }
                    } catch (ignored: Exception) {
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                MySnackBar.make(mMapView!!, "Hủy chụp ảnh", false)
            } else {
                MySnackBar.make(mMapView!!, "Lỗi khi chụp ảnh", false)
            }
            Constant.REQUEST_LOGIN -> if (Activity.RESULT_OK != resultCode) {
                handleLoginFail()
            } else {
                handleLoginSuccess()
            }
        }
    }

    private fun handleLoginFail() {
        finish()

    }

    private fun handleLoginSuccess() {
        setLoginInfos()
        initMapView()
    }

    override fun onConnected(bundle: Bundle?) {

    }

    override fun onConnectionSuspended(i: Int) {

    }

    companion object {
        private val LATITUDE = 10.2500783//10.205155129125103;//;
        private val LONGTITUDE = 105.9431823//105.94397118543621;//;
        private val LEVEL_OF_DETAIL = 14
        private val REQUEST_ID_IMAGE_CAPTURE = 55
    }
}