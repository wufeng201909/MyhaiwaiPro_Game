package com.sdk.mysdklibrary.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sdk.mysdklibrary.Tools.ResourceUtil;

public class PromptDialog extends Dialog {
    public final static String OFFICIAL = "official";
    public final static String WALLET = "wallet";
    public final static String PAY_SUC = "success";
    public final static String PAY_FAILED = "failed";
    public final static String PAY_WALLET_SUC = "wallet_suc";
    public final static String PAY_WALLET_FAILED = "wallet_failed";
    private Context mCon;
    private String mContent;
    private String mType;

    private ClickInterface clickInterface;

    public PromptDialog(Context context, String content, int style_id,String type){
        super(context,style_id);
        this.mCon=context;
        this.mContent=content;
        this.mType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        int privacydialog_layout_id = ResourceUtil.getLayoutId(mCon,"myths_promptdialog");
        View view = LayoutInflater.from(mCon).inflate(privacydialog_layout_id,null,false);
        setContentView(view);

        LinearLayout lay_payresult = view.findViewById(ResourceUtil.getId(mCon,"lay_payresult"));
        ScrollView lay_dia = view.findViewById(ResourceUtil.getId(mCon,"lay_dia"));
        TextView pay_result_icon = view.findViewById(ResourceUtil.getId(mCon,"pay_result_icon"));
        TextView pay_result_content = view.findViewById(ResourceUtil.getId(mCon,"pay_result_content"));
        TextView content = view.findViewById(ResourceUtil.getId(mCon,"dialog_content"));
        TextView button_yes = view.findViewById(ResourceUtil.getId(mCon,"dialog_confirm"));
        if(this.mType == OFFICIAL||this.mType == WALLET){
            content.setText(mContent);
        }else if(this.mType == PAY_WALLET_FAILED||this.mType == PAY_WALLET_SUC){
            button_yes.setText(ResourceUtil.getString(mCon,"myths_paywallet_con"));
            lay_payresult.setVisibility(View.VISIBLE);
            lay_dia.setVisibility(View.GONE);
            pay_result_content.setText(mContent);
            if(this.mType == PAY_WALLET_FAILED){
                pay_result_icon.setBackgroundResource(ResourceUtil.getDrawableId(mCon,"myths_wallet_failed"));
            }else{
                pay_result_icon.setBackgroundResource(ResourceUtil.getDrawableId(mCon,"myths_wallet_success"));
            }
        }

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (clickInterface != null) {
                    clickInterface.doCofirm();
                }
            }
        });

        Window dialogWindow = getWindow();

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int width = 100;
        int height = 100;
        if(this.mType == OFFICIAL){
            width = ResourceUtil.dip2px(mCon,330);
            height = ResourceUtil.dip2px(mCon,340);
        }else if(this.mType == WALLET){
            width = ResourceUtil.dip2px(mCon,330);
            height = ResourceUtil.dip2px(mCon,250);
        }else if(this.mType == PAY_WALLET_FAILED||this.mType == PAY_WALLET_SUC){
            width = ResourceUtil.dip2px(mCon,300);
            height = ResourceUtil.dip2px(mCon,300);
        }

        lp.width = width;
        lp.height = height;
        dialogWindow.setAttributes(lp);
    }

    public void setClickListener(ClickInterface clickInterface){
        this.clickInterface=clickInterface;
    }

    /**
     * 点击事件的监听接口
     */
    public interface ClickInterface{
        void  doCofirm();
//        void doCancel();
    }
}
