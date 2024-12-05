/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sdk.mysdklibrary.volley.toolbox;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.AESSecurity;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.volley.NetworkResponse;
import com.sdk.mysdklibrary.volley.ParseError;
import com.sdk.mysdklibrary.volley.Response;
import com.sdk.mysdklibrary.volley.Response.ErrorListener;
import com.sdk.mysdklibrary.volley.Response.Listener;


/**
 * A request for retrieving a {@link JSONObject} response body at a given URL, allowing for an
 * optional {@link JSONObject} to be passed in as part of the request body.
 */
public class JsonObjectRequest extends JsonRequest<JSONObject> {

	protected  boolean isconstant = false;
    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JsonObjectRequest(int method, String url, JSONObject jsonRequest,
            Listener<JSONObject> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                    errorListener);
    }
    
    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest a json string to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JsonObjectRequest(boolean isconstant, int method, String url, String jsonRequest,
            Listener<JSONObject> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest, listener,
                    errorListener);
        this.isconstant = isconstant;
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     * @see #JsonObjectRequest(int, String, JSONObject, Listener, ErrorListener)
     */
    public JsonObjectRequest(String url, JSONObject jsonRequest, Listener<JSONObject> listener,
            ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(final NetworkResponse response) {
        try {
            String jsonString =
                new String(response.data, HttpHeaderParser.parseCharset(response.headers));//HttpHeaderParser.parseCharset(response.headers)
            MLog.a("JsonObjectRequest", "jsonString---->"+jsonString);
            
            //测试登录问题
//            if(OutFace.getInstance(null).getmActivity()!=null){
//            	
//            	OutFace.getInstance(null).getmActivity().runOnUiThread(new Runnable() {
//            		
//            		@Override
//            		public void run() {
//            			
//            			AlertDialog.Builder builder =new Builder(OutFace.getInstance(null).getmActivity());
//            			builder.setTitle("提示");  
//            			String s = "";
//            			try {
//            				s = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//            			} catch (UnsupportedEncodingException e) {
//            				e.printStackTrace();
//            			}
//            			builder.setMessage(s);
//            			builder.setPositiveButton("确定",
//            					new android.content.DialogInterface.OnClickListener() {
//            				public void onClick(DialogInterface dialog, int which) {
//            					dialog.dismiss();
//            				}
//            			});
//            			builder.create().show();
//            		}
//            	});
//            }
            
            
            
            if (jsonString == null || jsonString.contains("error") || jsonString.contains("exception") || jsonString.contains("Fatal")) {
            	jsonString = "exception Net";
    		}else{
    			if (isconstant) {
    				jsonString = AESSecurity.constantdecryptResult(jsonString, HttpUtils.KEY);
    			}else{
    				jsonString = AESSecurity.decryptResult(jsonString);
    			}
    		}
            
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (final UnsupportedEncodingException e) {
//        	if(OutFace.getInstance(null).getmActivity()!=null){
//            	
//            	OutFace.getInstance(null).getmActivity().runOnUiThread(new Runnable() {
//            		
//            		@Override
//            		public void run() {
//            			
//            			AlertDialog.Builder builder =new Builder(OutFace.getInstance(null).getmActivity());
//            			builder.setTitle("提示");  
//            			String s = "";
//            			s = e.getMessage();
//            			builder.setMessage(s);
//            			builder.setPositiveButton("确定",
//            					new android.content.DialogInterface.OnClickListener() {
//            				public void onClick(DialogInterface dialog, int which) {
//            					dialog.dismiss();
//            				}
//            			});
//            			builder.create().show();
//            		}
//            	});
//            }
            return Response.error(new ParseError(e));
        } catch (final JSONException je) {
//        	if(OutFace.getInstance(null).getmActivity()!=null){
//            	
//            	OutFace.getInstance(null).getmActivity().runOnUiThread(new Runnable() {
//            		
//            		@Override
//            		public void run() {
//            			
//            			AlertDialog.Builder builder =new Builder(OutFace.getInstance(null).getmActivity());
//            			builder.setTitle("提示");  
//            			String s = "";
//            			s = je.getMessage();
//            			builder.setMessage(s);
//            			builder.setPositiveButton("确定",
//            					new android.content.DialogInterface.OnClickListener() {
//            				public void onClick(DialogInterface dialog, int which) {
//            					dialog.dismiss();
//            				}
//            			});
//            			builder.create().show();
//            		}
//            	});
//            }
            return Response.error(new ParseError(je));
        }
    }
}
