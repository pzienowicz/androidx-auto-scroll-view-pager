package pl.pzienowicz.autoscrollviewpager.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val images = ArrayList<String>()
        images.add("https://wallpaperaccess.com/full/51607.jpg")
        images.add("https://wallpaperaccess.com/full/51616.jpg")
        images.add("https://wallpaperaccess.com/full/51621.jpg")
        images.add("https://wallpaperaccess.com/full/11810.jpg")

        viewPager.adapter = ImagesAdapter(this, images)
        viewPager.setInterval(2000)
        viewPager.startAutoScroll()
    }
}
