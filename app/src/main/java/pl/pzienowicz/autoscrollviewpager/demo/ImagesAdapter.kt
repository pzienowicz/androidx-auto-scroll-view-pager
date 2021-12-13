package pl.pzienowicz.autoscrollviewpager.demo

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso

class ImagesAdapter(
    private val context: Context,
    private var images: List<String>
) : PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount() = images.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        Picasso.get().load(images[position]).into(imageView)

        container.addView(imageView)

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}