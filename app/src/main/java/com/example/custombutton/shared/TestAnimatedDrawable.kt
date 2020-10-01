package com.example.custombutton.shared

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

class TestAnimatedDrawable(
    private val borderWidth: Float,
    arcColor: Int
) : Drawable(), Animatable {
    private val angleInterpolator: Interpolator = LinearInterpolator()
    private val fBounds = RectF()

    private val objectAnimatorAngle: ObjectAnimator = ObjectAnimator.ofFloat(
        this,
        ::currentGlobalAngle.name,
        FULL_CIRCLE_DEGREE
    ).apply {
        interpolator = angleInterpolator
        duration = ANGLE_ANIMATOR_DURATION
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
    }

    private var currentGlobalAngle = 0f
        set(value) {
            field = value
            invalidateSelf()
        }

    private var running = false

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
        color = arcColor
    }

    var percentRate = 60

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        fBounds.left = bounds.left + borderWidth / 2f
        fBounds.right = bounds.right - borderWidth / 2f
        fBounds.top = bounds.top + borderWidth / 2f
        fBounds.bottom = bounds.bottom - borderWidth / 2f
    }

    override fun draw(canvas: Canvas) {
        val startAngle = currentGlobalAngle
        val endAngle = (percentRate / 100F) * 360
        canvas.drawArc(fBounds, startAngle, endAngle, false, paint)
    }

    override fun start() {
        if (isRunning) {
            return
        }
        running = true
        objectAnimatorAngle.start()
        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning) {
            return
        }
        running = false
        objectAnimatorAngle.cancel()
        invalidateSelf()
    }

    override fun isRunning(): Boolean {
        return running
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    companion object {
        private const val ANGLE_ANIMATOR_DURATION = 1000L
        const val FULL_CIRCLE_DEGREE = 360f
    }
}