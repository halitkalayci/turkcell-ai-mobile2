---
description: Bu dosya frontend projelerindeki UI stillendirme kurallarını içerir.
applyTo: '**/*.dart,**/*.html' 
---

# 1) GENEL UI PRENSİPLERİ

Bütün UI yapıları aşağıdaki kuralları uygulamak zorundadır:

- Temiz ve minimal tasarım
- Tutarlı boşluklandırma
- Tutarlı tipografi
- Tekrar kullanılabilir component mantığı
- `Inline Styling`'den **kesinlikle** kaçınılmalıdır.

## 1.1) Token Kullanım Zorunluluğu

Aşağıdaki değerler **doğrudan kod içinde yazılamaz** (magic value yasağı):

- Ham renk kodu: `Color(0xFF...)`, `Colors.blue`, `Colors.red`
- Ham sayısal spacing: `SizedBox(height: 16)`, `padding: EdgeInsets.all(8)`
- Ham font boyutu: `fontSize: 14`, `fontWeight: FontWeight.w600`

Bu değerlerin **tamamı** aşağıdaki token sınıflarından alınmak zorundadır:
`AppColors`, `AppSpacing`, `AppTextStyles`, `AppRadius`.

---

# 2) Renklendirme Sistemi

## 2.1) Token Tanımı

Tüm renkler `lib/core/theme/app_colors.dart` dosyasındaki `AppColors` sınıfında `static const` olarak tanımlanır.

```dart
abstract final class AppColors {
  // Primary
  static const Color primary = Color(0xFF6200EE);
  static const Color primaryVariant = Color(0xFF3700B3);
  static const Color onPrimary = Color(0xFFFFFFFF);

  // Secondary
  static const Color secondary = Color(0xFF03DAC6);
  static const Color secondaryVariant = Color(0xFF018786);
  static const Color onSecondary = Color(0xFF000000);

  // Background & Surface
  static const Color background = Color(0xFFF5F5F5);
  static const Color surface = Color(0xFFFFFFFF);
  static const Color onBackground = Color(0xFF1C1C1E);
  static const Color onSurface = Color(0xFF1C1C1E);

  // Semantic
  static const Color error = Color(0xFFB00020);
  static const Color onError = Color(0xFFFFFFFF);
  static const Color success = Color(0xFF4CAF50);
  static const Color warning = Color(0xFFFFC107);

  // Text
  static const Color textPrimary = Color(0xFF1C1C1E);
  static const Color textSecondary = Color(0xFF6B6B6B);
  static const Color textDisabled = Color(0xFFBDBDBD);
  static const Color textInverse = Color(0xFFFFFFFF);

  // Border & Divider
  static const Color border = Color(0xFFE0E0E0);
  static const Color divider = Color(0xFFF0F0F0);
}
```

## 2.2) Renk Kullanım Kuralları

- `AppColors.primary` gibi semantik isimler kullanılır; `AppColors.purple` gibi ham renk isimleri yasaktır.
- Opaklık için `AppColors.primary.withOpacity(0.1)` kabul edilir; `Color(0x1A6200EE)` şeklinde ham yazım yasaktır.
- Tema değişkenine gerek varsa `Theme.of(context).colorScheme` kullanılır ancak token'lar bu schema'yı besler.

---

# 3) Boşluklandırma Kuralları

## 3.1) Token Tanımı

Tüm spacing değerleri `lib/core/theme/app_spacing.dart` dosyasındaki `AppSpacing` sınıfında tanımlanır. Taban birim **4 dp**'dir.

```dart
abstract final class AppSpacing {
  static const double xs  = 4.0;   // 1x — ikon içi, chip padding
  static const double sm  = 8.0;   // 2x — kompakt boşluk
  static const double md  = 12.0;  // 3x — orta boşluk
  static const double lg  = 16.0;  // 4x — standart padding (varsayılan)
  static const double xl  = 24.0;  // 6x — bölüm arası
  static const double xxl = 32.0;  // 8x — ekran kenar padding büyük
  static const double xxxl = 48.0; // 12x — hero alanları
}
```

## 3.2) Spacing Kullanım Kuralları

