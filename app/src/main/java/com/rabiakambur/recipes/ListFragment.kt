package com.rabiakambur.recipes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.rabiakambur.ListRecyclerAdapter
import com.rabiakambur.recipes.databinding.FragmentListBinding
import com.rabiakambur.recipes.databinding.FragmentRecipeBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null

    private val binding get() = _binding!!

    var foodNameList = ArrayList<String>()
    var foodIdList = ArrayList<Int>()
    private lateinit var listAdapter : ListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter = ListRecyclerAdapter(foodNameList, foodIdList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = listAdapter

        sqlDataRetrieval()
    }

    fun sqlDataRetrieval(){
        try {
            activity?.let {
                val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE, null)

                val cursor = database.rawQuery("SELECT * FROM foods", null)
                val foodNameIndex = cursor.getColumnIndex("foodName")
                val foodIdIndex = cursor.getColumnIndex("id")

                foodNameList.clear()
                foodIdList.clear()

                while (cursor.moveToNext()){
                    foodNameList.add(cursor.getString(foodNameIndex))
                    foodIdList.add(cursor.getInt(foodIdIndex))
                }

                listAdapter.notifyDataSetChanged()

                cursor.close()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}