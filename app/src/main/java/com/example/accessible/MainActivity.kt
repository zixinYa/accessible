package com.example.accessible

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accessible.accessibilityService.MyAccessibilityService
import com.example.accessible.databinding.ActivityMainBinding
import com.example.accessible.recyclerView.Item
import com.example.accessible.recyclerView.MyAdapter
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var itemList: List<Item>
    private lateinit var binding: ActivityMainBinding
    private val colors = intArrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        view()
    }

    @SuppressLint("SetTextI18n")
    private fun view(){
        listOf(
            binding.sampleText1 to "Button1",
            binding.sampleText2 to "Button2",
            binding.sampleText3 to "Button3",
            binding.sampleText4 to "Button4"
        ).forEach { (buttonView, buttonText) ->
            buttonView.setOnClickListener { buttonClick(buttonText, buttonView) }
        }
        // 从自定义布局中查找视图
        val editTextInput: EditText = findViewById(R.id.editTextInput)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)
        val textViewOutput: TextView = findViewById(R.id.textViewOutput)

        // 设置按钮点击事件
        buttonSubmit.setOnClickListener {
            val inputText = editTextInput.text.toString()
            if (inputText.isNotBlank()) {
                textViewOutput.text = "你输入的内容是：$inputText"
            } else {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textclick.setOnClickListener { showCustomDialog(R.layout.dialog_text_test) }
//        binding.sampleEditText.setOnClickListener { showCustomDialog(R.layout.dialog_edit_text) }

        binding.testButon.setOnClickListener {
            val service = MyAccessibilityService.instance
            if (service != null) {
                binding.testButon.visibility = View.GONE
                service.performTestAction()
            } else {
                Toast.makeText(this, "Accessibility service not available", Toast.LENGTH_SHORT).show()
            }
        }

        // 设置 SeekBar 的 OnSeekBarChangeListener
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("MainActivity", "SeekBar progress changed to $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("MainActivity", "Started tracking SeekBar")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d("MainActivity", "Stopped tracking SeekBar")
            }
        })

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        itemList = listOf(
            Item("Title 1", "Description 1"),
            Item("Title 2", "Description 2"),
            Item("Title 3", "Description 3"),
            // Add more items here
        )

        adapter = MyAdapter(itemList)
        recyclerView.adapter = adapter
    }

    private fun showCustomDialog(int: Int) {
        // 创建Dialog
        val dialog = Dialog(this)

        // 使用自定义布局
        val view = LayoutInflater.from(this).inflate(int, null)
        dialog.setContentView(view)
        // 设置Dialog属性（如宽高、背景、取消按钮等）
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.show() // 显示Dialog

        val dialogButton: Button = view.findViewById(R.id.dialogButtonDismiss)
        dialogButton.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Dialog Dismiss", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buttonClick(string: String, button: Button){
        // 随机选择一个颜色
        val random = Random()
        val randomColor: Int = colors[random.nextInt(colors.size)]
        // 设置按钮背景颜色
        button.setBackgroundColor(randomColor)
    }

}
