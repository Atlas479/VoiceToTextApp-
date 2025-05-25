# 🚀 课程录音转文字APP - 一键生成APK完整指南

## 📱 应用简介

这是一个专为课程录音设计的Android应用，支持：
- ✅ **智能录音控制**：手动开始、暂停、继续、停止
- ✅ **自定义命名**：每个录音文件可单独命名  
- ✅ **实时转换**：语音立即转换为文字，无需网络
- ✅ **文件管理**：查看、分享、导出、删除录音文件
- ✅ **本地处理**：保护隐私，数据不上传

## 🎯 三种APK生成方案

### 方案一：GitHub在线构建（推荐⭐⭐⭐⭐⭐）

**优点**：无需安装任何软件，完全在线，最简单可靠
**时间**：10-15分钟
**要求**：能访问GitHub

#### 详细步骤：

1. **注册GitHub账号**
   - 访问：https://github.com
   - 点击"Sign up"注册账号
   - 验证邮箱完成注册

2. **创建新仓库**
   - 登录后点击右上角"+"
   - 选择"New repository"
   - 仓库名称：`VoiceToTextApp`
   - 选择"Public"（公开）
   - 点击"Create repository"

3. **上传项目文件**
   ```
   方法一：网页上传
   - 点击"uploading an existing file"
   - 将VoiceToTextApp文件夹内的所有文件拖拽上传
   - 填写提交信息："Initial commit"
   - 点击"Commit changes"
   
   方法二：Git命令（如果安装了Git）
   git clone https://github.com/你的用户名/VoiceToTextApp.git
   复制所有文件到克隆的文件夹
   git add .
   git commit -m "Initial commit"
   git push
   ```

4. **自动构建APK**
   - 上传完成后，GitHub Actions会自动开始构建
   - 点击仓库顶部的"Actions"标签
   - 等待构建完成（约10-15分钟）
   - 绿色✅表示构建成功

5. **下载APK文件**
   - 在Actions页面点击最新的构建任务
   - 滚动到底部的"Artifacts"部分
   - 下载"课程录音转文字-debug"文件
   - 解压得到`app-debug.apk`

### 方案二：Android Studio（推荐⭐⭐⭐⭐）

**优点**：功能最全面，适合开发者
**时间**：1-2小时（包含下载安装）
**要求**：稳定网络，至少8GB内存

#### 详细步骤：

1. **下载Android Studio**
   - 访问：https://developer.android.com/studio
   - 下载适合您系统的版本
   - 安装时选择"Standard"标准安装

2. **首次启动配置**
   - 启动Android Studio
   - 选择"Do not import settings"
   - 按向导完成SDK下载（约2-3GB）

3. **打开项目**
   - 选择"Open an existing project"
   - 浏览到`VoiceToTextApp`文件夹
   - 点击"OK"打开项目

4. **同步项目**
   - 等待Gradle同步完成
   - 如果提示缺少SDK，点击"Install missing SDK"

5. **生成APK**
   - 菜单：`Build` → `Build Bundle(s)/APK(s)` → `Build APK(s)`
   - 等待构建完成
   - 点击"locate"找到APK文件
   - 文件位置：`app/build/outputs/apk/debug/app-debug.apk`

### 方案三：命令行构建（适合技术用户⭐⭐⭐）

**优点**：轻量级，不需要IDE
**时间**：30分钟-1小时
**要求**：命令行操作经验

#### 环境准备：

1. **安装Java JDK**
   ```
   下载地址：https://adoptium.net/
   选择JDK 11或17版本
   安装后设置JAVA_HOME环境变量
   ```

2. **下载Android SDK**
   ```
   下载地址：https://developer.android.com/studio#command-tools
   解压到：C:\Android\Sdk
   设置ANDROID_HOME环境变量
   ```

3. **构建APK**
   ```bash
   cd VoiceToTextApp
   gradlew assembleDebug
   ```

## 📲 APK安装到手机

### 1. 传输APK文件
- **USB数据线**：连接电脑，复制APK到手机
- **微信/QQ**：发送文件给自己，手机接收
- **云盘**：上传到百度云盘等，手机下载
- **邮件**：发送邮件附件

### 2. 手机设置
```
不同品牌设置路径：
华为：设置 → 安全 → 更多安全设置 → 外部来源应用下载
小米：设置 → 安全 → 应用管理 → 应用包安装程序
OPPO：设置 → 安全 → 应用安装 → 安装外部来源应用
vivo：设置 → 更多设置 → 安全 → 未知来源
三星：设置 → 安全 → 未知来源
```

### 3. 安装应用
- 点击APK文件
- 选择"安装"
- 等待安装完成
- 点击"打开"启动应用

### 4. 权限设置
首次启动时需要授予权限：
- ✅ **麦克风权限**：用于录音
- ✅ **存储权限**：用于保存文件
- ✅ **通知权限**：显示录音状态

## 🔧 故障排除

### 构建失败
```
问题：Gradle下载超时
解决：使用GitHub在线构建

问题：内存不足
解决：关闭其他程序，重启电脑

问题：网络连接问题
解决：使用手机热点或VPN
```

### 安装失败
```
问题：解析包时出现问题
解决：重新下载APK文件

问题：应用未安装
解决：检查存储空间，开启未知来源

问题：权限被拒绝
解决：手动到设置中授予权限
```

## 📊 构建状态检查

### GitHub Actions状态
- 🟢 **绿色**：构建成功，可以下载APK
- 🟡 **黄色**：正在构建中，请等待
- 🔴 **红色**：构建失败，检查代码

### 本地构建检查
```bash
# 检查Java版本
java -version

# 检查Android SDK
echo %ANDROID_HOME%

# 检查Gradle
gradlew --version
```

## 🎉 成功标志

构建成功后您将得到：
- 📱 **APK文件**：约8-12MB大小
- 📋 **应用名称**：课程录音转文字
- 🔢 **版本号**：1.0.x
- 📅 **构建日期**：当前日期

## 💡 使用建议

1. **推荐方案**：GitHub在线构建（最简单）
2. **备用方案**：请朋友帮忙构建
3. **学习方案**：Android Studio（学习开发）
4. **技术方案**：命令行构建（技术用户）

## 📞 技术支持

如果遇到问题：
1. 查看本文档的故障排除部分
2. 检查GitHub Actions构建日志
3. 确保网络连接稳定
4. 尝试不同的构建方案

---

**总结：推荐使用GitHub在线构建，这是最快最可靠的方法！** 🚀

构建完成后，您就可以在手机上使用这个专业的课程录音转文字应用了！ 