@echo off
chcp 65001 >nul
echo.
echo ========================================
echo    课程录音转文字 APK 简化生成工具
echo ========================================
echo.

echo 🔍 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误：未找到Java环境
    echo 请先安装JDK 8或更高版本
    echo 下载地址：https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo ✅ Java环境检查通过

echo.
echo 📋 生成APK的方法：
echo.
echo 方法一：使用Android Studio（推荐）
echo 1. 下载安装Android Studio：https://developer.android.com/studio
echo 2. 打开Android Studio，选择"Open an existing project"
echo 3. 选择当前VoiceToTextApp文件夹
echo 4. 等待Gradle同步完成
echo 5. 菜单栏：Build → Build Bundle(s)/APK(s) → Build APK(s)
echo 6. APK文件会生成在：app\build\outputs\apk\debug\app-debug.apk
echo.

echo 方法二：在线构建（如果有网络）
echo 1. 将项目上传到GitHub
echo 2. 使用GitHub Actions自动构建
echo 3. 或使用其他在线Android构建服务
echo.

echo 方法三：手动配置Gradle
echo 1. 确保安装了Android SDK
echo 2. 设置ANDROID_HOME环境变量
echo 3. 下载Gradle 8.0并配置PATH
echo 4. 运行：gradle assembleDebug
echo.

echo 💡 推荐使用Android Studio，它会自动处理所有依赖和配置
echo.

echo 📱 生成APK后的安装步骤：
echo 1. 将APK文件传输到手机（USB、微信、QQ等）
echo 2. 手机设置→安全→开启"未知来源"或"允许安装未知应用"
echo 3. 在手机上点击APK文件进行安装
echo 4. 安装完成后授予麦克风和存储权限
echo.

echo 🎯 应用功能特色：
echo ✅ 手动控制录音开始、暂停、继续、停止
echo ✅ 自定义文件名，每次录音单独保存
echo ✅ 实时语音转文字，无需网络
echo ✅ 内置文件管理，支持查看、分享、导出
echo ✅ 专为课程录音设计，适合学习使用
echo.

echo 是否打开Android Studio下载页面？(Y/N)
set /p choice=
if /i "%choice%"=="Y" (
    start https://developer.android.com/studio
)

echo.
echo 📞 如需技术支持，请查看"生成APK指南.md"文件
echo 🎉 感谢使用课程录音转文字应用！
pause 