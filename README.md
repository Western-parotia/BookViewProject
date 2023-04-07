# 介绍

提供一种阅读器的实现思路，基于RecyclerView，只需编写Paper布局。采用木偶View将渲染，
Paper页面布局、事件、动画完全分离。在paperLayout继承成自LinearLayout，
自然支持放入图片，视频等元素，但完全无需关心翻页动画的渲染。（不包含文字的处理，以后应该也不会添加）

在 SimpleBookAct 中提供了一个简单的使用示范

# 实现思路细节更新中...

* BookView ：提供使用API
* PaperLayout：页面根布局
* PuppetView：接受事件，渲染动画
* ReadLayoutManager：控制布局
* ReadRecyclerView：
* RecyclerView.Adapter