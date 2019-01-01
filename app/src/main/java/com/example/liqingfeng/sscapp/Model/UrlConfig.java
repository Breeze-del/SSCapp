package com.example.liqingfeng.sscapp.Model;

import com.example.liqingfeng.sscapp.Presenter.Util.ViewUtil.ScreenUtils;

/**
 * 记录所有的接口的地址
 */
public class UrlConfig {

    /**
     * 获取图片的基地址
     */
    public static String imageBaseUrl="http://wangzhengyu.cn/avatars";
    /**
     * 获取业务接口的基地址
     */
    public static String bnsBaseUrl="http://wangzhengyu.cn/sports_back";
    /**
     * 运动模块接口
     */
    public static String SpmodleUrl="/sports/sports.do";
    /**
     * 运动房间接口
     */
    public static String SproomUrl="/room/rooms.do";
    /**
     * 登陆接口
     */
    public static String loginUrl="/user/login.do";
    /**
     * 注册接口
     */
    public static String registerUrl="/user/register.do";
    /**
     *获取验证码接口
     */
    public static String verifyUrl="/verify/getVerify.do";
    /**
     * 通过id查找用户
     */
    public static String findUserByID="/user/user.do";
    /**
     * 用户信息文件存放地址
     */
    public static String userInformation="user.txt";
    /**
     * 参数  userId 头像avatar 将头像上转到后台
     */
    public static String sendHeadPicture="/user/setAvatar.do";
    /**
     * 将修改的个人信息上传到后台
     */
    public static String changeUserInfo = "/user/update.do";
    /**
     * 加入房间接口
     */
    public static String enterRoomUrl = "/room/enter.do";
    /**
     *  运动记录接口
     */
    public static String sportsLogUrl = "/user/sportsLog.do";
    /**
     * 创建房间
     */
    public static String createRoomUrl = "/room/create.do";
    /**
     *  聊天接口
     */
    public static String chatRoomUrl = "ws://wangzhengyu.cn/sports_back/community/";
    /**
     * 改密码接口
     */
    public static String changePasswordUrl = "/user/setPass.do";
    /**
     * 删除房间
     */
    public static String deleteRoomUrl="/room/end.do";
    /**
     * 发送反馈
     */
    public static String sendFeedbackByUrl= "/complain/insert.do";
    /**
     * 签到接口
     */
    public static String signUrl = "/user/sign.do";
}
