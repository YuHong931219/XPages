package com.xuexiang.xpagedemo;

import android.app.Application;

import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xpage.base.XPageSimpleListFragment;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xutil.XUtil;

import java.util.List;

/**
 * @author xuexiang
 * @date 2018/1/7 下午6:40
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        XUtil.init(this);
        XUtil.debug(true);

        //页面注册
        PageConfig.getInstance()
                .setPageConfiguration(context -> {
                    //自动注册页面,是编译时自动生成的，build一下就出来了 AppPageConfig.getInstance().getPages()
                    return null;
                })
                .debug("PageLog")       //开启调试
                .enableWatcher(false)   //设置是否开启内存泄露监测
                .setContainActivityClazz(XPageActivity.class) //设置默认的容器Activity
                .init(this);   //初始化页面配置
    }

    /**
     * 增加组件信息和子演示页信息
     *
     * @param clazz 【继承了ListSimpleFragment的类】
     */
    private void addPageInfoAndSubPages(List<PageInfo> pageInfos, Class<? extends XPageSimpleListFragment> clazz) {
        pageInfos.add(PageConfig.getPageInfo(clazz));
        try {
            registerPageInfos(pageInfos, clazz.newInstance().getSimplePageClasses());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册多个页面信息
     *
     * @param clazz
     * @return
     */
    private void registerPageInfos(List<PageInfo> pageInfos, Class... clazz) {
        for (Class aClazz : clazz) {
            pageInfos.add(PageConfig.getPageInfo(aClazz));
        }
    }


}
