package com.juziml.read.business.read;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.juziml.read.R;

import java.util.ArrayList;
import java.util.List;

/**
 * create by zhusw on 2020-07-30 15:21
 */
public class RecyclerViewWithV2CurlAct extends AppCompatActivity {

    ReadViewGroup readViewGroup;

    AdapterV2 adapter2d;

    int position = 0;
    final int DATA_SIZE = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recycler2dcurl);
        readViewGroup = findViewById(R.id.ar2dc_readViewGroup);
        adapter2d = new AdapterV2();

        readViewGroup.setAdapter(adapter2d);
        adapter2d.setReadRecyclerViewV2(readViewGroup);
        readViewGroup.setFlipMode(ReadLayoutManagerV2.BookFlipMode.MODE_CURL);
        position = 0;
        adapter2d.setNewData(createData(DATA_SIZE));
        findViewById(R.id.ar2dc_btn_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    readViewGroup.scrollToPosition(position -= 1);
                }
            }
        });

        findViewById(R.id.ar2dc_btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < DATA_SIZE - 1) {
                    readViewGroup.scrollToPosition(position += 1);
                }
            }
        });

        adapter2d.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.ir2d_iv) {
                    Toast.makeText(RecyclerViewWithV2CurlAct.this, "click iv" + position, Toast.LENGTH_LONG).show();
                } else if (view.getId() == R.id.ir2d_btn1) {
                    Toast.makeText(RecyclerViewWithV2CurlAct.this, "ir2d_btn1" + position, Toast.LENGTH_LONG).show();
                } else if (view.getId() == R.id.ir2d_btn2) {
                    Toast.makeText(RecyclerViewWithV2CurlAct.this, "ir2d_btn2" + position, Toast.LENGTH_LONG).show();
                }
            }
        });
        readViewGroup.setOnPositionChangedListener(new ReadViewGroup.OnPositionChangedListener() {
            @Override
            public void onChanged(boolean arriveNext, int curPosition) {
                position = curPosition;
            }
        });

        readViewGroup.setOnClickMenuListener(new ReadViewGroup.OnClickMenuListener() {
            @Override
            public void onClickMenu() {
                Toast.makeText(RecyclerViewWithV2CurlAct.this, "点击菜单", Toast.LENGTH_LONG).show();
            }
        });


        findViewById(R.id.ar2dc_btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readViewGroup.setFlipMode(ReadLayoutManagerV2.BookFlipMode.MODE_COVER);
            }
        });

        findViewById(R.id.ar2dc_btn_curl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readViewGroup.setFlipMode(ReadLayoutManagerV2.BookFlipMode.MODE_CURL);

            }
        });
        findViewById(R.id.ar2dc_btn_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readViewGroup.setFlipMode(ReadLayoutManagerV2.BookFlipMode.MODE_NORMAL);
            }
        });

    }


    private List<BookInfo> createData(int size) {
        if (size <= 0) size = 5;
        List<BookInfo> data = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            BookInfo book = new BookInfo();
            book.content = buildString(i);
            data.add(book);
        }
        return data;
    }

    private String buildString(int i) {
        StringBuilder builder = new StringBuilder();
        for (int f = 0; f < 1000; f++) {
            builder.append(i);
            builder.append("-");
        }
        return builder.toString();
    }
}
