package com.example.shortvideoawareness

import android.accessibilityservice.AccessibilityService
import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlin.math.abs

class MonitorAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())

    private var score = 0
    private var upwardSwipes = 0
    private var lastScrollAt = 0L
    private var lastAlertAt = 0L
    private var currentPackage = ""

    private val behaviorWindow = 15_000L
    private val alertCooldown = 60_000L

    // 常见短视频/视频入口的辅助识别。
    private val knownVideoPackages = setOf(
        "com.ss.android.ugc.aweme",       // 抖音
        "tv.danmaku.bili",                // 哔哩哔哩
        "com.xingin.xhs",                 // 小红书
        "com.tencent.mm",                 // 微信
        "com.google.android.youtube",     // YouTube
        "com.instagram.android",          // Instagram
        "com.kuaishou.nebula",            // 快手系
        "com.smile.gifmaker"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        if (event.packageName != null) {
            currentPackage = event.packageName.toString()
        }

        val now = System.currentTimeMillis()

        if (now - lastScrollAt > behaviorWindow) {
            score = 0
            upwardSwipes = 0
        }

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> handleScroll(event, now)
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                // 内容变化本身不触发提醒，只用于后续判断。
            }
        }
    }

    private fun handleScroll(event: AccessibilityEvent, now: Long) {
        val delta = event.toIndex - event.fromIndex

        // 对列表型界面，toIndex 增大通常意味着内容向下移动、用户向上滑。
        // 某些 App 不提供 index，因此退化为 scrollY 变化。
        val likelyUp = delta > 0 ||
                (event.scrollY >= 0 && event.maxScrollY > 0 && event.scrollY > 0)

        if (!likelyUp) return

        upwardSwipes++

        val rapid = now - lastScrollAt in 0..2500
        if (rapid) score += 3 else score += 2

        if (knownVideoPackages.contains(currentPackage)) {
            score += 2
        }

        val root = rootInActiveWindow
        if (hasVideoLikeContent(root)) score += 2
        if (hasShortVideoLikeContent(root)) score += 2

        lastScrollAt = now

        if (score >= 8 && now - lastAlertAt >= alertCooldown) {
            lastAlertAt = now
            score = 0
            upwardSwipes = 0
            showReminder()
        }
    }

    private fun hasVideoLikeContent(root: AccessibilityNodeInfo?): Boolean {
        if (root == null) return false

        val words = listOf(
            "播放", "暂停", "全屏", "视频", "进度条",
            "play", "pause", "fullscreen", "video"
        )
        return containsAny(root, words, 0)
    }

    private fun hasShortVideoLikeContent(root: AccessibilityNodeInfo?): Boolean {
        if (root == null) return false

        val words = listOf(
            "Shorts", "Reels", "短视频", "视频号",
            "推荐", "关注", "喜欢", "评论", "分享"
        )
        return containsAny(root, words, 0)
    }

    private fun containsAny(
        node: AccessibilityNodeInfo,
        words: List<String>,
        depth: Int
    ): Boolean {
        if (depth > 20) return false

        val text = buildString {
            append(node.text?.toString() ?: "")
            append(" ")
            append(node.contentDescription?.toString() ?: "")
            append(" ")
            append(node.viewIdResourceName ?: "")
        }

        if (words.any { text.contains(it, ignoreCase = true) }) return true

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = containsAny(child, words, depth + 1)
            child.recycle()
            if (found) return true
        }
        return false
    }

    private fun showReminder() {
        handler.post {
            AlertDialog.Builder(this)
                .setTitle("提醒一下")
                .setMessage(
                    "你正在形成连续刷视频的行为。\n\n" +
                    "你刚才拿起手机，本来是想做什么？"
                )
                .setNegativeButton("退出") { dialog, _ ->
                    dialog.dismiss()
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
                .setPositiveButton("我就是要刷") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onInterrupt() {}
}
