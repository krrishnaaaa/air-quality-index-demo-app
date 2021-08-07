package com.pcsalt.example.airqualityindex.display.data

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pcsalt.example.airqualityindex.databinding.ItemAqiDataBinding
import com.pcsalt.example.airqualityindex.db.entity.AQIData
import com.pcsalt.example.airqualityindex.ext.getAQIBand
import com.pcsalt.example.airqualityindex.ext.getAQIColor
import com.pcsalt.example.airqualityindex.ext.toComparativeTime
import com.pcsalt.example.airqualityindex.ext.toTwoDecimal
import com.pcsalt.example.airqualityindex.util.Logger


class AQIDataAdapter : RecyclerView.Adapter<AQIDataAdapter.DataVH>() {

    private val dataList: ArrayList<AQIData> = ArrayList()
    var listener: OnItemClickListener? = null

    fun clear() {
        dataList.clear()
    }

    fun setData(newData: List<AQIData>) {
        if (this.dataList.isNotEmpty() && newData.isNotEmpty()) {
            var newItemAdded = false
            for (data in newData) {
                val idx = this.dataList.indexOf(data)
                Logger.d("idx $idx")
                if (idx > -1) {
                    val old = this.dataList[idx]
                    this.dataList[idx] = data
                    notifyItemChanged(idx, DataPayload(old, data))
                } else {
                    this.dataList.add(data)
                    newItemAdded = true
                }
            }
            if (newItemAdded) {
                notifyDataSetChanged()
            }
        } else {
            this.dataList.addAll(newData)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataVH {
        val binding = ItemAqiDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataVH(binding)
    }

    override fun onBindViewHolder(holder: DataVH, position: Int) {
        holder.initData(dataList[position])
    }

    override fun onBindViewHolder(holder: DataVH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                if (payload is DataPayload) {
                    holder.initData(payload.newData)
                    holder.notifyBandChange(payload.oldData.aqi, payload.newData.aqi)
                }
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class DataVH(var binding: ItemAqiDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun initData(data: AQIData) {
            binding.root.setOnClickListener {
                listener?.onClick(data)
            }
            binding.tvCityName.text = data.cityName
            binding.apply {
                tvCityName.text = data.cityName
                tvAqi.text = data.aqi.toTwoDecimal()
                tvLastUpdated.text = data.lastUpdated.toComparativeTime()
                tvAqi.setTextColor(ContextCompat.getColor(tvAqi.context, data.aqi.getAQIColor()))
            }
        }

        fun notifyBandChange(oldAQI: Double, newAQI: Double) {
            val change = checkBandChanged(oldAQI, newAQI)
            Logger.d("$oldAQI $newAQI $change")
            if (change == 1 || change == -1) {
                animateBgChange(oldAQI.getAQIColor(), newAQI.getAQIColor())
            }
        }

        private fun animateBgChange(colorStart: Int, colorEnd: Int) {
            val colorFrom: Int = ContextCompat.getColor(binding.tvAqi.context, colorStart)
            val colorTo: Int = ContextCompat.getColor(binding.tvAqi.context, colorEnd)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 950

            colorAnimation.addUpdateListener { animator -> binding.tvAqi.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    binding.tvAqi.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.tvAqi.context,
                            android.R.color.transparent
                        )
                    )
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationRepeat(animation: Animator?) {}
            })
            colorAnimation.start()
        }

        private fun checkBandChanged(oldAQI: Double, newAQI: Double): Int {
            val oldBand = oldAQI.getAQIBand()
            val newBand = newAQI.getAQIBand()
            return newBand - oldBand
        }
    }

    interface OnItemClickListener {
        fun onClick(item: AQIData)
    }
}

data class DataPayload(val oldData: AQIData, val newData: AQIData)
