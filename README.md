# 🏠 Nền tảng Web Bất Động Sản  
*Đồ án cuối kỳ môn Công nghệ Java – Đại học Tôn Đức Thắng*

---

## 📄 Tổng quan
Đây là **đồ án cuối kỳ** của môn Công nghệ Java tại Trường Đại học Tôn Đức Thắng.  
Mục tiêu dự án là xây dựng một **nền tảng web bất động sản** hỗ trợ người dùng đăng tin, tìm kiếm, thuê hoặc mua bất động sản, đồng thời tích hợp thanh toán trực tuyến VNPay.

- **Frontend:** Thymeleaf + HTML/CSS/Bootstrap/JavaScript  
- **Backend:** Spring Boot, Spring Security, JWT  
- **Cơ sở dữ liệu:** MySQL (với JPA/Hibernate)  
- **Kiểm thử:** JUnit 5 và Mockito  
- **Kiến trúc:** MVC  

---

## ✨ Tính năng chính

### Người dùng & Xác thực
- Đăng ký, đăng nhập, đăng xuất
- Quên mật khẩu và đặt lại mật khẩu qua email
- Đổi mật khẩu
- Nâng cấp tài khoản thành môi giới sau khi thanh toán thành công

### Đăng tin bất động sản
- Đăng, chỉnh sửa, xóa tin bất động sản (mua hoặc cho thuê)
- Tải ảnh, video chi tiết của bất động sản
- Trang quản lý danh sách tin đăng của môi giới

### Tìm kiếm & Yêu thích
- Tìm kiếm và lọc bất động sản theo địa chỉ, loại phòng, giá…
- Lưu bất động sản vào danh sách “Yêu thích”

### Giao dịch & Hợp đồng
- Đặt cọc cho bất động sản
- Tự động tạo và lưu trữ hợp đồng sau khi đặt cọc thành công
- Xem và tải hợp đồng

### Thanh toán
- Tích hợp **VNPay** cho các thanh toán an toàn
- Hỗ trợ thanh toán khi nâng cấp tài khoản hoặc đặt cọc

### Khác
- Đặt lịch hẹn xem bất động sản với môi giới
- Môi giới xác nhận hoặc từ chối lịch hẹn

---

## 🗂️ Thiết kế hệ thống

### Sơ đồ Use Case  
![Use Case Diagram](https://github.com/nguyencongquang-github/real-estate-web/issues/2#issue-3428668892)  
*(Ảnh sơ đồ Use Case của dự án)*

### Thiết kế cơ sở dữ liệu  
Hệ thống sử dụng mô hình quan hệ với các bảng chính:

- **Role**: lưu vai trò người dùng (Admin, User, Môi giới)
- **User**: thông tin người dùng
- **Listing**: thông tin bài đăng bất động sản
- **PropertyImage**: hình ảnh của từng bất động sản
- **Transaction**: thông tin giao dịch mua/thuê
- **Payment**: thông tin thanh toán gắn với giao dịch

**Quan hệ chính:**
- 1 Role → nhiều User
- 1 User → nhiều Listing
- 1 Listing → nhiều PropertyImage
- 1 Listing → nhiều Transaction
- 1 Transaction → nhiều Payment

**Sơ đồ CSDL:**  
![Database Diagram](https://github.com/nguyencongquang-github/real-estate-web/issues/1#issue-3428637650)

---

## 🧪 Kiểm thử
- Sử dụng **JUnit 5** để viết unit test cho service và controller  
- Sử dụng **Mockito** để mock các dependency, kiểm thử độc lập các thành phần  
- Chạy lệnh:
```bash
mvn test
````

---

## 🖥️ Công nghệ sử dụng

* **Spring Boot:** Xây dựng backend, REST API, tích hợp JPA/Hibernate
* **Spring Security + JWT:** Xác thực & phân quyền
* **Thymeleaf:** Frontend động, tích hợp với Spring Boot
* **MySQL:** Lưu trữ dữ liệu
* **Postman:** Kiểm thử API

---

## 🛠️ Cài đặt & chạy

### Yêu cầu

* Java 17+
* Maven 3+
* MySQL chạy cục bộ

### Các bước

1. Clone repo:

   ```bash
   git clone https://github.com/<username>/real-estate-web.git
   cd real-estate-web
   ```
2. Cấu hình kết nối cơ sở dữ liệu trong `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/realestate_db
   spring.datasource.username=root
   spring.datasource.password=root
   ```
3. Build & chạy:

   ```bash
   mvn spring-boot:run
   ```
4. Truy cập ứng dụng tại:

   ```
   http://localhost:8080
   ```

---

## 📺 Video demo

[Nhấn để xem video demo](https://drive.google.com/file/d/1u1LmBmobh4XlVKLZJ4WEDsfe3GaL0UJg/view?usp=sharing)

---

## 👨‍💻 Nhóm thực hiện

* **Nguyễn Công Quang** – 52200177 - Leader
* **Võ Văn Thuận** – 52200133
* **Phạm Gia Huy** – 52200101
