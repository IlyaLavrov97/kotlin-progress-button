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

class CircularAnimatedDrawable(
    private val borderWidth: Float,
    arcColor: Int
) : Drawable(), Animatable {
    private val angleInterpolator: Interpolator = LinearInterpolator()
    private val sweepInterpolator: Interpolator = DecelerateInterpolator()
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

    private val objectAnimatorSweep: ObjectAnimator = ObjectAnimator.ofFloat(
        this,
        ::currentSweepAngle.name,
        FULL_CIRCLE_DEGREE - MIN_SWEEP_ANGLE * 2
    ).apply {
        interpolator = sweepInterpolator
        duration = SWEEP_ANIMATOR_DURATION
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
    }

    private val sweepAnimatorListener = object : AnimatorListener {
        override fun onAnimationStart(animation: Animator) = Unit
        override fun onAnimationEnd(animation: Animator) = Unit
        override fun onAnimationCancel(animation: Animator) = Unit
        override fun onAnimationRepeat(animation: Animator) {
            toggleAppearingMode()
        }
    }

    private var modeAppearing = false
    private var currentGlobalAngleOffset = 0f

    private var currentGlobalAngle = 0f
        set(value) {
            field = value
            invalidateSelf()
        }
    private var currentSweepAngle = 0f
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

    private fun toggleAppearingMode() {
        modeAppearing = !modeAppearing
        if (modeAppearing) {
            currentGlobalAngleOffset = (currentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        fBounds.left = bounds.left + borderWidth / 2f
        fBounds.right = bounds.right - borderWidth / 2f
        fBounds.top = bounds.top + borderWidth / 2f
        fBounds.bottom = bounds.bottom - borderWidth / 2f
    }

    override fun draw(canvas: Canvas) {
        var startAngle = currentGlobalAngle - currentGlobalAngleOffset
        var sweepAngle = currentSweepAngle
        if (!modeAppearing) {
            startAngle += sweepAngle
            sweepAngle = FULL_CIRCLE_DEGREE - sweepAngle - MIN_SWEEP_ANGLE
        } else {
            sweepAngle += MIN_SWEEP_ANGLE.toFloat()
        }
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, paint)
    }

    override fun start() {
        if (isRunning) {
            return
        }
        running = true
        objectAnimatorSweep.run {
            addListener(sweepAnimatorListener)
            start()
        }
        objectAnimatorAngle.start()
        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning) {
            return
        }
        running = false
        objectAnimatorSweep.run {
            removeListener(sweepAnimatorListener)
            cancel()
        }
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
        private const val SWEEP_ANIMATOR_DURATION = 1000L
        const val MIN_SWEEP_ANGLE = 30
        const val FULL_CIRCLE_DEGREE = 360f
    }
}