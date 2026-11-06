# 实验四 思考题与实验小结
移动终端软件开发 · 实验四 图形图像和多媒体技术  胡俊峰 202402323
## 2.1 习题
### 1. 如何设置画笔的颜色、笔触的宽度？如何绘制一个实心矩形？如何绘制字体大小为 25 的一行文本？如何绘制路径？
1. 设置画笔颜色用 Paint.setColor(int color)，常用 Color.RED、Color.rgb(r,g,b)、Color.argb(a,r,g,b)，也可用 setShader 设置渐变；设置笔触宽度用 Paint.setStrokeWidth(float width)，单位为像素，仅当 setStyle(Paint.Style.STROKE) 描边时线宽才可见。
2. 绘制实心矩形：paint.setStyle(Paint.Style.FILL); canvas.drawRect(left, top, right, bottom, paint) 或 canvas.drawRect(RectF, paint)；若先 setStyle(Paint.Style.STROKE) 再 drawRect 则是空心矩形。
3. 绘制字体大小为 25 的文本：paint.setTextSize(25); canvas.drawText("文本", x, y, paint)，其中 (x, y) 是文字基线的起点；需要居中时可用 paint.measureText() 或 Paint.Align.CENTER。
4. 绘制路径：Path path = new Path(); path.moveTo(x0,y0); path.lineTo(x1,y1); path.lineTo(...); path.close(); canvas.drawPath(path, paint)；Path 还支持 addRect/addCircle/quadTo/cubicTo 等，配合 canvas.drawTextOnPath() 还可沿路径绘制文本。

### 2. 请描述 Matrix 类的旋转变换方法 setRotate(60)、postRotate(60)、preRotate(60) 以及 setRotate(60,100,150) 的区别是什么？以及如何对一幅图像进行平移变换或缩放变换或扭曲变换并显示之？
1. 三个方法都绕原点 (0,0) 旋转 60°，区别在于与已有变换的叠加方式：
（1）setRotate(60)：先把矩阵重置为单位矩阵，再设置为旋转 60°，会清除之前的平移/缩放到矩阵中的设置；
（2）postRotate(60)：后乘旋转矩阵，M' = R × M，相当于先执行矩阵已有变换，再整体绕原点旋转；常与 setTranslate 等配合使用；
（3）preRotate(60)：前乘旋转矩阵，M' = M × R，相当于先在原坐标系中旋转，再执行矩阵已有变换；
（4）setRotate(60, 100, 150)：重置矩阵后绕指定点 (100,150)（而不是原点）旋转 60°。
2. 平移：Matrix m = new Matrix(); m.setTranslate(dx, dy); canvas.drawBitmap(bitmap, m, paint) （等价于 drawBitmap(bitmap, dx, dy, paint)）。
3. 缩放：m.setScale(sx, sy); m.postTranslate(dx, dy); canvas.drawBitmap(bitmap, m, paint)；注意第二次要用 postTranslate，若再用 setTranslate 会把缩放覆盖掉。
4. 扭曲：m.setSkew(kx, ky); m.postTranslate(dx, dy); canvas.drawBitmap(bitmap, m, paint)，也可直接 m.setSkew(kx, ky, px, py) 指定中心点。

### 3. 如何制作一个帧动画？
制作帧动画的步骤如下：
（1）准备一组连续动作的图片（如 p01.png~p10.png），放入 res/drawable；
（2）在 res/anim 目录下新建 XML（如 album_list.xml），根元素为 <animation-list>，用 android:oneshot="false" 设置为循环播放，每个 <item android:drawable="@drawable/p01" android:duration="1000"/> 指定一帧图片和显示时长；
（3）在布局中用 ImageView 显示，可设置 android:background="@anim/album_list"，也可在代码中 iv.setBackgroundResource(R.anim.album_list)）；
（4）代码中通过 AnimationDrawable ad = (AnimationDrawable) iv.getBackground(); 取得动画对象，调用 ad.start() 开始播放、ad.stop() 停止播放；
（5）注意 start() 必须在窗口可见之后调用（如按钮单击回调或 onWindowFocusChanged 中），否则动画不会播放。

