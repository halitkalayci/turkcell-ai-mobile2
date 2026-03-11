---
description: Clean Code Bölüm 6 — Nesneler ve Veri Yapıları; veri soyutlama, Law of Demeter, DTO ile Entity ayrımı.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 6: Nesneler ve Veri Yapıları

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 6: Objects and Data Structures

---

## 1. Veri Soyutlama (Data Abstraction)

Değişkenleri `private` yapıp getter/setter eklemek otomatik olarak soyutlama sağlamaz. Soyutlama, iç yapıyı gizleyip anlamlı bir arayüz sunmaktır.

```java
// YANLIŞ: İç yapıyı doğrudan açığa çıkarıyor (getter/setter ile)
public interface Vehicle {
    double getFuelTankCapacityInGallons();
    double getGallonsOfGasoline();
}

// DOĞRU: Soyut kavramla iç yapıyı gizliyor
public interface Vehicle {
    double getPercentFuelRemaining();
}
```

```java
// YANLIŞ: Product entity'sinin tüm alanlarını getter/setter ile dışarı açmak
@Entity
public class Product {
    private BigDecimal price;
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
// Dışarıdan: product.setPrice(product.getPrice().multiply(BigDecimal.valueOf(1.18)));

// DOĞRU: Davranışı entity üzerinde kapsülle
@Entity
public class Product {
    private BigDecimal price;
    public BigDecimal getPriceWithTax(BigDecimal taxRate) {
        return price.multiply(BigDecimal.ONE.add(taxRate));
    }
}
```

---

## 2. Veri/Nesne Anti-Simetrisi (The Object-Data Asymmetry)

Martin iki zıt yapıyı tanımlar:

| Özellik | **Nesne (Object)** | **Veri Yapısı (Data Structure)** |
|---|---|---|
| Amaç | Davranış gizler, veriyi saklar | Veriyi açar, davranış yoktur |
| Yeni tür ekleme | Kolay (yeni sınıf) | Zor (tüm fonksiyonlar değişir) |
| Yeni fonksiyon ekleme | Zor (tüm sınıflar değişir) | Kolay |
| Örnek | `ProductServiceImpl` | `ProductRequest`, `ProductResponse` |

**Kural:**
- Yeni davranış türleri eklenecekse → **Polimorfizm / OOP**.
- Yeni operasyonlar eklenecekse → **Veri yapısı + prosedürel**.
- DTO'lar saf veri yapısıdır; davranış ekleme.

```java
// DOĞRU: ProductRequest saf veri yapısı — davranış içermez
public class ProductRequest {
    private String name;
    private BigDecimal price;
    private int stock;
    private String sku;
    // yalnızca getter/setter veya @Data (Lombok)
}

// DOĞRU: ProductServiceImpl davranış içeriyor — veriyi gizler
@Service
public class ProductServiceImpl implements ProductService {
    public ProductResponse createProduct(ProductRequest request) { ... }
}
```

---

## 3. Demeter Yasası (The Law of Demeter)

> **"Yalnızca yakın arkadaşlarınla konuş."**

Bir metot yalnızca şunlara mesaj gönderebilir:
1. Kendi sınıfı (`this`)
2. Metodun parametreleri
3. Metodun içinde oluşturduğu nesneler
4. Sınıfın instance değişkenleri

**Kural:** Zincirleme çağrılarla derine inmek yasaktır.

```java
// YANLIŞ: Tren enkazı (Train Wreck) — Demeter ihlali
String outputDir = ctxt.getOptions().getScratchDir().getAbsolutePath();

// DOĞRU: Her nesne yalnızca doğrudan arkadaşına sorar
Options options = ctxt.getOptions();
File scratchDir = options.getScratchDir();
String outputDir = scratchDir.getAbsolutePath();

// DAHA İYİ: Sorumluluğu sahibine bırak
String outputDir = ctxt.getScratchDirectoryPath();
```

