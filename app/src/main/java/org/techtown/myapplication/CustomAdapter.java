package org.techtown.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<TodoItem> mTodoItems;
    private Context mContext;
    private DBHelper mDBHelper;
    private Button Btn;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public CustomAdapter(ArrayList<TodoItem> mTodoItems, Context mContext, OnItemClickListener onItemClickListener) {
        this.mTodoItems = mTodoItems;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
        mDBHelper = new DBHelper(mContext);
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        holder.tv_title.setText(mTodoItems.get(position).getTitle());
        holder.tv_timer.setText(mTodoItems.get(position).getTimer());
        int count[] = new int[100];
        holder.onBind(mTodoItems.get(position));

        Handler handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                // 원래 하려던 동작 (UI변경 작업 등)
                holder.tv_set.setText(Integer.toString(count[position]));
                //MainActivity.adapter.notifyDataSetChanged();
            }
        };

        holder.getBtnTest().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (holder.tv_timer.getText().toString().isEmpty()==true) {
                    Snackbar.make(v, "타이머를 입력하세요.", Snackbar.LENGTH_LONG).show();
                } else {
                    int t = Integer.parseInt(holder.tv_timer.getText().toString());
                    onItemClickListener.onItemClick(v, position);


                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        int cnt = 0;

                        @Override
                        public void run() {

                            if (cnt++ < t) {
                                System.out.println("task…");
                            } else {
                                Intent intent= new Intent(v.getContext(), time_end.class);
                                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                v.getContext().startActivity(intent);
                                timer.cancel();
                                count[position]++;
                                Message msg = handler.obtainMessage();
                                handler.sendMessage(msg);

                            }
                        }
                    };
                    timer.schedule(timerTask, 0, 1000);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private TextView tv_timer;
        private TextView tv_set;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_timer = itemView.findViewById(R.id.tv_timer);
            tv_set = itemView.findViewById(R.id.tv_set);
            Btn = itemView.findViewById(R.id.startbtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String var2 = ((MainActivity)MainActivity.context_number).var;
                    int curPos = getAdapterPosition();
                    TodoItem todoItem = mTodoItems.get(curPos);
                    String[] strChoiceItems = {"수정하기", "삭제하기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("원하는 작업을 선택 해주세요");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if(position == 0){
                                //수정하기
                                Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.dialog_edit);
                                EditText ed_title = dialog.findViewById(R.id.ed_title);
                                EditText ed_timer = dialog.findViewById(R.id.ed_timer);
                                TextView ed_number = dialog.findViewById(R.id.te_des_number);
                                Button okbtn = dialog.findViewById(R.id.okbtn);

                                ed_title.setText(todoItem.getTitle());
                                ed_timer.setText(todoItem.getTimer());
                                ed_number.setText(var2);

                                String beforetitle = todoItem.getTitle();
                                String beforetimer = todoItem.getTimer();

                                ed_title.setSelection(ed_title.getText().length() - 1);

                                okbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String number = ed_number.getText().toString();
                                        String title = ed_title.getText().toString();
                                        String timer = ed_timer.getText().toString();

                                        mDBHelper.UpdateTodo(title, timer, beforetitle, beforetimer, number);

                                        todoItem.setTitle(title);
                                        todoItem.setTimer(timer);
                                        notifyItemChanged(curPos, todoItem);
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "수정완료", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                dialog.show();
                            }

                            else if(position==1){
                                //삭제하기
                                String title = todoItem.getTitle();
                                String timer = todoItem.getTimer();
                                String number = todoItem.getNumber();
                                mDBHelper.deleteTodo(title, timer, number);
                                mTodoItems.remove(curPos);
                                notifyItemRemoved(curPos);
                                Toast.makeText(mContext,"제거완료",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                }
            });
        }

        void onBind(TodoItem todoItem) {

        }





        public Button getBtnTest() {
            return Btn;
        }
    }
    public void addItem(TodoItem _item){
        mTodoItems.add(0,_item);
        notifyItemInserted(0);
    }
}



