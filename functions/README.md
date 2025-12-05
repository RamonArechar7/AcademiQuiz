# Despliegue de Cloud Functions

Esta carpeta contiene una Cloud Function que asegura que cuando borras un usuario de la base de datos, también se borre su cuenta de acceso.

## Requisitos Previos

Necesitas tener Node.js instalado y la CLI de Firebase.

1.  **Instalar Node.js**: Descarga e instala desde [nodejs.org](https://nodejs.org/) (versión 18 recomendada).
2.  **Instalar Firebase CLI**:
    ```bash
    npm install -g firebase-tools
    ```

## Pasos para Desplegar

Abre una terminal en esta carpeta `functions` (o en la raíz del proyecto) y ejecuta:

1.  **Iniciar sesión en Firebase**:
    ```bash
    firebase login
    ```

2.  **Instalar dependencias**:
    ```bash
    cd functions
    npm install
    ```

3.  **Desplegar la función**:
    ```bash
    firebase deploy --only functions
    ```

Una vez desplegado, la función `eliminarUsuarioAuth` estará activa. Cuando borres un usuario desde la App (Admin), esta función se ejecutará automáticamente en la nube y limpiará la cuenta de acceso.
