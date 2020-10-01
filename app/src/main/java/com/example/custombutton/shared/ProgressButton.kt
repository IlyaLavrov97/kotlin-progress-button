package com.example.custombutton.shared

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.custombutton.R


class ProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var progressDrawable: TestAnimatedDrawable =
        TestAnimatedDrawable(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                5f,
                resources.displayMetrics
            ),
            ContextCompat.getColor(context, R.color.colorAccent)
        ).apply {
            callback = this@ProgressButton
        }

    var isLoading: Boolean = false
    private var buttonText: String = text.toString()

    override fun verifyDrawable(who: Drawable): Boolean {
        if (who == progressDrawable) {
            return progressDrawable.isRunning
        }
        return super.verifyDrawable(who)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val progressRadius = height / 4
        progressDrawable.setBounds(
            width / 2 - progressRadius,
            height / 2 - progressRadius,
            width / 2 + progressRadius,
            height / 2 + progressRadius
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isLoading) {
            drawProgress(canvas)
        } else {
            progressDrawable.stop()
        }
    }

    private fun drawProgress(canvas: Canvas) {
        if (progressDrawable.isRunning) {
            progressDrawable.draw(canvas)
        } else {
            progressDrawable.start()
        }
    }

    fun startLoading() {
        isLoading = true
        buttonText = text.toString()
        text = ""
        invalidate()
    }

    fun stopLoading() {
        isLoading = false
        text = buttonText
        invalidate()
    }
}