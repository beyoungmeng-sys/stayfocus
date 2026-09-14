package com.example.shortvideoawareness

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(42, 60, 42, 40)
        }

        root.addView(TextView(this).apply {
            text = "短视频提醒 V2"
            textSize = 28f
        })

        root.addView(TextView(this).apply {
            text = """
                它不是限制你使用短视频，而是在你“不知不觉开始刷”的时候提醒你。

                判断依据：
                • 连续向上滑动
                • 短时间内重复滑动
                • 当前界面是否有视频/播放特征
                • 常见短视频页面特征
                • 行为评分达到阈值

                所有判断均在手机本地完成。
            """.trimIndent()
            textSize = 16f
            setPadding(0, 28, 0, 24)
        })

        root.addView(Button(this).apply {
            text = "开启 / 管理无障碍服务"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        })

        root.addView(TextView(this).apply {
            text = "默认规则：15 秒行为窗口；达到 8 分提醒；提醒后 60 秒冷却。"
            textSize = 14f
            setPadding(0, 20, 0, 0)
        })

        setContentView(root)
    }
}
