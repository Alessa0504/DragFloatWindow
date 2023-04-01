package com.example.dragfloatwindowdemo.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.RelativeLayout
import com.example.dragfloatwindowdemo.util.ScreenUntil
import kotlin.math.abs

/**
 * @Description: 可贴边吸附的悬浮窗
 * @Author: zouji
 * @CreateDate: 2023/3/31 16:00
 */
class FloatView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet) :
    RelativeLayout(context, attributeSet), OnTouchListener {

    private var lastX = 0
    private var lastY = 0

    //悬浮的父布局高度
    private var parentWidth: Int? = null
    private var parentHeight: Int? = null

    //最小拖拽距离
    private val MIN_DISTANCE = ScreenUntil.dip2px(context, 3f)

    fun setView(floatView: View, startX: Int, startY: Int) {
        addView(floatView)
        this.x = startX.toFloat()
        this.y = startY.toFloat()
        setOnTouchListener(this@FloatView)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        parentWidth = (parent as ViewGroup).width   //悬浮的父布局高度
        parentHeight = (parent as ViewGroup).height
        val view = getChildAt(0)  //获取子view
        view.layout(0, 0, view.width, view.height)  //子视图相对与父视图中的位置
    }


    /**
     * 处理子view有点击事件造成的冲突
     * @param event
     * @return
     */
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        val action = event!!.action
        //获取相对屏幕位置
        val rawX = event.rawX.toInt()
        val rawY = event.rawY.toInt()
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = rawX
                lastY = rawY
            }
            // 做滑动区间判断, 否则很难点击悬浮窗
            MotionEvent.ACTION_MOVE -> {
                val dx = rawX - lastX
                val dy = rawY - lastY
                // 视为点击, 不拦截
                if ((abs(dx) < MIN_DISTANCE) && (abs(dy) < MIN_DISTANCE)) {
                    return false
                }
                return true
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    /**
     * 重写拖拽事件
     * @param view
     * @param event
     * @return
     */
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        //获取相对屏幕位置
        val rawX = event!!.rawX.toInt()
        val rawY = event.rawY.toInt()
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_MOVE -> {
                val dx = rawX - lastX
                val dy = rawY - lastY
                var x = this.x + dx   //this.x, this.y是该控件相对于父控件的坐标
                var y = this.y + dy
                // 检测是否到达边缘 左上右下
                x = if (x < 0) 0f else if (x > parentWidth!! - this.width) {
                    (parentWidth!! - this.width).toFloat()
                } else x
                y = if (y < 0) 0f else if (y > parentHeight!! - this.height) {
                    (parentHeight!! - this.height).toFloat()
                } else y
                setX(x)
                setY(y)
                lastX = rawX
                lastY = rawY
            }
            MotionEvent.ACTION_UP -> {
                moveHide(rawX, rawY)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun moveHide(rawX: Int, rawY: Int) {
        var oaX: ObjectAnimator? = null
        oaX = if (rawX >= ScreenUntil.widthPixels / 2) {   //靠右吸附
            ObjectAnimator.ofFloat(
                this@FloatView,
                "x",
                this.x,
                parentWidth!! - this.width.toFloat()
            )
        } else {   //靠左吸附
            ObjectAnimator.ofFloat(this@FloatView, "x", this.x, 0f)
        }
        oaX.interpolator = BounceInterpolator()  //回弹差值器
        oaX.duration = 500
        oaX.start()
    }

}