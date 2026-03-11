---
description: Bu dosya Clean Code prensipleri içerisinde isinlendirme prensiplerinin doğru uygulanması üzerine kural setini içeren dosyadır.
applyTo: "**/*.java"
---

# Clean Code - İsimlendirme Kuralları

## Amaç

Bu dosya, Robert C. Martin'in **Clean Code** kitabındaki **isimlendirme** prensiplerinin uygulanması için talimatları içerir.

> **Kaynak:** Robert C. Martin — *Clean Code: A Handbook of Agile Software Craftsmanship* (2008), Bölüm 2: Meaningful Names

---

## 1. Amacı Açıklayan İsimler Kullan (Use Intention-Revealing Names)

Bir değişken, fonksiyon ya da sınıf isminin neden var olduğunu, ne yaptığını ve nasıl kullanıldığını açıkça ifade etmesi gerekir. İsim bir yorum gerektiriyorsa, yanlış isim seçilmiş demektir.

**Kural:** İsim; *neden var*, *ne yapar*, *nasıl kullanılır* sorularını cevaplar.

```java
// YANLIŞ: Ne olduğu belirsiz
int d; // elapsed time in days

// DOĞRU
int elapsedTimeInDays;
int daysSinceCreation;
```

```java
// YANLIŞ: Amacı gizli
List<int[]> getThem() { ... }

// DOĞRU
List<Product> getActiveProducts() { ... }
```

---

## 2. Yanlış Bilgi Vermekten Kaçın (Avoid Disinformation)

Yanıltıcı ipuçları bırakmak veya yanlış anlama yaratacak isimler kullanmak yasaktır. Programcılar gerçek anlamı farklı olan kelimeleri tür adı olarak kullanmamalıdır.

**Kural:** İsim, gerçekte temsil ettiği şeyden farklı bir anlam çağrıştırmamalıdır.

```java
// YANLIŞ: Liste değil ama ismi "List" içeriyor
Map<UUID, Product> productList; // Map tipinde ama "List" adı taşıyor

// DOĞRU
Map<UUID, Product> productMap;
Map<UUID, Product> productsById;
```

```java
// YANLIŞ: Küçük L ve büyük O karıştırılabilir
int l = 1;
int O = 0;

// DOĞRU: Açıklayıcı isimler
int lineCount = 1;
int offsetValue = 0;
```

---

## 3. Anlamlı Ayrımlar Yap (Make Meaningful Distinctions)

Derleyiciyi memnun etmek için yapılan keyfi değişiklikler (sayı ekleme, gürültü kelimesi kullanma) anlamsızdır. İki şeyin ismi farklıysa, farklı şeyleri temsil etmelidir.

**Kural:** `a1`, `a2` gibi sayı serisi isimlendirme ve `Info`, `Data`, `Manager`, `Processor` gibi gürültü ekler kullanılmaz.

```java
// YANLIŞ: Sayı serisi — neden farklı olduklarını göstermiyor
void copyChars(char[] a1, char[] a2) { ... }

// DOĞRU
void copyChars(char[] source, char[] destination) { ... }
```

```java
// YANLIŞ: Gürültü kelimeler — ProductInfo ve Product aynı mı?
class Product { ... }
class ProductInfo { ... }   // fark belirsiz
class ProductData { ... }   // fark belirsiz

// DOĞRU: Farkı yansıtan isimler
class Product { ... }
class ProductRequest { ... }
class ProductResponse { ... }
```

> **Proje notu:** Bu projede `ProductRequest` / `ProductResponse` ayrımı doğru uygulanmaktadır. Bu kalıba uyun.

---

## 4. Telaffuz Edilebilir İsimler Kullan (Use Pronounceable Names)

Konuşurken telaffuz edemediğin bir isim, iletişimsizliğe yol açar.

**Kural:** Kısaltma ve kırpılmış isimler yerine tam, telaffuz edilebilir kelimeler kullan.

