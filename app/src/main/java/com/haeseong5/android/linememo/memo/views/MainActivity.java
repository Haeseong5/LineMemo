package com.haeseong5.android.linememo.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.haeseong5.android.linememo.R;
import com.haeseong5.android.linememo.memo.database.DatabaseHelper;
import com.haeseong5.android.linememo.memo.database.models.Memo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_MEMO_REQUEST = 1001;
    public static final int EDIT_MEMO_REQUEST = 1002;
    public static final int DETAIL_MEMO_REQUEST = 1003;

    private ArrayList<Memo> mMemoList;
    private DatabaseHelper db;
    MemoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        mMemoList = new ArrayList<>();
        mMemoList.addAll(db.getAllNotes()); //Init memo data
        initView();
    }
    private void initView(){
        //Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.app_title);

        //Set RecyclerView
        RecyclerView recyclerView = findViewById(R.id.main_rv_memo_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MemoAdapter(this, mMemoList);
        recyclerView.setAdapter(mAdapter);

        //Move DetailActivity
        mAdapter.setOnItemClickListener(new MemoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("request_code", DETAIL_MEMO_REQUEST);
                intent.putExtra("position", position);
                intent.putExtra("memo", mMemoList.get(position));
                startActivityForResult(intent, DETAIL_MEMO_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_item_add_memo :
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("request_code", ADD_MEMO_REQUEST);
                startActivityForResult(intent, ADD_MEMO_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MEMO_REQUEST || requestCode == DETAIL_MEMO_REQUEST){
            if(resultCode == RESULT_OK){
                mMemoList.clear();
                mMemoList.addAll(db.getAllNotes());
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}//End Class

