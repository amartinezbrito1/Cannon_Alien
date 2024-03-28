package com.example.app5

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import java.util.Timer
import java.util.TimerTask
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {

    private var timer = Timer()
    private var time : Float = 0f

    companion object {
        private var instance: MainActivity? = null
        public fun getInstance(): MainActivity {
            return instance!!
        }

        public var run: Boolean = false

        public var speed = 0
        public var score = 0
        public var shot = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (supportActionBar != null)
            supportActionBar?.hide() //hide tittle bar
        instance = this
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //settingle the handler
        var handler = Handler()
        var seekBar = findViewById<SeekBar>(R.id.seekBar)
        var seekBar2 = findViewById<SeekBar>(R.id.seekBar2)
        var button = findViewById<Button>(R.id.button)

        seekBar.setOnSeekBarChangeListener(handler)
        seekBar2.setOnSeekBarChangeListener(handler)
        button.setOnClickListener(handler)

        var timerTask = TimerObject()
        timer.schedule(timerTask, 0, 25)

    }

    inner class Handler : View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        DialogInterface.OnClickListener {
        override fun onClick(p0: View?) {
            var shots = findViewById<TextView>(R.id.shots)
            shot++
            shots.setText(shot.toString())
            //reseting timer
            time = 0f
            run = true
        }

       override fun onClick(p0: DialogInterface?, p1: Int) {
            var shots = findViewById<TextView>(R.id.shots)
            var scoreCount = findViewById<TextView>(R.id.scoreCount)

            if (p1 == DialogInterface.BUTTON_POSITIVE) {
                MyView.reset()
                shot = 0
                score = 0
                shots.setText(shot.toString())
                scoreCount.setText(score.toString())
                p0?.dismiss()
            }
        }

        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            var seekBar = findViewById<SeekBar>(R.id.seekBar)
            var seekBar2 = findViewById<SeekBar>(R.id.seekBar2)
            var angle = findViewById<TextView>(R.id.angle)
            var velocity =  findViewById<TextView>(R.id.velocity)

            if (p0 == seekBar) {
                MyView.setCAngle(p1.toFloat())
                //MyView.setCAngle(p1)
               angle.setText(p1.toString())
                MyView.getInstance().invalidate()
            }
            else if (p0 == seekBar2) {
                speed = p1
                velocity.setText(p1.toString())
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {

            var fhfh = 3
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {

            var fhfh = 3
        }
    }

    class HelperThread : Runnable
    {
        override fun run()
        {
            MainActivity.getInstance().update()
        }
    }
    class TimerObject : TimerTask()
    {
        override fun run()
        {
            var helper = HelperThread()

            MainActivity.getInstance().runOnUiThread(helper)
        }
    }


    public fun update() {
        var scoreCount = findViewById<TextView>(R.id.scoreCount)
        scoreCount.setText(score.toString())
        //once fire
        time+=0.2f
        if (run) {
            if (MyView.getStart() == true) {
                val ballCoords = MyView.getBallCoords()
                val angleRad = (MyView.getCAngle() * (Math.PI / 180))

                val cannonBase = MyView.GetCannonBase()
                val height = 5
                val width = 1
                //MyView.setCannon((height*Math.cos(angleRad)) as Float, (cannonBase.y + height*Math.cos(angleRad)) as Float, cannonBase.x, cannonBase.y )

                val dx = Math.sin(angleRad) + 0.5f
                val dy = Math.cos(angleRad*time) //time increases every frame
                val x1 =
                    (ballCoords.left + dx * speed).toInt()//(ballCoords.left + ((speed) * 1 * Math.cos(angleRad))).toInt()
                val y1 =
                    (ballCoords.top - (dy * speed)).toInt() //(ballCoords.top + ((speed) * 1 * Math.sin(angleRad))).toInt()
                val x2 =
                    (ballCoords.right + dx * speed).toInt()//(ballCoords.right + ((speed) * 1 * Math.cos(angleRad))).toInt()
                val y2 =
                    (ballCoords.bottom - (dy * speed)).toInt()//(ballCoords.bottom + ((speed) * 1 * Math.sin(angleRad))).toInt()

                MyView.setBallCoords(x1, y1, x2, y2)
                println("($x1,$y1,$x2,$y2)")

                if ((y1 <= 0 || y1 > MyView.height1) || (x1 > MyView.width1)) {
                    MyView.setStart(false)
                    val cannon = MyView.getCannon()
                    MyView.setBallCoords(
                        cannon.left.toInt(),
                        cannon.top.toInt() - 40,
                        cannon.right.toInt() + 10,
                        cannon.top.toInt()
                    )

                    run = false
                    println("boundary hit")
                }
                if (MyView.isOver()) {
                    //Display alert box
                    var handler = Handler()
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.setMessage("Game Over!")
                    dialogBuilder.setPositiveButton("OK", handler)
                    val alert1 = dialogBuilder.create()
                    alert1.setTitle("Game Over!")
                    alert1.show()
                }

                MyView.getInstance().invalidate()

            } else {
                //getting coords
                var ballCoords = MyView.getBallCoords()
                val centerx1 = ballCoords.left
                val centery1 = ballCoords.top + 100
                val centerx2 = ballCoords.right
                val centery2 = ballCoords.bottom + 100
                    //converting to radians from degrees
                val angleRad = MyView.getCAngle() * (Math.PI / 180)
                //rotation
                val x1 =
                    (Math.cos(angleRad) * (ballCoords.left - centerx1) - Math.sin(angleRad) * (ballCoords.top - centery1) + centerx1).toInt()
                val y1 =
                    (Math.sin(angleRad) * (ballCoords.left - centerx1) + Math.cos(angleRad) * (ballCoords.top - centery1) + centery1).toInt()
                val x2 =
                    (Math.cos(angleRad) * (ballCoords.right - centerx2) - Math.sin(angleRad) * (ballCoords.bottom - centery2) + centerx2).toInt()//ballCoords.right + ratio
                val y2 =
                    (Math.sin(angleRad) * (ballCoords.right - centerx2) + Math.cos(angleRad) * (ballCoords.bottom - centery2) + centery2).toInt()//ballCoords.bottom + ratio
                MyView.setBallCoords(x1, y1, x2, y2)
                MyView.setStart(true)
                MyView.getInstance().invalidate()
            }
        }
    }
}