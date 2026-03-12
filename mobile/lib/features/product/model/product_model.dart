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
        id: json['id'] as String?,
        name: json['name'] as String?,
        price: json['price'] == null ? null : (json['price'] as num).toDouble(),
        stock: json['stock'] as int?,
        sku: json['sku'] as String?,
      );
}
