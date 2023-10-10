package com.rabiakambur

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.rabiakambur.recipes.ListFragmentDirections
import com.rabiakambur.recipes.databinding.RecyclerRowBinding

class ListRecyclerAdapter(
    private val foodList: ArrayList<String>,
    private val idList: ArrayList<Int>
) : RecyclerView.Adapter<ListRecyclerAdapter.FoodHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerRowBinding.inflate(inflater, parent, false)
        return FoodHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.bind(foodList[position])
    }

    inner class FoodHolder(private val binding: RecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.recyclerRowText.setOnClickListener {
                val action = ListFragmentDirections.actionListFragmentToRecipeFragment(
                    "fromtherecycler",
                    idList[position]
                )
                Navigation.findNavController(it).navigate(action)
            }
        }

        fun bind(text: String) {
            binding.recyclerRowText.text = text
        }
    }
}