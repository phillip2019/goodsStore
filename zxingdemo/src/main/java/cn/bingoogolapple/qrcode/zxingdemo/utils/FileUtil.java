package cn.bingoogolapple.qrcode.zxingdemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import cn.bingoogolapple.qrcode.zxingdemo.DTO.GoodsDTO;
import cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant;

import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.APP_IMAGE_DIRECTORY;


public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    public static File getDownloadStorage4Image(String imagePath) throws IOException {
        File dirFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), CommonConstant.APP_DIRECTORY);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }

        File imageDir = new File(dirFile, APP_IMAGE_DIRECTORY);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        Log.d(TAG, "图片文件名为" + imagePath);
        File f = new File(imageDir, imagePath + ".png");
        if (f.exists()) {
            Log.i(TAG, "已存在文件图片，不需要创建临时文件");
            return f;
        }
        return File.createTempFile(imagePath, ".png", imageDir);
    }

    /**
     * 创建文件
     * @param documentName 文档名
     * @return
     */
    public static File getDownloadStorageDir(String documentName) {
//        String fileName = CommonConstant.APP_DIRECTORY + documentName;
        // create app directory
        // Get the directory for the user's public pictures directory.
        File dirFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), CommonConstant.APP_DIRECTORY);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }

        File file = new File(dirFile, documentName);
        try {
            if (!file.exists()) {
                boolean flag = file.createNewFile();
                if (flag) {
                    Log.e(TAG, "创建文件失败");
                }
            }
        } catch (IOException e) {
           Log.e(TAG, "创建文件失败，遇到异常", e);
        }
        return file;
    }

    /**
     * 读取csv中的数据，转换成map key: id value: goodsDTO
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Map<String, GoodsDTO> loadGoodsData(String fileName) throws IOException {
        Map<String, GoodsDTO> goodsMap = new HashMap<>(100);
        File f = getDownloadStorageDir(fileName);
        FileInputStream fileInputStream = new FileInputStream(f);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "gbk");

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(inputStreamReader);
        for (CSVRecord record : records) {
            GoodsDTO goods = csvRecord2GoodsDTO(record);
            if (goods != null) {
                goodsMap.put(goods.getId(), goods);
            }
        }

        inputStreamReader.close();
        fileInputStream.close();
        return goodsMap;
    }

    /**
     * 从文件中读取数据
     * @param csvRecord
     * @return
     */
    public static GoodsDTO csvRecord2GoodsDTO(CSVRecord csvRecord) {
        if (csvRecord == null) {
            return null;
        }
        GoodsDTO goods = new GoodsDTO();
        goods.setId(csvRecord.get("编号"));
        goods.setName(csvRecord.get("名称"));
        goods.setCategory(csvRecord.get("类别"));
        goods.setTradePrice(Double.valueOf(csvRecord.get("批发价")));
        goods.setCounts(Integer.valueOf(csvRecord.get("库存数量")));
        goods.setRetailPrice1(Double.valueOf(csvRecord.get("建议零售价1")));
        goods.setRetailPrice2(Double.valueOf(csvRecord.get("建议零售价2")));
        goods.setRetailPrice3(Double.valueOf(csvRecord.get("建议零售价3")));
        goods.setImagePath(csvRecord.get("图片"));
        goods.setRemark(csvRecord.get("备注"));
        return goods;
    }

    /**
     * 将map 中数据写入到文件中
     * @param fileName
     * @param m
     * @return
     * @throws IOException
     */
    public static boolean dumpsGoodsData(String fileName, Map<String, GoodsDTO> m) {
        // 先将文件写到新文件中，后面再进行重命名
        String newFileName = fileName + ".new";
        File newFile = getDownloadStorageDir(newFileName);
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter csvWriter = null;
        try {
            fileOutputStream = new FileOutputStream(newFile);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "gbk");
            csvWriter = new BufferedWriter(outputStreamWriter);
        } catch (IOException e) {
            Log.e(TAG, "写文件出错");
            return false;
        }

        CSVPrinter csvPrinter = null;
        try {
            csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT
                    .withHeader("编号", "名称", "类别", "批发价", "库存数量", "建议零售价1", "建议零售价2", "建议零售价3", "图片",  "备注"));
        } catch (IOException e) {
            Log.e(TAG, "创建csv printer出错");
            return false;
        }

        for (Map.Entry<String, GoodsDTO> goodsEntry : m.entrySet()) {
            GoodsDTO goods = goodsEntry.getValue();
            try {
                csvPrinter.printRecord(
                        goods.getId(),
                        goods.getName(),
                        goods.getCategory(),
                        goods.getTradePrice(),
                        goods.getCounts(),
                        goods.getRetailPrice1(),
                        goods.getRetailPrice2(),
                        goods.getRetailPrice3(),
                        goods.getImagePath(),
                        goods.getRemark());
            } catch (IOException e) {
                Log.e(TAG, String.format("写入csv printer出错, 错误数据为: %s", goods.getId()), e);
                try {
                    csvPrinter.flush();
                    csvPrinter.close();
                    csvWriter.close();
                    outputStreamWriter.close();
                    fileOutputStream.close();
                } catch (IOException e1) {
                    Log.e(TAG, "关闭文件失败", e1);
                }
                return false;
            }
        }

        try {
            csvPrinter.flush();
            csvPrinter.close();
            csvWriter.close();
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e1) {
            Log.e(TAG, "关闭文件失败", e1);
        }

        File f = getDownloadStorageDir(fileName);
        try {
            FileUtils.copyFile(newFile, f);
        } catch (IOException e) {
            Log.e(TAG, "拷贝文件出错, 将新dumps的文件成存储文件出错", e);
            return false;
        }
        return true;
    }

    public static void setImage4ViewBitmap(ImageView v, String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = v.getWidth();
        int targetH = v.getHeight();
        Log.e(TAG, "宽度为:" + targetW);
        Log.e(TAG, "高度为:" + targetH);
        if (targetH == 0) {
            targetH = 200;
            targetW = 150;
        }

        //根据图片的filepath获取到一个ExifInterface的对象
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            Log.e(TAG, "获取ExifInterface图像文件信息失败", e);
        }
        int degree = 0;
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        if (degree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
        }

        v.setImageBitmap(bitmap);
    }

}
