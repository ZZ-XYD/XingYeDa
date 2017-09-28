package com.xingyeda.ehome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.ldl.imageloader.core.ImageLoader;
import com.ldl.imageloader.core.assist.FailReason;
import com.ldl.imageloader.core.listener.ImageLoadingListener;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.AdvertisementBean;
import com.xingyeda.ehome.bean.AnnunciateBean;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.bean.LifeBean;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.ldl.okhttp.callback.BitmapCallback;

import static android.R.attr.bitmap;

public class HomepageHttp {

	public static List<HomeBean> uploadXiaoqu(String id, final Context context, final EHomeApplication mApplication) {
		final List<HomeBean> mXiaoqu_List = new ArrayList<HomeBean>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", id);
		OkHttp.get(context, ConnectPath.RETURN_HOUSE_PATH, params,
				new BaseStringCallback(context, new CallbackHandler<String>() {

					@Override
					public void parameterError(JSONObject response) {

					}

					@Override
					public void onResponse(JSONObject response) {
						try {
							JSONArray jan = (JSONArray) response.get("obj");
							if (jan != null && jan.length() != 0) {
								SharedPreUtil.put(context, "xiaoqu", true);
								for (int i = 0; i < jan.length(); i++) {
									HomeBean bean = new HomeBean();
									JSONObject jobj = jan.getJSONObject(i);
									if (jobj.getString("isChecked").equals("1")) {
										bean.setmCommunityId(jobj
												.getString("rid"));
										bean.setmCommunity(jobj
												.getString("rname"));
										bean.setmPeriodsId(jobj
												.getString("nid"));
										bean.setmPeriods(jobj
												.getString("nname"));
										if (jobj.has("tid")) {
											bean.setmUnitId(jobj
													.getString("tid"));
											mApplication.addMap(
													jobj.getString("tid"), null);
										}
										bean.setmUnit(jobj.getString("tname"));
										bean.setmHouseNumber(jobj
												.getString("hname"));
										bean.setmHouseNumberId(jobj
												.getString("hid"));
										bean.setmIdentityType(jobj
												.getString("type"));

										if (jobj.has("eid")) {
											bean.setmEquipmentId(jobj
													.getString("eid"));
										} else {
											bean.setmEquipmentId("");
										}
										if (jobj.has("isDefault")) {
											SharedPreUtil.put(context, "isChecked", true);
											bean.setmIsDefault(jobj
													.getString("isDefault"));

											if (jobj.getString("isDefault")
													.equals("1")) {
												SharedPreUtil.put(context, "eid", jobj.has("eid") ? jobj
														.getString("eid")
														: "");
												SharedPreUtil.put(
														context,
														"dongshu",
														jobj.has("tid") ? jobj
																.getString("tid")
																: "");
												mApplication.getmCurrentUser()
														.setmXiaoqu(bean);
												mApplication.setmAnnunciateList(menuHint(bean.getmEquipmentId(), context));
											}
										}
										mXiaoqu_List.add(bean);
									}
								}
							} else {
								SharedPreUtil.put(context, "xiaoqu", false);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure() {
					}
				}));
		return mXiaoqu_List;

	}

	public static List<AnnunciateBean> menuHint(String id, Context context) {
		final List<AnnunciateBean> list = new ArrayList<AnnunciateBean>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("eid", id);
		OkHttp.get(context, ConnectPath.MENUHINT_PATH, params, new BaseStringCallback(
				context, new CallbackHandler<String>() {

			@Override
			public void parameterError(JSONObject response) {
			}

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray jsonArray = (JSONArray) response
							.get("obj");
					if (jsonArray != null && jsonArray.length() != 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jobj = jsonArray
									.getJSONObject(i);
							AnnunciateBean bean = new AnnunciateBean();
							bean.setmTitle(jobj.getString("title"));
							bean.setmContent(jobj.getString("content"));
							bean.setmTime(jobj.getString("createTime"));
							list.add(bean);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure() {
			}
		}));
		return list;

	}

	public static void refreshXiaoqu(String id, final Context context, final EHomeApplication mApplication) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", id);
		OkHttp.get(context, ConnectPath.RETURN_HOUSE_PATH, params,
				new BaseStringCallback(context, new CallbackHandler<String>() {

					@Override
					public void parameterError(JSONObject response) {

					}

					@Override
					public void onResponse(JSONObject response) {
						try {
							List<HomeBean> mXiaoqu_List = new ArrayList<HomeBean>();
							JSONArray jan = (JSONArray) response.get("obj");
							if (jan != null && jan.length() != 0) {
								SharedPreUtil.put(context, "xiaoqu", true);
								for (int i = 0; i < jan.length(); i++) {
									HomeBean bean = new HomeBean();
									JSONObject jobj = jan.getJSONObject(i);
									if (jobj.getString("isChecked").equals("1")) {
										bean.setmCommunityId(jobj
												.getString("rid"));
										bean.setmCommunity(jobj
												.getString("rname"));
										bean.setmPeriodsId(jobj
												.getString("nid"));
										bean.setmPeriods(jobj
												.getString("nname"));
										if (jobj.has("tid")) {
											bean.setmUnitId(jobj
													.getString("tid"));
											mApplication.addMap(
													jobj.getString("tid"), null);
										}
										bean.setmUnit(jobj.getString("tname"));
										bean.setmHouseNumber(jobj
												.getString("hname"));
										bean.setmHouseNumberId(jobj
												.getString("hid"));
										bean.setmIdentityType(jobj
												.getString("type"));

										if (jobj.has("eid")) {
											bean.setmEquipmentId(jobj
													.getString("eid"));
										} else {
											bean.setmEquipmentId("");
										}
										if (jobj.has("isDefault")) {
											SharedPreUtil.put(context, "isChecked", true);
											bean.setmIsDefault(jobj
													.getString("isDefault"));

											if (jobj.getString("isDefault")
													.equals("1")) {
												SharedPreUtil.put(context, "eid", jobj.has("eid") ? jobj
														.getString("eid")
														: "");
												SharedPreUtil.put(
														context,
														"dongshu",
														jobj.has("tid") ? jobj
																.getString("tid")
																: "");
												mApplication.getmCurrentUser()
														.setmXiaoqu(bean);
												mApplication.setmAnnunciateList(menuHint(bean.getmEquipmentId(), context));
											}
										}
										mXiaoqu_List.add(bean);
									}
								}
								mApplication.getmCurrentUser().setmXiaoquList(mXiaoqu_List);
//								Intent mIntent = new Intent(DoorFragment.ACTION_REFRESH);  
//								 mIntent.putExtra("yaner", "refresh");  
//								 context.sendBroadcast(mIntent);
							} else {
								SharedPreUtil.put(context, "xiaoqu", false);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure() {
					}
				}));

	}


	// 小区物业通告
	public static List<AnnunciateBean> annunciate(String id, Context context) {
		final List<AnnunciateBean> List = new ArrayList<AnnunciateBean>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", id);
		params.put("pageIndex", "1");
		params.put("pageSize", "10");
		OkHttp.get(context, ConnectPath.ANNUNCIATE_PATH, params, new BaseStringCallback(context, new CallbackHandler<String>() {

			@Override
			public void parameterError(JSONObject response) {
			}

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonObject = (JSONObject) response
							.get("obj");

					JSONArray ad_List = (JSONArray) jsonObject
							.get("list");
					if (ad_List != null && ad_List.length() != 0) {
						for (int i = 0; i < ad_List.length(); i++) {
							JSONObject jobj = ad_List
									.getJSONObject(i);
							AnnunciateBean bean = new AnnunciateBean();
							bean.setmTitle(jobj.getString("title"));
							bean.setmContent(jobj
									.getString("content"));
							bean.setmTime(jobj
									.getString("sendTime"));
							List.add(bean);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure() {
			}
		}));
		return List;
	}

	public static AdvertisementBean ad(final Context context) {
		final AdvertisementBean bean = new AdvertisementBean();
		OkHttp.get(context, ConnectPath.ADVERTISEMENT_PATH, new BaseStringCallback(context, new CallbackHandler<String>() {

			@Override
			public void parameterError(JSONObject response) {

			}

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.has("obj")) {
						JSONObject jobj = (JSONObject) response.get("obj");
						bean.setmImagePath(jobj.has("url") ? jobj.getString("url") : "");
						bean.setmAdPath(jobj.has("href") ? jobj.getString("href") : "");
						bean.setmTitle(jobj.has("title") ? jobj.getString("title") : "");
						if (jobj.has("url")) {
							if (jobj.getString("url").startsWith("http")) {
								adImage(context, bean);
							}
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure() {

			}
		}));
		return bean;
//		return bean;
	}

	public static List<LifeBean> life(String id, final Context context) {
		final List<LifeBean> mList = new ArrayList<LifeBean>();
		Map<String, String> params = new HashMap<String, String>();
		if (!"".equals(id)) {
			params.put("xId", id);
		}
		OkHttp.get(context, ConnectPath.LIFETAG_PATH, params, new BaseStringCallback(
				context, new CallbackHandler<String>() {

			@Override
			public void parameterError(JSONObject response) {
			}

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray jsonArray = (JSONArray) response.get("obj");
					if (jsonArray != null && jsonArray.length() != 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							LifeBean bean = new LifeBean();
							JSONObject jobj = jsonArray.getJSONObject(i);
							bean.setmId(jobj.has("id") ? jobj.getString("id") : "");
							bean.setmName(jobj.has("name") ? jobj.getString("name") : "");
							bean.setmPath(jobj.has("img") ? jobj.getString("img") : "");
							bean.setmType(jobj.has("type") ? jobj.getString("type") : "");
							bean.setmContent(jobj.has("content") ? jobj.getString("content") : "");
							if (bean.getmPath().startsWith("http")) {
								mList.add(life_Image(context, bean));
							}
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure() {
			}
		}));

		return mList;
	}

	public static AdvertisementBean adImage(Context context, AdvertisementBean bean) {
		final AdvertisementBean beans = bean;
		ImageLoader.getInstance().loadImage(bean.getmImagePath(),new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
										FailReason failReason) {

			}
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                bitmap = loadedImage;
				beans.setmBitmap(loadedImage);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {

			}
		});
		return beans;
	}

	public static LifeBean life_Image(Context context, LifeBean bean) {
		final LifeBean beans = bean;

		ImageLoader.getInstance().loadImage(bean.getmPath(),new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
										FailReason failReason) {

			}
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                bitmap = loadedImage;
				beans.setmImage(loadedImage);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {

			}
		});
		return beans;
	}

	public static void head(final Context context, String url, final EHomeApplication mApplication) {
		ImageLoader.getInstance().loadImage(url,new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
										FailReason failReason) {

			}
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                bitmap = loadedImage;
				mApplication.getmCurrentUser().setmHeadPhoto(loadedImage);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {

			}
		});
//		OkHttp.getImage(context, url, new BitmapCallback() {
//
//			@Override
//			public void onResponse(Bitmap bitmap, int id) {
//				mApplication.getmCurrentUser().setmHeadPhoto(bitmap);
//
//			}
//
//			@Override
//			public void onError(Call call, Exception e, int id) {
//			}
//		});
	}

	public static void menuSet(final Context context, String id) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", id);
		params.put("type", "get");
		OkHttp.get(context, ConnectPath.GETSETUP_PATH, params, new BaseStringCallback(
				context, new CallbackHandler<String>() {

			@Override
			public void parameterError(JSONObject response) {

			}

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jobj = (JSONObject) response.get("obj");
					SharedPreUtil.put(context, "vocality", jobj.has("voice") ? Boolean.valueOf(jobj.getString("voice")) : true);
					SharedPreUtil.put(context, "shake", jobj.has("shock") ? Boolean.valueOf(jobj.getString("shock")) : true);
					SharedPreUtil.put(context, "wifi", jobj.has("wifi") ? Boolean.valueOf(jobj.getString("wifi")) : true);
					SharedPreUtil.put(context, "3gAnd4g", jobj.has("mobileNetwork") ? Boolean.valueOf(jobj.getString("mobileNetwork")) : true);
					SharedPreUtil.put(context, "receivecall", jobj.has("accept") ? Boolean.valueOf(jobj.getString("accept")) : true);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure() {

			}
		}));
	}

}
