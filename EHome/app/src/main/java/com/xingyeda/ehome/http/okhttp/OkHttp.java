package com.xingyeda.ehome.http.okhttp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import android.content.Context;
import android.graphics.Bitmap;

import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.dialog.DialogShow;

import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.NetUtils;
import com.ldl.okhttp.OkHttpUtils;
import com.ldl.okhttp.callback.BitmapCallback;
import com.ldl.okhttp.callback.Callback;
import com.ldl.okhttp.callback.StringCallback;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class OkHttp {

	/**
	 * 参数get请求
	 *
	 * @param url
	 * @param callback
	 */
	public static void get(Context context,String url, Callback callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			Map<String,String> params = new HashMap<>();
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}
	/**
	 * 参数get请求
	 *
	 * @param url
	 * @param
	 */
	public static void get(Context context,String url,Map params) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
				@Override
				public void onError(Call call, Exception e, int id) {

				}

				@Override
				public void onResponse(String response, int id) {

				}
			});
		}
	}

	/**
	 * 带参数get请求
	 *
	 * @param url
	 * @param params
	 */
	public static void get(Context context,String url, Map params, Callback callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}

	/**
	 * 不带参数get请求string
	 *
	 * @param url
	 * @param callback
	 */
	public static void get(Context context,String url, BaseStringCallback callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			Map<String,String> params = new HashMap<>();
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}

	/**
	 * 带参数get请求string
	 *
	 * @param url
	 * @param params
	 */
	public static void get(Context context,String url, Map params, BaseStringCallback callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}

	/**
	 * 不带参数get请求Json
	 *
	 * @param url
	 * @param callback
	 */
	public static void getJson(Context context,String url, JsonCallback callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			Map<String,String> params = new HashMap<>();
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}

	/**
	 * 带参数get请求Json
	 *
	 * @param url
	 * @param params
	 * @param callback
	 */
	public static void getJson(Context context,String url, Map params, JsonCallback callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}

	/**
	 * 不带参数get请求集合Object
	 *
	 * @param url
	 * @param callback
	 */
	public static void getObjects(Context context,String url, Callback<?> callback) {
		if (!NetUtils.isConnected(context)) {
			DialogShow.showHintDialog(context,"网络异常，请检查网络");
		} else {
			Map<String,String> params = new HashMap<>();
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}

	/**
	 * 带参数get请求集合Object
	 *
	 * @param url
	 * @param params
	 * @param callback
	 */
	public static void getObjects(Context context,String url, Map params, Callback<?> callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}

	/**
	 * 不带参数显示图片
	 *
	 * @param context
	 * @param url
	 * @param mImageView
	 */
//	public static void getImage(Context context, String url,
//			final ImageView mImageView) {
//		LogUtils.i(url);
//		if (!NetUtils.isConnected(context)) {
//			DialogShow.showHintDialog(context,"网络异常，请检查网络");
//		} else {
//		OkHttpUtils.get().url(url).tag(context).build().connTimeOut(20000)
//				.readTimeOut(20000).writeTimeOut(20000)
//				.execute(new BitmapCallback() {
//
//					@Override
//					public void onError(Call call, Exception e, int id) {
//
//					}
//
//					@Override
//					public void onResponse(Bitmap response, int id) {
//
//					}
//
//				});
//		}
//	}
	/**
	 * 不带参数显示图片
	 *
	 * @param context
	 * @param url
	 * @param callback
	 */


	/**
	 * 不带参数请求图片
	 *
	 * @param context
	 * @param url
	 * @param callback
	 */
//	public static void getImage(Context context, String url,
//			Callback<?> callback) {
//		LogUtils.i(url);
//		if (!NetUtils.isConnected(context)) {
//			DialogShow.showHintDialog(context,"网络异常，请检查网络");
//		} else {
//		OkHttpUtils.get().url(url).tag(context).build().connTimeOut(20000)
//				.readTimeOut(20000).writeTimeOut(20000).execute(callback);
//		}
//	}

	/**
	 * 带参数请求图片
	 *
	 * @param context
	 * @param url
	 * @param callback
	 */
//	public static void getImage(Context context, String url, Map params,
//			Callback<?> callback) {
//		LogUtils.i(url);
//		if (!NetUtils.isConnected(context)) {
//			DialogShow.showHintDialog(context,"网络异常，请检查网络");
//		} else {
//		OkHttpUtils.get().url(url).tag(context).params(params).build()
//				.connTimeOut(20000).readTimeOut(20000).writeTimeOut(20000)
//				.execute(callback);
//		}
//	}

	/**
	 * 不带参post提交string
	 *
	 * @param url
	 * @param obj
	 * @param callback
	 */
	public static void postString(Context context,String url, Object obj, Callback<?> callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			url = url+"?intefaceKey="+EHomeApplication.getInstance().getKey();
			OkHttpUtils.postString().url(url).content(new Gson().toJson(obj))
					.build().execute(callback);
		}
	}

	/**
	 * 带参post提交string
	 *
	 * @param url
	 * @param params
	 * @param obj
	 * @param callback
	 */
