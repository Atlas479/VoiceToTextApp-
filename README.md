# 课程录音转文字应用

## 📱 应用简介

这是一个专为课程录音设计的Android手机应用，能够实时将语音转换成文字并保存到手机内存中。应用支持手动控制录音的开始、暂停、继续和结束，每次录音都可以单独命名和保存，非常适合课堂学习使用。

## ✨ 主要功能

### 🎤 智能录音控制
- **手动开始**：点击开始录音，可自定义文件名
- **暂停继续**：支持录音过程中暂停和继续
- **停止保存**：结束录音时可重新确认文件名
- **实时转换**：语音立即转换为文字，无需等待

### 💾 专业文件管理
- **自定义命名**：每个录音文件都可以自定义名称
- **独立保存**：每次录音保存为单独的文件
- **时间戳记录**：每段语音都标记准确的时间
- **暂停标记**：文件中清楚标记暂停和继续的时间点

### 🔧 实用工具
- **文件浏览**：内置文件管理器，方便查看所有录音
- **文件分享**：可以将文字内容分享到其他应用
- **导出功能**：将文件导出到下载目录
- **清理工具**：自动清理30天前的旧文件

## 🚀 快速开始

### 系统要求
- Android 7.0 (API 24) 或更高版本
- 麦克风权限
- 存储权限

### 使用方法
1. **开始录音**：
   - 点击"开始新录音"按钮
   - 输入文件名（如：数学课第一章）
   - 点击"开始录音"

2. **录音控制**：
   - **暂停**：点击"暂停"按钮临时停止录音
   - **继续**：点击"继续录音"按钮恢复录音
   - **查看文字**：在主界面实时查看转换的文字

3. **结束保存**：
   - 点击"停止并保存"按钮
   - 确认或修改文件名
   - 点击"保存"完成

4. **文件管理**：
   - 点击"查看已保存文件"管理录音文件
   - 支持查看、分享、导出、删除操作

## 📁 文件存储

### 存储位置
文件保存在：`/Android/data/com.voicetotext.app/files/Documents/CourseRecordings/`

### 文件命名规则
- 课程录音：`course_数学课第一章_2024-01-15_14-30-25.txt`
- 日常记录：`course_daily_2024-01-15.txt`

### 文件格式示例
```
=== 课程录音转文字 ===
文件名：数学课第一章
开始时间：2024年01月15日 14:30:25
==================================================

[14:30:30] 今天我们学习函数的基本概念
[14:30:45] 函数是数学中的重要概念

[14:31:10] === 录音暂停 ===
[14:32:15] === 录音继续 ===

[14:32:20] 接下来我们看函数的定义域
[14:32:35] 定义域是函数的重要组成部分

=== 录音结束 2024-01-15 14:35:00 ===
录音时长：从 2024-01-15 14:30:25 到 2024-01-15 14:35:00
文件名：数学课第一章
```

## 🎯 使用场景

### 📚 课堂学习
- **课程录音**：记录老师讲课内容
- **重点标记**：通过暂停功能标记重要内容
- **复习材料**：转换的文字可作为复习资料

### 📝 会议记录
- **会议纪要**：记录会议讨论内容
- **分段记录**：不同议题可暂停分段
- **快速整理**：文字内容便于后期整理

### 🎓 学习笔记
- **听课笔记**：实时转换语音为文字笔记
- **思路记录**：记录学习过程中的思考
- **知识整理**：便于后期知识点整理

## 🛠️ 技术特性

### 核心技术
- **Android SpeechRecognizer**：使用系统内置的语音识别引擎
- **前台服务**：确保录音过程稳定运行
- **MVVM架构**：清晰的代码结构和数据管理
- **ViewBinding**：类型安全的视图绑定

### 性能优化
- **智能重启**：识别错误时自动重新开始
- **状态管理**：完善的录音状态控制
- **内存管理**：及时释放不需要的资源
- **电池优化**：合理使用系统资源

## 🔒 隐私保护

- **本地处理**：所有语音识别在设备本地完成
- **无网络传输**：不会将语音数据发送到服务器
- **用户控制**：用户完全控制录音的开始、暂停和停止
- **数据安全**：文件仅保存在用户设备上

## 📋 权限说明

| 权限 | 用途 | 必需性 |
|------|------|--------|
| RECORD_AUDIO | 录制音频进行语音识别 | 必需 |
| WRITE_EXTERNAL_STORAGE | 保存文字文件 | 必需 |
| READ_EXTERNAL_STORAGE | 读取已保存的文件 | 必需 |
| FOREGROUND_SERVICE | 后台持续运行 | 必需 |
| WAKE_LOCK | 保持设备唤醒 | 可选 |

## 🐛 故障排除

### 常见问题

**Q: 语音识别不准确怎么办？**
A: 
- 确保在安静的环境中使用
- 说话清晰，语速适中
- 检查手机的语音识别设置
- 尽量靠近麦克风

**Q: 录音过程中意外中断？**
A: 
- 检查手机电量是否充足
- 确保应用有足够的存储空间
- 避免在录音时频繁切换应用

**Q: 暂停功能不工作？**
A: 
- 确保在录音状态下使用暂停功能
- 重启应用重新尝试
- 检查权限设置

**Q: 文件保存失败？**
A: 
- 检查存储权限是否已授予
- 确保手机有足够的存储空间
- 文件名不要包含特殊字符

### 使用技巧

1. **最佳录音环境**：选择安静的环境，减少背景噪音
2. **合理使用暂停**：在换话题或休息时使用暂停功能
3. **及时保存**：录音结束后及时保存，避免数据丢失
4. **定期整理**：定期查看和整理保存的文件
5. **备份重要文件**：将重要的录音文件导出备份

## 📄 开源协议

本项目采用 MIT 协议开源，详见 [LICENSE](LICENSE) 文件。

## 🔄 版本历史

### v1.0.0 (2024-01-15)
- 🎉 首次发布
- ✅ 手动录音控制功能
- ✅ 暂停/继续录音功能
- ✅ 自定义文件命名
- ✅ 独立文件保存
- ✅ 实时语音转文字
- ✅ 完整的文件管理

---

**开发者**: VoiceToText Team  
**最后更新**: 2024年1月15日  
**专为课程学习设计** 📚 