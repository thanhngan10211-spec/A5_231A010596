package vn.edu.vhu.ltdd.a5intent;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

public class MainActivity extends AppCompatActivity {

    // TODO: thay 2201234567 bằng MSSV của bạn
    private static final String TAG = "A5_231A010596";

    /** Khóa dùng chung cho hai màn hình — luôn khai báo hằng số, không gõ chuỗi hai lần. */
    public static final String EXTRA_CONTACT = "extra_contact";
    public static final String EXTRA_NGUOI_GUI = "extra_nguoi_gui";

    private EditText edtHoTen, edtDienThoai, edtEmail;
    private TextView tvKetQuaTraVe;

    /** Bộ nhận kết quả trả về từ DetailActivity (thay cho onActivityResult đã lỗi thời). */
    private final ActivityResultLauncher<Intent> chiTietLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Contact daSua = IntentCompat.getParcelableExtra(
                            result.getData(), EXTRA_CONTACT, Contact.class);
                    if (daSua != null) {
                        edtHoTen.setText(daSua.getHoTen());
                        tvKetQuaTraVe.setText(getString(R.string.returned, daSua.getHoTen()));
                        Log.d(TAG, "Nhận kết quả trả về: " + daSua.getHoTen());
                    }
                } else {
                    tvKetQuaTraVe.setText(R.string.returned_cancel);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        edtHoTen = findViewById(R.id.edtHoTen);
        edtDienThoai = findViewById(R.id.edtDienThoai);
        edtEmail = findViewById(R.id.edtEmail);
        tvKetQuaTraVe = findViewById(R.id.tvKetQuaTraVe);

        Button btnChiTiet = findViewById(R.id.btnChiTiet);
        Button btnGoi = findViewById(R.id.btnGoi);
        Button btnWeb = findViewById(R.id.btnWeb);
        Button btnChiaSe = findViewById(R.id.btnChiaSe);

        btnChiTiet.setOnClickListener(v -> moManHinhChiTiet());
        btnGoi.setOnClickListener(v -> goiDien());
        btnWeb.setOnClickListener(v -> moTrangWeb());
        btnChiaSe.setOnClickListener(v -> chiaSe());

        Intent incomingIntent = getIntent();
        String action = incomingIntent.getAction();
        String type = incomingIntent.getType();

// Kiểm tra xem Intent có phải là hành động SEND và định dạng là text/plain không
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                // Lấy chuỗi văn bản được chia sẻ từ app khác sang
                String sharedText = incomingIntent.getStringExtra(Intent.EXTRA_TEXT);

                if (sharedText != null && !sharedText.isEmpty()) {
                    // Đưa văn bản nhận được điền thẳng vào ô Họ tên hoặc ô Điện thoại tùy ý bạn
                    edtHoTen.setText(sharedText);

                    // Thông báo nhỏ cho người dùng biết đã nhận thành công
                    Toast.makeText(this, "Đã nhận văn bản chia sẻ!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // ============ INTENT TƯỜNG MINH (explicit) ============

    private void moManHinhChiTiet() {
        String hoTen = edtHoTen.getText().toString().trim();
        if (hoTen.isEmpty()) {
            edtHoTen.setError(getString(R.string.err_empty));
            return;
        }

        // Đã cập nhật truyền đủ 4 tham số (bao gồm chuỗi "" cho trường ghi chú ở NC1)
        Contact contact = new Contact(
                hoTen,
                edtDienThoai.getText().toString().trim(),
                edtEmail.getText().toString().trim(),
                ""
        );

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_CONTACT, contact);          // đóng gói cả đối tượng
        intent.putExtra(EXTRA_NGUOI_GUI, TAG);            // dữ liệu đơn giản kèm theo
        chiTietLauncher.launch(intent);                   // mở và CHỜ kết quả trả về
    }

    // ============ INTENT NGẦM ĐỊNH (implicit) ============

    private void goiDien() {
        String sdt = edtDienThoai.getText().toString().trim();
        if (sdt.isEmpty()) {
            edtDienThoai.setError(getString(R.string.err_empty));
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + sdt));
        moAnToan(intent);
    }

    private void moTrangWeb() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.school_url)));
        moAnToan(intent);
    }

    private void chiaSe() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text,
                edtHoTen.getText().toString(), edtDienThoai.getText().toString()));
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }

    private void moAnToan(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.err_no_app, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Không có ứng dụng nào xử lý: " + intent.getAction(), e);
        }
    }
}