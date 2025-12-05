package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemUsuarioBinding
import com.rar.academiquiz1.models.Usuario

/**
 * Adaptador para listar usuarios en la pantalla de gestión (solo Admin/Maestros).
 * Permite editar y eliminar usuarios.
 *
 * @property onEditClick Callback invocado al solicitar la edición de un usuario.
 * @property onDeleteClick Callback invocado al solicitar la eliminación de un usuario.
 */
class UsuarioAdapter(
    private val onEditClick: (Usuario) -> Unit,
    private val onDeleteClick: (Usuario) -> Unit
) : ListAdapter<Usuario, UsuarioAdapter.UsuarioViewHolder>(UsuarioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder para el item de usuario.
     */
    inner class UsuarioViewHolder(
        private val binding: ItemUsuarioBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula un objeto [Usuario] a la vista.
         * Muestra el rol codificado por colores.
         */
        fun bind(usuario: Usuario) {
            binding.apply {
                tvUserName.text = usuario.nombre
                tvUserEmail.text = usuario.email
                tvUserRole.text = getRoleDisplayName(usuario.rol ?: "ESTUDIANTE")

                // Color según rol para facilitar identificación visual
                val roleColor = when (usuario.rol) {
                    "ADMIN" -> com.rar.academiquiz1.R.color.error
                    "MAESTRO" -> com.rar.academiquiz1.R.color.accent
                    else -> com.rar.academiquiz1.R.color.primary
                }
                tvUserRole.setTextColor(root.context.getColor(roleColor))

                btnEdit.setOnClickListener {
                    onEditClick(usuario)
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(usuario)
                }
            }
        }

        private fun getRoleDisplayName(role: String): String {
            return when (role) {
                "ESTUDIANTE" -> "Estudiante"
                "MAESTRO" -> "Profesor"
                "ADMIN" -> "Administrador"
                else -> role
            }
        }
    }

    class UsuarioDiffCallback : DiffUtil.ItemCallback<Usuario>() {
        override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.id_usuario == newItem.id_usuario
        }

        override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem == newItem
        }
    }
}
