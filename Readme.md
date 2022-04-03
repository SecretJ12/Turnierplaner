# Quarkus + Vue

## Vue

Edit your VueJs app inside of `frontend/app`

To setup the project use

```shell
mvn compile -DfrontendDev -DnoFrontendComp
```

```shell
cd frontend/app
npm install
```

For compilation and hot reload
```shell
npm run dev
```

## Quarkus

For testing run
```shell
cd backend
mvn compile quarkus:dev -DbackendDev
```

## Build Quarkus+Vue

```shell
mvn package
```

Don't compile frontend
```shell
mvn package -DnoFrontendComp
```
Don't compile backend
```shell
mvn package -DnoBackendComp
```

## Clean
```shell
mvn clean
```