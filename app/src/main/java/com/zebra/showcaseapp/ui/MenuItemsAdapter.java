package com.zebra.showcaseapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.zebra.showcaseapp.R;

import java.util.List;

/**
 * Created by Chandan Jana on 14-10-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class MenuItemsAdapter extends BaseAdapter {

    private static String lastSelectedSection;
    private final Context mContext;
    private final List<String> mSections;
    private final OnClickMenu mListener;
    private int currentTextcolor;

    public MenuItemsAdapter(Context context, List<String> sections, OnClickMenu listener) {
        mContext = context;
        mSections = sections;
        mListener = listener;

        lastSelectedSection = sections.get(0);
    }

    @Override
    public int getCount() {
        return mSections.size();
    }

    @Override
    public String getItem(int position) {
        return mSections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        final MenuItemHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.layout_left_menu_item, parent, false);
            holder = new MenuItemHolder(convertView);


            convertView.setTag(holder);

        } else {

            holder = (MenuItemHolder) convertView.getTag();

        }

        Resources r = mContext.getResources();
        int pxMarginSection = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());

        holder.position = position;

        holder.mLine.setVisibility(View.GONE);

        if (mSections.get(position).equals(mContext.getString(R.string.new_app_install))) {
            holder.mIconView.setImageResource(R.drawable.ic_install_mobile);
            holder.mLayoutItem.setPadding(0, 0, 0, pxMarginSection);
            holder.mTitle.setText(mContext.getString(R.string.new_app_install));
            holder.mLine.setVisibility(View.VISIBLE);
            holder.mLayoutItem.setPadding(0, 0, 0, pxMarginSection);
        } else if (mSections.get(position).equals(mContext.getString(R.string.beta_app))) {
            holder.mIconView.setImageResource(R.drawable.ic_beta_app);
            holder.mTitle.setText(mContext.getString(R.string.beta_app));
            holder.mLayoutItem.setPadding(0, 0, 0, pxMarginSection);
            holder.mLine.setVisibility(View.VISIBLE);
        }

        holder.mLayoutItem.setOnTouchListener((view, motionEvent) -> {

            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    currentTextcolor = holder.mTitle.getCurrentTextColor();
                    holder.mLayoutItemSelect.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_color));
                    holder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.title_color));
                    holder.mIconView.setColorFilter(ContextCompat.getColor(mContext, R.color.title_color));
                    return true;

                case MotionEvent.ACTION_UP:

                    holder.mLayoutItemSelect.setBackgroundResource(R.color.bgLeftMenu);
                    holder.mTitle.setTextColor(currentTextcolor);
                    holder.mIconView.setColorFilter(ContextCompat.getColor(mContext, R.color.title_color));

                    mListener.onClick(mSections.get(position));
                    return true;

                case MotionEvent.ACTION_CANCEL:

                    holder.mLayoutItemSelect.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bgLeftMenu));
                    holder.mTitle.setTextColor(currentTextcolor);
                    holder.mIconView.setColorFilter(ContextCompat.getColor(mContext, R.color.title_color));

                    return true;
                default:
                    return false;

            }

        });

        return convertView;

    }

    public void setLastSelectedSection(String idSection) {
        lastSelectedSection = idSection;
    }

    static class MenuItemHolder {

        View view;
        TextView mTitle;
        TextView updateText;
        ImageView mIconView;
        RelativeLayout mLayoutItem;
        View mLine;
        LinearLayout mLayoutItemSelect;

        int position;

        public MenuItemHolder(View itemView) {
            view = itemView;
            mTitle = view.findViewById(R.id.title);
            updateText = view.findViewById(R.id.update_text);
            mIconView = view.findViewById(R.id.icon);
            mLayoutItem = view.findViewById(R.id.layoutItem);
            mLine = view.findViewById(R.id.rl_line);
            mLayoutItemSelect = view.findViewById(R.id.layoutItemSelect);
        }

    }
}