```java
// YANLIŞ: Telaffuz edilemez
private Date genymdhms; // generation year, month, day, hour, minute, second
private Date modymdhms;

// DOĞRU
private Date generationTimestamp;
private Date modificationTimestamp;
```

---

## 5. Aranabilir İsimler Kullan (Use Searchable Names)

Tek harfli isimler ve sayısal sabitler aranabilir değildir. Uzun isimler kısa isimlerden daha iyidir çünkü aranabilirdir.

**Kural:** Sabitler için `static final` sabit tanımla; tek harfli isimler yalnızca çok kısa kapsamlı döngü sayaçları için kabul edilir.

```java
// YANLIŞ: 7 ve 4 ne anlama geliyor?
for (int j = 0; j < 34; j++) {
    s += (t[j] * 4) / 5;
}

// DOĞRU
static final int WORK_DAYS_PER_WEEK = 5;
static final int NUMBER_OF_TASKS = 34;

int realDaysPerIdealDay = 4;
int sum = 0;
for (int taskIndex = 0; taskIndex < NUMBER_OF_TASKS; taskIndex++) {
    int realTaskDays = taskEstimate[taskIndex] * realDaysPerIdealDay;
    sum += realTaskDays / WORK_DAYS_PER_WEEK;
}
```

---

## 6. Kodlamalardan Kaçın (Avoid Encodings)

Tip ya da kapsam bilgisini isme kodlamak ek bir kod çözme yükü yaratır. Modern IDE'ler bu tür bilgileri zaten gösterir.

### 6.1 Macar Notasyonu Kullanma

```java
// YANLIŞ: Macar notasyonu
String strProductName;
int iStock;
boolean bIsActive;

// DOĞRU
String productName;
int stock;
boolean active;
```

### 6.2 `I` Ön Eki ile Arayüz İsimlendirme

```java
// YANLIŞ: Arayüz adına "I" ön eki
interface IProductService { ... }

// DOĞRU: Arayüz sade, implementasyon açıklayıcı
interface ProductService { ... }
class ProductServiceImpl implements ProductService { ... }
```

> **Proje notu:** Bu projede `ProductService` / `ProductServiceImpl` kalıbı kullanılmaktadır. Bu kalıba uyun.

---

## 7. Zihinsel Haritalardan Kaçın (Avoid Mental Mapping)

Okuyucunun ismini zihninde başka bir kavrama çevirmesini gerektiren isimler tercih edilmez.

**Kural:** Tek harfli değişken isimlerinden kaçın; `i`, `j`, `k` yalnızca küçük kapsamlı for döngüsü sayaçları için kabul edilebilir.

```java
// YANLIŞ: r neyi temsil ediyor?
String r = product.getName().toLowerCase();

// DOĞRU
String normalizedProductName = product.getName().toLowerCase();
```

---

## 8. Sınıf İsimleri (Class Names)

**Kural:** Sınıf ve nesne isimleri **isim ya da isim öbeği** olmalıdır. Fiil kullanılmaz.

| Kabul Edilebilir | Kabul Edilemez |
|---|---|
| `Product` | `ProductManager` (muğlak) |
| `ProductRepository` | `ProductProcessor` (muğlak) |
| `ProductRequest` | `ProductInfo` (gürültü) |
| `ProductResponse` | `ProductData` (gürültü) |
| `ProductNotFoundException` | `ManageProduct` (fiil) |
| `GlobalExceptionHandler` | `DoProductStuff` (anlamsız) |

```java
// YANLIŞ
class ProductManager { ... }
class DataProcessor { ... }

// DOĞRU
class ProductServiceImpl { ... }
class GlobalExceptionHandler { ... }
```

---

## 9. Metot İsimleri (Method Names)

**Kural:** Metot isimleri **fiil ya da fiil öbeği** olmalıdır.

```java
// YANLIŞ: İsim gibi isimlendirme
public ProductResponse product(UUID id) { ... }

// DOĞRU: Fiil ile başlar
public ProductResponse getProductById(UUID id) { ... }
public ProductResponse createProduct(ProductRequest request) { ... }
public void deleteProduct(UUID id) { ... }
```

