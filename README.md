# Pet Tracking Application

This project contains the code for the Tractive Pet Tracking application which allows a user to do all CRUD operations. The project contains the following tech stacks:

- **Backend**: Java 21
- **Database**: H2
- **ORM**: Hibernate
- **Build tool**: Gradle

## **Run the Project**

From the root of project, you can run the following command in Terminal to run the API:

```bash
./gradlew bootRun
```

## **Run Tests**

To run the unit tests, you can execute the following command in terminal:

```bash
./gradlew test
```

## **Test Backend API**

To test the backend API, there 2 ways. For each way, you should first run the API.


### Run HTTP Tests using Ready Requests

There is directory named, `development` in the root of the project. It contains a `http` subdirectory where you can run each http request using them.

To run each http request from these files, when you opened a file, you should first select the environment (`dev`) to load the variables from `http-client.env.json`. You can change variable's value from this file.


### Run HTTP Tests using cURL

1. Create a Pet
    
- Create a Cat:

```bash
curl --location 'http://localhost:8080/api/v1/pet-tracker' \
--header 'Content-Type: application/json' \
--data '{
"ownerId": 15,
"petType": "cat",
"trackerType": "big",
"inZone": false,
"lostTracker": false
}'
```

- Create a Dog

```bash
curl --location 'http://localhost:8080/api/v1/pet-tracker' \
--header 'Content-Type: application/json' \
--data '{
    "ownerId": 22,
    "petType": "dog",
    "trackerType": "small",
    "inZone": false
}'
```

2. Get a Pet by ID:

```bash
curl --location 'http://localhost:8080/api/v1/pet-tracker/1'
```

3. Get list of Pets:

```bash
curl --location 'http://localhost:8080/api/v1/pet-tracker'
```

Also, it is possible to get list of Pets with pagination:

```bash
curl --location 'http://localhost:8080/api/v1/pet-tracker?page=0&size=10'
```

4. Get the number of Pets outside the zone:

```bash
curl --location 'http://localhost:8080/api/v1/pet-tracker/zone-info'
```

5. Update a Pet by `id` (in this case a dog):

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/pet-tracker/2' \
--header 'Content-Type: application/json' \
--data '{
    "ownerId": 1,
    "petType": "dog",
    "trackerType": "medium",
    "inZone": true
}'
```

6. Delete a Pet by `id`:

```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/pet-tracker/1'
```

# **Architectural Decisions**

1. The 3-tier architecture is used to make the app maintainable, reusable, and testable and also have clear boundaries between different responsibilities.
2. Spring Data (Hibernate) is used to access data in the database.
3. I applied versioning for the API to make it more reusable and maintainable.
4. Repository pattern has been applied for accessing data layer.