//	public static void postString(Context context,String url, Map params, Object obj,
//			Callback<?> callback) {
//		if (!NetUtils.isConnected(context)) {
//			DialogShow.showHintDialog(context,"网络异常，请检查网络");
//		} else {
//		OkHttpUtils.postString().url(url).params(params)
//				.content(new Gson().toJson(obj)).build().execute(callback);
//		}
//	}

	/**
	 * 不带参post提交文件
	 *
	 * @param context
	 * @param url
	 * @param file
	 * @param callback
	 */
	public static void postFile(Context context, String url, File file,
								Callback<?> callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			if (!file.exists()) {
				Toast.makeText(context, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
				return;
			}
			url = url+"?intefaceKey="+EHomeApplication.getInstance().getKey();
			OkHttpUtils.postFile().url(url).file(file).build().execute(callback);
		}
	}

	/**
	 * 带参post提交文件
	 *
	 * @param context
	 * @param url
	 * @param params
	 * @param file
	 * @param callback
	 */
//	public static void postFile(Context context, String url, Map params,
//			File file, Callback<?> callback) {
//		if (!NetUtils.isConnected(context)) {
//			DialogShow.showHintDialog(context,"网络异常，请检查网络");
//		} else {
//		if (!file.exists()) {
//			Toast.makeText(context, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
//			return;
//		}
//		OkHttpUtils.postFile().url(url).params(params).file(file).build()
//				.execute(callback);
//		}
//	}

	/**
	 * 不带参数的单文件上传
	 *
	 * @param context
	 * @param url
	 * @param name
	 * @param fileName
	 * @param file
	 * @param callback
	 */
	public static void uploadFile(Context context, String url, String name,
								  String fileName, File file, Callback<?> callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			if (!file.exists()) {
				Toast.makeText(context, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
				return;
			}
			Map<String,String> params = new HashMap<>();
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().addFile(name, fileName, file).url(url).params(params).build()
					.execute(callback);
		}
	}

	/**
	 * 带参数的单文件上传
	 *
	 * @param context
	 * @param url
	 * @param name
	 * @param fileName
	 * @param file
	 * @param callback
	 */
	public static void uploadFile(Context context, String url, String name,
								  String fileName, Map params, File file, Callback<?> callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			if (!file.exists()) {
				Toast.makeText(context, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
				return;
			}
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().addFile(name, fileName, file).url(url)
					.params(params).build().execute(callback);
		}
	}

	/**
	 * 不带参数下载
	 *
	 * @param context
	 * @param url
	 * @param callback
	 */
	public static void downloadFile(Context context, String url,
									Callback<?> callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			Map<String,String> params = new HashMap<>();
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}

	/**
	 * 带参数下载
	 *
	 * @param context
	 * @param url
	 * @param params
	 * @param callback
	 */
	public static void downloadFile(Context context, String url, Map params,
									Callback<?> callback) {
		if (!NetUtils.isConnected(context)) {
			BaseUtils.showShortToast(context,"网络异常，请检查网络");
		} else {
			params.put("intefaceKey", EHomeApplication.getInstance().getKey());
			OkHttpUtils.post().url(url).params(params).build().execute(callback);
		}
	}

}
