package ichizawa.mikitaka.techacademy.autoslideshowapp
import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.os.Handler

class MainActivity : AppCompatActivity(){

    private val PERMISSIONS_REQUEST_CODE = 100


    private var cursor: Cursor? = null

    private var mTimer: Timer? = null
    private var mHandler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }


        next_button.setOnClickListener {

            if (!cursor!!.moveToNext()) {
                cursor!!.moveToFirst()
                // 次に進めないので、一番最初にcursorを戻す
            }

            var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor!!.getLong(fieldIndex)
            var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)


        }

        return_button.setOnClickListener{


            if (!cursor!!.moveToPrevious()) {
                cursor!!.moveToLast()
                // 次に進めないので、一番最初にcursorを戻す
            }

            var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor!!.getLong(fieldIndex)
            var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)




        }

        pause_button.setOnClickListener {
            if (mTimer == null) {
                mTimer = Timer()
                pause_button.text = String.format("停止")
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        if (!cursor!!.moveToNext()) {
                            cursor!!.moveToFirst()
                            // 次に進めないので、一番最初にcursorを戻す
                        }

                        var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                        var id = cursor!!.getLong(fieldIndex)
                        var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                        mHandler.post {
                        imageView.setImageURI(imageUri)
                    }



                    }

                }, 2000, 2000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定
                next_button.setEnabled(false)
                return_button.setEnabled(false)
            }else{
                pause_button.text = String.format("再生")
                mTimer!!.cancel()
                mTimer = null
                next_button.setEnabled(true)
                return_button.setEnabled(true)
            }

        }



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        if (cursor?.moveToFirst()!!) {

            // indexからIDを取得し、そのIDから画像のURIを取得する
            var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor!!.getLong(fieldIndex)
            var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)


            imageView.setImageURI(imageUri)


        }

    }
}
