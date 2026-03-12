---
applyTo: "**/*.dart"
---

# Flutter Veri Katmanı Kuralları

Bu kurallar Flutter projesindeki **model** ve **servis** sınıfları için geçerlidir.
Tüm maddeler zorunludur; istisna tanınmaz.

---

## A) Contract-First Model Kuralları

### A.1 — Spec Önce Okunur

Model sınıfı oluşturmadan ya da güncellemeden önce ilgili OpenAPI dosyası **mutlaka okunmalıdır**.

```
docs/openapi/product-v1.yml   → ProductResponse  → ProductModel
docs/openapi/category-v1.yml  → CategoryResponse → CategoryModel
```

Spec okunmadan model yazılamaz. Bu kural **ihlal edilemez**.

### A.2 — Yalnızca Spec'teki Alanlar

- Spec'teki `*Response` şemasında **bulunmayan** alan modele eklenemez.
- Spec'teki `required` listesindeki alanların **tamamı** modelde yer almalıdır.

**Yasak örnekler** (`product-v1.yml` `ProductResponse`'unda olmayan alanlar):
```dart
// ❌ description → spec'te yok
// ❌ categoryId  → spec'te yok
final String description;
final int categoryId;
```

### A.3 — Tip Eşleme Tablosu (Zorunlu)

| OpenAPI tipi | Dart tipi |
|---|---|
| `string` | `String` |
| `string` + `format: uuid` | `String` |
| `integer` | `int` |
| `number` | `double` |
| `boolean` | `bool` |
| `array` | `List<T>` |

**Yasak örnek** (`id` spec'te `string/uuid`, Dart'ta `int` olamaz):
```dart
// ❌
final int id;

// ✅
final String id;
```

### A.4 — Nullable Politikası

Spec'teki `required` listesinde **olmayan** alanlar Dart'ta nullable tanımlanır.

```dart
// spec'te required: [id, name, price, stock, sku]  →  diğerleri nullable
final String? description;   // required'da yok → nullable ✅
```

### A.5 — fromJson Tip Güvenliği

`fromJson` içinde her alan spec'teki tipe uygun cast edilmelidir:

```dart
factory ProductModel.fromJson(Map<String, dynamic> json) => ProductModel(
  id:    json['id']    as String,
  name:  json['name']  as String,
  price: (json['price'] as num).toDouble(),   // number → double
  stock: json['stock'] as int,
  sku:   json['sku']   as String,
);
```

---

## B) Network Sabit Kuralları

### B.1 — AppConstants Zorunlu Yapısı

`baseUrl` ve tüm endpoint path sabitleri **yalnızca**
`lib/core/network/app_constants.dart` içindeki `AppConstants` sınıfında
`static const` olarak tanımlanır.

```dart
abstract final class AppConstants {
  static const String baseUrl = 'http://localhost:8080/api/v1';

  static const String productsPath   = '/products';
  static const String categoriesPath = '/categories';
}
```

### B.2 — Magic String Yasağı

Servis sınıfları içinde ham URL string yazımı **kesinlikle yasaktır**.

```dart
// ❌ Magic string — yasak
static const String _baseUrl = 'http://localhost:8080/api/v1/products';

// ✅ Sabitten türetme — zorunlu
final uri = Uri.parse('${AppConstants.baseUrl}${AppConstants.productsPath}');
```

### B.3 — Servis Sınıfı Örüntüsü

```dart
import 'package:ecommerce/core/network/app_constants.dart';

class ProductService {
  Future<List<ProductModel>> fetchAll() async {
    final uri = Uri.parse('${AppConstants.baseUrl}${AppConstants.productsPath}');
    final response = await http.get(uri);

    if (response.statusCode != 200) {
      throw Exception('Ürünler yüklenemedi: ${response.statusCode}');
    }

    final List<dynamic> body = jsonDecode(response.body) as List<dynamic>;
    return body
        .map((item) => ProductModel.fromJson(item as Map<String, dynamic>))
        .toList();
  }
}
```
