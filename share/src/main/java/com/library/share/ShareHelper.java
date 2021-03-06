package com.library.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import com.library.utils.bitmap.BitmapUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/10.
 */

public class ShareHelper {

    private final int IMAGE_SIZE = 100;

    private String mQQKey;
    private String mWechatKey;

    private ShareHelper() {

    }

    private static class ShareHelperManager {
        private static ShareHelper sShareHelper = new ShareHelper();
    }

    public static ShareHelper getInstances() {
        return ShareHelperManager.sShareHelper;
    }

    public void init(String qq_app_key, String wechat_app_key) {
        mQQKey = qq_app_key;
        mWechatKey = wechat_app_key;
    }

    public void share(Activity activity, ShareItem shareItem) {
        if (null == activity || null == shareItem) {
            return;
        }
        switch (shareItem.getTarget()) {
            case ShareItem.SHARE_QQ:
                //分享到QQ
                shareQQ(activity, shareItem);
                break;
            case ShareItem.SHARE_QZONE:
                //分享到QQ空间
                shareQzone(activity, shareItem);
                break;
            case ShareItem.SHARE_WECHAT:
                //分享到微信
                shareWechat(activity, shareItem);
                break;
            case ShareItem.SHARE_WECHATMOMENTS:
                //分享到朋友圈
                shareWechatMoments(activity, shareItem);
                break;
        }
    }

    private void shareQQ(Activity activity, ShareItem shareItem) {
        Tencent tencent = Tencent.createInstance(mQQKey, activity.getApplicationContext());
        Bundle params = new Bundle();
        // 分享的类型 必填
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        // 分享的标题, 最长30个字符。 必填
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareItem.getTitle());
        // 分享的消息摘要，最长40个字。 可选 ----content字段(content为空取url,url为空取title)
        String summary = shareItem.getTitle();
        if (!TextUtils.isEmpty(shareItem.getUrl())) {
            summary = shareItem.getUrl();
        }
        if (!TextUtils.isEmpty(shareItem.getContent())) {
            summary = shareItem.getContent();
        }
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        //分享地址
        if (!TextUtils.isEmpty(shareItem.getUrl())) {
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareItem.getUrl());
        }
        // 分享图片地址(本地图片地址)
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareItem.getImage());
        // 分享图片地址(网络地址)
        // params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareParams.getImagePath());
        // 不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框
        // params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        // 享时隐藏分享到QZone按钮
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        tencent.shareToQQ(activity, params, shareItem.getShareQQListener());
    }

    private void shareQzone(Context context, ShareItem shareItem) {
        Tencent tencent = Tencent.createInstance(mQQKey, context.getApplicationContext());
        Bundle params = new Bundle();
        // 分享的类型 必填
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        // 分享的标题, 最长30个字符。 必填
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareItem.getTitle());
        // 点击后的跳转URL 必填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareItem.getUrl());
        // 分享的消息摘要，最长40个字。 可选 ----content字段(content为空取url,url为空取title)
        String summary = shareItem.getTitle();
        if (!TextUtils.isEmpty(shareItem.getUrl())) {
            summary = shareItem.getUrl();
        }
        if (!TextUtils.isEmpty(shareItem.getContent())) {
            summary = shareItem.getContent();
        }
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        // 分享图片地址(本地、网络图片地址)-----支持传多个imageUrl
        ArrayList<String> imageUrls = new ArrayList<>();
        String[] imageArray = shareItem.getImage().split(",");
        if (null != imageArray && imageArray.length > 0) {
            for (int i = 0; i < imageArray.length; i++) {
                imageUrls.add(imageArray[i]);
            }
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        tencent.shareToQzone((Activity) context, params, shareItem.getShareQQListener());
    }

    /***
     * 分享微信好友
     *
     * @param context
     * @param shareItem
     */
    private void shareWechat(Context context, ShareItem shareItem) {
        shareToWechat(context, shareItem, SendMessageToWX.Req.WXSceneSession);
    }

    /***
     * 分享微信朋友圈
     *
     * @param context
     * @param shareItem
     */
    private void shareWechatMoments(Context context, ShareItem shareItem) {
        shareToWechat(context, shareItem, SendMessageToWX.Req.WXSceneTimeline);
    }

    private void shareToWechat(Context context, ShareItem shareItem, int scene) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, mWechatKey, true);
        iwxapi.registerApp(mWechatKey);
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(shareItem.getImage())) {
            Bitmap b = BitmapUtils.decodeBitmap(shareItem.getImage(), IMAGE_SIZE, IMAGE_SIZE);
            bitmap = BitmapUtils.compressImage(b, 32);
            wxMediaMessage.thumbData = BitmapUtils.bitmap2Bytes(bitmap);
            if (b != bitmap) {
                BitmapUtils.recycleBitmap(b);
            }
        }
        switch (shareItem.getType()) {
            case ShareItem.SHARE_TYPE_TEXT:
                //分享文本
                WXTextObject wxTextObject = new WXTextObject();
                wxTextObject.text = shareItem.getTitle();
                wxMediaMessage.mediaObject = wxTextObject;
                break;
            case ShareItem.SHARE_TYPE_IMAGE:
                //分享图片
                WXImageObject imgObj = new WXImageObject(bitmap);
                wxMediaMessage.mediaObject = imgObj;
                break;
            case ShareItem.SHARE_TYPE_URL:
                //分享链接
                WXWebpageObject wxWebpageObject = new WXWebpageObject();
                wxWebpageObject.webpageUrl = shareItem.getUrl();
                wxMediaMessage.mediaObject = wxWebpageObject;
                break;
            case ShareItem.SHARE_TYPE_MUSIC:
                //分享音乐
                WXMusicObject wxMusicObject = new WXMusicObject();
                wxMusicObject.musicUrl = shareItem.getMusic();
                wxMediaMessage.mediaObject = wxMusicObject;
                break;
            case ShareItem.SHARE_TYPE_VIDEO:
                //分享视频
                WXVideoObject wxVideoObject = new WXVideoObject();
                wxVideoObject.videoUrl = shareItem.getVideo();
                wxMediaMessage.mediaObject = wxVideoObject;
                break;
        }
        BitmapUtils.recycleBitmap(bitmap);
        wxMediaMessage.title = shareItem.getTitle();
        String description = shareItem.getTitle();
        if (!TextUtils.isEmpty(shareItem.getUrl())) {
            description = shareItem.getUrl();
        }
        if (!TextUtils.isEmpty(shareItem.getContent())) {
            description = shareItem.getContent();
        }
        wxMediaMessage.description = description;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = wxMediaMessage;
        req.scene = scene;
        iwxapi.sendReq(req);
    }
}
