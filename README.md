# Limes
```
Latin
līmes m (genitive līmitis); third declension

limit, border, path.
```

The main purpose of this application is to be an integration-point/proxy for miles.no & conf.miles.no for services like cv partner.
As such the focus is on making a proxy that is easy to consume for these clients.

This project uses Quarkus: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```
> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

this is to a large extend a standard quarkus application, as such the official documentation is a great guide.
it is deployed with heroku as a jar.

## Packaging and running the application
The application can be packaged using:
```shell script
./mvnw package
```

### Creating a native executable

The application is not deployed as a native executable, although the following should still work.

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/miles_limes-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Openapi

The application uses openapi with interface first using the quarkus-openapi-generator, this is a client side generator.
As such it is only used to create the request/response models. openapi located at src/main/resources/openapi/consultants.yaml

openapi gui available at https://miles-no.github.io/miles_limes/

## Authentication
uses quarkus extension quarkus-smallrye-jwt and quarkus-smallrye-jwt-build, see https://quarkus.io/guides/security-jwt

## env variables

the following env variables must be set to run Limes

| Environment variable | Description                           | Example                        |
|----------------------|---------------------------------------|--------------------------------|
| CV_PARTNER_API_KEY   | Api key from cv partner               | random base 64 string          |
| CV_PARTNER_URL       | url to cv partner                     | https://someurl.com            |
| KEY_ISSUER           | issuer of JWT used for authentication | my.issuer.com                  |
| KEY_PUBLIC           | public key used to issue JWT          | -----BEGIN PUBLIC KEY----- etc |