```java
// YANLIŞ: Proje örneği — Demeter ihlali
public void printProductCategory(Order order) {
    String category = order.getProduct().getCategory().getName(); // zincirleme
    System.out.println(category);
}

// DOĞRU
public void printProductCategory(Order order) {
    System.out.println(order.getProductCategoryName());
}
```

---

## 4. Tren Enkazları (Train Wrecks)

Zincirleme metodlar "tren enkazı" olarak adlandırılır. Hem Demeter'i ihlal eder hem de okunaktan uzaktır.

```java
// YANLIŞ
return request.getProduct().getInventory().getWarehouse().getCity();

// DOĞRU: Yüksek seviye metot yaz
return request.getProductWarehouseCity();
```

---

## 5. Bastardlar: Karma Yapılar (Hybrids)

Hem davranış hem veri içeren yapılar (yarı nesne - yarı veri yapısı) ikisinin de dezavantajlarını taşır. Yeni fonksiyon ve yeni tür eklemeyi zorlaştırır.

**Kural:** Bir sınıf ya saf davranış (nesne) ya da saf veri (DTO) olmalıdır. İkisi karıştırılmaz.

```java
// YANLIŞ: DTO içinde iş mantığı
public class ProductRequest {
    private BigDecimal price;

    // DTO'da iş mantığı olmamalı
    public BigDecimal getPriceWithTax() {
        return price.multiply(new BigDecimal("1.18"));
    }
}

// DOĞRU: İş mantığı service katmanında
public class ProductServiceImpl {
    public BigDecimal calculatePriceWithTax(BigDecimal price) {
        return price.multiply(TAX_RATE);
    }
}
```

---

## 6. Veri Transfer Nesneleri (Data Transfer Objects — DTO)

DTO'lar saf veri yapısıdır. `public` alanlar ya da getter/setter içerebilirler. **İş mantığı içermezler.**

**Kural:** Entity ile DTO ayrımını koru. Entity asla controller katmanına çıkmamalıdır.

```java
// YANLIŞ: Entity doğrudan dönülüyor
@GetMapping("/{id}")
public ResponseEntity<Product> getProduct(@PathVariable UUID id) {
    return ResponseEntity.ok(productRepository.findById(id).orElseThrow());
}

// DOĞRU: DTO ile dön
@GetMapping("/{id}")
public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
    return ResponseEntity.ok(productService.getProductById(id));
}
```

---

## 7. Active Record Antipattern

Active Record, hem veri hem de `save()`, `find()` gibi navigasyon metodlarını içeren veri yapısıdır. İş mantığı buraya eklenmez.

**Kural:** Active Record yapılarını saf veri yapısı olarak gör. İş kurallarını ayrı bir nesneye taşı.

```java
// YANLIŞ: Entity içinde iş mantığı
@Entity
public class Product {
    private int stock;

    public boolean isAvailableForSale() {
        return stock > 0 && computedRiskScore() < 5; // iş mantığı entity'de
    }

    private int computedRiskScore() { ... }
}

// DOĞRU: İş mantığı service katmanında
@Service
public class ProductAvailabilityService {
    public boolean isAvailableForSale(Product product) {
        return product.getStock() > 0;
    }
}
```

---

## 8. Özet — Bölüm 6 Temel Kurallar

| Kural | Açıklama |
|---|---|
| Soyutlama | Getter/setter = soyutlama değil; anlamlı arayüz sun |
| Nesne vs DTO | Nesne davranış gizler; DTO veri açar |
| Demeter Yasası | Yalnızca yakın arkadaşlarınla konuş |
| Tren enkazı yasak | Zincirleme erişim refactor edilir |
| Karma yapı yasak | Sınıf ya nesne ya veri yapısı |
| Entity ≠ DTO | Entity controller'a çıkmaz |
| Active Record | İş mantığı taşımaz; veri yapısı olarak kalır |
