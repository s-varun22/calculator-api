# **calculator-api**

A simple web service to implement a calculator. The service offers an endpoint that reads a string input and returns the result.

**Contrains:**

The service only evaluates mathematical expressions containing below contrains

- Addition **+**
- Substraction **-**
- Multiplication **\***
- Division **/**
- Parenthesis **()**

### **Compile the Application**

    mvn clean install -DskipTests

### **Run the Application**

1. Navigate to the project foler and
2. Open the terminal
3. Run the below command

   ```sh
   java -jar target/calculator-0.0.1-SNAPSHOT.jar
   ```

The application can also be started locally using the script start.sh present in the project folder.

### **API Description**

**Endpoint:** GET /calculus?query=[input]

The input is expected to be UTF-8 with BASE64 encoding

**On Success Response:**

```json
{
    "error": false,
    "result": number
}
```

**On Error Response:**

```json
{
    "error": true,
    "message": string
}
```

