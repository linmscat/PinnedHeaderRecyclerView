package com.qgnie.pinnedheaderrecyclerview.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.qgnie.pinnedheaderrecyclerview.decoration.RVHeaderDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class RVHeaderAdapter<T> extends RecyclerView.Adapter implements RVHeaderDecoration.HeaderDecorationCallback {
    private List<T> mDatas = new ArrayList<>();
    private List<HeaderData> mHeaderData = new ArrayList<HeaderData>();
    private int mSpanCount = 1;

    abstract public String getHeader(T info);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        updateLayoutManager(recyclerView.getLayoutManager());
    }

    public void updateLayoutManager(RecyclerView.LayoutManager manager) {
        if (manager.getClass() == GridLayoutManager.class) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            mSpanCount = gridManager.getSpanCount();
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return RVHeaderAdapter.this.getSpanSize(position);
                }
            });
        } else if (manager.getClass() == LinearLayoutManager.class) {
            mSpanCount = 1;
        } else {
            throw new UnsupportedOperationException("LayoutManager must be GridLayoutManager or LinearLayoutManager");
        }
    }

    protected T getItem(int pos) {
        return mDatas.get(pos);
    }

    /**
     * We deal the last one of each header, to make it fill the rest span
     * For other, just treat it as span size 1
     */
    public int getSpanSize(int pos) {
        for (HeaderData headerData : mHeaderData) {
            if (pos == headerData.posEnd) {
                int restSpan = (headerData.posEnd - headerData.posStart + 1) % mSpanCount;
                if (restSpan == 0) {
                    // No rest, return self size
                    return 1;
                } else {
                    //     rest size              + self size
                    return (mSpanCount - restSpan) + 1;
                }
            }
        }
        return 1;
    }

    @Override
    public boolean hasHeaderOffset(int pos) {
        for (HeaderData data : mHeaderData) {
            if (pos >= data.posStart && pos <= data.posEnd) {
                // The first, second, ..., spanCount will have header offset
                return (pos - data.posStart) < mSpanCount;
            }
        }
        return false;
    }

    @Override
    public String getHeader(int pos) {
        for (HeaderData data : mHeaderData) {
            if (pos >= data.posStart && pos <= data.posEnd) {
                return data.header;
            }
        }
        return null;
    }

    @Override
    public boolean isDrawHeader(int pos, boolean isFirstChildView) {
        if (isFirstChildView) {
            // first child view always has header
            return true;
        } else {
            for (HeaderData data : mHeaderData) {
                // Draw header if it is the first one of each header except first child view
                if (pos == data.posStart) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setDatas(List<T> datas) {
        clearHeaderData();
        generatePinnedHeaderList(datas);
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
        clearHeaderData();
        notifyDataSetChanged();
    }

    private void generatePinnedHeaderList(List<T> datas) {
        clearHeaderData();

        HashMap<String, HeaderData> map = new HashMap<>();
        int sz = datas.size();
        for (int pos = 0; pos < sz; ++pos) {
            T data = datas.get(pos);
            String header = getHeader(data);
            HeaderData headerData = map.get(header);
            if (null == headerData) {
                headerData = new HeaderData();
                headerData.posStart = pos;
                headerData.header = header;
                mHeaderData.add(headerData);
                map.put(header, headerData);
            }
            headerData.posEnd = pos;
        }
    }

    private void clearHeaderData() {
        mHeaderData.clear();
    }

    private static class HeaderData {
        int posStart;
        int posEnd;
        String header;
    }
}