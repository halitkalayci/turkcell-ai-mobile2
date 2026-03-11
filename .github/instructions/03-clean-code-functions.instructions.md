---
description: Clean Code Bölüm 3 — Fonksiyonlar; küçük tut, tek iş yap, soyutlama seviyeleri, yan etkisizlik.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 3: Fonksiyonlar

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 3: Functions

---

## 1. Küçük Tut (Small!)

Bir fonksiyonun ilk kuralı: **küçük olmalıdır.** İkinci kural: **daha da küçük olmalıdır.**

Fonksiyon gövdesi ideal olarak 4–10 satır arasında tutulur. Ekrana sığmayan fonksiyon refactor sinyali verir.

```java
// YANLIŞ: Tek fonksiyonda çok fazla iş
public ProductResponse saveProduct(ProductRequest request) {
    if (productRepository.existsBySku(request.getSku())) {
        throw new SkuAlreadyExistsException(request.getSku());
    }
    Product product = new Product();
    product.setName(request.getName());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    product.setSku(request.getSku());
    Product saved = productRepository.save(product);
    ProductResponse response = new ProductResponse();
    response.setId(saved.getId());
    response.setName(saved.getName());
    response.setPrice(saved.getPrice());
    response.setStock(saved.getStock());
    response.setSku(saved.getSku());
    return response;
}

// DOĞRU: Her iş ayrı küçük metoda taşındı
public ProductResponse createProduct(ProductRequest request) {
    assertSkuIsUnique(request.getSku());
    Product saved = productRepository.save(toEntity(request));
    return toResponse(saved);
}

private void assertSkuIsUnique(String sku) {
    if (productRepository.existsBySku(sku))
        throw new SkuAlreadyExistsException(sku);
}
```

---

## 2. Tek İş Yap (Do One Thing)

> **"Fonksiyonlar bir şey yapmalıdır. Onu iyi yapmalıdır. Yalnızca onu yapmalıdır."**

Fonksiyonun "bir şey" yapıp yapmadığını anlamak için: fonksiyonun adından başka anlamlı bir isimle yeni bir fonksiyon çıkarabiliyorsan, o fonksiyon birden fazla şey yapıyordur.

**Kural:** Fonksiyon adına "ve" (and) bağlacı girmemeli.

```java
// YANLIŞ: validate VE save yapıyor
public void validateAndSaveProduct(ProductRequest request) { ... }

// DOĞRU: Ayrı sorumluluklar
public void validateProduct(ProductRequest request) { ... }
public Product saveProduct(Product product) { ... }
```

---

## 3. Fonksiyon Başına Tek Soyutlama Seviyesi (One Level of Abstraction per Function)

Bir fonksiyon içinde yüksek seviye soyutlamalar (`getProducts()`) ile düşük seviye detaylar (`product.price = price * 0.18`) karışmamalıdır.

**Kural:** Bir fonksiyonun tüm ifadeleri aynı soyutlama seviyesinde olmalıdır.

```java
// YANLIŞ: Yüksek seviye + düşük seviye detay karışık
public ProductResponse createProduct(ProductRequest request) {
    if (productRepository.existsBySku(request.getSku()))  // düşük seviye kontrol
        throw new SkuAlreadyExistsException(request.getSku());
    Product product = Product.builder()
        .name(request.getName())
        .price(request.getPrice())     // düşük seviye atama
        .sku(request.getSku())
        .build();
    return toResponse(productRepository.save(product));    // yüksek seviye
}

// DOĞRU: Aynı seviyede soyutlama
public ProductResponse createProduct(ProductRequest request) {
    assertSkuIsUnique(request.getSku());
    return toResponse(productRepository.save(toEntity(request)));
}
```

---

## 4. Switch İfadelerinden Kaçın (Switch Statements)

`switch` ifadeleri doğası gereği birden fazla iş yapar. Kaçınılamıyorsa, **bir kez** yazılmalı ve polimorfizm arkasına gizlenmelidir.

```java
// YANLIŞ: switch içinde davranış değişikliği
public BigDecimal calculateDiscount(String productType, BigDecimal price) {
    switch (productType) {
        case "ELECTRONICS": return price.multiply(new BigDecimal("0.10"));
        case "CLOTHING":    return price.multiply(new BigDecimal("0.20"));
        default:            return BigDecimal.ZERO;
    }
}

// DOĞRU: Polimorfizm ile Abstract Factory
interface DiscountPolicy {
    BigDecimal apply(BigDecimal price);
}
class ElectronicsDiscount implements DiscountPolicy { ... }
class ClothingDiscount implements DiscountPolicy { ... }
```

---

## 5. Açıklayıcı İsimler (Use Descriptive Names)

Uzun bir isim, kısa ve belirsiz bir isimden iyidir. Bir fonksiyonun yaptığı şeyi açıklayan bir isim bulmak için harcanan zaman, okuyucunun kodu anlamak için harcayacağı süreden daha azdır.

**Kural:** Fonksiyon ismi ne yaptığını açıkça söylemelidir; yorum gerektirmemelidir.

