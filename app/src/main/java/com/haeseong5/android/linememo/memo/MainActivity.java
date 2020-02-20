package com.haeseong5.android.linememo.memo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.haeseong5.android.linememo.R;
import com.haeseong5.android.linememo.memo.database.DatabaseHelper;
import com.haeseong5.android.linememo.memo.database.model.Memo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_MEMO_REQUEST = 1001;
    public static final int EDIT_MEMO_REQUEST = 1002;
    public static final int DETAIL_MEMO_REQUEST = 1003;

    private RecyclerView recyclerView;
    private ArrayList<Memo> memoList;
    private Toolbar mToolbar;
    private DatabaseHelper db;
    MemoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        memoList = new ArrayList<>();
        memoList.addAll(db.getAllNotes());
        initView();
        Log.d("memo list size: ", String.valueOf(memoList.size()));

    }
    private void initView(){
        recyclerView = findViewById(R.id.main_recyclerview);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        //App Bar의 좌측 영영에 Drawer를 Open 하기 위한 Incon 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.app_title);

        // layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        // adapter
        mAdapter = new MemoAdapter(memoList);
        recyclerView.setAdapter(mAdapter);

        //상세보기 화면으로 이동.
        mAdapter.setOnItemClickListener(new MemoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // TODO : 아이템 클릭 이벤트를 MainActivity에서 처리.
//                printToast(String.valueOf(position));
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                intent.putExtra("request_code", DETAIL_MEMO_REQUEST);
                intent.putExtra("position", position);
                intent.putExtra("memo", memoList.get(position));
                startActivityForResult(intent, DETAIL_MEMO_REQUEST);
            }
        }) ;

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
            //글쓰기 화면으로 이동.
            case R.id.menu_item_add_memo :
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                intent.putExtra("request_code", ADD_MEMO_REQUEST);
                startActivityForResult(intent, ADD_MEMO_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ADD_MEMO_REQUEST:
                if(resultCode == RESULT_OK){
                    Memo memo = (Memo)data.getSerializableExtra("result");
                    if (memo != null) {
                        // adding new note to array list at 0 position
//                        memoList.add(0, memo);
                        memoList.add(0, memo);
                        // refreshing the list
                        mAdapter.notifyDataSetChanged();
                        Log.d("add memo result", memo.getTitle());
                    }
                }
                break;
            case DETAIL_MEMO_REQUEST:
                if(resultCode == RESULT_OK){

                    Memo memo = (Memo)data.getSerializableExtra("result");
                    int position = data.getIntExtra("position",-1);
                    Log.d("detail", String.valueOf(position));
                    if (memo != null) {
                        // adding new note to array list at 0 position
//                        memoList.add(0, memo);
                        memoList.set(position, memo);
                        // refreshing the list
                        mAdapter.notifyDataSetChanged();
                        Log.d("DETAIL_MEMO_RESULT", memo.getTitle());
                    }else{
                        Log.d("DETAIL_MEMO_RESULT", "memo null");

                    }
                    mAdapter.notifyDataSetChanged();

                }
                break;
        }
    }
}//End Class

/**
 * 기능1: 메모리스트
 * 로컬 영역에 저장된 메모를 읽어 리스트 형태로 화면에 표시합니다.
 * 리스트에는 메모에 첨부되어있는 이미지의 썸네일, 제목, 글의 일부가 보여집니다. (이미지가 n개일 경우, 첫 번째 이미지가 썸네일이 되어야 함)
 * 리스트의 메모를 선택하면 메모 상세 보기 화면으로 이동합니다.
 * 새 메모 작성하기 기능을 통해 메모 작성 화면으로 이동할 수 있습니다.
 *z
 * 기능2: 메모 상세 보기
 * 작성된 메모의 제목과 본문을 볼 수 있습니다.
 * 메모에 첨부되어있는 이미지를 볼 수 있습니다. (이미지는 n개 존재 가능)
 * 메뉴를 통해 메모 내용 편집 또는 삭제가 가능합니다.
 *
 * 기능3: 메모 편집 및 작성
 * 제목 입력란과 본문 입력란, 이미지 첨부란이 구분되어 있어야 합니다. (글 중간에 이미지가 들어갈 수 있는 것이 아닌, 첨부된 이미지가 노출되는 부분이 따로 존재)
 * 이미지 첨부란의 ‘추가' 버튼을 통해 이미지 첨부가 가능합니다. 첨부할 이미지는 다음 중 한 가지 방법을 선택해서 추가할 수 있습니다. 이미지는 0개 이상 첨부할 수 있습니다. 외부 이미지의 경우, 이미지를 가져올 수 없는 경우(URL이 잘못되었거나)에 대한 처리도 필요합니다.
 *
 * 사진첩에 저장되어 있는 이미지
 * 카메라로 새로 촬영한 이미지
 * 외부 이미지 주소(URL) (참고: URL로 이미지를 추가하는 경우, 다운로드하여 첨부할 필요는 없습니다.)
 * 편집 시에는 기존에 첨부된 이미지가 나타나며, 이미지를 더 추가하거나 기존 이미지를 삭제할 수 있습니다.
 */

/**
 * ArrayList: 데이터의 검색에 유리하며 추가, 삭제에는 성능을 고려해야 한다.
 * LinkedList : ArrayList 에 비해 데이터의 추가, 삭제에 유리하며 데이터 검색 시에는 성능을 고려해야 한다.
 */
