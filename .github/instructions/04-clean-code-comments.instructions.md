---
description: Clean Code Bölüm 4 — Yorumlar; iyi yorum türleri, kötü yorum antipatternleri, yorum yerine kod tercih etme.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 4: Yorumlar

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 4: Comments

---

## 1. Yorumlar Başarısızlığı Telafi Eder

> **"Yorumların varlığı her zaman bir başarısızlığın göstergesidir."**

Açıklayıcı kod yazamadığımızda yoruma başvururuz. Yorum yazmadan önce şu soruyu sor: *"Kodu daha iyi yazarak bu yoruma ihtiyacı ortadan kaldırabilir miyim?"*

**Kural:** Yorum yazmak son çaredir; önce kodu iyileştirmeyi dene.

```java
// YANLIŞ: Yorumla açıklamak zorunda kaldık
// kalan stok sıfırın üzerindeyse ve aktifse
if (product.getStock() > 0 && product.isActive()) { ... }

// DOĞRU: Yoarum gerek yok, kod kendini anlatıyor
if (product.isAvailableForSale()) { ... }
```

---

## 2. İyi Yorumlar (Good Comments)

Bazı yorumlar zorunlu ya da gerçekten faydalıdır.

### 2.1 Yasal Yorumlar (Legal Comments)

Telif hakkı ve lisans bildirimleri dosya başına konur.

```java
// Copyright (c) 2024 Turkcell. All rights reserved.
// Licensed under the Apache License, Version 2.0
```

### 2.2 Açıklayıcı Yorumlar (Informative Comments)

Soyut metot ya da regex gibi karmaşık ifadelerin döndüreceği değeri açıklar.

```java
// Formatı: kk:dd:ss EEE, dd MMM yyyy
Pattern timeMatcher = Pattern.compile("\\d*:\\d*:\\d* \\w*, \\w* \\d*, \\d*");
```

### 2.3 Niyet Açıklaması (Explanation of Intent)

Neden bu kararı aldığını açıklar — ne yaptığını değil.

```java
// Performans nedeniyle ürün listesi önceden sıralanıyor;
// downstream servislerin tekrar sıralaması engelleniyor.
Collections.sort(products, Comparator.comparing(Product::getSku));
```

### 2.4 Açıklama (Clarification)

Standart kütüphanenin döndürdüğü belirsiz değeri netleştirmek için kullanılır.

```java
assertTrue(a.compareTo(b) == -1); // a < b
assertTrue(a.compareTo(b) == 1);  // a > b
```

### 2.5 Uyarılar (Warning of Consequences)

Başka geliştiricileri olası sonuçlar hakkında uyarır.

```java
// Bu test çok yavaş çalışır; CI/CD ortamında @Disabled bırakılması önerilir.
@Test
public void testWithLargeDataSet() { ... }
```

### 2.6 TODO Yorumları

Yapılması gereken ama henüz yapılmamış işleri işaretler. IDE'ler bu etiketleri listeler.

```java
// TODO: Stok kontrolü asenkron event ile yapılacak (v2 scope)
public void reserveStock(UUID productId, int quantity) { ... }
```

### 2.7 Önem Vurgulama (Amplification)

Önemsiz görünen ama kritik olan bir detayı vurgular.

```java
// trim() kritiktir! Baştaki boşluk liste eşleşmesini bozar.
String sku = request.getSku().trim();
```

### 2.8 Javadoc (Public API)

Herkese açık API metodlarında Javadoc kullanılır.

```java
/**
 * Verilen SKU'ya sahip ürünü döndürür.
 *
 * @param sku Stok kodu
 * @return Ürün yanıtı
 * @throws ProductNotFoundException Ürün bulunamazsa
 */
public ProductResponse getProductBySku(String sku) { ... }
```

---

## 3. Kötü Yorumlar (Bad Comments)

### 3.1 Gevezelik (Mumbling)

Aceleyle yazılan, anlam taşımayan yorumlar.

