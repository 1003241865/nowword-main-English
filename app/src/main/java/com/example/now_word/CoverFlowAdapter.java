package com.example.now_word;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import net.wujingchao.android.view.SimpleTagImageView;

import java.util.ArrayList;
/*
复习界面的适配器
 */
public class CoverFlowAdapter extends BaseAdapter {
	
	private ArrayList<ReviewCardEntity> mData = new ArrayList<>(0);
	private Context mContext;

	public CoverFlowAdapter(Context context) {
		mContext = context;
	}
	
	public void setData(ArrayList<ReviewCardEntity> data) {
		mData = data;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int pos) {
		return mData.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.review_card_item, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.cardView=(CardView)rowView.findViewById(R.id.review_card);
            viewHolder.text = (TextView) rowView.findViewById(R.id.review_type_title);
            viewHolder.image = (SimpleTagImageView) rowView
                    .findViewById(R.id.header_image);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        Glide.with(mContext).load(mData.get(position).imageResId).into(holder.image);//图片加载引擎，解决卡顿问题
        holder.text.setText(mData.get(position).titleResId);
        holder.image.setTagText("单词数："+String.valueOf(mData.get(position).num));

		return rowView;
	}


    static class ViewHolder {
        public TextView text;
        public SimpleTagImageView image;
        public CardView cardView;
    }
}
