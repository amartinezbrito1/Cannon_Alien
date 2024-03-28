package com.example.app5

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.Image
import android.widget.ImageView

class MyView: View {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var path: Path = Path()
    var ballImage = ImageView(MainActivity.getInstance())


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        instance = this
        ballImage.setImageResource(R.drawable.cannon_ball)
    }

    companion object {
        private var instance: MyView? = null
        public fun getInstance(): MyView {
            return instance!!
        }

        //initializing float 0.0
        private var Cangle: Float = 0f
        private var cannon: RectF = RectF(0f, 0f, 0f, 0f)

        //rect with floating point precision
        private var ballCoords: Rect = Rect(0, 0, 0, 0)
        private var start: Boolean = false

        //getters and setters
        public fun GetCannonBase() :ImageView
        {
            return MainActivity.getInstance().findViewById<ImageView>(R.id.cannon_base)
        }

        //public fun setCAngle(x: Float) {
        public fun setCAngle(x: Float) {
           this.Cangle = x
        }

        public fun getCAngle(): Float {
            return Cangle
        }

        public fun setCannon(ux: Float, uy: Float, lx: Float, ly: Float) {
            this.cannon.set(ux, uy, lx, ly)
        }

        public fun getCannon(): RectF {
            return cannon
        }

        public fun setBallCoords(ux: Int, uy: Int, lx: Int, ly: Int) {
            this.ballCoords.set(ux, uy, lx, ly)
        }

        public fun getBallCoords(): Rect {
            return ballCoords
        }

        public fun setStart(x: Boolean) {
            this.start = x
        }

        public fun getStart(): Boolean {
            return start
        }

        public var width1: Int = 0
        public var height1: Int = 0
        public var targets = ArrayList<Drawable>()

        public fun reset() {
            var start_x = (0.5 * width1).toInt()
            var start_y = (0.2 * height1).toInt()
            var offset = (0.1 * height1).toInt()
            var x: Int
            var y = start_y
            for (i in 0..6) {
                x = start_x
                for (j in 0..6) {
                    var imageView = ImageView(MainActivity.getInstance())
                    //untouched.png is the alien
                    imageView.setImageResource(R.drawable.untouched)
                    var drawable = imageView.getDrawable()
                    drawable.setBounds(x, y, x + offset, y + offset) //Sets the dimensions
                    targets.add(drawable)  //stores away the image
                    x += offset
                }
                y += offset
            }
        }

        public fun isOver(): Boolean {
            return targets.isEmpty()
        }


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        var width = this.width
        var height = this.height

        width1 = width
        height1 = height

        var start_x = (0.5 * width).toInt()
        var start_y = (0.2 * height).toInt()
        var offset = (0.1 * height).toInt()
        Cangle = 0f

        //cannon = RectF(45f,490f,65f,570f)
        val cannonBase = MyView.GetCannonBase()
        val cannonHeight : Float = 100f
        val cannonWidth : Float = 40f
        //MyView.setCannon(40f, 40f, 40f, 40f)
        val debug = cannonBase.bottom
        cannon = RectF(((cannonBase.x + cannonBase.measuredWidth/2) - cannonWidth/2) as Float, 800 - cannonHeight, ((cannonBase.x + cannonBase.measuredWidth/2) + cannonWidth/2) as Float, 800f + cannonBase.measuredHeight/2)


        ballCoords = Rect(cannon.left.toInt(),cannon.top.toInt()-40,cannon.right.toInt()+10,cannon.top.toInt())

        var x : Int
        var y = start_y
        for (i in 0..6) {
            x = start_x
            for (j in 0..6) {
                var imageView = ImageView(MainActivity.getInstance())
                imageView.setImageResource(R.drawable.untouched)
                var drawable = imageView.getDrawable()
                drawable.setBounds(x, y, x + offset, y + offset) //Sets the dimensions
                targets.add(drawable)  //stores away the image
                x += offset
            }
            y += offset
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var drawable : Drawable
        paint.setColor(Color.BLACK)
        path.addRect(cannon,Path.Direction.CW)
        canvas.save()
        canvas.rotate(Cangle,cannon.right, cannon.bottom)
        canvas.drawPath(path,paint)
        canvas.restore()
        if (start) {
            drawable = ballImage.getDrawable()
            drawable.setBounds(ballCoords)
            drawable.draw(canvas)

            for (i in 0..targets.size-1) {
                if (i >= targets.size)
                    break
                var target = targets[i].getBounds()
                if ((ballCoords.left > target.left) && (ballCoords.top > target.top) &&
                    (ballCoords.left < target.right) && (ballCoords.top < target.bottom)) {
                    targets.removeAt(i)
                    MainActivity.score++
                }
            }
        }

        for (i in 0..targets.size-1)
        {
            drawable = targets.get(i)
            drawable.draw(canvas)
        }

    }




}
