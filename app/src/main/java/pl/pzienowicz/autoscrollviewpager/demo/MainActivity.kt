package pl.pzienowicz.autoscrollviewpager.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager
import pl.pzienowicz.autoscrollviewpager.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val images = listOf(
            "https://wallpaperaccess.com/full/51607.jpg",
            "https://wallpaperaccess.com/full/51616.jpg",
            "https://wallpaperaccess.com/full/51621.jpg",
            "https://wallpaperaccess.com/full/11810.jpg"
        )

        binding.viewPager.adapter = ImagesAdapter(this, images)
        binding.viewPager.setInterval(2000)
        binding.viewPager.setDirection(AutoScrollViewPager.Direction.RIGHT)
        binding.viewPager.setCycle(true)
        binding.viewPager.setBorderAnimation(true)
        binding.viewPager.setSlideBorderMode(AutoScrollViewPager.SlideBorderMode.TO_PARENT)
        binding.viewPager.startAutoScroll()
    }
}
