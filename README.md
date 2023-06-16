# url-shortner


POST : http://localhost:8080/generate

Body -> raw -> json

```agsl
{
    url : "www.google.com"
}

```

```agsl
{
    "originalUrl": "www.google.com",
    "shortLink": "6a39fd8c",
    "expirationDate": "2023-06-16T15:41:29.7698858"
}
```