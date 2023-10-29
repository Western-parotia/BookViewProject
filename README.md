![中文文档](./README-ZH.md)

This is an Android-based reader project that uses a ViewGroup with custom RecyclerView and
LayoutManager to achieve complete separation of animation and data binding, allowing for custom item
views.

The project supports embedded images and video ads, as shown in the video below:

https://user-images.githubusercontent.com/13959965/230751166-a72e1f4b-317b-47a7-aa1c-bbc70ca34f13.mp4

![simple_view](./images/demo_pic.png)

# Usage

Just like using a regular RecyclerView:

```kotlin
bookView.setAdapter(RecyclerView.Adapter)
bookView.setFlipMode(@BookLayoutManager.BookFlipMode)

```

# Directory structure

* app module：Reader View demo
* gpu_test module：A pure curl anim demo

```java
BookRecyclerView.printViewToBitmap()
```



