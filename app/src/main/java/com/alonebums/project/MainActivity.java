package com.alonebums.project;


import android.content.Intent;
import android.view.View;

import com.alonebums.project.image.ImageActivity;
import com.alonebums.project.network.HttpActivity;
import com.alonebums.project.qrcode.QrCodeActivity;
import com.alonebums.project.recycler.RecyclerActivity;
import com.alonebums.project.sp.SpActivity;
import com.alonebums.project.utils.LUtils;
import com.library.base.BaseActivity;
import com.library.crop.Crop;
import com.library.crop.CropUtils;

public class MainActivity extends BaseActivity {

    @Override
    public void log(String message) {
        //打印日记
        LUtils.d(message);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initUI() {
        //执行顺序  initUI  initLogic  initData
    }

    @Override
    public void initLogic() {
        addOnClick(R.id.btn_main_img);
        addOnClick(R.id.btn_main_crop);
        addOnClick(R.id.btn_main_net);
        addOnClick(R.id.btn_main_sp);
        addOnClick(R.id.btn_main_recycle);
        addOnClick(R.id.btn_main_qr_code);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        Integer request = null;
        switch (v.getId()) {
            case R.id.btn_main_img:
                //图片
                intent = new Intent(this, ImageActivity.class);
                break;
            case R.id.btn_main_crop:
                //裁剪
                Crop crop = new Crop(this);
                crop.setRequestCode(1000);
                CropUtils.crop(crop);
                break;
            case R.id.btn_main_net:
                //网络
                intent = new Intent(this, HttpActivity.class);
                break;
            case R.id.btn_main_sp:
                //SharedPreferences
                intent = new Intent(this, SpActivity.class);
                break;
            case R.id.btn_main_recycle:
                //上拉下拉
                intent = new Intent(this, RecyclerActivity.class);
                break;
            case R.id.btn_main_qr_code:
                //二维码
                intent = new Intent(this, QrCodeActivity.class);
                break;
        }
        if (null != intent) {
            if (null != request) {
                startActivityForResult(intent, request);
            } else {
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    //图片裁剪
                    break;
            }
        }
    }
}
