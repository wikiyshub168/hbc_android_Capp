package com.huangbaoche.hbcframe.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.huangbaoche.hbcframe.activity.BaseFragmentActivity;
import com.huangbaoche.hbcframe.data.net.ErrorHandler;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.ArrayList;


public abstract class BaseFragment extends Fragment implements HttpRequestListener {
    public static String KEY_FRAGMENT_NAME = "key_fragment_name";

    public boolean needHttpRequest = true;
    public Callback.Cancelable cancelable;
    protected int contentId = -1;

    private boolean injected = false;
    private ErrorHandler errorHandler;
    private Fragment mTargetFragment;
    private ArrayList<EditText> editTextArray = new ArrayList<EditText>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        injected = true;
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!injected) {
            x.view().inject(this, this.getView());
        }
        initHeader();
        initView();

    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(this + " onStart");
        if(needHttpRequest){
            cancelable = requestData();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(this + " onResume");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        LogUtil.i(this + " onViewStateRestored");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtil.i(this + " onSaveInstanceState");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(this + " onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.i(this + " onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(this + " onDestroy");
        if(cancelable!=null){
            cancelable.cancel();
        }
    }


    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        if(errorHandler==null){
            errorHandler = new ErrorHandler(getActivity());
        }
        errorHandler.onDataRequestError(errorInfo, request);
        errorHandler = null;
    }

    @Override
    public void onDataRequestCancel(BaseRequest request) {

    }

    /**
     * fragment回调使用；<br/>
     * Fragment A 启用 Fragment B<br/>
     * B 向 A 回传 数据可以通过 B的finishForResult(bundle);<br/>
     * A 在 onFragmentResult(Bundle bundle) 可以收到B 回传的数据<br/>
     *
     * @param bundle 参数
     */
    public void onFragmentResult(Bundle bundle) {
    }


    /**
     * 初始化Header
     */
    protected abstract void initHeader();


    /**
     * 初始化界面
     */
    protected abstract void initView();

    /**
     * 请求方法 会在加载完成是调用
     */
    protected abstract Callback.Cancelable requestData();
    /**
     * 请求方法 会在加载完成是调用
     */
    protected Callback.Cancelable requestData(BaseRequest request){
        cancelable = HttpRequestUtils.request(getActivity(),request, this,null);
       return cancelable;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * 启动新的fragment
     * @param fragment
     */
    public void startFragment(BaseFragment fragment) {

        Bundle bundle = fragment.getArguments();
        bundle = bundle ==null?new Bundle():bundle;
        startFragment(fragment, bundle);
    }

    public void startFragment(BaseFragment fragment,Bundle bundle) {
        if (fragment == null) return;
        if (contentId == -1) throw new RuntimeException("BaseFragment ContentId not null, BaseFragment.setContentId(int)");
        ((BaseFragmentActivity) getActivity()).addFragment(fragment);
        collapseSoftInputMethod();
        editTextClearFocus();
        if(bundle!=null){
            fragment.setArguments(bundle);
        }
            fragment.setTarget(this);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(contentId, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

    }

    /**
     * 将fragment从后台堆栈中弹出,  (模拟用户按下BACK 命令).
     */
    public void finish() {
        //将fragment从后台堆栈中弹出,  (模拟用户按下BACK 命令).
        if(getFragmentManager()==null)return;
        getFragmentManager().popBackStack();
        Fragment fragment = this.getTarget();
        if (fragment != null && fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onResume();
        }
        ((BaseFragmentActivity) getActivity()).removeFragment(this);
    }
    /**
     * 回传数据使用，启动fragment 在 onFragmentResult中接收数据
     *
     * @param bundle 参数
     */
    public void finishForResult(Bundle bundle) {

        collapseSoftInputMethod();
        finish();
        Bundle mBundle = new Bundle();
        if(getArguments()!=null) mBundle.putAll(getArguments());
        if (bundle != null )mBundle.putAll(bundle);
        mBundle.putString(KEY_FRAGMENT_NAME, this.getClass().getSimpleName());
        Fragment fragment = this.getTarget();
        if (fragment != null && fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onFragmentResult(mBundle);
        }
        LogUtil.i(this + " finishForResult " + fragment);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 点击 back键的时候触发该方法，如果返回false，返回事件交给上层处理。如果返回true，则表示自己处理事件
     * @return
     */
    public boolean onBackPressed(){
        LogUtil.e(this + "onBackPressed");
        if(cancelable!=null)
            cancelable.cancel();
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    /**
     * 展示软键盘
     */
    public void showSoftInputMethod(EditText inputText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT);
    }
    /**
     * 收起软键盘
     */
    public void collapseSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null)
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    /**
     * 取消光标
     */
    public void editTextClearFocus() {
        if(editTextArray!=null&&editTextArray.size()>0){
            for(EditText editText:editTextArray){
                editText.clearFocus();
            }
        }
    }

    public Fragment getTarget() {
        return mTargetFragment;
    }

    public void setTarget(Fragment mTargetFragment) {
        this.mTargetFragment = mTargetFragment;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }
}