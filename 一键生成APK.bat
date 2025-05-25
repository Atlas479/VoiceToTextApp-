@echo off
chcp 65001 >nul
echo.
echo ========================================
echo    课程录音转文字 APK 一键生成工具
echo ========================================
echo.

echo [1/4] 检查环境...
if not exist "gradlew.bat" (
    echo ❌ 错误：请在项目根目录运行此脚本
    pause
    exit /b 1
)

echo [2/4] 清理项目...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo ❌ 清理失败，请检查环境配置
    pause
    exit /b 1
)

echo [3/4] 生成 Debug APK...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo ❌ 编译失败，请检查代码和依赖
    pause
    exit /b 1
)

echo [4/4] 生成 Release APK...
call gradlew.bat assembleRelease
if %errorlevel% neq 0 (
    echo ⚠️  Release版本生成失败，但Debug版本已生成
    goto :show_result
)

:show_result
echo.
echo ✅ APK 生成完成！
echo.
echo 📁 文件位置：
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo    Debug版本: app\build\outputs\apk\debug\app-debug.apk
)
if exist "app\build\outputs\apk\release\app-release.apk" (
    echo    Release版本: app\build\outputs\apk\release\app-release.apk
)

echo.
echo 📱 安装方法：
echo    1. 将APK文件传输到手机
echo    2. 在手机上开启"未知来源"安装
echo    3. 点击APK文件进行安装
echo.

echo 是否打开APK文件夹？(Y/N)
set /p choice=
if /i "%choice%"=="Y" (
    if exist "app\build\outputs\apk" (
        explorer "app\build\outputs\apk"
    )
)

echo.
echo 🎉 完成！感谢使用课程录音转文字应用
pause 