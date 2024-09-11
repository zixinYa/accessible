package com.example.accessible.accessibilityService

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.LinkedList
import java.util.Queue
import java.util.Random

class MyAccessibilityService : AccessibilityService() {
    companion object {
        var instance: MyAccessibilityService? = null
            private set
        const val STOP_TEST_ACTION = "com.example.accessible.STOP_TEST_ACTION"
    }

    private val handler = Handler(Looper.getMainLooper())
    private val testActionRunnable = object : Runnable {
        override fun run() {
            performTestAction()
            handler.postDelayed(this, 500)
        }
    }

    private val stopTestReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == STOP_TEST_ACTION) {
                handler.removeCallbacks(testActionRunnable) // 停止定时任务
                Log.d("MyAccessibilityService", "Test action stopped via ADB")
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        handler.post(testActionRunnable) // 开始定时任务
        val filter = IntentFilter(STOP_TEST_ACTION)
        registerReceiver(stopTestReceiver, filter)
        Log.d("MyAccessibilityService", "Service connected and test action started")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        handler.removeCallbacks(testActionRunnable) // 停止定时任务
        unregisterReceiver(stopTestReceiver)
        Log.d("MyAccessibilityService", "Service destroyed and test action stopped")
    }

    fun performTestAction() {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            Log.d("MyAccessibilityService", "Performing test action")
            traverseNodes(rootNode)
        } else {
            Log.e("MyAccessibilityService", "Root node is null")
        }
    }

    private fun traverseNodes(rootNode: AccessibilityNodeInfo) {
        val nodesQueue: Queue<AccessibilityNodeInfo> = LinkedList()
        val nodesList: MutableList<AccessibilityNodeInfo> = mutableListOf()
        nodesQueue.add(rootNode)

        // 收集所有节点
        while (nodesQueue.isNotEmpty()) {
            val node = nodesQueue.poll() ?: continue
            nodesList.add(node)

            // 遍历子节点
            for (i in 0 until node.childCount) {
                val childNode = node.getChild(i)
                if (childNode != null) {
                    nodesQueue.add(childNode)
                }
            }
        }

        if (nodesList.isNotEmpty()) {
            val randomNode = nodesList.random()
            handleNode(randomNode)
        }

        Log.d("MyAccessibilityService", "Node traversal complete")
    }

    private fun handleNode(node: AccessibilityNodeInfo) {
        try {
            when (node.className) {
                "android.widget.Button", "android.widget.TextView",
                "android.widget.CheckBox", "android.widget.Switch",
                "android.widget.RadioButton", "android.widget.ToggleButton" -> {
                    if (node.isClickable) {
                        val action = if (Random().nextBoolean()) {
                            AccessibilityNodeInfo.ACTION_CLICK
                        } else {
                            AccessibilityNodeInfo.ACTION_LONG_CLICK
                        }
                        node.performAction(action)
                    }
                }
                "android.widget.SeekBar" -> {
                    scrollSeekBar(node, Random().nextBoolean())
                }
                "android.widget.EditText" -> {
                    if (node.isFocusable) {
                        node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                        val randomText = generateRandomText()
                        val arguments = Bundle()
                        arguments.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                            randomText
                        )
                        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    }
                }
                "android.widget.LinearLayout", "android.widget.RelativeLayout",
                "android.widget.FrameLayout", "android.widget.ConstraintLayout",
                "android.widget.GridLayout", "android.view.ViewGroup" -> {
                    if (node.isClickable) {
                        val action = if (Random().nextBoolean()) {
                            AccessibilityNodeInfo.ACTION_CLICK
                        } else {
                            AccessibilityNodeInfo.ACTION_LONG_CLICK
                        }
                        node.performAction(action)
                    }
                }
                else -> {
                }
            }
        } catch (e: Exception) {
            Log.e("MyAccessibilityService", "Error performing action on node: ${e.message}")
        }
    }

    private fun generateRandomText(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val length = Random().nextInt(10) + 1
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(chars[Random().nextInt(chars.length)])
        }
        return sb.toString()
    }

    private fun scrollSeekBar(node: AccessibilityNodeInfo, forward: Boolean) {
        val action = if (forward) {
            AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
        } else {
            AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        }
        val result = node.performAction(action)
        Log.d("MyAccessibilityService", "Result of scroll action: $result")
    }
}
