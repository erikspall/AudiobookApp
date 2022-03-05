package de.erikspall.audiobookapp.ui.library.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class AudiobookItemAnimator : DefaultItemAnimator() {
    private val pendingProgressChanges = arrayListOf<progressChangeInfo>()

    private inner class progressChangeInfo(
        val oldProgress: Int,
        val newProgress: Int,
        val oldHolder: AudioBookCardAdapter.GridCardViewHolder,
        val newHolder: AudioBookCardAdapter.GridCardViewHolder
    )

    override fun runPendingAnimations() {
        for (info in pendingProgressChanges)
            animateChangeImpl(info)
        super.runPendingAnimations()
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        // Log.d("I dont even know", "Heyho")


        // If it is the same don't animate anything
        if (newHolder is AudioBookCardAdapter.GridCardViewHolder &&
            oldHolder is AudioBookCardAdapter.GridCardViewHolder
        ) {
           // Log.d("I dont even know", "Heyho")
            pendingProgressChanges.add(
                progressChangeInfo(
                    oldHolder.book_progress_indicator.progress,
                    newHolder.book_progress_indicator.progress,
                    oldHolder,
                    newHolder
                )
            )
            return true
        }
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY)

    }

    private fun animateChangeImpl(progressChangeInfo: progressChangeInfo) {
        dispatchChangeFinished(progressChangeInfo.oldHolder, true)
        ValueAnimator.ofInt(progressChangeInfo.oldProgress, progressChangeInfo.newProgress)
            .apply {
                duration = 300

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        dispatchChangeStarting(progressChangeInfo.newHolder, false)
                    }
                })

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        dispatchChangeFinished(progressChangeInfo.newHolder, false)
                    }
                })

                addUpdateListener { updatedAnimation ->
                    progressChangeInfo.newHolder.book_progress_indicator.progress =
                        updatedAnimation.animatedValue as Int
                }
                start()
            }
    }
}