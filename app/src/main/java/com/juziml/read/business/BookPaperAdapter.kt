package com.juziml.read.business

import com.foundation.widget.crvadapter.viewbinding.ViewBindingQuickAdapter
import com.foundation.widget.crvadapter.viewbinding.ViewBindingViewHolder
import com.juziml.read.R
import com.juziml.read.databinding.ItemReadBinding

class BookPaperAdapter : ViewBindingQuickAdapter<ItemReadBinding, BookMockData>() {

    override fun convertVB(
        holder: ViewBindingViewHolder<ItemReadBinding>,
        vb: ItemReadBinding,
        item: BookMockData
    ) {
        vb.tvContent.text = item.content
        vb.tvContent2.text = item.content

        holder.addOnClickListener(R.id.ir2d_iv)
        holder.addOnClickListener(R.id.ir2d_btn1)
        holder.addOnClickListener(R.id.ir2d_btn2)
    }
}