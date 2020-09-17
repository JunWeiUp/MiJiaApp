/**
 *  ClassName: KuaiPaiActivity.java
 *  created on 2012-10-23
 *  Copyrights 2012-10-23 hjgang All rights reserved.
 *  site: http://www.hjgang.tk
 *  email: hjgang@yahoo.cn
 */
package com.handongkeji.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author hjgang
 * @category 图片处理工具�?
 * @日期 2012-10-23
 * @时间 下午2:30:50
 * @年份 2012
 */
public class ImageHelper {

	public static BitmapDrawable getFileBitmap(String filename) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		Bitmap bm = BitmapFactory.decodeFile(filename, opts);
		@SuppressWarnings("deprecation")
        BitmapDrawable bd = new BitmapDrawable(bm);
		return bd;
	}

	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap getHttpBitmap(String url) {
		URL myurl = null;
		InputStream in = null;
		Bitmap bitmap = null;
		try {
			myurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
			conn.setConnectTimeout(0);
			conn.setDoInput(true);
			conn.connect();
			in = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 根据资源ID获取对应的Bitmap实例
	 * 
	 * @param ctx
	 * @param id
	 * @return
	 */
	public static Bitmap getBitmap(Context ctx, int id) {
		return BitmapFactory.decodeResource(ctx.getResources(), id);
	}

	/**
	 * 把png或jpg(jpeg)格式图片按指定名称写入指定目录下
	 * 
	 * @param bmp
	 * @param path
	 * @param fileName
	 */
	public static void write(Bitmap bmp, File file) {
		FileOutputStream fos = null;

		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 75, fos);

			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 把png或jpg(jpeg)格式图片按指定名称写入指定目录下
	 * 
	 * @param bmp
	 * @param path
	 * @param fileName
	 */
	public static void write(Bitmap bmp, String fileName) {
		String extension = IOHelper.getExtension(fileName);
		FileOutputStream fos = null;

		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);

			if ("png".equalsIgnoreCase(extension)) {
				bmp.compress(Bitmap.CompressFormat.PNG, 75, fos);
			} else if ("jpg".equalsIgnoreCase(extension)
					|| "jpeg".equalsIgnoreCase(extension)) {
				bmp.compress(Bitmap.CompressFormat.JPEG, 75, fos);
			}

			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 把png或jpg(jpeg)格式图片按指定名称写入指定目录下
	 * 
	 * @param bmp
	 * @param path
	 * @param fileName
	 */
	public static void write(Bitmap bmp, String path, String fileName) {
		String extension = IOHelper.getExtension(fileName);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(new File(path, fileName));

			if ("png".equalsIgnoreCase(extension)) {
				bmp.compress(Bitmap.CompressFormat.PNG, 75, fos);
			} else if ("jpg".equalsIgnoreCase(extension)
					|| "jpeg".equalsIgnoreCase(extension)) {
				bmp.compress(Bitmap.CompressFormat.JPEG, 75, fos);
			}

			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Bitmap loadFromFile(File name) throws IOException {
		Bitmap bmp = null;
		InputStream is = null;
		try {
			is = new FileInputStream(name);
			bmp = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return bmp;
	}

	public static Bitmap loadFromFile(String name) throws IOException {
		Bitmap bmp = null;
		InputStream is = null;
		try {
			is = new FileInputStream(name);
			bmp = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return bmp;
	}

	/**
	 * 图片的缩放，以图片的原宽度和目标宽度作为缩放�?
	 * 
	 * @param src
	 *            源图�?
	 * @param destWidth
	 *            目标宽度
	 * @return
	 */
	public static Bitmap zoom(Bitmap src, int destWidth) {
		int width = src.getWidth();
		int height = src.getHeight();
		if (width == destWidth) {
			width = width - 1;
			height = height - 1;
		}
		// 计算缩放比例
		float scaleWidth = ((float) destWidth) / width;
		// float scaleHeight = ((float) destHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();

		matrix.postScale(scaleWidth, scaleWidth);

		// 得到新的图片
		Bitmap b = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
		src.recycle();
		return b;
	}

	public static Bitmap zoom(Bitmap src, int destWidth, int destHeight) {
		int width = src.getWidth();
		int height = src.getHeight();
		if (width == destWidth || height == destHeight) {
			width = width - 1;
			height = height - 1;
		}
		// 计算缩放比例
		float scaleWidth = ((float) destWidth) / width;
		// float scaleHeight = ((float) destHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();

		matrix.postScale(scaleWidth, scaleWidth);

		// 得到新的图片
		Bitmap b = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
		src.recycle();
		return b;
	}

	/**
	 * 根据图片名称，从assets中加载对应的Bitmap对象
	 * 
	 * @param ctx
	 * @param fileName
	 * @return
	 */
	public static Bitmap getImageFromAssetsFile(Context ctx, String fileName) {
		Bitmap image = null;
		AssetManager am = ctx.getResources().getAssets();
		InputStream is = null;
		try {
			is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return image;
	}

	/**
	 * 图像对象绑定�?
	 * 
	 * @author qjyong
	 */
	public static class MyViewBinder implements ViewBinder {
		public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
			if ((view instanceof ImageView) && (data instanceof Bitmap)) {
				ImageView iv = (ImageView) view;
				Bitmap bm = (Bitmap) data;
				iv.setImageBitmap(bm);

				return true;
			}
			return false;
		}
	}
	
	public static Bitmap comp(Bitmap image) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出	
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return bitmap;//compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}
	@SuppressWarnings("unused")
	private static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024>100) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

}
