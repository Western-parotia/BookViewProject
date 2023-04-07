https://api.openai.com/v1/chat/completions

请求数据：

```json
{
  "model": "gpt-3.5-turbo",
  "messages": [
    {
      "role": "user",
      "content": "我说A,请重复"
    },
    {
      "role": "system",
      "content": "A"
    },
    {
      "role": "user",
      "content": "我刚才让你重复什么？"
    }

```

响应数据 {
"id": "chatcmpl-6xV9UO1OYynBEBBxEAViX6ZcKrhcu",
"object": "chat.completion",
"created": 1679639120,
"model": "gpt-3.5-turbo-0301",
"usage": {
"prompt_tokens": 40,
"completion_tokens": 14,
"total_tokens": 54 },
"choices": [
{
"message": {
"role": "assistant",
"content": "您让我重复\"A\"这个单词。"
},
"finish_reason": "stop",
"index": 0 }
]
} 标题栏：#0CA6A4 聊天列表：#F2F2F2 聊天列表上的文字：#333333 页面底部工具栏：#FFFFFF 工具栏左侧语音按钮：#767676 (未选中状态) / #0CA6A4 (
选中状态)
文本输入框：#FFFFFF 语音录制按钮：#767676 (未选中状态) / #0CA6A4 (选中状态)
发送按钮：#0CA6A4 取消录制按钮：#FF3B30