package com.android.youtubelist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.youtubelist.R;
import com.android.youtubelist.model.CategoryVideo;
import com.android.youtubelist.model.Video;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaylistAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final ArrayList<CategoryVideo> mListCategoryVideo;

    public PlaylistAdapter(Context context) {
        mContext = context;
        mListCategoryVideo = new ArrayList<>();
    }

    public void clearAndAddAll(List<CategoryVideo> data) {
        mListCategoryVideo.clear();
        mListCategoryVideo.addAll(data);
    }

    @Override
    public int getGroupCount() {
        return mListCategoryVideo.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(mListCategoryVideo.get(groupPosition).getListItems()).size();
    }

    @Override
    public CategoryVideo getGroup(int groupPosition) {
        return mListCategoryVideo.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(getGroup(groupPosition).getListItems()).get(childPosition);
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
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_category_item, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.titleTv = convertView.findViewById(R.id.categoryTitleTv);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.titleTv.setText(mListCategoryVideo.get(groupPosition).getListTitle());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        Video video = mListCategoryVideo.get(groupPosition).getListItems().get(childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_video_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.titleTv = convertView.findViewById(R.id.title);
            childViewHolder.thumbImg = convertView.findViewById(R.id.img_thumbnail);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.titleTv.setText(video.getTitle());
        Glide.with(mContext)
            .load(video.getThumb())
            .crossFade()
            .placeholder(R.drawable.img_video_default)
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(childViewHolder.thumbImg);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ChildViewHolder {
        TextView titleTv;
        ImageView thumbImg;
    }

    class GroupViewHolder {
        TextView titleTv;
    }
}
