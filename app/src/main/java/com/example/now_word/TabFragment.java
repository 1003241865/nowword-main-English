package com.example.now_word;
/*
   这里存放单词列表的碎片类
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import com.example.dao.WordRecordDao;
import com.example.dao.WordDao;
import com.example.dao.WordTypeDao;
import com.example.model.Word;
import com.example.model.WordType;
import com.example.utils.AudioMediaPlayer;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

public class TabFragment extends Fragment implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener{
    private List<Word> wordList=new ArrayList<Word>();//单词列表数据
    private View view;
    private String mTitle;//单词类型
    private String mtype;//单词书
    private RecyclerTouchListener onTouchListener;//侧滑菜单
    private AlertDialog.Builder builder;//添加单词本对话框
    private Word word0;//全局变量，记录单词结果列表
    private boolean[] checkedItems;//记录各个列表项的状态
    private String[] items;     //记录列表项要选择的单词本
    private View dialog;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;
    private WordAdapter wordAdapter;
    private WordDao wordDao;
    private WordTypeDao wordTypeDao;

    //这个构造方法是便于各导航同时调用一个fragment
    public TabFragment(String title,String type){
        mTitle=title;
        mtype=type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view=inflater.inflate(R.layout.tab_fragment,container,false);
        wordDao=new WordDao(getContext());
        wordTypeDao=new WordTypeDao(getContext());
        RecyclerView recyclerView=view.findViewById(R.id.word_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new WordAdapter(getData(),getContext()));
        onTouchListener = new RecyclerTouchListener(getActivity(), recyclerView);
        onTouchListener.setIndependentViews(R.id.rowButton)
                .setViewsToFade(R.id.rowButton)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Word word=getData().get(position);

                        Intent intent = new Intent(getContext(), WordDetailsActivity.class);
                        intent.putExtra("word",word.get_id());
                        startActivity(intent);

                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                        AudioMediaPlayer audioMediaPlayer=new AudioMediaPlayer(getContext());
                        audioMediaPlayer.updateMP(getData().get(position).getHeadWord(),0);

                    }
                })
                .setSwipeOptionViews(R.id.add, R.id.edit, R.id.change)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
            public void onSwipeOptionClicked(int viewID, int position) {
            final int id=wordList.get(position).get_id();
            final int wordRank=wordList.get(position).getWordRank();
            final String wordType=wordList.get(position).getWordType();
            LayoutInflater inflater = getLayoutInflater();
            dialog = inflater.inflate(R.layout.add_word, (ViewGroup) view.findViewById(R.id.add_dialog_view),false);
            final EditText headWord = (EditText) dialog.findViewById(R.id.headWord);
            final EditText tranCn = (EditText) dialog.findViewById(R.id.tranCN);
            final EditText tranEn = (EditText) dialog.findViewById(R.id.tranEn);
            final EditText sentences = (EditText) dialog.findViewById(R.id.sentences);
            final EditText phrases= (EditText) dialog.findViewById(R.id.phrases);
            final EditText synos = (EditText) dialog.findViewById(R.id.synos);
            //添加单词弹出框
            if (viewID == R.id.add) {
                word0=wordDao.find(id);
                //获取已有单词本
                List<WordType> wordTypeList=wordTypeDao.getAllType();
                items=new String[wordTypeList.size()];
                for (int i=0;i<wordTypeList.size();i++){
                    items[i]=wordTypeList.get(i).getWordType();
                }
                //初始化记录各个列表项状态的数组
                checkedItems=new boolean[wordTypeList.size()];
                for (int i=0;i<wordTypeList.size();i++){
                    checkedItems[i]=false;
                }
                //显示带列表的对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("请选择想要保存的单词本:");
                builder.setIcon(R.drawable.icon_app_launcher);
                builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which]=isChecked;//改变被操作列表项的状态

                    }
                });
                //为对话框添加确定按钮和监听器
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获取要存入的单词表
                        List<String> typeList= new ArrayList<>();
                        for (int i=0;i<checkedItems.length;i++){
                            if(checkedItems[i]){
                                typeList.add(items[i]);
                            }
                        }
                        wordDao.SetNewWordBook(word0,typeList);
                        Toast.makeText(getActivity(),"添加单词成功",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

            }
            //编辑弹出框
            else if (viewID == R.id.edit) {

                headWord.setText(wordList.get(position).getHeadWord());
                tranCn.setText(wordList.get(position).getTranCN());
                tranEn.setText(wordList.get(position).getTranEN());
                sentences.setText(wordList.get(position).getSentences());
                phrases.setText(wordList.get(position).getPhrases());
                synos.setText(wordList.get(position).getSyno());

                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("编辑单词");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Word word=new Word(id,wordRank,headWord.getText().toString(),sentences.getText().toString(),"","",synos.getText().toString(),phrases.getText().toString(),
                                tranCn.getText().toString(),tranEn.getText().toString(),wordType);
                        wordDao=new WordDao(getContext());
                        wordDao.update(word);
                        wordList.clear();
                        wordList.addAll(getData());
                        wordAdapter.notifyDataSetChanged();
                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.drawable.slide_about_icon);
                builder.show();
            }
            //删除弹出框
            else if (viewID == R.id.change) {
                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("确认删除").setMessage("是否确认删除,删除后不可恢复！");
                builder.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wordDao=new WordDao(getContext());
                        Word word=wordDao.find(id);
                        Log.d("haha", "onClick: "+word.get_id());
                        wordDao.delete(word);
                        wordList.clear();
                        wordList.addAll(getData());
                        wordAdapter.notifyDataSetChanged();


                    }
                })
                        .setNegativeButton("取消",null);
                builder.setIcon(R.drawable.slide_about_icon);
                builder.show();
            }
        }
    });
        rfaLayout=view.findViewById(R.id.rfal);
        rfaBtn=view.findViewById(R.id.rfab);
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getContext());
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("添加单词")
                .setResId(R.drawable.icon_add)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    //获取不同类型单词数据
    private List<Word> getData() {
        List<Word> data = null;
        WordRecordDao wordRecordDao =new WordRecordDao(getContext());
        WordDao wordDao=new WordDao(getContext());
        switch(mTitle) {
            case "已学单词":
                data= wordRecordDao.getLearnedWords(mtype);
                break;
            case "标记单词":
                data= wordRecordDao.getTypeFlagWords(mtype);
                break;
            case "易错单词":
                data= wordRecordDao.getHighWrongWords(mtype,1);
                break;
            case "完成单词":
                data= wordRecordDao.getTypeFinishWords(mtype);
                break;
            case "全部单词":
                data=wordDao.getTypeWords(mtype);
                break;

        }

        return data;
    }

    @Override
    public void onResume() {

        RecyclerView recyclerView=view.findViewById(R.id.word_list);
        recyclerView.addOnItemTouchListener(onTouchListener);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        wordList=getData();
        wordAdapter=new WordAdapter(wordList,getContext());
        recyclerView.setAdapter(wordAdapter);
        super.onResume();
    }
    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        switch (position){
            case 0:
                LayoutInflater inflater = getLayoutInflater();
                dialog = inflater.inflate(R.layout.add_word, (ViewGroup) view.findViewById(R.id.add_dialog_view),false);
                final EditText headWord = (EditText) dialog.findViewById(R.id.headWord);
                final EditText tranCn = (EditText) dialog.findViewById(R.id.tranCN);
                final EditText tranEn = (EditText) dialog.findViewById(R.id.tranEn);
                final EditText sentences = (EditText) dialog.findViewById(R.id.sentences);
                final EditText phrases= (EditText) dialog.findViewById(R.id.phrases);
                final EditText synos = (EditText) dialog.findViewById(R.id.synos);
                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("添加单词到单词本");
                builder.setPositiveButton("确定添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Log.i("hahaha",headWord.getText().toString());
//                        Log.i("hahaha","是否为空"+(headWord.getText().toString().isEmpty()));//不知道为什么这里用==""判断不行
                        if((!headWord.getText().toString().trim().isEmpty())&&(!tranCn.getText().toString().trim().isEmpty())){//判断单词和中文意思是否为空
                            Word word=new Word(headWord.getText().toString(),sentences.getText().toString(),"","",synos.getText().toString(),phrases.getText().toString(),
                                    tranCn.getText().toString(),tranEn.getText().toString(),mtype);
                            WordDao wordDao=new WordDao(getContext());
                            wordDao.add(word);
                            wordList.clear();
                            wordList.addAll(getData());
                            wordAdapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getContext(),"单词名称或中文意思不能为空！",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.drawable.slide_about_icon);
                builder.show();
                break;

        }
        rfabHelper.toggleContent();

    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        switch (position){
            case 0:
                LayoutInflater inflater = getLayoutInflater();
                dialog = inflater.inflate(R.layout.add_word, (ViewGroup) view.findViewById(R.id.add_dialog_view),false);
                final EditText headWord = (EditText) dialog.findViewById(R.id.headWord);
                final EditText tranCn = (EditText) dialog.findViewById(R.id.tranCN);
                final EditText tranEn = (EditText) dialog.findViewById(R.id.tranEn);
                final EditText sentences = (EditText) dialog.findViewById(R.id.sentences);
                final EditText phrases= (EditText) dialog.findViewById(R.id.phrases);
                final EditText synos = (EditText) dialog.findViewById(R.id.synos);
                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("添加单词到单词本");
                builder.setPositiveButton("确定添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Log.i("hahaha",headWord.getText().toString());
//                        Log.i("hahaha","是否为空"+(headWord.getText().toString().isEmpty()));//不知道为什么这里用==""判断不行
                        if((!headWord.getText().toString().trim().isEmpty())&&(!tranCn.getText().toString().trim().isEmpty())){//判断单词和中文意思是否为空
                            Word word=new Word(headWord.getText().toString(),sentences.getText().toString(),"","",synos.getText().toString(),phrases.getText().toString(),
                                    tranCn.getText().toString(),tranEn.getText().toString(),mtype);
                            WordDao wordDao=new WordDao(getContext());
                            wordDao.add(word);
                            wordList.clear();
                            wordList.addAll(getData());
                            wordAdapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getContext(),"单词名称或中文意思不能为空！",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.drawable.slide_about_icon);
                builder.show();
                break;

        }
        rfabHelper.toggleContent();
    }
}
