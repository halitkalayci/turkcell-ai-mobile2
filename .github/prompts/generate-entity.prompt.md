---
name: generate-entity
description: >
  OpenAPI sözleşme dosyasından veritabanı tablosu olabilecek yapıları çıkarıp
  spring-generate-entity skill kurallarına uygun JPA entity ve repository sınıfları üretir.
agent: "Senior Backend Dev."
argument-hint: "Sözleşme dosya yolu (ör: docs/openapi/customer-v1.yml)"
---

# OpenAPI Sözleşmesinden Entity Üretimi

Sen bir Spring Boot JPA entity üretim uzmanısın. Girdi olarak verilen OpenAPI
sözleşme dosyasını analiz edip veritabanı tablosuna dönüştürülebilecek yapıları
çıkarır ve `spring-generate-entity` skill'ine tam uyumlu entity + repository sınıfları üretirsin.

## Zorunlu Skill

Bu prompt `spring-generate-entity` skill'ini kullanır. Üretim öncesi mutlaka oku:
- Skill dosyası: `.github/skills/spring-generate-entity/SKILL.md`

## Girdi

Kullanıcıdan aldığın argüman bir OpenAPI sözleşme dosya yoludur.
Dosya `docs/openapi/` dizininde bulunur.

## Paket Yolu Otomatik Çıkarımı

Sözleşme dosya adından servis adını otomatik türet:

```
docs/openapi/customer-v1.yml  → com.turkcell.customerservice
docs/openapi/product-v1.yml   → com.turkcell.productservice
docs/openapi/order-v1.yml     → com.turkcell.orderservice
```

**Kural:** Dosya adındaki `-v1.yml` son ekini kaldır, kalan kısmı `service` ile birleştir.

## Adım Adım Akış

### Adım 1 — Sözleşmeyi Oku ve Analiz Et

1. Argüman olarak verilen sözleşme dosyasını **tam olarak oku**.
2. `components.schemas` altındaki tüm şemaları listele.
3. Veritabanı tablosuna karşılık gelen yapıları tespit et.

**Tablo adayı tespit kuralları:**
- `*Response` şemaları birincil tablo adaylarıdır (`ErrorResponse` hariç).
- `id` (uuid) alanı içeren şemalar kesinlikle entity adayıdır.
- `*Request` şemaları **entity değildir**, ancak alan kısıtlamalarını (`required`, `minLength`, `maxLength` vb.) entity'ye taşımak için referans olarak kullanılır.
- Nested `$ref` ile başka bir şemaya bağlanan array alanlar **ilişki** adayıdır.

### Adım 2 — Entity Haritasını Çıkar

Tespit edilen her entity adayı için şu tabloyu oluştur ve kullanıcıya sun:

| OpenAPI Şeması | Entity Adı | Tablo Adı | İlişkiler |
|---|---|---|---|
| `CustomerResponse` | `Customer` | `customers` | — |
| `OrderResponse` | `Order` | `orders` | `OrderItem` (OneToMany) |
| `OrderItemResponse` | `OrderItem` | `order_items` | `Order` (ManyToOne) |

### Adım 3 — Tip Eşleme Tablosu

Sözleşmedeki OpenAPI tiplerini Java tiplerine şu kurallara göre dönüştür:

| OpenAPI Tipi | Format | Java Tipi |
|---|---|---|
| `string` | — | `String` |
| `string` | `uuid` | `UUID` |
| `string` | `date-time` | `LocalDateTime` |
| `string` | `email` | `String` |
| `integer` | — | `int` |
| `integer` | `int64` | `long` |
| `number` | — | `double` |
| `number` | `decimal` | `BigDecimal` |
| `boolean` | — | `boolean` |
| `array` (with `$ref`) | — | `List<T>` (ilişki) |

### Adım 4 — Alan Kısıtlamalarını Belirle

Her alan için `*Request` şemasındaki validasyon bilgilerini entity `@Column` tanımına taşı:

| OpenAPI Kısıtlama | JPA Karşılığı |
|---|---|
| `required` listesinde | `nullable = false` |
| `maxLength: N` | `length = N` |
| `format: uuid` (farklı entity ref) | `@ManyToOne` veya `UUID` alanı |
| `unique` ipucu (409 Conflict response varsa) | `unique = true` |
| `number / format: decimal` | `@Column(precision = 10, scale = 2)` |

**Unique alan tespiti:** Sözleşmede `409 Conflict` response tanımını kontrol et. Conflict açıklamasında hangi alanın çakıştığı belirtilir (ör: "E-posta çakışması"), bu alan `unique = true` olarak işaretlenir.

### Adım 5 — Dosya Dökümü Sun ve Onay Al

Üretilecek entity ve repository dosyalarının dökümünü kullanıcıya sun:

```
| Dosya | İşlem | Neden |
|---|---|---|
| `com/turkcell/customerservice/entity/Customer.java` | Oluşturulacak | CustomerResponse şemasından |
| `com/turkcell/customerservice/repository/CustomerRepository.java` | Oluşturulacak | Customer entity için JPA repository |
```

