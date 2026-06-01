# 📱 App Development Plan: Truth or Dare (Multi-language)
**Framework:** Android Native (Jetpack Compose)
**Architecture:** MVVM (Model-View-ViewModel)

---

## 1. UI/UX Guidelines (Dark Party Theme)
Ứng dụng theo phong cách Neubrutalism kết hợp Dark Mode để tạo cảm giác "premium" cho các buổi tiệc.

*   **Color Palette:**
    *   `Background`: `#121214` (Đen sâu)
    *   `Card Truth`: `#FF3366` (Hồng Neon) - Kích thích sự tò mò.
    *   `Card Dare`: `#33CCFF` (Xanh Cyan) - Kích thích hành động.
    *   `Accent/Buttons`: `#FFCC00` (Vàng Chanh) - Tạo điểm nhấn.
*   **Ad Placement Rules:**
    *   **Banner Ad:** 1 Banner nhỏ cố định sát mép dưới cùng màn hình chơi (Bottom).
    *   **Interstitial Ad:** KHÔNG dùng popup tràn màn hình liên tục. Chỉ hiển thị khi user chuyển đổi qua lại giữa chế độ `Normal` và `18+`.

---

## 2. Onboarding Flow (First-time Users)
Sử dụng `ModalBottomSheet` hoặc `HorizontalPager` cho màn hình chào mừng gồm 3 bước:

1.  **Language Selection:** Hiển thị danh sách cờ quốc gia (VN, EN, FR, ES...). Lưu lựa chọn vào `DataStore` hoặc `SharedPreferences` để map ngôn ngữ cho UI và nội dung câu hỏi.
2.  **Mode Selection:** Hiển thị 2 thẻ lớn:
    *   `Normal 🌟`: Hài hước, trong sáng (Gia đình/Bạn mới).
    *   `Adult 18+ 🔥`: Táo bạo, deep (Tiệc tùng/Cặp đôi).
3.  **Quick Tutorial:** Highlight 2 nút Truth / Dare bằng SpotLight effect. Thêm dòng Text: *"Chạm để chọn số phận của bạn. Lắc máy nếu muốn đổi câu hỏi khác!"*

---

## 3. Localization Architecture (i18n)
*   **Static UI:** Sử dụng hệ thống `strings.xml` tiêu chuẩn của Android cho button, title, onboarding text.
*   **Dynamic Data (Questions):** Sử dụng Local JSON File đính kèm trong thư mục `assets`. Dữ liệu được crawl/generate sẵn hỗ trợ đa ngôn ngữ để đảm bảo app chạy mượt Offline (Zero-latency).

### JSON Data Schema (`assets/questions.json`)
```json
[
  {
    "id": "t_001",
    "type": "truth",
    "rating": "normal",
    "translations": {
      "en": "What is your most embarrassing childhood memory?",
      "vi": "Kỷ niệm thời thơ ấu quê xệ nhất của bạn là gì?",
      "fr": "Quel est ton souvenir d'enfance le plus embarrassant ?",
      "es": "¿Cuál es tu recuerdo infantil más vergonzoso?"
    }
  },
  {
    "id": "d_001",
    "type": "dare",
    "rating": "adult_18",
    "translations": {
      "en": "Let the person to your right text anyone they want from your phone.",
      "vi": "Để người bên phải bạn nhắn tin cho bất kỳ ai họ muốn bằng điện thoại của bạn.",
      "fr": "Laisse la personne à ta droite envoyer un SMS à qui elle veut depuis ton téléphone.",
      "es": "Deja que la persona a tu derecha le envíe un mensaje de texto a quien quiera desde tu teléfono."
    }
  }
]