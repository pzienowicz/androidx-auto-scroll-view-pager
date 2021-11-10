[![](https://jitpack.io/v/pzienowicz/androidx-auto-scroll-view-pager.svg)](https://jitpack.io/#pzienowicz/androidx-auto-scroll-view-pager) 
[![](https://jitpack.io/v/pzienowicz/androidx-auto-scroll-view-pager/month.svg)](https://jitpack.io/#pzienowicz/androidx-auto-scroll-view-pager) 
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19) 
![GitHub issues](https://img.shields.io/github/issues/pzienowicz/Trialer.svg?style=flat-square) 
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-androidx--auto--scroll--view--pager-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7907) 
[![GitHub license](https://img.shields.io/badge/license-Apache%202-brightgreen.svg)](https://raw.githubusercontent.com/pzienowicz/androidx-auto-scroll-view-pager/master/LICENSE)
[![androidx](https://img.shields.io/badge/androidx-brightgreen.svg)](https://developer.android.com/topic/libraries/support-library/refactor)

Androidx Auto Scroll ViewPager
==============================
* ViewPager which can auto scrolling, cycling, decelerating.  
* ViewPager which can be slided manually in parent ViewPager.
* ViewPager which is compatible with AndroidX library.
* ViewPager which is written in Kotlin and be supported for a long time.

# Thanks
Many thanks to [Trinea](https://github.com/Trinea) because this library is the newest, kotlin version of his [library](https://github.com/Trinea/android-auto-scroll-view-pager). We support AndroidX library, so if you have problem when migrating to AndroidX, this version should work like a charm. 

Installation
------------

### Gradle
Add this to your root build.gradle file under repositories:
```
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```
Add this to your app level build.gradle as dependency:

    implementation 'com.github.pzienowicz:androidx-auto-scroll-view-pager:{latest.version}'
Latest version: ![](https://jitpack.io/v/pzienowicz/androidx-auto-scroll-view-pager.svg)

## Usage
- include this library
- use below code

``` xml
<pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager
	android:id="@+id/view_pager"
	android:layout_width="match_parent"
	android:layout_height="wrap_content" />
```
instead of
``` xml
<android.support.v4.view.ViewPager
	android:id="@+id/view_pager"
	android:layout_width="match_parent"
	android:layout_height="wrap_content" />
```

## Functions
- `startAutoScroll()` start auto scroll, delay time is `getInterval()`.
- `startAutoScroll(int)` start auto scroll delayed.
- `stopAutoScroll()` stop auto scroll.

## Settings
- `setInterval(long)` set auto scroll time in milliseconds, default is `1500`.  
- `setDirection(Direction)` set auto scroll direction, default is `Direction.RIGHT`.  
- `setCycle(boolean)` set whether automatic cycle when auto scroll reaching the last or first item, default is `true`. 
- `setScrollDurationFactor(double)` set the factor by which the duration of sliding animation will change.  
- `setSlideBorderMode(SlideBorderMode)` set how to process when sliding at the last or first item, default is `SlideBorderMode.NONE`.
- `setStopScrollWhenTouch(boolean)` set whether stop auto scroll when touching, default is `true`.  
- `setBorderAnimation(boolean)` set whether animating when auto scroll at the last or first item, default is `true`.  
- You cannot combine with [ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator) if `setCycle(true)`. 

## Proguard
```
-keep class pl.pzienowicz.** { *; }
-keepclassmembers pl.pzienowicz.** { *; }
-dontwarn pl.pzienowicz.**
```

# Example
Please check example app contained in this repository.
This is how it looks like:
![Example](https://github.com/pzienowicz/androidx-auto-scroll-view-pager/blob/master/app/files/ezgif-3-f33a4fdf55f4.gif)
