package com.example.community.util;

public interface CommunityConstant {

    /*
    激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /*
    重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /*
    激活失败
     */
    int ACTIVATION_FAILURE =  2;

    /*
    默认登录过期时常:一天
     */
    int DEFAULT_LOGIN_SECONDS = 3600 * 12;

    /*
    勾选记住我 登录过期时间：100天
     */
    int REMEMBER_LOGIN_SECONDS = 3600 * 24 * 100;

    /*
    帖子类型：评论
     */
    int ENTITY_TYPE_POST = 1;

    /*
    帖子类型：回复
     */
    int ENTITY_TYPE_COMMENT = 2;

    /*
    帖子类型：回复
     */
    int ENTITY_TYPE_USER = 3;

    /*
    主题类型：点赞
     */
    String TOPIC_LIKE = "like";

    /*
    主题类型：评论
     */
    String TOPIC_COMMENT = "comment";

    /*
    主题类型：关注
     */
    String TOPIC_FOLLOW = "follow";

    /*
    系统用户id
     */

    int SYSTEM_ID = 1;

}
