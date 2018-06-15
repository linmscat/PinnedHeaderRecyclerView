package com.qgnie.pinnedheaderrecyclerview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import qgnie.com.pinnedheader.R;

public class RVHeaderDecoration extends RecyclerView.ItemDecoration {
    private HeaderDecorationCallback mCallback;
    private Context mContext;

    private int mHeaderHeight;

    private DecorationHeaderAdapter mAdapter;
    private View mHeaderView;

    public RVHeaderDecoration(Context context, int headerHeight, HeaderDecorationCallback callback, DecorationHeaderAdapter adapter) {
        mCallback = callback;
        mHeaderHeight = headerHeight;
        mContext = context;
        mAdapter = adapter;
        if (null == mAdapter) {
            mAdapter = new DecorationHeaderAdapter() {
                @Override
                public View onCreateHeaderView() {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.header, null);
                    TextView header = (TextView) view.findViewById(R.id.text);
                    view.setTag(header);
                    return view;
                }

                @Override
                public void onBindHeaderView(View parent, String header) {
                    TextView headerView = (TextView) parent.getTag();
                    headerView.setText(header);
                }
            };
        }

        mHeaderView = mAdapter.onCreateHeaderView();
        if (mHeaderView == null) {
            throw new IllegalArgumentException("Header view supported is invalid");
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        super.getItemOffsets(outRect, itemPosition, parent);

        if (mCallback.hasHeaderOffset(itemPosition)) {
            outRect.top = mHeaderHeight;
        } else {
            outRect.top = 0;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount; ++i) {
            View view = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(view);

            if (!mCallback.isDrawHeader(pos, i == 0)) {
                continue;
            }

            String header = mCallback.getHeader(pos);
            if (TextUtils.isEmpty(header)) {
                return;
            }
            int headerY = Math.max(mHeaderHeight, view.getTop());

            setHeader(header);
            measureAndLayoutHeader(parent.getWidth(), left, 0, right, mHeaderHeight);
            drawHeader(c, left, headerY - mHeaderHeight);
        }
    }

    private void setHeader(String header) {
        mAdapter.onBindHeaderView(mHeaderView, header);
    }

    private void measureAndLayoutHeader(int width, int left, int top, int right, int bottom) {
        mHeaderView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mHeaderHeight, View.MeasureSpec.EXACTLY));
        mHeaderView.layout(left, top, right, bottom);
    }

    private void drawHeader(Canvas canvas, int left, int top) {
        mHeaderView.setDrawingCacheEnabled(true);
        canvas.drawBitmap(mHeaderView.getDrawingCache(), left, top, null);
        // To reuse view
        mHeaderView.destroyDrawingCache();
    }

    public interface HeaderDecorationCallback {
        String getHeader(int pos);
        boolean hasHeaderOffset(int pos);
        boolean isDrawHeader(int pos, boolean isFirstView);
    }

    public interface DecorationHeaderAdapter {
        View onCreateHeaderView();
        void onBindHeaderView(View parent, String header);
    }
}

