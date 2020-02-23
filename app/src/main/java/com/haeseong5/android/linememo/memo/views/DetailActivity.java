package com.haeseong5.android.linememo.memo.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.haeseong5.android.linememo.R;
import com.haeseong5.android.linememo.memo.utils.DrawUrlImageTask;
import com.haeseong5.android.linememo.memo.database.DatabaseHelper;
import com.haeseong5.android.linememo.memo.utils.DbBitmapUtility;
import com.haeseong5.android.linememo.memo.database.models.Memo;

import java.io.IOException;
import java.util.ArrayList;

import static com.haeseong5.android.linememo.memo.views.MainActivity.ADD_MEMO_REQUEST;
import static com.haeseong5.android.linememo.memo.views.MainActivity.DETAIL_MEMO_REQUEST;
import static com.haeseong5.android.linememo.memo.views.MainActivity.EDIT_MEMO_REQUEST;

public class DetailActivity extends AppCompatActivity {

    public String TAG = DetailActivity.class.getName();

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private DatabaseHelper db;

    private int mActivityMode; // ADD or EDIT or DETAIL MODE
    private int mPosition = -1; // RecyclerView Item Index
    private Memo mMemo; //Current Memo Data
    private ArrayList<byte[]> mImageList; //Current Image list of Current memo data

    //view
    public ImageUrlDialog mDialog;
    private EditText mEtTitle, mEtContent;
    private Toolbar mToolbar;
    private RecyclerView mRvImageList;
    private ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getCameraPermission();
        db = new DatabaseHelper(this);
        mImageList = new ArrayList<>();

        initView();
        initToolbar();
        initImageRecyclerView();

        //initial Working Mode and Data
        Intent intent = getIntent();
        if (intent != null) {
            mActivityMode = intent.getIntExtra("request_code", -1);
            mPosition = intent.getIntExtra("mPosition", -1);
            switch (mActivityMode) {
                case ADD_MEMO_REQUEST:
                    mActivityMode = ADD_MEMO_REQUEST;
                    mEtTitle.setEnabled(true);
                    mEtContent.setEnabled(true);
                    break;
                case DETAIL_MEMO_REQUEST:
                    mActivityMode = DETAIL_MEMO_REQUEST;
                    mMemo = intent.getParcelableExtra("memo");
                    if (mMemo != null) {
                        mEtTitle.setText(mMemo.getTitle());
                        mEtContent.setText(mMemo.getContent());
                        if (db.getImages(mMemo.getId()).size() > 0) {
                            mImageList.addAll(db.getImages(mMemo.getId()));
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    mEtContent.setEnabled(false);
                    mEtTitle.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    }

    private void initView() {
        mEtTitle = findViewById(R.id.detail_et_title);
        mEtContent = findViewById(R.id.detail_et_content);
        mRvImageList = findViewById(R.id.detail_rv_image_list);
        mToolbar = findViewById(R.id.toolbar);
    }

    void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolbar.setTitle(R.string.app_title);
    }

    private void initImageRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mRvImageList.setLayoutManager(layoutManager);
        mAdapter = new ImageAdapter(this, mImageList);
        mRvImageList.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                AlertDialog.Builder imageDialog = new AlertDialog.Builder(DetailActivity.this);
                imageDialog.setMessage("이미지를 삭제하시겠습니까?")
                        .setTitle("Line Memo")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mImageList.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ;
                            }
                        }).
                        setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                        .show();
            }
        });
    }

    public void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new ImageUrlDialog(this);
        }
        mDialog.setCancelable(false);
        mDialog.show();

        mDialog.setDialogListener(new ImageUrlDialog.CustomDialogListener() {
            @Override
            public void onPositiveClicked(String url) {
                new DrawUrlImageTask(mImageList, mAdapter).execute(url);
            }

            @Override
            public void onNegativeClicked() {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
    }

    private Memo createMemo(String title, String content, ArrayList<byte[]> imageDTOS) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(title, content, imageDTOS);

        // get the newly inserted note from db
        Memo memo = db.getNote(id);
        return memo;
    }

    private void updateNote() {
        // updating note in db
        db.updateNote(mMemo);
        if (mImageList.size() > 0) {
            db.deleteImage(mMemo.getId());
            // insert image_table
            for (byte[] b : mImageList) {
                db.addImage(mMemo.getId(), b);
            }
        }
        //Change to Detail Activity Mode : can't edit
        mActivityMode = DETAIL_MEMO_REQUEST;
        invalidateOptionsMenu(); //replace toolbar
    }

    private void deleteNote() {
        // deleting the note from db
        db.deleteNote(mMemo);
        db.deleteImage(mMemo.getId());
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //replace toolbar according to the situation.
        MenuInflater inflater = getMenuInflater();
        if (mActivityMode == EDIT_MEMO_REQUEST || mActivityMode == ADD_MEMO_REQUEST) {
            inflater.inflate(R.menu.menu_toolbar_edit, menu);
        } else if (mActivityMode == DETAIL_MEMO_REQUEST) {
            inflater.inflate(R.menu.menu_toolbar_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // back arrow icon
                finish();
                return true;
            case R.id.menu_item_detail_edit:  // pen icon : change to edit mode
                mActivityMode = EDIT_MEMO_REQUEST;
                invalidateOptionsMenu();
                mEtTitle.setEnabled(true);
                mEtContent.setEnabled(true);
                return true;
            case R.id.menu_item_finish_memo: // check icon : write data to db. change to detail mode
                String title = mEtTitle.getText().toString();
                String content = mEtContent.getText().toString();
                ArrayList<byte[]> images = mImageList;
                if (mActivityMode == ADD_MEMO_REQUEST) {
                    createMemo(title, content, images);
                    mActivityMode = DETAIL_MEMO_REQUEST;
                    invalidateOptionsMenu();
                    mEtTitle.setEnabled(false);
                    mEtContent.setEnabled(false);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                } else if (mActivityMode == EDIT_MEMO_REQUEST) {
                    mMemo.setTitle(mEtTitle.getText().toString());
                    mMemo.setContent(mEtContent.getText().toString());
                    updateNote();
                    mEtTitle.setEnabled(false);
                    mEtContent.setEnabled(false);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
                return true;

            case R.id.toolbar_menu_detail_delete: //delete menu
                deleteNote();
                return true;
            case R.id.toolbar_menu_gallery: //picture icon
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQUEST);
                return true;
            case R.id.toolbar_menu_camera: //picture icon
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                return true;
            case R.id.toolbar_menu_url: //picture icon
                showProgressDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED && requestCode == GALLERY_REQUEST) {
            Uri image = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);

                byte[] bytes = DbBitmapUtility.getBytes(bitmap);
                mImageList.add(bytes);
                mAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data.hasExtra("data")) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap != null) {
                byte[] bytes = DbBitmapUtility.getBytes(bitmap);
                mImageList.add(bytes);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(DetailActivity.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    void printLog(String message) {
        Log.d(TAG, message);
    }
}
