package com.handongkeji.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import com.handongkeji.common.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;


public class SDUtil {

	static private double MB = 1024;
	static private double FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
	static private double IMAGE_EXPIRE_TIME = 10;

	static public Bitmap getImage(String fileName) {
		// check image file exists
		String realFileName = Constants.CACHE_DIR_IMAGE + "/" + fileName;
		File file = new File(realFileName);
		if (!file.exists()) {
			return null;
		}
		// get original image
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(realFileName, options);
	}
	
	static public Bitmap getSample(String fileName) {
		// check image file exists
		String realFileName = Constants.CACHE_DIR_IMAGE + "/" + fileName;
		File file = new File(realFileName);
		if (!file.exists()) {
			return null;
		}
		// get original size
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(realFileName, options);
		int zoom = (int) (options.outHeight / (float) 50);
		if (zoom < 0) zoom = 1;
		// get resized image
		options.inSampleSize = zoom;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(realFileName, options);
		return bitmap;
	}
	
	static public String saveImage(Context context, Bitmap bitmap, String fileName) {
		if (bitmap == null) {
			//Logger.i(TAG, " trying to save null bitmap");
			return null;
		}
		String SDState = Environment.getExternalStorageState();
		String realFileName = null;
		if(SDState.equals(Environment.MEDIA_MOUNTED)) {
            /** 
             * �?要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的 
             * 这里使用的这种方式有�?个好处就是获取的图片是拍照后的原�? 
             * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰 
             */  
			// 判断sdcard上的空间
			if (FREE_SD_SPACE_NEEDED_TO_CACHE > getFreeSpace()) {
				//Logger.i(TAG, "Low free space onsd, do not cache");
				saveInDataDir(context, bitmap,fileName);
			}
			// 不存在则创建目录
			File dir = new File(Constants.CACHE_DIR_IMAGE);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 保存图片
			try {
				realFileName = Constants.CACHE_DIR_IMAGE + "/" + fileName;
				File file = new File(realFileName);
				file.createNewFile();
				OutputStream outStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
				//Logger.i(TAG, "Image saved to sd "+realFileName);
				return realFileName;
			} catch (FileNotFoundException e) {
				//Logger.w(TAG, "FileNotFoundException");
			} catch (IOException e) {
				//Logger.w(TAG, "IOException");
			}
        }else{  
        	//Logger.i(TAG,"内存卡不存在,使用手机内存�?");  
        	realFileName = saveInDataDir(context, bitmap,fileName);
        	return realFileName;
        }
		return realFileName;
	}

	static public void saveImage(Bitmap newImage, String cacheKey) {
		saveImage(null, newImage, cacheKey);
	}
	
	static private String saveInDataDir(Context context, Bitmap bitmap, String fileName) {
		File cacheDir = context.getCacheDir();
		String realFileName = cacheDir.getAbsolutePath()+ File.separator+fileName;
		//Logger.i(TAG, realFileName);
		File file = new File(realFileName);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
			//Logger.i(TAG, "Image saved to Data Cache");
		} catch (FileNotFoundException e) {
			//Logger.i(TAG, "FileNotFoundException");
		} catch (IOException e) {
			//Logger.i(TAG, "IOException");
		}
		//Logger.i(TAG, "Image saved to Data Cache : "+realFileName);
		return realFileName;
	}

	protected static void updateTime(String fileName) {
		File file = new File(fileName);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}

	/**
	 * 计算sdcard上的剩余空间
	 * 
	 * @return
	 */
	static public int getFreeSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		@SuppressWarnings("deprecation")
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

	static public void removeExpiredCache(String dirPath, String filename) {
		File file = new File(dirPath, filename);
		if (System.currentTimeMillis() - file.lastModified() > IMAGE_EXPIRE_TIME) {
			//Logger.i(TAG, "Clear some expiredcache files ");
			file.delete();
		}
	}

	static public void removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > getFreeSpace()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifSort());
			//Logger.i(TAG, "Clear some expiredcache files ");
			for (int i = 0; i < removeFactor; i++) {
				files[i].delete();
			}

		}

	}

	static private class FileLastModifSort implements Comparator<File> {
		@Override
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	static public String getSDPath() {
		File sdDir = null;
		File soundDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存�?
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根目�?
			String path = sdDir.getPath()+"/huijiachifan/";
			soundDir = new File(path);
			if(!soundDir.exists()) {
				 //若不存在，创建目录，可以在应用启动的时�?�创�?
				 soundDir.mkdirs();
			}
		}
		if(soundDir == null) return null;
		else return soundDir.toString();
	}
	
}