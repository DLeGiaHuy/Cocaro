package game;

//========================================================================
//TOÀN BỘ FILE NÀY THỂ HIỆN LUỒNG XỬ LÝ CỦA [UC-04: ĐÁNH QUÂN]
//========================================================================

/**
 * ==================================================================================
 * SYSTEM USE CASE: UC-04 - ĐÁNH QUÂN (PLAY MOVE)
 * ==================================================================================
 * * [MỤC ĐÍCH]
 * Điều khiển toàn bộ vòng đời của một nước đi do người chơi thực hiện, từ lúc tiếp nhận 
 * tọa độ click trên giao diện (GUI) cho đến khi cập nhật trạng thái bàn cờ và chuyển lượt.
 *
 * [LUỒNG SỰ KIỆN CHÍNH - BASIC FLOW]
 * 1. Tiếp nhận tọa độ (x, y) từ CaroGUI (Bước 1 & 2).
 * 2. Xác thực ô cờ còn trống thông qua kiemTraTinhHopLe() (Bước 3).
 * 3. Nếu HỢP LỆ:
 * - Cập nhật giá trị vào ma trận dữ liệu ngầm thông qua capNhatMaTran() (Bước 4).
 * - Ra lệnh cho CaroGUI vẽ quân cờ tương ứng lên màn hình qua veQuanCo() (Bước 5).
 * - >> INCLUDE << Kích hoạt phân đoạn kiểm tra kết quả trận đấu (Bước 6).
 * - Nếu trận đấu tiếp tục: Tự động gọi doiLuotChoi() (Bước 7) và hiển thị lượt mới (Bước 8).
 *
 * [LUỒNG THAY THẾ - ALTERNATIVE FLOW]
 * - Nếu ô cờ ĐÃ CÓ QUÂN (Không hợp lệ): Hệ thống chạy vào nhánh rẽ 'else', gọi hàm 
 * tuChoiThaoTac() để phản hồi lỗi về giao diện và giữ nguyên hiện trạng bàn cờ.
 *
 * ==================================================================================
 */
//=== 1. LỚP GIAO DIỆN (Boundary Class) ===
class CaroGUI {
 private GameLogic gameLogic;

 public CaroGUI() {}

 public void setGameLogic(GameLogic gameLogic) {
     this.gameLogic = gameLogic;
 }

 /**
  * Bước 1 (UC-04): Người chơi click chọn ô cờ trên màn hình
  */
 public void clickChonOCo(int x, int y) {
     System.out.println("1. [Người chơi] -> [CaroGUI]: Click chọn ô cờ (" + x + ", " + y + ")");
     
     // Bước 2 (UC-04): Gửi yêu cầu đánh quân sang xử lý logic
     gameLogic.danhQuan(x, y);
 }

 /**
  * Bước 5 (UC-04): Vẽ quân cờ lên bàn cờ giao diện
  */
 public void veQuanCo(int x, int y, String loaiQuan) {
     System.out.println("5. [GameLogic] -> [CaroGUI]: veQuanCo(" + x + ", " + y + ", " + loaiQuan + ")");
 }

 /**
  * Bước 8 (UC-04): Hiển thị thông báo lượt chơi mới
  */
 public void hienThiLuotMoi() {
     System.out.println("8. [GameLogic] -> [CaroGUI]: hienThiLuotMoi()");
 }

 /**
  * Luồng thay thế của UC-04: Từ chối thao tác khi ô đã có quân
  */
 public void tuChoiThaoTac() {
     System.out.println("<-- [CaroGUI]: Từ chối thao tác (Không thay đổi trên bàn cờ)");
 }
}


//=== 2. LỚP XỬ LÝ LOGIC (Controller/Logic Class) ===
class GameLogic {
 private CaroGUI caroGUI;
 private String loaiQuan = "X"; 
 private String[][] maTranBanCo = new String[15][15]; 
 private boolean tranDauTiepTuc = true; 

 public GameLogic(CaroGUI caroGUI) {
     this.caroGUI = caroGUI;
 }

