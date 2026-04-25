# Free Deployment Guide

This project is ready to deploy with:

- **Koyeb** for the Spring Boot app
- **Aiven MySQL** for the database

## 1. Keep Secrets Out of GitHub

Do not place real passwords or API keys in `application.properties`.

Use deployment environment variables instead.

If you shared database or MSG91 credentials anywhere publicly, rotate them before going live.

## 2. Aiven MySQL Values

From your Aiven service, copy:

- host
- port
- database name
- username
- password

If Aiven did not give you a database name explicitly, use the database shown in the Aiven console, often `defaultdb`.

## 3. Koyeb Service Setup

Create a new **Web Service** from this GitHub repository.

Choose the Docker deployment path so Koyeb uses the included [Dockerfile](/Users/apple/Downloads/ProjectFullstack/Dockerfile).

## 4. Required Environment Variables

Set these in Koyeb:

```bash
APP_NAME=MegaMart Smart Billing
SERVER_PORT=8080

DB_URL=jdbc:mysql://YOUR_AIVEN_HOST:YOUR_AIVEN_PORT/YOUR_DATABASE?ssl-mode=REQUIRED
DB_USERNAME=YOUR_AIVEN_USERNAME
DB_PASSWORD=YOUR_AIVEN_PASSWORD

JPA_DDL_AUTO=update
JPA_SHOW_SQL=false

MULTIPART_MAX_FILE_SIZE=10MB
MULTIPART_MAX_REQUEST_SIZE=10MB
PRODUCT_IMAGES_DIR=/tmp/product-images

OTP_EMAIL_ENABLED=false
MSG91_ENABLED=false
```

## 5. Optional Email Settings

If you want invoice emails later, also add:

```bash
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
OTP_EMAIL_FROM=your_email@gmail.com
OTP_EMAIL_ENABLED=true
```

## 6. Optional MSG91 Settings

If you want mobile OTP in production, add:

```bash
MSG91_ENABLED=true
MSG91_WIDGET_ID=your_widget_id
MSG91_TOKEN_AUTH=your_token_auth
MSG91_AUTHKEY=your_authkey
MSG91_COUNTRY_CODE=91
MSG91_OTP_VALID_MINUTES=5
```

## 7. First Deploy Checklist

- Database is reachable from public internet
- `DB_URL` includes `ssl-mode=REQUIRED`
- Product uploads are treated as temporary unless you later attach persistent object storage
- You have restarted/redeployed after changing environment variables

## 8. Production Notes

Free hosting is fine for demo and early pilot use, but for real supermarkets you should later add:

- persistent file/object storage
- managed secrets
- daily backups
- monitoring
- domain + HTTPS branding
- paid OTP/SMS budget
