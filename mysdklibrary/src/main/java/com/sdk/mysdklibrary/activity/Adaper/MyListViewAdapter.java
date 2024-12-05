package com.sdk.mysdklibrary.activity.Adaper;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.activity.PromptDialog;

import java.util.ArrayList;

public class MyListViewAdapter extends BaseAdapter {
    private Activity con = null;
    private ArrayList<String[]> list_ = null;
    public MyListViewAdapter(Activity activity, ArrayList<String[]> data) {

        con = activity;
        list_ = data;
    }

    @Override
    public int getCount() {
        return list_.size();
    }

    @Override
    public Object getItem(int position) {
        return list_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder mHolder;
        final String[] item = list_.get(position);

        if (convertView == null) {
            mHolder = new ViewHolder();
            int paylistitem_id = ResourceUtil.getLayoutId(con,"myths_pay_griditem");
            convertView = LayoutInflater.from(con).inflate(paylistitem_id, null,false);
            mHolder.item_log = convertView.findViewById(ResourceUtil.getId(con,"item_log"));
            mHolder.item_name = convertView.findViewById(ResourceUtil.getId(con,"item_name"));
            mHolder.item_offic = convertView.findViewById(ResourceUtil.getId(con,"item_offic"));
            mHolder.item_offic_icon = convertView.findViewById(ResourceUtil.getId(con,"item_offic_icon"));
            mHolder.item_offic_con = convertView.findViewById(ResourceUtil.getId(con,"item_offic_con"));
            mHolder.item_lay_tips = convertView.findViewById(ResourceUtil.getId(con,"item_lay_tips"));
            mHolder.item_tip = convertView.findViewById(ResourceUtil.getId(con,"item_tip"));
            mHolder.item_rebate = convertView.findViewById(ResourceUtil.getId(con,"item_rebate"));
            mHolder.item_req = convertView.findViewById(ResourceUtil.getId(con,"item_req"));
            mHolder.item_lay = convertView.findViewById(ResourceUtil.getId(con,"item_lay"));
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        RelativeLayout.LayoutParams p= (RelativeLayout.LayoutParams)mHolder.item_lay.getLayoutParams();
        if(ResourceUtil.island(con)){
            p.width = ResourceUtil.dip2px(con,535);
            mHolder.item_lay.setBackgroundResource(ResourceUtil.getDrawableId(con,"myths_payitem_bg_land"));
        }else{
            p.width = ResourceUtil.dip2px(con,335);
            mHolder.item_lay.setBackgroundResource(ResourceUtil.getDrawableId(con,"myths_payitem_bg"));
        }
        mHolder.item_lay.setLayoutParams(p);

        mHolder.item_log.setBackgroundResource(ResourceUtil.getDrawableId(con,item[0]));
        mHolder.item_name.setText(item[2]);
        //是否有支付描述信息
        if(TextUtils.isEmpty(item[3])){
            mHolder.item_lay_tips.setVisibility(View.GONE);
        }else{
            mHolder.item_lay_tips.setVisibility(View.VISIBLE);
            mHolder.item_tip.setText(item[3]);
            if("32".equals(item[1])||"36".equals(item[1])){//钱包
                mHolder.item_tip.setTextColor(con.getResources().getColor(ResourceUtil.getColorId(con,"red")));
            }else if("33".equals(item[1])){//官方
                mHolder.item_tip.setTextColor(con.getResources().getColor(ResourceUtil.getColorId(con,"orange")));
            }else if("31".equals(item[1])||"34".equals(item[1])||"35".equals(item[1])) {//第三方本地支付
                mHolder.item_tip.setTextColor(con.getResources().getColor(ResourceUtil.getColorId(con,"gray2")));
                if(ResourceUtil.island(con)){
                    p.height = ResourceUtil.dip2px(con,65);
                }else{
                    p.height = ResourceUtil.dip2px(con,95);
                }
                mHolder.item_lay.setLayoutParams(p);
            }
            if(TextUtils.isEmpty(item[6])||"0".equals(item[6])){//'switch2' => 0or1,//百分比开关
                mHolder.item_rebate.setVisibility(View.GONE);
            }else{
                mHolder.item_rebate.setVisibility(View.VISIBLE);
            }
        }
        //'switch1' => 0or1,//官方标志开关
        if(TextUtils.isEmpty(item[5])||"0".equals(item[5])){
            mHolder.item_offic.setVisibility(View.GONE);
        }else{
            mHolder.item_offic.setVisibility(View.VISIBLE);
            if("33".equals(item[1])){
                mHolder.item_offic.setBackgroundResource(ResourceUtil.getDrawableId(con,"myths_official"));
                mHolder.item_offic_icon.setVisibility(View.GONE);
                mHolder.item_offic_con.setText("Official");
                mHolder.item_offic_con.setTextColor(con.getResources().getColor(ResourceUtil.getColorId(con,"white")));
            }else if("32".equals(item[1])||"36".equals(item[1])){
                mHolder.item_offic.setBackgroundResource(ResourceUtil.getDrawableId(con,"myths_usdt_bg"));
                mHolder.item_offic_icon.setVisibility(View.VISIBLE);
                mHolder.item_offic_con.setText("USDT");
                mHolder.item_offic_con.setTextColor(con.getResources().getColor(ResourceUtil.getColorId(con,"green")));
            }
        }
        //?文案是否为空
        mHolder.item_req.setVisibility(TextUtils.isEmpty(item[4])?View.GONE:View.VISIBLE);
        //?显示与否，或显示->
        if("31".equals(item[1])||"34".equals(item[1])||"35".equals(item[1])){//第三方本地支付
            mHolder.item_req.setVisibility(View.VISIBLE);
            mHolder.item_req.setBackgroundResource(ResourceUtil.getDrawableId(con,"myths_jiantou"));
        }else{
            mHolder.item_req.setBackgroundResource(ResourceUtil.getDrawableId(con,"myths_req"));
        }

        mHolder.item_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("31".equals(item[1])||"34".equals(item[1])||"35".equals(item[1])) {//第三方本地支付
                    return;
                }
                String type = PromptDialog.WALLET;
                if("32".equals(item[1])||"36".equals(item[1])){
                    type = PromptDialog.WALLET;
                }else if("33".equals(item[1])){
                    type = PromptDialog.OFFICIAL;
                }
                PromptDialog promptDialog = new PromptDialog(con,item[4],ResourceUtil.getStyleId(con,"ay_dialog_style"),type);
                promptDialog.setCancelable(true);// 点击返回键或者空白处消失
                promptDialog.setClickListener(new PromptDialog.ClickInterface() {
                    @Override
                    public void doCofirm() {
                        promptDialog.dismiss();
                    }
                });
                promptDialog.show();
            }
        });
        return convertView;
    }

    class ViewHolder {
        private RelativeLayout item_lay;//
        private TextView item_log;//支付icon
        private TextView item_name;//支付名称

        private LinearLayout item_offic;//official
        private ImageView item_offic_icon;
        private TextView item_offic_con;

        private LinearLayout item_lay_tips;//支付item描述
        private TextView item_tip;
        private LinearLayout item_rebate;//--100%

        private TextView item_req;//?
    }
}
