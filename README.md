[中文文档](./README-ZH.md)

This is a reader project for the Android platform.
It is based on a custom RecyclerView within a ViewGroup and uses a custom LayoutManager.
By arranging elements along the Z-axis, it achieves complete separation of reader animations and
data binding,
allowing for full customization of the ItemView.

It also supports embedding image and video advertisements. Please see the video below for an example
of the effects:

https://user-images.githubusercontent.com/13959965/230751166-a72e1f4b-317b-47a7-aa1c-bbc70ca34f13.mp4

![simple_view](./images/demo_pic.png)

# Usage

Just like using a regular RecyclerView:

```kotlin
bookView.setAdapter(RecyclerView.Adapter)
bookView.setFlipMode(@BookLayoutManager.BookFlipMode)

```

# Directory structure

* app module：BookView demo

* gpu_test module：A demo of pure curl animations



