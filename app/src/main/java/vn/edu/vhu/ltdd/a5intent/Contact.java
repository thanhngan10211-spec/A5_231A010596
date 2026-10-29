package vn.edu.vhu.ltdd.a5intent;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {

    private String hoTen;
    private String dienThoai;
    private String email;
    private String ghiChu; // Thêm thuộc tính ghi chú cho NC1

    public Contact(String hoTen, String dienThoai, String email, String ghiChu) {
        this.hoTen = hoTen;
        this.dienThoai = dienThoai;
        this.email = email;
        this.ghiChu = ghiChu;
    }

    protected Contact(Parcel in) {
        hoTen = in.readString();
        dienThoai = in.readString();
        email = in.readString();
        ghiChu = in.readString(); // Đọc ghi chú đúng thứ tự
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hoTen);
        dest.writeString(dienThoai);
        dest.writeString(email);
        dest.writeString(ghiChu); // Ghi ghi chú
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}