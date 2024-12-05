package com.sdk.mysdklibrary.Tools;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.TouchDelegate;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class ResourceUtil {
	 public static int getLayoutId(Context paramContext, String paramString) {
	    return paramContext.getResources().getIdentifier(paramString, "layout", paramContext.getPackageName());
	 }

	 public static int getStringId(Context paramContext, String paramString) {
		 int stringId = paramContext.getResources().getIdentifier(paramString, "string", paramContext.getPackageName());
		 if(stringId == 0){
			 stringId = paramContext.getResources().getIdentifier("net_error_0", "string", paramContext.getPackageName());
		 }
	    return stringId;
	 }
	 
	 public static String getString(Context paramContext,String paramString){
		 int stringId = paramContext.getResources().getIdentifier(paramString, "string", paramContext.getPackageName());
		 if(stringId == 0){
			 return "";
		 }
		 return paramContext.getString(stringId);
	 }

	 public static int getDrawableId(Context paramContext, String paramString) {
	    return paramContext.getResources().getIdentifier(paramString, "drawable", paramContext.getPackageName());
	 }

	 public static int getStyleId(Context paramContext, String paramString) {
	    return paramContext.getResources().getIdentifier(paramString, "style", paramContext.getPackageName());
	 }

	 public static int getId(Context paramContext, String paramString) {
	    return paramContext.getResources().getIdentifier(paramString, "id", paramContext.getPackageName());
	 }

	 public static int getColorId(Context paramContext, String paramString) {
	    return paramContext.getResources().getIdentifier(paramString, "color", paramContext.getPackageName()); 
	 }

	 public static int getDimenId(Context paramContext, String paramString) {
	    return paramContext.getResources().getIdentifier(paramString, "dimen", paramContext.getPackageName());
	 }
	 
	 public static int getAnimId(Context paramContext, String paramString) {
		    return paramContext.getResources().getIdentifier(paramString, "anim", paramContext.getPackageName());
		 }
	 
	 public static int getArrayId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "array", paramContext.getPackageName());
	}	
	 
	public static int getstyleableId(Context paramContext, String paramString) {
		    return paramContext.getResources().getIdentifier(paramString, "styleable", paramContext.getPackageName());
	}
	 
	public static int [] getstyleableArray(Context paramContext, String paramString) {
		int i = paramContext.getResources().getIdentifier(paramString, "styleable", paramContext.getPackageName());
		return paramContext.getResources().getIntArray(i);
    }

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	//判断横竖屏
	private static int island = 0;
	public static boolean island(Context mCon){
		if(island == 0){
			DisplayMetrics displayMetrics = mCon.getResources().getDisplayMetrics();
			int width = displayMetrics.widthPixels;
			int height = displayMetrics.heightPixels;
			island = width>height?1:2;
		}
		return island ==1?true:false;
	}

	//扩大点击域
	public static void expandTouchArea(final View view, final int size) {
		final View parentView = (View) view.getParent();
		parentView.post(new Runnable() {

			@Override
			public void run() {
				Rect rect = new Rect();
				view.getHitRect(rect);

				rect.top -= size;
				rect.bottom += size;
				rect.left -= size;
				rect.right += size;

				parentView.setTouchDelegate(new TouchDelegate(rect, view));
			}
		});

	}

	public static String getJsonItemByKey(JSONObject data_item, String key){
		String value = "";
		try {
			value = data_item.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String getJsonItem(String data, String key){
		String value = "";
		try {
			value = new JSONObject(data).getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}
}
