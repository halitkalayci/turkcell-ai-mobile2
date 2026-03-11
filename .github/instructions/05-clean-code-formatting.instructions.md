---
description: Clean Code Bölüm 5 — Biçimlendirme; dikey düzen, gazete kuralı, yatay hizalama ve takım uyumu.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 5: Biçimlendirme

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 5: Formatting

---

## 1. Biçimlendirme Neden Önemlidir?

Kod değişir; biçimlendirme kalır. İyi biçimlendirme iletişim aracıdır. İlk kod satırları çok geçmeden silinse bile, okunabilirlik kültürü ve stili devam eder.

**Kural:** Biçimlendirme kuralları takım olarak belirlenmeli ve IDE formatter ile otomatik uygulanmalıdır. Bireysel tercih değil, takım anlaşmasıdır.

---

## 2. Dikey Biçimlendirme (Vertical Formatting)

### 2.1 Gazete Metaforu (The Newspaper Metaphor)

İyi bir gazete makalesini düşün: başlık ne hakkında olduğunu söyler, ilk paragraf büyük resmi verir, aşağı indikçe detay artar.

**Kural:** Kaynak dosya da aynı şekilde okunmalıdır. Üstte yüksek seviye kavramlar ve kararlar, aşağıda giderek azalan detay.

```java
// DOĞRU: Yüksek seviye önce
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) { // yüksek seviye
        assertSkuIsUnique(request.getSku());
        return toResponse(productRepository.save(toEntity(request)));
    }

    private void assertSkuIsUnique(String sku) { ... }   // orta seviye

    private Product toEntity(ProductRequest request) { ... }  // düşük seviye

    private ProductResponse toResponse(Product product) { ... } // düşük seviye
}
```

### 2.2 Dikey Açıklık (Vertical Openness Between Concepts)

İlgisiz kavramlar arasına **boş satır** koy.

```java
// YANLIŞ: Her şey bitişik
@Override
public ProductResponse createProduct(ProductRequest request) {
    assertSkuIsUnique(request.getSku());
    Product product = toEntity(request);
    Product saved = productRepository.save(product);
    return toResponse(saved);
}
private void assertSkuIsUnique(String sku) {
    if (productRepository.existsBySku(sku))
        throw new SkuAlreadyExistsException(sku);
}

// DOĞRU: Metodlar arasında boş satır
@Override
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

### 2.3 Dikey Yoğunluk (Vertical Density)

Aynı kavrama ait satırlar birbirine yakın tutulmalıdır. İlgisiz yorumlar kavramları birbirinden ayırmamalıdır.

```java
// YANLIŞ: Yorum alanları ilişkili kodları ayırıyor
public class Product {
    // Ürün adı
    private String name;

    // Fiyat bilgisi
    private BigDecimal price;
}

// DOĞRU: İlişkili alanlar yanyana
public class Product {
    private String name;
    private BigDecimal price;
    private int stock;
    private String sku;
}
```

### 2.4 Dikey Mesafe (Vertical Distance)

Birbirleriyle ilgili kavramlar birbirine yakın olmalıdır.

- **Değişken tanımları:** Kullanıldığı yere yakın.
- **Örnek değişkenler (Instance variables):** Sınıfın en üstünde, tek bir yerde.
- **Bağımlı fonksiyonlar:** Çağıran metot çağrılan metottan önce.
- **Kavramsal yakınlık:** Benzer işler yapan metodlar birbirine yakın.

```java
// YANLIŞ: Değişken uzakta tanımlanmış
public List<ProductResponse> getFilteredProducts() {
    List<ProductResponse> results = new ArrayList<>(); // burada tanımlandı
    // ... 30 satır kod ...
    for (Product p : productRepository.findAll()) {   // burada kullanıldı
        results.add(toResponse(p));
    }
    return results;
}