### 9.1 Accessor / Mutator / Predicate İsimleri

JavaBeans standardına göre:
- **Accessor (getter):** `get` ön eki → `getName()`, `getPrice()`
- **Mutator (setter):** `set` ön eki → `setName()`, `setStock()`
- **Predicate (boolean):** `is` veya `has` ön eki → `isActive()`, `hasStock()`

```java
// DOĞRU
product.getName();
product.setPrice(new BigDecimal("99.99"));
product.isActive();

// YANLIŞ
product.name();      // getter mi, alan mı?
product.active();    // boolean mu yoksa void mu?
```

### 9.2 Overloaded Constructor Yerine Static Factory Method

```java
// KORUYUCU YÖNTEM
// Overloaded constructor yerine açıklayıcı static factory method
Complex point = Complex.fromRealNumber(23.0);

// Değil:
Complex point = new Complex(23.0);
```

---

## 10. Şakacı Olmayın (Don't Be Cute)

Mizahî, kültüre özgü veya argo isimler kullanma. Anlamı doğrudan ifade et.

**Kural:** Niyetini açıkça söyle; espri yapma.

```java
// YANLIŞ: Şakacı isimler
void holyHandGrenade() { ... }  // deleteItems() yerine
void whack() { ... }            // kill() yerine
void eatMyShorts() { ... }      // abort() yerine

// DOĞRU
void deleteItems() { ... }
void killProcess() { ... }
void abort() { ... }
```

---

## 11. Kavram Başına Bir Kelime Seç (Pick One Word per Concept)

Aynı soyut kavram için tek ve tutarlı bir kelime kullan. Eşdeğer metotlarda farklı kelimeler kullanmak kafa karıştırır.

**Kural:** Bir sınıfta veri çeken metodun adı `fetch` ise, diğerinde `retrieve` ya da `get` kullanma.

```java
// YANLIŞ: Aynı kavram için 3 farklı kelime
UserController.fetchUser()
ProductController.retrieveProduct()
OrderController.getOrder()

// DOĞRU: Tutarlı kelime
UserController.getUser()
ProductController.getProduct()
OrderController.getOrder()
```

> **Proje notu:** Bu projede tüm controller ve service metotları `get`, `create`, `update`, `delete` ön ekleri ile tutarlı biçimde isimlendirilmiştir. Bu kalıbı koruyun.

---

## 12. Çifte Anlam Kullanma (Don't Pun)

Aynı kelimeyi iki farklı amaç için kullanma. Tek kelime — tek kavram kuralının tersine, bir kelimeyi farklı anlamlarda kullanmak okuyucuyu yanıltır.

**Kural:** Aynı `add` kelimesi hem "listeye ekleme" hem "iki değeri toplama" için kullanılmamalıdır.

```java
// YANLIŞ: addProduct hem listeye ekliyor hem de id toplamına "add" ediyor
void addProduct(Product product) { productList.add(product); }
int addStockValues(int a, int b) { return a + b; }

// DOĞRU: Farklı kavramlar için farklı isimler
void addProduct(Product product) { productList.add(product); }
int sumStockValues(int first, int second) { return first + second; }
```

---

## 13. Çözüm Alanı İsimlerini Kullan (Use Solution Domain Names)

Kodu okuyanlar programcılardır. `AccountVisitor`, `JobQueue` gibi bilgisayar bilimi terimleri, algoritma adları, kalıp adları kullanmak uygundur.

```java
// KABUL EDİLEBİLİR: Programcı terminolojisi
class ProductRepository { ... }     // Repository Pattern
class GlobalExceptionHandler { ... } // Handler Pattern
BlockingQueue<Order> orderQueue;    // CS terimi
```

---

## 14. Problem Alanı İsimlerini Kullan (Use Problem Domain Names)

Programcı terminolojisine karşılık gelen bir isim yoksa, problem alanından (iş kurallarından) alınan ismi kullan. Kodu bakım yapan programcı, alan uzmanına sorabilir.

