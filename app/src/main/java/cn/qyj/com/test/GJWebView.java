package cn.qyj.com.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GJWebView extends WebView {
    private int currentY = 0;
    private Context context;
    private int startX;
    public  boolean isSliding;
    private LeftSlidingListener leftSlidingListener;
    private RightSlidingListener rightSlidingListener;
    public  interface LeftSlidingListener{
        public void leftSlidingAction();
    }
    public  interface RightSlidingListener{
        public void rightSlidingAction();
    }

    public void setLeftSlidingListener(LeftSlidingListener leftSlidingListener) {
        this.leftSlidingListener = leftSlidingListener;
    }

    public void setRightSlidingListener(RightSlidingListener rightSlidingListener) {
        this.rightSlidingListener = rightSlidingListener;
    }

    public GJWebView(Context context) {
        super(context);
        init(context);
    }

    public GJWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public GJWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        initWebSettings();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        currentY = t;
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public int getCurrentY() {
        return currentY;
    }

    public boolean canZoomIn() {
        boolean canZoomIn = true;
        try {
            Field mActualScale = WebView.class.getDeclaredField("mActualScale");
            Field mMaxZoomScale = WebView.class
                    .getDeclaredField("mMaxZoomScale");
            mActualScale.setAccessible(true);
            mMaxZoomScale.setAccessible(true);
            canZoomIn = mActualScale.getFloat(this) < mMaxZoomScale
                    .getFloat(this);
        } catch (Exception e) {
            try {
                Field mZoomManager = WebView.class
                        .getDeclaredField("mZoomManager");
                if (mZoomManager != null) {
                    mZoomManager.setAccessible(true);
                    Object zoomManager = mZoomManager.get(this);
                    if (zoomManager != null) {
                        Field mEmbeddedZoomControl = zoomManager.getClass()
                                .getDeclaredField("mEmbeddedZoomControl");
                        if (mEmbeddedZoomControl != null) {
                            mEmbeddedZoomControl.setAccessible(true);
                            Object zoomControlEmbedded = mEmbeddedZoomControl
                                    .get(zoomManager);
                            if (zoomControlEmbedded != null) {
                                mZoomManager = zoomControlEmbedded.getClass()
                                        .getDeclaredField("mZoomManager");
                                if (mZoomManager != null) {
                                    mZoomManager.setAccessible(true);
                                    zoomManager = mZoomManager
                                            .get(zoomControlEmbedded);
                                    Method canZoomInMethod = zoomManager
                                            .getClass().getDeclaredMethod(
                                                    "canZoomIn");
                                    if (canZoomInMethod != null) {
                                        canZoomInMethod.setAccessible(true);
                                        Object canZoomInObj = canZoomInMethod
                                                .invoke(zoomManager,
                                                        new Object[0]);
                                        if (canZoomInObj != null
                                                && canZoomInObj instanceof Boolean) {
                                            return (Boolean) canZoomInObj;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e2) {
            }
        }
        return canZoomIn;
    }

    public boolean canZoomOut() {
        boolean canZoomOut = true;
        try {
            Field mActualScale = WebView.class.getDeclaredField("mActualScale");
            Field mMinZoomScale = WebView.class
                    .getDeclaredField("mMinZoomScale");
            Field mInZoomOverview = WebView.class
                    .getDeclaredField("mInZoomOverview");
            mActualScale.setAccessible(true);
            mMinZoomScale.setAccessible(true);
            mInZoomOverview.setAccessible(true);
            canZoomOut = mActualScale.getFloat(this) > mMinZoomScale
                    .getFloat(this) && !mInZoomOverview.getBoolean(this);
        } catch (Exception e) {
            try {
                Field mZoomManager = WebView.class
                        .getDeclaredField("mZoomManager");
                if (mZoomManager != null) {
                    mZoomManager.setAccessible(true);
                    Object zoomManager = mZoomManager.get(this);
                    if (zoomManager != null) {
                        Field mEmbeddedZoomControl = zoomManager.getClass()
                                .getDeclaredField("mEmbeddedZoomControl");
                        if (mEmbeddedZoomControl != null) {
                            mEmbeddedZoomControl.setAccessible(true);
                            Object zoomControlEmbedded = mEmbeddedZoomControl
                                    .get(zoomManager);
                            if (zoomControlEmbedded != null) {
                                mZoomManager = zoomControlEmbedded.getClass()
                                        .getDeclaredField("mZoomManager");
                                if (mZoomManager != null) {
                                    mZoomManager.setAccessible(true);
                                    zoomManager = mZoomManager
                                            .get(zoomControlEmbedded);
                                    Method canZoomOutMethod = zoomManager
                                            .getClass().getDeclaredMethod(
                                                    "canZoomOut");
                                    if (canZoomOutMethod != null) {
                                        canZoomOutMethod.setAccessible(true);
                                        Object canZoomOutObj = canZoomOutMethod
                                                .invoke(zoomManager,
                                                        new Object[0]);
                                        if (canZoomOutObj != null
                                                && canZoomOutObj instanceof Boolean) {
                                            return (Boolean) canZoomOutObj;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e2) {
            }
        }
        return canZoomOut;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void initWebSettings() {
        WebSettings webSettings = getSettings();
        webSettings.setRenderPriority(RenderPriority.HIGH);
        webSettings.setTextSize(TextSize.NORMAL);// 设置字体

        // 设置支持缩放
        webSettings.setAllowFileAccess(true);// 设置可以访问文件
        webSettings.setDomStorageEnabled(true); //teacher_welcome_open DOM storage API 功能
        // 设置屏幕自适应
        if (Integer.parseInt(Build.VERSION.SDK) <= 10) {
            webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);//支持内容重新布局
        } else {
            webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        }
        // 启用插件
        // webSettings.setPluginsEnabled(true);

        // 设置缓存
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCacheMaxSize(10 * 1204 * 1024);
        webSettings.setDatabaseEnabled(true);//teacher_welcome_open database storage API 功能
        webSettings.setBlockNetworkImage(false);//延迟加载图片，首先加载文字

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 去掉缩放按钮
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);// 是否支持变焦
        webSettings.setBuiltInZoomControls(true);// 设置WebView是否应该使用其内置变焦机制,显示放大缩小 
        webSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        this.setInitialScale(100);// 初始化时缩放
        webSettings.setJavaScriptEnabled(true);//支持js


        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        /**
         * 允许webview加载本地资源 进行js post请求
         */
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = this.getSettings().getClass();
                Method method = clazz.getMethod(
                        "setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(this.getSettings(), true);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//可以debug
            this.setWebContentsDebuggingEnabled(true);
        }
    }

}


