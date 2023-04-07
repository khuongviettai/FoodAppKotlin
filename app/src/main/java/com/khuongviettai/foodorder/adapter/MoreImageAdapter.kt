package com.khuongviettai.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khuongviettai.foodorder.adapter.MoreImageAdapter.MoreImageViewHolder
import com.khuongviettai.foodorder.databinding.ItemMoreImageBinding
import com.khuongviettai.foodorder.model.Image
import com.khuongviettai.foodorder.utils.GlideUtils

class MoreImageAdapter(private val mListImages: MutableList<Image>?) : RecyclerView.Adapter<MoreImageViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreImageViewHolder {
        val itemMoreImageBinding = ItemMoreImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoreImageViewHolder(itemMoreImageBinding)
    }

    override fun onBindViewHolder(holder: MoreImageViewHolder, position: Int) {
        val image = mListImages?.get(position) ?: return
        GlideUtils.loadUrl(image.getUrl(), holder.mItemMoreImageBinding.imageFood)
    }

    override fun getItemCount(): Int {
        return mListImages?.size ?: 0
    }

    class MoreImageViewHolder(val mItemMoreImageBinding: ItemMoreImageBinding) : RecyclerView.ViewHolder(mItemMoreImageBinding.root)
}