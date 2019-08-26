package javizz;

public interface TypeModel { // contient attribut name

	// mère de: prédef (enumerate), class, ->interface<-
	// class et interface ont une mère commune -> faire une ReferenceModel (contient attribut package)
	// interface -> faire une InterfaceModel
	// voir où chaque catégorie diffère/se ressemble
	// tableaux ?

	enum PrimitiveType implements TypeModel {
		INT, LONG // ...
		// doesn't need a Link
		// peut-on utiliser des enum ? car pamela
	}

}
