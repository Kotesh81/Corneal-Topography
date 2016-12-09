package com.serenegiant.usbcameratest0;
/*
vedipen
*/

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends BaseActivity implements CameraDialog.CameraDialogParent {
	private static final boolean DEBUG = true;	// TODO set false when production
	private static final String TAG = "MainActivity";

    private final Object mSync = new Object();
    // for accessing USB and USB camera
    private USBMonitor mUSBMonitor;
	private UVCCamera mUVCCamera;
	private SurfaceView mUVCCameraView;
	// for open&start / stop&close camera preview
	private ImageButton mCameraButton;
    private Surface mPreviewSurface;
	private boolean isActive, isPreview;
    FrameLayout savedImage = null;
    private File myGeneralFolder;
    private String newString;
    SQLiteDatabase db;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
	protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras;
        extras = getIntent().getExtras();
        if(extras == null) {
            newString= null;
        }
        else {
              /* fetching the string passed with intent using extras*/
            newString= extras.get("mr_no").toString();
        }
        mCameraButton = (ImageButton) findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(mOnClickListener);
        mUVCCameraView = (SurfaceView) findViewById(R.id.camera_surface_view);
//        holder.getSurface().setOnClickListener(new OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//			@Override
//            public void onClick(View view) {
//				StorageAccess();
//                store(view);
//                Toast.makeText(MainActivity.this, "Hello There", Toast.LENGTH_SHORT).show();;
//            }
//        });
        mUVCCameraView.getHolder().addCallback(mSurfaceViewCallback);
        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
    }


    public Bitmap viewToBitmap(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }

	final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

	@RequiresApi(api = Build.VERSION_CODES.M)
	private void StorageAccess() {
		int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
			if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				showMessageOKCancel("You need to allow access to Storage",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
										REQUEST_CODE_ASK_PERMISSIONS);
							}
						});
				return;
			}
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					REQUEST_CODE_ASK_PERMISSIONS);
			return;
		}
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(MainActivity.this)
				.setMessage(message)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", null)
				.create()
				.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (DEBUG) Log.v(TAG, "onStart:");
		synchronized (mSync) {
			if (mUSBMonitor != null) {
				mUSBMonitor.register();
			}
		}
	}

	@Override
	protected void onStop() {
		if (DEBUG) Log.v(TAG, "onStop:");
		synchronized (mSync) {
			if (mUSBMonitor != null) {
				mUSBMonitor.unregister();
			}
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (DEBUG) Log.v(TAG, "onDestroy:");
		synchronized (mSync) {
			isActive = isPreview = false;
			if (mUVCCamera != null) {
				mUVCCamera.destroy();
				mUVCCamera = null;
			}
			if (mUSBMonitor != null) {
				mUSBMonitor.destroy();
				mUSBMonitor = null;
			}
		}
		mUVCCameraView = null;
		mCameraButton = null;
		super.onDestroy();
	}

	private final OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(final View view) {
			if (mUVCCamera == null) {
				// XXX calling CameraDialog.showDialog is necessary at only first time(only when app has no permission).
				CameraDialog.showDialog(MainActivity.this);
			} else {
				synchronized (mSync) {
					mUVCCamera.destroy();
					mUVCCamera = null;
					isActive = isPreview = false;
				}
			}
		}
	};

	private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
		@Override
		public void onAttach(final UsbDevice device) {
			if (DEBUG) Log.v(TAG, "onAttach:");
			Toast.makeText(MainActivity.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
			if (DEBUG) Log.v(TAG, "onConnect:");
			synchronized (mSync) {
				if (mUVCCamera != null) {
					mUVCCamera.destroy();
				}
				isActive = isPreview = false;
			}
			queueEvent(new Runnable() {
				@Override
				public void run() {
					synchronized (mSync) {
						final UVCCamera camera = new UVCCamera();
						camera.open(ctrlBlock);
						if (DEBUG) Log.i(TAG, "supportedSize:" + camera.getSupportedSize());
						try {
							camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_MJPEG);
						} catch (final IllegalArgumentException e) {
							try {
								// fallback to YUV mode
								camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
							} catch (final IllegalArgumentException e1) {
								camera.destroy();
								return;
							}
						}
						mPreviewSurface = mUVCCameraView.getHolder().getSurface();
						if (mPreviewSurface != null) {
							isActive = true;
							camera.setPreviewDisplay(mPreviewSurface);
							camera.startPreview();
							isPreview = true;
						}
						synchronized (mSync) {
							mUVCCamera = camera;
						}
					}
				}
			}, 0);
		}

		@Override
		public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
			if (DEBUG) Log.v(TAG, "onDisconnect:");
			// XXX you should check whether the comming device equal to camera device that currently using
			queueEvent(new Runnable() {
				@Override
				public void run() {
					synchronized (mSync) {
						if (mUVCCamera != null) {
							mUVCCamera.close();
							if (mPreviewSurface != null) {
								mPreviewSurface.release();
								mPreviewSurface = null;
							}
							isActive = isPreview = false;
						}
					}
				}
			}, 0);
		}

		@Override
		public void onDettach(final UsbDevice device) {
			if (DEBUG) Log.v(TAG, "onDettach:");
			Toast.makeText(MainActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(final UsbDevice device) {
		}
	};

	/**
	 * to access from CameraDialog
	 * @return
	 */
	@Override
	public USBMonitor getUSBMonitor() {
		return mUSBMonitor;
	}

	@Override
	public void onDialogResult(boolean canceled) {
		if (canceled) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// FIXME
				}
			}, 0);
		}
	}


	private final SurfaceHolder.Callback mSurfaceViewCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceCreated(final SurfaceHolder holder) {
			if (DEBUG) Log.v(TAG, "surfaceCreated:");
		}

		@Override
		public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
			if ((width == 0) || (height == 0)) return;
			if (DEBUG) Log.v(TAG, "surfaceChanged:");
			mPreviewSurface = holder.getSurface();
			synchronized (mSync) {
				if (isActive && !isPreview && (mUVCCamera != null)) {
					mUVCCamera.setPreviewDisplay(mPreviewSurface);
                    mUVCCamera.startPreview();
					isPreview = true;
				}
			}
		}

		@Override
		public void surfaceDestroyed(final SurfaceHolder holder) {
			if (DEBUG) Log.v(TAG, "surfaceDestroyed:");
			synchronized (mSync) {
				if (mUVCCamera != null) {
					mUVCCamera.stopPreview();
				}
				isPreview = false;
			}
			mPreviewSurface = null;
		}
	};
}
