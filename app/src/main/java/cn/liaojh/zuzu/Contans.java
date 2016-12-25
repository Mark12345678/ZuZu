package cn.liaojh.zuzu;

/**
 * Created by Liaojh on 2016/10/17.
 */

public class Contans {

    public  static final int STATE_NORMAL=0;
    public  static final int STATE_REFREH=1;
    public  static final int STATE_MORE=2;

    public static final String USER_JSON="user_json";

    public static final int RELEASE_GOODS = 3;
    public static final int SEARCH_GOODS = 4;
    public static final String SEARCHFRAGMENT_TYPE = "SearchFragment_type";

    public static class API{

        //显示头像图片的路径
        public static final String SHOW_PHOTO = "http://150p743k61.iask.in:17063/name";

        public static final String SHOW_GOODS_PHOTO = "http://150p743k61.iask.in:17063/goods";

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
    }

}
