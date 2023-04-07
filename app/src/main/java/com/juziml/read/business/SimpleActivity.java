package com.juziml.read.business;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.juziml.read.R;
import com.juziml.read.business.read.view.BookView;
import com.juziml.read.business.read.view.ReadLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * create by zhusw on 2020-07-30 15:21
 */
public class SimpleActivity extends AppCompatActivity {

    BookView bookView;

    BookPaperAdapter bookPaperAdapter;

    int position = 0;
    final int DATA_SIZE = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_simple);
        bookView = findViewById(R.id.ar2dc_readViewGroup);
        bookPaperAdapter = new BookPaperAdapter();

        bookView.setAdapter(bookPaperAdapter);
        bookPaperAdapter.setReadRecyclerViewV2(bookView);
        bookView.setFlipMode(ReadLayoutManager.BookFlipMode.MODE_CURL);
        position = 0;
        bookPaperAdapter.setNewData(createData(DATA_SIZE));
        findViewById(R.id.ar2dc_btn_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    bookView.scrollToPosition(position -= 1);
                }
            }
        });

        findViewById(R.id.ar2dc_btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < DATA_SIZE - 1) {
                    bookView.scrollToPosition(position += 1);
                }
            }
        });

        bookPaperAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.ir2d_iv) {
                    Toast.makeText(SimpleActivity.this, "click iv" + position, Toast.LENGTH_LONG).show();
                } else if (view.getId() == R.id.ir2d_btn1) {
                    Toast.makeText(SimpleActivity.this, "ir2d_btn1" + position, Toast.LENGTH_LONG).show();
                } else if (view.getId() == R.id.ir2d_btn2) {
                    Toast.makeText(SimpleActivity.this, "ir2d_btn2" + position, Toast.LENGTH_LONG).show();
                }
            }
        });
        bookView.setOnPositionChangedListener(new BookView.OnPositionChangedListener() {
            @Override
            public void onChanged(boolean arriveNext, int curPosition) {
                position = curPosition;
            }
        });

        bookView.setOnClickMenuListener(new BookView.OnClickMenuListener() {
            @Override
            public void onClickMenu() {
                Toast.makeText(SimpleActivity.this, "点击菜单", Toast.LENGTH_LONG).show();
            }
        });


        findViewById(R.id.ar2dc_btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookView.setFlipMode(ReadLayoutManager.BookFlipMode.MODE_COVER);
            }
        });

        findViewById(R.id.ar2dc_btn_curl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookView.setFlipMode(ReadLayoutManager.BookFlipMode.MODE_CURL);

            }
        });
        findViewById(R.id.ar2dc_btn_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookView.setFlipMode(ReadLayoutManager.BookFlipMode.MODE_NORMAL);
            }
        });

    }


    private List<BookMockData> createData(int size) {
        if (size <= 0) size = 5;
        List<BookMockData> data = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            BookMockData book = new BookMockData();
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
