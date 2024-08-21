# Tournament planer

# Tournament Planner

This project is a comprehensive tournament planning application that leverages modern web technologies like Vue3 (in the frontend), 
Quarkus (for the backend), Keycloak (for authentication) to provide a seamless user experience.

## Getting Started
This application is a monorepo that contains both the frontend and backend code. The frontend is a Vue3 application that 
is located in the `frontend/app` directory, while the backend is a Quarkus application located in the `backend` directory.
The application supports mail sending for verification and reminder mails.

For this create a `.env` file under backend with the following content:
```
MAIL_FROM=${ your mail address }
MAIL_HOST=${ your mail host }
MAIL_USER=${ your mail address }
MAIL_PASS=${ your mail password }
```
For admin verification the application uses keycloak before deploying this application for production you need to 
set up the admin username and password. For this create a `.env` file under the folder `keycloak` with the following content:
```env
KEYCLOAK_ADMIN_USER=${ your keycloak admin user }
KEYCLOAK_ADMIN_PASS=${ your keycloak admin password }

KC_DB_USERNAME=${the username for the keycloak database}
KC_DB_PASSWORD=${the password for the keycloak database}
```

# Starting the application in development mode
The application can be started in development mode by following the steps below:
## Vue
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
The Quarkus application starts in development mode also a keycloak container, which is used for user authentication:
```shell
cd backend
mvn compile quarkus:dev -DbackendDev
```

## Build Quarkus+Vue
To build the whole application:
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

# Starting the application in production mode
First start the keycloak service by running the following command in the `keycloak` directory:
```shell
docker compose up
```
Then you need to import the Quarkus realm under:
- Realm settings in the menu.
- Point to the Action menu in the top right corner of the realm settings screen, and select Partial import.
- A prompt then appears where you can select the file you want to import. 
  Based on this file, you see the resources you can import along with the realm settings.
- Click Import.

After that you can start the Quarkus application by running the following command in the `backend` directory:
```shell
./mvnw install
java -jar target/quarkus-app/quarkus-run.jar
```

Finally, start the Vue application by running the following command in the `frontend/app` directory:
TBD

