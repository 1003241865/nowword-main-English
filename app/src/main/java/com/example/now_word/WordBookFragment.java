package com.example.now_word;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dao.SettingDao;
import com.example.dao.WordDao;
import com.example.dao.WordTypeDao;
import com.example.model.Word;
import com.example.model.WordType;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//单词本页面
public class WordBookFragment extends Fragment implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener{
    private RecyclerView recyclerView;
    private WordTypeDao wordTypeDao;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;
    private WordBookAdapter wordBookAdapter;
    private RecyclerTouchListener onTouchListener;//侧滑菜单
    private ArrayList<WordBookEntity> bookList;
    private AlertDialog.Builder builder;//添加单词本对话框
    private View dialog,view;
    private SettingDao settingDao;
    private WordDao wordDao;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view=inflater.inflate(R.layout.word_book_fragment,container,false);

        wordTypeDao=new WordTypeDao(getContext());
        settingDao=new SettingDao(getContext());
        wordDao=new WordDao(getContext());

        recyclerView=view.findViewById(R.id.word_book);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);//设置为3列
        recyclerView.setLayoutManager(layoutManager);
        bookList=this.getData();



        wordBookAdapter=new WordBookAdapter(getContext(),bookList);
        wordBookAdapter.setOnItemListenerListener(new WordBookAdapter.OnItemListener() {
            @Override
            public void OnItemClickListener(View view, int position) {
                String title;
                title=bookList.get(position).title;
                //Toast.makeText(v.getContext(),"你点击了"+word.getTranCN().toString(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), WordListActivity.class);
                intent.putExtra("title",title);
                getContext().startActivity(intent);
            }

            @Override
            public void OnItemLongClickListener(View view, final int position) {
                Vibrator vibrator = (Vibrator)getContext().getSystemService(getContext().VIBRATOR_SERVICE);//震动功能
                vibrator.vibrate(150);//震动时间
                LayoutInflater inflater = getLayoutInflater();
                dialog = inflater.inflate(R.layout.wordbook_edit_dialog, (ViewGroup) view.findViewById(R.id.dialog),false);
                final EditText editText = (EditText) dialog.findViewById(R.id.text_name);
                final EditText contextText = (EditText) dialog.findViewById(R.id.text_context);
                final ImageView delete=dialog.findViewById(R.id.delete);
                WordType wordType=wordTypeDao.find(bookList.get(position).title);
                editText.setText(wordType.getWordType());
                contextText.setText(wordType.getContext());

                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("编辑单词本:"+bookList.get(position).title);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editText.getText().toString()!=""){
                            WordType wordType=new WordType(editText.getText().toString(),contextText.getText().toString(),0);
                            WordTypeDao wordTypeDao=new WordTypeDao(getContext());
                            wordTypeDao.update(wordType);
                            Toast.makeText(getContext(),"编辑成功",Toast.LENGTH_SHORT).show();
                            bookList.clear();
                            bookList.addAll(getData());
                            wordBookAdapter.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(getContext(),"单词本名称不能为空",Toast.LENGTH_SHORT);
                        }
                    }
                }).setNegativeButton("取消",null);
                builder.setView(dialog);
                builder.setIcon(R.drawable.icon_edit);
                final AlertDialog dialog0 = builder.show();

                delete.setOnClickListener(new View.OnClickListener() {
                    //删除弹出框
                    @Override
                    public void onClick(View v) {
                        builder = new AlertDialog.Builder(getContext());
                        if (position>=bookList.size()){
                            Toast.makeText(getContext(),"该单词本已被删除！",Toast.LENGTH_SHORT).show();
                        }else {
                            builder.setTitle("确认删除"+bookList.get(position).title).setMessage("是否确认删除,删除后不可恢复！");
                            builder.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    wordTypeDao=new WordTypeDao(getContext());
                                    WordType wordType=wordTypeDao.find(bookList.get(position).title);
                                    Log.d("hahaha", "onClick: "+wordType.getWordType());
                                    if(wordType.getIsSystem()==0){
                                        wordTypeDao.delete(wordType);
                                        bookList.clear();
                                        bookList.addAll(getData());
                                        wordBookAdapter.notifyDataSetChanged();
                                        Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT).show();
                                        //关闭对话框
                                        dialog0.dismiss();
                                    } else{
                                        Toast.makeText(getContext(),"系统单词本无法删除",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                                    .setNegativeButton("取消",null);
                            builder.setIcon(R.drawable.slide_about_icon);
                            builder.show();
                        }

                    }
                });

            }


        });
        recyclerView.setAdapter(wordBookAdapter);
        onTouchListener = new RecyclerTouchListener(getActivity(), recyclerView);


        //原始方案，长按会出现连点问题
        //长按弹出编辑框