- Widget'lar arası dikey boşluk için `SizedBox(height: AppSpacing.lg)` kullanılır.
- `Padding` ve `EdgeInsets` değerleri daima `AppSpacing` tokenlarından oluşturulur.
- Ekran kenar (horizontal) padding'i varsayılan olarak `AppSpacing.lg` (16 dp) olmalıdır.
- `Spacer()` yalnızca esnek alan doldurmak için; sabit boşluklar için `SizedBox` + token kullanılır.

---

# 4) Tipografi Kuralları

## 4.1) Token Tanımı

Tüm metin stilleri `lib/core/theme/app_text_styles.dart` dosyasındaki `AppTextStyles` sınıfında tanımlanır.

```dart
abstract final class AppTextStyles {
  static const String _fontFamily = 'Inter';

  // Display
  static const TextStyle displayLarge = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 32,
    fontWeight: FontWeight.w700,
    height: 1.25,
    letterSpacing: -0.5,
  );

  // Heading
  static const TextStyle headingLarge = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 24,
    fontWeight: FontWeight.w700,
    height: 1.3,
  );

  static const TextStyle headingMedium = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 20,
    fontWeight: FontWeight.w600,
    height: 1.35,
  );

  static const TextStyle headingSmall = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 16,
    fontWeight: FontWeight.w600,
    height: 1.4,
  );

  // Body
  static const TextStyle bodyLarge = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 16,
    fontWeight: FontWeight.w400,
    height: 1.5,
  );

  static const TextStyle bodyMedium = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 14,
    fontWeight: FontWeight.w400,
    height: 1.5,
  );

  static const TextStyle bodySmall = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 12,
    fontWeight: FontWeight.w400,
    height: 1.5,
  );

  // Label & Caption
  static const TextStyle labelLarge = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 14,
    fontWeight: FontWeight.w500,
    height: 1.4,
    letterSpacing: 0.1,
  );

  static const TextStyle caption = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 11,
    fontWeight: FontWeight.w400,
    height: 1.4,
    letterSpacing: 0.4,
  );

  static const TextStyle overline = TextStyle(
    fontFamily: _fontFamily,
    fontSize: 10,
    fontWeight: FontWeight.w500,
    height: 1.6,
    letterSpacing: 1.5,
  );
}
```

## 4.2) Tipografi Kullanım Kuralları

- `Text` widget'ında `style` parametresi her zaman `AppTextStyles.*` token'ından alınır.
- `copyWith()` yalnızca renk veya tek bir özellik override etmek için kullanılır, tüm stil sıfırlamak yasaktır.
- Font boyutu ve font ağırlığı doğrudan `fontSize: 14` veya `fontWeight: FontWeight.bold` şeklinde yazılamaz.

---

# 5) Kenar Yuvarlaklığı (Border Radius)

## 5.1) Token Tanımı

```dart
abstract final class AppRadius {
  static const double xs  = 4.0;
  static const double sm  = 8.0;
  static const double md  = 12.0;
  static const double lg  = 16.0;
  static const double xl  = 24.0;
  static const double full = 999.0; // pill / circular

  static const BorderRadius cardRadius =
      BorderRadius.all(Radius.circular(md));
  static const BorderRadius buttonRadius =
      BorderRadius.all(Radius.circular(sm));
  static const BorderRadius inputRadius =
      BorderRadius.all(Radius.circular(sm));
  static const BorderRadius chipRadius =
      BorderRadius.all(Radius.circular(full));
}
```

## 5.2) Kullanım Kuralları

- `BorderRadius.circular(12)` gibi ham değer **yasaktır**; `AppRadius.cardRadius` gibi semantik token kullanılır.

---

# 6) Token Olmayan Durumlarda Yeni Token Oluşturma

Eğer mevcut bir token ihtiyacı karşılamıyorsa:

1. İlgili token dosyasına (`AppColors`, `AppSpacing`, `AppTextStyles`, `AppRadius`) yeni `static const` eklenir.
2. Token ismi semantik olmalıdır: `AppColors.cardBackground` ✅ — `AppColors.grey200` ❌
3. Inline değer **asla** bırakılmaz; önce token oluşturulur, sonra kullanılır.