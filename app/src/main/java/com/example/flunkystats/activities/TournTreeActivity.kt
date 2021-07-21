package com.example.flunkystats.activities

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.example.flunkystats.R
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.models.ListMatchModel
import com.example.flunkystats.ui.util.LoadsData
import com.example.flunkystats.util.DPconvertion
import com.example.flunkystats.util.DPconvertion.toDP
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min


class TournTreeActivity: AppCompatActivity(), LoadsData {

    lateinit var bitmap: Bitmap
    lateinit var canvas: Canvas
    lateinit var drawable: Drawable
    lateinit var treeLayout: ConstraintLayout
    lateinit var pgsBar: ProgressBar
    lateinit var toolbar: Toolbar
    private var yOffset = 0
    private val matchCardList = arrayListOf<CardView>()
    private val paint = Paint().apply {
            // Smooths out edges of what is drawn without affecting shape.
            isAntiAlias = true
            // Dithering affects how colors with higher-precision than the device are down-sampled.
            isDither = true
            style = Paint.Style.STROKE // default: FILL
            strokeJoin = Paint.Join.ROUND // default: MITER
            strokeCap = Paint.Cap.BUTT // default: BUTT

        }

    var paddingShort = 1
    var paddingMid = 1
    var paddingLong = 1


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tourn_tree)


        treeLayout = findViewById<ConstraintLayout>(R.id.cl_tourn_tree)

        pgsBar = addProgressBar(findViewById<ConstraintLayout>(R.id.cl_tourn_root), this)
        pgsBar.visibility = View.VISIBLE

        loadTournament("-MKosJyZqNFKkDyD2FI2")

        paint.strokeWidth =  3F.toDP(this)
        paint.color = ContextCompat.getColor(this, R.color.grey)

        toolbar = findViewById<Toolbar>(R.id.toolbar)

        findViewById<FloatingActionButton>(R.id.fab_test).setOnClickListener {


            paddingMid = (paddingMid * 0.9).toInt()
            paddingShort = (paddingShort * 0.9).toInt()
            paddingLong = (paddingLong * 0.9).toInt()

            treeLayout.scaleX *= 0.9F
            treeLayout.scaleY *= 0.9F

        }


        paddingShort = 32.toDP(this)
        paddingMid = 128.toDP(this)
        paddingLong = 128.toDP(this)

    }

    private fun loadTournament(tournID: String) {
        val asyncTask = DatabaseAsyncTask(this)
        asyncTask.execute(tournID)
    }

    private fun buildTournTree(matchListData: List<ListMatchModel>) {

        for(i in 0 until 12) {
            addMatchCard(matchListData[i], -1, (i-1), -1)
        }

        addMatchCard(matchListData[12], 0, 0, 1)
        addMatchCard(matchListData[13], 0, 2, 3)
        addMatchCard(matchListData[14], 0, 4, 5)
        addMatchCard(matchListData[15], 0, 6, 7)

        addMatchCard(matchListData[16], 11, 10, -1)
        addMatchCard(matchListData[17], 10, -1, 16)
        addMatchCard(matchListData[18], 9, -1, 17)
        addMatchCard(matchListData[19], 8, -1, 18)

        addMatchCard(matchListData[20], 16, 17, 16)
        addMatchCard(matchListData[21], 19, 19, 18)

        addMatchCard(matchListData[22], 21, 12, 13)
        addMatchCard(matchListData[23], 21, 14, 15)

        addMatchCard(matchListData[24], 21, 19, 18)
        addMatchCard(matchListData[25], 20, 17, 16)

        addMatchCard(matchListData[26], 24, 24, 25)

        addMatchCard(matchListData[27], 26, 22, 23)

        addMatchCard(matchListData[28], 26, 24, 25)

        addMatchCard(matchListData[29], 28, 27, 28)


        treeLayout.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->

            bitmap = Bitmap.createBitmap(treeLayout.width, treeLayout.height, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap)
            canvas.drawColor(ContextCompat.getColor(this, R.color.backgroundMidDark))
            drawable = BitmapDrawable(resources, bitmap)
            treeLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            treeLayout.background = drawable

            yOffset = -toolbar.height

            addLine(matchCardList[0], matchCardList[12])
            addLine(matchCardList[12], matchCardList[22])
            addLine2(matchCardList[1], matchCardList[12])
            addLine2(matchCardList[13], matchCardList[22])

            addLine(matchCardList[27], matchCardList[29])
            addLine2(matchCardList[28], matchCardList[29])



            pgsBar.visibility = View.GONE
        }


    }

    private fun addMatchCard(data: ListMatchModel, startIndex: Int, topIndex: Int, botIndex: Int) {
        val matchCard = CardView.inflate(this, R.layout.inflatable_tourn_tree_match, null) as CardView


        matchCard.id = View.generateViewId()
        matchCard.tag = data.matchNumb
        matchCard.findViewById<TextView>(R.id.tv_tourn_tree_match_numb).text = data.matchNumb.toString()
        val tv1 = matchCard.findViewById<TextView>(R.id.tv_tourn_tree_match_t1)
        tv1.text = data.team1Name
        val tv2 = matchCard.findViewById<TextView>(R.id.tv_tourn_tree_match_t2)
        tv2.text = data.team2Name


        val crown = ContextCompat.getDrawable(this, R.drawable.ic_crown)
        crown?.setTint(ContextCompat.getColor(this, R.color.colorPrimary))
        if(data.winnerID == data.team1ID) {
            tv1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, crown, null)
        } else {
            tv2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, crown, null)
        }


        treeLayout.addView(matchCard)

        //center it
        val constSet = ConstraintSet()

        constSet.constrainHeight(matchCard.id, ConstraintSet.WRAP_CONTENT)
        constSet.constrainWidth(matchCard.id, ConstraintSet.WRAP_CONTENT)

        if (startIndex == -1) {
            constSet.connect(matchCard.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        } else {
            constSet.connect(matchCard.id, ConstraintSet.START, matchCardList[startIndex].id, ConstraintSet.END, paddingMid)
        }

        if(botIndex != -1 && topIndex != -1) {
            constSet.connect(matchCard.id, ConstraintSet.BOTTOM, matchCardList[botIndex].id, ConstraintSet.TOP)
            constSet.connect(matchCard.id, ConstraintSet.TOP, matchCardList[topIndex].id, ConstraintSet.BOTTOM)
        } else if (botIndex == -1 && topIndex != -1) {
            val padding = if (topIndex == 7 ) paddingLong else paddingShort
            constSet.connect(matchCard.id, ConstraintSet.TOP, matchCardList[topIndex].id, ConstraintSet.BOTTOM, padding)
        } else if (botIndex != -1 && topIndex == -1) {
            constSet.connect(matchCard.id, ConstraintSet.BOTTOM, matchCardList[botIndex].id, ConstraintSet.TOP, paddingShort)
        } else { //(botIndex == -1 && topIndex == -1)
            constSet.connect(matchCard.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        }

        constSet.applyTo(treeLayout)

        val layoutParams = matchCard.layoutParams
        layoutParams.width = 125.toDP(this)
        layoutParams.height = 50.toDP(this)
        matchCard.layoutParams = layoutParams

        matchCardList.add(matchCard)
    }

    private fun addLine(card1: CardView, card2: CardView) {

        val rect1 = Rect()
        card1.getDrawingRect(rect1)
        treeLayout.offsetDescendantRectToMyCoords(card1, rect1)
        val rect2 = Rect()
        card2.getDrawingRect(rect2)
        treeLayout.offsetDescendantRectToMyCoords(card2, rect2)

        val path = Path()
        val rect1MidY = rect1.top + (rect1.height() / 2)
        val rect2MidY = rect2.top + (rect2.height() / 2)
        val midX = rect1.right + (rect2.left-rect1.right)/2
        val cornerRadius = 12.toDP(this)
        val line1EndX = midX - cornerRadius
        val line2StartY = rect1MidY + cornerRadius
        val line2EndY= rect2MidY - cornerRadius
        val line3StartX = midX + cornerRadius

        path.moveTo(rect1.right.toFloat(), rect1MidY.toFloat())
        path.lineTo(line1EndX.toFloat(), rect1MidY.toFloat())
        path.arcTo(line1EndX.toFloat()- cornerRadius,
            rect1MidY.toFloat(),
            midX.toFloat(),
            line2StartY.toFloat() + cornerRadius,
            270F, 90F, true)
        path.lineTo(midX.toFloat(), line2EndY.toFloat())
        path.arcTo(midX.toFloat(),
            line2EndY.toFloat()- cornerRadius,
            line3StartX.toFloat() + cornerRadius,
            rect2MidY.toFloat(),
            180F, -90F, true)
        path.lineTo(rect2.left.toFloat(), rect2MidY.toFloat())

        canvas.drawPath(path, paint)
    }

    private fun addLine2(card1: CardView, card2: CardView) {

        val rect1 = Rect()
        card1.getDrawingRect(rect1)
        treeLayout.offsetDescendantRectToMyCoords(card1, rect1)
        val rect2 = Rect()
        card2.getDrawingRect(rect2)
        treeLayout.offsetDescendantRectToMyCoords(card2, rect2)

        val path = Path()
        val rect1MidY = rect1.top + (rect1.height() / 2)
        val rect2MidY = rect2.top + (rect2.height() / 2)
        val midX = rect1.right + (rect2.left-rect1.right)/2
        val cornerRadius = 12.toDP(this)
        val line1EndX = midX - cornerRadius
        val line2StartY = rect1MidY - cornerRadius
        val line2EndY= rect2MidY + cornerRadius
        val line3StartX = midX + cornerRadius

        path.moveTo(rect1.right.toFloat(), rect1MidY.toFloat())
        path.lineTo(line1EndX.toFloat(), rect1MidY.toFloat())
        path.arcTo(line1EndX.toFloat() - cornerRadius,
            line2StartY.toFloat() - cornerRadius,
            midX.toFloat(),
            rect1MidY.toFloat() ,
            90F, -90F, true)
        path.lineTo(midX.toFloat(), line2EndY.toFloat())
        path.arcTo(midX.toFloat(),
            rect2MidY.toFloat(),
            line3StartX.toFloat() + cornerRadius,
            line2EndY.toFloat() + cornerRadius,
            180F, 90F, true)
        path.lineTo(rect2.left.toFloat(), rect2MidY.toFloat())

        canvas.drawPath(path, paint)
    }


    private class DatabaseAsyncTask(activity: TournTreeActivity): AsyncTask<String, Void, List<ListMatchModel>>() {

        private var activityWeakReference: WeakReference<TournTreeActivity>? = null

        init {
            activityWeakReference = WeakReference<TournTreeActivity>(activity)
        }

        override fun doInBackground(vararg tournID: String?): List<ListMatchModel> {

            if (tournID.isEmpty() || tournID[0] == null) return arrayListOf()

            val activity: TournTreeActivity? = activityWeakReference?.get()
            if (activity == null || activity.isFinishing) {
                return arrayListOf()
            }

            val dbHelper = DataBaseHelper(activity as Context)

            var resList = dbHelper.getMatchListData(tournID[0]!!)

            val resListSorted = resList.sortedBy { it.matchNumb }

            return resListSorted
        }

        override fun onPostExecute(result: List<ListMatchModel>?) {
            super.onPostExecute(result)

            val activity: TournTreeActivity? = activityWeakReference?.get()
            if (activity == null || activity.isFinishing) {
                return
            }
            if(result != null) {
                activity.buildTournTree(result)
            }
        }
    }


}