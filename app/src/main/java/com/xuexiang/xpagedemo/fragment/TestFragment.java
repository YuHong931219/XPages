package com.xuexiang.xpagedemo.fragment;

import android.content.Intent;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xpagedemo.R;

import butterknife.BindView;

import static com.xuexiang.xpagedemo.fragment.DateReceiveFragment.KEY_BACK_DATA;
import static com.xuexiang.xpagedemo.fragment.DateReceiveFragment.KEY_IS_NEED_BACK;

/**
 * @author xuexiang
 * @date 2018/1/7 下午11:27
 */
@Page(name = TestFragment.PAGE_NAME, extra = 123, params = {KEY_IS_NEED_BACK})
public class TestFragment extends XPageFragment {
    public final static String PAGE_NAME = "测试页面";
    @BindView(R.id.tv_content)
    TextView tvContent;


    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }

    @Override
    protected TitleBar initTitleBar() {
        return super.initTitleBar().setLeftClickListener(v -> {
            Intent intent = new Intent().putExtra(KEY_BACK_DATA, "==【返回的数据】==");
            setFragmentResult(500, intent);
            popToBack();
        });
    }

    @Override
    protected void initArgs() {

    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        tvContent.setText(getPageName());
    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListeners() {

    }

}
