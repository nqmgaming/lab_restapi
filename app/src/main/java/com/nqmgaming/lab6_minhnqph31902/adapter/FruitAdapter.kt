package com.nqmgaming.lab6_minhnqph31902.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nqmgaming.lab6_minhnqph31902.databinding.ItemFruitBinding
import com.nqmgaming.lab6_minhnqph31902.model.Fruit

class FruitAdapter(
    private val onDelete: (Fruit) -> Unit,
    private val onUpdate: (Fruit) -> Unit
) : RecyclerView.Adapter<FruitAdapter.FruitViewHolder>() {
    private var fruitList = emptyList<Fruit>()

    inner class FruitViewHolder(private val binding: ItemFruitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            fruit: Fruit,
        ) {
            Glide.with(binding.root)
                .load(fruit.image[0].url)
                .into(binding.fruitIv)
            binding.fruitNameTv.text = "Name: ${fruit.name}"
            binding.fruitPriceTv.text = "Price: ${fruit.price}"
            binding.fruitQuantityTv.text = "Quantity: ${fruit.quantity}"
            binding.fruitDescriptionTv.text = fruit.description
            binding.fruitDistributorTv.text = "Distributor: ${fruit.distributor?.name}"
            binding.statusTv.text = if (fruit.status == 1) "Active" else "Inactive"

            binding.textViewOptions.setOnClickListener {
                val popup = android.widget.PopupMenu(
                    binding.textViewOptions.context,
                    binding.textViewOptions
                )
                popup.inflate(com.nqmgaming.lab6_minhnqph31902.R.menu.menu_item_fruit)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        com.nqmgaming.lab6_minhnqph31902.R.id.edit_fruit -> onUpdate(fruit)
                        com.nqmgaming.lab6_minhnqph31902.R.id.delete_fruit -> onDelete(fruit)
                    }
                    true
                }
                popup.show()
            }

            binding.root.setOnClickListener {
                onUpdate(fruit)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FruitViewHolder {
        val binding = ItemFruitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FruitViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fruitList.size
    }

    override fun onBindViewHolder(holder: FruitViewHolder, position: Int) {
        holder.bind(fruitList[position])
    }

    fun setFruitList(fruitList: List<Fruit>) {
        this.fruitList = fruitList
        notifyDataSetChanged()
    }

}