// DOĞRU: Kullanıma yakın tanım, kısa metot
public List<ProductResponse> getFilteredProducts() {
    return productRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
}
```

### 2.5 Bağımlı Fonksiyon Sıralaması (Dependent Functions)

Çağıran fonksiyon çağrılan fonksiyonun hemen **üstünde** yer almalıdır.

```java
// DOĞRU: createProduct → assertSkuIsUnique → toEntity → toResponse sırasıyla
public ProductResponse createProduct(ProductRequest request) {
    assertSkuIsUnique(request.getSku());
    return toResponse(productRepository.save(toEntity(request)));
}

private void assertSkuIsUnique(String sku) { ... }

private Product toEntity(ProductRequest request) { ... }

private ProductResponse toResponse(Product product) { ... }
```

---

## 3. Yatay Biçimlendirme (Horizontal Formatting)

### 3.1 Satır Uzunluğu

Maksimum **120 karakter** per satır. Ideal hedef 80–100 karakter.

**Kural:** Yatay kaydırma gerektiren satırlar bölünmelidir.

```java
// YANLIŞ: Çok uzun satır
return productRepository.findBySkuAndActiveTrueAndStockGreaterThanOrderByNameAsc(request.getSku(), minStock);

// DOĞRU: Bölünmüş
return productRepository
        .findBySkuAndActiveTrueAndStockGreaterThan(request.getSku(), minStock);
```

### 3.2 Yatay Açıklık ve Yoğunluk (Horizontal Openness & Density)

Operatörler arasına boşluk koy; metodun çağrı parantezleri ile adı arasına koyma.

```java
// YANLIŞ
int result=a*b+c/d;
doSomething (x,y);

// DOĞRU
int result = a * b + c / d;
doSomething(x, y);
```

### 3.3 Yatay Hizalama (Horizontal Alignment)

Sütun hizalaması yapmak hatalıdır: gözün yanlış yere kaymasına neden olur ve refactor'ı zorlaştırır.

```java
// YANLIŞ: Sütun hizalaması
private   String       name;
private   BigDecimal   price;
private   int          stock;

// DOĞRU: Hizalanmamış, sade
private String name;
private BigDecimal price;
private int stock;
```

### 3.4 Girintileme (Indentation)

Her kapsam seviyesi için **4 boşluk** girintileme kullanılır (tab değil). Kısa `if` bloklarını bile girintile; tek satıra sıkıştırma.

```java
// YANLIŞ: Girintisiz kısa blok
if (product.isActive()) return toResponse(product);

// DOĞRU
if (product.isActive()) {
    return toResponse(product);
}
```

---

## 4. Takım Kuralları (Team Rules)

**Kural:** Takım bir biçimlendirme standardı belirlemelidir ve tüm üyeler ona uymalıdır. Kişisel tercihler takım standardının önüne geçemez.

Bu proje için önerilen standartlar:

| Ayar | Değer |
|---|---|
| Girinti | 4 boşluk |
| Satır uzunluğu | Maks. 120 karakter |
| Açılış parantezi `{` | Aynı satırda (K&R stili) |
| Metot arası boş satır | 1 boş satır |
| İmport sırası | `java.*` → `javax.*` → `org.*` → `com.*` → proje paketleri |
| Joker import yasak | `import java.util.*` yerine açık import |

---

## 5. Özet — Bölüm 5 Temel Kurallar

| Kural | Açıklama |
|---|---|
| Gazete metaforu | Üstte yüksek seviye; aşağıda detay |
| Kavramlar arası boş satır | İlgisiz kod blokları ayrılır |
| İlişkili kod yakın | Instance variable sınıf tepesinde |
| Çağıran üstte | Bağımlı fonksiyon çağrılandan önce |
| Maks. 120 karakter/satır | Yatay kaydırma olmaz |
| Sütun hizalama yasak | Sahte sütunlar oluşturma |
| 4 boşluk girinti | Her kapsam için |
| Takım standardı | Kişisel tercih değil, anlaşma |
