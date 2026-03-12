---
name: flutter-generate-model
description: >
  OpenAPI spec'ten (`*Response` şeması) Dart model sınıfı (`*Model`) üreten yetenek.
  Contract-first zorunluluğunu ve tip eşleme kurallarını adım adım uygular.
---

# Flutter Model Oluşturma Yeteneği

Bu yetenek, OpenAPI sözleşmesindeki `*Response` şemasını okuyarak
projenin veri katmanı kurallarına tam uyumlu bir `*Model` sınıfı üretir.

---

## ADIM 1 — Spec'i Oku (Zorunlu İlk Adım)

İlgili OpenAPI dosyasını **mutlaka oku**; spec okunmadan model üretilemez.

```
docs/openapi/<domain>-v1.yml
```

Dosyayı okuduktan sonra `*Response` şemasından şunları çıkar:

| Çıkarılacak bilgi | Kaynak |
|---|---|
| Alan adları | `properties` anahtarları |
| Alan tipleri | `type` (+ `format` varsa) |
| Zorunlu alanlar | `required` listesi |
| Opsiyonel alanlar | `required`'da **olmayan** property'ler |

Spec'te **bulunmayan** bilgiyi uydurma; eksikse işlemi durdur ve kullanıcıya sor.

---

## ADIM 2 — Tip Eşlemesi Yap

Spec'teki her alan için karşılık gelen Dart tipini belirle:

| OpenAPI | Dart (non-nullable) | Dart (nullable) |
|---|---|---|
| `string` | `String` | `String?` |
| `string` + `format: uuid` | `String` | `String?` |
| `integer` | `int` | `int?` |
| `number` | `double` | `double?` |
| `boolean` | `bool` | `bool?` |
| `array` | `List<T>` | `List<T>?` |

- `required` listesindeki alan → **non-nullable**
- `required` listesinde **olmayan** alan → **nullable**

---

## ADIM 3 — Dosya Yerleşimi

```
mobile/
  lib/
    features/
      <domain>/
        model/
          <domain>_model.dart    ← buraya oluştur
```

Sınıf adı `PascalCase`, dosya adı `snake_case` olmalıdır:
- `ProductModel` → `product_model.dart`
- `CategoryModel` → `category_model.dart`

---

## ADIM 4 — Sınıf Şablonu

```dart
class <Domain>Model {
  const <Domain>Model({
    required this.<field1>,
    required this.<field2>,
    this.<optionalField>,       // required'da olmayan → nullable, this. ile
  });

  final <Type1> <field1>;
  final <Type2> <field2>;
  final <Type3>? <optionalField>;

  factory <Domain>Model.fromJson(Map<String, dynamic> json) => <Domain>Model(
    <field1>: json['<field1>'] as <Type1>,
    <field2>: (json['<field2>'] as num).toDouble(),   // number → double için
    <optionalField>: json['<optionalField>'] as <Type3>?,
  );
}
```

**`fromJson` cast kuralları:**
- `string` / `uuid` → `json['field'] as String`
- `integer` → `json['field'] as int`
- `number` → `(json['field'] as num).toDouble()`
- `boolean` → `json['field'] as bool`
- `array` → `(json['field'] as List<dynamic>).map(...).toList()`
- `nullable` alan → `json['field'] as Type?`

---

## ADIM 5 — Doğrulama Kontrol Listesi

Model üretildikten sonra aşağıdaki listeyi birer birer kontrol et:

- [ ] Spec'teki `required` alanların **tamamı** modelde non-nullable olarak mevcut
- [ ] Spec'te **olmayan** hiçbir alan modelde yok
- [ ] Her alanın Dart tipi tip eşleme tablosuna uygun
- [ ] `fromJson` içinde her cast spec'teki tiple tutarlı
- [ ] Sınıf adı `<Domain>Model` formatında, dosya adı `<domain>_model.dart` formatında

---

## Örnek: ProductResponse → ProductModel

**Spec özeti** (`product-v1.yml` → `ProductResponse`):
```yaml
required: []        # ProductResponse'da required yoksa tüm alanlar nullable
properties:
  id:    type: string, format: uuid
  name:  type: string
  price: type: number
  stock: type: integer
  sku:   type: string
```

**Üretilen model:**
```dart
class ProductModel {
  const ProductModel({
    this.id,
    this.name,
    this.price,
    this.stock,
    this.sku,
  });

  final String? id;
  final String? name;
  final double? price;
  final int? stock;
  final String? sku;

  factory ProductModel.fromJson(Map<String, dynamic> json) => ProductModel(
    id:    json['id']    as String?,
    name:  json['name']  as String?,
    price: json['price'] == null ? null : (json['price'] as num).toDouble(),
    stock: json['stock'] as int?,
    sku:   json['sku']   as String?,
  );
}
```

> **Not:** `ProductResponse`'da `required` bloğu tanımlıysa tüm required alanlar
> non-nullable olur. Her üretimde spec'teki güncel `required` listesi esas alınır.
