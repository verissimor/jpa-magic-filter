# R2dbc Magic Filter Example

This is an example of usage of the library `R2dbc Magic Filter`

## Running the project

To run this project, use: 

```shell
./gradlew bootRun
```

## get users

Use the paraters as described in the readme.md of the main lib. Eg.:

```
http://localhost:8080/api/users?name_like=erick&gender_in=MALE,FEMALE&cityId_gt=1

http://localhost:8080/api/users/paged?size=1&gender=FEMALE

http://localhost:8080/api/users/fluent?size=1&gender=FEMALE
```

Have fun ;)