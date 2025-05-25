@echo off
chcp 65001 >nul
echo.
echo ========================================
echo 📱 课程录音转文字 APK 快速生成工具
echo ========================================
echo.

echo 🔍 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 未检测到Java环境
    echo.
    echo 📥 正在下载便携版Java...
    echo 请稍等，这可能需要几分钟...
    
    :: 创建临时目录
    if not exist "temp" mkdir temp
    
    :: 下载便携版OpenJDK
    echo 下载Java运行环境...
    powershell -Command "& {Invoke-WebRequest -Uri 'https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip' -OutFile 'temp\openjdk.zip'}"
    
    :: 解压Java
    echo 解压Java环境...
    powershell -Command "& {Expand-Archive -Path 'temp\openjdk.zip' -DestinationPath 'temp' -Force}"
    
    :: 设置Java路径
    set "JAVA_HOME=%cd%\temp\jdk-17.0.2"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    
    echo ✅ Java环境配置完成
) else (
    echo ✅ Java环境检查通过
)

echo.
echo 🔧 准备构建环境...

:: 创建必要的目录
if not exist "gradle\wrapper" mkdir gradle\wrapper

:: 下载Gradle Wrapper JAR
echo 📥 下载Gradle Wrapper...
powershell -Command "& {Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'}"

:: 创建gradle-wrapper.properties
echo 📝 创建Gradle配置...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
echo networkTimeout=10000
echo validateDistributionUrl=true
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > gradle\wrapper\gradle-wrapper.properties

:: 设置Android SDK路径（如果存在）
if exist "%LOCALAPPDATA%\Android\Sdk" (
    set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
    echo ✅ 找到Android SDK: %ANDROID_HOME%
) else (
    echo ⚠️  未找到Android SDK，将使用在线构建
)

echo.
echo 🚀 开始构建APK...
echo 这可能需要几分钟时间，请耐心等待...
echo.

:: 构建Debug APK
echo 📦 构建Debug版本...
gradlew.bat assembleDebug --no-daemon --stacktrace

if %errorlevel% equ 0 (
    echo.
    echo ✅ APK构建成功！
    echo.
    echo 📁 APK文件位置：
    echo    app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 📱 安装方法：
    echo    1. 将APK文件传输到手机
    echo    2. 在手机设置中开启"未知来源"安装
    echo    3. 点击APK文件进行安装
    echo.
    
    :: 尝试打开APK所在文件夹
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo 🎉 正在打开APK文件夹...
        explorer app\build\outputs\apk\debug\
    )
) else (
    echo.
    echo ❌ APK构建失败
    echo.
    echo 🔧 可能的解决方案：
    echo    1. 检查网络连接
    echo    2. 安装Android Studio
    echo    3. 使用在线构建方案
    echo.
)

echo.
echo 按任意键退出...
pause >nul 