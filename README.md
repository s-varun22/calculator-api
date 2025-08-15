# Calculator API

A RESTful web service for evaluating mathematical expressions.

This API parses expressions (supporting addition, subtraction, multiplication, division, parentheses, decimals, and
negative numbers) and returns the computed result in JSON format.

**Constraints:**

The service evaluates mathematical expressions containing only the following operators:

- Addition (`+`)
- Subtraction (`-`)
- Multiplication (`*`)
- Division (`/`)
- Parentheses (`()`)

Numbers can be integers or decimals, and negative numbers are supported.
Implicit multiplication using parentheses is also allowed.

Unary plus (`+`) and unary minus (`-`) operators are supported, including multiple consecutive unary operators, which
are resolved according to standard arithmetic rules.

### **Compile the Application**

    mvn clean install

### **Run the Application**

1. Navigate to the project folder
2. Open the terminal
3. Run the below command

   ```sh
   java -jar target/calculator-1.0.jar
   ```

### **API Description**

**Endpoint:** GET /calculus?query=[input]

The input is expected to be UTF-8 with BASE64 encoding

For example, the expression `2 + 3` should be encoded to `MiArIDM=` in Base64.

**Sample Request:**

```sh
curl "http://localhost:8080/calculus?query=MiArIDM="
```

**On Success Response:**

```json
{
  "result": 5.0
}
```

**On Error Response:**

```json
{
  "failureReason": "string"
}
```
