package com.juziml.read.business;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.juziml.read.R;
import com.juziml.read.business.read.view.BookView;

/**
 * @Desc: -
 * create by zhusw on 2020-03-27 14:54
 */
public class BookPaperAdapter extends BaseQuickAdapter<BookMockData, BaseViewHolder> {

    private int bgRes;
    private BookView bookView;

    public BookPaperAdapter() {
        super(R.layout.item_read, null);
        bgRes = R.color.colorPrimaryDark;
    }

    public void setBgRes(int bgRes) {
        this.bgRes = bgRes;
    }

    public void setBookView(BookView bookView) {
        this.bookView = bookView;
    }

    @Override
    protected void convert(BaseViewHolder helper, BookMockData item) {
        helper.setText(R.id.ir2d_tv_content, item.content);
        helper.setText(R.id.ir2d_tv_content2, item.content);
        helper.setText(R.id.ir2d_tv_position, helper.getLayoutPosition() + "");

        helper.getView(R.id.irb_fl_content).setBackgroundColor(mContext.getResources().getColor(R.color.read_bg));
        helper.setTextColor(R.id.ir2d_tv_content, mContext.getResources().getColor(R.color.read_txt));
        helper.setTextColor(R.id.ir2d_tv_content2, mContext.getResources().getColor(R.color.read_txt));

//        if(helper.getAdapterPosition()%2==0){
//            helper.getView(R.id.ir2d_root).setBackgroundColor(Color.rgb(0x33,0xCC,0xFF));
//        }else {
//            helper.getView(R.id.ir2d_root).setBackgroundColor(Color.rgb(0x00,0xE6,0xAC));
//        }

        helper.addOnClickListener(R.id.ir2d_iv);
        helper.addOnClickListener(R.id.ir2d_btn1);
        helper.addOnClickListener(R.id.ir2d_btn2);
    }
}
