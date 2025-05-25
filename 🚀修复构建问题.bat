@echo off
chcp 65001 >nul
echo.
echo ========================================
echo 🚀 修复GitHub Actions构建问题
echo ========================================
echo.

echo 📋 正在检查Git状态...
git status

echo.
echo 📝 添加修复版配置文件...
git add .github/workflows/build-apk.yml

echo.
echo 💾 提交修改...
git commit -m "🔧 修复GitHub Actions构建问题

- 更新为修复版构建配置
- 添加详细的错误诊断
- 自动检测和修复缺失文件
- 改进构建稳定性"

echo.
echo 🚀 推送到GitHub...
git push origin main

echo.
echo ========================================
echo ✅ 修复完成！
echo ========================================
echo.
echo 📱 接下来的步骤：
echo 1. 打开GitHub仓库页面
echo 2. 进入Actions标签页
echo 3. 手动触发新的构建
echo 4. 等待构建完成并下载APK
echo.
echo 🌐 GitHub仓库地址：
echo https://github.com/Atlas479/VoiceToTextApp-
echo.
pause 