package com.scrat.app.core.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.text.TextUtils;

import com.scrat.app.core.CommonContext;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Created by yixuanxuan on 16/4/12.
 */
public class Utils {
    public static boolean isEmpty(Collection<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static boolean isEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    public static DateFormat sShortDateFormat =
            java.text.DateFormat.getDateInstance(DateFormat.SHORT);

    public static String getUpdateString(long timestamp) {
        if (timestamp == 0L)
            return "未知";

        long now = System.currentTimeMillis();
        long distance = now - timestamp;
        distance /= 1000L;
        if(distance < 60L)
            return "刚刚";

        if(distance >= 60L && distance < 3600L)
            return (distance / 60L) + "分钟前";

        if(distance >= 3600L && distance < 24L * 3600L)
            return (distance / 3600L) + "小时前";

        return sShortDateFormat.format(timestamp);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean phoneNumValid(String mobiles) {
        String telRegex = "[1][358]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public static boolean personIdValidation(String text) {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        return text.matches(regx) || text.matches(reg1) || text.matches(regex);
    }

    public static void saveImage(Bitmap photo, String path) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(path, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(bos);
        }
    }

    public static boolean isX86CPU() {
        return "x86".equals(android.os.Build.CPU_ABI);
    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String getAndroidID(Context context) {
        try {
            ContentResolver cr = context.getContentResolver();
            return Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return "";
        }
    }

    private final static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static int toInt(byte[] b, int s, int n) {
        int ret = 0;

        final int e = s + n;
        for (int i = s; i < e; ++i) {
            ret <<= 8;
            ret |= b[i] & 0xFF;
        }
        return ret;
    }

    public static String toHexString(byte[] d, int s, int n) {
        final char[] ret = new char[n * 2];
        final int e = s + n;

        int x = 0;
        for (int i = s; i < e; ++i) {
            final byte v = d[i];
            ret[x++] = HEX[0x0F & (v >> 4)];
            ret[x++] = HEX[0x0F & v];
        }
        return new String(ret);
    }

    public static int getVersionCode() {
        try {
            String pkgName = CommonContext.getContext().getPackageName();
            return CommonContext.getContext().getPackageManager()
                    .getPackageInfo(pkgName, 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName() {
        try {
            String pkName = CommonContext.getContext().getPackageName();
            return CommonContext.getContext().getPackageManager()
                    .getPackageInfo(pkName, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
