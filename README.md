# fashion-weave

[![](https://jitpack.io/v/mcxinyu/fashion-weave.svg)](https://jitpack.io/#mcxinyu/fashion-weave)

在 android 上，自己画一个编织效果的 view。

## 引入

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

and:

```gradle
dependencies {
    implementation 'com.github.mcxinyu:fashion-weave:{latest version}'
}
```

## 效果
<!--![](https://github.com/mcxinyu/fashion-weave/blob/master/art/device-2019-04-08-162143.png?raw=true)-->

<img src="https://github.com/mcxinyu/fashion-weave/blob/master/art/device-2019-04-08-162143.png?raw=true" width="300">

## View 的实际绘制区域
由于需要考虑画布旋转，所以使用最方便的绘制方式就是对 View 做外切圆，再取外切圆的外切矩形（only正方形）进行绘制。

实际绘制过程是将 View 的原点移动到整个 View 的中心，再进行计算绘制。

<!--![](https://raw.githubusercontent.com/mcxinyu/fashion-weave/42532dee90b3fe608811d39f237d69a5401565b3/art/20190409164548.png)-->
<img src="https://raw.githubusercontent.com/mcxinyu/fashion-weave/42532dee90b3fe608811d39f237d69a5401565b3/art/20190409164548.png" width="600">