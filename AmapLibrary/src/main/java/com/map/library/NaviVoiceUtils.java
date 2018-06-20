package com.map.library;

import android.text.TextUtils;

/**
 *
 * @author jikun
 * @date 17/9/27
 */

public class NaviVoiceUtils {


    private final static String[] JUDGE_CONTAIN_STRINGS = {
            "大约需要",
            "违章拍照",
            "测速拍照",
            "车辆汇入",
            "当前车速"
    };

    private final static String[] JUDGE_EQUALS_STRINGS = {
            "请走右侧车道",
            "请走左侧车道"
    };

    /**
     * 判断导航回调的语音文本是否重要
     */
    public static boolean isImportantNavigationText(String text) {
        boolean isImport = true;
        if (TextUtils.isEmpty(text)) {
            isImport = false;
        } else {
            for (int i = 0; i < JUDGE_CONTAIN_STRINGS.length; i++) {

                if (text.contains(JUDGE_CONTAIN_STRINGS[i])) {
                    isImport = false;
                    break;
                }
            }
            for (int i = 0; i < JUDGE_EQUALS_STRINGS.length; i++) {
                if (text.equals(JUDGE_EQUALS_STRINGS[i])) {
                    isImport = false;
                    break;
                }
            }
        }


        return isImport;

    }


}
