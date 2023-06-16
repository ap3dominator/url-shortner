# url-shortner


POST : http://localhost:8080/generate

---

Request Body -> raw -> json

```agsl
{
    url : "www.google.com"
}

```

---

Response Body
```agsl
{
    "originalUrl": "www.google.com",
    "shortLink": "6a39fd8c",
    "expirationDate": "2023-06-16T15:41:29.7698858"
}
```