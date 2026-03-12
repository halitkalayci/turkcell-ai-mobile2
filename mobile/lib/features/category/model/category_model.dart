class CategoryModel {
  const CategoryModel({
    required this.id,
    required this.name,
    required this.description,
  });

  final int id;
  final String name;
  final String description;

  factory CategoryModel.fromJson(Map<String, dynamic> json) => CategoryModel(
        id: json['id'] as int,
        name: json['name'] as String,
        description: json['description'] as String,
      );
}
