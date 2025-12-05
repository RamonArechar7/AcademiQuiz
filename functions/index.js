const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * Trigger: Cuando se elimina un documento en la colección 'usuarios'.
 * Acción: Elimina la cuenta de autenticación correspondiente en Firebase Auth.
 */
exports.eliminarUsuarioAuth = functions.firestore
    .document("usuarios/{userId}")
    .onDelete(async (snap, context) => {
      const userId = context.params.userId;
      const usuario = snap.data();

      console.log(`Iniciando eliminación de cuenta Auth para usuario: ${userId} (${usuario.email})`);

      try {
        await admin.auth().deleteUser(userId);
        console.log(`Cuenta Auth eliminada exitosamente para el usuario: ${userId}`);
      } catch (error) {
        // Si el error es 'auth/user-not-found', significa que ya estaba borrado, lo cual está bien.
        if (error.code === "auth/user-not-found") {
          console.log(`El usuario ${userId} ya no existía en Auth.`);
        } else {
          console.error(`Error al eliminar cuenta Auth para ${userId}:`, error);
        }
      }
    });