```java
// YANLIŞ: Ne demek istediği belirsiz
// catch buraya geliyor
catch (Exception e) {
    // bir şeyler oldu
}
```

### 3.2 Gereksiz Yorumlar (Redundant Comments)

Zaten kodun söylediği şeyi tekrarlayan yorum zaman çalar.

```java
// YANLIŞ: Yorum koda hiçbir değer katmıyor
// productRepository'den ürün getir
return productRepository.findById(id);

// DOĞRU: Yorum olmadan aynı anlam
return productRepository.findById(id);
```

### 3.3 Yanıltıcı Yorumlar (Misleading Comments)

Kodu değiştirirken yorumu güncellemeyince yorum yalan söylemeye başlar.

```java
// YANLIŞ: Yorum kodu yanlış tanımlıyor
// Stok 0'dan büyükse false döner
public boolean isOutOfStock() {
    return stock <= 0; // Aslında <=0 kontrolü yapılıyor
}
```

### 3.4 Zorunlu Yorumlar (Mandated Comments)

Kurallar gereği her metoda Javadoc yazmak anlamsız gürültü üretir.

```java
// YANLIŞ: Hiçbir değer katmayan zorunlu Javadoc
/**
 * @param id id
 * @return product
 */
public Product getById(UUID id) { ... }
```

### 3.5 Günlük Yorumlar (Journal Comments)

Versiyon kontrol sistemleri varken değişiklik günlüğü yorum olarak tutulmamalıdır.

```java
// YANLIŞ
// 2024-01-10 - Ali: fiyat kontrolü eklendi
// 2024-01-15 - Veli: stok kontrolü kaldırıldı
// 2024-01-20 - Ayşe: exception handling düzeltildi
public ProductResponse createProduct(ProductRequest request) { ... }
```

### 3.6 Gürültü Yorumları (Noise Comments)

Açıkça belli olan şeyleri tekrarlayan yorumlar.

```java
// YANLIŞ
/** Constructor. */
public ProductServiceImpl() {}

/** The name of the product. */
private String productName;
```

### 3.7 Pozisyon İşaretçileri (Position Markers)

```java
// YANLIŞ: Bölüm başlıkları gürültüdür
// /////////// Actions ///////////
```

### 3.8 Kapanış Parantezi Yorumları (Closing Brace Comments)

Fonksiyon kısaysa bu yoruma hiç gerek yoktur.

```java
// YANLIŞ
while (hasNext()) {
    process();
} // while sonu
```

### 3.9 Atıf ve İmza Yorumları (Attributions and Bylines)

```java
// YANLIŞ: Git blame bu bilgiyi zaten sağlar
// Eklendi: Ali Yılmaz
private String sku;
```

### 3.10 HTML Yorumları

Javadoc dışı HTML etiketleri kaynak kodda okunaksız gürültüdür.

```java
// YANLIŞ
/**
 * <p>Bu metot ürünü günceller.</p>
 * <ul><li>SKU kontrol edilir</li></ul>
 */
```

### 3.11 Devre Dışı Bırakılmış Kod (Commented-Out Code)

Yoruma alınan kod silinmelidir; git gerekirse geri getirir.

```java
// YANLIŞ: Yoruma alınmış ölü kod
// product.setDiscount(0.10);
// product.setCategory("ELECTRONICS");
productRepository.save(product);
```

---

## 4. Özet — Bölüm 4 Temel Kurallar

| Kural | Açıklama |
|---|---|
| Kod > Yorum | Yorum yazmak yerine kodu daha iyi yaz |
| İzin verilen yorum | Yasal, niyet, uyarı, TODO, Javadoc (public API) |
| Gereksiz Javadoc yasak | Her metoda zorunlu yorum ekleme |
| Yoruma alınmış kod yasak | Ölü kodu sil; git geri getirir |
| Günlük yorum yasak | Değişiklik geçmişi için git kullan |
| Yanıltıcı yorum yasak | Kodu güncellersen yorumu da güncelle |
| Gürültü yorum yasak | Kodun söylediğini tekrarlama |
