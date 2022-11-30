package com.example.lesson26.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson26.R
import com.example.lesson26.TIME_FORMAT_SHOW
import com.example.lesson26.callbacks.DiffUtilsTrackCallBack
import com.example.lesson26.databinding.ItemTrackBinding
import com.example.lesson26.interfaes.TrackListener
import com.example.lesson26.models.Track
import com.example.lesson26.utils.getFormattedDate
import com.example.lesson26.utils.getFormattedDistance
import com.example.lesson26.utils.getFormattedTime

class TrackAdapter(
    private val trackListener: TrackListener,
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private var trackList = listOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, trackListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val trackItem = trackList[position]
        holder.bind(trackItem)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun setListTrack(listTrack: List<Track>) {
        val result = DiffUtil.calculateDiff(
            DiffUtilsTrackCallBack(trackList, listTrack), true
        )

        trackList = listTrack
        result.dispatchUpdatesTo(this)
    }

    class TrackViewHolder(
        private val binding: ItemTrackBinding,
        private val trackListener: TrackListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val resources = binding.root.context.resources

        fun bind(trackItem: Track) {

            binding.date.text = resources.getString(
                R.string.date_start,
                getFormattedDate(trackItem.beginsAt)
            )

            binding.distance.text = resources.getString(
                R.string.distance,
                getFormattedDistance(trackItem.distance)
            )

            binding.timeJogging.text = resources.getString(
                R.string.running_time,
                getFormattedTime(trackItem.time, TIME_FORMAT_SHOW)
            )

            binding.oneItem.setOnClickListener {
                trackListener.onTrackClick(trackItem)
            }
        }
    }
}