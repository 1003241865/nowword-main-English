package com.example.now_word;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dao.WordDao;
import com.example.dao.WordTypeDao;
import com.example.model.Word;
import com.example.model.WordType;
import com.example.utils.AudioMediaPlayer;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchWordActivity extends AppCompatActivity implements TextWatcher {
    private WordDao wordDao;
    private EditText serchInput;
    private List<Word> wordList;
    private RecyclerView recyclerView;
    private String input;
    public WordAdapter wordAdapter;
    public List<Word> wordresult;
    private RecyclerTouchListener onTouchListener;//侧滑菜单
    private AlertDialog.Builder builder;//添加单词本对话框
    private Word word0;//全局变量，记录单词结果列表
    private boolean[] checkedItems;//记录各个列表项的状态
    private String[] items;     //记录列表项要选择的单词本
    View dialog;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;
    private WordTypeDao wordTypeDao;
    private String type ;//搜索的单词类型
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_word);
        init();
    }

    public void init(){
        type=getIntent().getStringExtra("type");//获取页面传过来的要搜索的单词类型
        wordDao=new WordDao(this);
        wordTypeDao=new WordTypeDao(this);
        input="a";
        serchInput=findViewById(R.id.serch_input);
        serchInput.setHint("从 "+type+" 搜索：");
        RecyclerView recyclerView=findViewById(R.id.word_list_search);
        wordresult=getData();
        wordAdapter=new WordAdapter(wordresult,this);
        onTouchListener = new RecyclerTouchListener(this, recyclerView);
        onTouchListener.setIndependentViews(R.id.rowButton)
                .setViewsToFade(R.id.rowButton)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Word word=getData().get(position);

                        Intent intent = new Intent(SearchWordActivity.this, WordDetailsActivity.class);
                        intent.putExtra("word",word.get_id());
                        startActivity(intent);

                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                        AudioMediaPlayer audioMediaPlayer=new AudioMediaPlayer(SearchWordActivity.this);
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
                        dialog = inflater.inflate(R.layout.add_word, (ViewGroup) findViewById(R.id.add_dialog_view),false);
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
                            AlertDialog.Builder builder=new AlertDialog.Builder(SearchWordActivity.this);
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
                                    Toast.makeText(SearchWordActivity.this,"添加单词成功",Toast.LENGTH_SHORT).show();
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

                            builder = new AlertDialog.Builder(SearchWordActivity.this);
                            builder.setTitle("编辑单词");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Word word=new Word(id,wordRank,headWord.getText().toString(),sentences.getText().toString(),"","",synos.getText().toString(),phrases.getText().toString(),
                                            tranCn.getText().toString(),tranEn.getText().toString(),wordType);
                                    wordDao=new WordDao(SearchWordActivity.this);
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
                            builder = new AlertDialog.Builder(SearchWordActivity.this);
                            builder.setTitle("确认删除").setMessage("是否确认删除,删除后不可恢复！");
                            builder.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    wordDao=new WordDao(SearchWordActivity.this);
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
        recyclerView.addOnItemTouchListener(onTouchListener);//添加监听器
        recyclerView.setAdapter(wordAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        serchInput.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        input=serchInput.getText().toString().trim();
        //System.out.println(input);
        wordresult.clear();
        wordresult.addAll(getData());
        wordAdapter.notifyDataSetChanged();//更新结果数据
    }

    private List<Word> getData() {
        wordList=wordDao.getSerchWords(input,type);
        System.out.println(wordList.size());
        return wordList;
        }
}