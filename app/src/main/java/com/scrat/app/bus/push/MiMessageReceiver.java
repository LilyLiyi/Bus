package com.scrat.app.bus.push;

import android.content.Context;

import com.scrat.app.core.utils.L;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

/**
 * Created by yixuanxuan on 16/5/26.
 */
public class MiMessageReceiver extends PushMessageReceiver {

    /**
     * 接收服务器发送的透传消息
     */
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        L.v("onReceivePassThroughMessage is called. %s", message);
    }

    /**
     * 接收服务器发来的通知栏消息（用户点击通知栏时触发）
     */
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        L.v("onNotificationMessageClicked is called. %s", message);
    }

    /**
     * 接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
     */
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        L.v("onNotificationMessageArrived is called. %s", message);
    }

    /**
     * 接收客户端向服务器发送命令消息后返回的响应
     */
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        L.v("onCommandResult is called. %s", message);
    }
    //这里是注释，看是否上传成功

    /**
     * 接受客户端向服务器发送注册命令消息后返回的响应。
     */
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        L.v("onReceiveRegisterResult is called. %s", message);
    }

}