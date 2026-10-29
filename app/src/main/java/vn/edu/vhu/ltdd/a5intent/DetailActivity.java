package vn.edu.vhu.ltdd.a5intent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "A5_2201234567";

    private Contact contact;
    private EditText edtHoTenMoi;

    // Bộ nhận kết quả trả về từ NoteActivity (Màn hình 3)
    private final ActivityResultLauncher<Intent> noteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Contact updatedContact = IntentCompat.getParcelableExtra(
                            result.getData(), MainActivity.EXTRA_CONTACT, Contact.class);
                    if (updatedContact != null) {
                        contact = updatedContact;
                        Toast.makeText(this, "Đã cập nhật ghi chú!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        TextView tvThongTin = findViewById(R.id.tvThongTin);
        TextView tvNguoiGui = findViewById(R.id.tvNguoiGui);
        edtHoTenMoi = findViewById(R.id.edtHoTenMoi);
        Button btnLuu = findViewById(R.id.btnLuu);
        Button btnHuy = findViewById(R.id.btnHuy);
        Button btnMoGhiChu = findViewById(R.id.btnMoGhiChu);

        contact = IntentCompat.getParcelableExtra(getIntent(),
                MainActivity.EXTRA_CONTACT, Contact.class);
        String nguoiGui = getIntent().getStringExtra(MainActivity.EXTRA_NGUOI_GUI);

        if (contact == null) {
            tvThongTin.setText(R.string.no_data);
            Log.w(TAG, "Không nhận được Contact từ Intent");
            return;
        }

        tvThongTin.setText(getString(R.string.detail_format,
                contact.getHoTen(), contact.getDienThoai(), contact.getEmail()));
        tvNguoiGui.setText(getString(R.string.sent_by, nguoiGui));
        edtHoTenMoi.setText(contact.getHoTen());

        // Sự kiện bấm nút mở Màn hình Ghi chú
        btnMoGhiChu.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, NoteActivity.class);
            intent.putExtra(MainActivity.EXTRA_CONTACT, contact);
            noteLauncher.launch(intent);
        });

        btnLuu.setOnClickListener(v -> luuVaQuayLai());
        btnHuy.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void luuVaQuayLai() {
        String hoTenMoi = edtHoTenMoi.getText().toString().trim();
        if (hoTenMoi.isEmpty()) {
            edtHoTenMoi.setError(getString(R.string.err_empty));
            return;
        }
        contact.setHoTen(hoTenMoi);

        Intent ketQua = new Intent();
        ketQua.putExtra(MainActivity.EXTRA_CONTACT, contact);
        setResult(RESULT_OK, ketQua);
        Log.d(TAG, "Trả kết quả về: " + hoTenMoi);
        finish();
    }
}