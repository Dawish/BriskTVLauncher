package com.danxx.brisktvlauncher.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danxingxi on 2016/3/31.
 */
public abstract class BaseRecyclerViewAdapter< T > extends RecyclerView.Adapter<BaseRecyclerViewHolder>{
    /**header view type**/
    public static final int TYPE_HEADER = 0;
    /**item view type**/
    public static final int TYPE_NORMAL = 1;
    private View mHeaderView;
    public OnItemClickListener<T> mOnItemClickListener;
    /**保存处于选中状态的itemView的position**/
    private SparseBooleanArray selectedItems;
//    private List<Model> mData = new ArrayList<Model>();
//
//    public void setData(List<? extends Model> data){
//        mData.clear();
//        mData.addAll(data);
//    }
    public BaseRecyclerViewAdapter(){
        selectedItems = new SparseBooleanArray();
    }
    private List< T > mData = new ArrayList< T >();

    public void setData(List< T > data){
        mData.clear();
        mData.addAll(data);
    }


    public T getItemData(int position) {
        T res = null;

        if(position < mData.size()) {
            res = mData.get(position);
        }

        return res;
    }

    public void clearData(){
        if(mData != null){
            mData.clear();
        }
    }

    public void setOnItemClickListener(OnItemClickListener li) {
        mOnItemClickListener = li;
    }

    /**
     * add header view
     * @param headerView
     */
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    /**
     *  get header view
     * @return
     */
    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return TYPE_NORMAL;
        if(position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    /**
     * itemView的选中状态和非选中状态切换并及时更新UI状态
     * 选中状态调用时就切换为非选中状态，反之对调状态
     * @param position 用户点击的itemView的位置
     */
    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        /*这个更新UI会使焦点闪烁一下*/
//        notifyItemChanged(position);
    }

    /**
     * 判断这个位置的item是处于选中状态
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }


    /**
     * 清除所有Item的选中状态
     */
    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        notifyDataSetChanged();
//        for (Integer i : selection) {
//            notifyItemChanged(i);
//        }
    }

    /**
     * 获得所有选中状态item的position集合
     * @return
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<> (selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            /*我们使用keyAt可以取到position，当然也可以使用valueAt取到value值，显然这个集合中的value都为true*/
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
    /**
     * 获得item的位置
     * @param holder
     * @return
     */
    public int getRealPosition(BaseRecyclerViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER) return new HeaderViewHolder(mHeaderView);
        return createItem(parent ,viewType);
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder holder, int position) {
        /**如果是header view就直接返回，不需要绑定数据**/
        if(getItemViewType(position) == TYPE_HEADER) return;
        final int pos = getRealPosition(holder);
        final T data = mData.get(pos);
        bindData(holder ,pos);

        if(mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(pos, data);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onItemLongClick(pos, data);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mData.size() : mData.size() + 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(BaseRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if(lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    /**
     * 创建item view
     * @param parent
     * @param viewType
     * @return
     */
    protected  abstract BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType);

    /**
     * 绑定数据
     * @param holder
     * @param position
     */
    protected abstract void bindData(BaseRecyclerViewHolder holder, int position);

    /**
     *header view ViewHolder
     */
    class HeaderViewHolder extends BaseRecyclerViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected View getView() {
            return null;
        }
    }

    /**
     * item 点击事件接口
     * @param <T>
     */
    public interface OnItemClickListener<T> {
        /**单击监听**/
        void onItemClick(int position, T data);
        /**长按监听**/
        void onItemLongClick(int position, T data);
    }
}

