package com.networks.testapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.saber.stickyheader.stickyData.HeaderData;
import com.saber.stickyheader.stickyData.StickyMainData;
import com.saber.stickyheader.stickyView.StickHeaderItemDecoration;

import java.util.ArrayList;
import java.util.List;

public abstract class StickyHeaderAdapter<D extends StickyMainData, H extends HeaderData> extends RecyclerView.Adapter implements StickHeaderItemDecoration.StickyHeaderInterface{
    private List<StickyMainData> mData = new ArrayList<>();
    private boolean showEmptyView = false;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        StickHeaderItemDecoration stickHeaderDecoration = new StickHeaderItemDecoration(this);
        recyclerView.addItemDecoration(stickHeaderDecoration);
    }

    @Override
    public final int getItemViewType(int position) {
        if(hasExtraRow() && position == getItemCount() - 2){
            return EmptyHeaderDataImpl.EMPTY_HEADER;
        }if(hasExtraRow() && position == getItemCount() - 1){
            getViewType(position);
        } else if (mData.get(position) instanceof HeaderData) {
            return ((HeaderData) mData.get(position)).getHeaderType();
        }
        return getViewType(position);
    }

    @Override
    public boolean isHeader(int itemPosition) {
        if(hasExtraRow() && itemPosition == getItemCount() - 2){
            return true;
        }else if(hasExtraRow() && itemPosition == getItemCount() - 1){
            return false;
        }else if(itemPosition < 0){
            return false;
        }else return mData.get(itemPosition) instanceof HeaderData;
    }

    @Override
    public int getItemCount() {
        return mData.size() + (hasExtraRow() ? 2 : 0);
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        if(hasExtraRow() && headerPosition == getItemCount() - 2){
            return R.layout.item_empty_sticky_header;
        }else return ((HeaderData) mData.get(headerPosition)).getHeaderLayout();

    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    /*
    *Show empty viewholder
     */
    public void showEmptyViewHolder(boolean newShowEmptyView){


        boolean previousShowEmptyView = showEmptyView;

        showEmptyView = newShowEmptyView;

        if (previousShowEmptyView != newShowEmptyView) {
            if (previousShowEmptyView) {
                notifyItemRangeRemoved(getItemCount(),2);
            } else {
                notifyItemRangeInserted(getItemCount(),2);
            }
        }
    }

    /*
     *Add new data to already existing one
     */
    public void addHeaderAndData(@NonNull List<D> datas, @Nullable HeaderData header) {

        if (header != null) {
            mData.add(header);
        }

        mData.addAll(datas);
    }

    /*
     *Replaces old data with newData list
     */
    public void setHeaderAndData(@NonNull List<D> datas, @Nullable HeaderData header) {

        mData.clear();

        if (header != null) {
            mData.add(header);
        }

        mData.addAll(datas);
    }

    protected boolean hasExtraRow(){return showEmptyView;}

    protected int getViewType(int pos){
        return 0;
    }

    protected D getDataInPosition(int position) {
        return (D) mData.get(position);
    }

    protected H getHeaderDataInPosition(int position) {
        return (H) mData.get(position);
    }
}