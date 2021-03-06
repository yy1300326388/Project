package com.sonny.project.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.sonny.project.R;
import com.library.base.BaseActivity;
import com.library.image.photo.ChoosePhotoActivity;
import com.library.image.photo.bean.Image;
import com.library.image.photoview.PreviewPhotoActivity;
import com.library.image.utils.ImageUtils;
import com.library.utils.file.FileUtils;
import com.library.utils.permission.PermissionUtils;
import com.library.utils.toast.ToastUtils;

import java.io.File;
import java.util.ArrayList;

public class ImageActivity extends BaseActivity {

    private ImageView mIvImg;

    private String mImgUrl = "http://www.haopic.me/wp-content/uploads/2014/10/20141015011203530.jpg";
    private String mImgUrl_1 = "http://www.haopic.me/wp-content/uploads/2016/08/20160808105305131.jpg";

    private ArrayList<Image> mSelect = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_image;
    }

    @Override
    public void initUI() {
        mIvImg = (ImageView) findViewById(R.id.iv_image_img);
    }

    @Override
    public void initLogic() {
        addOnClick(R.id.btn_image_show);
        addOnClick(R.id.btn_image_circle);
        addOnClick(R.id.btn_image_select);

        addOnClick(mIvImg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_image_show:
                //显示图片
                ImageUtils.displayImage(this, mImgUrl, mIvImg);
                //ImageUtils.displayImage(this, R.drawable.icon_image_show, mIvImg);
                break;
            case R.id.btn_image_circle:
                //圆形图片
                ImageUtils.displayCircleImage(this, R.drawable.icon_image_show, mIvImg);
                //本地图片圆角
                //ImageUtils.displayCircleImage(this, mImgUrl, mIvImg);

                //圆角 自定义半径
                //ImageUtils.displayRadiusImage(this, R.drawable.icon_image_show, mIvImg, 20.0f);
                break;
            case R.id.iv_image_img:
                //点击图片预览
                Intent intent = new Intent(this, PreviewPhotoActivity.class);
                ArrayList<Image> arrayList = new ArrayList<>();
                Image image = new Image();
                image.setImagePath(mImgUrl);
                image.setThumbnailPath(mImgUrl);
                arrayList.add(image);

                image = new Image();
                image.setImagePath(mImgUrl_1);
                image.setThumbnailPath(mImgUrl_1);
                arrayList.add(image);

                intent.putParcelableArrayListExtra(PreviewPhotoActivity.IMAGE_SELECT, arrayList);
                startActivityForResult(intent, 100);
                //startActivity(intent);
                break;
            case R.id.btn_image_select:
                //选择图片
                requestPermissions(3000, PermissionUtils.PERMISSION_GROUP_CAMERA, PermissionUtils.PERMISSION_GROUP_STORAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (100 == requestCode) {
                ArrayList<Image> arrayList = data.getParcelableArrayListExtra(PreviewPhotoActivity.IMAGE_SELECT);
                if (null != arrayList) {
                    ToastUtils.showLongMsg(this, "the select image size : " + arrayList.size());
                }
                mSelect = arrayList;
            }
            if (101 == requestCode) {
                ArrayList<Image> arrayList = data.getParcelableArrayListExtra(PreviewPhotoActivity.IMAGE_SELECT);
                if (null != arrayList) {
                    ToastUtils.showLongMsg(this, "the select image size : " + arrayList.size());
                }
                mSelect = arrayList;
            }
        }
    }

    @Override
    public void requestPermissionsSuccess(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.requestPermissionsSuccess(requestCode, permissions, grantResults);
        if(3000 == requestCode){
            //选择图片
            Intent intent = new Intent(this, ChoosePhotoActivity.class);
            //每行显示图片张数
            intent.putExtra(ChoosePhotoActivity.COLUMNS_NUM, 3);
            //最多选择的图片数
            intent.putExtra(ChoosePhotoActivity.IMAGE_MAX_NUM, 2);

            //选中的图片
            intent.putParcelableArrayListExtra(ChoosePhotoActivity.IMAGE_SELECT, mSelect);

            File file = new File(FileUtils.getRootFilePath() + File.separator + "Project");
            file.mkdirs();
            file = new File(file.getAbsolutePath(), FileUtils.createFileNameByDateTime() + ".png");
            //拍照  自定义存储地址
            intent.putExtra(ChoosePhotoActivity.CAMERA_PATH, file.getAbsolutePath());
            startActivityForResult(intent, 101);
        }
    }

    @Override
    public void requestPermissionsFail(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.requestPermissionsFail(requestCode, permissions, grantResults);
        switch (requestCode){
            case 3000:
                //
                showMessage("请在应用管理中打开拍照权限");
                break;
        }
    }
}
