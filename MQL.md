

# Introduction #

This page describes the MQL language.  For information on how to use MQL see the MJORM home page, api documentation and\or `Statement` interface.

# Table of contents #


# MJORM Query Language #
MJORM Query Language (or MQL - pronounced "Michael") allows for SQL (Structured Query Language) like querying of a MongoDB database.  An MQL query can contain many commands separated by a semi colon.

## Command ##
An MQL command has the following structure:

```
FROM
   collection_name
   [WHERE 
     condition [, condition ...]]
   action
```

  * _collection\_name_ is the name of the collection that is being operated on.
  * _condition_ is a query condition that must be met
  * _action_ is the action to perform on the documents in the collection specified that match the specified conditions (or all documents if no conditions are specified)

### Variable literals ###
Variable literals used in MQL come in the following flavors:
| **type** | **example** |
|:---------|:------------|
| parameter | :parameterName, ? |
| regular expression | /expression/ |
| string | 'a value', "double quoted as well" |
| boolean | true, false |
| integer | 1, -1, 200 |
| decimal | 1.1, -2.3, 234.64 |
| hex number | 0xfe01, 0xA2 |
| array | [1, 2, 3], ['hello', 'world'], [[1,2], ['multi', 'dimensional']] |
| function | date('2012-01-01 00:00:01'), now() |

#### functions ####
Functions that provide values as variable literals are easily added to MQL.  By default the following functions are available:

| **name** | **arguments** | **return** | **description**|
|:---------|:--------------|:-----------|:|
| now() | none | Date | returns the current date and time |
| date(...) | long or string | Date | Returns the date and time represented by the value passed to it |
| object\_id(...) | string | ObjectId | converts the given string into an ObjectId |

