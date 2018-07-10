package com.anwesh.uiprojects.linkeddectriwaveview

/**
 * Created by anweshmishra on 10/07/18.
 */

import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.MotionEvent

val DTW_NODES : Int = 5

class LinkedDecTriWaveView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class DTWState(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += dir * 0.1f
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class DTWNode(var i : Int, val state : DTWState = DTWState()) {

        var next : DTWNode? = null

        var prev : DTWNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < DTW_NODES - 1) {
                next = DTWNode(i + 1)
                next?.prev = this
            }
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            state.update {
                stopcb(i, it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = Math.min(w, h) / 60
            val sc1 : Float = Math.min(0.5f, state.scale) * 2
            val sc2 : Float = Math.min(0.25f, Math.max(0f, state.scale - 0.5f)) * 4
            val sc3 : Float = Math.min(0.25f, Math.max(0f, state.scale - 0.75f)) * 4
            paint.strokeWidth = Math.min(w, h) / 60
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = Color.parseColor("#e74c3c")
            canvas.save()
            canvas.translate(i * gap, h/2)
            canvas.drawLine(0f, 0f, (gap / 2) * sc1, 0f, paint)
            canvas.drawLine(gap/2, 0f, gap/2 + (gap/4) * sc2, -gap/4 * sc2, paint)
            canvas.drawLine(gap / 2 + gap / 4, -gap/4, gap/2 + gap/4 + gap /4 * sc3, -gap/4 + gap/4 * sc3, paint)
            canvas.restore()
            next?.draw(canvas, paint)
        }

        fun getNext(dir : Int, cb : () -> Unit) : DTWNode {
            var curr : DTWNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedDTW(var i : Int) {

        private var curr : DTWNode = DTWNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            curr.update {j, scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LinkedDecTriWaveView) {

        private val dtw : LinkedDTW = LinkedDTW(0)

        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            dtw.draw(canvas, paint)
            animator.animate {
                dtw.update {j, scale ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            dtw.startUpdating {
                animator.start()
            }
        }
    }
}
