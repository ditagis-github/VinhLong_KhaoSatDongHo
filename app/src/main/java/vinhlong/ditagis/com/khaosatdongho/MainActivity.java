package vinhlong.ditagis.com.khaosatdongho;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.ArcGISSublayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.util.ListenableList;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vinhlong.ditagis.com.khaosatdongho.Editing.EditingVatTu;
import vinhlong.ditagis.com.khaosatdongho.adapter.DanhSachDongHoKHAdapter;
import vinhlong.ditagis.com.khaosatdongho.async.PreparingAsycn;
import vinhlong.ditagis.com.khaosatdongho.async.UpdateAttachmentAsync;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.LayerInfoDTG;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.ListObjectDB;
import vinhlong.ditagis.com.khaosatdongho.libs.Action;
import vinhlong.ditagis.com.khaosatdongho.libs.FeatureLayerDTG;
import vinhlong.ditagis.com.khaosatdongho.tools.TraCuu;
import vinhlong.ditagis.com.khaosatdongho.utities.CheckConnectInternet;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.LocationHelper;
import vinhlong.ditagis.com.khaosatdongho.utities.MapViewHandler;
import vinhlong.ditagis.com.khaosatdongho.utities.MySnackBar;
import vinhlong.ditagis.com.khaosatdongho.utities.Popup;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Uri mUri;
    private Popup popup;
    private MapView mMapView;
    private ArcGISMap mMap;
    private Callout mCallout;
    private MapViewHandler mMapViewHandler;
    private static double LATITUDE = 10.2500783;//10.205155129125103;//;
    private static double LONGTITUDE = 105.9431823;//105.94397118543621;//;
    private static int LEVEL_OF_DETAIL = 14;
    private SearchView mTxtSearch;
    private ListView mListViewSearch;
    private DanhSachDongHoKHAdapter danhSachDongHoKHAdapter;
    private ArcGISMapImageLayer taiSanImageLayers, hanhChinhImageLayers;
    private LinearLayout mLinnearDisplayLayerTaiSan, mLinnearDisplayLayerBaseMap;
    private FloatingActionButton mFloatButtonLayer, mFloatButtonLocation;
    private CheckBox cb_Layer_HanhChinh, cb_Layer_TaiSan;
    private TraCuu traCuu;
    private int states[][];
    private int colors[];
    private boolean isChangingGeometry = false;
    private LocationDisplay mLocationDisplay;
    private int requestCode = 2;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 55;
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private LocationHelper mLocationHelper;
    private Location mLocation;
    private DApplication mApplication;
    private ArcGISFeature selectedFeature;

    public void setChangingGeometry(boolean changingGeometry, ArcGISFeature feature) {
        this.isChangingGeometry = changingGeometry;
        if (this.isChangingGeometry) {
            showPinToAdd();
            this.selectedFeature = feature;
        } else dismissPin();
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLicense();
        mApplication = (DApplication) getApplication();
        setUp();
        initListViewSearch();
        initLayerListView();
        setOnClickListener();
        startGPS();
        startSignIn();
        mApplication.setMainActivity(this);
    }

    private void setLoginInfos() {
        String displayName = mApplication.getUser().getDisplayName();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        View headerLayout = navigationView.getHeaderView(0);
        TextView namenv = headerLayout.findViewById(R.id.namenv);
        namenv.setText(displayName);
    }

    private void startGPS() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationHelper = new LocationHelper(this, (longtitude, latitude) -> {

        });
        if (!mLocationHelper.checkPlayServices()) {
            mLocationHelper.buildGoogleApiClient();
        }
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
//                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(i);
                if (!mLocationHelper.checkPlayServices()) {
                    mLocationHelper.buildGoogleApiClient();
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        assert locationManager != null;
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }

    private void startSignIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, Constant.
                REQUEST_LOGIN);
    }

    private void setOnClickListener() {
        findViewById(R.id.layout_layer_open_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_topo).setOnClickListener(this);
        findViewById(R.id.floatBtnLayer).setOnClickListener(this);
        findViewById(R.id.floatBtnAdd).setOnClickListener(this);
        findViewById(R.id.btn_add_feature_close).setOnClickListener(this);
        findViewById(R.id.btn_layer_close).setOnClickListener(this);
        findViewById(R.id.img_layvitri).setOnClickListener(this);
        findViewById(R.id.floatBtnLocation).setOnClickListener(this);
        findViewById(R.id.floatBtnHome).setOnClickListener(this);
    }

    private void initListViewSearch() {
        this.mListViewSearch = findViewById(R.id.lstview_search);
        //đưa listview search ra phía sau
        this.mListViewSearch.invalidate();
        List<DanhSachDongHoKHAdapter.Item> items = new ArrayList<>();
        this.danhSachDongHoKHAdapter = new DanhSachDongHoKHAdapter(MainActivity.this, items);
        this.mListViewSearch.setAdapter(danhSachDongHoKHAdapter);
        this.mListViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String objectID = ((DanhSachDongHoKHAdapter.Item) parent.getItemAtPosition(position)).getObjectID();
                mMapViewHandler.queryByObjectID(objectID);
                danhSachDongHoKHAdapter.clear();
                danhSachDongHoKHAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setUp() {
        states = new int[][]{{android.R.attr.state_checked}, {}};
        colors = new int[]{R.color.colorTextColor_1, R.color.colorTextColor_1};
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestPermisson();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMapView() {
        mMapView = findViewById(R.id.mapView);
        mMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, LATITUDE, LONGTITUDE, LEVEL_OF_DETAIL);
        mMapView.setMap(mMap);
        mCallout = mMapView.getCallout();
        final PreparingAsycn preparingAsycn = new PreparingAsycn(MainActivity.this, output -> {
            ListObjectDB.getInstance().getLstFeatureLayerDTG();
            setFeatureService();
        });
        if (CheckConnectInternet.isOnline(this))
            preparingAsycn.execute();
        final EditText edit_latitude = ((EditText) findViewById(R.id.edit_latitude));
        final EditText edit_longtitude = ((EditText) findViewById(R.id.edit_longtitude));
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {


            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                try {
                    if (mMapViewHandler != null)
                        mMapViewHandler.onSingleTapMapView(e);
                } catch (ArcGISRuntimeException ex) {
                    Log.d("", ex.toString());
                }
                return super.onSingleTapConfirmed(e);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mMapViewHandler != null) {
                    double[] location = mMapViewHandler.onScroll();
                    edit_longtitude.setText(location[0] + "");
                    edit_latitude.setText(location[1] + "");
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return super.onScale(detector);
            }
        });
        changeStatusOfLocationDataSource();
        mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                Point position = locationChangedEvent.getLocation().getPosition();
                edit_longtitude.setText(position.getX() + "");
                edit_latitude.setText(position.getY() + "");
                Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWebMercator());
                mMapView.setViewpointCenterAsync(geometry.getExtent().getCenter());
            }

        });

    }

    private void initLayerListView() {
        mLinnearDisplayLayerTaiSan = findViewById(R.id.linnearDisplayLayerTaiSan);
        mLinnearDisplayLayerBaseMap = findViewById(R.id.linnearDisplayLayerBaseMap);
        findViewById(R.id.layout_layer_open_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_topo).setOnClickListener(this);
        mFloatButtonLayer = findViewById(R.id.floatBtnLayer);
        mFloatButtonLayer.setOnClickListener(this);
        findViewById(R.id.btn_layer_close).setOnClickListener(this);
        mFloatButtonLocation = findViewById(R.id.floatBtnLocation);
        mFloatButtonLocation.setOnClickListener(this);

        cb_Layer_HanhChinh = findViewById(R.id.cb_Layer_HanhChinh);
        cb_Layer_TaiSan = findViewById(R.id.cb_Layer_TaiSan);
        cb_Layer_TaiSan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < mLinnearDisplayLayerTaiSan.getChildCount(); i++) {
                View view = mLinnearDisplayLayerTaiSan.getChildAt(i);
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    if (isChecked) checkBox.setChecked(true);
                    else checkBox.setChecked(false);
                }
            }
        });
        cb_Layer_HanhChinh.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < mLinnearDisplayLayerBaseMap.getChildCount(); i++) {
                View view = mLinnearDisplayLayerBaseMap.getChildAt(i);
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    if (isChecked) checkBox.setChecked(true);
                    else checkBox.setChecked(false);
                }
            }
        });
    }

    private void setFeatureService() {
        if (ListObjectDB.getInstance().getLstFeatureLayerDTG().size() == 0) return;
        popup = new Popup(MainActivity.this, mMapView, mCallout);
        mMapViewHandler = new MapViewHandler(mMapView, this, popup);
        EditingVatTu editingVatTu = new EditingVatTu(this, mMapView);
        mApplication.setEditingVatTu(editingVatTu);
        traCuu = new TraCuu(this, popup);
        for (LayerInfoDTG layerInfoDTG : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
            if (!layerInfoDTG.isView()) continue;
            String url = layerInfoDTG.getUrl();
            if (layerInfoDTG.getId().toUpperCase().equals(Constant.IDLayer.BASEMAP)) {
                hanhChinhImageLayers = new ArcGISMapImageLayer(url);
                hanhChinhImageLayers.setId(layerInfoDTG.getId());
                mMapView.getMap().getOperationalLayers().add(hanhChinhImageLayers);
                hanhChinhImageLayers.addDoneLoadingListener(() -> {
                    if (hanhChinhImageLayers.getLoadStatus() == LoadStatus.LOADED) {
                        ListenableList<ArcGISSublayer> sublayerList = hanhChinhImageLayers.getSublayers();
                        for (ArcGISSublayer sublayer : sublayerList) {
                            addCheckBox_SubLayer((ArcGISMapImageSublayer) sublayer, mLinnearDisplayLayerBaseMap);
                        }
                        String url_HanhChinh = url + "/5";
                        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url_HanhChinh);
                        popup.setmSFTHanhChinh(serviceFeatureTable);
                    }
                });
                hanhChinhImageLayers.loadAsync();
            } else if (layerInfoDTG.getId().toUpperCase().equals(Constant.IDLayer.CHUYENDE) && layerInfoDTG.isView()) {
                taiSanImageLayers = new ArcGISMapImageLayer(url);
                taiSanImageLayers.setName(layerInfoDTG.getTitleLayer());
                taiSanImageLayers.setId(layerInfoDTG.getId());
                mMapView.getMap().getOperationalLayers().add(taiSanImageLayers);
                taiSanImageLayers.addDoneLoadingListener(() -> {
                    if (taiSanImageLayers.getLoadStatus() == LoadStatus.LOADED) {
                        ListenableList<ArcGISSublayer> sublayerList = taiSanImageLayers.getSublayers();
                        for (ArcGISSublayer sublayer : sublayerList) {
                            if (sublayer.getId() != Constant.idDongHoKhachHang) {
                                addCheckBox_SubLayer((ArcGISMapImageSublayer) sublayer, mLinnearDisplayLayerTaiSan);
                            }
                        }
                    }
                });
                taiSanImageLayers.loadAsync();
            } else {
                ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
                FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
                featureLayer.setName(layerInfoDTG.getTitleLayer());
                featureLayer.setId(layerInfoDTG.getId());
                Action action = new Action(layerInfoDTG.isView(), layerInfoDTG.isCreate(), layerInfoDTG.isEdit(), layerInfoDTG.isDelete());
                FeatureLayerDTG featureLayerDTG = new FeatureLayerDTG(featureLayer, layerInfoDTG.getTitleLayer(), action);
                featureLayerDTG.setOutFields(getFieldsDTG(layerInfoDTG.getOutField()));
                featureLayerDTG.setQueryFields(getFieldsDTG(layerInfoDTG.getOutField()));
                featureLayerDTG.setUpdateFields(getFieldsDTG(layerInfoDTG.getOutField()));
                if (layerInfoDTG.getId() != null && layerInfoDTG.getId().equals(Constant.IDLayer.DHKHLYR)) {
//                    String userName = mApplication.getUser().getUserName();
                    featureLayer.setDefinitionExpression("NVKhaoSat = 'khaosatdongho' and TinhTrang = 'DKS'");
                    popup.setDongHoKHDTG(featureLayerDTG);
                    featureLayer.addDoneLoadingListener(() -> {
                        addCheckBox_LayerDHKH(featureLayer);
                    });
                    mApplication.setDongHoKHDTG(featureLayerDTG);
                    mMapViewHandler.setDongHoKHSFT(serviceFeatureTable);
                    mApplication.setDongHoKHSFT(serviceFeatureTable);
                    mApplication.getEditingVatTu().setDongHoKHSFT(serviceFeatureTable);
                }
                if (layerInfoDTG.getId() != null && layerInfoDTG.getId().equals(Constant.IDLayer.VATTUDONGHOTBL)) {
                    mApplication.getEditingVatTu().setVatTuDTG(featureLayerDTG);
                }
                if (layerInfoDTG.getId() != null && layerInfoDTG.getId().equals(Constant.IDLayer.DMVATTUTBL)) {
                    featureLayer.addDoneLoadingListener(() -> {
                        if (featureLayer.getLoadStatus().equals(LoadStatus.LOADED)) {
                            mApplication.getEditingVatTu().setDmVatTuSFT(serviceFeatureTable);
                        }
                    });
                }
                mMap.getOperationalLayers().add(featureLayer);
            }
        }
    }

    private void addCheckBox_LayerDHKH(FeatureLayer featureLayer) {
        LinearLayout linearLayout = findViewById(R.id.linnearDisplayLayer);
        final CheckBox checkBox = new CheckBox(linearLayout.getContext());
        checkBox.setText(featureLayer.getName());
        checkBox.setChecked(true);
        CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkBox.isChecked()) {
                if (buttonView.getText().equals(featureLayer.getName()))
                    featureLayer.setVisible(true);
            } else {
                if (checkBox.getText().equals(featureLayer.getName()))
                    featureLayer.setVisible(false);
            }
        });
        linearLayout.addView(checkBox);
    }

    private void addCheckBox_SubLayer(final ArcGISMapImageSublayer layer, LinearLayout linearLayout) {
        final CheckBox checkBox = new CheckBox(linearLayout.getContext());
        checkBox.setText(layer.getName());
        checkBox.setChecked(false);
        layer.setVisible(false);
        CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkBox.isChecked()) {
                if (buttonView.getText().equals(layer.getName()))
                    layer.setVisible(true);
            } else {
                if (checkBox.getText().equals(layer.getName()))
                    layer.setVisible(false);
            }
        });
        linearLayout.addView(checkBox);
    }

    private String[] getFieldsDTG(String stringFields) {
        String[] returnFields = null;
        if (stringFields != null) {
            if (stringFields == "*") {
                returnFields = new String[]{"*"};
            } else {
                returnFields = stringFields.split(",");
            }
        }
        return returnFields;
    }

    private void setLicense() {
        //way 1
        ArcGISRuntimeEnvironment.setLicense(getString(R.string.license));
    }

    private void changeStatusOfLocationDataSource() {
        mLocationDisplay = mMapView.getLocationDisplay();
//        changeStatusOfLocationDataSource();
        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {

                // If LocationDisplay started OK, then continue.
                if (dataSourceStatusChangedEvent.isStarted()) return;

                // No error is reported, then continue.
                if (dataSourceStatusChangedEvent.getError() == null) return;

                // If an error is found, handle the failure to start.
                // Check permissions to see if failure may be due to lack of permissions.
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[0]) == PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[1]) == PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quan_ly_su_co, menu);
        mTxtSearch = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mTxtSearch.setQueryHint(getString(R.string.title_search));
        mTxtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mMapViewHandler.querySearch(query, danhSachDongHoKHAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    danhSachDongHoKHAdapter.clear();
                    danhSachDongHoKHAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            MainActivity.this.mListViewSearch.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_thongke) {
            final Intent intent = new Intent(this, CongViecActivity.class);
            this.startActivityForResult(intent, requestCode);
        }  else if (id == R.id.nav_tracuu) {
            traCuu.start();
        } else if (id == R.id.nav_logOut) {
            startSignIn();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean requestPermisson() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, REQUEST_ID_IMAGE_CAPTURE);
        }
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else return true;
    }

    private void goHome() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();

        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    public void showPinToAdd() {
        findViewById(R.id.linear_addfeature).setVisibility(View.VISIBLE);
        findViewById(R.id.img_map_pin).setVisibility(View.VISIBLE);
        findViewById(R.id.floatBtnAdd).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatBtnLayer:
                v.setVisibility(View.INVISIBLE);
                ((LinearLayout) findViewById(R.id.layout_layer)).setVisibility(View.VISIBLE);
                break;
            case R.id.layout_layer_open_street_map:
                mMapView.getMap().setMaxScale(1128.497175);
                mMapView.getMap().setBasemap(Basemap.createOpenStreetMap());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_open_street_map);
                break;
            case R.id.layout_layer_street_map:
                mMapView.getMap().setMaxScale(1128.497176);
                mMapView.getMap().setBasemap(Basemap.createStreets());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_street_map);
                break;
            case R.id.layout_layer_topo:
                mMapView.getMap().setMaxScale(5);
                mMapView.getMap().setBasemap(Basemap.createImageryWithLabels());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_topo);

                break;
            case R.id.btn_layer_close:
                findViewById(R.id.layout_layer).setVisibility(View.INVISIBLE);
                findViewById(R.id.floatBtnLayer).setVisibility(View.VISIBLE);
                break;
            case R.id.img_layvitri:
                if (this.isChangingGeometry) {
                    mMapViewHandler.updateGeometry(this.selectedFeature);
                } else
                    mMapViewHandler.addFeature();
                break;
            case R.id.floatBtnAdd:
                showPinToAdd();
                break;
            case R.id.btn_add_feature_close:
                dismissPin();
                break;
            case R.id.floatBtnLocation:
                if (!mLocationDisplay.isStarted()) mLocationDisplay.startAsync();
                else mLocationDisplay.stop();
                break;
            case R.id.floatBtnHome:
                goHome();
                break;
        }
    }

    public void dismissPin() {
        findViewById(R.id.linear_addfeature).setVisibility(View.GONE);
        findViewById(R.id.img_map_pin).setVisibility(View.GONE);
        findViewById(R.id.floatBtnAdd).setVisibility(View.VISIBLE);
    }

    @SuppressLint("ResourceAsColor")
    private void handlingColorBackgroundLayerSelected(int id) {
        switch (id) {
            case R.id.layout_layer_open_street_map:
                ((ImageView) findViewById(R.id.img_layer_open_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                ((ImageView) findViewById(R.id.img_layer_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                ((ImageView) findViewById(R.id.img_layer_topo)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.layout_layer_street_map:
                ((ImageView) findViewById(R.id.img_layer_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                ((ImageView) findViewById(R.id.img_layer_open_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                ((ImageView) findViewById(R.id.img_layer_topo)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.layout_layer_topo:
                ((ImageView) findViewById(R.id.img_layer_topo)).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                ((ImageView) findViewById(R.id.img_layer_open_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                ((ImageView) findViewById(R.id.img_layer_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
        }
    }

    @Nullable
    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            assert in != null;
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Bitmap bitmap;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                bitmap = BitmapFactory.decodeStream(in, null, o);
                // resize to desired dimensions
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x, (int) y, true);
                bitmap.recycle();
                bitmap = scaledBitmap;

                System.gc();
            } else {
                bitmap = BitmapFactory.decodeStream(in);
            }
            assert in != null;
            in.close();
            return bitmap;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String returnedResult = data.getExtras().get(getString(R.string.ket_qua_objectid)).toString();
            if (resultCode == Activity.RESULT_OK) {
                mMapViewHandler.queryByObjectID(returnedResult);
            }
        } catch (Exception e) {
        }

        switch (requestCode) {
            case Constant.REQUEST.ID_UPDATE_ATTACHMENT:
                if (resultCode == RESULT_OK) {
                    Uri uri = mApplication.getUri();
                    if (uri != null) {
                        Bitmap bitmap = getBitmap(uri.getPath());
                        try {
                            if (bitmap != null) {
                                Matrix matrix = new Matrix();
                                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                byte[] image = outputStream.toByteArray();
                                outputStream.close();
                                Toast.makeText(this, "Đã lưu ảnh", Toast.LENGTH_SHORT).show();
                                UpdateAttachmentAsync updateAttachmentAsync = new UpdateAttachmentAsync(this, mApplication.getSelectedFeature(), image);
                                updateAttachmentAsync.execute();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    MySnackBar.make(mMapView, "Hủy chụp ảnh", false);
                } else {
                    MySnackBar.make(mMapView, "Lỗi khi chụp ảnh", false);
                }
                break;
            case Constant.REQUEST_LOGIN:
                if (Activity.RESULT_OK != resultCode) {
                    finish();

                    return;
                } else {
                    setLoginInfos();
                    initMapView();
                }
                break;
        }
    }
}