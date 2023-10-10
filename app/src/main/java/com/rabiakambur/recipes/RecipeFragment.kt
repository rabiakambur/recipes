package com.rabiakambur.recipes

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.rabiakambur.recipes.databinding.FragmentRecipeBinding
import java.io.ByteArrayOutputStream


class RecipeFragment : Fragment() {

    var selectedImage : Uri? = null
    var selectedBitmap : Bitmap? = null

    private var _binding: FragmentRecipeBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val foodName = binding.foodNameText.text.toString()
            val foodMaterials = binding.foodMaterialText.text.toString()

            if (selectedBitmap != null){
                val smallBitmap = createSmallBitmap(selectedBitmap!!, 300)

                val outputStream = ByteArrayOutputStream()
                smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                val byteArray = outputStream.toByteArray()

                try {
                    context?.let {
                        val database = it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE, null)
                        database.execSQL("CREATE TABLE IF NOT EXISTS foods (id INTEGER PRIMARY KEY, foodName VARCHAR,foodIngredients VARCHAR, image BLOB)")

                        val sqlString = "INSERT INTO foods(foodName, foodIngredients, image) VALUES (?, ?, ?)"

                        val steatement = database.compileStatement(sqlString)
                        steatement.bindString(1,foodName)
                        steatement.bindString(2, foodMaterials)
                        steatement.bindBlob(3, byteArray)
                        steatement.execute()
                    }

                }catch (e: Exception){
                    e.printStackTrace()
                }
                val action = RecipeFragmentDirections.actionRecipeFragmentToListFragment()
                Navigation.findNavController(view).navigate(action)
            }
        }

        binding.imageView.setOnClickListener {

            activity?.let {
                if (ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                }else {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, 2)

                }
            }



        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            selectedImage = data.data

            try {

                context?.let {
                    if (selectedImage != null){
                        if (Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(it.contentResolver, selectedImage!!)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.imageView.setImageBitmap(selectedBitmap)
                        }else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, selectedImage)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }
                    }
                }

            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun createSmallBitmap(userSelectedBitmap : Bitmap, maxSize: Int) : Bitmap {
        var width = userSelectedBitmap.width
        var height = userSelectedBitmap.height

        val bitmapRate : Double = width.toDouble() / height.toDouble()

        if (bitmapRate > 1){
            width = maxSize
            val shortenedHeight = width / bitmapRate
            height = shortenedHeight.toInt()
        }else {
            height = maxSize
            val shortenedWidth = height * bitmapRate
            width = shortenedWidth.toInt()
        }

        return Bitmap.createScaledBitmap(userSelectedBitmap,width, height, true)
    }

}