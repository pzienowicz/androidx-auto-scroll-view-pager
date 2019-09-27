package pl.pzienowicz.autoscrollviewpager

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Interpolator
import androidx.viewpager.widget.ViewPager

class AutoScrollViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    constructor(context: Context): this(context, null)

    val DEFAULT_INTERVAL = 1500

    val LEFT = 0
    val RIGHT = 1

    /** do nothing when sliding at the last or first item  */
    val SLIDE_BORDER_MODE_NONE = 0
    /** cycle when sliding at the last or first item  */
    val SLIDE_BORDER_MODE_CYCLE = 1
    /** deliver event to parent when sliding at the last or first item  */
    val SLIDE_BORDER_MODE_TO_PARENT = 2

    /** auto scroll time in milliseconds, default is [.DEFAULT_INTERVAL]  */
    private var interval = DEFAULT_INTERVAL.toLong()
    /** auto scroll direction, default is [.RIGHT]  */
    private var direction = RIGHT
    /** whether automatic cycle when auto scroll reaching the last or first item, default is true  */
    private var isCycle = true
    /** whether stop auto scroll when touching, default is true  */
    private var stopScrollWhenTouch = true
    /** how to process when sliding at the last or first item, default is [.SLIDE_BORDER_MODE_NONE]  */
    private var slideBorderMode = SLIDE_BORDER_MODE_NONE
    /** whether animating when auto scroll at the last or first item  */
    private var isBorderAnimation = true

    private var myHandler: Handler

    private var isAutoScroll = false
    private var isStopByTouch = false
    private var touchX = 0f
    private var downX = 0f
    private var scroller: CustomDurationScroller? = null

    val SCROLL_WHAT = 0

    init {
        myHandler = MyHandler()
        setViewPagerScroller()
    }

    fun startAutoScroll() {
        isAutoScroll = true
        sendScrollMessage(interval)
    }

    /**
     * @param delayTimeInMills first scroll delay time
     */
    fun startAutoScroll(delayTimeInMills: Int) {
        isAutoScroll = true
        sendScrollMessage(delayTimeInMills.toLong())
    }

    fun stopAutoScroll() {
        isAutoScroll = false
        myHandler.removeMessages(SCROLL_WHAT)
    }

    /**
     * set the factor by which the duration of sliding animation will change
     */
    fun setScrollDurationFactor(scrollFactor: Double) {
        scroller?.setScrollDurationFactor(scrollFactor)
    }

    private fun sendScrollMessage(delayTimeInMills: Long) {
        /** remove messages before, keeps one message is running at most  */
        myHandler.removeMessages(SCROLL_WHAT)
        myHandler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills)
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

        val nextItem = if (direction == LEFT) --currentItem else ++currentItem

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
     *
     * if stopScrollWhenTouch is true
     *  * if event is down, stop auto scroll.
     *  * if event is up, start auto scroll again.
     *
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

        if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT || slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
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
                if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
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

    private inner class MyHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == SCROLL_WHAT) {
                scrollOnce()
                sendScrollMessage(interval)
            }
        }
    }

    /**
     * get auto scroll time in milliseconds, default is [.DEFAULT_INTERVAL]
     *
     * @return the interval
     */
    fun getInterval(): Long {
        return interval
    }

    /**
     * set auto scroll time in milliseconds, default is [.DEFAULT_INTERVAL]
     *
     * @param interval the interval to set
     */
    fun setInterval(interval: Long) {
        this.interval = interval
    }

    /**
     * get auto scroll direction
     *
     * @return [.LEFT] or [.RIGHT], default is [.RIGHT]
     */
    fun getDirection(): Int {
        return if (direction == LEFT) LEFT else RIGHT
    }

    /**
     * set auto scroll direction
     *
     * @param direction [.LEFT] or [.RIGHT], default is [.RIGHT]
     */
    fun setDirection(direction: Int) {
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
     *
     * @param isCycle the isCycle to set
     */
    fun setCycle(isCycle: Boolean) {
        this.isCycle = isCycle
    }

    /**
     * whether stop auto scroll when touching, default is true
     *
     * @return the stopScrollWhenTouch
     */
    fun isStopScrollWhenTouch(): Boolean {
        return stopScrollWhenTouch
    }

    /**
     * set whether stop auto scroll when touching, default is true
     *
     * @param stopScrollWhenTouch
     */
    fun setStopScrollWhenTouch(stopScrollWhenTouch: Boolean) {
        this.stopScrollWhenTouch = stopScrollWhenTouch
    }

    /**
     * get how to process when sliding at the last or first item
     *
     * @return the slideBorderMode [.SLIDE_BORDER_MODE_NONE], [.SLIDE_BORDER_MODE_TO_PARENT],
     * [.SLIDE_BORDER_MODE_CYCLE], default is [.SLIDE_BORDER_MODE_NONE]
     */
    fun getSlideBorderMode(): Int {
        return slideBorderMode
    }

    /**
     * set how to process when sliding at the last or first item
     *
     * @param slideBorderMode [.SLIDE_BORDER_MODE_NONE], [.SLIDE_BORDER_MODE_TO_PARENT],
     * [.SLIDE_BORDER_MODE_CYCLE], default is [.SLIDE_BORDER_MODE_NONE]
     */
    fun setSlideBorderMode(slideBorderMode: Int) {
        this.slideBorderMode = slideBorderMode
    }

    /**
     * whether animating when auto scroll at the last or first item, default is true
     *
     * @return
     */
    fun isBorderAnimation(): Boolean {
        return isBorderAnimation
    }

    /**
     * set whether animating when auto scroll at the last or first item, default is true
     *
     * @param isBorderAnimation
     */
    fun setBorderAnimation(isBorderAnimation: Boolean) {
        this.isBorderAnimation = isBorderAnimation
    }
}