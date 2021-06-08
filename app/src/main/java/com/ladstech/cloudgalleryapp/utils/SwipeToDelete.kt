package com.ladstech.cloudgalleryapp.utils

import android.content.Context
import android.graphics.Canvas
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ladstech.cloudgalleryapp.R

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeToDelete(context: Context, dragDir:Int, swipeDir:Int) : ItemTouchHelper.SimpleCallback(dragDir,swipeDir) {

    val backgroundColor = ContextCompat.getColor(context, R.color.white)
    val labelColor= ContextCompat.getColor(context,R.color.brickRed)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        (viewHolder.itemView as CardView).cardElevation = 10F

/*.addSwipeLeftLabel("Delete")*/
        RecyclerViewSwipeDecorator.Builder(
            c,recyclerView,viewHolder, dX, dY, actionState, isCurrentlyActive
        )
            .addSwipeLeftBackgroundColor(backgroundColor)
            .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_forever_24)
            .setIconHorizontalMargin(5,5)
            .setSwipeLeftLabelTextSize(5,3F)

            .setSwipeLeftLabelColor(labelColor)
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}