---
name: flutter-create-widget
description: Flutter uygulamasında yeniden kullanılabilir UI widgetları üreten yetenek.
---

# Flutter Widget Oluşturma Yeteneği

Bu yetenek, projenin tasarım tokenlarına ve Clean Code prensiplerine tam uyumlu, yeniden kullanılabilir Flutter widget'ları üretir.

---

## ADIM 1 — Talep Analizi

Widget oluşturmadan önce şunu belirle:

| Soru | Yanıt alınacak kaynak |
|---|---|
| Hangi domain? (product / category / common / ...) | Kullanıcı isteği |
| Stateless mu, Stateful mı? | Durumu var mı? → Stateful; yoksa Stateless |
| Hangi verilere ihtiyaç duyuyor? | Backend DTO'ları (`ProductResponse`, `CategoryResponse`) |
| Nereye yerleşiyor (liste, detay, form, ...)?  | Kullanıcı açıklaması |

Eğer bilgi eksikse **uydurma**; kullanıcıya sor.

---

## ADIM 2 — Dosya Yerleşim Kuralı

```
mobile/
  lib/
    features/
      <domain>/           ← product | category | order | ...
        widgets/
          <widget_name>.dart
    core/
      widgets/            ← birden fazla domain'de kullanılan ortak widgetlar
        <widget_name>.dart
      theme/
        app_colors.dart
        app_spacing.dart
        app_text_styles.dart
        app_radius.dart
```

- Domain-specific widget → `lib/features/<domain>/widgets/`
- Paylaşılan widget → `lib/core/widgets/`
- Her widget **tek dosyada** yaşar; birden fazla widget tek dosyaya sıkıştırılmaz.

---

## ADIM 3 — Sınıf Yapısı

### 3.1 İsimlendirme

- Sınıf adı `PascalCase`, dosya adı `snake_case` olmalıdır.
- İsim **ne olduğunu** söylemeli, **nasıl göründüğünü** değil.
  - ✅ `ProductCard`, `CategoryChip`, `PriceLabel`
  - ❌ `BigRedBox`, `Item1Widget`, `MyCard`
- Widget kelimesi sınıf adına eklenmez: `ProductCard` ✅ — `ProductCardWidget` ❌

### 3.2 StatelessWidget Şablonu

```dart
class ProductCard extends StatelessWidget {
  const ProductCard({
    super.key,
    required this.name,
    required this.price,
    this.imageUrl,
    this.onTap,
  });

  final String name;
  final double price;
  final String? imageUrl;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: _buildCard(),
    );
  }

  Widget _buildCard() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.lg),
      decoration: BoxDecoration(
        color: AppColors.surface,
        borderRadius: AppRadius.cardRadius,
        border: Border.all(color: AppColors.border),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (imageUrl != null) _buildImage(),
          SizedBox(height: AppSpacing.sm),
          _buildName(),
          SizedBox(height: AppSpacing.xs),
          _buildPrice(),
        ],
      ),
    );
  }

  Widget _buildName() {
    return Text(
      name,
      style: AppTextStyles.headingSmall.copyWith(color: AppColors.textPrimary),
      maxLines: 2,
      overflow: TextOverflow.ellipsis,
    );
  }

  Widget _buildPrice() {
    return Text(
      '₺${price.toStringAsFixed(2)}',
      style: AppTextStyles.labelLarge.copyWith(color: AppColors.primary),
    );
  }

  Widget _buildImage() {
    return ClipRRect(
      borderRadius: AppRadius.cardRadius,
      child: Image.network(
        imageUrl!,
        height: 120,
        width: double.infinity,
        fit: BoxFit.cover,
      ),
    );
  }
}
```

### 3.3 StatefulWidget Şablonu