//        onTouchListener.setLongClickable(true, new RecyclerTouchListener.OnRowLongClickListener() {//设置震动
//                    @Override
//                    public void onRowLongClicked(final int position) {
//                        LayoutInflater inflater = getLayoutInflater();
//                        dialog = inflater.inflate(R.layout.wordbook_edit_dialog, (ViewGroup) view.findViewById(R.id.dialog),false);
//                        final EditText editText = (EditText) dialog.findViewById(R.id.text_name);
//                        final EditText contextText = (EditText) dialog.findViewById(R.id.text_context);
//                        final ImageView delete=dialog.findViewById(R.id.delete);
//                        WordType wordType=wordTypeDao.find(bookList.get(position).title);
//                        editText.setText(wordType.getWordType());
//                        contextText.setText(wordType.getContext());
//                        delete.setOnClickListener(new View.OnClickListener() {
//                            //删除弹出框
//
//                            @Override
//                            public void onClick(View v) {
//                                builder = new AlertDialog.Builder(getContext());
//                                if (position>=bookList.size()){
//                                    Toast.makeText(getContext(),"该单词本已被删除！",Toast.LENGTH_SHORT).show();
//                                }else {
//                                    builder.setTitle("确认删除"+bookList.get(position).title).setMessage("是否确认删除,删除后不可恢复！");
//                                    builder.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            wordTypeDao=new WordTypeDao(getContext());
//                                            WordType wordType=wordTypeDao.find(bookList.get(position).title);
//                                            Log.d("hahaha", "onClick: "+wordType.getWordType());
//                                            if(wordType.getIsSystem()==0){
//                                                wordTypeDao.delete(wordType);
//                                                bookList.clear();
//                                                bookList.addAll(getData());
//                                                wordBookAdapter.notifyDataSetChanged();
//                                                Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT).show();
//                                            } else{
//                                                Toast.makeText(getContext(),"系统单词本无法删除",Toast.LENGTH_SHORT).show();
//                                            }
//
//                                        }
//                                    })
//                                            .setNegativeButton("取消",null);
//                                    builder.setIcon(R.drawable.slide_about_icon);
//                                    builder.show();
//                                }
//
//                            }
//                        });
//                        builder = new AlertDialog.Builder(getContext());
//                        builder.setTitle("编辑单词本:"+bookList.get(position).title);
//                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if(editText.getText().toString()!=""){
//                                    WordType wordType=new WordType(editText.getText().toString(),contextText.getText().toString(),0);
//                                    WordTypeDao wordTypeDao=new WordTypeDao(getContext());
//                                    wordTypeDao.update(wordType);
//                                    Toast.makeText(getContext(),"编辑成功",Toast.LENGTH_SHORT).show();
//                                    bookList.clear();
//                                    bookList.addAll(getData());
//                                    wordBookAdapter.notifyDataSetChanged();
//                                }
//                                else{
//                                    Toast.makeText(getContext(),"单词本名称不能为空",Toast.LENGTH_SHORT);
//                                }
//                            }
//                        }).setNegativeButton("取消",null);
//                        builder.setView(dialog);
//                        builder.setIcon(R.drawable.icon_edit);
//                        builder.show();
//                    }
//                });

        //右下角的按钮
        rfaLayout=view.findViewById(R.id.rfal);
        rfaBtn=view.findViewById(R.id.rfab);
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getContext());
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("添加单词本")
                .setResId(R.drawable.icon_add)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("拍照取词")
                .setResId(R.drawable.icon_photo)
                .setIconNormalColor(0xff4e342e)
                .setIconPressedColor(0xff3e2723)
                .setLabelColor(Color.BLACK)
                .setLabelSizeSp(14)
                .setWrapper(1)
        );
        rfaContent
                .setItems(items)
                .setIconShadowColor(0xff888888)
        ;
        rfabHelper = new RapidFloatingActionHelper(
                getContext(),
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    //更新单词本
    @Override
    public void onResume() {
        bookList.clear();
        bookList.addAll(getData());
        wordBookAdapter.notifyDataSetChanged();
        recyclerView.addOnItemTouchListener(onTouchListener);
        super.onResume();
    }

    public ArrayList<WordBookEntity> getData() {
        ArrayList<WordBookEntity> mData=new ArrayList<>(0);
        WordTypeDao wordTypeDao=new WordTypeDao(getContext());
        List<WordType> title=wordTypeDao.getAllType();
        Iterator<WordType> iterator=title.iterator();
        while (iterator.hasNext()){
            String wordType=iterator.next().getWordType();
            mData.add(new WordBookEntity(R.drawable.review_dog,wordType,wordDao.getTypeCount(wordType)));
        }
        return mData;

    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        switch (position){
            case 0:
                LayoutInflater inflater = getLayoutInflater();
                dialog = inflater.inflate(R.layout.add_wordtype_xml, (ViewGroup) view.findViewById(R.id.add_dialog_view),false);
                final EditText editText = (EditText) dialog.findViewById(R.id.text_name);
                final EditText contextText = (EditText) dialog.findViewById(R.id.text_context);
                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("添加单词本");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editText.getText().toString().trim()!=""&&!editText.getText().toString().trim().isEmpty()){
                            WordType wordType=new WordType(editText.getText().toString(),contextText.getText().toString(),0);
                            WordTypeDao wordTypeDao=new WordTypeDao(getContext());
                            if(wordTypeDao.find(wordType)==null){
                                wordTypeDao.add(wordType);
                            }else{
                                Toast.makeText(getContext(),"单词本已存在",Toast.LENGTH_SHORT).show();
                            }
                            bookList.clear();
                            bookList.addAll(getData());
                            wordBookAdapter.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(getContext(),"单词本名称不能为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.drawable.slide_about_icon);
                builder.show();
                break;
            case 1:
                Intent intent=new Intent(getActivity(),TakePictureActivity.class);
                startActivity(intent);
                break;

        }
        rfabHelper.toggleContent();

    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        switch (position){
            case 0:
                LayoutInflater inflater = getLayoutInflater();
                dialog = inflater.inflate(R.layout.add_wordtype_xml, (ViewGroup) view.findViewById(R.id.add_dialog_view),false);
                final EditText editText = (EditText) dialog.findViewById(R.id.text_name);
                final EditText contextText = (EditText) dialog.findViewById(R.id.text_context);
                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("添加单词本");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editText.getText().toString().trim()!=""&&!editText.getText().toString().trim().isEmpty()){
                            WordType wordType=new WordType(editText.getText().toString(),contextText.getText().toString(),0);
                            WordTypeDao wordTypeDao=new WordTypeDao(getContext());
                            if(wordTypeDao.find(wordType)==null){
                                wordTypeDao.add(wordType);
                            }else{
                                Toast.makeText(getContext(),"单词本已存在",Toast.LENGTH_SHORT).show();
                            }
                            bookList.clear();
                            bookList.addAll(getData());
                            wordBookAdapter.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(getContext(),"单词本名称不能为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.drawable.slide_about_icon);
                builder.show();
                break;
            case 1:
                Intent intent=new Intent(getActivity(),TakePictureActivity.class);
                startActivity(intent);
                break;

        }
        rfabHelper.toggleContent();
    }
}
