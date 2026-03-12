class CategoryModel {
  const CategoryModel({
    this.id,
    this.name,
    this.description,
    this.createdAt,
    this.updatedAt,
  });

  final String? id;
  final String? name;
  final String? description;
  final String? createdAt;
  final String? updatedAt;

  factory CategoryModel.fromJson(Map<String, dynamic> json) => CategoryModel(
        id: json['id'] as String?,
        name: json['name'] as String?,
        description: json['description'] as String?,
        createdAt: json['createdAt'] as String?,
        updatedAt: json['updatedAt'] as String?,
      );
}