### Conditions ###
Conditions are optionally separated by a comma.  There are 3 types of query conditions: field comparison, field function and document function.  Because MQL queries are essentially converted to a `DBObject` query they still suffer from the same limitations as the traditional query approach for fields in that a field may be only used once for comparisons, but may sometimes be used multiple times for functions.  This limitation may be lifted at some point if MJORM uses a lower level BSON protocol for querying MongoDB (in BSON, it's perfectly acceptable to use the same field on an object twice - but because the 10gen library's `DBObject` uses a `Map` this is impossible without trickery and violation of the `Map` interface's contract).

### field comparison conditions ###
MQL supports the following field comparison operators:
| **operator** | **name** |
|:-------------|:---------|
| > | greater than |
| >= | greater than or equal |
| < | less than |
| <= | less than or equal |
| != | not equal |
| <> | not equal |
| =~ | regular expression matching |

```
FROM
   collection_name
   WHERE
       age > 10
       height >= 12
       weight < 150
       length <= 25
       name != "Jalopy"
       size <> 15
       description =~ /^Distance: [0-9]+/
   action
```

### field function conditions ###
Field function conditions generally perform some sort of operation on a field not possible with a simple comparision.  MQL supports the following field function conditions:
| **name** | **description** |
|:---------|:----------------|
| exists | checks for the presence of the field |
| not\_exists | checks for the absence of a field |
| between | checks to see if a field is between a specific range |
| elemMatch | performs an [$elemMatch](http://www.mongodb.org/display/DOCS/Advanced+Queries) |
| mod | performs an [$mod](http://www.mongodb.org/display/DOCS/Advanced+Queries) |
| size | checks the size of the field |
| type | checks the [type](http://www.mongodb.org/display/DOCS/Advanced+Queries) of the field |
| in | checks that the field contains one of the values specified |
| nin | checks that the field doesn't contain any of the values specified |
| all | checks that the field contains all of the values specified |
| near | performs a geospacial near query |
| within\_box | performs a geospacial within query for the given box |
| within\_circle | performs a geospacial within query for the given circle |
| within\_polygon | performs a geospacial within query for the given polygon |

```
FROM
   collection_name
   WHERE
       age exists()
       height not_exists()
       weight between(150, 175)
       author elemMatch(firstName='john', lastName='doe')
       name mod(1, 2)
       tags size(10)
       whatever type(10)
       names in('john', 'jacob', 'jingleheimer')
       somethingElse nin('a value', 10)
       otherTags all('fun', 'intersting')
       location near(x, y, distance)
       location within_box(x, y, xx, yy)
       location within_circle(x, y, radius)
       location within_polygon([x, y], [x, y], [x, y], [x, y], ...)
       
   action
```

### field condition negation ###
field function conditions and field comparison conditions can be negated with the `NOT` keyword.

```
FROM
   collection_name
   WHERE
       not age > 10
       not name =~ /something/
   action
```

### document function conditions ###
Document functions define conditions that are used on the document as a whole or contain field conditions.  The following document functions are available:
| **name** | **description** |
|:---------|:----------------|
| or | performs an [$or](http://www.mongodb.org/display/DOCS/Advanced+Queries) |
| nor | performs a [$nor](http://www.mongodb.org/display/DOCS/Advanced+Queries) |
| and | performs an [$and](http://www.mongodb.org/display/DOCS/Advanced+Queries) |
| predicate | enables the use of a javascript for a [$where](http://www.mongodb.org/display/DOCS/Advanced+Queries) statement |

```
FROM
   collection_name
   WHERE
       or(x=1, y=2, name='jets')
       nor(a=1, b=3, name=~ /whatever/)
       and(h=1, i=2, name in(10, 1, 3))
       predicate('this.x > 2 && this.y * 2 == 14');
   action
```

## Actions ##
An MQL query is not complete without an action.  The following actions are supported:
| **action** | description |
|:-----------|:------------|
| select | query for documents |
| explain | return a document with a query execution plan |
| delete | delete documents |
| update | update documents |
| find and modify | modify documents and return them |
| find and delete | delete objects and return them |

### explain ###
Explain is the most simple action and has the following structure:

```
FROM
   collection_name
   [WHERE 
     condition [, condition ...]]
   EXPLAIN
   [hint]
```

An explain query merely returns a `DBObject` containing the query execution plan as defined by the database.  Hints are used to give the database hints as to which indexes to use and are defined in greater detail in the Common Constructs section of this document.

example:
```
FROM
   collection_name
   WHERE size > 10
   EXPLAIN
```

### select ###
Select is used to return documents from the database and has the following structure:

```
FROM
   collection_name
   [WHERE 
     condition [, condition ...]]
   SELECT { * | field_name [, field_name ...] }
   [hint]
   [sort]
   [limit]
```

example:
```
FROM
   collection_name
   WHERE
        size > 10
        name = 'duder'
   SELECT name, size, dateCreated
   HINT dateCreated
   SORT dateCreated ASC
   LIMIT 15, 100
```

or at a bare minumum:
```
FROM collection_name SELECT *
```

### delete ###
The delete action deletes the documents matching the condition portion of the query from the given collection name and has the following structure:

```
FROM
   collection_name
   [WHERE 
     condition [, condition ...]]
   [ATOMIC] DELETE
```

example:
```
FROM
    collection_name
    WHERE
       age > 20
       name =~ /.*Smith/
    DELETE
```

Same thing as above, but atomic:
```
FROM
    collection_name
    WHERE
       age > 20
       name =~ /.*Smith/
    ATOMIC DELETE
```

### update ###
The update action modifies the documents matching the condition portion of the query in the given collection name and has the following structure:

```
FROM
   collection_name
   [WHERE 
     condition [, condition ...]]
   [ATOMIC] UPDATE [MULTI] [update_operation_list]
```

The update\_operation\_list is defined in the Common constructs section of this document.

example:
```
FROM
    collection_name
    WHERE age=1
    UPDATE MULTI
      set newBorn=false
      set type='toddler'
      unset baby
```

updates can also be atomic:
```
FROM
    collection_name
    WHERE age=1
    ATOMIC UPDATE MULTI
      set newBorn=false
      set type='toddler'
      unset baby
```

they can also be performed against only the first matching document (removing MULTI):
```
FROM
    collection_name
    WHERE age=1
    UPDATE
      set newBorn=false
      set type='toddler'
      unset baby
```

### upsert ###
The upsert operation is very similar to update except that it operates on a single document.  It creates the document (using the query criteria) if it doesn't already exist and has the following structure:

```
FROM
   collection_name
   [WHERE 
     condition [, condition ...]]
   [ATOMIC] UPSERT [update_operation_list]
```

for example:
```
FROM
    collection_name
    WHERE age=1
    UPSERT
      set newBorn=false
      set type='toddler'
      unset baby
```

and:
```
FROM
    collection_name
    WHERE age=1
    ATOMIC UPSERT
      set newBorn=false
      set type='toddler'
      unset baby
```

### find and modify ###
Find and modify is used to atomically modify a document and return it and has the following structure:

```
FROM
   collection_name
   [WHERE 
     condition [, condition ...]]
   [UPSERT] FIND AND MODIFY RETURN {NEW | OLD}
   [update_operation_list]
   SELECT { * | field_name [, field_name ...] }
   [sort]
```

For example:
```
FROM
    collection_name
    WHERE name='iPhone Review'
    FIND AND MODIFY RETURN NEW
    INC viewCount 1
    SELECT *
    SORT rating DESC
```

Or to return the object before modification:
```
FROM
    collection_name
    WHERE name='iPhone Review'
    FIND AND MODIFY RETURN OLD
    INC viewCount 1
    SELECT *
    SORT rating DESC
```

find and modify also supports upsert:
```
FROM
    collection_name
    WHERE name='iPhone Review'
    UPSERT FIND AND MODIFY RETURN OLD
    INC viewCount 1
    SELECT *
    SORT rating DESC
```


### find and delete ###
Find and delete is similar to find and modify except that it deletes the document rather than modifying it and always returns the "old" document (since there isn't a new one).  It has the following structure:

```
FROM
   collection_name
   [WHERE 
     condition [, condition ...]]
   FIND AND DELETE
   SELECT { * | field_name [, field_name ...] }
   [sort]
```

for example:
```
FROM
    collection_name
    WHERE name='iPhone Review'
    FIND AND DELETE
    SELECT *
    SORT rating ASC
```


## Common constructs ##
Some query constructs are used across many actions.

### update operations ###
Update operations are used with actions that modify an existing (or new in the case of upsert) document or documents.  Below is a list of update operations:

| **name** | **example** | **description** |
|:---------|:------------|:----------------|
| [inc](http://www.mongodb.org/display/DOCS/Updating#Updating-%24inc) | inc fieldName 1 | Increments the given field name by the given number, negative numbers decrement |
| [set](http://www.mongodb.org/display/DOCS/Updating#Updating-%24set) | set fieldName 'newValue' | Gives the field a new value |
| [unset](http://www.mongodb.org/display/DOCS/Updating#Updating-%24unset) | unset fieldName | Unsets (removes) the given field |
| [push](http://www.mongodb.org/display/DOCS/Updating#Updating-%24push) | push fieldName 'underwear' | Adds a value to a given field |
| [push all](http://www.mongodb.org/display/DOCS/Updating#Updating-%24pushAll) | push all fieldName ['val1', 2, 'val3'] | Adds values to a given field |
| [add to set](http://www.mongodb.org/display/DOCS/Updating#Updating-%24addToSetand%24each) | add to set fieldName 'value' | Adds a value to a given field if the value doesn't already exist |
| [add to set each](http://www.mongodb.org/display/DOCS/Updating#Updating-%24addToSetand%24each) | add to set fieldName each ['val', 'another', 3] | Adds each value to the given field if they don't already exist |
| [pop](http://www.mongodb.org/display/DOCS/Updating#Updating-%24pop) | pop fieldName | Removes the last element in an array field |
| [shift](http://www.mongodb.org/display/DOCS/Updating#Updating-%24pop) | shift fieldName | Removes the first element in an array field |
| [pull](http://www.mongodb.org/display/DOCS/Updating#Updating-%24pull) | pull fieldName 'aValue' | Removes all occurrences of the value from the given array field |
| [pull all](http://www.mongodb.org/display/DOCS/Updating#Updating-%24pullAll) | pull all fieldName ['a val', 'another', 10] | Removes each occurrence of the values from the given array field |
| [rename](http://www.mongodb.org/display/DOCS/Updating#Updating-%24rename) | rename fieldName newName | Renames an existing field |
| [bitwise or](http://www.mongodb.org/display/DOCS/Updating#Updating-%24bit) | bitwise or fieldName 210 | Applys a bitwise OR to the given field name using the given integer |
| [bitwise and](http://www.mongodb.org/display/DOCS/Updating#Updating-%24bit) | bitwise and fieldName 210 | Applys a bitwise AND to the given field name using the given integer |

### hints ###
Hints are used to help the database determine the best index to use when querying and have the following structure:

```
HINT {NATURAL [ASC | DESC] | index_name [ASC | DESC] | field [ASC | DESC] [, field [ASC | DESC]...]}] 
```

natural:
```
FROM
   collection_name
   WHERE x=1
   SELECT *
   HINT NATURAL
```

by name:
```
FROM
   collection_name
   WHERE x=1
   SELECT *
   HINT 'name_1_dateUpdated_1'
```

by field(s):
```
FROM
   collection_name
   WHERE x=1
   SELECT *
   HINT name ASC, dateUpdated DESC
```

### sorting ###
Sorting changes the order in which documents are returned (or modified depending on the action)

```
SORT field_name [, field_name ...]
```

```
FROM
   collection_name
   WHERE x=1
   SELECT *
   SORT name ASC, age DESC
```

### pagination\limit ###
Pagination limits the number of documents returned or affected by a query and has the following structure:

```
LIMIT {max | start, max}
```

Note: start and max may be integers or parameters.

first 10:
```
FROM
   collection_name
   WHERE x=1
   SELECT *
   LIMIT 10
```

document 10 through 25:
```
FROM
   collection_name
   WHERE x=1
   SELECT *
   LIMIT 10, 15
```