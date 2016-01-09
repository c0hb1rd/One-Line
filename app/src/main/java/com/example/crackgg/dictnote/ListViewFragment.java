package com.example.crackgg.dictnote;

import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by crackgg on 1/8/16.
 */
public class ListViewFragment extends Fragment {

    final int SORT_INDEX = 0;
    final int SORT_NOTE = 1;
    final int SORT_INDEX_FIRST = 2;
    final int SORT_NOTE_FIRST = 3;
    final int SEARCH_INDEX = 4;
    final int SEARCH_NOTE = 5;
    final int SORT_DEFAULT = 6;

    View view;
    ListView listView;
    MyDatabase db;
    List<Map<String, Object>> listItems;
    LayoutInflater inflaterGlobal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frgment_main, container, false);

        //初始化控件
        initialization();
        displayListItems(SORT_DEFAULT, null);
        inflaterGlobal = inflater;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.detial_layout, null);
                dialog.setView(linearLayout);
                TextView showIndex = (TextView) linearLayout.findViewById(R.id.showIndex);
                TextView showNote = (TextView) linearLayout.findViewById(R.id.showNote);
                showIndex.setText(listItems.get(position).get("index").toString());
                showNote.setText(listItems.get(position).get("note").toString());
                dialog.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder updateDialog = new AlertDialog.Builder(getActivity());

                        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_layout, null);
                        final EditText world = (EditText) linearLayout.findViewById(R.id.edit_index);
                        final EditText mean = (EditText) linearLayout.findViewById(R.id.edit_note);
                        TextView textView = (TextView) linearLayout.findViewById(R.id.edit_text);
                        textView.setText("修改");
                        world.setText(listItems.get(position).get("index").toString());
                        mean.setText(listItems.get(position).get("note").toString());
                        updateDialog.setView(linearLayout);
                        updateDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                updateListItems(listItems.get(position).get("id").toString(), world.getText().toString(), mean.getText().toString());
                                displayListItems(SORT_DEFAULT, null);
                            }
                        });
                        updateDialog.setNegativeButton("取消", null);
                        updateDialog.create().show();
                    }
                });
                dialog.setPositiveButton("返回", null);
                dialog.create().show();

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                String [] items = {"修改", "删除", "取消"};
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                AlertDialog.Builder updateDialog = new AlertDialog.Builder(inflaterGlobal.getContext());
                                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_layout, null);
                                TextView textView = (TextView) linearLayout.findViewById(R.id.edit_text);
                                textView.setText("修改");
                                final EditText world = (EditText) linearLayout.findViewById(R.id.edit_index);
                                final EditText mean = (EditText) linearLayout.findViewById(R.id.edit_note);

                                world.setText(listItems.get(position).get("index").toString());
                                mean.setText(listItems.get(position).get("note").toString());
                                updateDialog.setView(linearLayout);
                                updateDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        updateListItems(listItems.get(position).get("id").toString(), world.getText().toString(), mean.getText().toString());
                                        displayListItems(SORT_DEFAULT, null);
                                    }
                                });
                                updateDialog.setNegativeButton("取消", null);
                                updateDialog.create().show();
                                break;
                            case 1:
                                AlertDialog.Builder delDialog = new AlertDialog.Builder(getActivity()).setTitle(listItems.get(position).get("index").toString()).setMessage("确定删除此条目吗？");
                                delDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        delListItem(listItems.get(position).get("id").toString());
                                        displayListItems(SORT_DEFAULT, null);
                                    }
                                });
                                delDialog.setNegativeButton("取消", null);
                                delDialog.create().show();
                                break;
                            case 2:
                                break;
                        }
                    }
                });
                dialog.create().show() ;
                return false;
            }
        });


        return view;
    }

    public void initialization() {
        listView = (ListView) view.findViewById(R.id.listView);
        listItems = new ArrayList<>();
        db = new MyDatabase(getActivity(), "itemDB.db3", null, 1);

        FloatingActionButton fab1 = (FloatingActionButton) getActivity().findViewById(R.id.fab1);
        FloatingActionButton fab2 = (FloatingActionButton) getActivity().findViewById(R.id.fab2);

        fab1.show();
        fab2.hide();

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                LinearLayout linearLayout = (LinearLayout) inflaterGlobal.inflate(R.layout.dialog_layout, null);
                final EditText index = (EditText) linearLayout.findViewById(R.id.edit_index);
                final EditText note = (EditText) linearLayout.findViewById(R.id.edit_note);

                dialog.setView(linearLayout);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!addListItems(getItemID(), index.getText().toString(), note.getText().toString()))
                            return;
                        displayListItems(SORT_DEFAULT, null);
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.create().show();
            }
        });
    }

    private void updateListItems(String itemID, String index, String note) {
        if (index.equals("") || index.equals(" ")) {
            Toast.makeText(getActivity(), "INDEX不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (note.equals("") || note.equals(" ")) {
            Toast.makeText(getActivity(), "批注不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        db.getReadableDatabase().execSQL("update form set v1=?, v2=? where id=?", new String [] {index, note, itemID});
    }

    private void delListItem(String id) {
        db.getReadableDatabase().execSQL("delete from form where id=?", new String [] {id});
    }

    public boolean addListItems(String itemID, String index, String note) {
        if (index.equals("") || index.equals(" ")) {
            Toast.makeText(getActivity(), "INDEX不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (note.equals("") || note.equals(" ")) {
            Toast.makeText(getActivity(), "批注不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        String [] data = {itemID, index, note, index.substring(0, 1), note.substring(0, 1)};
        db.getReadableDatabase().execSQL("insert into form values(?, ?, ?, ?, ?)", data);
        return true;
    }

    public String getItemID() {
        Calendar calendar = Calendar.getInstance();
        String year = calendar.get(Calendar.YEAR) + "";
        String month = calendar.get(Calendar.MONTH) + 1 + "";
        String day = calendar.get(Calendar.DAY_OF_MONTH) + "";
        String hour = calendar.get(Calendar.HOUR_OF_DAY) < 10? "0" + calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR_OF_DAY) + "";
        String minute = calendar.get(Calendar.MINUTE) < 10? "0" + calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE) + "";
        String second = calendar.get(Calendar.SECOND) < 10? "0" + calendar.get(Calendar.SECOND) : calendar.get(Calendar.SECOND) + "";
        return year + month + day + hour + minute + second;
    }



    public void displayListItems(int which, String character) {
        Cursor cursor;
        db = new MyDatabase(getActivity(), "itemDB.db3", null, 1);
        switch (which) {
            case 0:
                cursor = db.getReadableDatabase().rawQuery("select * from form order by v1", null); break;
            case 1:
                cursor = db.getReadableDatabase().rawQuery("select * from form order by v2", null); break;
            case 2:
                cursor = db.getReadableDatabase().rawQuery("select * from form where v1_first=? order by v1", new String [] {character}); break;
            case 3:
                cursor = db.getReadableDatabase().rawQuery("select * from form where v2_first=? order by v2", new String [] {character}); break;
            case 4:
                cursor = db.getReadableDatabase().rawQuery("select * from form where v1=?", new String [] {character}); break;
            case 5:
                cursor = db.getReadableDatabase().rawQuery("select * from form where v2=?", new String [] {character}); break;
            default:
                cursor = db.getReadableDatabase().rawQuery("select * from form order by id", null); break;
        }

        boolean i = true;
        listItems = new ArrayList<>();
        while ( cursor.moveToNext() ) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", cursor.getString(0));
            item.put("index", cursor.getString(1));
            item.put("note", cursor.getString(2));
            listItems.add(item);
            i = false;
        }
        cursor.close();

        if (i && which == SORT_DEFAULT) {
            String [] initIndex = {"nmap", "西瓜", "this is a test", "initialization", "西游记", "sqlmap", "metasploit", "XSS", "CSRF", "SSRF", "XSCH", "aircrack-ng", "apktool", "dexjar2", "0Day", "demo", "习近平", "自定义"};
            String [] initNote = {"知名网络嗅探工具", "外绿内红的圆形瓜果", "这是一个测试", "初始化", "中国四大名著之一", "SQL数据库注入工具", "漏洞利用exploit开发框架", "跨站脚本伪造", "跨站请求伪造", "服务端请求伪造", "跨站内容劫持", "WIFI破解工具集", "Android逆向工具", "Android逆向工具", "无补丁漏洞", "演示", "中共第xxx届军委主席", "请自行编辑需要的条目内容"};
            for (int j = 0; j < initIndex.length; j++) {
                addListItems(getItemID(), initIndex[j], initNote[j]);
            }
            displayListItems(SORT_DEFAULT, null);
            return;
        }
        String [] itemsKey = {"index", "note"};
        int [] itemLayout = {R.id.showWorld, R.id.showMean};
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), listItems, R.layout.list_item, itemsKey, itemLayout);
        listView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
