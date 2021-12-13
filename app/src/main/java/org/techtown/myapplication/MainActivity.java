package org.techtown.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static Context context_number;
    public String var;
    Dialog dialog;
    DrawerLayout drawerLayout;
    View drawerView;
    TextView routinnumber;
    Spinner spinner;
    private ArrayList<TodoItem> copyItems;
    private RecyclerView mrv_todo;
    private FloatingActionButton mwritebtn;
    private ArrayList<TodoItem> mTodoItems;
    private DBHelper mDBHelper;
    private CustomAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context_number = this;
        dialog = new Dialog(MainActivity.this);       // Dialog 초기화
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog.setContentView(R.layout.dialog);             // xml 레이아웃 파일과 연결
        spinner = (Spinner)findViewById(R.id.spinner);
        routinnumber = findViewById(R.id.routinnumber);
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        setInit();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                routinnumber.setText(parent.getItemAtPosition(position).toString());
                var = parent.getItemAtPosition(position).toString();
                mTodoItems.clear();
                copyItems = mDBHelper.getTodoList(routinnumber.getText().toString());
                mTodoItems.addAll(copyItems);
                mAdapter.notifyDataSetChanged();
                drawerLayout.closeDrawers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        }); // 드로어 리스너




        // 버튼: 커스텀 다이얼로그 띄우기
        findViewById(R.id.helpbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        //드로어 설정
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        ImageButton buttonOpenDrawer = (ImageButton) findViewById(R.id.opendrawer);
        buttonOpenDrawer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        Button buttonCloseDrawer = (Button) findViewById(R.id.closedrawer);
        buttonCloseDrawer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                drawerLayout.closeDrawers();
            }
        });

        drawerView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });

    }

    DrawerLayout.DrawerListener myDrawerListener = new DrawerLayout.DrawerListener() {

        public void onDrawerClosed(View drawerView) {
        }

        @Override
        public void onDrawerSlide(@NonNull @NotNull View drawerView, float slideOffset) {

        }

        public void onDrawerOpened(View drawerView) {
        }

        public void onDrawerStateChanged(int newState) {
            String state;
            switch (newState) {
                case DrawerLayout.STATE_IDLE:
                    state = "STATE_IDLE";
                    break;
                case DrawerLayout.STATE_DRAGGING:
                    state = "STATE_DRAGGING";
                    break;
                case DrawerLayout.STATE_SETTLING:
                    state = "STATE_SETTLING";
                    break;
                default:
                    state = "unknown!";
            }
        }
    };



    public void showDialog(){
        dialog.show(); // 다이얼로그 띄우기

        // 네 버튼
        dialog.findViewById(R.id.yesbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void setInit() {
        mDBHelper = new DBHelper(this);

        mrv_todo = findViewById(R.id.RecyclerView);

        mwritebtn = findViewById(R.id.addbtn);

        mTodoItems = new ArrayList<>();

        loadRecentDB();

        mwritebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialog.setContentView(R.layout.dialog_edit);
                EditText ed_title = dialog.findViewById(R.id.ed_title);
                EditText ed_timer = dialog.findViewById(R.id.ed_timer);
                TextView ed_number = dialog.findViewById(R.id.te_des_number);
                ed_number.setText(routinnumber.getText().toString());
                Button okbtn = dialog.findViewById(R.id.okbtn);

                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDBHelper.InsertTodo(ed_title.getText().toString(), ed_timer.getText().toString(), routinnumber.getText().toString() );

                        TodoItem item = new TodoItem();
                        item.setNumber(ed_number.getText().toString());
                        item.setTitle(ed_title.getText().toString());
                        item.setTimer(ed_timer.getText().toString());
                        mAdapter.addItem(item);

                        mrv_todo.smoothScrollToPosition(0);
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,"추가 완료",Toast.LENGTH_SHORT).show();

                    }
                });
                dialog.show();
            }
        });
    }

    private void loadRecentDB() {
        //저장 되어있던 DB를 가져온다
        mTodoItems = mDBHelper.getTodoList(routinnumber.getText().toString());
        if(mAdapter == null){
            mAdapter = new CustomAdapter(mTodoItems, this, new CustomAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });
            mrv_todo.setHasFixedSize(true);
            mrv_todo.setAdapter(mAdapter);
        }
    }
}