```dart
class QuantitySelector extends StatefulWidget {
  const QuantitySelector({
    super.key,
    required this.initialValue,
    required this.onChanged,
    this.min = 1,
    this.max = 99,
  });

  final int initialValue;
  final ValueChanged<int> onChanged;
  final int min;
  final int max;

  @override
  State<QuantitySelector> createState() => _QuantitySelectorState();
}

class _QuantitySelectorState extends State<QuantitySelector> {
  late int _quantity;

  @override
  void initState() {
    super.initState();
    _quantity = widget.initialValue;
  }

  void _increment() {
    if (_quantity < widget.max) {
      setState(() => _quantity++);
      widget.onChanged(_quantity);
    }
  }

  void _decrement() {
    if (_quantity > widget.min) {
      setState(() => _quantity--);
      widget.onChanged(_quantity);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        _buildButton(Icons.remove, _decrement),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: AppSpacing.md),
          child: Text('$_quantity', style: AppTextStyles.bodyLarge),
        ),
        _buildButton(Icons.add, _increment),
      ],
    );
  }

  Widget _buildButton(IconData icon, VoidCallback onPressed) {
    return IconButton(
      onPressed: onPressed,
      icon: Icon(icon, size: 20),
      style: IconButton.styleFrom(
        backgroundColor: AppColors.primary.withOpacity(0.1),
        foregroundColor: AppColors.primary,
        shape: RoundedRectangleBorder(borderRadius: AppRadius.buttonRadius),
      ),
    );
  }
}
```

---

## ADIM 4 — Zorunlu Kurallar (Checklist)

Her üretilen widget aşağıdaki kontrolleri geçmek zorundadır:

- [ ] **Token yasağına uyuluyor**: Ham renk, spacing, fontSize yok
- [ ] **`const` constructor**: Mümkünse `const` tanımlı
- [ ] **`build()` metodu sade**: Yalnızca widget ağacını döndürür; iş mantığı `_buildXxx()` private metodlarında
- [ ] **`_buildXxx()` metodları tek sorumluluk taşıyor**: Her metod yalnızca bir alt bölümü inşa eder
- [ ] **Nullable parametreler `?` ile işaretli**: Zorunlu olmayanlar `required` değil
- [ ] **`onTap` / callback parametreleri `VoidCallback?` veya `ValueChanged<T>?`**: Doğrudan `Function` kullanılmaz
- [ ] **`maxLines` + `overflow`**: Her `Text` widget'ında taşma önlemi var

---

## ADIM 5 — Token Referans Tablosu

| Kullanım | Token | Örnek |
|---|---|---|
| Arka plan rengi | `AppColors.surface` / `AppColors.background` | `color: AppColors.surface` |
| Birincil renk | `AppColors.primary` | `color: AppColors.primary` |
| Hata rengi | `AppColors.error` | `color: AppColors.error` |
| Metin (birincil) | `AppColors.textPrimary` | `.copyWith(color: AppColors.textPrimary)` |
| Standart padding | `AppSpacing.lg` (16 dp) | `EdgeInsets.all(AppSpacing.lg)` |
| Küçük boşluk | `AppSpacing.sm` (8 dp) | `SizedBox(height: AppSpacing.sm)` |
| Kart başlığı | `AppTextStyles.headingSmall` | `style: AppTextStyles.headingSmall` |
| Gövde metni | `AppTextStyles.bodyMedium` | `style: AppTextStyles.bodyMedium` |
| Kart köşe | `AppRadius.cardRadius` | `borderRadius: AppRadius.cardRadius` |
| Buton köşe | `AppRadius.buttonRadius` | `borderRadius: AppRadius.buttonRadius` |

---

## ADIM 6 — Çıktı Formatı

Her widget üretiminde şunlar sağlanır:

1. **Widget dosyası** — tam implementasyon, import'lar dahil
2. **Kullanım örneği** — widget'ın bir üst katmanda nasıl çağrıldığını gösteren kod snippet'i
3. **Parametre tablosu** — her constructor parametresinin adı, tipi ve amacı

### Kullanım Örneği Formatı

```dart
// Örnek kullanım — ProductCard
ProductCard(
  name: product.name,
  price: product.price,
  imageUrl: product.imageUrl,
  onTap: () => context.push('/products/${product.id}'),
)
```

### Parametre Tablosu Formatı

| Parametre | Tip | Zorunlu | Açıklama |
|---|---|---|---|
| `name` | `String` | Evet | Ürün adı |
| `price` | `double` | Evet | Ürün fiyatı (TL) |
| `imageUrl` | `String?` | Hayır | Ürün görseli URL'i |
| `onTap` | `VoidCallback?` | Hayır | Kart tıklama callback'i |