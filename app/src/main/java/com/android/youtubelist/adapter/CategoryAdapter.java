package com.android.youtubelist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.youtubelist.R;
import com.android.youtubelist.model.Category;
import com.android.youtubelist.model.Video;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class CategoryAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final ArrayList<Category> mListCategory;

    public CategoryAdapter(Context context) {
        mContext = context;
        mListCategory = new ArrayList<>();
    }

    public void clearAndAddAll(ArrayList<Category> data) {
        mListCategory.clear();
        mListCategory.addAll(data);
    }

    @Override
    public int getGroupCount() {
        return mListCategory.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // todo is there any check indexOutOf Bound??
        return mListCategory.get(groupPosition).getListItems().size();
    }

    @Override
    public Category getGroup(int groupPosition) {
        return mListCategory.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getListItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_category_item, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.categoryTitleTv);
        listTitleTextView.setText(mListCategory.get(groupPosition).getListTitle());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        Video video = mListCategory.get(groupPosition).getListItems().get(childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_video_item, null);
        }
        TextView txtListChild = convertView.findViewById(R.id.title);
        txtListChild.setText(video.getTitle());
        ImageView thumbImg = convertView.findViewById(R.id.img_thumbnail);
        Glide.with(mContext)
            .load(video.getThumb())
            .crossFade()
            .placeholder(R.drawable.img_video_default)
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(thumbImg);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }//todo holder here, also this  list looks not nice: animations, try another library
}