```java
// YANLIŞ
private boolean check(Product p) { ... }
private void process(ProductRequest r) { ... }

// DOĞRU
private boolean isSkuAlreadyRegistered(String sku) { ... }
private ProductResponse buildResponseFromRequest(ProductRequest request) { ... }
```

---

## 6. Fonksiyon Argümanları (Function Arguments)

İdeal argüman sayısı **sıfırdır (niladik)**. Sonra **bir (monadik)**, sonra **iki (diadik)**. Üç argüman (triadik) ciddi gerekçe ister. Dörtten fazla argüman **yasaktır**.

### 6.1 Bayrak Argümanları (Flag Arguments)

Boolean parametre geçmek, fonksiyonun iki şey yaptığına işarettir.

```java
// YANLIŞ: true/false ile farklı davranış
public ProductResponse upsertProduct(ProductRequest request, boolean isNew) { ... }

// DOĞRU: İki ayrı fonksiyon
public ProductResponse createProduct(ProductRequest request) { ... }
public ProductResponse updateProduct(UUID id, ProductRequest request) { ... }
```

### 6.2 Çok Argümanlı Metodları Nesneyle Sarmala

```java
// YANLIŞ: Çok parametre
Circle createCircle(double x, double y, double radius) { ... }

// DOĞRU: Nesneyle paketle
Circle createCircle(Point center, double radius) { ... }
```

---

## 7. Yan Etkisiz Ol (Have No Side Effects)

Bir fonksiyon adı ne söylüyorsa yalnızca onu yapmalı; başka global durumu, parametre nesnesini ya da sistemi sessizce değiştirmemelidir.

```java
// YANLIŞ: checkPassword hem kontrol hem session açıyor (yan etki)
public boolean checkPassword(String userName, String password) {
    User user = findByName(userName);
    if (user.passwordMatches(password)) {
        Session.initialize(); // YAN ETKİ — isim bunu söylemiyor
        return true;
    }
    return false;
}

// DOĞRU: Adı niyeti açıklıyor ya da iki ayrı metot
public boolean isPasswordValid(String userName, String password) { ... }
public void initializeSession(String userName) { ... }
```

---

## 8. Komut-Sorgu Ayrımı (Command Query Separation)

Bir fonksiyon ya bir şey **yapar** (komut) ya da bir şey **döndürür** (sorgu). İkisini aynı anda yapmamalıdır.

```java
// YANLIŞ: set yapıp boolean dönüyor — belirsiz
if (set("username", "Bob")) { ... }

// DOĞRU: Sorgu ve komut ayrı
if (attributeExists("username")) {
    setAttribute("username", "Bob");
}
```

---

## 9. Exception Fırlat, Hata Kodu Döndürme (Prefer Exceptions to Error Codes)

Hata kodları döndürmek; çağıranı kodun hemen ardından hatayı kontrol etmeye zorlar, kodu iç içe geçirir.

```java
// YANLIŞ: Hata kodu
int result = deletePage(page);
if (result == E_OK) {
    registry.deleteReference(page.name);
} else {
    logger.log("delete failed");
}

// DOĞRU: Exception fırlat
try {
    deletePage(page);
    registry.deleteReference(page.name);
} catch (Exception e) {
    logger.log(e.getMessage());
}
```

---

## 10. Kendini Tekrarlama (Don't Repeat Yourself — DRY)

Tekrar, kötü kodun temelidir. Bir algoritmanın birden fazla yerde tekrar etmesi, her değişiklikte tüm kopyaların güncellenmesini gerektirir. Bir güncelleme unutulursa hata doğar.

**Kural:** Aynı mantık iki yerde görünüyorsa ortak bir metoda çıkar.

```java
// YANLIŞ: Aynı mapping iki yerde
public ProductResponse createProduct(ProductRequest req) {
    Product p = Product.builder().name(req.getName()).price(req.getPrice()).build();
    ...
}
public ProductResponse updateProduct(UUID id, ProductRequest req) {
    Product p = Product.builder().name(req.getName()).price(req.getPrice()).build();
    ...
}

// DOĞRU: Tek kaynak
private Product toEntity(ProductRequest req) {
    return Product.builder().name(req.getName()).price(req.getPrice()).build();
}
```

---

## 11. Özet — Bölüm 3 Temel Kurallar

| Kural | Açıklama |
|---|---|
| Küçük tut | 4–20 satır ideal; ekrana sığmayan refactor sinyali |
| Tek iş | Adına "ve" giremiyorsa tek iştir |
| Tek soyutlama seviyesi | Yüksek ve düşük seviye karışmaz |
| Switch → polimorfizm | switch bir kez; sonra soyutla |
| Açıklayıcı isim | Yorum gerektirmeyen isim |
| Max 3 argüman | 4+ yasak; nesneyle paketle |
| Bayrak argümanı yasak | Boolean parametre = iki fonksiyon |
| Yan etkisiz | İsim ne söylüyorsa yalnızca onu yap |
| CQS | Komut ya da sorgu; ikisi birden değil |
| DRY | Tekrar eden mantık → ortak metot |
