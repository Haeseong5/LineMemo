package com.haeseong5.android.linememo.memo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.textfield.TextInputEditText;
import com.haeseong5.android.linememo.R;
import com.haeseong5.android.linememo.memo.database.DatabaseHelper;
import com.haeseong5.android.linememo.memo.database.model.Memo;

import java.util.ArrayList;

import static com.haeseong5.android.linememo.memo.MainActivity.ADD_MEMO_REQUEST;
import static com.haeseong5.android.linememo.memo.MainActivity.DETAIL_MEMO_REQUEST;
import static com.haeseong5.android.linememo.memo.MainActivity.EDIT_MEMO_REQUEST;

public class WriteActivity extends AppCompatActivity {
    private int mActivityMode;
    private TextInputEditText mEtTitle, mEtContent;
    private Toolbar mToolbar;
    private RecyclerView mRvImageList;
    private ArrayList<ImageItem> images;
    private int position = -1;
    private DatabaseHelper db;
    private Memo mMemo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        initView();
        initToolbar();
        db = new DatabaseHelper(this);
        initRecyclerView();

        Intent intent = getIntent();
        if(intent != null){
            mActivityMode = intent.getIntExtra("request_code" , -1);
            position = intent.getIntExtra("position", -1);
            switch (mActivityMode){
                case ADD_MEMO_REQUEST:
                    mActivityMode = ADD_MEMO_REQUEST;
                    mEtContent.setEnabled(true);
                    mEtTitle.setEnabled(true);
                    break;
                case DETAIL_MEMO_REQUEST:
                    mActivityMode = DETAIL_MEMO_REQUEST;
                    mMemo = (Memo)intent.getSerializableExtra("memo");
                    if(mMemo != null){
                        mEtTitle.setText(mMemo.getTitle());
                        mEtContent.setText(mMemo.getContent());
                    }else {
                        mMemo = new Memo();
                    }
                    mEtContent.setEnabled(false);
                    mEtTitle.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    }
    private void initView(){
        mEtTitle = findViewById(R.id.et_write_title);
        mEtContent = findViewById(R.id.et_write_content);
        mRvImageList = findViewById(R.id.rv_write_image_list);
        mToolbar = findViewById(R.id.toolbar);
    }
    void initToolbar(){
        //액션바는 버전마다 다르게 동작한다. => 툴바를 사용 => 일관된 앱바 동작 확보
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //App Bar의 좌측 영영에 Drawer를 Open 하기 위한 Incon 추가
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        mToolbar.setTitle(R.string.app_title);
//        Log.d("menu icon", String.valueOf(mToolbar.getMenu().getItem(R.id.menu_item_add_memo).getTitle()));
    }


    private void initRecyclerView(){
        images = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvImageList.setLayoutManager(layoutManager);

        // adapter
        RecyclerView.Adapter adapter = new ImageAdapter(this, images);
        mRvImageList.setAdapter(adapter);
    }

    //데이터베이스에 새 메모를 삽입하고 RecyclerView 목록에 새로 삽입 된 메모를 추가합니다.
    private Memo createMemo(String title, String content){
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(title, content);

        // get the newly inserted note from db
        Memo memo = db.getNote(id);
        Log.d("Result Add Memo", memo.getTitle());
        return memo;
    }
    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote() {
        // updating note in db
        db.updateNote(mMemo);
        mActivityMode = DETAIL_MEMO_REQUEST;
        invalidateOptionsMenu();
        // refreshing the list
//        notesList.set(position, n);
//        mAdapter.notifyItemChanged(position);
//
//        toggleEmptyNotes();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        if(mActivityMode == EDIT_MEMO_REQUEST || mActivityMode == ADD_MEMO_REQUEST){
            inflater.inflate(R.menu.menu_toolbar_edit, menu);
        } else if (mActivityMode == DETAIL_MEMO_REQUEST){
            inflater.inflate(R.menu.menu_toolbar_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home :
                Log.d("TOOLBAR", "HOME CLICKED");
                finish();
//                onBackPressed();
                return true;
            case R.id.menu_item_detail_edit:
                Log.d("TOOLBAR", "Finish CLICKED");
                mActivityMode = EDIT_MEMO_REQUEST;
                invalidateOptionsMenu();

                mEtTitle.setEnabled(true);
                mEtContent.setEnabled(true);
                return true;
            case R.id.menu_item_finish_memo:
                Log.d("TOOLBAR", "Finish CLICKED");
                String title = mEtTitle.getText().toString();
                String content = mEtContent.getText().toString();
                if (mActivityMode == ADD_MEMO_REQUEST){
                    Memo memo = createMemo(title, content);
                    Intent intent = new Intent();
                    intent.putExtra("result", memo);
                    setResult(RESULT_OK, intent);
                    finish();
                }else if (mActivityMode == EDIT_MEMO_REQUEST){
//                    updateNote
                    mMemo.setTitle(mEtTitle.getText().toString());
                    mMemo.setContent(mEtContent.getText().toString());
//                    mMemo.set
                    updateNote();
                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    intent.putExtra("result", mMemo);
                    setResult(RESULT_OK, intent);
                    mEtTitle.setEnabled(false);
                    mEtContent.setEnabled(false);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
