package com.juziml.read.business

import android.os.Bundle
import android.widget.Toast
import com.foundation.app.arc.utils.ext.lazyAtomic
import com.juziml.read.R
import com.juziml.read.base.BaseActivity
import com.juziml.read.business.read.view.BookLayoutManager
import com.juziml.read.databinding.ActSimpleBinding

private const val DATA_SIZE = 20

class SimpleBookAct : BaseActivity() {
    private val vb by lazyAndSetRoot<ActSimpleBinding>()
    var position = 0
    private val bookPaperAdapter by lazyAtomic {
        BookPaperAdapter()
    }

    override fun init(savedInstanceState: Bundle?) {
        bookPaperAdapter.apply {
            setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.ir2d_iv -> {
                        showToast("点击图片：ir2d_iv")
                    }
                    R.id.ir2d_btn1 -> {
                        showToast("点击按钮：ir2d_btn1")
                    }
                    R.id.ir2d_btn2 -> {
                        showToast("点击按钮：ir2d_btn2")
                    }
                }
            }

        }
        vb.bookView.apply {
            setAdapter(bookPaperAdapter)
            setOnPositionChangedListener { _, curPosition ->
                position = curPosition
            }
            setFlipMode(BookLayoutManager.BookFlipMode.MODE_CURL)
            setOnClickMenuListener {
                showToast("点击菜单")
            }

        }

        vb.btnCover.setOnClickListener {
            vb.bookView.setFlipMode(BookLayoutManager.BookFlipMode.MODE_COVER)
        }
        vb.btnCurl.setOnClickListener {
            vb.bookView.setFlipMode(BookLayoutManager.BookFlipMode.MODE_CURL)
        }
        vb.btnNormal.setOnClickListener {
            vb.bookView.setFlipMode(BookLayoutManager.BookFlipMode.MODE_NORMAL)
        }
        vb.btnPrevious.setOnClickListener {
            if (position > 0) {
                position -= 1
                vb.bookView.scrollToPosition(position)
            }
        }
        vb.btnNext.setOnClickListener {
            if (position < DATA_SIZE - 1) {
                position += 1
                vb.bookView.scrollToPosition(position)
            }
        }
    }

    override fun bindData() {
        bookPaperAdapter.setNewData(createData(DATA_SIZE))
    }

    private fun createData(paperSize: Int): List<BookMockData> {
        var size = paperSize
        if (size <= 0) size = 5
        val data: MutableList<BookMockData> = ArrayList()
        for (i in 0 until size) {
            val book = BookMockData()
            book.content = buildString(i)
            data.add(book)
        }
        return data
    }

    private fun buildString(i: Int): String? {
        val builder = StringBuilder()
        for (f in 0..999) {
            builder.append(i)
            builder.append("-")
        }
        return builder.toString()
    }

    private fun showToast(text: String) {
        Toast.makeText(this@SimpleBookAct, text, Toast.LENGTH_LONG).show()
    }
}