package com.example.now_word;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.dao.WordDao;
import com.example.dao.WordTypeDao;
import com.example.model.Word;
import com.example.model.WordType;
import com.example.utils.AudioMediaPlayer;
import com.example.utils.Tran_CN_split;
import com.example.utils.TransApi;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PictureResultActivity extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener{
    private WordDao wordDao;
    private WordTypeDao wordTypeDao;

    private RecyclerTouchListener onTouchListener;//侧滑菜单
    private RecyclerView recyclerView;

    //悬浮按钮
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;
    private AlertDialog.Builder builder;//添加单词本对话框
    private View dialog;//对话框视图

    private List<Word> words0;//全局变量，记录单词结果列表
    private Word word0;//全局变量，记录单词结果列表
    private boolean[] checkedItems;//记录各个列表项的状态
    private String[] itemstype;     //记录列表项要选择的单词本

    public WordAdapter wordAdapter;
    private static String stringResult;
    private static String en_str="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_result);

        //初始化
        wordTypeDao=new WordTypeDao(this);

        //为添加所有单词设置监听
        rfaLayout=findViewById(R.id.rfal);
        rfaBtn=findViewById(R.id.rfab);
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("添加到新单词本")
                .setResId(R.drawable.icon_add)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("添加到已有单词本")
                .setResId(R.drawable.slide_header_img)
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
                this,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();



        //获取处理结果,返回单词列表
        wordDao=new WordDao(this);
        words0= (List<Word>) getIntent().getExtras().getSerializable("result");//全局变量，记录查询结果

        recyclerView=findViewById(R.id.word_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        wordAdapter=new WordAdapter(words0,this);
        recyclerView.setAdapter(wordAdapter);
        onTouchListener = new RecyclerTouchListener(this, recyclerView);
        onTouchListener.setIndependentViews(R.id.rowButton)
                .setViewsToFade(R.id.rowButton)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Word word=words0.get(position);
                        //Toast.makeText(PictureResultActivity.this,word.getHeadWord(),Toast.LENGTH_SHORT).show();
                        if(wordDao.find(word)!=null) {
                            Intent intent = new Intent(PictureResultActivity.this, WordDetailsActivity.class);
                            intent.putExtra("word", word.get_id());
                            startActivity(intent);
                        }else {
                            Toast.makeText(PictureResultActivity.this,"词库中暂时没有该词汇，请添加后再查看",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                        AudioMediaPlayer audioMediaPlayer=new AudioMediaPlayer(PictureResultActivity.this);
                        audioMediaPlayer.updateMP(words0.get(position).getHeadWord(),0);

                    }
                })
                .setSwipeOptionViews(R.id.add, R.id.edit, R.id.change)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, final int position) {
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
                            word0=words0.get(position);
                            //获取已有单词本
                            List<WordType> wordTypeList=wordTypeDao.getAllType();
                            itemstype=new String[wordTypeList.size()];
                            for (int i=0;i<wordTypeList.size();i++){
                                itemstype[i]=wordTypeList.get(i).getWordType();
                            }
                            //初始化记录各个列表项状态的数组
                            checkedItems=new boolean[wordTypeList.size()];
                            for (int i=0;i<wordTypeList.size();i++){
                                checkedItems[i]=false;
                            }
                            //显示带列表的对话框
                            AlertDialog.Builder builder=new AlertDialog.Builder(PictureResultActivity.this);
                            builder.setTitle("请选择想要保存的单词本:");
                            builder.setIcon(R.drawable.icon_app_launcher);
                            builder.setMultiChoiceItems(itemstype, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
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
                                            typeList.add(itemstype[i]);
                                        }
                                    }
                                    wordDao.SetNewWordBook(word0,typeList);
                                    Toast.makeText(PictureResultActivity.this,"添加单词成功",Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.show();

                        } else if (viewID == R.id.edit) { //编辑弹出框
                            headWord.setText(words0.get(position).getHeadWord());
                            tranCn.setText(words0.get(position).getTranCN());
                            tranEn.setText(words0.get(position).getTranEN());
                            sentences.setText(words0.get(position).getSentences());
                            phrases.setText(words0.get(position).getPhrases());
                            synos.setText(words0.get(position).getSyno());

                            builder = new AlertDialog.Builder(PictureResultActivity.this);
                            builder.setTitle("编辑单词");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Word word=new Word(headWord.getText().toString(),sentences.getText().toString(),"","",synos.getText().toString(),phrases.getText().toString(),
                                            tranCn.getText().toString(),tranEn.getText().toString(),null );
                                    wordDao=new WordDao(PictureResultActivity.this);
                                    words0.set(position,word);
                                    wordAdapter.notifyDataSetChanged();
                                }
                            });
                            builder.setView(dialog);
                            builder.setIcon(R.drawable.slide_about_icon);
                            builder.show();
                        }else if (viewID == R.id.change) {//删除对话框
                            builder = new AlertDialog.Builder(PictureResultActivity.this);
                            builder.setTitle("确认删除").setMessage("是否确认删除,删除后不可恢复！");
                            builder.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    words0.remove(position);
                                    wordAdapter.notifyDataSetChanged();
                                }
                            })
                                    .setNegativeButton("取消",null);
                            builder.setIcon(R.drawable.slide_about_icon);
                            builder.show();
                        }
                    }
                });
        recyclerView.addOnItemTouchListener(onTouchListener);

    }







    @Override
    public void onRFACItemLabelClick(int position, final RFACLabelItem item) {
        switch (position){
            case 0://添加到新建单词本
                //添加弹出窗
                LayoutInflater inflater = getLayoutInflater();
                dialog = inflater.inflate(R.layout.add_wordtype_xml, (ViewGroup)findViewById(R.id.add_dialog_view),false);
                final EditText editText = (EditText) dialog.findViewById(R.id.text_name);
                final EditText contextText = (EditText) dialog.findViewById(R.id.text_context);
                builder = new AlertDialog.Builder(this);
                builder.setTitle("添加到新建单词本");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String wordTypeStr=editText.getText().toString();
                        if(wordTypeStr==""){Toast.makeText(PictureResultActivity.this,"单词本名称不能空",Toast.LENGTH_SHORT).show();}
                        else {
                            WordType wordType=new WordType(wordTypeStr,contextText.getText().toString(),0);
                            WordTypeDao wordTypeDao=new WordTypeDao(PictureResultActivity.this);
                            if(wordTypeDao.find(wordType)==null){
                                wordTypeDao.add(wordType);
                            }else{
                                Toast.makeText(PictureResultActivity.this,"单词本已存在,添加到已有单词本",Toast.LENGTH_SHORT).show();
                            }
                            //添加到单词本
                           wordDao.SetNewWordBook(words0,wordTypeStr);
                            Toast.makeText(PictureResultActivity.this,"添加单词成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.drawable.slide_about_icon);
                builder.show();
                break;
            case 1://添加到已有单词本
                //获取已有单词本
                List<WordType> wordTypeList=wordTypeDao.getAllType();
                itemstype =new String[wordTypeList.size()];
                for (int i=0;i<wordTypeList.size();i++){
                    itemstype[i]=wordTypeList.get(i).getWordType();
                }
                //初始化记录各个列表项状态的数组
                checkedItems=new boolean[wordTypeList.size()];
                for (int i=0;i<wordTypeList.size();i++){
                    checkedItems[i]=false;
                }
                //显示带列表的对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(PictureResultActivity.this);
                builder.setTitle("请选择想要保存的单词本:");
                builder.setIcon(R.drawable.icon_app_launcher);
                builder.setMultiChoiceItems(itemstype, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
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
                                typeList.add(itemstype[i]);
                            }
                        }
                        wordDao.SetNewWordBook(words0,typeList);
                        Toast.makeText(PictureResultActivity.this,"添加单词成功",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

                break;

        }
        rfabHelper.toggleContent();

    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        switch (position){
            case 0://添加到新建单词本
                //添加弹出窗
                LayoutInflater inflater = getLayoutInflater();
                dialog = inflater.inflate(R.layout.add_wordtype_xml, (ViewGroup)findViewById(R.id.add_dialog_view),false);
                final EditText editText = (EditText) dialog.findViewById(R.id.text_name);
                final EditText contextText = (EditText) dialog.findViewById(R.id.text_context);
                builder = new AlertDialog.Builder(this);
                builder.setTitle("添加到新建单词本");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String wordTypeStr=editText.getText().toString();
                        if(wordTypeStr==""){Toast.makeText(PictureResultActivity.this,"单词本名称不能空",Toast.LENGTH_SHORT).show();}
                        else {
                            WordType wordType=new WordType(wordTypeStr,contextText.getText().toString(),0);
                            WordTypeDao wordTypeDao=new WordTypeDao(PictureResultActivity.this);
                            if(wordTypeDao.find(wordType)==null){
                                wordTypeDao.add(wordType);
                            }else{
                                Toast.makeText(PictureResultActivity.this,"单词本已存在,添加到已有单词本",Toast.LENGTH_SHORT).show();
                            }
                            //添加到单词本
                            wordDao.SetNewWordBook(words0,wordTypeStr);
                            Toast.makeText(PictureResultActivity.this,"添加单词成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.drawable.slide_about_icon);
                builder.show();
                break;
            case 1://添加到已有单词本
                //获取已有单词本
                List<WordType> wordTypeList=wordTypeDao.getAllType();
                itemstype =new String[wordTypeList.size()];
                for (int i=0;i<wordTypeList.size();i++){
                    itemstype[i]=wordTypeList.get(i).getWordType();
                }
                //初始化记录各个列表项状态的数组
                checkedItems=new boolean[wordTypeList.size()];
                for (int i=0;i<wordTypeList.size();i++){
                    checkedItems[i]=false;
                }
                //显示带列表的对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(PictureResultActivity.this);
                builder.setTitle("请选择想要保存的单词本:");
                builder.setIcon(R.drawable.icon_app_launcher);
                builder.setMultiChoiceItems(itemstype, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
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
                                typeList.add(itemstype[i]);
                            }
                        }
                        wordDao.SetNewWordBook(words0,typeList);
                        Toast.makeText(PictureResultActivity.this,"添加单词成功",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

                break;

        }
        rfabHelper.toggleContent();
    }

}