# PieView

 ![img](https://github.com/tangchaoyu/PieView/blob/master/715FB68A0D61AD1877799D6FB15FAB16.gif)
 
 ### 统计饼图
 
 此饼图应该满足大部分功能和UI的定制变化,其中自己也参考的一些其他开源统计饼图的写法
 
 ### 用法
 
 ```
 <com.example.tcy.view.PieView
    android:id="@+id/pieView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_centerInParent="true"
    />
```
#### layout_width layout_height 可以自己指定
 
#### 实现PieView.ClickListener接口
 ```
 class MainActivity : AppCompatActivity(),PieView.ClickListener
 
 override fun onArcClick(i: Int) {

  }
 override fun onCenterClick() {
    Toast.makeText(applicationContext, "圆被点击", Toast.LENGTH_SHORT).show()
  }
 
```
 #### 初始数据
 ```
    private val value = intArrayOf(20, 40, 30, 60, 40, 30)
    private val name = arrayOf("A", "B", "C", "D", "E", "F")
    private val color = intArrayOf(R.color.a, R.color.b, R.color.c, R.color.d, R.color.e, R.color.f)
```
#### 设置属性
 ```
        //设置文字颜色 默认白色
        pieView?.setTextColor(Color.WHITE)
        //设置中间圆大小 0不显示中间圆 1到10中间圆逐渐减小
        pieView?.setCenterCir(2)
        //设置中间文字,在中间圆半径pieView.setCenterCir值1-5中间时才会显示
        pieView?.setCenterText(resources.getString(R.string.percent))
        //设置中间文字颜色 默认黑色
        pieView?.setCenterTextColor(Color.RED)
        //是否显示百分比文字 默认true
        pieView?.setPercentageTextShow(true)
        //是否显示动画 默认true
        pieView?.setShowAnimation(true)
        //是否绘制分割线 默认true
        pieView?.setDrawLine(true)
        //设置分割线颜色默认白色
        pieView?.setLineColor(Color.WHITE)
        //是否绘制中心阴影 默认true
        pieView?.isShadow(true)
        //点击是否自动旋转到底部 默认true
        pieView?.setTouchStart(true)
        //点击是否切割扇形 默认true
        pieView?.setTouchCarve(true)
        //设置事件监听
        pieView?.setListener(this)
```

 #### 设置数据
```
 //设置所有颜色
  pieView?.setmColors(colors)
  //设置数据
  pieView?.setData(Dates)
```
