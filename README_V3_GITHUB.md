# V3：GitHub 一键生成 APK

## 最简单的使用方法

1. 在 GitHub 新建一个空的 Repository。
2. 把这个工程里的所有文件上传到仓库。
3. 打开仓库的 **Actions**。
4. 选择 **Build APK (No Wrapper Required)**。
5. 点击 **Run workflow**。
6. 等待构建完成。
7. 打开这次运行记录，在 **Artifacts** 中下载 `ShortVideoAwareness-debug`。
8. 解压后得到 `app-debug.apk`，传到手机安装。

也可以直接 push 到 main 后使用 `Build APK` 工作流，但该工作流需要仓库中存在 Gradle Wrapper。
因此第一次建议使用 **Build APK (No Wrapper Required)**。

GitHub Actions 会在 GitHub 托管的 runner 上执行构建，APK 通过 workflow artifact 保存。