 /**
  * Bước 2 (UC-04): Hàm xử lý chính của Use Case Đánh Quân
  */
 public void danhQuan(int x, int y) {
     System.out.println("2. [CaroGUI] -> [GameLogic]: danhQuan(" + x + ", " + y + ")");

     // Bước 3 (UC-04): Kiểm tra tính hợp lệ (Kiểm tra ô trống)
     boolean hopLe = kiemTraTinhHopLe(x, y);

     // Khối alt lớn của UC-04
     if (hopLe) {
         // -------------------------------------------------------------
         // [Khối alt: Ô hợp lệ (Basic Flow của UC-04)]
         // -------------------------------------------------------------
         System.out.println("\n--- BẮT ĐẦU: Khối [Alt: Ô hợp lệ (Basic Flow)] ---");

         // Bước 4 (UC-04): Cập nhật ma trận dữ liệu bàn cờ
         capNhatMaTran(x, y, loaiQuan);

         // Bước 5 (UC-04): Gọi giao diện vẽ quân cờ
         caroGUI.veQuanCo(x, y, loaiQuan);

         // =============================================================
         // ĐƯỜNG KẺ NGANG: [UC-04] BAO HÀM (INCLUDE) [UC-05: KIỂM TRA THẮNG HÒA]
         // =============================================================
         System.out.println("\n>>>>> UC-04 INCLUDE -> [UC-05: KIỂM TRA THẮNG HÒA] <<<<<");
         
         // Bước 6 (UC-05): Thực hiện kiểm tra kết quả thắng/hòa
         kiemTraKetQua(x, y);
         System.out.println(">>>>> KẾT THÚC XỬ LÝ [UC-05] <<<<<\n");

         // Khối alt nhỏ (Quay lại luồng xử lý tiếp theo của UC-04 dựa trên kết quả UC-05)
         if (tranDauTiepTuc) {
             // [Khối alt: Trận đấu tiếp tục]
             System.out.println("--- BẮT ĐẦU: Khối [Alt: Trận đấu tiếp tục] ---");

             // Bước 7 (UC-04): Đổi lượt chơi
             doiLuotChoi();

             // Bước 8 (UC-04): Hiển thị lượt mới lên GUI
             caroGUI.hienThiLuotMoi();
         } else {
             System.out.println("--- Khối [Alt: Trận đấu kết thúc] ---");
         }

     } else {
         // -------------------------------------------------------------
         // [Khối alt: Ô đã có quân (Alternative Flow của UC-04)]
         // -------------------------------------------------------------
         System.out.println("\n--- BẮT ĐẦU: Khối [Alt: Ô đã có quân (Alternative Flow)] ---");
         caroGUI.tuChoiThaoTac();
     }
 }

 /**
  * Bước 3 (UC-04): Kiểm tra ô trống
  */
 private boolean kiemTraTinhHopLe(int x, int y) {
     System.out.println("3. [GameLogic] -> [GameLogic]: kiemTraTinhHopLe(" + x + ", " + y + ") [Ghi chú: Kiểm tra ô trống]");
     return maTranBanCo[x][y] == null;
 }

 /**
  * Bước 4 (UC-04): Cập nhật ma trận
  */
 private void capNhatMaTran(int x, int y, String loaiQuan) {
     System.out.println("4. [GameLogic] -> [GameLogic]: capNhatMaTran(" + x + ", " + y + ", " + loaiQuan + ")");
     maTranBanCo[x][y] = loaiQuan;
 }

 /**
  * Bước 6 (UC-05): Hàm này thuộc về gói xử lý của UC-05
  */
 private void kiemTraKetQua(int x, int y) {
     System.out.println("6. [GameLogic] -> [GameLogic]: kiemTraKetQua(" + x + ", " + y + ") -> [Xử lý logic của UC-05]");
     this.tranDauTiepTuc = true; 
 }

 /**
  * Bước 7 (UC-04): Đổi lượt người chơi
  */
 private void doiLuotChoi() {
     System.out.println("7. [GameLogic] -> [GameLogic]: doiLuotChoi()");
     this.loaiQuan = this.loaiQuan.equals("X") ? "O" : "X";
 }
}