package com.qgnie;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnie.pinnedheaderrecyclerview.adapter.RVHeaderAdapter;
import com.qgnie.pinnedheaderrecyclerview.decoration.RVHeaderDecoration;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GRID_COLUMN = 3;
    private RecyclerView recyclerView;
    private TextView tvShowMode;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private boolean isGrid = false;
    private RVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mockData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_mode:
                changeShowMode(!isGrid);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void changeShowMode(boolean isGrid) {
        this.isGrid = isGrid;
        if (isGrid) {
            if (null == gridLayoutManager) {
                gridLayoutManager = new GridLayoutManager(this, GRID_COLUMN);
            }
            recyclerView.setLayoutManager(gridLayoutManager);
            if (null != adapter) {
                adapter.updateLayoutManager(gridLayoutManager);
            }
        } else {
            if (null == linearLayoutManager) {
                linearLayoutManager = new LinearLayoutManager(this);
            }
            recyclerView.setLayoutManager(linearLayoutManager);
            if (null != adapter) {
                adapter.updateLayoutManager(linearLayoutManager);
            }
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview);
        changeShowMode(isGrid);

        tvShowMode = findViewById(R.id.show_mode);
        tvShowMode.setOnClickListener(this);
    }

    private void mockData() {
        String[] testData = new String[]{"Ava", "Albert", "Allen", "Ailsa", "Malcolm", "Joan", "Niki", "Betty",
                "Linda", "Whitney", "Lily", "Fred", "Gary", "William", "Charles", "Michael", "Karl", "Barbara", "Elizabeth",
                "Helen", "Katharine", "Lee", "Ann", "Diana", "Fiona", "Alibaba", "Awae", "Awae", "Awaewat", "Awaea"};
        final ArrayList<String> datas = new ArrayList<String>();
        for (String str : testData) {
            datas.add(str);
        }
        Collections.sort(datas);

        adapter = new RVAdapter();
        adapter.setDatas(datas);
        recyclerView.addItemDecoration(new RVHeaderDecoration(this, getResources().getDimensionPixelSize(R.dimen.header_height), adapter,
                new RVHeaderDecoration.DecorationHeaderAdapter() {
                    @Override
                    public View onCreateHeaderView() {
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.header, null);
                        TextView header = (TextView) view.findViewById(R.id.text);
                        view.setTag(header);
                        return view;
                    }

                    @Override
                    public void onBindHeaderView(View parent, String header) {
                        TextView headerView = (TextView) parent.getTag();
                        headerView.setText(header);
                    }
                }));
        recyclerView.setAdapter(adapter);
    }

    private class RVAdapter extends RVHeaderAdapter<String> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(MainActivity.this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(300, 100));
            textView.setBackgroundColor(Color.GRAY);
            return new RecyclerView.ViewHolder(textView) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(getItem(position));
        }

        @Override
        public String getHeader(String info) {
            return info.substring(0, 1);
        }
    }
}
