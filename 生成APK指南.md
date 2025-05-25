# 📱 课程录音转文字 APK 生成指南

## 🚀 快速生成APK

### 方法一：使用Android Studio（推荐）

#### 1. 环境准备
- 下载安装 [Android Studio](https://developer.android.com/studio)
- 确保安装了 Android SDK (API 24+)
- 安装 JDK 8 或更高版本

#### 2. 导入项目
1. 打开 Android Studio
2. 选择 "Open an existing Android Studio project"
3. 选择 `VoiceToTextApp` 文件夹
4. 等待 Gradle 同步完成

#### 3. 生成APK
1. 在菜单栏选择 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
2. 等待编译完成
3. 点击通知中的 "locate" 链接找到APK文件
4. APK文件位置：`app/build/outputs/apk/debug/app-debug.apk`

### 方法二：使用命令行

#### Windows系统
```cmd
# 进入项目目录
cd VoiceToTextApp

# 生成Debug版本APK
gradlew.bat assembleDebug

# 生成Release版本APK
gradlew.bat assembleRelease
```

#### Mac/Linux系统
```bash
# 进入项目目录
cd VoiceToTextApp

# 给gradlew执行权限
chmod +x gradlew

# 生成Debug版本APK
./gradlew assembleDebug

# 生成Release版本APK
./gradlew assembleRelease
```

## 📲 安装APK到手机

### 方法一：直接传输安装

#### 1. 传输APK文件
- **USB传输**：用数据线连接手机，将APK文件复制到手机存储
- **微信/QQ**：通过聊天软件发送APK文件到手机
- **云盘**：上传到百度网盘、OneDrive等，手机下载
- **邮件**：发送到邮箱，手机下载附件

#### 2. 手机设置
1. 打开手机 `设置` → `安全` → 开启 `未知来源` 或 `允许安装未知应用`
2. 不同手机品牌设置路径可能不同：
   - **华为**：设置 → 安全 → 更多安全设置 → 安装未知应用
   - **小米**：设置 → 安全 → 安装未知应用
   - **OPPO/Vivo**：设置 → 安全 → 未知来源
   - **三星**：设置 → 生物识别和安全 → 安装未知应用

#### 3. 安装应用
1. 在手机文件管理器中找到APK文件
2. 点击APK文件
3. 按提示点击 `安装`
4. 安装完成后点击 `打开` 即可使用

### 方法二：ADB安装（开发者）

```bash
# 确保手机开启USB调试
# 连接手机到电脑

# 安装APK
adb install app-debug.apk

# 如果已安装，强制覆盖安装
adb install -r app-debug.apk
```

## 🔧 APK文件说明

### Debug版本 vs Release版本

| 版本类型 | 文件名 | 特点 | 适用场景 |
|---------|--------|------|----------|
| Debug | app-debug.apk | 包含调试信息，文件较大 | 测试使用 |
| Release | app-release.apk | 优化压缩，文件较小 | 正式发布 |

### 文件位置
```
VoiceToTextApp/
├── app/build/outputs/apk/
│   ├── debug/
│   │   └── app-debug.apk          # Debug版本
│   └── release/
│       └── app-release.apk        # Release版本
```

## ⚠️ 常见问题解决

### 1. 编译错误

**问题**：Gradle同步失败
**解决**：
- 检查网络连接
- 更新Android Studio到最新版本
- 清理项目：`Build` → `Clean Project`

**问题**：SDK版本错误
**解决**：
- 打开 `Tools` → `SDK Manager`
- 安装 Android API 24 (Android 7.0) 及以上版本

### 2. 安装问题

**问题**：手机提示"禁止安装"
**解决**：
- 确保开启了"未知来源"选项
- 检查手机存储空间是否充足
- 尝试重启手机后再安装

**问题**：安装后无法打开
**解决**：
- 检查手机Android版本是否为7.0以上
- 确保手机有足够的运行内存
- 重新下载APK文件

### 3. 权限问题

**问题**：应用无法录音
**解决**：
- 手机设置 → 应用管理 → 课程录音转文字 → 权限
- 开启"麦克风"和"存储"权限

## 📋 系统要求

### 最低要求
- **Android版本**：7.0 (API 24) 及以上
- **内存**：至少2GB RAM
- **存储**：至少100MB可用空间
- **权限**：麦克风、存储权限

### 推荐配置
- **Android版本**：8.0 (API 26) 及以上
- **内存**：4GB RAM 或更多
- **存储**：500MB 可用空间
- **网络**：不需要网络连接（本地处理）

## 🎯 使用提示

### 首次使用
1. 安装后首次打开会请求权限，请全部允许
2. 建议在安静环境下测试录音效果
3. 可以先录制一小段测试语音识别准确性

### 最佳实践
- 保持手机电量充足
- 在安静环境中使用
- 说话清晰，语速适中
- 定期清理旧的录音文件

## 📞 技术支持

如果在生成或安装APK过程中遇到问题：

1. **检查日志**：Android Studio的Build窗口会显示详细错误信息
2. **清理重建**：`Build` → `Clean Project` → `Rebuild Project`
3. **更新工具**：确保Android Studio和SDK都是最新版本
4. **网络问题**：如果下载依赖失败，可以尝试使用VPN或更换网络

---

**生成的APK文件可以直接分享给其他人安装使用！** 📱✨ 