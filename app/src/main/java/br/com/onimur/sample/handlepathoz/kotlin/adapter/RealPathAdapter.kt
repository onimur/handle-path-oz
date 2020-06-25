/*
 * Created by Murillo Comino on 24/06/20 21:13
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 24/06/20 21:01
 */

package br.com.onimur.sample.handlepathoz.kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.comino.sample.handlepathoz.R
import br.com.onimur.sample.handlepathoz.kotlin.model.PathModel

class RealPathAdapter(private val list: MutableList<PathModel>) :
    RecyclerView.Adapter<RealPathAdapter.RealPathAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealPathAdapterVH {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.row_real_path, parent, false)
        return RealPathAdapterVH(view)
    }

    override fun onBindViewHolder(holder: RealPathAdapterVH, position: Int) {
        list[position].also {
            with(holder) {
                tvTitle.text = it.nameFile
                tvType.text = it.type
                tvPath.text = it.absolutePath

                if (it.expanded) {
                    layoutExpandable.visibility = View.VISIBLE
                    ivArrow.animateExpand()
                } else {
                    layoutExpandable.visibility = View.GONE
                    ivArrow.animateCollapse()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateListChanged(newList: List<PathModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class RealPathAdapterVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.findViewById(R.id.tv_title) as TextView
        val tvType = view.findViewById(R.id.tv_type) as TextView
        val tvPath = view.findViewById(R.id.tv_path) as TextView
        val ivArrow = view.findViewById(R.id.iv_arrow) as ImageView
        val layoutExpandable = view.findViewById(R.id.cl_expandable) as ConstraintLayout
        private val layoutMain = view.findViewById(R.id.cl_main) as ConstraintLayout

        init {
            layoutMain.setOnClickListener {
                val pathModel = list[adapterPosition]
                pathModel.expanded = !pathModel.expanded
                notifyItemChanged(adapterPosition)
            }
        }

        fun ImageView.animateExpand() {
            RotateAnimation(360f, 180f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
                .also {
                    it.duration = 300
                    it.fillAfter = true
                    this.animation = it
                }
        }

        fun ImageView.animateCollapse() {
            RotateAnimation(180f, 360f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
                .also {
                    it.duration = 300
                    it.fillAfter = true
                    this.animation = it
                }
        }
    }
}
