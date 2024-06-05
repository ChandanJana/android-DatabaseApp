package com.zebra.showcaseapp.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;
import com.zebra.showcaseapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chandan Jana on 14-10-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class SimpleLeftMenuView extends NavigationView {
    private final LayoutInflater mInflater;
    private final Context mContext;

    private ListView mItemsList;
    private MenuItemsAdapter mItemsAdapter;

    private OnClickMenu mListener;

    private ImageView mHeader;

    //region Constructors
    public SimpleLeftMenuView(Context context) {
        super(context);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initLayout();

        setData();
    }

    public SimpleLeftMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initLayout();

        setData();
    }

    public SimpleLeftMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initLayout();

        setData();
    }

    private void initLayout() {
        mInflater.inflate(R.layout.layout_left_menu, this);
        mItemsList = findViewById(R.id.menu_items_list);

        mHeader = findViewById(R.id.header);

        mHeader.setOnClickListener(view -> {
            // do something
        });
    }

    public void setSelectedSection(String idSection) {
        mItemsAdapter.setLastSelectedSection(idSection);
    }

    public void setmListener(OnClickMenu mListener) {
        this.mListener = mListener;
    }

    private void setData() {

        List<String> sections = new ArrayList<>();

        sections.add(mContext.getString(R.string.new_app_install));
        sections.add(mContext.getString(R.string.beta_app));

        mItemsAdapter = new MenuItemsAdapter(mContext, sections, new OnClickMenu() {
            @Override
            public void onClick(String id) {
                mItemsAdapter.setLastSelectedSection(id);

                if (mListener != null)
                    mListener.onClick(id);
            }
        });
        mItemsList.setAdapter(mItemsAdapter);
        mItemsList.setSelection(0);
        mItemsList.setItemChecked(0, true);
    }
}
