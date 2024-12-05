package com.nqmgaming.lab6_minhnqph31902.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nqmgaming.lab6_minhnqph31902.R
import com.nqmgaming.lab6_minhnqph31902.model.Distributor

class DistributorAdapter(
    private var onDelete: (Distributor) -> Unit,
    private val onUpdate: (Distributor) -> Unit
) : RecyclerView.Adapter<DistributorAdapter.DistributorViewHolder>() {

    private var distributorList = emptyList<Distributor>()

    inner class DistributorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(distributor: Distributor) {
            itemView.findViewById<TextView>(R.id.name_tv).text = distributor.name.toString()
            itemView.findViewById<ImageButton>(R.id.delete_btn).setOnClickListener {
                onDelete(distributor)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistributorViewHolder {
        return DistributorViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_distributor, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return distributorList.size
    }

    override fun onBindViewHolder(holder: DistributorViewHolder, position: Int) {
        holder.onBind(distributorList[position])
        val currentItem = distributorList[position]
        holder.itemView.setOnClickListener {
            onUpdate(currentItem)
        }
    }

    fun setData(distributor: List<Distributor>) {
        this.distributorList = distributor
        notifyDataSetChanged()
    }
}