**AGENTS.MD kuralı:** Onay almadan implementasyona başlama.

### Adım 6 — Entity Üretimi

Onay sonrası her entity için şu kuralları uygula (spring-generate-entity skill):

1. **Primary Key:** `UUID` tipinde, `@GeneratedValue(strategy = GenerationType.UUID)`
2. **Anotasyonlar:** `@Entity`, `@Table(name = "çoğul_snake_case")`
3. **İmport:** Sadece `jakarta.persistence.*` (Spring Boot 3+)
4. **Lombok YASAK:** Getter/setter manuel yazılacak
5. **id setter yok:** Sadece getter
6. **Audit alanları zorunlu:** `createdAt`, `updatedAt` + `@PrePersist`, `@PreUpdate`
7. **Constructor:** `protected` no-arg (JPA) + parametreli constructor
8. **İlişkiler:** `@ManyToOne(fetch = FetchType.LAZY)`, `@OneToMany(mappedBy = ...)`
9. **Sözleşmedeki `createdAt`/`updatedAt`:** Response'ta varsa entity audit alanlarına karşılık gelir, ayrı alan olarak tekrar eklenmez.
10. **Grup kuralı:** Maksimum 5 dosyalık gruplar halinde üret, her grup arası onay iste.

### Adım 7 — Repository Üretimi

Her entity için eşlik eden bir JPA repository interface üret:

```java
package com.turkcell.<service>.repository;

import com.turkcell.<service>.entity.<Entity>;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface <Entity>Repository extends JpaRepository<<Entity>, UUID> {
}
```

**Ek query metotları:** Eğer entity'de `unique = true` olan bir alan varsa, o alan için finder metodu ekle:

```java
Optional<<Entity>> findByEmail(String email);
```

### Adım 8 — Doğrulama Kontrol Listesi

Üretim tamamlandıktan sonra her entity için kontrol et:

- [ ] Primary key UUID + `GenerationType.UUID`?
- [ ] `@Entity` ve `@Table(name = "...")` mevcut?
- [ ] Tablo adı çoğul ve snake_case?
- [ ] Zorunlu alanlar `nullable = false`?
- [ ] Karakter alanlarına `length` kısıtlaması eklenmiş?
- [ ] `createdAt` + `updatedAt` audit alanları var?
- [ ] Lombok kullanılmamış?
- [ ] Getter/setter manuel?
- [ ] No-arg constructor mevcut?
- [ ] Tüm importlar `jakarta.persistence.*`?
- [ ] Sözleşmedeki tüm `*Response` alanları entity'de karşılık buluyor?
- [ ] Repository interface `JpaRepository<Entity, UUID>` extend ediyor?
- [ ] Unique alanlara karşılık finder metodu repository'de var?

## Çıktı Formatı

Her entity üretimi sonrası şunları sun:
1. **Happy-Path Test:** Entity'nin persist/read senaryosu
2. **Dosya dökümü:** Ne eklendi/değişti, neden

## Önemli Kısıtlamalar

- Sözleşmede **olmayan** alan entity'ye eklenemez (audit alanları hariç).
- `ErrorResponse` şeması entity'ye dönüştürülmez.
- `*Request` şeması entity değildir, sadece kısıtlama kaynağıdır.
- Paket yapısı: `com.turkcell.<service-name>.entity` ve `com.turkcell.<service-name>.repository`

## Örnek Kullanım

```
Kullanıcı: /generate-entity docs/openapi/customer-v1.yml

Agent: customer-v1.yml sözleşmesini okudum. Tespit edilen entity adayları:

| OpenAPI Şeması | Entity Adı | Tablo Adı | İlişkiler |
|---|---|---|---|
| CustomerResponse | Customer | customers | — |

Unique alan tespiti: 409 Conflict → "E-posta çakışması" → email alanı unique

Dosya dökümü:
| Dosya | İşlem | Neden |
|---|---|---|
| `Customer.java` | Oluşturulacak | CustomerResponse şemasından entity |
| `CustomerRepository.java` | Oluşturulacak | Customer JPA repository + findByEmail |

Devam edeyim mi?
```

```
Kullanıcı: /generate-entity docs/openapi/order-v1.yml

Agent: order-v1.yml sözleşmesini okudum. Tespit edilen entity adayları:

| OpenAPI Şeması | Entity Adı | Tablo Adı | İlişkiler |
|---|---|---|---|
| OrderResponse | Order | orders | OrderItem (OneToMany) |
| OrderItemResponse | OrderItem | order_items | Order (ManyToOne) |

Dosya dökümü:
| Dosya | İşlem | Neden |
|---|---|---|
| `Order.java` | Oluşturulacak | OrderResponse şemasından entity |
| `OrderItem.java` | Oluşturulacak | OrderItemResponse şemasından entity |
| `OrderRepository.java` | Oluşturulacak | Order JPA repository |
| `OrderItemRepository.java` | Oluşturulacak | OrderItem JPA repository |

4 dosya tek grupta üretilebilir (≤5 limit). Devam edeyim mi?
```
