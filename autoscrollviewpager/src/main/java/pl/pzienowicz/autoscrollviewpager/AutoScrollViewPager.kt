package pl.pzienowicz.autoscrollviewpager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Interpolator
import androidx.viewpager.widget.ViewPager

class AutoScrollViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    constructor(context: Context): this(context, null)

    private val defaultInterval = 1500
    private val scrollWhat = 0

    enum class Direction        { LEFT, RIGHT }
    enum class SlideBorderMode  { NONE, CYCLE, TO_PARENT }

    private var interval = defaultInterval.toLong()
    private var direction = Direction.RIGHT
    private var isCycle = true
    private var stopScrollWhenTouch = true
    private var slideBorderMode = SlideBorderMode.NONE
    private var isBorderAnimation = true

    private var isAutoScroll = false
    private var isStopByTouch = false
    private var touchX = 0f
    private var downX = 0f
    private var scroller: CustomDurationScroller? = null
    private var myHandler: Handler

    init {
        myHandler = MyHandler()
        setViewPagerScroller()
    }

    fun startAutoScroll() {
        isAutoScroll = true
        sendScrollMessage(interval)
    }

    fun startAutoScroll(delayTimeInMills: Int) {
        isAutoScroll = true
        sendScrollMessage(delayTimeInMills.toLong())
    }

    fun stopAutoScroll() {
        isAutoScroll = false
        myHandler.removeMessages(scrollWhat)
    }

    /**
     * set the factor by which the duration of sliding animation will change
     */
    fun setScrollDurationFactor(scrollFactor: Double) {
        scroller?.setScrollDurationFactor(scrollFactor)
    }

    private fun sendScrollMessage(delayTimeInMills: Long) {
        /** remove messages before, keeps one message is running at most  */
        myHandler.removeMessages(scrollWhat)
        myHandler.sendEmptyMessageDelayed(scrollWhat, delayTimeInMills)
    }

    /**
     * set ViewPager scroller to change animation duration when sliding
     */
    private fun setViewPagerScroller() {
        try {
            val scrollerField = ViewPager::class.java.getDeclaredField("mScroller")
            scrollerField.isAccessible = true
            val interpolatorField = ViewPager::class.java.getDeclaredField("sInterpolator")
            interpolatorField.isAccessible = true

            scroller = CustomDurationScroller(context, interpolatorField.get(null) as Interpolator)
            scrollerField.set(this, scroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun scrollOnce() {
        if (adapter == null || adapter!!.count <= 1) {
            return
        }
        var currentItem = currentItem
        val totalCount = adapter!!.count

        val nextItem = if (direction == Direction.LEFT) --currentItem else ++currentItem

        if (nextItem < 0) {
            if (isCycle) {
                setCurrentItem(totalCount - 1, isBorderAnimation)
            }
        } else if (nextItem == totalCount) {
            if (isCycle) {
                setCurrentItem(0, isBorderAnimation)
            }
        } else {
            setCurrentItem(nextItem, true)
        }
    }

    /**
     * if stopScrollWhenTouch is true
     *  * if event is down, stop auto scroll.
     *  * if event is up, start auto scroll again.
     */
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (stopScrollWhenTouch) {
            if (ev.action == MotionEvent.ACTION_DOWN && isAutoScroll) {
                isStopByTouch = true
                stopAutoScroll()
            } else if (ev.action == MotionEvent.ACTION_UP && isStopByTouch) {
                startAutoScroll()
            }
        }

        if (slideBorderMode == SlideBorderMode.TO_PARENT || slideBorderMode == SlideBorderMode.CYCLE) {
            touchX = ev.x
            if (ev.action == MotionEvent.ACTION_DOWN) {
                downX = touchX
            }
            val currentItem = currentItem
            val adapter = adapter
            val pageCount = adapter?.count ?: 0
            /**
             * current index is first one and slide to right or current index is last one and slide to left.<br></br>
             * if slide border mode is to parent, then requestDisallowInterceptTouchEvent false.<br></br>
             * else scroll to last one when current item is first one, scroll to first one when current item is last
             * one.
             */
            if (currentItem == 0 && downX <= touchX || currentItem == pageCount - 1 && downX >= touchX) {
                if (slideBorderMode == SlideBorderMode.TO_PARENT) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    if (pageCount > 1) {
                        setCurrentItem(pageCount - currentItem - 1, isBorderAnimation)
                    }
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                return super.onTouchEvent(ev)
            }
        }
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(ev)
    }

    private inner class MyHandler : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == scrollWhat) {
                scrollOnce()
                sendScrollMessage(interval)
            }
        }
    }

    /**
     * get auto scroll time in milliseconds, default is [.DEFAULT_INTERVAL]
     */
    fun getInterval(): Long {
        return interval
    }

    /**
     * set auto scroll time in milliseconds, default is [.DEFAULT_INTERVAL]
     */
    fun setInterval(interval: Long) {
        this.interval = interval
    }

    /**
     * get auto scroll direction
     */
    fun getDirection(): Direction {
        return direction
    }

    /**
     * set auto scroll direction
     */
    fun setDirection(direction: Direction) {
        this.direction = direction
    }

    /**
     * whether automatic cycle when auto scroll reaching the last or first item, default is true
     *
     * @return the isCycle
     */
    fun isCycle(): Boolean {
        return isCycle
    }

    /**
     * set whether automatic cycle when auto scroll reaching the last or first item, default is true
     */
    fun setCycle(isCycle: Boolean) {
        this.isCycle = isCycle
    }

    /**
     * whether stop auto scroll when touching, default is true
     */
    fun isStopScrollWhenTouch(): Boolean {
        return stopScrollWhenTouch
    }

    /**
     * set whether stop auto scroll when touching, default is true
     */
    fun setStopScrollWhenTouch(stopScrollWhenTouch: Boolean) {
        this.stopScrollWhenTouch = stopScrollWhenTouch
    }

    /**
     * get how to process when sliding at the last or first item
     */
    fun getSlideBorderMode(): SlideBorderMode {
        return slideBorderMode
    }

    /**
     * set how to process when sliding at the last or first item
     */
    fun setSlideBorderMode(slideBorderMode: SlideBorderMode) {
        this.slideBorderMode = slideBorderMode
    }

    /**
     * whether animating when auto scroll at the last or first item, default is true
     */
    fun isBorderAnimation(): Boolean {
        return isBorderAnimation
    }

    /**
     * set whether animating when auto scroll at the last or first item, default is true
     */
    fun setBorderAnimation(isBorderAnimation: Boolean) {
        this.isBorderAnimation = isBorderAnimation
    }
}