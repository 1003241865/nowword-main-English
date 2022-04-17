package com.example.now_word;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.model.WordType;

import net.wujingchao.android.view.SimpleTagImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/*
单词本界面列表适配器
 */

public class WordBookAdapter extends RecyclerView.Adapter<WordBookAdapter.ViewHolder>{

    private ArrayList<WordBookEntity> mData = new ArrayList<>(0);
    private Context mContext;

    private OnItemListener onItemListener;//接口回调步骤2，定义成员变量

    WordBookAdapter(Context context,ArrayList<WordBookEntity> Data){
        this.mContext=context;
        this.mData=Data;

    }
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 引入自定义列表项的资源文件;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_book_item, parent, false);
        //创建ViewHolder，返回每一项的布局
        final ViewHolder viewHolder = new ViewHolder(v);
        //在onCreateViewHolder()中为每个item添加点击事件
        v.setOnClickListener(new RV_ItemListener());
        v.setOnLongClickListener(new RV_ItemListener());

        //原始方案，长按会出现连点问题
//        viewHolder.cardView.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                int position=viewHolder.getAdapterPosition();
//                String title;
//                System.out.println(position);
//                title=mData.get(position).title;
//                //Toast.makeText(v.getContext(),"你点击了"+word.getTranCN().toString(),Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(v.getContext(), WordListActivity.class);
//                intent.putExtra("title",title);
//                mContext.startActivity(intent);
//
//            }
//        });
//        viewHolder.cardView.setLongClickable(true);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //将数据和控件绑定
          holder.mtitle.setText(mData.get(position).title);
          // Glide.with(mContext).load(mData.get(position).imageResId).into(holder.imageView);//图片加载引擎
        //将数据保存在ItemView的ID中，以便点击时进行获取
        holder.itemView.setId(position);
        holder.imageView.setTagText(String.valueOf(mData.get(position).num));//左下角的标签

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleTagImageView imageView;
        TextView mtitle;
        CardView cardView;

        public ViewHolder(View itemView) {//内部类，绑定控件
            super(itemView);
            imageView =(SimpleTagImageView) itemView.findViewById(R.id.word_book_cover);//单词书封面
            mtitle = (TextView)itemView.findViewById(R.id.word_book_title);//单词书名称
            cardView=(CardView)itemView.findViewById(R.id.word_book);//单词书

        }
    }

    //接口回调步骤1，定义监听接口类
    public interface  OnItemListener{
        void OnItemClickListener(View view, int position);
        void OnItemLongClickListener(View view, int position);
    }
    //接口回调步骤3：实例化 暴露给外面的调用者，定义Listener的方法（）
    public void setOnItemListenerListener(OnItemListener listener){
        this.onItemListener = listener;
    }

    //接口回调步骤4：实现接口回调方法 将点击事件转移给外面的调用者
    class RV_ItemListener implements View.OnClickListener, View.OnLongClickListener{

        @Override
        public void onClick(View view) {
            if (onItemListener != null){
                onItemListener.OnItemClickListener(view, view.getId());
            }
        }
        @Override
        public boolean onLongClick(View view) {
            if (onItemListener != null){
                onItemListener.OnItemLongClickListener(view,view.getId());
            }
            return true;
        }
    }
}
