package com.tryhard.myvoa.ui.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.InformationItem;

import java.util.ArrayList;

/**
 * Created by Chen on 2015/10/14.
 */
public class ListOfArticleFragAdapter extends RecyclerView.Adapter<ListOfArticleFragAdapter.ItemViewHolder> {
    private ArrayList<InformationItem> list;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private OnItemClickLitener mOnItemClickLitener;
    private Context mContext;
    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public ArrayList<InformationItem> getList() {
        return list;
    }

    public ListOfArticleFragAdapter(ArrayList<InformationItem> mInformationItems,Context context) {
        this.list = mInformationItems;
        mContext = context;
    }

    public void updateInfoItemList(ArrayList<InformationItem> mInformationItems){
        this.list = mInformationItems;
    }

    // RecyclerView的count设置为数据总条数+ 1（footerView）
    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        if(getItemViewType(position) == TYPE_ITEM) {
            InformationItem informationItem = list.get(position);
            holder.titleView.setText(informationItem.getTitle());
            holder.dateView.setText(informationItem.getDate());
            //若已浏览过，则改变字体颜色
            if(informationItem.getIsScaned() == true){
                holder.titleView.setTextColor(mContext.getResources().getColor(R.color.c001));
                holder.dateView.setTextColor(mContext.getResources().getColor(R.color.c001));
            }else{

                holder.titleView.setTextColor(mContext.getResources().getColor(R.color.c002));
                holder.dateView.setTextColor(mContext.getResources().getColor(R.color.c002));

            }
            if(informationItem.getBitmapOs() != null)
                holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(informationItem.getBitmapOs(), 0, informationItem.getBitmapOs().length, null));
            else{
                holder.imageView.setImageBitmap(null);
            }
        }
        //如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
           holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            });

        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.article_list_item, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
        }
        // type == TYPE_FOOTER 返回footerView
        else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.article_list_footer_view, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }
        return null;
    }

    class FooterViewHolder extends ItemViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView dateView;
        LinearLayout linearLayout;
        ImageView imageView;

        public ItemViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.culture_titleView);
            dateView = (TextView) view.findViewById(R.id.culture_dateView);
            linearLayout = (LinearLayout) view.findViewById(R.id.listItemLinear);
            imageView = (ImageView) view.findViewById(R.id.photo);
        }
    }

}
