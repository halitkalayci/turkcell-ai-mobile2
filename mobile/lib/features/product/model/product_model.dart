class ProductModel {
  const ProductModel({
    required this.id,
    required this.name,
    required this.description,
    required this.price,
    required this.stock,
    required this.sku,
    required this.categoryId,
  });

  final int id;
  final String name;
  final String description;
  final double price;
  final int stock;
  final String sku;
  final int categoryId;

  factory ProductModel.fromJson(Map<String, dynamic> json) => ProductModel(
        id: json['id'] as int,
        name: json['name'] as String,
        description: json['description'] as String,
        price: (json['price'] as num).toDouble(),
        stock: json['stock'] as int,
        sku: json['sku'] as String,
        categoryId: json['categoryId'] as int,
      );
}