### 4. 如何制作一个补间动画？
制作补间动画的步骤如下：
（1）在 res/anim 目录下新建动画 XML：<alpha>（透明度 fromAlpha/toAlpha）、<scale>（缩放 fromXScale/toScale、pivotX/pivotY 旋转轴心）、<translate>（平移 fromXDelta/toXDelta）、<rotate>（旋转 fromDegrees/toDegrees），公共属性有 duration（时长）、interpolator（速度插值器）、fillAfter（结束后是否保持）；
（2）需要同时播放多个动画时，用一个 <set> 作为根元素把各个动画包起来，并可用 shareInterpolator 指定是否共用插值器；
（3）在代码中用 Animation animation = AnimationUtils.loadAnimation(this, R.anim.tween_anim) 加载动画资源；
（4）调用 view.startAnimation(animation) 启动动画；重复播放前先 clearAnimation() 清除上一次动画；
（5）补间动画只是改变 View 的绘制效果，并不改变 View 的真实属性（left/top/size），若要真正改变属性应使用属性动画（ObjectAnimator/ValueAnimator）。

### 5. 使用 SoundPool 播放音频的主要步骤是什么？
使用 SoundPool 播放音频的主要步骤：
（1）创建 SoundPool 对象：SoundPool soundPool = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build()；旧版本可用 new SoundPool(5, AudioManager.STREAM_MUSIC, 0)；
（2）加载音频：int soundId = soundPool.load(this, R.raw.doorbell, 1)，load() 是异步的，返回音频 id，可注册 setOnLoadCompleteListener 在 status==0 时得知加载完成；
（3）播放音频：soundPool.play(soundId, leftVolume, rightVolume, priority, loop, rate)，返回 streamId，同一音效可同时播放多路；
（4）控制与释放：可用 soundPool.stop(streamId)、soundPool.pause(streamId) 控制，不再使用时调用 soundPool.release() 释放资源；
（5）SoundPool 适合短小的音效（如按键音、门铃），不适合长时间音乐，长音频应使用 MediaPlayer。

### 6. 如何使用 MediaPlayer 播放音频、暂停播放、停止播放音频？
使用 MediaPlayer 播放音频的步骤：
（1）创建并加载：MediaPlayer mp = MediaPlayer.create(this, R.raw.xiaoge)（内部已完成 prepare），也可 new MediaPlayer() 后 setDataSource() + prepare()；
（2）播放：mp.start()，用 mp.isPlaying() 判断是否正在播放；
（3）暂停：mp.pause()，再次调用 mp.start() 可以从暂停位置继续播放；
（4）停止：mp.stop()，停止后播放器处于 Stopped 状态，必须重新 prepare() 才能再次 start()；
（5）其他：setOnCompletionListener 监听播放完成，seekTo() 定位，getCurrentPosition()/getDuration() 获取进度与时长；
（6）释放：在 Activity 的 onDestroy() 中调用 mp.release() 释放底层解码器资源，避免内存泄漏。

### 7. 如何使用 VideoView 播放视频、暂停播放、停止播放？
使用 VideoView 播放视频（控件已封装好 MediaPlayer 与 SurfaceView）：
（1）布局中添加 <VideoView android:id="@+id/vv" .../>；
（2）设置视频源：vv.setVideoPath("/sdcard/beargolf.mp4")；也可 vv.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video)) 播放 res/raw 中的视频，或传入网络 URL；
（3）可选 vv.setMediaController(new MediaController(this)) 显示进度条控制条；
（4）播放：vv.start()；暂停：vv.pause()；继续：vv.resume()；
（5）停止：vv.stopPlayback()（停止并释放播放器），若要重新播放需再次 setVideoPath 再 start()；
（6）在 onDestroy() 中调用 vv.stopPlayback() 释放资源。

### 8. 如何使用 MediaPlayer+SurfaceView 播放视频、暂停播放、停止播放？
使用 MediaPlayer + SurfaceView 播放视频的步骤：
（1）布局中添加 SurfaceView（MediaPlayer 的画面会输出到它的 Surface 上）；
（2）在 Activity 中实现 SurfaceHolder.Callback，通过 surfaceView.getHolder().addCallback(this) 注册回调；
（3）创建 MediaPlayer：new MediaPlayer() 或 MediaPlayer.create(this, R.raw.video)，setDataSource(path) 指定视频（SD 卡路径如 "/sdcard/beargolf.mp4"）；
（4）在 surfaceCreated(SurfaceHolder holder) 中调用 mediaPlayer.setDisplay(holder)，把 MediaPlayer 与显示表面关联（必须在 Surface 创建之后）；
（5）准备并播放：mediaPlayer.prepare()（或 prepareAsync）+ mediaPlayer.start()；
（6）暂停：mediaPlayer.pause()，继续：mediaPlayer.start()；停止：mediaPlayer.pause() + seekTo(0)；
（7）在 surfaceDestroyed() 中 setDisplay(null)，在 onDestroy() 中 stop()/release() 释放资源。相比 VideoView，MediaPlayer + SurfaceView 更灵活，可自行控制渲染与布局。

