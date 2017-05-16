# DroiBaaSDemo-CampusDiary
## 系统框架设计

### 用到的框架及生产工具
日记的展示界面用了`SuperRecyclerView`  
图片加载与缓存用了`Glide`  
`DroiBaaS`的网络请求都是基于`OKHttp`的，所以`OKHttp`和`OKio`是必须用到的网络框架  
响应式编程用的是`RxAndroid`&`RxJava`  
高度整合封装的云服务BaaS作为第二代云计算的产物，为App的云后台开发提供了非常便利的生产工具，提高了开发效率、缩短了上线时间、降低了开发成本，这必将是一个潮流和趋势。
所有的云端功能，如推送、自更新、用户反馈、统计、云数据、用户管理功能全部是用`DroiBaaS`的SDK来实现。

### 日记展示UML架构设计
![](http://ojx2540cr.bkt.clouddn.com/1.png)
`MainActvity`继承`CircleContract.View`接口  
`CirclePresenter`继承`CircleContract.Presenter`接口  
`MainActvity`生成一个`CirclePresenter`对象同时把自身传入`CirclePresenter`  
`MainActvity`调用`CircleContract.Presenter`的各种数据获取接口，`CirclePresenter`从云端获取到数据后调用`CircleContract.View`的界面更新接口通知`MainActivity`来刷新`View`  
整个`MVP`架构相当的清晰明了，使用`MVP`最大的好处就在此处，代码简洁，同时简化了`Activity`的逻辑，利于以后的调试和单元测试，新功能加起来也非常的方便  
数据库设计如`UML`图所示  
主要是四个主要交互数据类，这个四个类同时也是后DroiBaaS云后台数据库保存的数据类  
`CircleItem`：日记内容  
`FavorItem`：用户点赞  
`CommentItem`：用户评论  
`User`：用户  

## 详细代码设计
日记`MVP`+`RxAndroid`代码如下：  
`CircleContract.java` - `Model`和`View`的中间接口类
``` java
public interface CircleContract {

    interface View extends BaseView {
        void update2DeleteCircle(String circleId);
        void update2AddFavor(int circlePosition, FavorItem addItem);
        void update2DeleteFavor(int circlePosition, String favortId);
        void update2AddComment(int circlePosition, CommentItem addItem);
        void update2DeleteComment(int circlePosition, String commentId);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
        void update2loadData(int loadType, List<CircleItem> datas);
    }

    interface Presenter {
        void loadData(int loadType);
        void deleteCircle(final String circleId);
        void addFavor(final int circlePosition, final String circleId);
        void deleteFavor(final int circlePosition, final String favortId);
        void addComment(String content, final CommentConfig config);
        void deleteComment(final int circlePosition, final String commentId);
        void showEditTextBody(CommentConfig commentConfig);
    }
}
```

`CirclePresenter.java` - 此类使用`RxAndroid`从云端获取数据再发回给`View`进行异步展示，在这个类中可以看出使用`RxAndroid`处理异步逻辑非常得心用手，推荐大家使用。
``` java
public class CirclePresenter implements CircleContract.Presenter {
    private final static String TAG = "CirclePresenter";
    private CircleContract.View view;
    private static int index = 0;
    private static final String API_KEY = "TfO1XdOKxTaxTitvGd9KZXt7drSXm2axjlDIcsFUJ4h6jVUzLQBwBNqIQe3bL_ZY";
    public static final int LIMIT = 10;

    public CirclePresenter(CircleContract.View view) {
        this.view = view;
    }

    @Override
    public void loadData(final int loadType) {
        if (loadType == TYPE_PULL_REFRESH) {
            index = 0;
        }
        getCircleData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CircleItem>>() {
                    @Override
                    public void onCompleted() {
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view == null) {
                            return;
                        }
                        Log.i(TAG, e.toString());
                        view.showToast(view.getResString(R.string.error));
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(List<CircleItem> data) {
                        if (view == null) {
                            return;
                        }
                        view.update2loadData(loadType, data);
                    }
                });
    }

    @Override
    public void deleteCircle(final String circleId) {
        deleteCircleData(circleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view == null) {
                            return;
                        }
                        Log.i(TAG, e.toString());
                        view.showToast(view.getResString(R.string.error));
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(Boolean result) {
                        if (view == null) {
                            return;
                        }
                        if (result) {
                            view.update2DeleteCircle(circleId);
                        } else {
                            view.showToast(view.getResString(R.string.delete_failed));
                        }
                    }
                });
    }

    @Override
    public void addFavor(final int circlePosition, String circleId) {
        createFavor(circleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FavorItem>() {
                    @Override
                    public void onCompleted() {
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view == null) {
                            return;
                        }
                        Log.i(TAG, e.toString());
                        view.showToast(view.getResString(R.string.error));
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(FavorItem data) {
                        if (view == null) {
                            return;
                        }
                        if (data != null) {
                            view.update2AddFavor(circlePosition, data);
                        } else {
                            view.showToast(view.getResString(R.string.favor_failed));
                        }
                    }
                });
    }

    @Override
    public void deleteFavor(final int circlePosition, final String favorId) {
        deleteFavor(favorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view == null) {
                            return;
                        }
                        Log.i(TAG, e.toString());
                        view.showToast(view.getResString(R.string.error));
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(Boolean result) {
                        if (view == null) {
                            return;
                        }
                        if (result) {
                            view.update2DeleteFavor(circlePosition, favorId);
                        } else {
                            view.showToast(view.getResString(R.string.delete_failed));
                        }
                    }
                });
    }

    @Override
    public void addComment(String content, final CommentConfig config) {
        if (config == null) {
            return;
        }
        createComment(content, config)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommentItem>() {
                    @Override
                    public void onCompleted() {
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view == null) {
                            return;
                        }
                        Log.i(TAG, e.toString());
                        view.showToast(view.getResString(R.string.error));
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(CommentItem data) {
                        if (view == null) {
                            return;
                        }
                        if (data != null) {
                            view.update2AddComment(config.circlePosition, data);
                        } else {
                            view.showToast(view.getResString(R.string.comment_failed));
                        }
                    }
                });
    }

    @Override
    public void deleteComment(final int circlePosition, final String commentId) {
        deleteComment(commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view == null) {
                            return;
                        }
                        Log.i(TAG, e.toString());
                        view.showToast(view.getResString(R.string.error));
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(Boolean result) {
                        if (view == null) {
                            return;
                        }
                        if (result) {
                            view.update2DeleteComment(circlePosition, commentId);
                        } else {
                            view.showToast(view.getResString(R.string.delete_failed));
                        }
                    }
                });
    }
}
```

`MainActivity.java` - 日记展现类，通过`CirclePresenter`获取的数据后再调用`View`的接口来展示和更新数据。（已去除`MVP`无关代码）
``` java
public class MainActivity extends BaseActivity implements CircleContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new CirclePresenter(this);
        refreshView();
        DroiUpdate.update(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = User.getCurrentUser(User.class);
        if (user == null || !currentUserId.equals(user.getUserId())) {
            if (user != null) {
                currentUserId = user.getUserId();
            }
            circleAdapter.notifyDataSetChanged();
            refreshView();
        }
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.recycle();
        }
        super.onDestroy();
    }

    void refreshView() {
        recyclerView.getSwipeToRefresh().post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setRefreshing(true);
                refreshListener.onRefresh();
            }
        });
    }

    @Override
    public void update2DeleteCircle(String circleId) {
        List<CircleItem> circleItems = circleAdapter.getDatas();
        for (int i = 0; i < circleItems.size(); i++) {
            if (circleId.equals(circleItems.get(i).getObjectId())) {
                circleItems.remove(i);
                circleAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void update2AddFavor(int circlePosition, FavorItem addItem) {
        if (addItem != null) {
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
            if (item.getFavorList() == null) {
                List<FavorItem> favors = new ArrayList<>();
                item.setFavorList(favors);
            }
            item.getFavorList().add(addItem);
            circleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void update2DeleteFavor(int circlePosition, String favorId) {
        CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
        List<FavorItem> items = item.getFavorList();
        for (int i = 0; i < items.size(); i++) {
            if (favorId.equals(items.get(i).getObjectId())) {
                items.remove(i);
                circleAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void update2AddComment(int circlePosition, CommentItem addItem) {
        if (addItem != null) {
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
            if (item.getCommentList() == null) {
                List<CommentItem> comments = new ArrayList<>();
                item.setCommentList(comments);
            }
            item.getCommentList().add(addItem);
            circleAdapter.notifyDataSetChanged();
        }
        editText.setText("");
    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId) {
        CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
        List<CommentItem> items = item.getCommentList();
        for (int i = 0; i < items.size(); i++) {
            if (commentId.equals(items.get(i).getObjectId())) {
                items.remove(i);
                circleAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        editTextBody.setVisibility(visibility);
        measureCircleItemHighAndCommentItemOffset(commentConfig);
        if (View.VISIBLE == visibility) {
            editText.requestFocus();
            CommonUtils.showSoftInput(editText.getContext(), editText);
        } else if (View.GONE == visibility) {
            CommonUtils.hideSoftInput(editText.getContext(), editText);
        }
    }

    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {
        if (datas == null) {
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
            return;
        }
        if (loadType == TYPE_PULL_REFRESH) {
            recyclerView.setRefreshing(false);
            circleAdapter.setDatas(datas);
        } else if (loadType == TYPE_UPLOAD_REFRESH) {
            circleAdapter.getDatas().addAll(datas);
        }
        circleAdapter.notifyDataSetChanged();
        if (circleAdapter.getDatas().size() < 45 + CircleAdapter.HEAD_VIEW_SIZE) {
            recyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                    if (circleAdapter.getDatas().size() % LIMIT == 0) {
                        presenter.loadData(TYPE_UPLOAD_REFRESH);
                    } else {
                        recyclerView.hideMoreProgress();
                    }
                }
            }, 1);
        } else {
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
        }
    }

    @Override
    public void showLoading(String msg) {
    }

    @Override
    public void hideLoading() {
        recyclerView.setRefreshing(false);
        recyclerView.hideMoreProgress();
    }

    @Override
    public void showToast(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getResString(@StringRes int resId) {
        return getString(resId);
    }
}
```

### 云端逻辑实现
前面已经提到了整个App没有搭建自己的服务器，只写了两个`CloudCode`云代码，整个云端逻辑都是通过`DroiBaaS`来提供，具体用到了如下功能 

 - 使用`CoreSDK`的来搭建App的用户系统，管理云数据（日记、点赞、评论等）
 - 使用版本更新SDK来完成应用的自更新，包括手动更新，给以后的APP更新提供通道
 - 使用用户反馈SDK来收集用户的建议和意见，持续改进自己的产品
 - 使用统计SDK来获取统计用户的新增、日活、以及其他自定义事件
 - 使用消息推送SDK来完成应用的推送功能，以后能够做一些营销或者是事务通知

但是如何来使用，下面我来按步骤一一介绍。
其实官网也有快速入门文档，根据快速入门文档来操作，也能很快上手。
链接：http://www.droibaas.com/html/doc_24138.html

 1. 注册`DroiBaaS`帐号
在网址栏输入www.droibaas.com或者在百度输入`DroiBaaS`进行搜索，打开官网后，点击右上角的“注册”按钮，在跳转页面填入你的手机、设置密码，收到验证码填入后就能激活你的`DroiBaaS`账户，然后就可以用`DroiBaaS`的各种SDK来轻松开发应用了。
 2. 网站后台创建应用
使用注册好的账号登录进入`DroiBaaS`控制台后，点击控制台界面左上角“创建应用”，在弹出框输入你应用的名称，然后点击确认，你就拥有了一个等待开发的应用。
 3. 获取应用密钥
选择你要开发的应用，进入该应用的应用管理界面
在跳转页面，进入应用设置/安全密钥，点击复制，即可得到AppID
 4. 下载和安装`DroiBaaS` SDK
在官网上点击上方的下载按钮就能够下载到`DroiBaaS`的SDK，比较好的是还能够支持打包下载，就不需要我一个一个的去下载了，还是挺方便
安装SDK流程比较简单根据快速入门以及其他SDK的引导的安装步骤来操作就OK，`DroiBaaS`使用的是GitHub的maven仓库，这样做的好处有两个
 - 省去了拷贝aar包到lib目录的步骤，自动从网上下载
 - 每次编译发布的时候都能够用到SDK的最新版本
但也存在缺点，因为GitHub的网站被墙了，在国内访问还是比较慢的，所以在下载aar包的时候有时候速度会比较慢。
 5. 使用`DroiBaaS`功能

### 搭建App用户系统
`DroiBaaS`的`CoreSDK`提供了`DroiUser`类能够用来建立用户系统，在这里我创建了一个类`User`继承于`DroiUser`，在这个类中添加一些自己App需要的属性，比如：`nickName`、`headIcon`等
``` java
public class User extends DroiUser {
    @DroiExpose
    private DroiFile headIcon;
    @DroiExpose
    private String nickName;

    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public DroiFile getHeadIcon() {
        return headIcon;
    }
    public void setHeadIcon(DroiFile headIcon) {
        this.headIcon = headIcon;
    }
}
```

注册用户
``` java
User user = new User();
if (user == null) {
    user = new User();
}
user.setUserId(username);
user.setPassword(password);
DroiPermission permission = new DroiPermission();
permission.setPublicReadPermission(true);
user.setPermission(permission);
DroiError droiError = user.signUp();
```

登陆
``` java
DroiError droiError = new DroiError();
User user = DroiUser.login(username, password, User.class, droiError);
```

修改密码
``` java
DroiUser myUser = DroiUser.getCurrentUser();
if (myUser != null && myUser.isAuthorized() && !myUser.isAnonymous()) {
    DroiError droiError = myUser.changePassword(oldPassword, newPassword);
}
```

上传头像
``` java
DroiFile headIcon = new DroiFile(new File(path));
User user = DroiUser.getCurrentUser(User.class);
DroiPermission permission = new DroiPermission();
permission.setPublicReadPermission(true);
headIcon.setPermission(permission);
user.setHeadIcon(headIcon);
user.saveInBackground(new DroiCallback<Boolean>() {
    @Override
    public void result(Boolean aBoolean, DroiError droiError) {
        if (aBoolean) {
            Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
        }
    }
});
```

### 使用云数据来管理日记内容
创建一个日记数据类`CircleItem`继承于`DroiObject`，使用`save`方法就能够在云端的数据库保存日记数据了。相当的简单和方便，传统的使用方式往往还要服务端编写一个接口，客户端和服务端定好相应的协议，使用http协议并携带相应的数据来访问接口，才会完成相应的操作。使用`DroiBaaS`的云数据功能，大大简化了流程，下面我们来看一看具体的使用
``` java
public class CircleItem extends DroiObject {
    public final static int TYPE_URL = 1;
    public final static int TYPE_IMG = 2;
    @DroiExpose
    private String content;
    @DroiExpose
    private int type;//1:链接  2:图片
    @DroiExpose
    private String linkImg;
    @DroiExpose
    private String linkTitle;
    @DroiExpose
    private List<DroiFile> photos;
    @DroiReference
    private User user;
    @DroiExpose
    private List<CommentItem> commentList;
    @DroiExpose
    private List<FavorItem> favorList;
    @DroiExpose
    private int favorCount;
}
```

发布日记：
``` java
CircleItem data = new CircleItem();
data.setContent(content);
data.setUser(User.getCurrentUser(User.class));
data.setType(2); //保留字段
data.setPhotos(createPhotos(items));
DroiPermission permission = new DroiPermission();
permission.setPublicReadPermission(true);
permission.setPublicWritePermission(false);
data.setPermission(permission);
DroiError droiError = data.save();
```

查询获取日记、评论、点赞数据。这里采用由`CloudCode`云代码组装数据，来减少请求次数。
``` java
private static Observable<List<CircleItem>> getCircleData() {
    return Observable.create(new Observable.OnSubscribe<List<CircleItem>>() {
        @Override
        public void call(final Subscriber<? super List<CircleItem>> subscriber) {
            try {
                List<CircleItem> circleData;
                CircleParameter parameter = new CircleParameter();
                parameter.limit = LIMIT;
                parameter.offset = index * LIMIT;
                DroiError droiError = new DroiError();
                CircleResult result = DroiCloud.callRestApi(API_KEY, "/api/v2/getCircleList",
                        DroiCloud.Method.POST, parameter, CircleResult.class, droiError);
                circleData = result.getCircles();
                if (droiError.isOk()) {
                    index++;
                    subscriber.onNext(circleData);
                } else {
                    subscriber.onError(new Exception(droiError.toString()));
                }
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                subscriber.onCompleted();
            }
        }
    });
}
```

获取日记数据的云代码 - `getCircleList.lua`
``` lua
local DroiObject = Droi.Object;
local DroiQuery = Droi.Query
local buildQuery = Droi.Condition();

local COLL_CircleItem = 'CircleItem'   
local COLL_CommentItem = 'CommentItem'
local COLL_FavorItem = 'FavorItem'  
local ROLE = 'AM5oaVcBMEMbzuTr-e86suZmlWYnjjiiL0zyLZvK'

local _M = {}

function _M.main( req )
    if (not req ) then
        return {code = 1000, desc = "params error"}
    end
    local off = req.offset or 0
    local size = req.limit or 10
    size = size>1000 and 1000 or size
    local records = {}
    local clause = buildQuery:QueryCondition()
    local query = DroiQuery(COLL_CircleItem, clause, ROLE)
    local resp = query:addDescending('_CreationTime'):offset(off):limit(size):includeDepth(3):find()
    if (resp:isSuccess()) then
        local list = resp:getRawData();
        if (#list>0) then
            for i=1,#list,1 do
                local item = list[i]
                local circleId = item['_Id']
                item.commentList = {}
                local clause2 = buildQuery:QueryCondition('circleId',buildQuery.OP.EQ, circleId)
                local query2 = DroiQuery(COLL_CommentItem, clause2, ROLE)
                local resp2 = query2:addAscending('_CreationTime'):limit(1000):includeDepth(3):find()
                if(resp2:isSuccess()) then
                    item.commentList = resp2:getRawData()
                end
                item.favorList  = {}
                item.favorCount = 0
                local clause3 = buildQuery:QueryCondition('circleId',buildQuery.OP.EQ, circleId)
                local query3 = DroiQuery(COLL_FavorItem, clause3, ROLE)
                local resp3 = query3:addAscending('_CreationTime'):limit(1000):includeDepth(3):find()
                if(resp3:isSuccess()) then
                    item.favorList = resp3:getRawData()
                    item.favorCount = resp3:getCount()
                end
                table.insert(records, item)
            end
        end
    end
    return { code = 0, data = records}
end

return _M
```

我们也提供删除一条圈子日记的方法（连同删除相关的评论和赞）

删除日记的云代码 - `removeCircle.lua`
``` lua
local DroiObject = Droi.Object;
local DroiQuery = Droi.Query
local buildQuery = Droi.Condition();

local COLL_CircleItem = 'CircleItem'   
local COLL_CommentItem = 'CommentItem'
local COLL_FavorItem = 'FavorItem'  
local ROLE = 'AM5oaVcBMEMbzuTr-e86suZmlWYnjjiiL0zyLZvK'

local _M = {}

function _M.main( req )
    if (not req or not req.circleId) then
        return { code = 1000, desc = "params error"}
    end
    local circleId = req.circleId
    local obj = DroiObject( COLL_CircleItem, circleId, ROLE)
    if (not obj) then
        return { code = 1001, desc = "no the circle"}
    end
    local respx = obj:destroy()
    if (not respx:isSuccess()) then
        return {code = respx:getCode() , desc = respx:getMessage()}
    end
    -- delete all comment by circleId
    local clause = buildQuery:QueryCondition('circleId',buildQuery.OP.EQ, circleId)
    local query = DroiQuery(COLL_CommentItem, clause, ROLE)
    local resp = query:limit(1000):find()
    if (resp:isSuccess()) then
        local list = resp:getResult()
        if (#list>0) then
            DroiObject.destroyAll( list, ROLE)
        end
    end
    -- delete all favor by circleId
    local clause = buildQuery:QueryCondition('circleId',buildQuery.OP.EQ, circleId)
    local query = DroiQuery(COLL_FavorItem, clause, ROLE)
    local resp = query:limit(1000):find()
    if (resp:isSuccess()) then
        local list = resp:getResult()
        if (#list>0) then
            DroiObject.destroyAll( list, ROLE)
        end
    end
    return { code = 0, desc = "ok" }
end

return _M
```

所有Save的数据都会在云端以数据库的形式保存，方便下次查询，如下图
App中用到的数据类（`CircleItem`，`FavorItem`，`CommentItem`，`User`）在云端都会生成相应的表格,我原来需要通过N个步骤才能实现的云端数据存储，现在只需要调用`DroiObject.save`就能一键保存至云端并生产相应表格。
![](http://ojx2540cr.bkt.clouddn.com/%E4%BA%91%E6%95%B0%E6%8D%AE.png)
### `DroiBaaS`其他功能——自更新、用户反馈、统计、推送功能
手动更新和用户反馈功能可以通过在“我的”页面点击按钮来调用，推送功能可以在初始化时添加，统计功能按照自己的统计需求进行打点上传数据，使用这些SDK都需要再`Application`的`onCreate`中进行初始化
``` java
public class MyApplication extends Application {

   @Override
   public void onCreate() {
      super.onCreate();
      Core.initialize(this);
      DroiFeedback.initialize(this);
      DroiUpdate.initialize(this);
      DroiAnalytics.initialize(this);
      DroiPush.initialize(this);
   }
}
```

#### 版本更新
当我们的产品在重大Bug修复、功能增加、增加变现入口的时候，需要对我们的App进行升级，升级的成功率至关重要，一个好的自更新SDK能省不少事。  
1. 版本更新SDK在此工程中，总共在两处添加接口调用。一次是在应用进入时，在入口`Activity`的`onCreate`中，主要实现在应用进入的时候自动检查是否有更新，有更新的话会帮你下载并安装（同时支持静默更新和强制更新），添加了如下代码：
``` java
DroiUpdate.update(this);
``` 
还有一次是在我的页面中，通过手动点击的方式调用来检查云端是否有版本需要更新：
``` java
DroiUpdate.manualUpdate(this)
```

2. 在`DroiBaaS`后台配置自更新，配置界面如下
![](http://ojx2540cr.bkt.clouddn.com/%E8%87%AA%E6%9B%B4%E6%96%B0.png)
#### 用户反馈
我们需要通过意见反馈来知道用户对应用的评价以及反馈，帮助我们持续改进App，通过点击按钮进入反馈的界面：
``` java
DroiFeedback.callFeedback(this);
```
所有的用户反馈在web控制台都能够看得到，你还可以选择对某些反馈进行回复，App用户也能看到相应的回复，如图
![](http://ojx2540cr.bkt.clouddn.com/%E7%94%A8%E6%88%B7%E5%8F%8D%E9%A6%88.png)

#### 消息推送
通过消息推送增加应用的日活，方便活动的推广等。只需在`Application`中添加一行代码即可实现：
``` java
DroiPush.initialize(this);
```

在`DroiBaaS`后台可以发送推送通知
![](http://ojx2540cr.bkt.clouddn.com/%E6%8E%A8%E9%80%81.png)

#### 统计功能
大数据时代，大家对于数据也越来越看重，怎么样收集自己App的用户数据，以利于分析用户分行为，为之后产品改进以及运营提供重要的策略指导。  
那么我在App尝试在哪些地方打点记录用户行为，具体如下：
1. 每个页面的跳转，主要是记录页面的访问记录以及每个页面的停留时间，`DroiBaaS`的统计SDK本身提供了记录页面访问的方式，我只需要在`BaseActivity`里面加上相应代码即可。
``` java
@Override
protected void onResume() {
    super.onResume();
    DroiAnalytics.onResume(BaseActivity.this);
}

@Override
protected void onPause() {
    super.onPause();
    DroiAnalytics.onPause(BaseActivity.this);
}
```

2. 用户点赞和评论按钮记录，主要是为了记录用户的活跃时间段以及互动的意愿。按钮的点击记录通过`DroiBaaS`统计SDK的自定义事件来实现
``` java
@Override
public void onItemClick(ActionItem actionitem, int position) {
    switch (position) {
        case 0://点赞、取消点赞
                DroiAnalytics.onEvent(context,"Favor");
            break;
        case 1://发布评论
                DroiAnalytics.onEvent(context,"Comment");
            break;
        default:
            break;
    }
}
```

在`DroiBaaS`后台能够看到所有用户的详细使用数据了
![](http://ojx2540cr.bkt.clouddn.com/%E7%BB%9F%E8%AE%A1.png)
