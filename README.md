# cordova仿微信拍照录像插件

## cordova-plugin-videoclips

### 功能

cordova仿微信拍照录像插件

### 安装

```javascript
cordova plugin add cordova-plugin-videoclips
```

### 卸载

```javascript
cordova plugin add AkeVideo
```

### 使用

```javascript
upvideo.coolMethod("参数",function(msg){
    //成功的回调
            alert('原生返回了：'+msg);
        },function(e){
            //失败的回调
            alert(e);
        })
```

#### 备注

安装时需要安装的依赖

* path
* fs
* shelljs
* semver

卸载时需要手动删除 "/platforms/android/app/src/main/res/layout/activity_video_up.xml"

点击返回以失败处理
