package com.princemartbd.dealer.activity.Base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.princemartbd.dealer.R;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;
import com.princemartbd.dealer.model.Address;
import com.princemartbd.dealer.model.Area;
import com.princemartbd.dealer.model.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetAddressActivity extends AppCompatActivity {

    private Session session;
    private Activity activity;

    public static Address address1;
    public static String pincodeId = "0", areaId = "0", cityId = "0";
    ArrayList<City> cityArrayList;
    ArrayList<Area> areaArrayList;
    Button btnSubmit;
    ProgressBar progressBar;
    TextView edtAddress;
    @SuppressLint("StaticFieldLeak")
    public static TextView edtPinCode, edtCity, edtArea;
    ScrollView scrollView;
    boolean isLoadMore = false;
    int total = 0;
    int offset = 0;
    CityAdapter cityAdapter;
    AreaAdapter areaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Address Update");
        }
        toolbar.setNavigationOnClickListener(v -> {
           session.goBackDealer(activity);
        });

        activity = this;
        session = new Session(activity);

        edtPinCode = findViewById(R.id.edtPinCode);
        edtCity = findViewById(R.id.edtCity);
        edtArea = findViewById(R.id.edtArea);
        edtAddress = findViewById(R.id.edtAddress);
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);
        btnSubmit = findViewById(R.id.btnSubmit);

        session = new Session(activity);

        address1 = new Address();

        edtCity.setOnClickListener(v -> OpenDialog(activity, "city"));

        edtArea.setOnClickListener(v -> {
            if (!cityId.equals("0") && edtArea.isEnabled()) {
                OpenDialog(activity, "area");
            } else {
                edtArea.setError(getString(R.string.select_city_first));
            }
        });

        btnSubmit.setOnClickListener(view -> {
            ApiConfig.hideKeyboard(activity, view);
            AddUpdateAddress();
        });
    }

    public void AddUpdateAddress() {
        if (cityId.isEmpty()) {
            Toast.makeText(activity, "City Is not Please select your City Name", Toast.LENGTH_SHORT).show();
        } else if (areaId.isEmpty()) {
            Toast.makeText(activity, "Area Is not Please select your Area Name", Toast.LENGTH_SHORT).show();
        } else if (edtAddress.getText().toString().trim().isEmpty()) {
            edtAddress.requestFocus();
            edtAddress.setError("Please enter address!");
        } else {
            Map<String, String> params = new HashMap<>();
            params.put(Constant.PAGE, Constant.UPDATE_ADDRESS);
            params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
            params.put(Constant.ADDRESS, edtAddress.getText().toString().trim());
            params.put(Constant.CITY_ID, cityId);
            params.put(Constant.AREA_ID, areaId);
            params.put(Constant.PINCODE_ID, pincodeId);

            ApiConfig.RequestToVolley((result, response) -> {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, activity, Constant.AUTH_LOGIN_URL, params, true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        session.goBackDealer(activity);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void OpenDialog(Activity activity, String from) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialogView = inflater1.inflate(R.layout.dialog_city_area_selection, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RecyclerView recyclerView;
        NestedScrollView scrollView;
        TextView tvSearch, tvAlert;
        EditText searchView;

        scrollView = dialogView.findViewById(R.id.scrollView);
        tvSearch = dialogView.findViewById(R.id.tvSearch);
        tvAlert = dialogView.findViewById(R.id.tvAlert);
        searchView = dialogView.findViewById(R.id.searchView);
        recyclerView = dialogView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchView.getText().toString().trim().length() > 0) {
                    searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close, 0);
                } else {
                    searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (from.equals("city")) {
            tvAlert.setText(getString(R.string.no_cities_found));
            GetCityData("", recyclerView, tvAlert, linearLayoutManager, scrollView, dialog);
            tvSearch.setOnClickListener(v -> GetCityData(searchView.getText().toString().trim(), recyclerView, tvAlert, linearLayoutManager, scrollView, dialog));
            searchView.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (searchView.getText().toString().trim().length() > 0) {
                        if (event.getRawX() >= (searchView.getRight() - searchView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
                            searchView.setText("");
                            GetCityData("", recyclerView, tvAlert, linearLayoutManager, scrollView, dialog);
                        }
                        return true;
                    }
                }
                return false;
            });
        } else {
            tvAlert.setText(getString(R.string.no_areas_found));
            GetAreaData("", recyclerView, tvAlert, linearLayoutManager, scrollView, dialog);
            tvSearch.setOnClickListener(v -> GetAreaData(searchView.getText().toString().trim(), recyclerView, tvAlert, linearLayoutManager, scrollView, dialog));
            searchView.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (searchView.getText().toString().trim().length() > 0) {
                        if (event.getRawX() >= (searchView.getRight() - searchView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
                            searchView.setText("");
                            GetAreaData("", recyclerView, tvAlert, linearLayoutManager, scrollView, dialog);
                        }
                        return true;
                    }
                }
                return false;
            });
        }

        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    void GetCityData(String search, RecyclerView recyclerView, TextView tvAlert, LinearLayoutManager linearLayoutManager, NestedScrollView scrollView, AlertDialog dialog) {
        cityArrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_CITIES, Constant.GetVal);
        params.put(Constant.SEARCH, search);
        params.put(Constant.OFFSET, "" + offset);
        params.put(Constant.LIMIT, "" + (Constant.LOAD_ITEM_LIMIT + 20));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        try {

                            total = Integer.parseInt(jsonObject.getString(Constant.TOTAL));

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            Gson g = new Gson();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                City city = g.fromJson(jsonObject1.toString(), City.class);
                                cityArrayList.add(city);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (offset == 0) {
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            tvAlert.setVisibility(View.GONE);
                            cityAdapter = new CityAdapter(activity, cityArrayList, dialog);
                            cityAdapter.setHasStableIds(true);
                            recyclerView.setAdapter(cityAdapter);
                            scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                                // if (diff == 0) {
                                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                    if (cityArrayList.size() < total) {
                                        if (!isLoadMore) {
                                            if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == cityArrayList.size() - 1) {
                                                //bottom of list!
                                                cityArrayList.add(null);
                                                cityAdapter.notifyItemInserted(cityArrayList.size() - 1);
                                                offset += Constant.LOAD_ITEM_LIMIT + 20;

                                                Map<String, String> params1 = new HashMap<>();
                                                params1.put(Constant.GET_CITIES, Constant.GetVal);
                                                params1.put(Constant.SEARCH, search);
                                                params1.put(Constant.OFFSET, "" + offset);
                                                params1.put(Constant.LIMIT, "" + (Constant.LOAD_ITEM_LIMIT + 20));

                                                ApiConfig.RequestToVolley((result1, response1) -> {
                                                    if (result1) {
                                                        try {
                                                            JSONObject jsonObject1 = new JSONObject(response1);
                                                            if (!jsonObject1.getBoolean(Constant.ERROR)) {
                                                                cityArrayList.remove(cityArrayList.size() - 1);
                                                                cityAdapter.notifyItemRemoved(cityArrayList.size());

                                                                JSONObject object = new JSONObject(response1);
                                                                JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                                                Gson g = new Gson();
                                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                                                    City city = g.fromJson(jsonObject2.toString(), City.class);
                                                                    cityArrayList.add(city);
                                                                }
                                                                cityAdapter.notifyDataSetChanged();
                                                                cityAdapter.setLoaded();
                                                                isLoadMore = false;
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, activity, Constant.LOCATION, params1, false);

                                            }
                                            isLoadMore = true;
                                        }

                                    }
                                }
                            });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        tvAlert.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, activity, Constant.LOCATION, params, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    void GetAreaData(String search, RecyclerView recyclerView, TextView tvAlert, LinearLayoutManager linearLayoutManager, NestedScrollView scrollView, AlertDialog dialog) {
        areaArrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_AREAS, Constant.GetVal);
        params.put(Constant.CITY_ID, cityId);
        params.put(Constant.SEARCH, search);
        params.put(Constant.OFFSET, "" + offset);
        params.put(Constant.LIMIT, "" + (Constant.LOAD_ITEM_LIMIT + 20));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        try {
                            total = Integer.parseInt(jsonObject.getString(Constant.TOTAL));

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            Gson g = new Gson();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Area area = g.fromJson(jsonObject1.toString(), Area.class);
                                areaArrayList.add(area);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (offset == 0) {
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            tvAlert.setVisibility(View.GONE);
                            areaAdapter = new AreaAdapter(activity, areaArrayList, dialog);
                            areaAdapter.setHasStableIds(true);
                            recyclerView.setAdapter(areaAdapter);
                            scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                                // if (diff == 0) {
                                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                    if (areaArrayList.size() < total) {
                                        if (!isLoadMore) {
                                            if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == areaArrayList.size() - 1) {
                                                //bottom of list!
                                                areaArrayList.add(null);
                                                areaAdapter.notifyItemInserted(areaArrayList.size() - 1);
                                                offset += Constant.LOAD_ITEM_LIMIT + 20;

                                                Map<String, String> params1 = new HashMap<>();
                                                params1.put(Constant.GET_AREAS, Constant.GetVal);
                                                params1.put(Constant.CITY_ID, cityId);
                                                params1.put(Constant.SEARCH, search);
                                                params1.put(Constant.OFFSET, "" + offset);
                                                params1.put(Constant.LIMIT, "" + (Constant.LOAD_ITEM_LIMIT + 20));

                                                ApiConfig.RequestToVolley((result1, response1) -> {
                                                    if (result1) {
                                                        try {
                                                            JSONObject jsonObject1 = new JSONObject(response1);
                                                            if (!jsonObject1.getBoolean(Constant.ERROR)) {
                                                                areaArrayList.remove(areaArrayList.size() - 1);
                                                                areaAdapter.notifyItemRemoved(areaArrayList.size());

                                                                JSONObject object = new JSONObject(response1);
                                                                JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                                                Gson g = new Gson();
                                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                                                    Area area = g.fromJson(jsonObject2.toString(), Area.class);
                                                                    areaArrayList.add(area);
                                                                }
                                                                areaAdapter.notifyDataSetChanged();
                                                                areaAdapter.setLoaded();
                                                                isLoadMore = false;
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, activity, Constant.LOCATION, params1, false);

                                            }
                                            isLoadMore = true;
                                        }

                                    }
                                }
                            });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        tvAlert.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, activity, Constant.LOCATION, params, false);
    }


    static class CityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        // for load more
        public final int VIEW_TYPE_ITEM = 0;
        public final int VIEW_TYPE_LOADING = 1;
        final Activity activity;
        final ArrayList<City> cities;
        public boolean isLoading;
        final Session session;
        final AlertDialog dialog;


        public CityAdapter(Activity activity, ArrayList<City> cities, AlertDialog dialog) {
            this.activity = activity;
            this.session = new Session(activity);
            this.cities = cities;
            this.dialog = dialog;
        }

        public void add(int position, City city) {
            cities.add(position, city);
            notifyItemInserted(position);
        }

        public void setLoaded() {
            isLoading = false;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
            View view;
            switch (viewType) {
                case (VIEW_TYPE_ITEM):
                    view = LayoutInflater.from(activity).inflate(R.layout.lyt_pin_code_list, parent, false);
                    return new HolderItems(view);
                case (VIEW_TYPE_LOADING):
                    view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
                    return new ViewHolderLoading(view);
                default:
                    throw new IllegalArgumentException("unexpected viewType: " + viewType);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, final int position) {

            if (holderParent instanceof HolderItems) {
                final HolderItems holder = (HolderItems) holderParent;
                try {
                    final City city = cities.get(position);
                    holder.tvPinCode.setText(city.getCity_name());

                    holder.tvPinCode.setOnClickListener(v -> {
                        edtCity.setText(city.getCity_name());
                        cityId = city.getId();
                        areaId = "0";
                        edtArea.setText("");
                        edtArea.setEnabled(true);
                        dialog.dismiss();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (holderParent instanceof ViewHolderLoading) {
                ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderParent;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return cities.size();
        }

        @Override
        public int getItemViewType(int position) {
            return cities.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        static class ViewHolderLoading extends RecyclerView.ViewHolder {
            public final ProgressBar progressBar;

            public ViewHolderLoading(View view) {
                super(view);
                progressBar = view.findViewById(R.id.itemProgressbar);
            }
        }

        static class HolderItems extends RecyclerView.ViewHolder {

            final TextView tvPinCode;

            public HolderItems(@NonNull View itemView) {
                super(itemView);

                tvPinCode = itemView.findViewById(R.id.tvPinCode);
            }
        }
    }

    static class AreaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        // for load more
        public final int VIEW_TYPE_ITEM = 0;
        public final int VIEW_TYPE_LOADING = 1;
        final Activity activity;
        final ArrayList<Area> areas;
        public boolean isLoading;
        final Session session;
        final AlertDialog dialog;


        public AreaAdapter(Activity activity, ArrayList<Area> areas, AlertDialog dialog) {
            this.activity = activity;
            this.session = new Session(activity);
            this.areas = areas;
            this.dialog = dialog;
        }

        public void add(int position, Area area) {
            areas.add(position, area);
            notifyItemInserted(position);
        }

        public void setLoaded() {
            isLoading = false;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
            View view;
            switch (viewType) {
                case (VIEW_TYPE_ITEM):
                    view = LayoutInflater.from(activity).inflate(R.layout.lyt_pin_code_list, parent, false);
                    return new HolderItems(view);
                case (VIEW_TYPE_LOADING):
                    view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
                    return new ViewHolderLoading(view);
                default:
                    throw new IllegalArgumentException("unexpected viewType: " + viewType);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, final int position) {

            if (holderParent instanceof HolderItems) {
                final HolderItems holder = (HolderItems) holderParent;
                try {
                    final Area area = areas.get(position);

                    holder.tvPinCode.setText(area.getName());

                    holder.tvPinCode.setOnClickListener(v -> {
                        edtArea.setText(area.getName());
                        areaId = area.getId();
                        pincodeId = area.getPincode_id();
                        edtPinCode.setText(area.getPincode());
                        dialog.dismiss();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (holderParent instanceof ViewHolderLoading) {
                ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderParent;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return areas.size();
        }

        @Override
        public int getItemViewType(int position) {
            return areas.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        static class ViewHolderLoading extends RecyclerView.ViewHolder {
            public final ProgressBar progressBar;

            public ViewHolderLoading(View view) {
                super(view);
                progressBar = view.findViewById(R.id.itemProgressbar);
            }
        }

        static class HolderItems extends RecyclerView.ViewHolder {

            final TextView tvPinCode;

            public HolderItems(@NonNull View itemView) {
                super(itemView);

                tvPinCode = itemView.findViewById(R.id.tvPinCode);
            }
        }
    }

}