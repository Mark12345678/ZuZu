package cn.liaojh.zuzu;

/**
 * Created by Liaojh on 2016/10/17.
 */

public class Contans {

    public  static final int STATE_NORMAL=0;
    public  static final int STATE_REFREH=1;
    public  static final int STATE_MORE=2;

    public static final String USER_JSON="user_json";

    //发布过的物品
    public static final int RELEASE_GOODS = 3;
    //搜索的物品
    public static final int SEARCH_GOODS = 4;
    //等待发货的物品
    public static final int WAITCOLLECT_GOODS = 5;
    //等待收回的物品
    public static final int WAITBACKGOODS = 6;
    //租赁历史
    public static final int RECORD = 7;
    //租赁进来
    public static final int RECORD_IN = 8;
    //租赁出去
    public static final int RECORD_OUT = 9;

    public static final String SEARCHFRAGMENT_TYPE = "SearchFragment_type";

    public static class API{

        //显示头像图片的路径
        public static final String SHOW_PHOTO = "http://150p743k61.iask.in:17063/name";

        public static final String SHOW_GOODS_PHOTO = "http://150p743k61.iask.in:17063/goods/";

        //public static final String BASE_URL="http://wx.liaojh.cn.tunnel.2bdata.com/ssh-test/";
        public static final String BASE_URL = "http://150p743k61.iask.in:17063/ssh-test/";

        //手机IP地址
        //public static final String BASE_URL = "http://150p743k61.iask.in:26042/ssh-test/";

        //获取横幅图片
        public static final String BANNER = BASE_URL + "getBannerForType";

        //注册
        public static final String REGITER = BASE_URL + "register";
        //查看手机号码是否已经注册
        public static final String FINDPHONE = BASE_URL + "findPhone";

        public static final String LOGIN = BASE_URL + "login";

        public static final String RELEASE = BASE_URL + "release";

        public static final String HOMEDATA = BASE_URL + "getGoods";

        //分类
        public static final String CATEGORY = BASE_URL + "getCategoryGoods";

        //物品详情
        public static final String DETAIL = BASE_URL + "goodsDetails";

        //添加好友
        public static final String ADDFRIEND = BASE_URL + "addFriend";

        //查找朋友列表
        public static final String FINDFRIENDLIST = BASE_URL + "findMyFriend";

        //删除喜欢标签
        public static final String CANCELLIKE = BASE_URL + "IDONlikeGood";

        //添加喜欢标签
        public static final String ADDLIKE = BASE_URL + "IlikeGood";

        //查找喜欢的物品
        public static final String FINDLIKE = BASE_URL + "findLike2";

        //查找的时候的Ajax
        public static final String SERACHAJAX = BASE_URL + "findBySubName";

        //根据关键字来搜索
        public static final String SEEACHKEY = BASE_URL + "findByLikeName";

        //跟换用户头像
        public static final String UPDATEHEAD = BASE_URL + "updateHead";

        //获取用户头像
        public static final String GETUSERHEAD = BASE_URL + "getHead";

        //获取用户自己发布过的物品
        public static final String USERRELEASE = BASE_URL + "getUserReleaseGoods";

        //获取物品图片
        public static final String GETGOODSPIC = BASE_URL + "getGoodsPic";

        //地图上根据类别来获取物品数据
        public static final String FINDBYMAPCATOGERY = BASE_URL + "findByMayCategory";

        //修改物主物品信息
        public static final String MODIFYGOODSMESSAGE = BASE_URL + "modifyGoodsMessage";

        //删除指定物品
        public static final String DELETEGOODS = BASE_URL + "deleteGoods";

        //为指定物品添加图片
        public static final String ADDGOODSPIC = BASE_URL + "addGoodsPic";

        //删除指定物品的某张图片
        public static final String DELETEGOODSPIC = BASE_URL + "deleteGoodsPic";

        //显示用户被授权的物品
        public static final String BYACCREDITSHOW = BASE_URL + "byAccreditShow";

        //用户确认支付
        public static final String USERPAYSURE = BASE_URL + "usetPaySure";

        //显示物主授权用户showAccredit
        public static final String SHOWACCREDIT = BASE_URL + "showAccredit";

        //物主添加授权用户 masterAddAccredit
        public static final String  ADDACCREDIT = BASE_URL + "masterAddAccredit";

        //物主删除授权用户 deleteAccredit
        public static final String DELETEACCREDIT = BASE_URL + "deleteAccredit";

        //物主确认付款 masterPaySure
        public static final String MASTERSUREPAY = BASE_URL + "masterPaySure";

        //获取待发货物品 showWaitCollect
        public static final String SHOWWAITCOLLECT = BASE_URL + "showWaitCollect";

        //获取物主待发货物品 masterShowWaitOut
        public static final String MASTERSHOWWAITOUT = BASE_URL + "masterShowWaitOut";

        //用户确认收货userSureWaitCollect
        public static final String USERSUREWAITCOLLECT = BASE_URL + "userSureWaitCollect";

        //物主确认已发货 masterSureWaitCollect
        public static final String MASTERSUREWAITCOLLECT = BASE_URL + "masterSureWaitCollect";

        //用户待还物品
        public static final String GOODSWAITBACK = BASE_URL + "showWaitBack";

        //用户确认物品已还 userSureWaitBack
        public static final String USERSUREWAITBACK = BASE_URL + "userSureWaitBack";

        //物主待收物品 masterShowWaitBack
        public static final String MASTERSHOWWAITBACK = BASE_URL + "masterShowWaitBack";

        //物主确认已经收到物品 masterSureWaitBack
        public static final String MASTERSUREWAITBACK = BASE_URL + "masterSureWaitBack";

        //用户租过进来的物品 InRentGoods
        public static final String INRENTGOODS = BASE_URL + "InRentGoods";

        //用户租过出去的物品 OutRentGoods
        public static final String OUTRENTGOODS = BASE_URL + "OutRentGoods";
    }

}
