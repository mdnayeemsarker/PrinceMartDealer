package com.princemartbd.dealer.helper.album.app.gallery;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.helper.album.Action;
import com.princemartbd.dealer.helper.album.Album;
import com.princemartbd.dealer.helper.album.ItemAction;
import com.princemartbd.dealer.helper.album.api.widget.Widget;
import com.princemartbd.dealer.helper.album.app.Contract;
import com.princemartbd.dealer.helper.album.mvp.BaseActivity;

public class GalleryActivity extends BaseActivity implements Contract.GalleryPresenter {

    public static Action<ArrayList<String>> sResult;
    public static Action<String> sCancel;

    public static ItemAction<String> sClick;
    public static ItemAction<String> sLongClick;

    private Widget mWidget;
    private ArrayList<String> mPathList;
    private int mCurrentPosition;
    private boolean mCheckable;

    private Map<String, Boolean> mCheckedMap;

    private Contract.GalleryView<String> mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity_gallery);
        mView = new GalleryView<>(this, this);

        Bundle argument = getIntent().getExtras();
        assert argument != null;
        mWidget = argument.getParcelable(Album.KEY_INPUT_WIDGET);
        mPathList = argument.getStringArrayList(Album.KEY_INPUT_CHECKED_LIST);
        mCurrentPosition = argument.getInt(Album.KEY_INPUT_CURRENT_POSITION);
        mCheckable = argument.getBoolean(Album.KEY_INPUT_GALLERY_CHECKABLE);

        mCheckedMap = new HashMap<>();
        for (String path : mPathList) mCheckedMap.put(path, true);

        mView.setTitle(mWidget.getTitle());
        mView.setupViews(mWidget, mCheckable);
        if (!mCheckable) mView.setBottomDisplay(false);
        mView.setLayerDisplay(false);
        mView.setDurationDisplay(false);
        mView.bindData(mPathList);

        if (mCurrentPosition == 0) {
            onCurrentChanged(mCurrentPosition);
        } else {
            mView.setCurrentItem(mCurrentPosition);
        }
        setCheckedCount();
    }

    private void setCheckedCount() {
        int checkedCount = 0;
        for (Map.Entry<String, Boolean> entry : mCheckedMap.entrySet()) {
            if (entry.getValue()) checkedCount += 1;
        }

        String completeText = getString(R.string.album_menu_finish);
        completeText += "(" + checkedCount + " / " + mPathList.size() + ")";
        mView.setCompleteText(completeText);
    }

    @Override
    public void clickItem(int position) {
        if (sClick != null) {
            sClick.onAction(GalleryActivity.this, mPathList.get(mCurrentPosition));
        }
    }

    @Override
    public void longClickItem(int position) {
        if (sLongClick != null) {
            sLongClick.onAction(GalleryActivity.this, mPathList.get(mCurrentPosition));
        }
    }

    @Override
    public void onCurrentChanged(int position) {
        mCurrentPosition = position;
        mView.setSubTitle(position + 1 + " / " + mPathList.size());

        if (mCheckable) mView.setChecked(mCheckedMap.get(mPathList.get(position)));
    }

    @Override
    public void onCheckedChanged() {
        String path = mPathList.get(mCurrentPosition);
        mCheckedMap.put(path, !mCheckedMap.get(path));

        setCheckedCount();
    }

    @Override
    public void complete() {
        if (sResult != null) {
            ArrayList<String> checkedList = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : mCheckedMap.entrySet()) {
                if (entry.getValue()) checkedList.add(entry.getKey());
            }
            sResult.onAction(checkedList);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (sCancel != null) sCancel.onAction("User canceled.");
        finish();
    }

    @Override
    public void finish() {
        sResult = null;
        sCancel = null;
        sClick = null;
        sLongClick = null;
        super.finish();
    }
}
