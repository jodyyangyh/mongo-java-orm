# MJORM (mongo-java-orm) - A MongoDB Java ORM #

This project aims to provide a robust query api and ORM for MongoDB and the Java programming language.  The goals of this project are:

  * Enable Object Relational Mapping between the MongoDB driver's `DBObject`s and POJOs
  * Make it easier to write testable MongoDB data access code
  * Provide a higher level MongoDB API for common tasks
  * Easily integrate with the Spring Framework
  * Provide an XML based ORM mapping
  * Provide an annotations based ORM mapping

## Features include ##

  * XML and annotation based mapping configuration for POJOs
  * Automatic `DBObject` to POJO (and vica verca) conversion
  * XML and JavaScript based Map\Reduce configuration and helper classes
  * High level query API via the `MongoDao`
  * Lower level criteria query API via `DaoQuery` and `DaoModifier`
  * [MQL](MQL.md) (MJORM query language. aka "Michael") a DSL for querying mongo similar to SQL
  * Easy integration with the Spring Framework

## Download ##
| **Name** | **Link** |
|:---------|:---------|
| Stable | [mongo-java-orm-1.2](http://mongo-java-orm.googlecode.com/svn/maven/repo/com/googlecode/mongo-java-orm/1.2/) |
| Development | [mongo-java-orm-1.3-SNAPSHOT](http://mongo-java-orm.googlecode.com/svn/maven/repo/com/googlecode/mongo-java-orm/1.3-SNAPSHOT/) |
| Older | [older](http://mongo-java-orm.googlecode.com/svn/maven/repo/com/googlecode/mongo-java-orm/) |





# Maven #
This project is built with [Maven](http://maven.apache.org). Be sure to check the pom.xml for the dependencies if you're not using maven.  Add the following to your pom.xml if you're using maven:

In `<repositories>`:
```
    <repository>
        <id>mjorm-webdav-maven-repo</id>
        <name>mjorm maven repository</name>
        <url>http://mongo-java-orm.googlecode.com/svn/maven/repo/</url>
        <layout>default</layout>
    </repository>
```

In `<dependencies>`:
```
    <dependency>
        <groupId>com.googlecode</groupId>
        <artifactId>mongo-java-orm</artifactId>
        <version>1.2</version>
    </dependency>
```

# Mapping #

MJORM supports XML and\or annotations to describe how POJOs are mapped to documents within MongoDB.  Follows is an example of how one might model a `Person` and their `Address`.

Java:
```
package com.googlecode.mjorm;

public class Person {
	private String id;
	private String firstName;
	private String lastName;
	private Address address;
...
}

public class Address {
	private String street;
	private String city;
	private String state;
	private String zipCode;
...
}

```

## XML Mapping ##
The following XML is an example of how the above objects would be mapped using MJORM:
```
<?xml version="1.2" encoding="UTF-8"?>
<descriptors>

	<object class="com.googlecode.mjorm.Address">
		<property name="id" id="true" auto="true" />
		<property name="firstName" />
		<property name="lastName" />
		<property name="address" />
	</object>

	<object class="com.googlecode.mjorm.Address">
		<property name="street" />
		<property name="city" />
		<property name="state" />
		<property name="zipCode" />
	</object>

</descriptors>
```

For the purpose of this example all of the mappings were defined in a single file, but it is perfectly reasonable to split them out into their own files.


### Usage ###
To use the above defined mapping files, one must use the `XmlDescriptorObjectMapper`.  The `XmlDescriptorObjectMapper` is an implementation of the `ObjectMapper` interface that uses XML documents described above to tell mjorm how to map between java POJOs and MongoDB's `DBObject`s.  Follows is an example on how to use it:

```
// connect to mongo
Mongo mongo = new Mongo(new MongoURI(uri)); // 10gen driver

// create object mapper and add mapping files
XmlDescriptorObjectMapper objectMapper = new XmlDescriptorObjectMapper();
mapper.addXmlObjectDescriptor(new File("/path/to/mappings.xml"));

// create MongoDao
MongoDao dao = new MongoDaoImpl(mongo.getDB("dbName"), objectMapper);
```

An XML mapping file starts with `<descriptors>` and contains a number of `<object>` tags that define the association between the mongo `DBObject`s and POJOs.

### `<descriptors>` ###
`<descriptors>` is the root element of a mjorm mapping file.

### `<object>` ###
The `<object>` tag must exist beneath the root `<descriptors>` tag and supports the following attributes:
  * `class` - (required) the POJO class name
  * `discriminator-name` - (optional) the descriminator name, only used for subclass mapping
  * `discriminator-type` - (optional) the descriminator type, defaults to string

### `<subclass>` ###
The `<subclass>` tag exists beneath an `<object>` tag for an object with mapped subclasses and supports the following attributes:
  * `class` - (required) the POJO sub class name
  * `discriminator-value` - (required) the POJO class name

### `<property>` ###
The `<property>` tag must exist beneath an `<object>` tag and supports the following attributes:
  * `name` - (required) the name of the property on the POJO
  * `id` - Specifies that this property is the object ID, an object may only contain one ID and can be of any type.
  * `auto` - this can only be used with `id` and can be specified to indicate that mjorm should auto-generate IDs
  * `class` - the type of the property (this can be set to do things like using a TreeSet for Set)
  * `column` - specifies the "column" or (property) name on the MongoDB object that the property is mapped to.  If this is omitted then `name` is used.

### `<type-param>` ###
`<type-param>` can be specified inside of a `<property>` tag and can be used to specify the generic type of object to create for collections and Maps.  It supports the following attributes:
  * `class` - (required) the class name of the type

### `<conversion-hints>` ###
`<conversion-hints>` can be specified inside of a `<property>` tag and can be used to specify conversion hints to the type conversion system.

### `<hint>` ###
`<hint>` is used within `<conversion-hints>` to specify conversion hints and has the following attributes:
  * `name` - (required) the name of the hint
The contents of the tag is used as the value of the hint

## Annotations Mapping ##

Follows is an example of the same sample objects above mapped with Annotations:

```
package com.googlecode.mjorm;

@Entity
public class Person {
	private String id;
	private String firstName;
	private String lastName;
	private Address address;

        @Id
        @Property
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

        @Property
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }

        @Property
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

        @Property
	public Address getAddress() { return address; }
	public void setAddress(Address address) { this.address = address; }
	
}

@Entity
public class Address {
	private String street;
	private String city;
	private String state;
	private String zipCode;

        @Property
	public String getStreet() { return street; }
	public void setStreet(String street) { this.street = street; }

        @Property
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }

        @Property
	public String getState() { return state; }
	public void setState(String state) { this.state = state; }

        @Property
	public String getZipCode() { return zipCode; }
	public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}

```

### Usage ###
The `AnnotationsDescriptorObjectMapper` is an implementation of the `ObjectMapper` interface that uses annotations as described above to tell mjorm how to map between java POJOs and MongoDB's `DBObject`s.  Follows is an example on how to use it:

```
// connect to mongo
Mongo mongo = new Mongo(new MongoURI(uri)); // 10gen driver

// create object mapper and add classes
AnnotationsDescriptorObjectMapper objectMapper = new AnnotationsDescriptorObjectMapper();
mapper.addClass(Person.class);
mapper.addClass(Address.class);

// create MongoDao
MongoDao dao = new MongoDaoImpl(mongo.getDB("dbName"), objectMapper);
```

An annotated class is annotated with `@Entity` at the class level and contains a number of getter methods (with appropriate setter methods) annotated with the `@Property` (and potentially a single `@Id` annotation).

### `@com.googlecode.mjorm.annotations.Entity` ###
The `@Entity` annotation must exist at the class level and supports the following attributes, all of which are optional:
  * `discriminatorName` - specifies the name of a property that is used as a descriminator for using with `@SubClass`
  * `discriminatorType` - the type of descriminator (defaults to string)
  * `subClasses` - an array of {{{@SubClass}} defining all of the mapped subclasses.

### `@com.googlecode.mjorm.annotations.SubClass` ###
  * `entityClass` - the class (must also be annotated) that is a subclass
  * `discriminiatorValue` - the value of the descriminator (defined on `@Entity` that tells mjorm when to use this subclass

### `@com.googlecode.mjorm.annotations.Id` ###
The `@Id` annotation must be accompanied by a `@Property` annotation and
is applied to methods.  Currently id fields must be Strings.  It has the following attributes, all optional:
  * `autoGenerated` - whether or not the ID value should be auto generated.

### `@com.googlecode.mjorm.annotations.Property` ###
The `@Property` annotation annotates methods and supports the following attributes, all of which are optional:
  * `type` - the type of the property (this can be set to do things like using a TreeSet for Set)
  * `field` - specifies the "column" or (property) name on the MongoDB object that the property is mapped to.  If this is omitted then the bean property name is used.
  * `genericParameterTypes` - an array of `Class` that define the generic parameter types for this property
  * `typeConversionHints` - an aray of `@TypeConversionHint`s, described below
  * `valueGeneratorClass` - a class that can generate a value for the property if it's empty

### `@com.googlecode.mjorm.annotations.TypeConversionHint` ###
Used by the ObjectMapper during object mapping
  * `name` - (required) the name of the translation hint
  * `stringValue` - (required) the value of the translation hint

# Java API #
While all of the APIs support the mapping of POJOs to and from mongos `DBObject`s the API also provides identical methods that provide direct access to mongos `DBCursor`, `WriteResult` and `DBObject` objects.  This allows for the use MJORM's powerful query APIS without being required to use it's data mapping functionality.

## `MongoDao` ##
The `MongoDao` interface (`MongoDaoImpl default implementation`) is at the core of the MJORM api.  It exposes a high level set of query methds and allows access to the criteria API and MQL functionality outlined below.  The `MongoDaoImpl` is created with a MongoDB `DB` object (from the 10gen driver) and an `ObjectMapper` as outlined above.  It provides a number of CRUD (create\count, read, update, delete) as well as utilities for executing MongoDB commands, Map\Reduce, creating indexes, find and modify and find and delete.

## Criteria API ##
The criteria API offered by MJORM provides a clean fluid way of querying a MongoDB database in the java programming language.  The criteria API starts by using the `DaoQuery` object.  The `DaoQuery` object can be instantiated manually and the configured through it's `setDB(...)` and `setObjectMapper(...)` methods or a preconfigured instance can be obtained by the `createQuery()` method available on the `MongoDao` object.

```
// configured manually
DaoQuery query = new DaoQuery();
query.setDB(db);
query.setObjectMapper(objectMapper);

// configured automatically by the MongoDao
MongoDao mongoDao = ...;
DaoQuery query = mongoDao.createQuery();

query.eq("firstName", "Bruce")
query.eq("lastName", "Banner")
query.in("aliases", "Hulk", "The Hulk", "His Hulkness");

List<Person> people = query.findObjects(Person.class); // expecting multiple
Person person = query.findObject(Person.class); // expecting one result
```

Most of the criteria and setter methods exposed by the `DaoQuery` object return the `DaoQuery` itself enabling chaining, for instance:

```
List<Book> books = mongoDao.createQuery()
    .all("tags", "outdoor", "hiking", "fishing")
    .gte("rating", 10)
    .findObjects(Book.class);
```

Modifications can also be performed with the criteria API by calling the `modify()` method on the `DaoQuery` object which returns a `DaoModifier` object that exposes many MongoDB modifiers as well as update, delete, find and modify and find and delete operations:

```

// put all of the low rated science fiction
// books on sale
mongoDao.createQuery()
    .lte("rating", 2)
    .eq("genre", GenreEnum.SCIENCE_FICTION)
    .modify()
        .inc("price", -1.50)
        .set("onSale", true)
        .push("tags", "sale")
        .update();

```

The criteria API provides a wide varity of criteria types that support all of MongoDB's query functionality.  In addition, custom `Criterion` can be created and used the with criteria API.  Anyone interested in the criteria API should spend some time investigating the various methods available on the `DaoQuery`, `Criteria`, `DaoModifier` and `Modifiers` classes as well as the classes available in the `com.googlecode.mjorm.query.criteria` and `com.googlecode.mjorm.query.modifiers` packages.

## MQL (MJORM Query Language) ##
[MQL](MQL.md) provides a SQL like language for querying MongoDB.  It's useful and sometimes a lot more natural to query MongoDB with MQL vs. creating large `DBObject` object graphs as is the norm with MongoDB.  the MQL interpreter uses the criteria API internally so all features supported by the criteria API are also supported by MQL.

At the core of the MQL API is the `Statement` interface (`StatementImpl default implementation`).  It is very similar to the java `PreparedStatement`.  A `Statement` is created per MQL query (or queries) and it is compiled and kept in memory, supports parameters and can be re-used much like `PreparedStatement`.  It differs from the `PreparedStatement` in that it is not compiled and kept in memory on the server, instead it is compiled and kept in memory within the JVM.  Like the criteria API MQL can be used with or without the `MongoDao` object.  For instance:

```
String query = "from books where tags in('hiking', 'fishing') select *";

// configured manually
Statement st = new StatementImpl(new ByteArrayInputStream(query.getBytes()), db, objectMapper);

// or via the MongoDao
Statement st = mongoDao.createStatement(query);

```

After instantiation it can then be used (and re-used) to execute the query and just like the criteria API and `MongoDao` interface it provides methods for returning `DBCursor`, `DBObject`, `WriteResult` and matching methods that do data mapping of POJOs:

```
Statement st = mongoDao.createStatement("from books where tags in(:tags) select *");

st.setParameter("tags", new Object[] { "hiking", "fishing" });
DBCursor cursor = st.execute(); // 10gen DBCursor
ObjectIterator<Book> itr = st.execute(Book.class); // mjorm ObjectIterator
```

parameters may also be indexed:
```
Statement st = mongoDao.createStatement("from books where name=? select *");
st.setParameter(0, "The Hitchhiker's Guide to the Galaxy");
```

The following types of queries are supported by MQL:

  * select
  * explain
  * delete
  * update
  * upsert
  * find and modify
  * find and delete

More details about the MQL language can be found on the [MQL](MQL.md) page.

# Use with The Spring Framework #

Mjorm can be configured for and used with the SpringFramework.  Follows is an example configuration:

```

<?xml version="1.2" encoding="UTF-8"?>
<beans>

	<!-- Mongo object -->
	<bean id="mongo" class="com.googlecode.mjorm.spring.MongoFactoryBean">
		<property name="uri" value="mongodb://localhost/dbName" />
		<property name="closeOnDestroy" value="true" />
	</bean>

	<!-- XML ObjectMapper -->
	<bean id="mongoObjectMapper" class="com.googlecode.mjorm.spring.XmlDescriptorObjectMapperFactoryBean">
		<property name="xmlResources">
			<list>
				<value>classpath:/com/company/City.mongo.xml</value>
				<value>classpath:/com/company/Address.mongo.xml</value>
			</list>
		</property>
	</bean>

        OR:

	<!-- Annotations ObjectMapper -->
	<bean id="mongoObjectMapper" class="com.googlecode.mjorm.spring.AnnotationsDescriptorObjectMapperFactoryBean">
		<property name="annotatedClasses">
			<list>
				<value>com.googlecode.mjorm.Person</value>
				<value>com.googlecode.mjorm.Address</value>
			</list>
		</property>
	</bean>

         DAOs:

	<!-- City DAO -->
	<bean id="cityDao" class="com.company.MongoDBCityDao">
		<property name="mongo" 		ref="mongo" />
		<property name="objectMapper" 	ref="mongoObjectMapper" />
		<property name="dbName" 	value="dbName" />
	</bean>


</beans>

```

Where MongoDBCityDao looks like this:

```
import com.googlecode.mjorm.spring.MongoDBDaoSupport;

class MongoDBCityDao
	extends MongoDBDaoSupport {
 	.. data access methods here, see the MongoDBDaoSupport class for more
}

```
