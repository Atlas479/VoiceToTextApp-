@echo off
chcp 65001 >nul
echo.
echo ========================================
echo 📱 课程录音转文字 - 本地APK构建工具
echo ========================================
echo.

echo 🔍 检查构建环境...

:: 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 未检测到Java环境
    echo.
    echo 📥 请先安装Java JDK：
    echo    下载地址：https://adoptium.net/
    echo    选择JDK 11或17版本
    echo.
    echo 💡 或者使用GitHub在线构建（推荐）：
    echo    查看：🚀一键生成APK完整指南.md
    echo.
    pause
    exit /b 1
) else (
    echo ✅ Java环境检查通过
)

:: 检查Android SDK
if exist "%LOCALAPPDATA%\Android\Sdk" (
    set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
    echo ✅ 找到Android SDK: %ANDROID_HOME%
) else if exist "C:\Android\Sdk" (
    set "ANDROID_HOME=C:\Android\Sdk"
    echo ✅ 找到Android SDK: %ANDROID_HOME%
) else (
    echo ⚠️  未找到Android SDK
    echo.
    echo 📥 请选择以下方案之一：
    echo    1. 安装Android Studio（推荐）
    echo    2. 使用GitHub在线构建（最简单）
    echo    3. 手动下载Android SDK
    echo.
    echo 💡 推荐使用GitHub在线构建，无需安装任何环境
    echo    详见：🚀一键生成APK完整指南.md
    echo.
    pause
    exit /b 1
)

echo.
echo 🔧 配置构建环境...

:: 创建gradle.properties配置文件，使用国内镜像
echo 📝 配置Gradle镜像源...
(
echo # Gradle配置
echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
echo org.gradle.parallel=true
echo org.gradle.caching=true
echo org.gradle.daemon=true
echo.
echo # 使用阿里云镜像加速下载
echo systemProp.http.proxyHost=
echo systemProp.http.proxyPort=
echo systemProp.https.proxyHost=
echo systemProp.https.proxyPort=
) > gradle.properties

:: 创建init.gradle使用国内镜像
if not exist "%USERPROFILE%\.gradle" mkdir "%USERPROFILE%\.gradle"
echo 📝 配置Maven镜像源...
(
echo allprojects {
echo     repositories {
echo         maven { url 'https://maven.aliyun.com/repository/google' }
echo         maven { url 'https://maven.aliyun.com/repository/central' }
echo         maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
echo         maven { url 'https://maven.aliyun.com/repository/public' }
echo         google()
echo         mavenCentral()
echo         gradlePluginPortal()
echo     }
echo }
) > "%USERPROFILE%\.gradle\init.gradle"

:: 检查Gradle Wrapper
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo 📥 下载Gradle Wrapper...
    if not exist "gradle\wrapper" mkdir gradle\wrapper
    
    :: 尝试从多个源下载
    echo 尝试从GitHub下载...
    powershell -Command "try { Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar' -TimeoutSec 30 } catch { exit 1 }"
    
    if not exist "gradle\wrapper\gradle-wrapper.jar" (
        echo 尝试从阿里云镜像下载...
        powershell -Command "try { Invoke-WebRequest -Uri 'https://mirrors.aliyun.com/gradle/gradle-8.2-bin.zip' -OutFile 'temp-gradle.zip' -TimeoutSec 30; Expand-Archive -Path 'temp-gradle.zip' -DestinationPath 'temp-gradle'; Copy-Item 'temp-gradle\gradle-8.2\lib\gradle-wrapper.jar' 'gradle\wrapper\gradle-wrapper.jar'; Remove-Item 'temp-gradle.zip', 'temp-gradle' -Recurse -Force } catch { exit 1 }"
    )
    
    if not exist "gradle\wrapper\gradle-wrapper.jar" (
        echo ❌ Gradle Wrapper下载失败
        echo.
        echo 💡 建议使用GitHub在线构建：
        echo    1. 访问 https://github.com
        echo    2. 创建新仓库并上传项目文件
        echo    3. GitHub会自动构建APK
        echo    4. 详见：🚀一键生成APK完整指南.md
        echo.
        pause
        exit /b 1
    )
)

:: 创建gradle-wrapper.properties
echo 📝 配置Gradle Wrapper...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://mirrors.aliyun.com/gradle/gradle-8.2-bin.zip
echo networkTimeout=60000
echo validateDistributionUrl=true
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > gradle\wrapper\gradle-wrapper.properties

echo.
echo 🚀 开始构建APK...
echo 这可能需要10-30分钟，请耐心等待...
echo.

:: 设置环境变量
set "PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools;%PATH%"

:: 清理之前的构建
echo 🧹 清理之前的构建...
if exist "app\build" rmdir /s /q "app\build"

:: 构建Debug APK
echo 📦 构建Debug版本...
gradlew.bat clean assembleDebug --no-daemon --stacktrace --info

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
    echo    4. 授予麦克风和存储权限
    echo.
    echo 📋 应用功能：
    echo    ✅ 手动控制录音开始、暂停、继续、停止
    echo    ✅ 自定义文件名，每次录音单独保存
    echo    ✅ 实时语音转文字，无需网络
    echo    ✅ 内置文件管理，支持查看、分享、导出
    echo.
    
    :: 尝试打开APK所在文件夹
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo 🎉 正在打开APK文件夹...
        explorer app\build\outputs\apk\debug\
        
        :: 显示文件大小
        for %%F in ("app\build\outputs\apk\debug\app-debug.apk") do (
            set /a size=%%~zF/1024/1024
            echo 📊 APK文件大小：!size! MB
        )
    )
) else (
    echo.
    echo ❌ APK构建失败
    echo.
    echo 🔧 可能的解决方案：
    echo    1. 检查网络连接是否稳定
    echo    2. 重启电脑后重试
    echo    3. 使用GitHub在线构建（推荐）
    echo    4. 安装Android Studio
    echo.
    echo 💡 最简单的方法：
    echo    使用GitHub在线构建，无需安装任何环境
    echo    详见：🚀一键生成APK完整指南.md
    echo.
    echo 📋 GitHub在线构建步骤：
    echo    1. 注册GitHub账号
    echo    2. 创建新仓库
    echo    3. 上传项目文件
    echo    4. 等待自动构建
    echo    5. 下载APK文件
    echo.
)

echo.
echo 按任意键退出...
pause >nul 