```java
// KABUL EDİLEBİLİR: İş alanı terminolojisi
String sku;              // Stock Keeping Unit — iş kuralı
BigDecimal unitPrice;    // İş alanından gelen kavram
int reorderThreshold;    // İş alanından gelen eşik değer
```

---

## 15. Anlamlı Bağlam Ekle (Add Meaningful Context)

İsimlerin tek başına anlam taşımadığı durumlarda, onları anlamlı sınıflar, fonksiyonlar veya isim alanları içine yerleştir. Son çare olarak ön ek kullan.

```java
// YANLIŞ: Bağlamı olmayan tekil alanlar
String firstName;
String lastName;
String street;
String city;
String state;
String zipCode;

// DOĞRU: Sınıf bağlam sağlar
class Address {
    String firstName;
    String lastName;
    String street;
    String city;
    String state;
    String zipCode;
}
```

---

## 16. Gereksiz Bağlam Ekleme (Don't Add Gratuitous Context)

Her isime gereksiz ön ek eklemek anlamsızdır. Sınıf adı zaten bağlamı sağlar.

**Kural:** Sınıf adını her alana ön ek olarak ekleme.

```java
// YANLIŞ: Her alana "product" öneki eklenmiş
class Product {
    private String productName;
    private BigDecimal productPrice;
    private int productStock;
    private String productSku;
}

// DOĞRU: Sınıf adı zaten bağlamı sağlar
class Product {
    private String name;
    private BigDecimal price;
    private int stock;
    private String sku;
}
```

> **Proje notu:** Mevcut `Product` entity'si bu kurala uymaktadır. Bu kalıbı koruyun.

---

## 17. Proje Genelinde Uygulama Özeti

Aşağıdaki tablo, bu projedeki katmanlara göre isimlendirme kurallarını özetlemektedir.

| Katman | İsimlendirme Kalıbı | Örnek |
|---|---|---|
| Entity | `<DomainNoun>` | `Product` |
| Repository | `<DomainNoun>Repository` | `ProductRepository` |
| Service (Arayüz) | `<DomainNoun>Service` | `ProductService` |
| Service (Impl) | `<DomainNoun>ServiceImpl` | `ProductServiceImpl` |
| Controller | `<DomainNoun>Controller` | `ProductController` |
| DTO (İstek) | `<DomainNoun>Request` | `ProductRequest` |
| DTO (Yanıt) | `<DomainNoun>Response` | `ProductResponse` |
| Exception | `<Cause><DomainNoun>Exception` | `ProductNotFoundException`, `SkuAlreadyExistsException` |
| Exception Handler | `Global ExceptionHandler` | `GlobalExceptionHandler` |

### Metot İsimleri — Katman Başına Standart Ön Ekler

| İşlem | Service Metodu | Controller Metodu |
|---|---|---|
| Listeleme | `getAll<Nouns>()` | `getAll<Nouns>()` |
| Tekil okuma | `get<Noun>ById(UUID id)` | `get<Noun>ById(UUID id)` |
| Oluşturma | `create<Noun>(Request)` | `create<Noun>(Request)` |
| Güncelleme | `update<Noun>(UUID, Request)` | `update<Noun>(UUID, Request)` |
| Silme | `delete<Noun>(UUID id)` | `delete<Noun>(UUID id)` |

---

## 18. Hızlı Referans — Yapılacaklar ve Yapılmayacaklar

| Yapılacak | Yapılmayacak |
|---|---|
| `elapsedTimeInDays` | `d` |
| `getProductById` | `getById`, `fetchProd` |
| `ProductServiceImpl` | `IProductService`, `ProductServiceClass` |
| `ProductRequest` | `ProductData`, `ProductInfo` |
| `isActive()` | `active()`, `checkActive()` |
| `WORK_DAYS_PER_WEEK` (sabit) | `5` (magic number) |
| `source`, `destination` | `a1`, `a2` |
| `normalizedProductName` | `r`, `tmp`, `val` |