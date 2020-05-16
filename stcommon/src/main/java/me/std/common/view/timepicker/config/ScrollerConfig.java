package me.std.common.view.timepicker.config;


import androidx.annotation.ColorRes;

import me.std.common.view.timepicker.data.Type;
import me.std.common.view.timepicker.data.WheelCalendar;
import me.std.common.view.timepicker.listener.OnDateChangeListener;
import me.std.common.view.timepicker.listener.OnDateSetListener;


/**
 * 滚动配置
 */
public class ScrollerConfig {
    public Type mType = DefaultConfig.TYPE;
    @ColorRes
    public int mToolbarBkgColor = DefaultConfig.TOOLBAR_BKG_COLOR; // 背景颜色
    @ColorRes
    public int mItemSelectorLine = DefaultConfig.ITEM_SELECTOR_LINE; // 选中线颜色
    @ColorRes
    public int mItemSelectorRect = DefaultConfig.ITEM_SELECTOR_RECT; // 选中框颜色

    public String mCancelString = DefaultConfig.CANCEL; // 取消
    public String mSureString = DefaultConfig.SURE; // 确认
    public String mTitleString = DefaultConfig.TITLE; // 标题
    public int mToolBarTVColor = DefaultConfig.TOOLBAR_TV_COLOR; // ToolBar的颜色

    public int mWheelTVNormalColor = DefaultConfig.TV_NORMAL_COLOR; // 滚轮默认颜色
    public int mWheelTVSelectorColor = DefaultConfig.TV_SELECTOR_COLOR; // 滚轮选中颜色
    public int mWheelTVSize = DefaultConfig.TV_SIZE; // 文字默认大小
    public boolean cyclic = DefaultConfig.CYCLIC; // 是否循环

    public String mYear = DefaultConfig.YEAR; // 年单位
    public String mMonth = DefaultConfig.MONTH; // 月单位
    public String mDay = DefaultConfig.DAY; // 日单位
    public String mHour = DefaultConfig.HOUR; // 小时单位
    public String mMinute = DefaultConfig.MINUTE; // 分钟单位

    public int  mTimeInterval = DefaultConfig.TIME_INTERVAL_MINUTE;

    public WheelCalendar mMinCalendar = new WheelCalendar(0); // 最小日期
    public WheelCalendar mMaxCalendar = new WheelCalendar(0); // 最大日期
    public WheelCalendar mCurCalendar = new WheelCalendar(System.currentTimeMillis()); // 当前日期

    public OnDateSetListener mCallback; // 日期选中确认回调

    public OnDateChangeListener mOnYearChangeListener; // 年份改变监听
    public OnDateChangeListener mOnMonthChangeListener; // 月份改变监听
    public OnDateChangeListener mOnDayChangeListener; // 日改变监听
    public OnDateChangeListener mOnHourChangeListener; // 小时改变监听
    public OnDateChangeListener mOnMiniteChangeListener; // 小时改变监听
}
