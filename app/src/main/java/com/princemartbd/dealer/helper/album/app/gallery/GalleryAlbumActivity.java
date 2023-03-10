package com.princemartbd.dealer.helper.album.app.gallery;

import android.os.Bundle;

import java.util.ArrayList;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.helper.album.Action;
import com.princemartbd.dealer.helper.album.Album;
import com.princemartbd.dealer.helper.album.AlbumFile;
import com.princemartbd.dealer.helper.album.ItemAction;
import com.princemartbd.dealer.helper.album.api.widget.Widget;
import com.princemartbd.dealer.helper.album.app.Contract;
import com.princemartbd.dealer.helper.album.mvp.BaseActivity;
import com.princemartbd.dealer.helper.album.util.AlbumUtils;

public class GalleryAlbumActivity extends BaseActivity implements Contract.GalleryPresenter {

    public static Action<ArrayList<AlbumFile>> sResult;
    public static Action<String> sCancel;

    public static ItemAction<AlbumFile> sClick;
    public static ItemAction<AlbumFile> sLongClick;

    private Widget mWidget;
    private ArrayList<AlbumFile> mAlbumFiles;
    private int mCurrentPosition;
    private boolean mCheckable;

    private Contract.GalleryView<AlbumFile> mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity_gallery);

        mView = new GalleryView<>(this, this);

        Bundle argument = getIntent().getExtras();
        assert argument != null;
        mWidget = argument.getParcelable(Album.KEY_INPUT_WIDGET);
        mAlbumFiles = argument.getParcelableArrayList(Album.KEY_INPUT_CHECKED_LIST);
        mCurrentPosition = argument.getInt(Album.KEY_INPUT_CURRENT_POSITION);
        mCheckable = argument.getBoolean(Album.KEY_INPUT_GALLERY_CHECKABLE);

        mView.setTitle(mWidget.getTitle());
        mView.setupViews(mWidget, mCheckable);
        mView.bindData(mAlbumFiles);
        if (mCurrentPosition == 0) {
            onCurrentChanged(mCurrentPosition);
        } else {
            mView.setCurrentItem(mCurrentPosition);
        }
        setCheckedCount();
    }

    private void setCheckedCount() {
        int checkedCount = 0;
        for (AlbumFile albumFile : mAlbumFiles) {
            if (albumFile.isChecked()) checkedCount += 1;
        }

        String completeText = getString(R.string.album_menu_finish);
        completeText += "(" + checkedCount + " / " + mAlbumFiles.size() + ")";
        mView.setCompleteText(completeText);
    }

    @Override
    public void clickItem(int position) {
        if (sClick != null) {
            sClick.onAction(GalleryAlbumActivity.this, mAlbumFiles.get(mCurrentPosition));
        }
    }

    @Override
    public void longClickItem(int position) {
        if (sLongClick != null) {
            sLongClick.onAction(GalleryAlbumActivity.this, mAlbumFiles.get(mCurrentPosition));
        }
    }

    @Override
    public void onCurrentChanged(int position) {
        mCurrentPosition = position;
        mView.setSubTitle(position + 1 + " / " + mAlbumFiles.size());

        AlbumFile albumFile = mAlbumFiles.get(position);
        if (mCheckable) mView.setChecked(albumFile.isChecked());
        mView.setLayerDisplay(albumFile.isDisable());

        if (albumFile.getMediaType() == AlbumFile.TYPE_VIDEO) {
            if (!mCheckable) mView.setBottomDisplay(true);
            mView.setDuration(AlbumUtils.convertDuration(albumFile.getDuration()));
            mView.setDurationDisplay(true);
        } else {
            if (!mCheckable) mView.setBottomDisplay(false);
            mView.setDurationDisplay(false);
        }
    }

    @Override
    public void onCheckedChanged() {
        AlbumFile albumFile = mAlbumFiles.get(mCurrentPosition);
        albumFile.setChecked(!albumFile.isChecked());

        setCheckedCount();
    }

    @Override
    public void complete() {
        if (sResult != null) {
            ArrayList<AlbumFile> checkedList = new ArrayList<>();
            for (AlbumFile albumFile : mAlbumFiles) {
                if (albumFile.isChecked()) checkedList.add(albumFile);
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
