package io.sahil.imagepreviewer.utils

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.graphics.PointF
import android.graphics.Bitmap

import android.graphics.drawable.BitmapDrawable





class CropView(context: Context): androidx.appcompat.widget.AppCompatImageView(context) {

    private val DRAG = 0
    private val LEFT = 1
    private val TOP = 2
    private val RIGHT = 3
    private val BOTTOM = 4

    var paint: Paint = Paint()
    private val initialSize = 300

    private var leftTop: Point = Point()
    private var rightBottom:Point = Point()
    private var center:Point = Point()
    private var previous:Point = Point()

    private var imageScaledWidth: Int = 0
    private var imageScaledHeight: Int = 0


    init {
        paint.color = Color.YELLOW
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(leftTop.equals(0, 0))
            resetPoints()
        canvas?.drawRect(leftTop.x.toFloat(),
            leftTop.y.toFloat(), rightBottom.x.toFloat(), rightBottom.y.toFloat(), paint)
    }

    private fun resetPoints() {
        center.set(width/2, height/2)
        leftTop.set((width-initialSize)/2, (height-initialSize)/2)
        rightBottom.set(leftTop.x + initialSize, leftTop.y + initialSize)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when(event?.action){

            MotionEvent.ACTION_DOWN -> {previous.set(event.x.toInt(), event.y.toInt())}

            MotionEvent.ACTION_MOVE -> {
                if (isActionInsideRectangle(event.x, event.y)){
                    adjustRectangle(event.x, event.y)
                    invalidate()
                    previous.set(event.x.toInt(), event.y.toInt())
                }
            }

            MotionEvent.ACTION_UP -> {
                previous = Point()
            }

        }

        return true
    }


    private fun isActionInsideRectangle(x: Float, y: Float): Boolean {
        val buffer = 10
        return (x >= leftTop.x - buffer && x <= rightBottom.x + buffer && y >= leftTop.y - buffer && y <= rightBottom.y + buffer)
    }

    private fun adjustRectangle(x: Float, y: Float){

        when(getAffectedSide(x, y)){

            LEFT -> {
                val movement: Int = (x - leftTop.x).toInt()
                if (isInImageRange(PointF((leftTop.x + movement).toFloat(),
                        (leftTop.y + movement).toFloat()
                    ))){
                    leftTop.set(leftTop.x + movement, leftTop.y + movement)
                }
            }

            TOP -> {
                val movement: Int = (y - leftTop.y).toInt()
                if (isInImageRange(
                        PointF(
                            (leftTop.x + movement).toFloat(),
                            (leftTop.y + movement).toFloat()
                        )
                    )
                ) leftTop.set(leftTop.x+movement,leftTop.y+movement)
            }

            RIGHT -> {
                val movement: Int = (x-rightBottom.x).toInt()

                if (isInImageRange(
                        PointF(
                            (rightBottom.x + movement).toFloat(),
                            (rightBottom.y + movement).toFloat()
                        )
                    )
                ) rightBottom.set(rightBottom.x+movement,rightBottom.y+movement)
            }

            BOTTOM -> {
                val movement: Int = (y-rightBottom.y).toInt()
                if (isInImageRange(
                        PointF(
                            (rightBottom.x + movement).toFloat(),
                            (rightBottom.y + movement).toFloat()
                        )
                    )
                )rightBottom.set(rightBottom.x+movement,rightBottom.y+movement)
            }

            DRAG -> {
                val movement: Int = (x-previous.x).toInt()
                val movementY: Int = (y-previous.y).toInt()
                if (isInImageRange(
                        PointF(
                            (leftTop.x + movement).toFloat(),
                            (leftTop.y + movementY).toFloat()
                        )
                    ) && isInImageRange(
                        PointF(
                            (rightBottom.x + movement).toFloat(),
                            (rightBottom.y + movementY).toFloat()
                        )
                    )){
                    leftTop.set(leftTop.x+movement,leftTop.y+movementY)
                    rightBottom.set(rightBottom.x+movement,rightBottom.y+movementY)
                }
            }

        }

    }


    private fun getAffectedSide(x: Float, y: Float): Int {
        val buffer = 10
        return if (x >= leftTop.x - buffer && x <= leftTop.x + buffer) LEFT
        else if (y >= leftTop.y - buffer && y <= leftTop.y + buffer) TOP
        else if (x >= rightBottom.x - buffer && x <= rightBottom.x + buffer) RIGHT
        else if (y >= rightBottom.y - buffer && y <= rightBottom.y + buffer) BOTTOM
        else DRAG
    }


    private fun isInImageRange(point: PointF): Boolean {
        // Get image matrix values and place them in an array
        val f = FloatArray(9)
        imageMatrix.getValues(f)

        // Calculate the scaled dimensions
        imageScaledWidth = Math.round(drawable.intrinsicWidth * f[Matrix.MSCALE_X])
        imageScaledHeight = Math.round(drawable.intrinsicHeight * f[Matrix.MSCALE_Y])
        return (point.x >= center.x - imageScaledWidth/2 && point.x <= center.x + imageScaledWidth/2 && point.y >= center.y - imageScaledHeight/2 && point.y <= center.y + imageScaledHeight/2)
    }


    fun getCroppedImage(): Bitmap {
        val drawable = drawable as BitmapDrawable
        val x = (leftTop.x - center.x + drawable.bitmap.width / 2).toFloat()
        val y = (leftTop.y - center.y + drawable.bitmap.height / 2).toFloat()
        /*val stream = ByteArrayOutputStream()
        cropped.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()*/
        return Bitmap.createBitmap(
            drawable.bitmap,
            x.toInt(), y.toInt(), rightBottom.x - leftTop.x, rightBottom.y - leftTop.y
        )
    }





}