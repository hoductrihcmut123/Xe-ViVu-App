package com.example.xevivuapp.common.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.xevivuapp.data.TripDataDto
import com.example.xevivuapp.common.utils.Constants
import com.example.xevivuapp.common.utils.Utils.extractTime
import com.example.xevivuapp.common.utils.Utils.getHourAndMinute
import com.example.xevivuapp.databinding.LayoutItemTripsHistoryBinding
import com.example.xevivuapp.features.menu.trip_history.TripDetailActivity

class TripHistoryAdapter(private val context: Context) :
    RecyclerView.Adapter<TripHistoryAdapter.ViewHolder>() {

    private val mTripHistory by lazy { mutableListOf<TripDataDto>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            LayoutItemTripsHistoryBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mTripHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(mTripHistory[position])

        holder.itemView.setOnClickListener {
            val newItem = mTripHistory[position]

            val intent = Intent(context, TripDetailActivity::class.java)
            intent.putExtra("TRIP_DATA", newItem)
            context.startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(listData: List<TripDataDto>) {
        mTripHistory.clear()
        mTripHistory.addAll(listData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutItemTripsHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(newItem: TripDataDto) {
            binding.tvBookingTime.text = newItem.bookingTime?.extractTime()
            binding.tvCanceled.isVisible =
                !(newItem.status == Constants.COMPLETED || newItem.status == Constants.ARRIVE)
            binding.tvTimeStart.text = getHourAndMinute(newItem.startTime ?: "")
            binding.tvTimeEnd.text = getHourAndMinute(newItem.endTime ?: "")
            binding.tvOriginAddress.text = newItem.originAddress
            binding.tvDestinationAddress.text = newItem.destinationAddress
        }
    }
}