## 2.2 实验小结
### 1 编程遇到的问题及解决方案
1. 自定义 View 添加方式错误导致不显示：最初只在布局中写了普通 View，忘记了自定义 View 需要在 布局里使用全类名（<com.example.graphicsmedia.ShapeView .../>），或者用 FrameLayout.addView(new MyView(this)) 动态添加，两种方式必须与 setContentView 的布局一致。
2. 画笔设置了宽度却画不出线：Paint.Style.FILL 时 setStrokeWidth 不生效，只有 STROKE 样式才有线宽；同时 setStyle 与 setStrokeWidth 的顺序没有影响，但 setColor 必须在 draw 之前设置。
3. Matrix 变换结果与预期不符：一开始每次都调用 setRotate/setScale，会清空前面的变换（setXXX 会重置矩阵），改为先 setScale/setRotate，再用 postTranslate 叠加平移后才得到正确结果。
4. 帧动画点击按钮不播放：AnimationDrawable.start() 在 onCreate() 里调用无效，因为此时窗口还不可见；改为在按钮 onClick 中启动后正常。另外忘记给 ImageView 设置背景动画资源也会黑屏。
5. 补间动画结束后图像跳回原位：补间动画只改变绘制效果，结束时会恢复；需要保留最终状态可设置 fillAfter="true"，或者改用属性动画。
6. SoundPool 第一次点击没有声音：load() 是异步加载，未加载完成就 play() 会返回 0；通过 setOnLoadCompleteListener 确认加载完成（status==0）后再播放即可。
7. MediaPlayer 停止后再次播放抛 IllegalStateException：调用 stop() 后播放器处于 Stopped 状态，必须重新 prepare() 才能 start()；改成 stop() + prepare() + seekTo(0) 后解决。
8. 视频播放黑屏：在 surfaceCreated() 之前调用 setDisplay() 无效，必须在 SurfaceHolder.Callback 的 surfaceCreated 回调中再 setDisplay(holder)，并且 MediaPlayer 要先准备完成才能输出画面。

### 2 实验收获及体会
1. 掌握了 Paint 的常用属性：颜色（setColor/setARGB）、样式（FILL/STROKE）、笔触宽度、抗锯齿、线帽、文字大小与加粗，以及 Canvas 的 drawRect/drawCircle/drawOval/drawArc/drawLine/drawPath/drawText 等绘图 API，理解了 Canvas 的坐标系与 save()/restore() 的作用。
2. 掌握了 Matrix 的变换原理：平移 setTranslate、旋转 setRotate、缩放 setScale、扭曲 setSkew，以及 pre/post 前缀表示与已有变换的叠加顺序，会用 Matrix + drawBitmap 显示变换后的图像。
3. 掌握了帧动画的制作流程：animation-list 资源 + AnimationDrawable 的 start()/stop()，理解了 oneshot、duration 的含义以及必须在窗口可见后启动的限制。
4. 掌握了补间动画的制作：alpha/scale/translate/rotate 四类 XML 资源及 set 组合动画，AnimationUtils.loadAnimation + startAnimation 的使用，以及 pivot、interpolator、fillAfter 等属性的作用。
5. 掌握了 SoundPool 播放短音效的完整流程（Builder 初始化、异步 load、play、release），以及 MediaPlayer 播放音频的 create/start/pause/stop/prepare/release 状态机。
6. 掌握了 MediaPlayer + SurfaceView 播放视频的方法：SurfaceHolder.Callback 生命周期、setDisplay() 关联、播放/暂停/停止控制与资源释放，体会到 SurfaceView 与 VideoView 的区别。
7. 体会到多媒体开发中“资源必须及时释放”的重要性（MediaPlayer.release、SoundPool.release、handler.removeCallbacks），否则容易造成内存泄漏和后台耗电。

### 3 不足之处及下一步需改进的地方
1. 绘图程序中图形坐标是按 1080×2400 屏幕写死的像素值，虽然做了等比缩放，但换到其他分辨率或横屏时仍可能出现文字重叠；下一步应结合 dp、屏幕密度和 ConstraintLayout 做自适应布局。
2. 帧动画把 10 张图片全部解码到内存中，图片较大时容易占用过多内存；后续可以改用更小的 WebP 图片、按需加载，或者改用视频/属性动画实现相同效果。
3. 音效与音频界面功能较单一，没有音量、进度条、循环模式等控制；可以增加 SeekBar 拖动定位、音量调节、播放列表，并处理 AudioFocus 与耳机拔出等音频焦点事件。
4. 程序对异常情况的处理不足（如视频文件不存在、格式不支持、SD 卡无权限等），后续应补充 try-catch、状态判断与用户提示；同时可以把七个程序改为一个带 Fragment/导航的完整应用，界面更统一。

